package automaton.model

import automaton.view.TransitionView.transitionFormat

import scala.util.boundary
import scala.util.boundary.break

/**
 * A deterministic finite automaton (DFA) implementation.
 *
 * @constructor Creates a new DFA with the specified components.
 * @param states       The set of states in the DFA
 * @param alphabet     The input alphabet symbols
 * @param transitions  The set of transitions between states
 * @param initialState The starting state of the DFA
 * @param finalStates  The set of accepting/final states
 * @param computations The sequence of computations performed by the DFA
 * @throws IllegalArgumentException if the DFA is invalid (missing states,
 *                                  invalid transitions, etc.)
 */
case class DFA(states: Set[State],
               alphabet: Set[String],
               transitions: Set[Transition],
               initialState: State,
               finalStates: Set[State],
               override val computations: Seq[Computation]
              ) extends Automaton[Transition, DFA] {

  validate()

  /**
   * Tests whether the DFA accepts a given input string.
   *
   * @param input The string to test
   * @return A tuple containing:
   *         - A boolean indicating whether the string was accepted
   *         - A string showing the computation steps
   */
  override def testString(input: String): (Boolean, String) = boundary[(Boolean, String)]:
    val inputSymbols = input.map(_.toString)
    var currentState = initialState
    val output = new StringBuilder()

    output.append(s"($currentState, ε)\n")

    inputSymbols.zipWithIndex.foreach { case (symbol, index) =>
      val currentInput = inputSymbols.take(index + 1).mkString
      transitions.find(t => t.source == currentState && t.symbol == symbol) match {
        case Some(transition) =>
          currentState = transition.destination
          val currentStateFmt = f"  → ($currentState, $currentInput)"
          output.append(f"$currentStateFmt%-50s")
          val appliedFmt = f"${transitionFormat(transition)}%-10s"
          output.append(s" Applied $appliedFmt\n")
        case None =>
          break((false, output.toString))
      }
    }
    val accepted = finalStates.contains(currentState)

    def acceptedValue = if (accepted) "ACCEPTED\n\n" else "REJECTED\n\n"

    output.append(s"Final state: $currentState → $acceptedValue")
    (accepted, output.toString)

  /**
   * Creates a new DFA with updated computations.
   *
   * @param newComps The new sequence of computations
   * @return A new DFA instance with the updated computations
   */
  override def withUpdatedComputations(newComps: Seq[Computation]): DFA =
    this.copy(computations = newComps)

  /**
   * Validates the DFA structure.
   *
   * @throws IllegalArgumentException if:
   *         - The DFA is missing states, initial state, or final states
   *         - States or symbols in transitions don't exist in the DFA
   *         - There aren't exactly one transition per state-symbol pair
   */
  override protected def validate(): Unit =
    super.validate()
    validateTransitions()

  /**
   * Validates that the DFA has exactly one transition for each state-symbol pair.
   *
   * @throws IllegalArgumentException if any state-symbol pair doesn't have
   *                                  exactly one transition
   */
  private def validateTransitions(): Unit = {
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
}