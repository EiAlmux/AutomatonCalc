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

class VisualizationScene extends Scene(600, 400) {
  val canvas = new Canvas(600, 400)
  val gc: GraphicsContext = canvas.graphicsContext2D

  val q0: State = new State("q0", false, 100, 100)
  val q1: State = new State("q1", true, 250, 100)

  drawAutomaton()

  root = new Pane {
    children = Seq(canvas)
  }

  def drawAutomaton(): Unit = {

    DrawingUtils.drawState(gc, q0)

    DrawingUtils.drawArrow(gc, 150, 125, 250, 125)

    DrawingUtils.drawState(gc, q1)

  }
}
