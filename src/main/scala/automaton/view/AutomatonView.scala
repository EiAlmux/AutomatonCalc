package automaton.view

import automaton.model.*
import automaton.view.ComputationView.{formatTracesSection, compFormat}
import automaton.view.TransitionView.transitionsStr
import automaton.view.cfgView.cfgFormat

object AutomatonView {

  def automatonFormat(automaton: Automaton[_, _]): String = {
    val header = automaton match {
      case _: DFA => "DFA: "
      case _: NFA => "NFA: "
      case _: ENFA => "ε-NFA: "
      case _: PDA => "PDA: "
      case _: CFG => "CFG: "
      case _: TuringMachine => "Turing Machine: "
      case _ => return "View error: Unknown automaton type"
    }

    automaton match {
      case cfg: CFG => cfgFormat(cfg)
      case _ => defaultAutomatonFormat(automaton, header)
    }
  }

  private def defaultAutomatonFormat(automaton: Automaton[_, _], header: String): String = {
    s"""
       |$header
       |  States: ${automaton.states.toList.sortBy(_.toString).mkString(", ")}
       |  Alphabet: ${automaton.alphabet.toList.sortBy(identity).mkString(", ")}${formatSecondAlphabet(automaton)}
       |  Transitions:
       |    ${transitionsStr(automaton)}
       |  Initial State: ${automaton.initialState}${formatInitialStackOrSymbol(automaton)}
       |  Final States: ${automaton.finalStates.toList.sortBy(_.toString).mkString(", ")}
       |  Computations:\n\t\t${automaton.computations.map(compFormat).mkString("\n\t\t")}
       |${formatTracesSection(automaton.computations)}
       |""".stripMargin
  }

  private def formatSecondAlphabet(automaton: Automaton[_, _]): String = automaton match {
    case pda: PDA => "\n|  Stack Alphabet: " + pda.stackAlphabet.toList.sortBy(identity).mkString(", ")
    case tm: TuringMachine => "\n|  Tape Alphabet: " + tm.tapeAlphabet.toList.sortBy(identity).mkString(", ")
    case _ => ""
  }

  private def formatInitialStackOrSymbol(automaton: Automaton[_, _]): String = automaton match {
    case pda: PDA => "\n|  Initial Stack: " + pda.initialStack
    case tm: TuringMachine => "\n|  Blank Symbol: " + tm.blankSymbol
    case _ => ""
  }

}
