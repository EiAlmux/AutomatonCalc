package automaton

import automaton.*
import scala.collection.mutable

object Main :

  val q0: State = State("q0", false)
  val q1: State = State("q1", true)
  val transition01 = Transition(q0, q1, "1")
  val automaton: Automaton = Automaton(Seq(q0, q1), Seq("0", "1"), Seq(transition01), q0, q1)

  var positions: mutable.Map[State, (Int, Int)] = mutable.Map(q0 -> (100, 100), q1 -> (250, 100))



