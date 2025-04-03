package automaton

import automaton.GUIMain.stage
import scalafx._
import scalafx.application.JFXApp3
import scalafx.scene._
import scenes.HomeScene

import scala.language.implicitConversions

object GUIMain extends JFXApp3 :

  override def start(): Unit =

    stage = new JFXApp3.PrimaryStage :

      title = "Automaton Calculator"
      scene = new HomeScene
    

  def changeScene(newScene: Scene): Unit =
      stage.scene = newScene


