package automaton.controller.builder

import automaton.model.{Computation, PDA, PDATransition, State}

case class PDAComponents(
                          states: Set[State] = Set.empty,
                          alphabet: Set[String] = Set.empty,
                          private val _stackAlphabet: Set[String] = Set.empty,
                          transitions: Set[PDATransition] = Set.empty,
                          initialState: Option[State] = None,
                          private val _initialStack: Option[String] = None,
                          finalStates: Set[State] = Set.empty,
                          computations: Seq[Computation] = Seq.empty,
                        ) extends AutomatonComponents[PDATransition, PDA] {

  def merge(other: AutomatonComponents[PDATransition, PDA]): PDAComponents = other match {
    case p: PDAComponents =>
      PDAComponents(
        states = this.states ++ other.states,
        alphabet = this.alphabet ++ other.alphabet,
        _stackAlphabet = this._stackAlphabet ++ other.stackAlphabet.getOrElse(Set.empty),
        transitions = this.transitions ++ other.transitions,
        initialState = this.initialState.orElse(other.initialState),
        _initialStack = this._initialStack.orElse(other.initialStack),
        finalStates = this.finalStates ++ other.finalStates,
        computations = this.computations ++ other.computations,
      )
    case _ => throw new IllegalArgumentException("Can only merge with PDAComponents")
  }

  def toPDA: PDA = toAutomaton match {
    case Right(pda) => pda
    case Left(err) => throw new RuntimeException(err)
  }

  override def toAutomaton: Either[String, PDA] = (initialState, initialStack) match {
    case (Some(init), Some(initialStack)) => Right(PDA(states, alphabet, stackAlphabet.getOrElse(Set.empty),
      transitions, init, initialStack, finalStates, computations))
    case (None, _) => Left("No initial state defined in the input")
    case (_, None) => Left("No initial stack symbol defined in the input")
  }

  //Override optional fields from AutomatonComponents
  override def stackAlphabet: Option[Set[String]] = Some(_stackAlphabet)

  override def initialStack: Option[String] = _initialStack
}
