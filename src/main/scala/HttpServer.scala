package org.kainovk

import api.{HistoryRoutes, SecurityRoutes}
import config.Config
import db.Database
import repository.impl.{DoobieHistoryRepository, DoobieSecurityRepository}

import scala.concurrent.ExecutionContext.global
import cats.effect._
import cats.implicits.toSemigroupKOps
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.implicits._

object HttpServer {

  def create(configFile: String = "application.conf"): IO[ExitCode] = {
    resources(configFile).use(create)
  }

  private def resources(configFile: String): Resource[IO, Resources] = {
    for {
      config <- Config.load(configFile)
      ec <- ExecutionContexts.fixedThreadPool[IO](config.database.threadPoolSize)
      transactor <- Database.transactor(config.database, ec)
    } yield Resources(transactor, config)
  }

  private def create(resources: Resources): IO[ExitCode] = {
    for {
      _ <- Database.initialize(resources.transactor)
      securityRepository = new DoobieSecurityRepository(resources.transactor)
      historyRepository = new DoobieHistoryRepository(resources.transactor)
      securityRoutes = new SecurityRoutes(securityRepository).routes
      historyRoutes = new HistoryRoutes(historyRepository).routes
      routes = securityRoutes <+> historyRoutes
      exitCode <- BlazeServerBuilder[IO]
        .bindHttp(resources.config.server.port, resources.config.server.host)
        .withHttpApp(routes.orNotFound).serve.compile.lastOrError
    } yield exitCode
  }

  private case class Resources(transactor: HikariTransactor[IO], config: Config)
}
