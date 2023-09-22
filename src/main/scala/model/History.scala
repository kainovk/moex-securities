package org.kainovk
package model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

import java.text.SimpleDateFormat
import java.util.Date

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
                   open: Double) {
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
  implicit val historyDecoder: Decoder[History] = Decoder.forProduct4(
    "secid",
    "tradedate",
    "numtrades",
    "open"
  )(History.apply)

  implicit val historyEncoder: Encoder[History] = deriveEncoder[History]
}
