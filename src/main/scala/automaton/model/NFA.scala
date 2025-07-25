package automaton.model

import automaton.view.TransitionView.transitionSetFormat

import scala.collection.mutable
import scala.util.boundary
import scala.util.boundary.break

/**
 * A class representing a Non-deterministic Finite Automaton (NFA).
 *
 * @param states       The set of states in the NFA
 * @param alphabet     The input alphabet symbols
 * @param transitions  The set of transitions between states
 * @param initialState The initial/start state of the NFA
 * @param finalStates  The set of accepting/final states
 * @param computations The sequence of computations performed by the NFA
 */
case class NFA (states: Set [State],
                alphabet: Set [String],
                transitions: Set [Transition],
                initialState: State,
                finalStates: Set [State],
                override val computations: Seq [Computation],
               ) extends Automaton [Transition, NFA] {


  validate()

  /**
   * Creates a new NFA with updated computations.
   *
   * @param newComps The new sequence of computations
   * @return A new NFA with the updated computations
   */
  override def withUpdatedComputations (newComps: Seq [Computation]): NFA =
    this.copy(computations = newComps)

  /**
   * Tests whether the NFA accepts a given input string.
   *
   * @param input The input string to test
   * @return A tuple containing (accepted: Boolean, trace: String) where:
   *         - accepted indicates if the string is accepted
   *         - trace provides a step-by-step computation trace
   */
  override def testString (input: String): (Boolean, String) = boundary [(Boolean, String)] {
    val inputSymbols = input.map(_.toString)
    var currentStates = Set(initialState)
    val output = new StringBuilder()

    def formatStates (states: Set [State]): String =
      if (states.isEmpty) "∅"
      else states.toList.mkString("{", ", ", "}")

    output.append(s"(${formatStates(currentStates)},  ε)\n")

    inputSymbols.zipWithIndex.foreach { case (symbol, index) =>
      val currentInput = inputSymbols.take(index + 1).mkString
      val (nextStates, transitions) = transition(currentStates, symbol)

      if (nextStates.isEmpty) {
        output.append(s"  → (∅, $currentInput)\n")
        output.append("REJECTED\n\n")
        break((false, output.toString))
      }

      val statesFmt = f"  → (${formatStates(nextStates)}, $currentInput)"
      output.append(f"$statesFmt%-50s")
      val appliedFmt = f"Applied ${transitionSetFormat(transitions)}\n"
      output.append(f"$appliedFmt%-10s")
      currentStates = nextStates
    }

    val accepted = currentStates.exists(finalStates.contains)
    val acceptedValue = if (accepted) "ACCEPTED\n\n" else "REJECTED\n\n"
    output.append(s"Final states: ${formatStates(currentStates)} → $acceptedValue")
    (accepted, output.toString)
  }

  /**
   * Computes the next set of states and transitions for a given set of current states and input symbol.
   *
   * @param states The current set of states
   * @param symbol The input symbol
   * @return A tuple containing (nextStates: Set[State], transitions: Set[Transition]) where:
   *         - nextStates is the set of destination states
   *         - transitions is the set of transitions used
   */
  private def transition (states: Set [State], symbol: String): (Set [State], Set [Transition]) = {
    val stateTransitionPairs = states.flatMap { state =>
      transitions.collect {
        case t if t.source == state && t.symbol == symbol => (t.destination, t)
      }
    }
    val destinationStates = stateTransitionPairs.map(_._1)
    val matchedTransitions = stateTransitionPairs.map(_._2)

    (destinationStates, matchedTransitions)
  }

  /**
   * Validates the NFA configuration.
   * Checks that all states, transitions, and alphabet symbols are properly defined.
   */
  override protected def validate (): Unit =
    super.validate()
    validateTransitions()

  /**
   * Validates all transitions in the NFA.
   * Ensures that:
   * - Transition sources and destinations are valid states
   * - No epsilon transitions are present
   * - Transition symbols are in the alphabet
   */
  private def validateTransitions (): Unit =
    transitions.foreach { t =>
      require(states.contains(t.source), s"Transition source ${t.source} must be in states")
      require(states.contains(t.destination), s"Transition destination ${t.destination} must be in states")
      require(t.symbol != "ε", "ε-transitions are not allowed in NFA")
      require(alphabet.contains(t.symbol), s"Transition symbol ${t.symbol} must be in alphabet")
    }

}
