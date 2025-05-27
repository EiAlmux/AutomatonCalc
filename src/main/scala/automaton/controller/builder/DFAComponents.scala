package automaton.controller.builder

import automaton.model.{Computation, DFA, State, Transition}

case class DFAComponents(
                          states: Set[State] = Set.empty,
                          alphabet: Set[String] = Set.empty,
                          transitions: Set[Transition] = Set.empty,
                          initialState: Option[State] = None,
                          finalStates: Set[State] = Set.empty,
                          computations: Seq[Computation] = Seq.empty,
                        ) extends AutomatonComponents[Transition, DFA] {

  def merge(other: AutomatonComponents[Transition, DFA]): DFAComponents = other match {
    case d: DFAComponents =>
      DFAComponents(
        states = this.states ++ d.states,
        alphabet = this.alphabet ++ d.alphabet,
        transitions = this.transitions ++ d.transitions,
        initialState = this.initialState.orElse(d.initialState),
        finalStates = this.finalStates ++ d.finalStates,
        computations = this.computations ++ d.computations
      )
    case _ => throw new IllegalArgumentException("Can only merge with DFAComponents")
  }

  def toDFA: DFA = toAutomaton match {
    case Right(dfa) => dfa
    case Left(err) => throw new RuntimeException(err)
  }

  override def toAutomaton: Either[String, DFA] = initialState match {
    case Some(init) => Right(DFA(states, alphabet, transitions, init, finalStates, computations))
    case None => Left("No initial state defined in the input")
  }
}
