package org.kainovk
package api

import model.Security
import repository.SecurityRepository

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl._

class SecurityRoutes(repo: SecurityRepository) extends Http4sDsl[IO] {

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req@POST -> Root / "securities" =>
      req.as[Security]
        .flatMap { s =>
          for {
            createdSecurity <- repo.createSecurity(s)
            resp <- Ok(createdSecurity)
          } yield resp
        }
        .handleErrorWith {
          case InvalidMessageBodyFailure(_, _) => BadRequest()
        }

    case GET -> Root / "securities" / IntVar(id) =>
      for {
        maybeSecurity <- repo.getSecurity(id)
        resp <- maybeSecurity match {
          case Some(security) => Ok(security)
          case None => NotFound()
        }
      } yield resp

    case GET -> Root / "securities" =>
      for {
        securities <- repo.getSecurities()
        resp <- Ok(securities)
      } yield resp

    case req@PUT -> Root / "securities" / IntVar(id) =>
      req.as[Security]
        .flatMap { s =>
          for {
            rows <- repo.updateSecurity(id, s)
            res <- rows match {
              case None => NotFound()
              case _ => Ok(rows.get)
            }
          } yield res
        }
        .handleErrorWith {
          case InvalidMessageBodyFailure(_, _) => BadRequest()
        }

    case DELETE -> Root / "securities" / IntVar(id) =>
      for {
        deleted <- repo.deleteSecurity(id)
        resp <- if (deleted) Ok() else NotFound()
      } yield resp
  }
}
