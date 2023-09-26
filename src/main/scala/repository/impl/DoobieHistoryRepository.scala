package org.kainovk
package repository.impl

import model.History
import repository.HistoryRepository

import cats.effect.IO
import doobie._
import doobie.implicits._
import doobie.util.update.Update

import java.text.SimpleDateFormat
import java.util.Date

class DoobieHistoryRepository(tx: Transactor[IO]) extends HistoryRepository {

  val format = new SimpleDateFormat("yyyy-MM-dd")

  private type InsertHistory = (String, Date, Int, Option[Double], Option[Double], String)

  override def createHistory(h: History): IO[History] = {
    val date: Date = format.parse(h.tradedate)
    sql"""
        INSERT INTO securities_history (secid, tradedate, numtrades, open, close)
        VALUES (${h.secid}, $date, ${h.numtrades}, ${h.open}, ${h.close})
      """
      .update
      .run
      .transact(tx)
      .map {
        _ => History(h.secid, h.tradedate, h.numtrades, h.open, h.close)
      }
  }

  def createHistory(histories: List[History]): IO[List[History]] = {
    val insertFragment =
      fr"""
          INSERT INTO securities_history (secid, tradedate, numtrades, open, close)
          SELECT ?, ?, ?, ?, ?
          WHERE EXISTS (SELECT 1 FROM securities WHERE secid = ?)
          ON CONFLICT (secid, tradedate) DO NOTHING
        """

    val insertAction = Update[InsertHistory](insertFragment.query.sql)

    val insertHistories = histories.map { history =>
      val date = format.parse(history.tradedate)
      (history.secid, date, history.numtrades, history.open, history.close, history.secid)
    }

    val insertAndReturn: ConnectionIO[Int] = insertAction.updateMany(insertHistories)

    insertAndReturn.transact(tx).flatMap { insertedCount =>
      val successfullyInsertedHistories = insertHistories.take(insertedCount).map {
        case (secid, date, numtrades, open, close, _) =>
          History(secid, format.format(date), numtrades, open, close)
      }
      IO.pure(successfullyInsertedHistories)
    }
  }

  override def getHistoryBySecid(secid: String): IO[List[History]] = {
    sql"""
        SELECT secid, tradedate, numtrades, open, close
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
        SELECT secid, tradedate, numtrades, open, close
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
        SET numtrades = ${h.numtrades}, open = ${h.open}, close = ${h.close}
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
