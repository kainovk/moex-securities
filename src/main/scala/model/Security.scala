package org.kainovk
package model

import scala.xml._

case class Security(secid: String,
                    regnumber: String,
                    name: Option[String],
                    emitentTitle: String)

object Security {

  def fromXml(rowElem: Node): Security = {
    val secid = rowElem.attribute("secid").getOrElse("").toString
    val regnumber = rowElem.attribute("regnumber").getOrElse("").toString
    val name = rowElem.attribute("name").map(_.toString)
    val emitentTitle = rowElem.attribute("emitent_title").getOrElse("").toString

    Security(secid, regnumber, name, emitentTitle)
  }
}

case class SecurityWithId(id: Option[Int],
                          secid: String,
                          regnumber: String,
                          name: Option[String],
                          emitentTitle: String)
