package automaton.controller.builder

import automaton.model.{Computation, PDA, PDATransition, State, Transition}

case class PDAComponents(
                          states: Set[State] = Set.empty,
                          alphabet: Set[String] = Set.empty,
                          stackAlphabet: Set[String] = Set.empty,
                          transitions: Set[PDATransition] = Set.empty,
                          initialState: Option[State] = None,
                          initialStack: Option[String] = None,
                          finalStates: Set[State] = Set.empty,
                          computations: Seq[Computation] = Seq.empty,
                          ) extends AutomatonComponents[PDATransition, PDA] {

  def merge (other: AutomatonComponents[PDATransition, PDA]): PDAComponents = other match {
    case p: PDAComponents =>
      PDAComponents (
        states = this.states ++ other.states,
        alphabet = this.alphabet ++ other.alphabet,
        stackAlphabet = this.stackAlphabet ++ other.stackAlphabet,
        transitions = this.transitions ++ other.transitions,
        initialState = this.initialState.orElse (other.initialState),
        initialStack = this.initialStack.orElse (other.initialStack),
        finalStates = this.finalStates ++ other.finalStates,
        computations = this.computations ++ other.computations,
        )
    case _ => throw new IllegalArgumentException ("Can only merge with PDAComponents")
  }

  def withComputations(newComps: Seq[Computation]): PDAComponents =
    this.copy(computations = newComps)

  override def toAutomaton: Either [String, PDA] = initialState match {
    case Some (init) => Right (PDA (states, alphabet, stackAlphabet, transitions, init, initialStack, finalStates, computations))
    case None => Left ("No initial state defined in the input")
  }

  def toPDA: PDA = toAutomaton match {
    case Right (pda) => pda
    case Left (err) => throw new RuntimeException (err)
  }
}
