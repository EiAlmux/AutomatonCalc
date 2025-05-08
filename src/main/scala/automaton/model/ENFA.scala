package automaton.model

import scala.collection.mutable
import scala.util.boundary
import scala.util.boundary.break

case class ENFA(states: Set [State],
                alphabet: Set [String],
                transitions: Set [Transition],
                initialState: State,
                finalStates: Set [State],
                override val computations: Seq [Computation]
                ) extends Automaton [Transition, ENFA] {


  validate()

  override protected def validate (): Unit =
    super.validate()
    validateTransitions()


  private def validateTransitions (): Unit =
    transitions.foreach { t =>
      require(states.contains(t.source), s"Transition source ${t.source} must be in states")
      require(states.contains(t.destination), s"Transition destination ${t.destination} must be in states")
      require(alphabet.contains(t.symbol) || t.symbol == "ε", s"Transition symbol ${t.symbol} must be in alphabet")
    }

  override def withUpdatedComputations (newComps: Seq [Computation]): ENFA =
    this.copy(computations = newComps)


  override def testString (input: String): (Boolean, String) = boundary [(Boolean, String)] {
    val inputSymbols = input.map(_.toString)
    var currentStates = epsilonClosure(Set(initialState))
    val output = new StringBuilder()

    def formatStates(states: Set[State]): String =
      if (states.isEmpty) "∅"
      else states.toList.mkString("{", ", ", "}")

    output.append(s"(${formatStates(currentStates)}, ε)\n")

    inputSymbols.zipWithIndex.foreach { case (symbol, index) =>
      val currentInput = inputSymbols.take(index + 1).mkString

      val afterTransition = transition(currentStates, symbol)

      if (afterTransition.isEmpty) {
        output.append(s"  → (∅, $currentInput)\n")
        output.append("REJECTED\n\n")
        break((false, output.toString))
      }
      currentStates = epsilonClosure(afterTransition)
      output.append(s"  → (${formatStates(currentStates)}, $currentInput)\n")
    }

    val accepted = currentStates.exists(finalStates.contains)
    val acceptedValue = if (accepted) "ACCEPTED\n\n" else "REJECTED\n\n"
    output.append(s"Final states: ${formatStates(currentStates)} → $acceptedValue")
    (accepted, output.toString)
  }

  private def transition (states: Set [State], symbol: String): Set [State] = {
    states.flatMap { state =>
      transitions.collect {
        case t if t.source == state && t.symbol == symbol => t.destination
      }
    }
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
}


