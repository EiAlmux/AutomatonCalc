package automaton

import automaton.controller.MainAutomaton
import automaton.view.AutomatonView.automatonFormat

object CLIMain:
  val DEBUG = 0
  def main(args: Array[String]): Unit = {

    val filePath = "src/eNFA.txt"
    val automaton = MainAutomaton.processAutomaton(filePath)
    if (DEBUG == 1) {
      automaton match {
        case Some(automaton) => println(automatonFormat(automaton))
        case None => println("Failed to process automaton")
      }
      println("\n\nProcessing input...\n")
    }


    val testedAutomaton = automaton.map(_.testAutomaton())

    testedAutomaton match {
      case Some(d) => println(automatonFormat(d))
      case None => println("Failed to process automaton")
    }
  }


