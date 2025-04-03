package automaton.utils

case class Transition(from: State, symbol: String, to: State)

case class State(label: String) {
  override def toString : String = label
}

abstract class Automaton(
                 val states: Seq[State],
                 val alphabet: Seq[String],
                 val transitions: Seq[Transition],
                 val initialState: State,
                 val finalStates: Seq[State],
                 val computations: Seq[String]
               ) {
  def addState(state: State): Automaton

  def addTransition(transition: Transition): Automaton

  def getTransitionsForState(state: State): Seq[Transition] = {
    transitions.filter(_.from == state)
  }

  def isValid: Boolean = {
    states.nonEmpty && transitions.nonEmpty
  }

  def testString(input: String): Boolean

}


