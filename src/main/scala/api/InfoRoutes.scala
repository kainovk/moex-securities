package org.kainovk
package api

import service.InfoService

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl._

class InfoRoutes(infoService: InfoService) extends Http4sDsl[IO] {

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "info" / secid =>
      infoService.getCombinedInfo(secid)
        .flatMap { combinedInfoList =>
          Ok(combinedInfoList)
        }

    case GET -> Root / "info" =>
      for {
        info <- infoService.getInfo
        resp <- Ok(info)
      } yield resp
  }
}
