package automaton.scenes

import automaton.core.GUIMain
import automaton.utils.{Automaton, DFA, MainAutomaton}
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.VBox

class HomeScene extends Scene(400, 300):
  root = new VBox:
    children = Seq(
      new Button("DFA") {
        onAction = _ =>
          val filePath = "src/exampleDFA.txt"
          val dfa = MainAutomaton.processDFA(filePath)
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


