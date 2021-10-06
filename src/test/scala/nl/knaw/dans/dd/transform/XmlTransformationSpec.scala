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

import java.net.URI
import java.util.UUID
import better.files.File
import nl.knaw.dans.dd.transform.fixture.TestSupportFixture
import org.json4s.native.JsonMethods.parse
import org.scalatest.BeforeAndAfterEach

import scala.xml.XML

class XmlTransformationSpec extends TestSupportFixture with BeforeAndAfterEach {

  private val files_open = (metadataDir / "metadata_OPEN_ACCESS/files.xml").toJava
  private val files_open_registered = (metadataDir / "metadata_OPEN_ACCESS_FOR_REGISTERED_USERS/files.xml").toJava
  private val files_request = (metadataDir / "metadata_REQUEST_PERMISSION/files.xml").toJava
  private val files_no = (metadataDir / "metadata_NO_ACCESS/files.xml").toJava
  private val files_other_namespace = (metadataDir / "metadata_OTHER_NAMESPACE/files.xml").toJava
  private val dataset_open = (metadataDir / "metadata_OPEN_ACCESS/dataset.xml").toJava
  private val dataset_open_registered = (metadataDir / "metadata_OPEN_ACCESS_FOR_REGISTERED_USERS/dataset.xml").toJava
  private val dataset_request = (metadataDir / "metadata_REQUEST_PERMISSION/dataset.xml").toJava
  private val dataset_no = (metadataDir / "metadata_NO_ACCESS/dataset.xml").toJava
  private val dataset_other_namespace = (metadataDir / "metadata_OTHER_NAMESPACE/dataset.xml").toJava
  private val downloadUrl = new URI("https://download/location/")
  private val bagId = UUID.fromString("12345678-1234-1234-1234-123456789012")

  override def beforeEach(): Unit = {
    super.beforeEach()
    metadataDir.clear()
    File(getClass.getResource("/metadata/").toURI).copyTo(metadataDir)
  }

//    "e" should "..." in {
//
//    val j = parse("""{"id":3,"identifier":"DAR/TM2JIM","persistentUrl":"https://doi.org/10.5072/DAR/TM2JIM","protocol":"doi","authority":"10.5072","publisher":"DANS Archaeology Data Station (dev)","publicationDate":"2021-09-27","storageIdentifier":"file://10.5072/DAR/TM2JIM","metadataLanguage":"undefined","datasetVersion":{"id":1,"datasetId":3,"datasetPersistentId":"doi:10.5072/DAR/TM2JIM","storageIdentifier":"file://10.5072/DAR/TM2JIM","versionNumber":1,"versionMinorNumber":0,"versionState":"RELEASED","productionDate":"2021-09-20","lastUpdateTime":"2021-09-27T07:27:15Z","releaseTime":"2021-09-27T07:27:15Z","createTime":"2021-09-27T07:26:28Z","license":{"label":"CC0-1.0","uri":"http://creativecommons.org/publicdomain/zero/1.0"},"fileAccessRequest":false,"metadataBlocks":{  "citation":{    "displayName":"Citation Metadata",    "name":"citation",    "fields":[      {"typeName":"title",       "multiple":false,       "typeClass":"primitive",       "value":"otsikko"},      {"typeName":"subtitle",       "multiple":false,       "typeClass":"primitive",       "value":"alaotsikko"},      {"typeName":"alternativeTitle",       "multiple":false,       "typeClass":"primitive",       "value":"vaihtoehtoinen otsikko"},      {"typeName":"author",       "multiple":true,       "typeClass":"compound",       "value":[         {"authorName":            {"typeName":"authorName",             "multiple":false,             "typeClass":"primitive",             "value":"Admin, Dataverse"},          "authorAffiliation":            {"typeName":"authorAffiliation",             "multiple":false,             "typeClass":"primitive",             "value":"Dataverse.org"}}]},      {"typeName":"datasetContact",       "multiple":true,       "typeClass":"compound",       "value":[         {"datasetContactName":           {"typeName":"datasetContactName",            "multiple":false,            "typeClass":"primitive",            "value":"Admin, Dataverse"},          "datasetContactAffiliation":            {"typeName":"datasetContactAffiliation",             "multiple":false,             "typeClass":"primitive",             "value":"Dataverse.org"}}]},      {"typeName":"dsDescription",       "multiple":true,       "typeClass":"compound",       "value":[         {"dsDescriptionValue":           {"typeName":"dsDescriptionValue",            "multiple":false,            "typeClass":"primitive",            "value":"tekstiä"},          "dsDescriptionDate":            {"typeName":"dsDescriptionDate",             "multiple":false,             "typeClass":"primitive",             "value":"2021-09-27"}}]},      {"typeName":"subject",       "multiple":true,       "typeClass":"controlledVocabulary",       "value":["Agricultural Sciences","Arts and Humanities"]},      {"typeName":"keyword",       "multiple":true,       "typeClass":"compound",       "value":[         {"keywordValue":           {"typeName":"keywordValue",            "multiple":false,            "typeClass":"primitive",            "value":"abc"}}]},      {"typeName":"topicClassification",       "multiple":true,       "typeClass":"compound",       "value":[         {"topicClassValue":           {"typeName":"topicClassValue",            "multiple":false,            "typeClass":"primitive",            "value":"def"}}]},      {"typeName":"publication",       "multiple":true,       "typeClass":"compound",       "value":[         {"publicationCitation":           {"typeName":"publicationCitation",            "multiple":false,"typeClass":            "primitive",            "value":"no joo..."}}]},      {"typeName":"language","multiple":true,"typeClass":"controlledVocabulary","value":["Finnish"]},{"typeName":"producer","multiple":true,"typeClass":"compound","value":[{"producerName":{"typeName":"producerName","multiple":false,"typeClass":"primitive","value":"Åkerman Vesa"},"producerAffiliation":{"typeName":"producerAffiliation","multiple":false,"typeClass":"primitive","value":"DANS"}}]},{"typeName":"productionDate","multiple":false,"typeClass":"primitive","value":"2021-09-20"},{"typeName":"productionPlace","multiple":false,"typeClass":"primitive","value":"Eefde"},{"typeName":"contributor","multiple":true,"typeClass":"compound","value":[{"contributorType":{"typeName":"contributorType","multiple":false,"typeClass":"controlledVocabulary","value":"Data Collector"},"contributorName":{"typeName":"contributorName","multiple":false,"typeClass":"primitive","value":"Peltomies"}}]},{"typeName":"depositor","multiple":false,"typeClass":"primitive","value":"Admin, Dataverse"},{"typeName":"dateOfDeposit","multiple":false,"typeClass":"primitive","value":"2021-09-27"},{"typeName":"timePeriodCovered","multiple":true,"typeClass":"compound","value":[{"timePeriodCoveredStart":{"typeName":"timePeriodCoveredStart","multiple":false,"typeClass":"primitive","value":"2021-01-01"},"timePeriodCoveredEnd":{"typeName":"timePeriodCoveredEnd","multiple":false,"typeClass":"primitive","value":"2021-09-01"}}]},{"typeName":"dateOfCollection","multiple":true,"typeClass":"compound","value":[{"dateOfCollectionStart":{"typeName":"dateOfCollectionStart","multiple":false,"typeClass":"primitive","value":"2021-01-02"},"dateOfCollectionEnd":{"typeName":"dateOfCollectionEnd","multiple":false,"typeClass":"primitive","value":"2021-09-02"}}]},{"typeName":"kindOfData","multiple":true,"typeClass":"primitive","value":["minkälaista tietoa"]},{"typeName":"relatedMaterial","multiple":true,"typeClass":"primitive","value":["yhteenkuuluva materiaali"]},{"typeName":"relatedDatasets","multiple":true,"typeClass":"primitive","value":["yhteenkuuluva datasetti"]},{"typeName":"otherReferences","multiple":true,"typeClass":"primitive","value":["muut referenssit"]},{"typeName":"dataSources","multiple":true,"typeClass":"primitive","value":["tietolähteet"]},{"typeName":"originOfSources","multiple":false,"typeClass":"primitive","value":"tietolähteiden lähteet"}]},"dansRights":{"displayName":"Rights Metadata","name":"dansRights","fields":[{"typeName":"dansRightsHolder","multiple":true,"typeClass":"primitive","value":["DANS"]},{"typeName":"dansPersonalDataPresent","multiple":false,"typeClass":"controlledVocabulary","value":"No"},{"typeName":"dansMetadataLanguage","multiple":true,"typeClass":"controlledVocabulary","value":["English","Finnish"]}]},"dansRelationMetadata":{"displayName":"Relation Metadata","name":"dansRelationMetadata","fields":[{"typeName":"dansCollection","multiple":true,"typeClass":"primitive","value":["ghi"]}]},"dansArchaeologyMetadata":{"displayName":"Archaeology-Specific Metadata","name":"dansArchaeologyMetadata","fields":[{"typeName":"dansAbrRapportType","multiple":true,"typeClass":"primitive","value":["jkl"]},{"typeName":"dansAbrRapportNummer","multiple":true,"typeClass":"primitive","value":["123"]},{"typeName":"dansAbrComplex","multiple":true,"typeClass":"primitive","value":["mno"]},{"typeName":"dansAbrPeriod","multiple":true,"typeClass":"primitive","value":["pqr"]}]},"dansTemporalSpatial":{"displayName":"Temporal and Spatial Coverage","name":"dansTemporalSpatial","fields":[{"typeName":"dansTemporalCoverage","multiple":true,"typeClass":"primitive","value":["tänään"]},{"typeName":"dansSpatialPoint","multiple":true,"typeClass":"compound","value":[{"dansSpatialPointX":{"typeName":"dansSpatialPointX","multiple":false,"typeClass":"primitive","value":"1"},"dansSpatialPointY":{"typeName":"dansSpatialPointY","multiple":false,"typeClass":"primitive","value":"2"},"dansSpatialPointScheme":{"typeName":"dansSpatialPointScheme","multiple":false,"typeClass":"controlledVocabulary","value":"latitude/longitude (m)"}}]},{"typeName":"dansSpatialBox","multiple":true,"typeClass":"compound","value":[{"dansSpatialBoxNorth":{"typeName":"dansSpatialBoxNorth","multiple":false,"typeClass":"primitive","value":"1"},"dansSpatialBoxEast":{"typeName":"dansSpatialBoxEast","multiple":false,"typeClass":"primitive","value":"2"},"dansSpatialBoxSouth":{"typeName":"dansSpatialBoxSouth","multiple":false,"typeClass":"primitive","value":"3"},"dansSpatialBoxWest":{"typeName":"dansSpatialBoxWest","multiple":false,"typeClass":"primitive","value":"4"},"dansSpatialBoxScheme":{"typeName":"dansSpatialBoxScheme","multiple":false,"typeClass":"controlledVocabulary","value":"latitude/longitude (m)"}}]},{"typeName":"dansSpatialCoverageControlled","multiple":true,"typeClass":"controlledVocabulary","value":["Netherlands"]},{"typeName":"dansSpatialCoverageText","multiple":true,"typeClass":"primitive","value":["paikan määritys"]}]},"dansDataVaultMetadata":{"displayName":"Data Vault Metadata","name":"dansDataVaultMetadata","fields":[{"typeName":"dansDataversePid","multiple":false,"typeClass":"primitive","value":"doi:10.5072/DAR/TM2JIM"},{"typeName":"dansDataversePidVersion","multiple":false,"typeClass":"primitive","value":"1.0"},{"typeName":"dansBagId","multiple":false,"typeClass":"primitive","value":"urn:uuid:4bc40957-722b-49b6-a8fc-525c7a74b615"},{"typeName":"dansNbn","multiple":false,"typeClass":"primitive","value":"urn:nbn:nl:ui:13-2a063b0e-f5a6-41ca-b6bb-10d0ee47f328"}]}},"files":[{"label":"2020710_10_17026_dans_z55_9u5c.pdf","restricted":false,"version":1,"datasetVersionId":1,"dataFile":{"id":4,"persistentId":"","pidURL":"","filename":"2020710_10_17026_dans_z55_9u5c.pdf","contentType":"application/pdf","filesize":15205018,"storageIdentifier":"file://17c2624f9a3-0209ca686fbc","rootDataFileId":-1,"checksum":{"type":"SHA-1","value":"240c8ff3e33d6f6fa4e841b0fd7a640c215c3362"},"creationDate":"2021-09-27"}},{"label":"Scarab 033.pdf","restricted":false,"version":1,"datasetVersionId":1,"dataFile":{"id":5,"persistentId":"","pidURL":"","filename":"Scarab 033.pdf","contentType":"application/pdf","filesize":35568,"storageIdentifier":"file://17c2625c9b1-4967a64d8dfe","rootDataFileId":-1,"checksum":{"type":"SHA-1","value":"87fe9921f8a4a779afb4f44ee2f6a3b0a69a4120"},"creationDate":"2021-09-27"}}],"citation":"Admin, Dataverse, 2021, "otsikko", https://doi.org/10.5072/DAR/TM2JIM, DANS Archaeology Data Station (dev), V1"}}""")
//    val xmlObject = {
//      <root>
//        <id>j.</id>
//        <doi>j.persistentUrl</doi>
//        <publisher>j.publisher</publisher>
//      </root>
//    }
//    xmlObject.text
//      val b = parse(""" { "numbers" : [1, 2, 3, 4] } """)
//      b.
//  }
//
//  "enrichFilesXml" should "leave in the first file element accessibleToRights and visibleToRights elements as they were" in {
//    val filesXml = XML.loadFile(files_open)
//    val datasetXml = XML.loadFile(dataset_open)
//    val firstFileElement = (XmlTransformation.enrichFilesXml(bagId, filesXml, datasetXml, downloadUrl) \ "file").head
//    (firstFileElement \ "accessibleToRights").text shouldBe "NONE"
//    (firstFileElement \ "visibleToRights").text shouldBe "RESTRICTED_REQUEST"
//  }
//
//  it should "add a new <source> element in file elements" in {
//    val filesXml = XML.loadFile(files_open)
//    val datasetXml = XML.loadFile(dataset_open)
//    val origSizeFirstFileElement = (filesXml \ "file").head.child.size
//    val firstFileElement = (XmlTransformation.enrichFilesXml(bagId, filesXml, datasetXml, downloadUrl) \ "file").head
//    val sourceElement = (XmlTransformation.enrichFilesXml(bagId, filesXml, datasetXml, downloadUrl) \ "file" \ "source").head
//    sourceElement.child should have size 1
//    firstFileElement.child should have size origSizeFirstFileElement + 1
//  }
//
//  it should "use dcterms namespace in the new <source> element" in {
//    val filesXml = XML.loadFile(files_open)
//    val datasetXml = XML.loadFile(dataset_open)
//    val sourceElement = (XmlTransformation.enrichFilesXml(bagId, filesXml, datasetXml, downloadUrl) \ "file" \ "source").head
//    sourceElement.toString should startWith ("<dcterms:source>")
//  }
//
//  it should "use dc namespace in the new <source> element" in {
//    val filesXml = XML.loadFile(files_other_namespace)
//    val datasetXml = XML.loadFile(dataset_other_namespace)
//    val sourceElement = (XmlTransformation.enrichFilesXml(bagId, filesXml, datasetXml, downloadUrl) \ "file" \ "source").head
//    sourceElement.toString should startWith ("<dc:source>")
//  }
//
//  it should "give download path as value for the new <source> element in all file elements" in {
//    val filesXml = XML.loadFile(files_open)
//    val datasetXml = XML.loadFile(dataset_open)
//    val sourceElement_1 = (XmlTransformation.enrichFilesXml(bagId, filesXml, datasetXml, downloadUrl) \ "file" \ "source").head
//    val sourceElement_2 = (XmlTransformation.enrichFilesXml(bagId, filesXml, datasetXml, downloadUrl) \ "file" \ "source") (1)
//    sourceElement_1.text shouldBe s"https://download/location/$bagId/data/path/to/file%2Etxt"
//    sourceElement_2.text shouldBe s"https://download/location/$bagId/data/quicksort%2Ehs"
//  }
//
//  it should "construct the download path correctly also when there are spaces in the filepath" in {
//    val filesXml = XML.loadFile(files_request)
//    val datasetXml = XML.loadFile(dataset_request)
//    val sourceElement_1 = (XmlTransformation.enrichFilesXml(bagId, filesXml, datasetXml, downloadUrl) \ "file" \ "source").head
//    val sourceElement_2 = (XmlTransformation.enrichFilesXml(bagId, filesXml, datasetXml, downloadUrl) \ "file" \ "source") (1)
//    sourceElement_1.text shouldBe s"https://download/location/$bagId/data/path%20to%20file%2Etxt"
//    sourceElement_2.text shouldBe s"https://download/location/$bagId/data/quicksort%2Ehs"
//  }
//
//  it should "add visibleToRights element with value 'ANONYMOUS' to the second file element" in {
//    val filesXml = XML.loadFile(files_open)
//    val datasetXml = XML.loadFile(dataset_open)
//    val secondFileElement = (XmlTransformation.enrichFilesXml(bagId, filesXml, datasetXml, downloadUrl) \ "file") (1)
//    (secondFileElement \ "visibleToRights").text shouldBe "ANONYMOUS"
//  }
//
//  it should "add accessibleToRights element with value 'ANONYMOUS' to the second file element, when dataset accessRights is OPEN_ACCESS" in {
//    val filesXml = XML.loadFile(files_open)
//    val datasetXml = XML.loadFile(dataset_open)
//    val firstFileElement = (XmlTransformation.enrichFilesXml(bagId, filesXml, datasetXml, downloadUrl) \ "file").head
//    val secondFileElement = (XmlTransformation.enrichFilesXml(bagId, filesXml, datasetXml, downloadUrl) \ "file") (1)
//    (firstFileElement \ "accessibleToRights").text shouldBe "NONE"
//    (firstFileElement \ "visibleToRights").text shouldBe "RESTRICTED_REQUEST"
//    (secondFileElement \ "accessibleToRights").text shouldBe "ANONYMOUS"
//    (secondFileElement \ "visibleToRights").text shouldBe "ANONYMOUS"
//  }
//
//  it should "add accessibleToRights element with value 'KNOWN' to the second file element, when dataset accessRights is OPEN_ACCESS_FOR_REGISTERED_USERS" in {
//    val filesXml = XML.loadFile(files_open_registered)
//    val datasetXml = XML.loadFile(dataset_open_registered)
//    val firstFileElement = (XmlTransformation.enrichFilesXml(bagId, filesXml, datasetXml, downloadUrl) \ "file").head
//    val secondFileElement = (XmlTransformation.enrichFilesXml(bagId, filesXml, datasetXml, downloadUrl) \ "file") (1)
//    (firstFileElement \ "accessibleToRights").text shouldBe "NONE"
//    (firstFileElement \ "visibleToRights").text shouldBe "RESTRICTED_REQUEST"
//    (secondFileElement \ "accessibleToRights").text shouldBe "KNOWN"
//    (secondFileElement \ "visibleToRights").text shouldBe "ANONYMOUS"
//  }
//
//  it should "add accessibleToRights element with value 'RESTRICTED_REQUEST' to the second file element, when dataset accessRights is REQUEST_PERMISSION" in {
//    val filesXml = XML.loadFile(files_request)
//    val datasetXml = XML.loadFile(dataset_request)
//    val firstFileElement = (XmlTransformation.enrichFilesXml(bagId, filesXml, datasetXml, downloadUrl) \ "file").head
//    val secondFileElement = (XmlTransformation.enrichFilesXml(bagId, filesXml, datasetXml, downloadUrl) \ "file") (1)
//    (firstFileElement \ "accessibleToRights").text shouldBe "NONE"
//    (firstFileElement \ "visibleToRights").text shouldBe "RESTRICTED_REQUEST"
//    (secondFileElement \ "accessibleToRights").text shouldBe "RESTRICTED_REQUEST"
//    (secondFileElement \ "visibleToRights").text shouldBe "ANONYMOUS"
//  }
//
//  it should "add accessibleToRights element with value 'NONE' to the second file element, when dataset accessRights is NO_ACCESS" in {
//    val filesXml = XML.loadFile(files_no)
//    val datasetXml = XML.loadFile(dataset_no)
//    val firstFileElement = (XmlTransformation.enrichFilesXml(bagId, filesXml, datasetXml, downloadUrl) \ "file").head
//    val secondFileElement = (XmlTransformation.enrichFilesXml(bagId, filesXml, datasetXml, downloadUrl) \ "file") (1)
//    (firstFileElement \ "accessibleToRights").text shouldBe "NONE"
//    (firstFileElement \ "visibleToRights").text shouldBe "RESTRICTED_REQUEST"
//    (secondFileElement \ "accessibleToRights").text shouldBe "NONE"
//    (secondFileElement \ "visibleToRights").text shouldBe "ANONYMOUS"
//  }
//
//  it should "return as many nodes in the output xml as was in the original xml" in {
//    val filesXml = XML.loadFile(files_open)
//    val datasetXml = XML.loadFile(dataset_open)
//    val origSize = filesXml.child.size
//    XmlTransformation.enrichFilesXml(bagId, filesXml, datasetXml, downloadUrl).child should have size origSize
//  }
}
