/*
 * Copyright 2016 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.makubi.maven.plugin.avrohugger

import java.io.File

import at.makubi.maven.plugin.avrohugger.Implicits._
import at.makubi.maven.plugin.avrohugger.api.AvrohuggerGenerator
import avrohugger.Generator
import avrohugger.format.SpecificRecord
import com.julianpeeters.avrohugger.filesorter.{AVDLFileSorter, AVSCFileSorter}
import org.apache.maven.plugin.logging.Log

import scala.collection.mutable.ListBuffer

class AvrohuggerGeneratorImpl extends AvrohuggerGenerator {
  override def generateScalaFiles(inputDirectory: File, outputDirectory: String, log: Log, recursive: Boolean): Unit = {
    val generator = new Generator(SpecificRecord)

    listFiles(inputDirectory, recursive).foreach { schemaFile =>
      log.info(s"Generating Scala files for ${schemaFile.getAbsolutePath}")

      generator.fileToFile(schemaFile, outputDirectory)
    }
  }

  protected def listFiles(inputDirectory: File, recursive: Boolean): Seq[File] = {
    val allFiles = inputDirectory.listFiles()
    val schemaFiles = new ListBuffer[File]()

    schemaFiles ++= AVDLFileSorter.sortSchemaFiles(allFiles.withSuffix(".avdl"))
    schemaFiles ++= AVSCFileSorter.sortSchemaFiles(allFiles.withSuffix(".avsc"))
    schemaFiles ++= allFiles.withSuffix(".avpr")
    schemaFiles ++= allFiles.withSuffix(".avro")

    if (recursive) {
      schemaFiles ++= allFiles.filter { _.isDirectory }.flatMap { listFiles(_, true) }
    }

    schemaFiles
  }

}
