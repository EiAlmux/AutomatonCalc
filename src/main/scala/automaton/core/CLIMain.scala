package automaton.core

import automaton.utils.MainAutomaton

object CLIMain:
  def main(args: Array[String]): Unit = {

    val filePath = "src/exampleDFA.txt"
    val dfa = MainAutomaton.processDFA(filePath)
    println(dfa.toString)
    println("\n\nProcessing input...\n")

    val testedDFA = dfa.map(_.testAutomaton())
    println(testedDFA.toString)
  }


