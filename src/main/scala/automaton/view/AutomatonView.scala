package automaton.view

import automaton.model.{Automaton, Computation, DFA, NFA}

object AutomatonView :

  def automatonFormat (automaton:Automaton[_, _]):String = {
    val header = automaton match {
      case _:DFA => "DFA:"
      case nfa: NFA => if (nfa.epsilonNFA) "ε-NFA:" else "NFA:"
      case _ => return "View error: Unknown automaton type"
    }
    val transitionsStr = automaton.states.map { state =>
      val stateTransitions = automaton.transitions.collect {
        case t if t.source == state => s"${t.symbol} --> ${t.destination}"
      }
      s"$state:\n      ${stateTransitions.mkString("\n      ")}"
    }.mkString("\n    ")

    s"""
       |$header
       |  States: ${automaton.states.mkString(", ")}
       |  Alphabet: ${automaton.alphabet.mkString(", ")}
       |  Transitions:
       |    $transitionsStr
       |  Initial State: ${automaton.initialState}
       |  Final States: ${automaton.finalStates.mkString(", ")}
       |  Computations: ${automaton.computations.map(compFormat).mkString("\n\t\t")}
       |""".stripMargin
  }

  private def compFormat (computation:Computation):String = {
    if (!computation.computed) computation.str
    else {
      val status = if (computation.isAccepted) "accepted" else "NOT accepted"
      s"${computation.str}: $status"
    }
  }

//    def automatonFormat(automaton: Automaton[_, _]): String = {
//    automaton match {
//      case dfa if automaton.automatonType.contains("DFA") => s"""
//                          |DFA:
//                          |  States: ${dfa.states.mkString(", ")}
//                          |  Alphabet: ${dfa.alphabet.mkString(", ")}
//                          |  Transitions:
//                          |    ${dfa.states.map(state => {
//                                val stateTransitions = dfa.transitions.filter(_.source == state)
//                                s"$state:\n      ${stateTransitions.map(t => s"${t.symbol} --> ${t.destination}").mkString("\n      ")}"
//                                }).mkString("\n    ")}
//                          |  Initial State: ${dfa.initialState}
//                          |  Final States: ${dfa.finalStates.mkString(", ")}
//                          |  Computations: ${dfa.computations.map(compFormat).mkString("\n\t\t\t\t")}
//                          |""".stripMargin
//      case nfa if automaton.automatonType.contains("NFA") => s"""
//                          |NFA:
//                          | States: ${nfa.states.mkString(", ")}
//                          | Alphabet: ${nfa.alphabet.mkString(", ")}
//                          |  Transitions:
//                          |   ${nfa.states.map (state => {
//                              val stateTransitions = nfa.transitions.filter (_.source == state)
//                              s"$state:\n      ${stateTransitions.map (t => s"${t.symbol} --> ${t.destination}").mkString ("\n      ")}"
//                              }).mkString ("\n    ")}
//                          | Initial State: ${nfa.initialState}
//                          | Final States: ${nfa.finalStates.mkString(", ")}
//                          | Computations: ${nfa.computations.map(compFormat).mkString("\n\t\t\t\t")}
//                          |""".stripMargin
//      case enfa if automaton.automatonType.contains("ε-NFA") => s"""
//                          |ε-NFA:
//                          | States: ${enfa.states.mkString(", ")}
//                          | Alphabet: ${enfa.alphabet.mkString(", ")}
//                          |  Transitions:
//                          |   ${enfa.states.map (state => {
//                              val stateTransitions = enfa.transitions.filter (_.source == state)
//                              s"$state:\n      ${stateTransitions.map (t => s"${t.symbol} --> ${t.destination}").mkString ("\n      ")}"
//                              }).mkString ("\n    ")}
//                          | Initial State: ${enfa.initialState}
//                          | Final States: ${enfa.finalStates.mkString(", ")}
//                          | Computations: ${enfa.computations.map(compFormat).mkString("\n\t\t\t\t")}
//                          |""".stripMargin
//      case _ => "View error: Unknown type"
//    }
//  }
//
//  private def compFormat(computation: Computation): String = {
//    if (!computation.computed) computation.str
//    else {
//      val status = if (computation.isAccepted) "accepted" else "NOT accepted"
//      s"${computation.str}: $status"
//    }
//  }
//
