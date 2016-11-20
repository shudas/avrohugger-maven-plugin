package com.julianpeeters.avrohugger.filesorter

import spray.json.DefaultJsonProtocol._
import spray.json._

/**
  * Code adapted from https://github.com/ch4mpy/sbt-avro/blob/master/src/main/scala/com/c4soft/sbtavro/SbtAvro.scala
  * by Jerome Wascongne
  *
  * Copied from https://github.com/julianpeeters/sbt-avro/tree/ebf8309223eedf716d36f2dc3c973d37fca58b81/src/main/scala/sbtavro/filesorter
  */
object ReferredTypeFinder {

  object Keys {
    val Fields = "fields"
    val Type = "type"
    val Items = "items"
    val Array = "array"
    val Enum = "enum"
    val Record = "record"
    val Name = "name"
  }

  def findReferredTypes(json: JsValue): List[String] = {

    def matchComplexType(fields: Map[String,JsValue]): List[String] = {
      val typeOfRef = fields(Keys.Type)
      typeOfRef match {
        case JsString(Keys.Array) => findReferredTypes(fields(Keys.Items))
        case JsString(Keys.Enum) => List(fields(Keys.Name).convertTo[String])
        case JsString(Keys.Record) => findReferredTypes(fields(Keys.Fields))
        case nestedDefinition => findReferredTypes(nestedDefinition)
      }
    }

    json match {
      case str: JsString => List(str.value)
      case union: JsArray => union.elements.toList.flatMap(findReferredTypes(_))
      case complex: JsObject => matchComplexType(complex.fields)
      case _ => List.empty
    }

  }

}