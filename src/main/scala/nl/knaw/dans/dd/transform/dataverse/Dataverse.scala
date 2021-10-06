/**
 * Copyright (C) 2021 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.dd.transform.dataverse

import nl.knaw.dans.dd.transform.Configuration
import nl.knaw.dans.lib.dataverse.{DataverseInstance, DataverseInstanceConfig, DataverseResponse, Version}
import nl.knaw.dans.lib.logging.DebugEnhancedLogging
import org.json4s.JsonAST.{JArray, JString, JValue}

import java.net.URI
import scala.util.Try
import scala.xml.{Elem, XML}
import scala.language.postfixOps

class Dataverse(configuration: Configuration) extends DebugEnhancedLogging {

  val baseUrl: URI = configuration.dataverseConfig.baseURL
  val connectionTimeout: Int = configuration.dataverseConfig.connectionTimeout
  val readTimeout: Int = configuration.dataverseConfig.readTimeout
  val unblockKey: String = configuration.dataverseConfig.unblockKey
  val apiToken: String = configuration.dataverseConfig.apiToken
  val server = new DataverseInstance(
    DataverseInstanceConfig(
      baseUrl = baseUrl,
      apiToken = apiToken,
      unblockKey = Option(unblockKey),
      connectionTimeout = connectionTimeout,
      readTimeout = readTimeout,
    )
  )

  def getMetadata(doi: String): Try[Elem] = Try {
    var xml = "<dataset>"
    val elements = List(("license", true), ("title", false), ("alternative", false), ("dsDescription", true),
      ("dateOfDeposit", false), ("dateOfCollection", true), ("language", false), ("author", true), ("distributor", true), ("subject", false),
      ("keyword", true), ("contributor", true), ("dansTemporal", false), ("topic", true), ("otherReferences", false), ("dansAbr", false),
      ("dansSpatial", true), ("dansSpatialCoverage", false), ("dataSources", false), ("dansRights", false))
    val metadata = server.dataset(doi).view(Version.LATEST_PUBLISHED)
    metadata.map(m =>
      m.json.map(j => {
        val namesAndValues = getNamesAndValues(j \\ "data")
        xml += getPid(j) + getLicense(j)
        elements.foreach(element => xml += getXml(namesAndValues, element))
        xml += getFilesXml(j \\ "files")
      }
      ))
    xml += "</dataset>"
    logger.info("xml: " + xml)
    XML.loadString(xml)
  }

  private def getNamesAndValues(j: JValue): List[(JValue, JValue)] = {
    val n = j \\ "typeName"
    val v = j \\ "value"
    val names = if (n.isInstanceOf[JString]) List(n) else n.children
    val values = if (v.isInstanceOf[JString]) List(v) else v.children
    names zip values
  }

  private def getPid(j: JValue): String = {
    getLeafXml(JString("datasetPersistentId"), j \\ "datasetPersistentId")
  }

  private def getLicense(j: JValue): String = {
    val label = j \\ "license" \\  "label"
    val uri = j \\ "license" \\  "uri"
    "<license>" + getLeafXml(JString("label"), label) + getLeafXml(JString("uri"), uri) + "</license>"
  }

  private def getXml(namesAndValues: List[(JValue, JValue)], element: (String, Boolean)): String = {
    var xml = ""
    val (name, isCompound) = element
    namesAndValues.filter(z => z._1.values.toString.startsWith(name)).foreach(z =>
      if (isCompound) xml += getCompoundXml(z._1, z._2)
      else xml += getLeafXml(z._1, z._2))
    xml
  }

  private def getFilesXml(j: JValue): String = {
    var xml = "<files>"
    val files = j.children
    files.foreach(f => {
      var innerXml = ""
      val filename = f \\ "filename"
      val directoryLabel = f \\ "directoryLabel"
      val restricted = f \\ "restricted"
      val contentType = f \\ "contentType"
      innerXml += getLeafXml(JString("filename"), filename)
      if (directoryLabel.isInstanceOf[JString]) innerXml += getLeafXml(JString("directoryLabel"), directoryLabel)
      if (restricted.values.toString.nonEmpty) innerXml += getLeafXml(JString("restricted"), restricted)
      if (contentType.values.toString.nonEmpty) innerXml += getLeafXml(JString("contentType"), contentType)
      xml += "<file>" + innerXml + "</file>"
    })
    xml += "</files>"
    xml
  }

  private def getLeafXml(name: JValue, value: JValue): String = {
    var xml = ""
    val leafValues = if (value.isInstanceOf[JArray]) value.children else List(value)
    leafValues.foreach(v => xml += s"<${name.values.toString}>${v.values.toString.trim}</${name.values.toString}>")
    xml
  }

  private def getCompoundXml(name: JValue, value: JValue): String = {
    var xml = ""
    val startTag = s"<${name.values.toString}>"
    val endTag = s"</${name.values.toString}>"
    value.children.foreach(v => {
      if (v.values.toString.nonEmpty) {
        var innerXml = ""
        val names = v \\ "typeName"
        val values = v \\ "value"
        val leafNames = if (names.isInstanceOf[JString]) List(names) else names.children
        val leafValues = if (values.isInstanceOf[JString]) List(values) else values.children
        val zipped = leafNames zip leafValues
        zipped.foreach(z => {
          innerXml += getLeafXml(z._1, z._2)
        })
        if (innerXml nonEmpty) {
          xml += startTag + innerXml + endTag
        }
      }
    })
    xml
  }
}
