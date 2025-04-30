package automaton

import automaton.controller.MainAutomaton
import automaton.model.Automaton
import automaton.view.AutomatonView

/*
TODO:
Expand pda
TestString in PDA

*/

object CLIMain:
  val DEBUG = 0

  def main(args: Array[String]): Unit = {
    //val filePath = "src/DFA.txt"
    //val filePath = "src/NFA.txt"
    //val filePath = "src/eNFA.txt"
    val filePath = "src/PDA.txt"

    val automaton = MainAutomaton.processAutomaton(filePath)

    if (DEBUG == 1) {
      automaton match {
        case Some(auto) =>
          println(AutomatonView.automatonFormat(auto))
          println("\n\nProcessing input...\n")
        case None =>
          println("Failed to process automaton")
          return
      }
    }

    val testedAutomaton = automaton.flatMap { auto =>
      try {
        Some(auto.testAutomaton())
      } catch {
        case e:Exception =>
          println(s"Error testing automaton: ${e.getMessage}")
          None
      }
    }
    
    testedAutomaton match {
      case Some(tested: Automaton[_, _]) => println(AutomatonView.automatonFormat(tested))
      case Some(_) => println("Error: Test result is not an Automaton")
      case None => println("Failed to test automaton")
    }
  }



