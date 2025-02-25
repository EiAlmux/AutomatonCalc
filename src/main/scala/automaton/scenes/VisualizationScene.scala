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


class VisualizationScene extends Scene(600, 400) {
  val canvas = new Canvas(600, 400)
  val gc: GraphicsContext = canvas.graphicsContext2D

  drawAutomaton()

  root = new Pane {
    children = Seq(canvas)
  }

  def drawAutomaton(): Unit = {
    // Example: Drawing a state as a circle
    DrawingUtils.drawState(gc, 100, 100, "q0")
    // Example: Drawing a transition (arrow)
    DrawingUtils.drawArrow(gc, 150, 125, 250, 125)

    DrawingUtils.drawState(gc, 250, 100, "q1")

  }
}
