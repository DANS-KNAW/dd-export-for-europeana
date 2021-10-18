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

import java.io.{ StringReader, StringWriter }

import better.files.File
import javax.xml.XMLConstants
import javax.xml.transform.stream.{ StreamResult, StreamSource }
import javax.xml.transform.{ Source, Transformer, TransformerFactory }
import javax.xml.validation.{ Schema, SchemaFactory, Validator }
import nl.knaw.dans.dd.transform.fixture.TestSupportFixture
import org.scalatest.BeforeAndAfterEach

import scala.xml.{ Node, PrettyPrinter, XML }

class XmlToCarareSpec extends TestSupportFixture with BeforeAndAfterEach {

  private val dataset = metadataDir / "metadata_DATAVERSE/dataset.xml"
  private val dataverseToCarareXSL = "src/main/resources/dataverse_carare.xsl"
  private val carareXSD = "src/main/resources/carare-v2.0.6.xsd"

  val factory: TransformerFactory = TransformerFactory.newInstance()
  val xslt = new StreamSource(File(dataverseToCarareXSL).toJava)
  val transformer: Transformer = factory.newTransformer(xslt)

  override def beforeEach(): Unit = {
    super.beforeEach()
    metadataDir.clear()
    File(getClass.getResource("/metadata/").toURI).copyTo(metadataDir)
  }

  "transform" should "produce an XML-file in Carare format, and it should validate against Carare schema 2.0.6" in {
    val carareXml = transformToCarare(dataset)
    validate(carareXml, File(carareXSD))
  }

  it should "produce a Carare XML-file with a correct structure" in {
    val carareXml = transformToCarare(dataset)

    (carareXml \ "carare").head.child should have size 4
    carareXml \ "carare" \ "collectionInformation" should have size 1
    carareXml \ "carare" \ "heritageAssetIdentification" should have size 1
    carareXml \ "carare" \ "digitalResource" should have size 2
  }

  it should "produce a Carare XML-file with a correct id" in {
    val carareXml = transformToCarare(dataset)
    val id = carareXml \ "carare" \ "@id"

    id.text shouldBe "10.5072/DAR/VZP5W1"
  }

  it should "produce a Carare XML-file with a correct collectionInformation contents" in {
    val carareXml = transformToCarare(dataset)
    val collectionInformation = carareXml \ "carare" \ "collectionInformation"

    (collectionInformation \ "title").text  shouldBe "Archeologische datasets in EASY (DANS-EASY)"
    (collectionInformation \ "contacts" \ "organization").text  shouldBe "Data Archiving and Networked Services (DANS)"
    (collectionInformation \ "rights" \ "licence").text  shouldBe "http://creativecommons.org/licenses/"
    (collectionInformation \ "coverage" \ "spatial" \ "locationSet" \ "geopoliticalArea").text  shouldBe "the Netherlands (general area)"
  }

  it should "produce a Carare XML-file with a correct heritageAssetIdentification contents" in {
    val carareXml = transformToCarare(dataset)
    val heritageAssetIdentification = carareXml \ "carare" \ "heritageAssetIdentification"

    (heritageAssetIdentification \ "recordInformation" \ "id").text  shouldBe "10.5072/DAR/VZP5W1"
    (heritageAssetIdentification \ "recordInformation" \ "creation" \ "date").text  shouldBe "2021-10-06"
    (heritageAssetIdentification \ "recordInformation" \ "language").head.text  shouldBe "English" //    (heritageAssetIdentification \ "recordInformation" \ "language").head.attribute("lang").get.text shouldBe "dcterms:ISO639-2"
    (heritageAssetIdentification \ "appellation" \ "name").head.text  shouldBe "PAN-00009021 - open plain arm ring with single knobbed terminals"
    (heritageAssetIdentification \ "appellation" \ "id").head.text  shouldBe "10.5072/DAR/VZP5W1"
    (heritageAssetIdentification \ "description").head.text  shouldBe "This find is registered at Portable Antiquities of the Netherlands with number PAN-00009021"
    (heritageAssetIdentification \ "generalType").head.text  shouldBe "Image"
    (heritageAssetIdentification \ "actors" \ "name").head.text  shouldBe "Admin, Dataverse"
    (heritageAssetIdentification \ "actors" \ "actorType").head.text  shouldBe "organization"
    (heritageAssetIdentification \ "actors" \ "roles").head.text  shouldBe "Data Collector"
//    (heritageAssetIdentification \ "characters" \ "heritageAssetType").head.text  shouldBe "open plain arm ring with single knobbed terminals"
//    (heritageAssetIdentification \ "characters" \ "heritageAssetType").head.attribute("namespace").get.text  shouldBe "https://data.cultureelerfgoed.nl/term/id/pan/PAN"
    (heritageAssetIdentification \ "characters" \ "heritageAssetType").head.attribute("termUID").get.text  shouldBe "https://data.cultureelerfgoed.nl/term/id/abr/689ebb47-4f4b-41be-8b5a-cedcf4786363"
//    (heritageAssetIdentification \ "characters" \ "heritageAssetType").head.attribute("term").get.text  shouldBe "open plain arm ring with single knobbed terminals"
    (heritageAssetIdentification \ "characters" \ "temporal" \ "displayDate").head.text  shouldBe "https://data.cultureelerfgoed.nl/term/id/abr/4bf24a9f-1f7d-497e-96a4-d4a0f42d564b"
    (heritageAssetIdentification \ "characters" \ "materials").head.text  shouldBe "metal"
    (heritageAssetIdentification \ "spatial" \ "locationSet" \ "namedLocation").head.text  shouldBe "Zaltbommel (undisclosed location)"
    (heritageAssetIdentification \ "publicationStatement" \ "publisher").head.text  shouldBe "Uitgever ltd"
    (heritageAssetIdentification \ "rights" \ "copyrightCreditLine").head.text  shouldBe "Vrije Universiteit Amsterdam"
    (heritageAssetIdentification \ "rights" \ "accessRights").head.text  shouldBe "Open Access"
    (heritageAssetIdentification \ "rights" \ "licence").head.text  shouldBe "http://creativecommons.org/publicdomain/zero/1.0"
    (heritageAssetIdentification \ "rights" \ "europeanaRights").head.text  shouldBe "The Creative Commons CC0 1.0 Universal Public Domain Dedication (CC0)"
    (heritageAssetIdentification \ "references" \ "appellation" \ "name").head.text  shouldBe "Portable Antiquities of The Netherlands"
    (heritageAssetIdentification \ "references" \ "appellation" \ "id").head.text  shouldBe "https://www.portable-antiquities.nl/pan/#/object/public/9021"
    (heritageAssetIdentification \ "hasRepresentation").head.text  shouldBe "10.5072/DAR/VZP5W1/images/PAN-00009021-001.jpg"
  }

  it should "produce a Carare XML-file with a correct digitalResource contents" in {
    val carareXml = transformToCarare(dataset)
    val digitalResource = (carareXml \ "carare" \ "digitalResource").head

    (digitalResource \ "recordInformation" \ "id").text  shouldBe "10.5072/DAR/VZP5W1/PAN-00009021-001.jpg"
    (digitalResource \ "appellation" \ "name").text  shouldBe "PAN-00009021-001.jpg"
    (digitalResource \ "appellation" \ "id").text  shouldBe "PAN-00009021-001.jpg"
    (digitalResource \ "description").text  shouldBe "Photo of the object"
    (digitalResource \ "format").text  shouldBe "image/jpeg"
    (digitalResource \ "object").text  shouldBe "https://dar.dans.knaw.nl/api/access/datafile/5"
    (digitalResource \ "isShownAt").text  shouldBe "https://doi.org/10.5072/DAR/VZP5W1"
    (digitalResource \ "rights" \ "accessRights").text  shouldBe "Open Access"
    (digitalResource \ "rights" \ "licence").text  shouldBe "http://creativecommons.org/publicdomain/zero/1.0"
  }

  private def transformToCarare(dataset: File): Node = {
    val datasetXml = XML.loadFile(dataset.toJava)

    val output = new StringWriter()
    val input: Source = new StreamSource(new StringReader(datasetXml.toString()))
    transformer.transform(input, new StreamResult(output))
    val result = XML.loadString(output.toString)
    result
  }

  private def validate(xmlFile: Node, xsdFile: File): Unit = {
    val schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
    val schema: Schema = schemaFactory.newSchema(xsdFile.toJava)
    val validator: Validator = schema.newValidator()
    val xml = new StreamSource(new StringReader(xmlFile.toString()))
    validator.validate(xml)
  }
}
