package org.kainovk
package repository.impl

import model.Info
import repository.InfoRepository

import cats.effect.IO
import doobie._
import doobie.implicits._

class DoobieInfoRepository(tx: Transactor[IO]) extends InfoRepository {

  override def getInfo: IO[List[Info]] = {
    sql"""
          SELECT s.secid, s.regnumber, s.name, s.emitent_title,
                 h.tradedate, h.numtrades, h.open, h.close
          FROM securities s
          LEFT JOIN securities_history h ON s.secid = h.secid
       """
      .query[Option[Info]]
      .to[List]
      .transact(tx)
      .map(_.flatten)
  }
}
