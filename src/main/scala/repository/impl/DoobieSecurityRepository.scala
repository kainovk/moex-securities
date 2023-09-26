package org.kainovk
package repository.impl

import model.{Security, SecurityWithId}
import repository.SecurityRepository

import cats.effect.IO
import doobie.ConnectionIO
import doobie.implicits._
import doobie.util.transactor.Transactor
import doobie.util.update.Update

class DoobieSecurityRepository(tx: Transactor[IO]) extends SecurityRepository {

  override def createSecurity(s: Security): IO[SecurityWithId] = {
    sql"""
          INSERT INTO securities (secid, regnumber, name, emitent_title)
          VALUES (${s.secid}, ${s.regnumber}, ${s.name}, ${s.emitentTitle})
       """
      .update
      .withUniqueGeneratedKeys[Int]("id")
      .transact(tx)
      .map {
        id => SecurityWithId(id = Some(id), s.secid, s.regnumber, s.name, s.emitentTitle)
      }
  }

  override def createSecurities(securities: List[Security]): IO[List[SecurityWithId]] = {
    val insertFragment =
      fr"""
        INSERT INTO securities (secid, regnumber, name, emitent_title)
        VALUES (?, ?, ?, ?)
        ON CONFLICT (secid) DO NOTHING
      """

    val insertAction = Update[Security](insertFragment.query.sql)

    val insertAndReturnKeys: ConnectionIO[List[SecurityWithId]] =
      insertAction.updateManyWithGeneratedKeys[Int]("id")(securities).compile.toList
        .map { generatedIds =>
          securities.zip(generatedIds).map { case (s, id) =>
            SecurityWithId(id = Some(id), s.secid, s.regnumber, s.name, s.emitentTitle)
          }
        }

    insertAndReturnKeys.transact(tx)
  }

  override def getSecurity(id: Int): IO[Option[SecurityWithId]] = {
    sql"""
          SELECT id, secid, regnumber, name, emitent_title
          FROM securities
          WHERE id = $id
       """
      .query[SecurityWithId]
      .option
      .transact(tx)
  }

  override def getSecurityBySecid(secid: String): IO[Option[SecurityWithId]] = {
    sql"""
          SELECT id, secid, regnumber, name, emitent_title
          FROM securities
          WHERE secid = $secid
       """
      .query[SecurityWithId]
      .option
      .transact(tx)
  }

  override def getSecurities(): IO[List[SecurityWithId]] = {
    sql"""
          SELECT id, secid, regnumber, name, emitent_title
          FROM securities
       """
      .query[SecurityWithId]
      .to[List]
      .transact(tx)
  }

  override def updateSecurity(id: Int, s: Security): IO[Option[SecurityWithId]] = {
    sql"""
          UPDATE securities
          SET secid = ${s.secid}, regnumber = ${s.regnumber}, name = ${s.name}, emitent_title = ${s.emitentTitle}
          WHERE id = $id
       """
      .update
      .run
      .transact(tx)
      .map { affectedRows =>
        if (affectedRows == 1) {
          Option.apply(SecurityWithId(id = Some(id), s.secid, s.regnumber, s.name, s.emitentTitle))
        } else {
          Option.empty
        }
      }
  }

  override def deleteSecurity(id: Int): IO[Boolean] = {
    sql"DELETE FROM securities WHERE id = $id"
      .update
      .run
      .transact(tx)
      .map { affectedRows => affectedRows == 1 }
  }
}
