package automaton.view

import automaton.model.{Automaton, Computation, DFA, NFA, PDA, PDATransition, ENFA}

object AutomatonView {

  def automatonFormat(automaton: Automaton[_, _]): String = {
    val header = automaton match {
      case _: DFA => "DFA:"
      case _: NFA => "NFA:"
      case _: ENFA => "ε-NFA:"
      case _: PDA => "PDA:"
      case _ => return "View error: Unknown automaton type"
    }

    s"""
       |$header
       |  States: ${automaton.states.mkString(", ")}
       |  Alphabet: ${automaton.alphabet.mkString(", ")}${formatStackAlphabet(automaton)}
       |  Transitions:
       |    ${transitionsStr(automaton)}
       |  Initial State: ${automaton.initialState}${formatInitialStack(automaton)}
       |  Final States: ${automaton.finalStates.mkString(", ")}
       |  Computations:\n\t\t${automaton.computations.map(compFormat).mkString("\n\t\t")}
       |${formatTracesSection(automaton.computations)}
       |""".stripMargin
  }

  private def formatStackAlphabet(automaton: Automaton[_, _]): String = automaton match {
    case pda: PDA => "\n|  Stack Alphabet: " + pda.stackAlphabet.mkString(", ")
    case _ => ""
  }

  private def formatInitialStack(automaton: Automaton[_, _]): String = automaton match {
    case pda: PDA => "\n|  Initial Stack: " + pda.initialStack
    case _ => ""
  }

  private def formatTracesSection(computations: Seq[Computation]): String = {
    val traces = computations.filter(_.trace.nonEmpty)
    if (traces.isEmpty) "" else {
      s"""
         |  Computation Traces:
         |${traces.map(formatTrace).mkString("\n\n")}
         |""".stripMargin
    }
  }

  private def formatTrace(computation: Computation): String = {
    val lines = computation.trace.split("\n")
    val formattedLines = lines.map {
      case line if line.trim.startsWith("→") || line.trim.startsWith("Final state:") => s"    $line"
      case line => s"  \t$line"
    }
    s"    ${computation.str}:\n${formattedLines.mkString("\n")}"
  }

  private def transitionsStr(automaton: Automaton[_, _]): String = automaton match {
    case pda: PDA =>
      pda.states.map { state =>
        val stateTransitions = pda.transitions.collect {
          case t if t.source == state =>
            val pushStr = if (t.symbolsToPush.isEmpty) "ε" else t.symbolsToPush.mkString
            s"${t.source}, ${t.symbol}, ${t.stackSymbolToPop} -> ${t.destination}, $pushStr"
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

  private def compFormat(computation: Computation): String = {
    if (!computation.computed) computation.str
    else {
      val status = if (computation.isAccepted) "ACCEPTED" else "REJECTED"
      s"${computation.str}: $status"
    }
  }
}
