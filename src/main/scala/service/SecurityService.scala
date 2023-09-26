package org.kainovk
package service

import model.{Security, SecurityWithId}

import cats.effect.IO
import org.http4s.multipart.Part

trait SecurityService {

  def createSecurity(s: Security): IO[SecurityWithId]

  def createSecurities(securities: List[Security]): IO[List[SecurityWithId]]

  def createSecuritiesFromXml(xmlFileOpt: Option[Part[IO]]): IO[List[SecurityWithId]]

  def getSecurity(id: Int): IO[Option[SecurityWithId]]

  def getSecurityBySecid(secid: String): IO[Option[SecurityWithId]]

  def getSecurities: IO[List[SecurityWithId]]

  def updateSecurity(id: Int, s: Security): IO[Option[SecurityWithId]]

  def deleteSecurity(id: Int): IO[Boolean]
}
