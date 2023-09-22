package org.kainovk
package api

import model.History
import repository.HistoryRepository

import cats.effect.IO
import org.http4s._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl._

import java.time.LocalDate
import java.time.format.DateTimeParseException

class HistoryRoutes(repo: HistoryRepository) extends Http4sDsl[IO] {

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req@POST -> Root / "history" =>
      req.as[History]
        .flatMap { history =>
          if (history.isValidDate) {
            for {
              createdHistory <- repo.createHistory(history)
              resp <- Ok(createdHistory)
            } yield resp
          } else {
            BadRequest("Invalid date format")
          }
        }
        .handleErrorWith {
          case InvalidMessageBodyFailure(_, _) => BadRequest()
        }

    case GET -> Root / "history" / secid =>
      for {
        history <- repo.getHistoryBySecid(secid)
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
            history <- repo.getHistoryByDate(date.toString)
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
            updatedHistory <- repo.updateHistory(history)
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
        deleted <- repo.deleteHistory(secid)
        resp <- if (deleted) Ok() else NotFound()
      } yield resp
  }
}
