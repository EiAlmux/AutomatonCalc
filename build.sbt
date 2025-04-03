
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
      "com.typesafe" % "config" % "1.4.3",

      "org.antlr" % "antlr4-runtime" % "4.13.2"

    ),
  )
enablePlugins(Antlr4Plugin)

antlr4Version := "4.13.2"

/*
antlr4Settings

antlr4GenerateListener := true
antlr4GenerateVisitor := true

antlr4OutputDir := (Compile / sourceManaged).value / "antlr"
*/