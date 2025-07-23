package automaton.model

import automaton.view.TransitionView.transitionSetFormat

import scala.collection.mutable
import scala.util.boundary
import scala.util.boundary.break

case class ENFA (states: Set [State],
                 alphabet: Set [String],
                 transitions: Set [Transition],
                 initialState: State,
                 finalStates: Set [State],
                 override val computations: Seq [Computation]
                ) extends Automaton [Transition, ENFA] {


  validate()

  override def withUpdatedComputations (newComps: Seq [Computation]): ENFA =
    this.copy(computations = newComps)

  override def testString (input: String): (Boolean, String) = boundary [(Boolean, String)] {
    val inputSymbols = input.map(_.toString)
    var currentStates = Set(initialState)
    val output = new StringBuilder()

    def formatStates (states: Set [State]): String =
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
      output.append(s"  → [${formatStates(nextStates)}] \t\t\t applied ${transitionSetFormat(transitions)}\n")

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

  private def epsilonClosure (states: Set [State]): Set [State] = {
    val closure = mutable.Set.empty [State]
    val stack = mutable.Stack.empty [State]

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

  override protected def validate (): Unit =
    super.validate()
    validateTransitions()

  private def validateTransitions (): Unit =
    transitions.foreach { t =>
      require(states.contains(t.source), s"Transition source ${t.source} must be in states")
      require(states.contains(t.destination), s"Transition destination ${t.destination} must be in states")
      require(alphabet.contains(t.symbol) || t.symbol == "ε", s"Transition symbol ${t.symbol} must be in alphabet")
    }
}


