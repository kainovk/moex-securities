package org.kainovk
package repository

import model.{Security, SecurityWithId}

import cats.effect.IO

trait SecurityRepository {

  def createSecurity(security: Security): IO[SecurityWithId]

  def getSecurity(id: Int): IO[Option[SecurityWithId]]

  def getSecurityBySecid(secid: String): IO[Option[SecurityWithId]]

  def getSecurities(): IO[List[SecurityWithId]]

  def updateSecurity(id: Int, security: Security): IO[Option[SecurityWithId]]

  def deleteSecurity(id: Int): IO[Boolean]
}
