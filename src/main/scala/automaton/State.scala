package automaton

case class State(label: String, SFinal: Boolean, x: Double, y: Double) {
  def displayName: String = if (SFinal) s"$label*" else label
}
