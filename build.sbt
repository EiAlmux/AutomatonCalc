import Antlr4Plugin.autoImport.{Antlr4, *}


ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.5"

lazy val root = (project in file("."))
  .settings(
    name := "AutomatonCalc",
    fork := true,

    Compile / unmanagedSourceDirectories += baseDirectory.value / "src/main/antrl4",

    //Antlr4 / unmanagedSourceDirectories += baseDirectory.value / "src/main/antrl4",
    Antlr4 / antlr4PackageName := Some("antrl4"),
    Antlr4 / antlr4Version := "4.13.2",

    javaOptions ++= {
      val osName = System.getProperty("os.name").toLowerCase match {
        case win if win.contains("win") => "win"
        case mac if mac.contains("mac") => "mac"
        case _ => "linux"
      }

      val javafxLibPath = baseDirectory.value / "lib" / s"javafx-sdk-24-$osName" / "lib"
      Seq(
        "--module-path", javafxLibPath.getAbsolutePath,
        "--add-modules", "javafx.controls,javafx.fxml"
      )
    },

    // Dependencies
    libraryDependencies ++= Seq(
      "org.scalafx" %% "scalafx" % "23.0.1-R34",
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
      "com.typesafe" % "config" % "1.4.3",
      "org.antlr" % "antlr4-runtime" % "4.13.2"
    )
  )
