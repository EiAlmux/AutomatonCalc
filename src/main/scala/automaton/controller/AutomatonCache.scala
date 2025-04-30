package automaton.controller

import automaton.model.{Automaton, PDA}

object AutomatonCache {
  private var cachedAutomaton:Option[Automaton[_, _]] = None
  
  def getAutomaton:Option[Automaton[_, _]] = cachedAutomaton
  def storeAutomaton (automaton:Automaton[_, _]):Unit = 
    cachedAutomaton = Some(automaton)

  private var cachedPDA: Option[PDA] = None

  def getPDA: Option [PDA] = cachedPDA
  def storePDA(pda: PDA): Unit =
    cachedPDA = Some(pda)

}




