package automaton.utils

case class DFA(
                override val states: Seq[State],
                override val alphabet: Seq[String],
                override val transitions: Seq[Transition],
                override val initialState: State,
                override val finalStates: Seq[State],
                override val computations: Seq[Computation]
              ) extends Automaton(states, alphabet, transitions, initialState, finalStates, computations) {

  override def toString: String =
    s"""
       |DFA(
       |  States: ${states.mkString(", ")}
       |  Alphabet: ${alphabet.mkString(", ")}
       |  Transitions:
       |    ${transitions.mkString("\n    ")}
       |  Initial State: $initialState
       |  Final States: ${finalStates.mkString(", ")}
       |  Computations: ${computations.mkString("\n\t\t\t\t")}
       |)""".stripMargin

  override def testString(input: String): Boolean = {
    val inputSymbols = input.map(_.toString)
    var currentState = initialState

    inputSymbols.foreach { symbol =>
      transitions.find(t => t.source == currentState && t.symbol == symbol) match
        case Some(transition) =>
          currentState = transition.destination
        case None =>
          None

    }
    val accepted = finalStates.contains(currentState)
    accepted
  }

  override def withComputations(newComps: Seq[Computation]): Automaton =
    DFAComponents(
      states = this.states,
      alphabet = this.alphabet,
      transitions = this.transitions,
      initialState = Some(this.initialState),
      finalStates = this.finalStates,
      computations = newComps
    ).toDFA

}


case class DFAComponents(
                          states: Seq[State] = Seq.empty,
                          alphabet: Seq[String] = Seq.empty,
                          transitions: Seq[Transition] = Seq.empty,
                          initialState: Option[State] = None,
                          finalStates: Seq[State] = Seq.empty,
                          computations: Seq[Computation] = Seq.empty
                        ) {
  def merge(other: DFAComponents): DFAComponents = DFAComponents(
    states = this.states ++ other.states,
    alphabet = this.alphabet ++ other.alphabet,
    transitions = this.transitions ++ other.transitions,
    initialState = this.initialState.orElse(other.initialState),
    finalStates = this.finalStates ++ other.finalStates,
    computations = this.computations ++ other.computations
  )

  def withComputations(newComps: Seq[Computation]): DFAComponents =
    this.copy(computations = newComps)

  def addComputation(comp: Computation): DFAComponents =
    this.copy(computations = computations :+ comp)

  def toDFA: DFA = initialState match {
    case Some(init) => DFA(states, alphabet, transitions, init, finalStates, computations)
    case None => throw new RuntimeException("No initial state defined in the input.")
  }
}