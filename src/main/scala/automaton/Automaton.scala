package automaton

case class Transition(from: String, symbol: Char, to: String)

class Automaton(
                 val states: Set[String],
                 val alphabet: Set[Char],
                 val transitions: Set[Transition],
                 val initialState: String,
                 val finalStates: Set[String]
               ) {
  def testString(input: String): Boolean = {
    // Implement automaton logic here
    false // Placeholder
  }
}