package automaton

import automaton.controller.MainAutomaton
import automaton.model.Automaton
import automaton.view.AutomatonView

import scala.util.{Failure, Success, Try}

/*
TODO:

Use tail recursive when possible and avoid var

Improve line alignment in view: ...new step.... applied transition

Reduce bloat and reuse code, such as in TransitionView

*/

object CLIMain:
  val DEBUG = 0

  //Standard arguments: "src/DFA.txt" "src/NFA.txt" "src/ENFA.txt" "src/PDA.txt"
  // "src/CFG.txt" "src/TuringMachine.txt" "src/MultipleAutomata.txt"
  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      println("Error: No input file specified.")
      sys.exit(1)
    }

    args.foreach { filePath =>
      println(s"\nProcessing file: $filePath")
      val automataResults: List[Try[Automaton[_, _]]] =
        MainAutomaton.processAutomata(filePath)

      if (automataResults.isEmpty) {
        println("No automata defined in file")
      } else {
        automataResults.zipWithIndex.foreach {
          case (Success(auto), idx) =>
            println(s"\nAutomaton ${idx + 1}:")
            val tested = auto.testAutomaton()
            println(AutomatonView.automatonFormat(tested))

          case (Failure(err), idx) =>
            println(s"Invalid automaton ${idx + 1}: ${err.getMessage}")
        }
      }
    }
  }
  



