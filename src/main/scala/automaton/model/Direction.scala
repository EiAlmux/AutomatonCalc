package automaton.model

sealed trait Direction

object Direction {
  def apply(text: String): Direction = text match {
    case "LEFT" | "Left" | "left" => LEFT
    case "RIGHT" | "Right" | "right" => RIGHT
    case _ => throw new IllegalArgumentException(s"Unknown direction: $text")
  }

  def unapply(direction: Direction): Option[String] = Some(direction.toString)
}

case object LEFT extends Direction

case object RIGHT extends Direction

