package automaton.view

import automaton.controller.MainAutomaton
import automaton.controller.AutomatonCache
import scalafx._
import scalafx.scene.canvas._
import scalafx.scene._
import scalafx.scene.layout._
import automaton.model.DFA

class VisualizationScene extends Scene(600, 400):
  val canvas = new Canvas(600, 400)
  val gc: GraphicsContext = canvas.graphicsContext2D

  root = new Pane {
    children = Seq(canvas)
  }

//drawAutomaton NOT WORKING
//DrawingUtils.drawAutomaton(gc, dfa)

