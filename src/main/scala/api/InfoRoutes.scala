package org.kainovk
package api

import model.Info
import repository.{HistoryRepository, InfoRepository, SecurityRepository}

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl._

class InfoRoutes(securityRepo: SecurityRepository,
                 historyRepo: HistoryRepository,
                 infoRepo: InfoRepository) extends Http4sDsl[IO] {

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "info" / secid =>
      val combinedInfoIO = for {
        security <- securityRepo.getSecurityBySecid(secid)
        history <- historyRepo.getHistoryBySecid(secid)
      } yield (security, history)

      combinedInfoIO.flatMap {
        case (Some(security), historyList) =>
          val combinedInfoList = historyList.map { history =>
            Info(
              security.secid,
              security.regnumber,
              security.name,
              security.emitentTitle,
              history.tradedate,
              history.numtrades,
              history.open,
              history.close
            )
          }
          Ok(combinedInfoList)

        case (None, _) =>
          NotFound()
      }

    case GET -> Root / "info" =>
      for {
        info <- infoRepo.getInfo
        resp <- Ok(info)
      } yield resp
  }
}
