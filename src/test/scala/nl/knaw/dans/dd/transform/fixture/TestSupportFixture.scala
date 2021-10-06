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
package nl.knaw.dans.dd.transform.fixture

import better.files.File
import better.files.File.currentWorkingDirectory
import org.scalatest.{ Inside, OptionValues }
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.slf4j.bridge.SLF4JBridgeHandler

trait TestSupportFixture extends AnyFlatSpec
  with Matchers
  with Inside
  with OptionValues {

  // Some (XSD) servers will deny requests if the User-Agent is set to the default value for Java
  System.setProperty("http.agent", "Test")

  // disable logs from okhttp3.mockwebserver
  SLF4JBridgeHandler.removeHandlersForRootLogger()
  SLF4JBridgeHandler.install()

  lazy val testDir: File = {
    val path = currentWorkingDirectory / s"target/test/${ getClass.getSimpleName }"
    if (path.exists) path.delete()
    path.createDirectories()
    path
  }

  lazy protected val metadataDir = {
    val path = testDir / "metadata/"
    if (path.exists) path.delete()
    path.createDirectories()
    path
  }
}
