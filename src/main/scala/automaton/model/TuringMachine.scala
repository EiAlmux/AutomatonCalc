package automaton.model

import automaton.view.TransitionView.transitionFormat

import scala.::
import scala.util.boundary
import scala.util.boundary.break

/**
 * A class representing a Turing Machine.
 *
 * @param states       The set of states in the Turing Machine
 * @param alphabet     The input alphabet (symbols that can appear in the input)
 * @param tapeAlphabet The complete tape alphabet (includes input alphabet and additional symbols)
 * @param transitions  The set of transition rules between states
 * @param initialState The starting state of the Turing Machine
 * @param blankSymbol  The symbol representing blank space on the tape
 * @param finalStates  The set of accepting/final states
 * @param computations The sequence of computations performed by the machine
 */
case class TuringMachine(
                          states: Set[State],
                          alphabet: Set[String], //input alphabet
                          tapeAlphabet: Set[String],
                          transitions: Set[TMTransition],
                          initialState: State,
                          blankSymbol: Char,
                          finalStates: Set[State],
                          override val computations: Seq[Computation]
                        ) extends Automaton[TMTransition, TuringMachine] {

  validate()

  /**
   * Creates a new Turing Machine with updated computations.
   *
   * @param newComps The new sequence of computations
   * @return A new Turing Machine with the updated computations
   */
  override def withUpdatedComputations(newComps: Seq[Computation]): TuringMachine =
    this.copy(computations = newComps)

  /**
   * Tests whether the Turing Machine accepts a given input string.
   *
   * @param input The input string to test
   * @return A tuple containing (acceptance status, computation trace)
   */
  override def testString(input: String): (Boolean, String) = boundary[(Boolean, String)] {
    var currentState = initialState

    // Helper classes for tape representation
    case class ActiveTape(state: State, char: Char) {
      override def toString: String = s"($state $char)"
    }
    case class Tape(before: List[Char] = Nil,
                    current: ActiveTape,
                    after: List[Char]) {
      override def toString: String = s"${before.mkString}${current.toString}${after.mkString}"

      /**
       * Moves the tape head right and updates the tape accordingly.
       *
       * @param newState    The new state after transition
       * @param symbolWrite The symbol to write to the current position
       * @return The updated tape after moving right
       */
      def moveRight(newState: State, symbolWrite: Char): Tape = after match {
        case head :: tail =>
          val newBefore = before :+ symbolWrite
          copy(before = newBefore, current = ActiveTape(newState, head), after = tail)
        case Nil =>
          val newBefore = before :+ symbolWrite
          copy(before = newBefore, current = ActiveTape(newState, blankSymbol), after = Nil)
      }

      /**
       * Moves the tape head left and updates the tape accordingly.
       *
       * @param newState    The new state after transition
       * @param symbolWrite The symbol to write to the current position
       * @return The updated tape after moving left
       */
      def moveLeft(newState: State, symbolWrite: Char): Tape = before match {
        case init :+ last =>
          val newAfter = symbolWrite :: after
          copy(before = init, current = ActiveTape(newState, last), after = newAfter)
        case Nil =>
          val newAfter = symbolWrite :: after
          copy(before = Nil, current = ActiveTape(newState, blankSymbol), after = newAfter)
      }
    }

    val trace = new StringBuilder()
    val inputList: List[Char] = input.toList

    var currentTape = Tape(Nil, ActiveTape(initialState, blankSymbol), inputList)
    currentTape = currentTape.moveRight(initialState, blankSymbol)
    trace.append(s"Initial tape:\n$currentTape")


    def accepted = finalStates.contains(currentState)

    val maxSteps = 1000 //check against infinite recursion
    var steps = 0
    while (!accepted) {
      transitions.find(t => t.source == currentState && t.symbol == currentTape.current.char.toString) match {
        case Some(t) =>
          if (t.direction == LEFT) {
            currentTape = currentTape.moveLeft(t.destination, t.symbolWrite.head)
            currentState = t.destination
          }
          else if (t.direction == RIGHT) {
            currentTape = currentTape.moveRight(t.destination, t.symbolWrite.head)
            currentState = t.destination
          }
          val tapeFmt = f"\n  → $currentTape"
          trace.append(f"$tapeFmt%-50s")
          val appliedFmt = f"Applied ${transitionFormat(t)}"
          trace.append(f"$appliedFmt%-1s")

          if (steps >= 1000) {
            trace.append(s"\nPossible infinite recursion: tried 1000 steps. ")
            break((false, trace.toString()))
          }
        case None =>
          trace.append(s"\nNo transition from ($currentState ${currentTape.current.char})  → REJECTED")
          break((false, trace.toString()))
      }
      steps += 1
    }

    def acceptedValue = if (accepted) "ACCEPTED\n\n" else "REJECTED\n\n"

    trace.append(s"\nFinal state: $currentState → $acceptedValue")

    (accepted, trace.toString())
  }

  /**
   * Validates the Turing Machine configuration.
   * Throws IllegalArgumentException if any validation fails.
   */
  override protected def validate(): Unit = {
    super.validate()
    require(tapeAlphabet.nonEmpty, "Tape alphabet must not be empty")
    require(alphabet.forall(tapeAlphabet.contains), "Input alphabet must be subset of tape alphabet")
    require(tapeAlphabet.contains(blankSymbol.toString), "Tape alphabet must include the blank symbol")
    require(!alphabet.contains(blankSymbol.toString), "Blank symbol should not be in input alphabet")

    validateTransitions()
  }

  /**
   * Validates the transitions of the Turing Machine.
   * Throws IllegalArgumentException if any validation fails.
   */
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

  /**
   * Checks if the Turing Machine is deterministic.
   *
   * @return true if the machine is deterministic, false otherwise
   */
  private def isDeterministic: Boolean = {
    transitions.groupBy(t => (t.source, t.symbol)).forall {
      case (_, ts) => ts.size <= 1
    }
  }
}
