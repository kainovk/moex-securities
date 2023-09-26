package org.kainovk
package repository

import model.History

import cats.effect.IO

trait HistoryRepository {

  def createHistory(history: History): IO[History]

  def createHistory(histories: List[History]): IO[List[History]]

  def getHistoryBySecid(secid: String): IO[List[History]]

  def getHistoryByDate(date: String): IO[List[History]]

  def updateHistory(history: History): IO[Option[History]]

  def deleteHistory(secid: String): IO[Boolean]
}
