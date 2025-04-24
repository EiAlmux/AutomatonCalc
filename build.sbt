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
      "com.typesafe" % "config" % "1.4.3",
      "org.antlr" % "antlr4-runtime" % "4.13.2",
    )


  )
