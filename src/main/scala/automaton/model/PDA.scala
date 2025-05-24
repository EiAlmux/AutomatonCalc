package automaton.model

case class PDA(
                states: Set[State],
                alphabet: Set[String],
                stackAlphabet: Set[String],
                transitions: Set[PDATransition],
                initialState: State,
                initialStack: String,
                finalStates: Set[State],
                override val computations: Seq[Computation],
              ) extends Automaton[PDATransition, PDA] {

  validate()

  override protected def validate(): Unit = {
    super.validate()
    require(stackAlphabet.nonEmpty, "stack alphabet non empty")
    require(stackAlphabet.contains(initialStack), "initial stack must be in stack alphabet")
    validateTransitions()
  }

  private def validateTransitions(): Unit = {
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

  override def withUpdatedComputations(newComps: Seq[Computation]): PDA =
    this.copy(computations = newComps)

  override def testString(input: String): (Boolean, String) = {
    case class Config(state: State, remainingInput: List[String], stack: List[String]) {
      override def toString: String = {
        val inputStr = if (remainingInput.isEmpty) "ε" else remainingInput.mkString
        val stackStr = if (stack.isEmpty) "ε" else stack.mkString
        s"($state, $inputStr, $stackStr)"
      }

      def isAccepting(finalStates: Set[State]): (Boolean, String) = {
        val byFinalState = remainingInput.isEmpty && finalStates.contains(state)
        val byEmptyStack = remainingInput.isEmpty && stack.isEmpty

        (byFinalState, byEmptyStack) match {
          case (_, true) => (true, "ACCEPTED by empty stack")
          case (true, _) => (true, "ACCEPTED by final state")
          case _ => (false, "REJECTED")
        }
      }
    }

    val output = new StringBuilder()
    val inputList = input.map(_.toString).toList
    var currentConfigs = Set(Config(initialState, inputList, List(initialStack)))
    var accepted = false
    var acceptanceReason = "REJECTED"

    output.append(s"Initial configuration: \n${currentConfigs.head}\n")

    while (currentConfigs.nonEmpty && !accepted) {
      val newConfigs = currentConfigs.flatMap { config =>
        val inputSymbol = config.remainingInput.headOption.getOrElse("ε")
        val stackSymbol = config.stack.headOption.getOrElse("ε")

        val transitionsToTake = transitions.collect {
          case t if t.source == config.state &&
            (t.symbol == inputSymbol || t.symbol == "ε") &&
            (t.stackSymbolToPop == stackSymbol || t.stackSymbolToPop == "ε") => t
        }

        if (transitionsToTake.isEmpty) {
          output.append(s"  → No transitions available from $config\n")
          None
        } else {
          transitionsToTake.map { t =>
            val newRemaining =
              if (t.symbol != "ε" && config.remainingInput.nonEmpty) config.remainingInput.tail
              else config.remainingInput
            val poppedStack =
              if (t.stackSymbolToPop != "ε") config.stack.tail
              else config.stack
            val updatedStack =
              t.symbolsToPush.filter(_ != "ε") ::: poppedStack

            val newConfig = Config(t.destination, newRemaining, updatedStack)
            output.append(s"  → $newConfig \n")
            newConfig
          }
        }
      }

      val acceptingConfigs = newConfigs.filter { config =>
        val (isAccepting, reason) = config.isAccepting(finalStates)
        if (isAccepting) {
          acceptanceReason = reason
        }
        isAccepting
      }

      if (acceptingConfigs.nonEmpty) {
        accepted = true
        output.append(s"Found accepting configuration: ${acceptingConfigs.head}\n")
      }

      currentConfigs = newConfigs
    }

    output.append(s"Final result: $acceptanceReason\n\n")
    (accepted, output.toString)
  }
 
}


      
