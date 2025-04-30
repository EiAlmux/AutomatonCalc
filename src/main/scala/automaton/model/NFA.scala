package automaton.model

import automaton.controller.builder.NFAComponents

import scala.collection.mutable
import scala.util.boundary
import scala.util.boundary.break

case class NFA (states:Set[State],
                alphabet:Set[String],
                transitions:Set[Transition],
                initialState:State,
                finalStates:Set[State],
                override val computations:Seq[Computation],
               ) extends Automaton[Transition, NFA] {

  
  validate()

  override protected def validate ():Unit =
    super.validate()
    validateTransitions()

  private def validateTransitions ():Unit =
    transitions.foreach { t =>
      require(states.contains(t.source), s"Transition source ${t.source} must be in states")
      require(states.contains(t.destination), s"Transition destination ${t.destination} must be in states")
      require(t.symbol != "ε", "ε-transitions are not allowed in NFA")
      require(alphabet.contains(t.symbol), s"Transition symbol ${t.symbol} must be in alphabet")

    }

  override def withUpdatedComputations (newComps:Seq[Computation]):NFA =
    this.copy(computations = newComps)


  override def testString (input:String):Boolean = boundary[Boolean] {
    val inputSymbols = input.map(_.toString)
    var currentStates = Set(initialState)

    inputSymbols.foreach { symbol =>
      currentStates = transition(currentStates, symbol)
      if (currentStates.isEmpty) break(false)
    }

    currentStates.exists(finalStates.contains)
  }

  private def transition (states:Set[State], symbol:String):Set[State] = {
    states.flatMap { state =>
      transitions.collect {
        case t if t.source == state && t.symbol == symbol => t.destination
      }
    }
  }
  
}
