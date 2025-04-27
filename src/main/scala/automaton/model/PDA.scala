package automaton.model

import automaton.controller.builder.PDAComponents

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
        require(
          sym.toString == "ε" || stackAlphabet.contains(sym.toString),
          s"Pushed symbol $sym must be in stack alphabet or ε"
        )
      }
    }
  }

  override def withUpdatedComputations (newComps:Seq[Computation]): PDA =
    this.copy(computations = newComps)

  override def testString(input: String): Boolean = {
    //Placeholder
    false
  }
}

      
