package automaton.scenes

import scalafx._
import scalafx.scene.canvas._
import scalafx.application.JFXApp3
import scalafx.scene._
import scalafx.scene.control._
import scalafx.scene.layout._
import scalafx.scene.paint._
import scalafx.scene.shape._
import scalafx.stage._
import automaton._
import automaton.Main._

class VisualizationScene extends Scene(600, 400) {
  val canvas = new Canvas(600, 400)
  val gc: GraphicsContext = canvas.graphicsContext2D

  root = new Pane {
    children = Seq(canvas)
  }

  DrawingUtils.drawAutomaton(gc, automaton)

}
