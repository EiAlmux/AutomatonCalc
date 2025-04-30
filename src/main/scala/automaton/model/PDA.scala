package automaton.model

case class PDA (
                 states: Set [State],
                 alphabet: Set [String],
                 stackAlphabet: Set [String],
                 transitions: Set [PDATransition],
                 initialState: State,
                 initialStack: String,
                 finalStates: Set [State],
                 override val computations: Seq [Computation],
               ) extends Automaton [PDATransition, PDA] {

  validate()


  override def withUpdatedComputations (newComps: Seq [Computation]): PDA =
    this.copy(computations = newComps)

  override def testString (input: String): Boolean = {
    case class Config (state: State, remainingInput: List [String], stack: List [String])

    var currentConfigs = Set(Config(initialState, input.map(_.toString).toList, List(initialStack)))
    var accepted = false

    while (currentConfigs.nonEmpty && !accepted) {

      val newConfigs = currentConfigs.flatMap { config =>
        val inputSymbol = config.remainingInput.headOption.getOrElse("ε")
        val stackSymbol = config.stack.headOption.getOrElse("ε")

        transitions.collect {
          case t if t.source == config.state &&
            (t.symbol == inputSymbol || t.symbol == "ε") &&
            (t.stackSymbolToPop == stackSymbol || t.stackSymbolToPop == "ε") =>

            val newRemaining =
              if (t.symbol != "ε" && config.remainingInput.nonEmpty) config.remainingInput.tail
              else config.remainingInput
            val poppedStack =
              if (t.stackSymbolToPop != "ε") config.stack.tail
              else config.stack
            val updatedStack =
              t.symbolsToPush.filter(_ != "ε") ::: poppedStack

            Config(t.destination, newRemaining, updatedStack)
        }
      }

      accepted = newConfigs.exists { case Config(state, rem, st) =>
        rem.isEmpty && (finalStates.contains(state) || st.isEmpty)
      }
      currentConfigs = newConfigs
    }
    accepted
  }

  override protected def validate (): Unit = {
    super.validate()
    require(stackAlphabet.nonEmpty, "stack alphabet non empty")
    require(stackAlphabet.contains(initialStack), "initial stack must be in stack alphabet")
    validateTransitions()
  }

  private def validateTransitions (): Unit = {
    transitions.foreach { t =>
      require(states.contains(t.source), s"Transition source ${t.source} must be in states")
      require(states.contains(t.destination), s"Transition destination ${t.destination} must be in states")

      require(
        t.symbol == "ε" || alphabet.contains(t.symbol),
        s"Transition symbol ${t.symbol} must be in alphabet or ε"
        )

      require(
        t.stackSymbolToPop == "ε" || stackAlphabet.contains(t.stackSymbolToPop),
        s"Stack symbol to pop ${t.stackSymbolToPop} must be in stack alphabet or ε"
        )

      t.symbolsToPush.foreach { sym =>
        require(sym == "ε" || stackAlphabet.contains(sym),
                s"Pushed symbol $sym must be in stack alphabet or ε"
                )
      }
    }
  }
}

      
