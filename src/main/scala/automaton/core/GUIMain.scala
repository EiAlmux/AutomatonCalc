package automaton.core

import scalafx._
import scalafx.application.JFXApp3
import scalafx.scene._
import automaton.scenes.HomeScene

import scala.language.implicitConversions

/*
TO DO

Check on the dfa to verify integrity
Align better with MVC model
Expand to nfa, enfa, pda

*/


object GUIMain extends JFXApp3 :

  override def start(): Unit =

    stage = new JFXApp3.PrimaryStage :

      title = "Automaton Calculator"
      scene = new HomeScene


  def changeScene(newScene: Scene): Unit =
      stage.scene = newScene


