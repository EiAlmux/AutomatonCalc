package automaton.model

import automaton.controller.builder.DFAComponents

import scala.util.boundary
import scala.util.boundary.break

case class DFA (states:Set[State],
                alphabet:Set[String],
                transitions:Set[Transition],
                initialState:State,
                finalStates:Set[State],
                override val computations:Seq[Computation]
               ) extends Automaton[Transition, DFA] {

  validate()

  override protected def validate ():Unit =
    super.validate()
    validateTransitions()

  private def validateTransitions ():Unit = {
    val transitionCounts = transitions
      .groupBy(t => (t.source, t.symbol))
      .view
      .mapValues(_.size)

    for {
      state <- states
      symbol <- alphabet
    } {
      require(
        transitionCounts.getOrElse((state, symbol), 0) == 1,
        s"DFA must have exactly one transition for state $state and symbol $symbol"
      )
    }
  }

  override def testString(input: String): (Boolean, String) = boundary[(Boolean, String)] :
    val inputSymbols = input.map(_.toString)
    var currentState = initialState
    val output = new StringBuilder()

    output.append(s"($currentState, ε)\n")

    inputSymbols.zipWithIndex.foreach { case (symbol, index) =>
      val currentInput = inputSymbols.take(index + 1).mkString
      transitions.find(t => t.source == currentState && t.symbol == symbol) match {
        case Some(transition) =>
          currentState = transition.destination
          output.append(s"  → ($currentState, $currentInput)\n")
        case None =>
          break((false, output.toString))
      }
    }
    val accepted = finalStates.contains(currentState)
    def acceptedValue = if (accepted) "ACCEPTED\n\n" else "REJECTED\n\n"
    output.append(s"Final state: $currentState → $acceptedValue")
    (accepted, output.toString)


  override def withUpdatedComputations(newComps: Seq[Computation]): DFA =
    this.copy(computations = newComps)

}