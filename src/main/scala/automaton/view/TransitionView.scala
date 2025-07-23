package automaton.view

import automaton.model._

//This could be reworked to use transition format and a method to get all transition from an automaton
//instead of whatever mess this is.

object TransitionView {

  def transitionsStr(automaton: Automaton[_, _]): String = automaton match {
    case tm: TuringMachine =>
      tm.states.toList.sortBy(_.toString).map { state =>
        val stateTransitions = tm.transitions.collect {
          case t if t.source == state => transitionFormat(t)
        }
        s"$state:\n      ${stateTransitions.mkString("\n      ")}"
      }.mkString("\n    ")
    case pda: PDA =>
      pda.states.toList.sortBy(_.toString).map { state =>
        val stateTransitions = pda.transitions.collect {
          case t if t.source == state =>
            val pushStr = if (t.symbolsToPush.isEmpty) "ε" else t.symbolsToPush.mkString
            s"${t.source}, ${t.symbol}, ${t.stackSymbolToPop} -> ${t.destination}, $pushStr"
        }
        s"$state:\n      ${stateTransitions.mkString("\n      ")}"
      }.mkString("\n    ")

    case dfa: DFA =>
      dfa.states.toList.sortBy(_.toString).map { state =>
        val stateTransitions = dfa.transitions.collect {
          case t if t.source == state => s"${t.symbol} --> ${t.destination}"
        }
        s"$state:\n      ${stateTransitions.mkString("\n      ")}"
      }.mkString("\n    ")
    case nfa: NFA =>
      nfa.states.toList.sortBy(_.toString).map { state =>
        val stateTransitions = nfa.transitions.collect {
          case t if t.source == state => s"${t.symbol} --> ${t.destination}"
        }
        s"$state:\n      ${stateTransitions.mkString("\n      ")}"
      }.mkString("\n    ")
    case enfa: ENFA =>
      enfa.states.toList.sortBy(_.toString).map { state =>
        val stateTransitions = enfa.transitions.collect {
          case t if t.source == state => s"${t.symbol} --> ${t.destination}"
        }
        s"$state:\n      ${stateTransitions.mkString("\n      ")}"
      }.mkString("\n    ")
    case _ => ""
  }

  def transitionFormat(transition: Rule): String = transition match {
    case tmt: TMTransition =>
      s"${tmt.source}, ${tmt.symbol} --> ${tmt.destination}, ${tmt.symbolWrite}, ${tmt.direction}"
    case pdaT: PDATransition =>
      val pushStr = if (pdaT.symbolsToPush.isEmpty) "ε" else pdaT.symbolsToPush.mkString
      s"${pdaT.source}, ${pdaT.symbol}, ${pdaT.stackSymbolToPop} -> ${pdaT.destination}, $pushStr"
    case t: Transition =>
      s"${t.source}, ${t.symbol} --> ${t.destination}"
    case _ => "Unknown transition"
  }
  
  //Used in NFA and e-NFA view
  def transitionSetFormat(transitions: Set[Transition]): String = {
    transitions.map(transitionFormat).mkString("(", "), (", ")")
  }
}
