package automaton.controller

import automaton.model.{Automaton, DFA, TransitionType}

object AutomatonCache {
  private var cachedAutomaton:Option[Automaton[_, _]] = None

  def getAutomaton:Option[Automaton[_, _]] = cachedAutomaton

  def storeAutomaton[T <: TransitionType, A <: Automaton[T, A]] (automaton:Automaton[T, A]):Unit = {
    cachedAutomaton = Some(automaton)
  }
}




