import scala.Seq

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.5"

lazy val root = (project in file("."))
  .settings(
    name := "AutomatonCalc",
    libraryDependencies ++= Seq(
      // ScalaFX for GUI
      "org.scalafx" %% "scalafx" % "23.0.1-R34",

      // ScalaTest for testing
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,

      // Typesafe Config for configuration files
      "com.typesafe" % "config" % "1.4.3"
    ),
  )