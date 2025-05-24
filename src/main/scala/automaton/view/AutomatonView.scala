package automaton.view

import automaton.model.{Automaton, CFG, CFGProduction, Computation, DFA, ENFA, NFA, PDA, PDATransition, State}

object AutomatonView {

  def automatonFormat(automaton: Automaton[_, _]): String = {
    val header = automaton match {
      case _: DFA => "DFA:"
      case _: NFA => "NFA:"
      case _: ENFA => "ε-NFA:"
      case _: PDA => "PDA: "
      case _: CFG => "CFG:"
      case _ => return "View error: Unknown automaton type"
    }

    automaton match {
      case cfg: CFG => cfgFormat(cfg)
      case _ => defaultAutomatonFormat(automaton, header)
    }
  }

  private def cfgFormat(cfg: CFG): String = {
    s"""
       |CFG:
       |  Variables: ${cfg.variables.mkString(", ")}
       |  Terminals: ${cfg.terminals.mkString(", ")}
       |  Start Symbol: ${cfg.startSymbol}
       |  Productions:
       |    ${formatCFGProductions(cfg.productions, cfg.startSymbol)}
       |  Derivations:${if (cfg.computations.isEmpty) " None" else ""}
       |${cfg.computations.map(c => s"    ${compFormat(c)}").mkString("\n")}
       |${formatTracesSection(cfg.computations)}
       |""".stripMargin
  }

  private def formatCFGProductions(productions: Set[CFGProduction],
                                  startSymbol: State): String = {
    val allLhs = productions.toList.map(_.lhs).distinct
    val lhsSymbols = startSymbol +: allLhs.filterNot(_ == startSymbol)

    lhsSymbols.map { lhs =>
      val prodsForLHS = productions.filter(_.lhs == lhs).toList

      val rhsFormatted = prodsForLHS.map { p =>
        if (p.rhs.isEmpty || p.rhs.mkString == "EPSILON") "ε"
        else p.rhs.mkString(" ")
      }

      s"$lhs → ${rhsFormatted.mkString(" | ")}"
    }.mkString("\n    ")
  }


  private def defaultAutomatonFormat(automaton: Automaton[_, _], header: String): String = {
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

    case dfa: DFA  =>
      dfa.states.map { state =>
        val stateTransitions = dfa.transitions.collect {
          case t if t.source == state => s"${t.symbol} --> ${t.destination}"
        }
        s"$state:\n      ${stateTransitions.mkString("\n      ")}"
      }.mkString("\n    ")
    case nfa: NFA =>
      nfa.states.map { state =>
        val stateTransitions = nfa.transitions.collect {
          case t if t.source == state => s"${t.symbol} --> ${t.destination}"
        }
        s"$state:\n      ${stateTransitions.mkString("\n      ")}"
      }.mkString("\n    ")
    case enfa: ENFA =>
      enfa.states.map { state =>
        val stateTransitions = enfa.transitions.collect {
          case t if t.source == state => s"${t.symbol} --> ${t.destination}"
        }
        s"$state:\n      ${stateTransitions.mkString("\n      ")}"
      }.mkString("\n    ")
    case _ => ""
  }

  private def compFormat(computation: Computation): String = {
    if (!computation.computed) computation.str
    else {
      val status = if (computation.isAccepted) "ACCEPTED" else "REJECTED"
      s"${computation.str}: $status"
    }
  }
}
