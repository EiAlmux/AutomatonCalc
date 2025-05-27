package automaton.view

import automaton.model.{CFG, CFGProduction, State}
import automaton.view.ComputationView.{compFormat, formatTracesSection}

object cfgView {

  def cfgFormat(cfg: CFG): String = {
    s"""
       |CFG:
       |  Variables: ${cfg.variables.toList.sortBy(_.toString).mkString(", ")}
       |  Terminals: ${cfg.terminals.toList.sortBy(identity).mkString(", ")}
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

    lhsSymbols.sortBy(_.toString).map { lhs =>
      val prodsForLHS = productions.filter(_.lhs == lhs).toList

      val rhsFormatted = prodsForLHS.map { p =>
        if (p.rhs.isEmpty || p.rhs.mkString == "EPSILON") "ε"
        else p.rhs.mkString(" ")
      }

      s"$lhs → ${rhsFormatted.mkString(" | ")}"
    }.mkString("\n    ")
  }


}
