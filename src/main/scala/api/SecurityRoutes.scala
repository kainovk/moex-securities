package org.kainovk
package api

import model.Security
import service.SecurityService

import cats.effect.IO
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl._
import org.http4s.multipart.{Multipart, Part}

class SecurityRoutes(service: SecurityService) extends Http4sDsl[IO] {

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req@POST -> Root / "securities" =>
      req.as[Security]
        .flatMap { s =>
          for {
            createdSecurity <- service.createSecurity(s)
            resp <- Ok(createdSecurity)
          } yield resp
        }
        .handleErrorWith {
          case InvalidMessageBodyFailure(_, _) => BadRequest()
        }

    case req@POST -> Root / "securities" / "upload-xml" =>
      req.decode[Multipart[IO]] { multipart =>
        val xmlFileOpt = multipart.parts.collectFirst {
          case part if isXmlFile(part) => part
        }

        for {
          insertedSecurities <- service.createSecuritiesFromXml(xmlFileOpt)
          resp <- Ok(insertedSecurities)
        } yield resp
      }

    case GET -> Root / "securities" / IntVar(id) =>
      for {
        maybeSecurity <- service.getSecurity(id)
        resp <- maybeSecurity match {
          case Some(security) => Ok(security)
          case None => NotFound()
        }
      } yield resp

    case GET -> Root / "securities" =>
      for {
        securities <- service.getSecurities
        resp <- Ok(securities)
      } yield resp

    case req@PUT -> Root / "securities" / IntVar(id) =>
      req.as[Security]
        .flatMap { s =>
          for {
            rows <- service.updateSecurity(id, s)
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
        deleted <- service.deleteSecurity(id)
        resp <- if (deleted) Ok() else NotFound()
      } yield resp
  }

  private def isXmlFile(part: Part[IO]): Boolean = {
    part.filename.exists(_.matches("securities_.*\\.xml"))
  }
}
