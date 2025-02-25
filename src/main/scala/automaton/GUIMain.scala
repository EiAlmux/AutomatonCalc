package automaton

import scalafx._
import scalafx.application.JFXApp3
import scalafx.scene._
import scalafx.scene.control._
import scalafx.scene.layout._
import scalafx.scene.paint._
import scalafx.scene.shape._
import scalafx.stage._

import automaton.scenes.HomeScene

import scala.language.implicitConversions

object GUIMain extends JFXApp3 :

  override def start(): Unit =

    stage = new JFXApp3.PrimaryStage {

      title = "Automaton Calculator"
      scene = new HomeScene
    }

  def changeScene(newScene: Scene): Unit =
      stage.scene = newScene


