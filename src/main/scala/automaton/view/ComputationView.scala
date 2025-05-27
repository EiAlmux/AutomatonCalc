package automaton.view

import automaton.model.Computation

object ComputationView {

  def formatTracesSection(computations: Seq[Computation]): String = {
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

  def compFormat(computation: Computation): String = {
    if (!computation.computed) computation.str
    else {
      val status = if (computation.isAccepted) "ACCEPTED" else "REJECTED"
      s"${computation.str}: $status"
    }
  }

}
