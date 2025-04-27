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

  private def validateTransitions ():Unit =
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

  override def testString(input: String): Boolean = boundary[Boolean] :
    val inputSymbols = input.map(_.toString)
    var currentState = initialState

    inputSymbols.foreach { symbol =>
      transitions.find(t => t.source == currentState && t.symbol == symbol) match
        case Some(transition) => currentState = transition.destination
        case None => break(false)
    }
    finalStates.contains(currentState)


  override def withUpdatedComputations(newComps: Seq[Computation]): DFA =
    this.copy(computations = newComps)







}