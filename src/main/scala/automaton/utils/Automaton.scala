package automaton.utils

case class Transition(source: State, symbol: String, destination: State)

case class State(label: String) {
  override def toString: String = label
}

case class Computation(str: String, isAccepted: Boolean = false, computed: Boolean = false) {
  override def toString: String = {
    if (!computed) str
    else {
      val status = if (isAccepted) "\t\taccepted" else "\t\tNOT accepted"
      s"$str: $status"
    }
  }
}

abstract class Automaton(
                          val states: Seq[State],
                          val alphabet: Seq[String],
                          val transitions: Seq[Transition],
                          val initialState: State,
                          val finalStates: Seq[State],
                          val computations: Seq[Computation]
                        ) {
  def getTransitionsForState(state: State): Seq[Transition] =
    transitions.filter(_.source == state)

  def isValid: Boolean =
    //EXPAND AND ACTUALLY USE IT
    states.nonEmpty && transitions.nonEmpty

  def withComputations(newComps : Seq[Computation]): Automaton

  def testString(input: String): Boolean

  def testAutomaton(): Automaton =
    val updatedComputations = computations.map {
      case c if c.computed => c
      case c => c.copy(
        isAccepted = testString(c.str),
        computed = true
      )
    }
    withComputations(updatedComputations)

}


