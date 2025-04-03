
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.5"

lazy val root = (project in file("."))
  .settings(
    name := "AutomatonCalc",
    fork := true,

    javaOptions ++= {
      val javafxLibPath = "C:/Users/chr18/Downloads/openjfx-24_windows-x64_bin-sdk/javafx-sdk-24/lib/"
      Seq(
        "--module-path", javafxLibPath,
        "--add-modules", "javafx.controls,javafx.fxml"
      )
    },

    libraryDependencies ++= Seq(
      "org.scalafx" %% "scalafx" % "23.0.1-R34",
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
      "com.typesafe" % "config" % "1.4.3",
      "org.antlr" % "antlr4-runtime" % "4.13.2"
    )
  )

enablePlugins(Antlr4Plugin)
antlr4Version := "4.13.2"