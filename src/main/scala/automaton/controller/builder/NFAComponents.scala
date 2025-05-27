package automaton.controller.builder

import automaton.model.{Computation, NFA, State, Transition}

case class NFAComponents(
                          states: Set[State] = Set.empty,
                          alphabet: Set[String] = Set.empty,
                          transitions: Set[Transition] = Set.empty,
                          initialState: Option[State] = None,
                          finalStates: Set[State] = Set.empty,
                          computations: Seq[Computation] = Seq.empty,
                        ) extends AutomatonComponents[Transition, NFA] {

  def merge(other: AutomatonComponents[Transition, NFA]): NFAComponents = other match {
    case n: NFAComponents =>
      NFAComponents(
        states = this.states ++ n.states,
        alphabet = this.alphabet ++ n.alphabet,
        transitions = this.transitions ++ n.transitions,
        initialState = this.initialState.orElse(n.initialState),
        finalStates = this.finalStates ++ n.finalStates,
        computations = this.computations ++ n.computations
      )
    case _ => throw new IllegalArgumentException("Can only merge with NFAComponents")
  }

  def toNFA: NFA = toAutomaton match {
    case Right(nfa) => nfa
    case Left(err) => throw new RuntimeException(err)
  }

  override def toAutomaton: Either[String, NFA] = initialState match {
    case Some(init) => Right(NFA(states, alphabet, transitions, init, finalStates, computations))
    case None => Left("No initial state defined in the input")
  }
}
