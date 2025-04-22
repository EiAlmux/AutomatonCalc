package automaton.controller.builder

import automaton.model.{Automaton, Computation, State, Transition}


case class AutomatonComponents(
                                states: Set[State] = Set.empty,
                                alphabet: Set[String] = Set.empty,
                                transitions: Set[Transition] = Set.empty,
                                initialState: Option[State] = None,
                                finalStates: Set[State] = Set.empty,
                                computations: Seq[Computation] = Seq.empty,
                                automatonType: Option[String] = None, //"DFA", "NFA", "ε-NFA"
                              ) {
  def merge(other: AutomatonComponents): AutomatonComponents = AutomatonComponents(
    states = this.states ++ other.states,
    alphabet = this.alphabet ++ other.alphabet,
    transitions = this.transitions ++ other.transitions,
    initialState = this.initialState.orElse(other.initialState),
    finalStates = this.finalStates ++ other.finalStates,
    computations = this.computations ++ other.computations,
    automatonType = (this.automatonType, other.automatonType) match {
      case (Some("ε-NFA"), _) | (_, Some("ε-NFA")) => Some("ε-NFA")
      case (Some("NFA"), _) | (_, Some("NFA")) => Some("NFA")
      case (Some("DFA"), _) | (_, Some("DFA")) => Some("DFA")
      case (None, None) => None
    }
  )

  def withComputations(newComps: Seq[Computation]): AutomatonComponents =
    this.copy(computations = newComps)

  def addComputation(comp: Computation): AutomatonComponents =
    this.copy(computations = computations :+ comp)

  def toAutomaton: Automaton = {
    val initState = initialState.getOrElse(
      throw new RuntimeException("No initial State")
    )

    automatonType match {
      case Some("DFA") =>
        DFAComponents(
          states,
          alphabet,
          transitions,
          Some(initState),
          finalStates,
          computations,
          Some("DFA")
        ).toDFA

      case Some("NFA") =>
        NFAComponents(
          states,
          alphabet,
          transitions,
          Some(initState),
          finalStates,
          computations,
          Some("NFA")
        ).toNFA

      case Some("eNFA") =>
        NFAComponents(
          states,
          alphabet,
          transitions,
          Some(initState),
          finalStates,
          computations,
          Some("ε-NFA")
        ).toNFA

      case Some(other) => throw new IllegalArgumentException(s"Unknown automaton type: $other")
      case None => throw new IllegalArgumentException("Empty automaton type")
    }
  }
}