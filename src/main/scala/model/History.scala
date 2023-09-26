package org.kainovk
package model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

import java.text.SimpleDateFormat
import java.util.Date
import scala.xml.Node

case class DateWrapper(tradedate: String) {
  def toDate: Option[Date] = {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
    try {
      Some(dateFormat.parse(tradedate))
    } catch {
      case _: Exception => None
    }
  }
}

object DateWrapper {
  implicit val dateWrapperDecoder: Decoder[DateWrapper] = deriveDecoder[DateWrapper]
}

case class History(secid: String,
                   // TODO: LocalDate + encoder, decoder
                   tradedate: String,
                   numtrades: Int,
                   open: Option[Double],
                   close: Option[Double]) {
  def isValidDate: Boolean = try {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
    dateFormat.setLenient(false)
    dateFormat.parse(tradedate)
    true
  } catch {
    case _: Exception => false
  }
}

object History {
  implicit val historyDecoder: Decoder[History] = Decoder.forProduct5(
    "secid",
    "tradedate",
    "numtrades",
    "open",
    "close"
  )(History.apply)

  implicit val historyEncoder: Encoder[History] = deriveEncoder[History]

  def fromXml(rowElem: Node): History = {
    val secid = rowElem.attribute("SECID").map(_.text).getOrElse("")
    val tradedate = rowElem.attribute("TRADEDATE").map(_.text).getOrElse("")
    val numtrades = rowElem.attribute("NUMTRADES").map(_.text.toInt).getOrElse(0)
    val open = rowElem.attribute("OPEN").flatMap(_.text.toDoubleOption)
    val close = rowElem.attribute("CLOSE").flatMap(_.text.toDoubleOption)

    History(secid, tradedate, numtrades, open, close)
  }
}
