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
import nl.knaw.dans.dd.transform.abr.Abr
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
  val downloadURL: String = configuration.downloadURL
  val server = new DataverseInstance(
    DataverseInstanceConfig(
      baseUrl = baseUrl,
      apiToken = apiToken,
      unblockKey = Option(unblockKey),
      connectionTimeout = connectionTimeout,
      readTimeout = readTimeout,
    )
  )
  private val abr = new Abr(configuration)

  def getMetadata(doi: String): Try[Elem] = Try {
    var xml = "<dataset>"
    val citationElements = List(("title", false), ("author", true), ("dsDescription", true),
      ("subject", false), ("keyword", true), ("language", false), ("dateOfDeposit", false),
      ("contributor", true), ("distributor", true))
    val dansRightsElements = List(("dansRightsHolder", false))
    val dansRelationMetadataElements = List(("dansRelation", false))
    val dansArchaeologyElements = List(("dansAbrComplex", false), ("dansAbrPeriod", false))
    val dansTemporalSpatialElements = List(("dansSpatialCoverageText", false))
    val metadata = server.dataset(doi).view(Version.LATEST_PUBLISHED)
    metadata.map(m =>
      m.json.map(j => {
        val metadataBlocks = j \\ "metadataBlocks"
        val citation = metadataBlocks \\ "citation" \\ "fields"
        val dansRights = metadataBlocks \\ "dansRights" \\ "fields"
        val dansRelationMetadata = metadataBlocks \\ "dansRelationMetadata" \\ "fields"
        val dansArchaeology = metadataBlocks \\ "dansArchaeologyMetadata" \\ "fields"
        val dansTemporalSpatial = metadataBlocks \\ "dansTemporalSpatial" \\ "fields"

        xml += getPid(j) + getLicense(j)
        citationElements.foreach(element => xml += getXml(citation, element._1, element._2))
        dansRightsElements.foreach(element => xml += getXml(dansRights, element._1, element._2))
        dansRelationMetadataElements.foreach(element => xml += getXml(dansRelationMetadata, element._1, element._2))
        dansArchaeologyElements.foreach(element => xml += getXml(dansArchaeology, element._1, element._2))
        dansTemporalSpatialElements.foreach(element => xml += getXml(dansTemporalSpatial, element._1, element._2))
        xml += getFilesXml(j \\ "files")
      }
      )).getOrElse(throw new Exception(metadata.get.message.get))
    xml += "</dataset>"
    XML.loadString(xml)
  }

  private def getPid(j: JValue): String = {
    getLeafXml("datasetPersistentId", j \\ "datasetPersistentId")
  }

  private def getLicense(j: JValue): String = {
    val label = j \\ "license" \\ "label"
    val uri = j \\ "license" \\ "uri"
    "<license>" + getLeafXml("label", label) + getLeafXml("uri", uri) + "</license>"
  }

  private def getXml(j: JValue, name: String, isCompound: Boolean): String = {
    var xml = ""
    j.children.foreach(child =>
      if (child.values.toString.contains(name)) {
        val value = child \\ "value"
        if (isCompound) xml += getCompoundXml(name, value)
        else xml += getLeafXml(name, value)
      }
    )
    xml
  }

  private def getFilesXml(j: JValue): String = {
    var xml = "<files>"
    xml += s"<downloadUrl>$downloadURL</downloadUrl>"
    val files = j.children
    files.foreach(f => {
      var innerXml = ""
      val id = f \\ "id"
      val filename = f \\ "filename"
      val directoryLabel = f \\ "directoryLabel"
      val restricted = f \\ "restricted"
      val contentType = f \\ "contentType"
      val filesize = f \\ "filesize"
      innerXml += getLeafXml("id", id) + getLeafXml("filename", filename) + getLeafXml("filesize", filesize)
      innerXml += getLeafXml("restricted", restricted) + getLeafXml("contentType", contentType)
      if (directoryLabel.isInstanceOf[JString]) innerXml += getLeafXml("directoryLabel", directoryLabel)
      xml += "<file>" + innerXml + "</file>"
    })
    xml += "</files>"
    xml
  }

  private def getLeafXml(name: String, value: JValue): String = {
    var xml = ""
    val leafValues = if (value.isInstanceOf[JArray]) value.children else List(value)
    leafValues.foreach(v => xml += getLeafXmlElements(name, v.values.toString.trim))
    xml
  }

  private def getLeafXmlElements(name: String, value: String): String = {
    if (name == "dansAbrComplex") {
      // Label and SchemeUri of AbrComplex are not stored in Dataverse. We fetch them from data.cultureelerfgoed.nl.
      // From dansAbrComplex leaf element we create here a complex element in the output.
      var xml = s"<dansAbrComplex><dansAbrComplexTermUID>$value</dansAbrComplexTermUID>"
      val labelAndScheme = abr.getAbrLabelAndScheme(value)
      if (labelAndScheme.isSuccess)
        xml += s"<dansAbrComplexTerm>${labelAndScheme.get._1}</dansAbrComplexTerm><dansAbrComplexScheme>${labelAndScheme.get._2}</dansAbrComplexScheme>"
      else
        logger.error(s"Could not retrieve AbrComplexLabel and AbrComplexSchemeUri for $value. Reason: $labelAndScheme")
      xml + "</dansAbrComplex>"
    }
    else if (name == "dansAbrPeriod") {
      // Label of AbrPeriod is not stored in Dataverse. We fetch it from data.cultureelerfgoed.nl
      // and we show the label in dansAbrPeriod leaf element.
      var xml = s"<dansAbrPeriod>"
      val labelAndScheme = abr.getAbrLabelAndScheme(value)
      if (labelAndScheme.isSuccess)
        xml += labelAndScheme.get._1
      else
        logger.error(s"Could not retrieve AbrPeriodLabel for $value. Reason: $labelAndScheme")
      xml + "</dansAbrPeriod>"
    }
    else {
      val valueCleaned = value.replace("<br>", " ").replace("</br>", " ")
        .replace("<p>", " ").replace("</p>", " ").replace("&", "&amp;")
      s"<$name>$valueCleaned</$name>"
    }
  }

  private def getCompoundXml(name: String, value: JValue): String = {
    var xml = ""
    val startTag = s"<$name>"
    val endTag = s"</$name>"
    value.children.foreach(v => {
      if (v.values.toString.nonEmpty) {
        var innerXml = ""
        val names = v \\ "typeName"
        val values = v \\ "value"
        val leafNames = if (names.isInstanceOf[JString]) List(names) else names.children
        val leafValues = if (values.isInstanceOf[JString]) List(values) else values.children
        val zipped = leafNames zip leafValues
        zipped.foreach(z => {
          innerXml += getLeafXml(z._1.values.toString, z._2)
        })
        if (innerXml nonEmpty) {
          xml += startTag + innerXml + endTag
        }
      }
    })
    xml
  }
}
