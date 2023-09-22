package org.kainovk
package repository.impl

import model.History
import repository.HistoryRepository

import cats.effect.IO
import doobie._
import doobie.implicits._

import java.text.SimpleDateFormat
import java.util.Date

class DoobieHistoryRepository(tx: Transactor[IO]) extends HistoryRepository {

  val format = new SimpleDateFormat("yyyy-MM-dd")

  override def createHistory(h: History): IO[History] = {
    val date: Date = format.parse(h.tradedate)
    sql"""
        INSERT INTO securities_history (secid, tradedate, numtrades, open)
        VALUES (${h.secid}, $date, ${h.numtrades}, ${h.open})
      """
      .update
      .run
      .transact(tx)
      .map {
        _ => History(h.secid, h.tradedate, h.numtrades, h.open)
      }
  }

  override def getHistoryBySecid(secid: String): IO[List[History]] = {
    sql"""
        SELECT secid, tradedate, numtrades, open
        FROM securities_history
        WHERE secid = $secid
      """
      .query[History]
      .to[List]
      .transact(tx)
  }

  override def getHistoryByDate(dateStr: String): IO[List[History]] = {
    val date: Date = format.parse(dateStr)
    sql"""
        SELECT secid, tradedate, numtrades, open
        FROM securities_history
        WHERE tradedate = $date
      """
      .query[History]
      .to[List]
      .transact(tx)
  }

  override def updateHistory(h: History): IO[Option[History]] = {
    val date: Date = format.parse(h.tradedate)
    sql"""
        UPDATE securities_history
        SET numtrades = ${h.numtrades}, open = ${h.open}
        WHERE secid = ${h.secid} AND tradedate = $date
      """
      .update
      .run
      .transact(tx)
      .map { affectedRows =>
        if (affectedRows == 1) {
          Some(h)
        } else {
          None
        }
      }
  }

  override def deleteHistory(secid: String): IO[Boolean] = {
    sql"DELETE FROM securities_history WHERE secid = $secid"
      .update
      .run
      .transact(tx)
      .map { affectedRows => affectedRows == 1 }
  }
}
