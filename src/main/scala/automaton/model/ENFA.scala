package automaton.model

import automaton.view.TransitionView.transitionSetFormat

import scala.collection.mutable
import scala.util.boundary
import scala.util.boundary.break

/**
 * A class representing an Epsilon-Nondeterministic Finite Automaton (ENFA).
 *
 * @constructor Creates a new ENFA with the specified components.
 * @param states       The set of states in the automaton.
 * @param alphabet     The input alphabet (symbols) the automaton recognizes.
 * @param transitions  The set of transitions between states.
 * @param initialState The starting state of the automaton.
 * @param finalStates  The set of accepting/final states.
 * @param computations The sequence of computations performed by the automaton.
 * @throws IllegalArgumentException if the automaton configuration is invalid
 */
case class ENFA(states: Set[State],
                alphabet: Set[String],
                transitions: Set[Transition],
                initialState: State,
                finalStates: Set[State],
                override val computations: Seq[Computation]
               ) extends Automaton[Transition, ENFA] {


  validate()

  /**
   * Creates a new ENFA with updated computations.
   *
   * @param newComps The new sequence of computations
   * @return A new ENFA instance with updated computations
   */
  override def withUpdatedComputations(newComps: Seq[Computation]): ENFA =
    this.copy(computations = newComps)

  /**
   * Tests whether the automaton accepts a given input string.
   *
   * @param input The string to test
   * @return A tuple containing (accepted: Boolean, trace: String) where:
   *         - accepted indicates if the string is accepted
   *         - trace provides a step-by-step computation trace
   */
  override def testString(input: String): (Boolean, String) = boundary[(Boolean, String)] {
    val inputSymbols = input.map(_.toString)
    var currentStates = Set(initialState)
    val output = new StringBuilder()

    def formatStates(states: Set[State]): String =
      if (states.isEmpty) "∅"
      else states.toList.mkString("{", ", ", "}")

    val initialEclose = epsilonClosure(currentStates)
    output.append(s"Initial state: ${formatStates(currentStates)}\n")
    output.append(s"ε-closure: ${formatStates(initialEclose)}\n")
    output.append(s"(${formatStates(initialEclose)}, ε)\n")
    currentStates = initialEclose

    inputSymbols.zipWithIndex.foreach { case (symbol, index) =>
      val currentInput = inputSymbols.take(index + 1).mkString

      val (nextStates, transitions) = transition(currentStates, symbol)

      if (nextStates.isEmpty) {
        output.append(s"  → (∅, $currentInput)\n")
        output.append("REJECTED\n\n")
        break((false, output.toString))
      }
      val statesFmt = f"  → [${formatStates(nextStates)}]"
      output.append(f"$statesFmt%-50s")
      val appliedFmt = f"Applied ${transitionSetFormat(transitions)}\n"
      output.append(f"$appliedFmt%-10s")

      val eclose = epsilonClosure(nextStates)
      output.append(s"ε-closure of ${formatStates(nextStates)}: ${formatStates(eclose)}\n")

      currentStates = eclose
      output.append(s"  → (${formatStates(currentStates)}, $currentInput)\n")
    }


    val accepted = currentStates.exists(finalStates.contains)
    val acceptedValue = if (accepted) "ACCEPTED\n\n" else "REJECTED\n\n"
    output.append(s"Final states: ${formatStates(currentStates)} → $acceptedValue")
    (accepted, output.toString)
  }

  /**
   * Computes the transition for a given set of states and input symbol.
   *
   * @param states The current set of states
   * @param symbol The input symbol
   * @return A tuple containing (nextStates: Set[State], transitions: Set[Transition]) where:
   *         - nextStates are the destination states
   *         - transitions are the matched transitions
   */
  private def transition(states: Set[State], symbol: String): (Set[State], Set[Transition]) = {
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
   * Computes the epsilon closure (ε-closure) for a set of states.
   * The ε-closure includes all states reachable via epsilon transitions.
   *
   * @param states The starting set of states
   * @return The set of all states reachable via epsilon transitions
   */
  private def epsilonClosure(states: Set[State]): Set[State] = {
    val closure = mutable.Set.empty[State]
    val stack = mutable.Stack.empty[State]

    states.foreach { s =>
      closure += s
      stack.push(s)
    }

    while (stack.nonEmpty) {
      val current = stack.pop()
      transitions.collect {
        case t if t.source == current && t.symbol == "ε" => t.destination
      }.foreach {
        nextState =>
          if (!closure.contains(nextState)) {
            closure += nextState
            stack.push(nextState)
          }
      }
    }
    closure.toSet
  }

  /**
   * Validates the automaton configuration.
   *
   * @throws IllegalArgumentException if the automaton is invalid
   */
  override protected def validate(): Unit =
    super.validate()
    validateTransitions()

  /**
   * Validates all transitions in the automaton.
   *
   * @throws IllegalArgumentException if any transition is invalid
   */
  private def validateTransitions(): Unit =
    transitions.foreach { t =>
      require(states.contains(t.source), s"Transition source ${t.source} must be in states")
      require(states.contains(t.destination), s"Transition destination ${t.destination} must be in states")
      require(alphabet.contains(t.symbol) || t.symbol == "ε", s"Transition symbol ${t.symbol} must be in alphabet")
    }
}


