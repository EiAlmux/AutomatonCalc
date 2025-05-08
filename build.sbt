import Antlr4Plugin.autoImport.Antlr4

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.5"

enablePlugins(Antlr4Plugin)

lazy val root = (project in file("."))
  .settings(
    name := "AutomatonCalc",

    Antlr4 / antlr4GenListener := false,
    Antlr4 / antlr4GenVisitor := true,
    Antlr4 / antlr4PackageName := Some("automaton.antrl4"),
    Antlr4 / antlr4Version := "4.13.2",

    // Dependencies
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.4.3",
      "org.antlr" % "antlr4-runtime" % "4.13.2",
    )
  )
