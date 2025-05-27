package automaton.controller.builder

import automaton.model.{Computation, State, TMTransition, TuringMachine}

case class TMComponents(
                         states: Set[State] = Set.empty,
                         alphabet: Set[String] = Set.empty,
                         tapeAlphabet: Set[String] = Set.empty,
                         transitions: Set[TMTransition] = Set.empty,
                         initialState: Option[State] = None,
                         blankSymbol: Option[String] = None,
                         finalStates: Set[State] = Set.empty,
                         computations: Seq[Computation] = Seq.empty,
                       ) extends AutomatonComponents[TMTransition, TuringMachine] {

  def merge(other: AutomatonComponents[TMTransition, TuringMachine]): TMComponents = other match {
    case tm: TMComponents =>
      TMComponents(
        states = this.states ++ tm.states,
        alphabet = this.alphabet ++ tm.alphabet,
        tapeAlphabet = this.tapeAlphabet ++ tm.tapeAlphabet,
        transitions = this.transitions ++ tm.transitions,
        initialState = this.initialState.orElse(tm.initialState),
        blankSymbol = this.blankSymbol.orElse(tm.blankSymbol),
        finalStates = this.finalStates ++ tm.finalStates,
        computations = this.computations ++ tm.computations
      )
    case _ => throw new IllegalArgumentException("Can only merge with TMComponents")
  }

  def toTM: TuringMachine = toAutomaton match {
    case Right(tm) => tm
    case Left(err) => throw new RuntimeException(err)
  }

  override def toAutomaton: Either[String, TuringMachine] = (initialState, blankSymbol) match {
    case (Some(init), Some(blankSymbol)) => Right(TuringMachine(states, alphabet, tapeAlphabet, transitions,
      init, blankSymbol, finalStates, computations))
    case (None, _) => Left("No initial state defined in the input")
    case (_, None) => Left("No blank symbol defined in the input")
  }
}
