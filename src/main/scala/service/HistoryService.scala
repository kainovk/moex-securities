package org.kainovk
package service

import model.History

import cats.effect.IO
import org.http4s.multipart.Part

trait HistoryService {

  def createHistory(history: History): IO[History]

  def createHistory(histories: List[History]): IO[List[History]]

  def createHistoryFromXml(xmlFileOpt: Option[Part[IO]]): IO[List[History]]

  def getHistoryBySecid(secid: String): IO[List[History]]

  def getHistoryByDate(dateStr: String): IO[List[History]]

  def updateHistory(history: History): IO[Option[History]]

  def deleteHistory(secid: String): IO[Boolean]
}
