package org.kainovk
package api

import model.History
import service.HistoryService

import cats.effect.IO
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl._
import org.http4s.multipart.{Multipart, Part}

import java.time.LocalDate
import java.time.format.DateTimeParseException

class HistoryRoutes(service: HistoryService) extends Http4sDsl[IO] {

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req@POST -> Root / "history" =>
      req.as[History]
        .flatMap { history =>
          if (history.isValidDate) {
            for {
              createdHistory <- service.createHistory(history)
              resp <- Ok(createdHistory)
            } yield resp
          } else {
            BadRequest("Invalid date format")
          }
        }
        .handleErrorWith {
          case InvalidMessageBodyFailure(_, _) => BadRequest()
        }

    case req@POST -> Root / "history" / "upload-xml" =>
      req.decode[Multipart[IO]] { multipart =>
        val xmlFileOpt = multipart.parts.collectFirst {
          case part if isXmlFile(part) => part
        }

        for {
          insertedHistory <- service.createHistoryFromXml(xmlFileOpt)
          resp <- Ok(insertedHistory)
        } yield resp
      }

    case GET -> Root / "history" / secid =>
      for {
        history <- service.getHistoryBySecid(secid)
        resp <- Ok(history)
      } yield resp

    case GET -> Root / "history" / "date" / dateStr => {
      val parsedDate: Option[LocalDate] = try {
        Some(LocalDate.parse(dateStr))
      } catch {
        case _: DateTimeParseException => None
      }

      parsedDate match {
        case Some(date) =>
          for {
            history <- service.getHistoryByDate(date.toString)
            resp <- Ok(history)
          } yield resp
        case None =>
          BadRequest(s"Invalid date format: $dateStr")
      }
    }

    case req@PUT -> Root / "history" =>
      req.as[History]
        .flatMap { history =>
          for {
            updatedHistory <- service.updateHistory(history)
            resp <- updatedHistory match {
              case Some(h) => Ok(h)
              case None => NotFound()
            }
          } yield resp
        }
        .handleErrorWith {
          case InvalidMessageBodyFailure(_, _) => BadRequest()
        }

    case DELETE -> Root / "history" / secid =>
      for {
        deleted <- service.deleteHistory(secid)
        resp <- if (deleted) Ok() else NotFound()
      } yield resp
  }

  private def isXmlFile(part: Part[IO]): Boolean = {
    part.filename.exists(_.matches("history_.*\\.xml"))
  }
}
