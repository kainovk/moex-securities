package org.kainovk
package service.impl

import model.{Security, SecurityWithId}
import repository.SecurityRepository
import service.SecurityService

import cats.effect.IO
import org.http4s.multipart.Part

import scala.xml.{NodeSeq, XML}

class SecurityServiceImpl(repo: SecurityRepository) extends SecurityService {

  override def createSecurity(s: Security): IO[SecurityWithId] = {
    repo.createSecurity(s)
  }

  override def createSecurities(securities: List[Security]): IO[List[SecurityWithId]] = {
    repo.createSecurities(securities)
  }

  override def createSecuritiesFromXml(xmlFileOpt: Option[Part[IO]]): IO[List[SecurityWithId]] = {
    xmlFileOpt match {
      case Some(xmlFile) =>
        xmlFile.body.compile.toVector.flatMap { bytes =>
          val xmlData = new String(bytes.toArray, "UTF-8")
          val securities = parseXml(xmlData)
          createSecurities(securities)
        }

      case None =>
        IO.raiseError(new RuntimeException("No XML file found in the request"))
    }
  }

  override def getSecurity(id: Int): IO[Option[SecurityWithId]] = {
    repo.getSecurity(id)
  }

  override def getSecurityBySecid(secid: String): IO[Option[SecurityWithId]] = {
    repo.getSecurityBySecid(secid)
  }

  override def getSecurities: IO[List[SecurityWithId]] = {
    repo.getSecurities()
  }

  override def updateSecurity(id: Int, s: Security): IO[Option[SecurityWithId]] = {
    repo.updateSecurity(id, s).flatMap {
      case Some(updatedSecurity) => IO.pure(Some(updatedSecurity))
      case None => IO.raiseError(new RuntimeException(s"Security with ID $id not found"))
    }
  }

  override def deleteSecurity(id: Int): IO[Boolean] = {
    repo.deleteSecurity(id)
  }

  private def parseXml(xmlData: String): List[Security] = {
    val elem = XML.loadString(xmlData)
    val dataElem = elem \\ "data" find (_.attribute("id").exists(_.text == "securities"))
    val rowElems = dataElem.map(_ \\ "row").getOrElse(NodeSeq.Empty)
    rowElems.map(Security.fromXml).toList
  }
}
