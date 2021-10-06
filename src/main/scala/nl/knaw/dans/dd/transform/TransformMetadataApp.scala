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
package nl.knaw.dans.dd.transform

import nl.knaw.dans.dd.transform.dataverse.Dataverse

import java.io.{StringReader, StringWriter, Writer}
import javax.xml.transform.stream.{StreamResult, StreamSource}
import javax.xml.transform.{Source, Transformer}
import scala.util.Try
import scala.xml.{Elem, Node, PrettyPrinter, XML}

class TransformMetadataApp(configuration: Configuration) {

  private lazy val prettyPrinter = new PrettyPrinter(160, 2)
  private val dataverse = new Dataverse(configuration)

  def processDataset(doi: Doi, transformer: Option[Transformer], output: Writer): Try[Unit] = {
    for {
      datasetXml <- fetchMetadata(doi)
//      resultXml <- transformer.fold(Try { datasetXml })(transform(datasetXml))
//      _ <- outputXml(resultXml, output)
    } yield ()
  }

  def fetchMetadata(doi: Doi) = {
    dataverse.getMetadata(doi)
  }

  private def transform(xml: Node)(transformer: Transformer): Try[Elem] = Try {
    val input: Source = new StreamSource(new StringReader(xml.toString()))
    val output = new StringWriter()
    transformer.transform(input, new StreamResult(output))

    XML.loadString(output.toString)
  }

  private def outputXml(xml: Node, output: Writer): Try[Unit] = Try {
    output.write(prettyPrinter.format(xml))
  }
}
