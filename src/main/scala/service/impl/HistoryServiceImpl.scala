package org.kainovk
package service.impl

import model.History
import repository.HistoryRepository
import service.HistoryService

import cats.effect.IO
import org.http4s.multipart.Part

import scala.xml.{NodeSeq, XML}

class HistoryServiceImpl(repo: HistoryRepository) extends HistoryService {

  override def createHistory(history: History): IO[History] = {
    createHistory(history)
  }

  override def createHistory(histories: List[History]): IO[List[History]] = {
    repo.createHistory(histories)
  }

  override def createHistoryFromXml(xmlFileOpt: Option[Part[IO]]): IO[List[History]] = {
    xmlFileOpt match {
      case Some(xmlFile) =>
        xmlFile.body.compile.toVector.flatMap { bytes =>
          val xmlData = new String(bytes.toArray, "UTF-8")
          val histories = parseXml(xmlData)
          createHistory(histories)
        }

      case None =>
        IO.raiseError(new RuntimeException("No XML file found in the request"))
    }
  }

  override def getHistoryBySecid(secid: String): IO[List[History]] = {
    repo.getHistoryBySecid(secid)
  }

  override def getHistoryByDate(dateStr: String): IO[List[History]] = {
    repo.getHistoryByDate(dateStr)
  }

  override def updateHistory(history: History): IO[Option[History]] = {
    repo.updateHistory(history).flatMap {
      case Some(updatedHistory) => IO.pure(Some(updatedHistory))
      case None => IO.raiseError(new RuntimeException(
        s"History with secid ${history.secid} " +
          s"and tradedate ${history.tradedate} not found"
      ))
    }
  }

  override def deleteHistory(secid: String): IO[Boolean] = {
    repo.deleteHistory(secid)
  }

  private def parseXml(xmlData: String): List[History] = {
    val elem = XML.loadString(xmlData)
    val dataElem = elem \\ "data" find (_.attribute("id").exists(_.text == "history"))
    val rowElems = dataElem.map(_ \\ "row").getOrElse(NodeSeq.Empty)
    rowElems.map(History.fromXml).toList
  }
}
