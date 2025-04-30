package automaton.view

import automaton.model.{Automaton, Computation, DFA, NFA, PDA, eNFA}

object AutomatonView {

  def automatonFormat(automaton: Automaton[_, _]): String = {
    val header = automaton match {
      case _: DFA => "DFA:"
      case _: NFA => "NFA:"
      case _: eNFA => "ε-NFA:" 
      case _: PDA => "PDA:"
      case _ => return "View error: Unknown automaton type"
    }

    val transitionsStr = automaton match {
      case pda: PDA =>
        pda.states.map { state =>
          val stateTransitions = pda.transitions.collect {
            case t if t.source == state =>
              s"${t.symbol}, ${t.stackSymbolToPop} → ${t.symbolsToPush} --> ${t.destination}"
          }
          s"$state:\n      ${stateTransitions.mkString("\n      ")}"
        }.mkString("\n    ")
      case _ =>
        automaton.states.map { state =>
          val stateTransitions = automaton.transitions.collect {
            case t if t.source == state => s"${t.symbol} --> ${t.destination}"
          }
          s"$state:\n      ${stateTransitions.mkString("\n      ")}"
        }.mkString("\n    ")
    }

    s"""
       |$header
       |  States: ${automaton.states.mkString(", ")}
       |  Alphabet: ${automaton.alphabet.mkString(", ")}    ${automaton match {
      case pda: PDA => "\n|  Stack Alphabet: " + pda.stackAlphabet.mkString(", ")
      case _ => ""
    }}
       |  Transitions:
       |    $transitionsStr
       |  Initial State: ${automaton.initialState}        ${automaton match {
      case pda: PDA => "\n|  Initial Stack: " + pda.initialStack
      case _ => ""
    }}
       |  Final States: ${automaton.finalStates.mkString(", ")}
       |  Computations: ${automaton.computations.map(compFormat).mkString("\n\t\t")}
       |""".stripMargin
  }

  private def compFormat(computation: Computation): String = {
    if (!computation.computed) computation.str
    else {
      val status = if (computation.isAccepted) "accepted" else "NOT accepted"
      s"${computation.str}: $status"
    }
  }
}