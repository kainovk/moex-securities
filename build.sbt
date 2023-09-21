ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "scala-test-task-tot-systems",
    idePackagePrefix := Some("org.kainovk")
  )

libraryDependencies ++= Seq(

  "io.circe" %% "circe-core" % "0.14.5",
  "io.circe" %% "circe-generic" % "0.14.5",
  "io.circe" %% "circe-parser" % "0.14.5",

  "org.tpolecat" %% "doobie-core" % "1.0.0-M5",
  "org.tpolecat" %% "doobie-hikari" % "1.0.0-M5",
  "org.tpolecat" %% "doobie-postgres" % "1.0.0-M5",

  "org.http4s" %% "http4s-dsl" % "0.23.18",
  "org.http4s" %% "http4s-blaze-server" % "0.23.14",
  "org.http4s" %% "http4s-circe" % "0.23.18",

  "org.typelevel" %% "kittens" % "3.0.0",

  "com.github.pureconfig" %% "pureconfig" % "0.17.4",
  "com.github.pureconfig" %% "pureconfig-cats-effect" % "0.17.2",
  "eu.timepit" %% "refined-pureconfig" % "0.10.3",

  "org.postgresql" % "postgresql" % "42.5.4",
  "org.flywaydb" % "flyway-core" % "9.16.0",

  "org.typelevel" %% "cats-core" % "2.9.0",

  "ch.qos.logback" % "logback-classic" % "1.4.7",
)