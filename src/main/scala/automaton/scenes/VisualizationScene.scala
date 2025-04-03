package automaton.scenes

import scalafx._
import scalafx.scene.canvas._
import scalafx.scene._
import scalafx.scene.layout._
import automaton.utils.{DFA, MainAutomaton}

class VisualizationScene extends Scene(600, 400) {
  val canvas = new Canvas(600, 400)
  val gc: GraphicsContext = canvas.graphicsContext2D

  root = new Pane {
    children = Seq(canvas)
  }

  val dfa: Option[DFA] = MainAutomaton.DFACache.getDFA

  //drawAutomaton NOT WORKING
  //DrawingUtils.drawAutomaton(gc, dfa)

}
