package automaton.scenes

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
import automaton._
import automaton.Main._

object DrawingUtils {
  //Draws a node for a state
  private def drawState(gc: GraphicsContext, state: State): Unit = {
    val (x, y) = positions.get(state) match
      case Some((x, y)) => (x, y)
      case None         => (-1, -1)
      
    //debug
    println(s"${state.label} $x $y")
    
    gc.setFill(Color.LightBlue)
    gc.fillOval(x, y, 50, 50) // Draw state as a circle

    // State label
    gc.setFill(Color.Black)
    gc.fillText(state.displayName, x + 15, y + 25)
  }

  // Draw an arrow
  private def drawArrow(gc: GraphicsContext, start: State, end: State): Unit = {
    val (startX, startY) = positions.get(start) match
      case Some((x,y)) => (x + 50, y + 25)
      case None        => (-1, -1)
    val (endX, endY) = positions.get(end) match
      case Some((x, y)) => (x, y + 25)
      case None         => (-1, -1)
      
    //debug
    println(s"arrow: $startX, $startY to $endX, $endY")
    
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

  def drawAutomaton(gc: GraphicsContext, automaton: Automaton): Unit =
    automaton.states.foreach(s => drawState(gc, s))
    automaton.transitions.foreach(a => drawArrow(gc, a.from, a.to))
}