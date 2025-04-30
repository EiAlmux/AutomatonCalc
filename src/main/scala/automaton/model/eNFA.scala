package automaton.model

import scala.collection.mutable
import scala.util.boundary
import scala.util.boundary.break

case class eNFA (states: Set [State],
                 alphabet: Set [String],
                 transitions: Set [Transition],
                 initialState: State,
                 finalStates: Set [State],
                 override val computations: Seq [Computation]
                ) extends Automaton [Transition, eNFA] {


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

  override def withUpdatedComputations (newComps: Seq [Computation]): eNFA =
    this.copy(computations = newComps)


  override def testString (input: String): Boolean = boundary [Boolean] {
    val inputSymbols = input.map(_.toString)
    var currentStates = epsilonClosure(Set(initialState))

    inputSymbols.foreach { symbol =>
      currentStates = epsilonClosure(transition(currentStates, symbol))
      if (currentStates.isEmpty) break(false)
    }

    currentStates.exists(finalStates.contains)
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


