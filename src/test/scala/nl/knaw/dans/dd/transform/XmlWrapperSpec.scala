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

import better.files.File
import nl.knaw.dans.dd.transform.fixture.TestSupportFixture
import org.scalatest.BeforeAndAfterEach


import scala.xml.{Utility, XML}

class XmlWrapperSpec extends TestSupportFixture with BeforeAndAfterEach {

  private val dataset_open = (metadataDir / "metadata_OPEN_ACCESS/dataset.xml").toJava
  private val files_open = (metadataDir / "metadata_OPEN_ACCESS/files.xml").toJava
  private val bagmetadataSchema = "src/main/resources/bagmetadata.xsd"

  override def beforeEach(): Unit = {
    super.beforeEach()
    metadataDir.clear()
    File(getClass.getResource("/metadata/").toURI).copyTo(metadataDir)
  }

  "wrap" should "return dataset.xml and files.xml in a wrapping xml" in {
    val datasetXml = XML.loadFile(dataset_open)
    val filesXml = XML.loadFile(files_open)
    val result = Utility.trim(XmlWrapper.wrap(datasetXml, filesXml))
    result.child should have size 2
    result \ "DDM" should have size 1
    result \ "files" should have size 1
    result.child.map(_.label) should contain inOrderOnly("DDM", "files")
  }
}
