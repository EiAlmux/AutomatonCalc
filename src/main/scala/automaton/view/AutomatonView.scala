package automaton.view

import automaton.model.{Automaton, Computation, DFA, NFA}

object AutomatonView :

  def automatonFormat(automaton: Automaton): String = {
    automaton match {
      case dfa: DFA => s"""
                          |DFA:
                          |  States: ${dfa.states.mkString(", ")}
                          |  Alphabet: ${dfa.alphabet.mkString(", ")}
                          |  Transitions:
                          |    ${dfa.states.map(state => {
                                val stateTransitions = dfa.transitions.filter(_.source == state)
                                s"$state:\n      ${stateTransitions.map(t => s"${t.symbol} --> ${t.destination}").mkString("\n      ")}"
                                }).mkString("\n    ")}
                          |  Initial State: ${dfa.initialState}
                          |  Final States: ${dfa.finalStates.mkString(", ")}
                          |  Computations: ${dfa.computations.map(compFormat).mkString("\n\t\t\t\t")}
                          |""".stripMargin
      case nfa: NFA => s"""
                          |NFA:
                          | States: ${nfa.states.mkString(", ")}
                          | Alphabet: ${nfa.alphabet.mkString(", ")}
                          |  Transitions:
                          |   ${nfa.states.map (state => {
                              val stateTransitions = nfa.transitions.filter (_.source == state)
                              s"$state:\n      ${stateTransitions.map (t => s"${t.symbol} --> ${t.destination}").mkString ("\n      ")}"
                              }).mkString ("\n    ")}
                          | Initial State: ${nfa.initialState}
                          | Final States: ${nfa.finalStates.mkString(", ")}
                          | Computations: ${nfa.computations.map(compFormat).mkString("\n\t\t\t\t")}
                          |""".stripMargin
      case _ => "Unknown type"
    }
  }

  private def compFormat(computation: Computation): String = {
    if (!computation.computed) computation.str
    else {
      val status = if (computation.isAccepted) "accepted" else "NOT accepted"
      s"${computation.str}: $status"
    }
  }

