package automaton.view

import automaton.GUIMain
import automaton.controller.MainAutomaton
import automaton.model.{Automaton, DFA}
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox

class HomeScene extends Scene(400, 300):
  root = new VBox:
    children = Seq(
      new Button("DFA") {
        onAction = _ =>
          val filePath = "src/exampleDFA.txt"
          val dfa = MainAutomaton.processAutomaton(filePath)
          println(dfa.toString)
          println("\n\nProcessing input...\n")

          val testedDFA = dfa.map(_.testAutomaton())
          println(testedDFA.toString)
      },
      new Button("Visualization") {
        //EMPTY FOR NOW
        onAction = _ => GUIMain.changeScene(new VisualizationScene)
      }
    )


