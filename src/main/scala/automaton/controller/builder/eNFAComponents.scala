package automaton.controller.builder

import automaton.model.{Computation, ENFA, State, Transition}

case class eNFAComponents(
                           states: Set[State] = Set.empty,
                           alphabet: Set[String] = Set.empty,
                           transitions: Set[Transition] = Set.empty,
                           initialState: Option[State] = None,
                           finalStates: Set[State] = Set.empty,
                           computations: Seq[Computation] = Seq.empty,
                         ) extends AutomatonComponents[Transition, ENFA] {

  def merge(other: AutomatonComponents[Transition, ENFA]): eNFAComponents = other match {
    case e: eNFAComponents =>
      eNFAComponents(
        states = this.states ++ e.states,
        alphabet = this.alphabet ++ e.alphabet,
        transitions = this.transitions ++ e.transitions,
        initialState = this.initialState.orElse(e.initialState),
        finalStates = this.finalStates ++ e.finalStates,
        computations = this.computations ++ e.computations
      )
    case _ => throw new IllegalArgumentException("Can only merge with NFAComponents")
  }

  def toeNFA: ENFA = toAutomaton match {
    case Right(enfa) => enfa
    case Left(err) => throw new RuntimeException(err)
  }

  override def toAutomaton: Either[String, ENFA] = initialState match {
    case Some(init) => Right(ENFA(states, alphabet, transitions, init, finalStates, computations))
    case None => Left("No initial state defined in the input")
  }
}
