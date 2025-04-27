package automaton.controller.builder

import automaton.model.{Automaton, Computation, State, Transition, TransitionType}


trait AutomatonComponents[T <: TransitionType, A <: Automaton[T, A]] {
  def states: Set[State]
  def alphabet: Set[String]
  def transitions: Set[T]
  def initialState: Option[State]
  def finalStates: Set[State]
  def computations: Seq[Computation]

  def merge(other: AutomatonComponents[T, A]): AutomatonComponents[T, A]
  def withComputations(newComps: Seq[Computation]): AutomatonComponents[T, A]
  def toAutomaton: Either[String, A]
}