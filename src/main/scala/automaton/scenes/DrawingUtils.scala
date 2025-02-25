package automaton.scenes

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
import automaton._

object DrawingUtils {
  //Draws a node for a state
  def drawState(gc: GraphicsContext, state: State): Unit = {
    gc.setFill(Color.LightBlue)
    gc.fillOval(state.x, state.y, 50, 50) // Draw state as a circle

    // State label
    gc.setFill(Color.Black)
    gc.fillText(state.label, state.x + 15, state.y + 25)
  }

  // Draw an arrow
  def drawArrow(gc: GraphicsContext, startX: Double, startY: Double, endX: Double, endY: Double): Unit = {
    gc.setStroke(Color.Black)
    gc.setLineWidth(2)
    gc.strokeLine(startX, startY, endX, endY)

    // Arrowhead
    val arrowSize = 10
    val angle = Math.atan2(endY - startY, endX - startX)
    val arrowX1 = endX - arrowSize * Math.cos(angle - Math.PI / 6)
    val arrowY1 = endY - arrowSize * Math.sin(angle - Math.PI / 6)
    val arrowX2 = endX - arrowSize * Math.cos(angle + Math.PI / 6)
    val arrowY2 = endY - arrowSize * Math.sin(angle + Math.PI / 6)

    gc.strokeLine(endX, endY, arrowX1, arrowY1)
    gc.strokeLine(endX, endY, arrowX2, arrowY2)
  }
}