package automaton.model

sealed trait TransitionType {
  def source: State
  def symbol: String
  def destination: State
}

case class Transition(
                       source: State,
                       symbol: String,
                       destination: State
                     ) extends TransitionType

case class PDATransition(
                          source: State,
                          symbol: String,
                          stackSymbolToPop: String,
                          symbolsToPush: String,
                          destination: State
                        ) extends TransitionType

