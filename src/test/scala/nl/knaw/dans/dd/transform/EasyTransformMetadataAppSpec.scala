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
///**
// * Copyright (C) 2021 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package nl.knaw.dans.dd.transform
//
//import nl.knaw.dans.dd.transform.fixture.TestSupportFixture
//
//import java.net.URI
//import java.util.UUID
//import okhttp3.HttpUrl
//import okhttp3.mockwebserver.{MockResponse, MockWebServer}
//import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
//
//import scala.util.{Failure, Success}
//
//class EasyTransformMetadataAppSpec extends TestSupportFixture with BeforeAndAfterAll with BeforeAndAfterEach {
//
//  // configure the mock server
//  private val server = new MockWebServer
//  server.start()
//  private val test_server = "/test_server/"
//  private val baseUrl: HttpUrl = server.url(test_server)
//
//  private val app = new TransformMetadataApp(Configuration("1.0.0", BagStoreConfig(baseUrl.uri(), 0l, 0l), new URI("")))
//  private val bagId: BagId = UUID.fromString("0000000-0000-0000-0000-000000000001")
//
//  override protected def afterAll(): Unit = {
//    server.shutdown()
//    super.afterAll()
//  }
//
//  "fetchDatasetXml" should "fetch the xml-file from a correct location" in {
//    server.enqueue(new MockResponse().setBody("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ddm:DDM>-</ddm:DDM>"))
//    app.fetchDatasetXml(bagId) should matchPattern { case Success(<ddm:DDM>-</ddm:DDM>) => }
//    server.takeRequest.getRequestLine shouldBe s"GET ${ test_server }bags/$bagId/metadata/dataset.xml HTTP/1.1"
//  }
//
//  it should "fail if the server returns 404" in {
//    server.enqueue(new MockResponse().setResponseCode(404).setBody("ERROR!!!"))
//    inside(app.fetchDatasetXml(bagId)) {
//      case Failure(HttpStatusException(_, response)) =>
//        response.body shouldBe "ERROR!!!"
//        response.code shouldBe 404
//    }
//    server.takeRequest().getRequestLine shouldBe s"GET ${ test_server }bags/$bagId/metadata/dataset.xml HTTP/1.1"
//  }
//
//  it should "have userAgent in the header of the request" in {
//    server.enqueue(new MockResponse().setBody("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ddm:DDM>-</ddm:DDM>"))
//    app.fetchDatasetXml(bagId) shouldBe a[Success[_]]
//    server.takeRequest().getHeader("User-Agent") shouldBe "dd-export-for-europeana/1.0.0"
//  }
//
//  "fetchFilesXml" should "fetch the xml-file from a correct location" in {
//    server.enqueue(new MockResponse().setBody("<?xml version=\"1.0\" encoding=\"UTF-8\"?><files>-</files>"))
//    app.fetchFilesXml(bagId) should matchPattern { case Success(<files>-</files>) => }
//    server.takeRequest.getRequestLine shouldBe s"GET ${ test_server }bags/$bagId/metadata/files.xml HTTP/1.1"
//  }
//}
