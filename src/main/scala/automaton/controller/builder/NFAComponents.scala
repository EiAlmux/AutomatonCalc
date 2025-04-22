package automaton.controller.builder

import automaton.model.{Computation, NFA, State, Transition}


case class NFAComponents(
                          states: Set[State] = Set.empty,
                          alphabet: Set[String] = Set.empty,
                          transitions: Set[Transition] = Set.empty,
                          initialState: Option[State] = None,
                          finalStates: Set[State] = Set.empty,
                          computations: Seq[Computation] = Seq.empty,
                          automatonType: Option[String] = Some("NFA")
                        ) {
  def merge(other: NFAComponents): NFAComponents = NFAComponents(
    states = this.states ++ other.states,
    alphabet = this.alphabet ++ other.alphabet,
    transitions = this.transitions ++ other.transitions,
    initialState = this.initialState.orElse(other.initialState),
    finalStates = this.finalStates ++ other.finalStates,
    computations = this.computations ++ other.computations,
    automatonType = (this.automatonType, other.automatonType) match {
      case (Some("ε-NFA"), _) | (_, Some("ε-NFA")) => Some("ε-NFA")
      case (Some("NFA"), _) | (_, Some("NFA")) => Some("NFA")
      case (None, None) => None
    }
  )

  def withComputations(newComps: Seq[Computation]): NFAComponents =
    this.copy(computations = newComps)

  def addComputation(comp: Computation): NFAComponents =
    this.copy(computations = computations :+ comp)

  def toNFA: NFA = initialState match {
    case Some(init) => NFA(states, alphabet, transitions, init, finalStates, computations, automatonType)
    case None => throw new RuntimeException("No initial state defined in the input.")
  }
}
