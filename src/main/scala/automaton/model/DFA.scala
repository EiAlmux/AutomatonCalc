package automaton.model

import automaton.controller.builder.DFAComponents

import scala.util.boundary
import scala.util.boundary.break

case class DFA(
                override val states: Set[State],
                override val alphabet: Set[String],
                override val transitions: Set[Transition],
                override val initialState: State,
                override val finalStates: Set[State],
                override val computations: Seq[Computation],
                override val automatonType: Option[String] = Some("DFA")
              ) extends Automaton(states, alphabet, transitions, initialState, finalStates, computations, automatonType) {

  validate()

  override def testString(input: String): Boolean = boundary[Boolean] :
    val inputSymbols = input.map(_.toString)
    var currentState = initialState

    inputSymbols.foreach { symbol =>
      transitions.find(t => t.source == currentState && t.symbol == symbol) match
        case Some(transition) => currentState = transition.destination
        case None => break(false)
    }
    finalStates.contains(currentState)


  override def withComputations(newComps: Seq[Computation]): Automaton =
    DFAComponents(
      states = this.states,
      alphabet = this.alphabet,
      transitions = this.transitions,
      initialState = Some(this.initialState),
      finalStates = this.finalStates,
      computations = newComps
    ).toDFA

  private def validate(): Unit =
    require(states.nonEmpty, "states non empty")
    require(alphabet.nonEmpty, "alphabet non empty")
    require(areDisjoint(states, alphabet), "States and alphabet must be disjoint")
    require(states.contains(initialState), "initial state must be in states")
    require(finalStates.forall(states.contains), "all final states must be in states")
    validateTransitions()

  private def areDisjoint(states: Set[State], strings: Set[String]): Boolean =
    !states.exists(s => strings.contains(s.label))

  private def validateTransitions(): Unit =
    transitions.foreach { t =>
      require(states.contains(t.source), s"Transition source ${t.source} must be in states")
      require(states.contains(t.destination), s"Transition destination ${t.destination} must be in states")
      require(alphabet.contains(t.symbol), s"Transition symbol ${t.symbol} must be in alphabet")
    }

    for {
      state <- states
      symbol <- alphabet
    } {
      val matchingTransitions = transitions.count(t => t.source == state && t.symbol == symbol)
      require(matchingTransitions == 1, s"DFA must have exactly one transition for state $state and symbol $symbol")
    }

}