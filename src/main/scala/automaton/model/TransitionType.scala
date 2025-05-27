package automaton.model

sealed trait Rule

sealed trait TransitionType extends Rule {
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
                          destination: State,
                          symbolsToPush: List[String]
                        ) extends TransitionType


case class CFGProduction(lhs: State, rhs: Seq[String]) extends Rule

case class TMTransition(
                         source: State,
                         symbol: String, //symbol to read, named as such for compatibility 
                         destination: State,
                         symbolWrite: String,
                         direction: Direction
                       ) extends TransitionType

