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

import nl.knaw.dans.dd.transform.AccessRights.AccessRights
import org.json4s.native.Json

import java.net.URI
import scala.xml.transform.{RewriteRule, RuleTransformer}
import scala.xml.{Elem, Node, XML}
import org.json4s._
import org.json4s.native.JsonMethods._

object XmlTransformation {

  private val accessibleToRightsMap = Map(
    AccessRights.OPEN_ACCESS -> AccessibleToRights.ANONYMOUS,
    AccessRights.OPEN_ACCESS_FOR_REGISTERED_USERS -> AccessibleToRights.KNOWN,
    AccessRights.REQUEST_PERMISSION -> AccessibleToRights.RESTRICTED_REQUEST,
    AccessRights.NO_ACCESS -> AccessibleToRights.NONE)

//  def jsonToXml(): String = {
//    val a = parse(""" { "numbers" : [1, 2, 3, 4] } """)
//
//    val j = parse("{\n\"id\":3,\n\"identifier\":\"DAR/TM2JIM\",\n\"persistentUrl\":\"https://doi.org/10.5072/DAR/TM2JIM\",\n\"protocol\":\"doi\",\n\"authority\":\"10.5072\",\n\"publisher\":\"DANS Archaeology Data Station (dev)\",\n\"publicationDate\":\"2021-09-27\",\"storageIdentifier\":\"file://10.5072/DAR/TM2JIM\",\n\"metadataLanguage\":\"undefined\",\n\"datasetVersion\":{\n\n\"id\":1,\n\"doi\":3,\n\"datasetPersistentId\":\"doi:10.5072/DAR/TM2JIM\",\n\"storageIdentifier\":\"file://10.5072/DAR/TM2JIM\",\n\"versionNumber\":1,\n\"versionMinorNumber\":0,\n\"versionState\":\"RELEASED\",\n\"productionDate\":\"2021-09-20\",\n\"lastUpdateTime\":\"2021-09-27T07:27:15Z\",\n\"releaseTime\":\"2021-09-27T07:27:15Z\",\n\"createTime\":\"2021-09-27T07:26:28Z\",\n\"license\":{\"label\":\"CC0-1.0\",\"uri\":\"http://creativecommons.org/publicdomain/zero/1.0\"},\n\"fileAccessRequest\":false,\n\"metadataBlocks\":{\n  \"citation\":{\n    \"displayName\":\"Citation Metadata\",\n    \"name\":\"citation\",\n    \"fields\":[\n      {\"typeName\":\"title\",\n       \"multiple\":false,\n       \"typeClass\":\"primitive\",\n       \"value\":\"otsikko\"},\n      {\"typeName\":\"subtitle\",\n       \"multiple\":false,\n       \"typeClass\":\"primitive\",\n       \"value\":\"alaotsikko\"},\n      {\"typeName\":\"alternativeTitle\",\n       \"multiple\":false,\n       \"typeClass\":\"primitive\",\n       \"value\":\"vaihtoehtoinen otsikko\"},\n      {\"typeName\":\"author\",\n       \"multiple\":true,\n       \"typeClass\":\"compound\",\n       \"value\":[\n         {\"authorName\":\n            {\"typeName\":\"authorName\",\n             \"multiple\":false,\n             \"typeClass\":\"primitive\",\n             \"value\":\"Admin, Dataverse\"},\n          \"authorAffiliation\":\n            {\"typeName\":\"authorAffiliation\",\n             \"multiple\":false,\n             \"typeClass\":\"primitive\",\n             \"value\":\"Dataverse.org\"}}]},\n      {\"typeName\":\"datasetContact\",\n       \"multiple\":true,\n       \"typeClass\":\"compound\",\n       \"value\":[\n         {\"datasetContactName\":\n           {\"typeName\":\"datasetContactName\",\n            \"multiple\":false,\n            \"typeClass\":\"primitive\",\n            \"value\":\"Admin, Dataverse\"},\n          \"datasetContactAffiliation\":\n            {\"typeName\":\"datasetContactAffiliation\",\n             \"multiple\":false,\n             \"typeClass\":\"primitive\",\n             \"value\":\"Dataverse.org\"}}]},\n      {\"typeName\":\"dsDescription\",\n       \"multiple\":true,\n       \"typeClass\":\"compound\",\n       \"value\":[\n         {\"dsDescriptionValue\":\n           {\"typeName\":\"dsDescriptionValue\",\n            \"multiple\":false,\n            \"typeClass\":\"primitive\",\n            \"value\":\"tekstiä\"},\n          \"dsDescriptionDate\":\n            {\"typeName\":\"dsDescriptionDate\",\n             \"multiple\":false,\n             \"typeClass\":\"primitive\",\n             \"value\":\"2021-09-27\"}}]},\n      {\"typeName\":\"subject\",\n       \"multiple\":true,\n       \"typeClass\":\"controlledVocabulary\",\n       \"value\":[\"Agricultural Sciences\",\"Arts and Humanities\"]},\n      {\"typeName\":\"keyword\",\n       \"multiple\":true,\n       \"typeClass\":\"compound\",\n       \"value\":[\n         {\"keywordValue\":\n           {\"typeName\":\"keywordValue\",\n            \"multiple\":false,\n            \"typeClass\":\"primitive\",\n            \"value\":\"abc\"}}]},\n      {\"typeName\":\"topicClassification\",\n       \"multiple\":true,\n       \"typeClass\":\"compound\",\n       \"value\":[\n         {\"topicClassValue\":\n           {\"typeName\":\"topicClassValue\",\n            \"multiple\":false,\n            \"typeClass\":\"primitive\",\n            \"value\":\"def\"}}]},\n      {\"typeName\":\"publication\",\n       \"multiple\":true,\n       \"typeClass\":\"compound\",\n       \"value\":[\n         {\"publicationCitation\":\n           {\"typeName\":\"publicationCitation\",\n            \"multiple\":false,\"typeClass\":\n            \"primitive\",\n            \"value\":\"no joo...\"}}]},\n      {\"typeName\":\"language\",\"multiple\":true,\"typeClass\":\"controlledVocabulary\",\"value\":[\"Finnish\"]},{\"typeName\":\"producer\",\"multiple\":true,\"typeClass\":\"compound\",\"value\":[{\"producerName\":{\"typeName\":\"producerName\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Åkerman Vesa\"},\"producerAffiliation\":{\"typeName\":\"producerAffiliation\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"DANS\"}}]},{\"typeName\":\"productionDate\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"2021-09-20\"},{\"typeName\":\"productionPlace\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Eefde\"},{\"typeName\":\"contributor\",\"multiple\":true,\"typeClass\":\"compound\",\"value\":[{\"contributorType\":{\"typeName\":\"contributorType\",\"multiple\":false,\"typeClass\":\"controlledVocabulary\",\"value\":\"Data Collector\"},\"contributorName\":{\"typeName\":\"contributorName\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Peltomies\"}}]},{\"typeName\":\"depositor\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"Admin, Dataverse\"},{\"typeName\":\"dateOfDeposit\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"2021-09-27\"},{\"typeName\":\"timePeriodCovered\",\"multiple\":true,\"typeClass\":\"compound\",\"value\":[{\"timePeriodCoveredStart\":{\"typeName\":\"timePeriodCoveredStart\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"2021-01-01\"},\"timePeriodCoveredEnd\":{\"typeName\":\"timePeriodCoveredEnd\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"2021-09-01\"}}]},{\"typeName\":\"dateOfCollection\",\"multiple\":true,\"typeClass\":\"compound\",\"value\":[{\"dateOfCollectionStart\":{\"typeName\":\"dateOfCollectionStart\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"2021-01-02\"},\"dateOfCollectionEnd\":{\"typeName\":\"dateOfCollectionEnd\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"2021-09-02\"}}]},{\"typeName\":\"kindOfData\",\"multiple\":true,\"typeClass\":\"primitive\",\"value\":[\"minkälaista tietoa\"]},{\"typeName\":\"relatedMaterial\",\"multiple\":true,\"typeClass\":\"primitive\",\"value\":[\"yhteenkuuluva materiaali\"]},{\"typeName\":\"relatedDatasets\",\"multiple\":true,\"typeClass\":\"primitive\",\"value\":[\"yhteenkuuluva datasetti\"]},{\"typeName\":\"otherReferences\",\"multiple\":true,\"typeClass\":\"primitive\",\"value\":[\"muut referenssit\"]},{\"typeName\":\"dataSources\",\"multiple\":true,\"typeClass\":\"primitive\",\"value\":[\"tietolähteet\"]},{\"typeName\":\"originOfSources\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"tietolähteiden lähteet\"}]},\"dansRights\":{\"displayName\":\"Rights Metadata\",\"name\":\"dansRights\",\"fields\":[{\"typeName\":\"dansRightsHolder\",\"multiple\":true,\"typeClass\":\"primitive\",\"value\":[\"DANS\"]},{\"typeName\":\"dansPersonalDataPresent\",\"multiple\":false,\"typeClass\":\"controlledVocabulary\",\"value\":\"No\"},{\"typeName\":\"dansMetadataLanguage\",\"multiple\":true,\"typeClass\":\"controlledVocabulary\",\"value\":[\"English\",\"Finnish\"]}]},\"dansRelationMetadata\":{\"displayName\":\"Relation Metadata\",\"name\":\"dansRelationMetadata\",\"fields\":[{\"typeName\":\"dansCollection\",\"multiple\":true,\"typeClass\":\"primitive\",\"value\":[\"ghi\"]}]},\"dansArchaeologyMetadata\":{\"displayName\":\"Archaeology-Specific Metadata\",\"name\":\"dansArchaeologyMetadata\",\"fields\":[{\"typeName\":\"dansAbrRapportType\",\"multiple\":true,\"typeClass\":\"primitive\",\"value\":[\"jkl\"]},{\"typeName\":\"dansAbrRapportNummer\",\"multiple\":true,\"typeClass\":\"primitive\",\"value\":[\"123\"]},{\"typeName\":\"dansAbrComplex\",\"multiple\":true,\"typeClass\":\"primitive\",\"value\":[\"mno\"]},{\"typeName\":\"dansAbrPeriod\",\"multiple\":true,\"typeClass\":\"primitive\",\"value\":[\"pqr\"]}]},\"dansTemporalSpatial\":{\"displayName\":\"Temporal and Spatial Coverage\",\"name\":\"dansTemporalSpatial\",\"fields\":[{\"typeName\":\"dansTemporalCoverage\",\"multiple\":true,\"typeClass\":\"primitive\",\"value\":[\"tänään\"]},{\"typeName\":\"dansSpatialPoint\",\"multiple\":true,\"typeClass\":\"compound\",\"value\":[{\"dansSpatialPointX\":{\"typeName\":\"dansSpatialPointX\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"1\"},\"dansSpatialPointY\":{\"typeName\":\"dansSpatialPointY\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"2\"},\"dansSpatialPointScheme\":{\"typeName\":\"dansSpatialPointScheme\",\"multiple\":false,\"typeClass\":\"controlledVocabulary\",\"value\":\"latitude/longitude (m)\"}}]},{\"typeName\":\"dansSpatialBox\",\"multiple\":true,\"typeClass\":\"compound\",\"value\":[{\"dansSpatialBoxNorth\":{\"typeName\":\"dansSpatialBoxNorth\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"1\"},\"dansSpatialBoxEast\":{\"typeName\":\"dansSpatialBoxEast\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"2\"},\"dansSpatialBoxSouth\":{\"typeName\":\"dansSpatialBoxSouth\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"3\"},\"dansSpatialBoxWest\":{\"typeName\":\"dansSpatialBoxWest\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"4\"},\"dansSpatialBoxScheme\":{\"typeName\":\"dansSpatialBoxScheme\",\"multiple\":false,\"typeClass\":\"controlledVocabulary\",\"value\":\"latitude/longitude (m)\"}}]},{\"typeName\":\"dansSpatialCoverageControlled\",\"multiple\":true,\"typeClass\":\"controlledVocabulary\",\"value\":[\"Netherlands\"]},{\"typeName\":\"dansSpatialCoverageText\",\"multiple\":true,\"typeClass\":\"primitive\",\"value\":[\"paikan määritys\"]}]},\"dansDataVaultMetadata\":{\"displayName\":\"Data Vault Metadata\",\"name\":\"dansDataVaultMetadata\",\"fields\":[{\"typeName\":\"dansDataversePid\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"doi:10.5072/DAR/TM2JIM\"},{\"typeName\":\"dansDataversePidVersion\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"1.0\"},{\"typeName\":\"dansBagId\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"urn:uuid:4bc40957-722b-49b6-a8fc-525c7a74b615\"},{\"typeName\":\"dansNbn\",\"multiple\":false,\"typeClass\":\"primitive\",\"value\":\"urn:nbn:nl:ui:13-2a063b0e-f5a6-41ca-b6bb-10d0ee47f328\"}]}},\"files\":[{\"label\":\"2020710_10_17026_dans_z55_9u5c.pdf\",\"restricted\":false,\"version\":1,\"datasetVersionId\":1,\"dataFile\":{\"id\":4,\"persistentId\":\"\",\"pidURL\":\"\",\"filename\":\"2020710_10_17026_dans_z55_9u5c.pdf\",\"contentType\":\"application/pdf\",\"filesize\":15205018,\"storageIdentifier\":\"file://17c2624f9a3-0209ca686fbc\",\"rootDataFileId\":-1,\"checksum\":{\"type\":\"SHA-1\",\"value\":\"240c8ff3e33d6f6fa4e841b0fd7a640c215c3362\"},\"creationDate\":\"2021-09-27\"}},{\"label\":\"Scarab 033.pdf\",\"restricted\":false,\"version\":1,\"datasetVersionId\":1,\"dataFile\":{\"id\":5,\"persistentId\":\"\",\"pidURL\":\"\",\"filename\":\"Scarab 033.pdf\",\"contentType\":\"application/pdf\",\"filesize\":35568,\"storageIdentifier\":\"file://17c2625c9b1-4967a64d8dfe\",\"rootDataFileId\":-1,\"checksum\":{\"type\":\"SHA-1\",\"value\":\"87fe9921f8a4a779afb4f44ee2f6a3b0a69a4120\"},\"creationDate\":\"2021-09-27\"}}],\"citation\":\"Admin, Dataverse, 2021, \\\"otsikko\\\", https://doi.org/10.5072/DAR/TM2JIM, DANS Archaeology Data Station (dev), V1\"}}")
//    val xmlObject = {
//      <root>
//        <id>j.id</id>
//        <doi>j.persistentUrl</doi>
//        <publisher>j.publisher</publisher>
//      </root>
//    }
//    xmlObject.text
//  }

  def enrichFilesXml(doi: Doi, filesXml: Node, datasetXml: Node, downloadUrl: URI): Node = {
    val dctermsNameSpace = getDctermsNamespace(filesXml)
    val accessRights = getAccessRights(datasetXml)
    val rule = new RewriteRule {
      override def transform(n: Node): Seq[Node] = n match {
        case elem: Elem if elem.label == "file" => enrichFileElement(doi, elem, dctermsNameSpace, accessRights, downloadUrl)
        case _ => n
      }
    }
    new RuleTransformer(rule).transform(filesXml).head
  }

  private def enrichFileElement(fileId: FileId, file: Elem, dcNameSpace: String, accessRights: AccessRights, downloadUrl: URI): Elem = {
    val accessibleToRights = file \\ "accessibleToRights"
    val visibleToRights = file \\ "visibleToRights"
    var enriched = file

    if (accessibleToRights.isEmpty)
      enriched = enriched.copy(child = enriched.child ++ getAccessibleToRightsElement(accessRights))

    if (visibleToRights.isEmpty)
      enriched = enriched.copy(child = enriched.child ++ getVisibleToRightsElement())

    addDownloadUrl(fileId, enriched, downloadUrl, dcNameSpace)
  }

  private def getAccessibleToRightsElement(accessRights: AccessRights): Elem = {
    <accessibleToRights>{accessibleToRightsMap(accessRights)}</accessibleToRights>
  }

  private def getVisibleToRightsElement(): Elem = {
    <visibleToRights>{VisibleToRights.ANONYMOUS}</visibleToRights>
  }

  private def addDownloadUrl(fileId: FileId, file: Elem, downloadUrl: URI, dcNameSpace: String) = {
    val downloadPath = downloadUrl.resolve(s"file.xhtml?fileId=$fileId")
    val sourceElement = XML.loadString(s"""<$dcNameSpace:source>$downloadPath</$dcNameSpace:source>""")
    file.copy(child = file.child ++ sourceElement)
  }

  private def getDctermsNamespace(filesXml: Node): String = {
    Option(filesXml.scope.getPrefix("http://purl.org/dc/terms/")).getOrElse("dcterms")
  }

  private def getAccessRights(datasetXml: Node): AccessRights = {
    AccessRights.withName((datasetXml \ "profile" \ "accessRights").text)
  }
}
