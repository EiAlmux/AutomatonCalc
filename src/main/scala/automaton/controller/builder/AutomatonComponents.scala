package automaton.controller.builder

import automaton.model.{Automaton, Computation, Rule, State}


trait AutomatonComponents[R <: Rule, A <: Automaton[R, A]] {
  def states: Set[State]
  def alphabet: Set[String]
  def transitions: Set[R]
  def initialState: Option[State]
  def finalStates: Set[State]
  def computations: Seq[Computation]

  //Optional
  def stackAlphabet: Option [Set [String]] = None
  def initialStack: Option [String] = None

  //Methods
  def merge(other: AutomatonComponents[R, A]): AutomatonComponents[R, A]
  def withComputations(newComps: Seq[Computation]): AutomatonComponents[R, A]
  def toAutomaton: Either[String, A]
}