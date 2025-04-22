package automaton.model

case class Transition(source: State, symbol: String, destination: State)

case class State(label: String) {
  override def toString: String = label
}

case class Computation(str: String, isAccepted: Boolean = false, computed: Boolean = false)


abstract class Automaton(
                          val states: Set[State],
                          val alphabet: Set[String],
                          val transitions: Set[Transition],
                          val initialState: State,
                          val finalStates: Set[State],
                          val computations: Seq[Computation],
                          val automatonType: Option[String]
                        ) {
  def getTransitionsForState(state: State): Set[Transition] =
    transitions.filter(_.source == state)

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


