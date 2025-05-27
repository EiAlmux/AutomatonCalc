package automaton.view

import automaton.model.{Automaton, DFA, ENFA, NFA, PDA, TuringMachine}

object TransitionView {

  def transitionsStr(automaton: Automaton[_, _]): String = automaton match {
    case tm: TuringMachine =>
      tm.states.toList.sortBy(_.toString).map { state =>
        val stateTransitions = tm.transitions.collect {
          case t if t.source == state => s"${t.source}, ${t.symbol} --> ${t.destination}, ${t.symbolWrite}, ${t.direction}"
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


}
