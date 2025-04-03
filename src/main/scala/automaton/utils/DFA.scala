package automaton.utils

import automaton.utils.{Automaton, State, Transition}

case class DFA(
                override val states: Seq[State],
                override val alphabet: Seq[String],
                override val transitions: Seq[Transition],
                override val initialState: State,
                override val finalStates: Seq[State],
                override val computations: Seq[String]
              ) extends Automaton(states, alphabet, transitions, initialState, finalStates, computations) {

  override def addState(state: State): DFA = copy(states = states :+ state)

  override def addTransition(transition: Transition): DFA = copy(transitions = transitions :+ transition)

  override def toString: String = {
    s"""
       |DFA(
       |  States: ${states.mkString(", ")}
       |  Alphabet: ${alphabet.mkString(", ")}
       |  Transitions:
       |    ${transitions.mkString("\n    ")}
       |  Initial State: $initialState
       |  Final States: ${finalStates.mkString(", ")}
       |  Computations: ${computations.mkString(", ")}
       |)""".stripMargin
  }

  override def testString(input: String): Boolean = {

    false // Placeholder
  }
}

case class DFAComponents(
                          states: Seq[State] = Seq.empty,
                          alphabet: Seq[String] = Seq.empty,
                          transitions: Seq[Transition] = Seq.empty,
                          initialState: Option[State] = None,
                          finalStates: Seq[State] = Seq.empty,
                          computations: Seq[String] = Seq.empty
                        ) {
  def merge(other: DFAComponents): DFAComponents = DFAComponents(
    states = this.states ++ other.states,
    alphabet = this.alphabet ++ other.alphabet,
    transitions = this.transitions ++ other.transitions,
    initialState = this.initialState.orElse(other.initialState),
    finalStates = this.finalStates ++ other.finalStates,
    computations = this.computations ++ other.computations
  )

  def toDFA: DFA = initialState match {
    case Some(init) => DFA(states, alphabet, transitions, init, finalStates, computations)
    case None => throw new RuntimeException("No initial state defined in the input.")
  }
}