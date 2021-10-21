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
package nl.knaw.dans.dd.transform.abr

import nl.knaw.dans.dd.transform.{Configuration, HttpStatusException}
import nl.knaw.dans.lib.logging.DebugEnhancedLogging
import scalaj.http.BaseHttp

import scala.language.postfixOps
import scala.util.{Failure, Success, Try}
import scala.xml.{Elem, XML}

class Abr(configuration: Configuration) extends DebugEnhancedLogging {

  val RDF_NAMESPACE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#"
  var abrCodes: Map[String, (String, String)] = Map.empty[String, (String, String)]
  val connectTimeout: Int = configuration.abrConfig.connectTimeout.toInt
  val readTimeout: Int = configuration.abrConfig.readTimeout.toInt
  object Http extends BaseHttp(userAgent = s"easy-transform-metadata/${ configuration.version }")

  def getAbrLabelAndScheme(uri: String): Try[(String, String)] = {
   abrCodes.get(uri).map(labelAndScheme => Try(labelAndScheme)).getOrElse(retrieveAbrLabelAndScheme(uri))
  }

  private def retrieveAbrLabelAndScheme(uri: String): Try[(String, String)] = {
    for {
      response <-loadAbrXml(uri)
      _ = logger.info(s"Fetched AbrComplex label and scheme from $uri")
      label = (response \\ "prefLabel").head.text
      scheme = (response \\ "inScheme").head.attribute(RDF_NAMESPACE_URI, "resource").map(_.head.text).getOrElse("")
      _ = abrCodes += (uri -> (label, scheme))
      _ = logger.info("abrCOdes:  " + abrCodes.toString())
    } yield (label, scheme)
  }

  private def loadAbrXml(uri: String): Try[Elem] = {
    for {
      response <- Try { Http(s"$uri.rdf").method("GET").timeout(connectTimeout, readTimeout).asString }
      _ <- if (response.code == 200) Success(())
      else Failure(HttpStatusException(uri, response))
    } yield XML.loadString(response.body)
  }
}
