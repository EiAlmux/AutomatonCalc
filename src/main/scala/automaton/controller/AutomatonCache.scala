package automaton.controller

import automaton.model.{Automaton, DFA}

object AutomatonCache {
  private var cachedAutomaton: Option[Automaton] = None

  def getAutomaton: Option[Automaton] = cachedAutomaton

  def storeAutomaton(automaton: Automaton): Unit = {
    cachedAutomaton = Some(automaton)
  }

  // For backward compatibility
  def getDFA: Option[DFA] = cachedAutomaton.collect { case dfa: DFA => dfa }
  def storeDFA(dfa: DFA): Unit = storeAutomaton(dfa)
}




