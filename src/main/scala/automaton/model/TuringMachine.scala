package automaton.model

case class TuringMachine(
             states: Set[State],
             alphabet: Set[String], //input alphabet
             tapeAlphabet: Set[String],
             transitions: Set[TMTransition],
             initialState: State,
             blankSymbol: String,
             finalStates: Set[State],
             override val computations: Seq[Computation]
             ) extends Automaton[TMTransition, TuringMachine] {

  validate()

  override protected def validate(): Unit = {
    super.validate()
    require(tapeAlphabet.nonEmpty, "Tape alphabet must not be empty")
    require(alphabet.forall(tapeAlphabet.contains), "Input alphabet must be subset of tape alphabet")
    require(tapeAlphabet.contains(blankSymbol), "Tape alphabet must include the blank symbol")
    require(!alphabet.contains(blankSymbol), "Blank symbol should not be in input alphabet")

    validateTransitions()
  }

  private def validateTransitions(): Unit = {
    val finalStateTransitions = transitions.filter(t => finalStates.contains(t.source))

    transitions.foreach { t =>
      //source and destination in states
      require(states.contains(t.source), s"Transition source ${t.source} must be in states")
      require(states.contains(t.destination), s"Transition destination ${t.destination} must be in states")

      //tape alphabet contains symbol to read and symbol to write
      require(tapeAlphabet.contains(t.symbol), s"Transition read symbol '${t.symbol}' not in tape alphabet")
      require(tapeAlphabet.contains(t.symbolWrite), s"Transition write symbol '${t.symbolWrite}' not in tape alphabet")
    }

    require(isDeterministic, "Transitions are non-deterministic (multiple options for some state/symbol)")
    require(finalStateTransitions.isEmpty, s"Final states ${finalStateTransitions.map(_.source).mkString(",")} have outgoing transitions")
  }

  //check determinism
  private def isDeterministic: Boolean = {
    transitions.groupBy(t => (t.source, t.symbol)).forall {
      case (_, ts) => ts.size <= 1
    }
  }

  override def withUpdatedComputations(newComps: Seq[Computation]): TuringMachine =
    this.copy(computations = newComps)

  override def testString(input: String): (Boolean, String) = {
    (false, "")
  }
}
