package automaton.model

import automaton.controller.builder.NFAComponents

import scala.collection.mutable
import scala.util.boundary
import scala.util.boundary.break

case class NFA(override val states: Set[State],
               override val alphabet: Set[String],
               override val transitions: Set[Transition],
               override val initialState: State,
               override val finalStates: Set[State],
               override val computations: Seq[Computation],
               override val automatonType: Option[String] = Some("NFA")
              ) extends Automaton(states, alphabet, transitions, initialState, finalStates, computations, automatonType) {

  validate()

  override def testString(input: String): Boolean = boundary[Boolean] {
    val inputSymbols = input.map(_.toString)
    var currentStates = epsilonClosure(Set(initialState))

    inputSymbols.foreach { symbol =>
      currentStates = epsilonClosure(transition(currentStates, symbol))
      if (currentStates.isEmpty) break(false)
    }

    currentStates.exists(finalStates.contains)
  }

  private def transition(states: Set[State], symbol: String): Set[State] = {
    states.flatMap { state =>
      transitions.collect {
        case t if t.source == state && t.symbol == symbol => t.destination
      }
    }
  }

  private def epsilonClosure(states: Set[State]): Set[State] = {
    if (!automatonType.contains("ε-NFA")) states
    else {
        val closure = mutable.Set.empty[State]
        val stack = mutable.Stack.empty[State]

        states.foreach { s =>
          closure += s
          stack.push(s)
        }

        while(stack.nonEmpty) {
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

  override def withComputations(newComps: Seq[Computation]): Automaton =
    NFAComponents(
      states = this.states,
      alphabet = this.alphabet,
      transitions = this.transitions,
      initialState = Some(this.initialState),
      finalStates = this.finalStates,
      computations = newComps,
      automatonType = this.automatonType
    ).toNFA

  private def validate(): Unit = {
    require(states.nonEmpty, "states non empty")
    require(alphabet.nonEmpty, "alphabet non empty")
    require(areDisjoint(states, alphabet), "States and alphabet must be disjoint")
    require(states.contains(initialState), "initial state must be in states")
    require(finalStates.forall(states.contains), "all final states must be in states")
    validateTransitions()
  }
  
  private def areDisjoint(states: Set[State], strings: Set[String]): Boolean =
    !states.exists(s => strings.contains(s.label))

  private def validateTransitions(): Unit =
    transitions.foreach { t =>
      require(states.contains(t.source), s"Transition source ${t.source} must be in states")
      require(states.contains(t.destination), s"Transition destination ${t.destination} must be in states")
      
      if (t.symbol == "ε") {
        require(automatonType.contains("ε-NFA"), "ε-transitions are only allowed in ε-NFA")
      } else {
        require(alphabet.contains(t.symbol), s"Transition symbol ${t.symbol} must be in alphabet")
      }
    }
}
