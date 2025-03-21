package automaton

case class Transition(from: State, to: State, symbol: String)

case class State(label: String, FinalState: Boolean) {
  def displayName: String = if (FinalState) s"$label*" else label
}


case class Automaton(
                 val states: Seq[State],
                 val alphabet: Seq[String],
                 val transitions: Seq[Transition],
                 val initialState: State,
                 val finalStates: State
               ) {

  def addState(state: State): Automaton = copy(states = states :+ state)

  def addTransition(transition: Transition): Automaton = copy(transitions = transitions :+ transition)

  // Method to get all transitions for a specific state
  def getTransitionsForState(state: State): Seq[Transition] = {
    transitions.filter(_.from.label == state.label)
  }

  def isValid: Boolean = {
    states.nonEmpty && transitions.nonEmpty
  }

  def testString(input: String): Boolean = {
    // Implement automaton logic here
    false // Placeholder
  }
}