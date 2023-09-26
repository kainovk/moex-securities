package org.kainovk
package service.impl

import model.Info
import repository.InfoRepository
import service.{HistoryService, InfoService, SecurityService}

import cats.effect.IO

class InfoServiceImpl(repo: InfoRepository,
                       securityService: SecurityService,
                       historyService: HistoryService
                     ) extends InfoService {

  override def getCombinedInfo(secid: String): IO[List[Info]] = {
    for {
      security <- securityService.getSecurityBySecid(secid)
      history <- historyService.getHistoryBySecid(secid)
    } yield {
      security match {
        case Some(sec) =>
          val combinedInfoList = history.map { history =>
            Info(
              sec.secid,
              sec.regnumber,
              sec.name,
              sec.emitentTitle,
              history.tradedate,
              history.numtrades,
              history.open,
              history.close
            )
          }
          combinedInfoList

        case None =>
          List.empty[Info]
      }
    }
  }

  override def getInfo: IO[List[Info]] = {
    repo.getInfo
  }
}
