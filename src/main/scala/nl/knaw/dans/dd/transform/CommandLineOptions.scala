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

import java.nio.file.Path

import better.files.File
import nl.knaw.dans.lib.string._
import org.rogach.scallop.{ ScallopConf, ScallopOption, stringConverter }

class CommandLineOptions(args: Array[String], configuration: Configuration) extends ScallopConf(args) {
  appendDefaultToDescription = true
  editBuilder(_.setHelpWidth(110))
  printedName = "dd-export-for-europeana"
  version(configuration.version)
  val description: String = "Tool to transform Dataverse metadata into Europeana format"
  val synopsis: String =
    s"""
       |  $printedName [-d,--doi|-l,--list] [-t,--transform] [-o,--output]""".stripMargin

  version(s"$printedName v${ configuration.version }")
  banner(
    s"""
       |  $description
       |
       |Usage:
       |
       |$synopsis
       |
       |Options:
       |""".stripMargin)

  private implicit val uuidConverter = stringConverter.flatMap(_.toUUID.fold(e => Left(e.getMessage), uuid => Right(Option(uuid))))

  val doi: ScallopOption[Doi] = opt("doi", short = 'd',
    descr = "The doi for which to transform the metadata")
  private val listPath: ScallopOption[Path] = opt("list", short = 'l',
    descr = "A file containing a newline separated list of doi's for which to transform the metadata")
  val list: ScallopOption[File] = listPath.map(File(_))
  private val transformPath: ScallopOption[Path] = opt("transform", short = 't',
    descr = "The file containing an XSLT to be applied to the metadata; " +
      "if not provided, no transformation will be performed, but the input for the transformation will be returned.")
  val transform: ScallopOption[File] = transformPath.map(File(_))
  private val outputPath: ScallopOption[Path] = opt("output", short = 'o',
    descr = "The directory in which to output the resulting metadata. " +
      "If '-d' is used, this is optional (default to stdout); if '-l' is used, this argument is mandatory.")
  val output: ScallopOption[File] = outputPath.map(File(_))

  requireOne(doi, listPath)
  dependsOnAll(listPath, List(outputPath))

  validatePathExists(listPath)
  validatePathIsFile(listPath)

  validatePathExists(transformPath)
  validatePathIsFile(transformPath)

  validatePathExists(outputPath)
  validatePathIsDirectory(outputPath)

  footer("")
}
