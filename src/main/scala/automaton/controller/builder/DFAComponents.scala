package automaton.controller.builder

import automaton.model.{State, Transition, Computation}
import automaton.model.DFA

case class DFAComponents(
                          states: Set[State] = Set.empty,
                          alphabet: Set[String] = Set.empty,
                          transitions: Set[Transition] = Set.empty,
                          initialState: Option[State] = None,
                          finalStates: Set[State] = Set.empty,
                          computations: Seq[Computation] = Seq.empty,
                          automatonType: Option[String] = Some("DFA")
                        ) {
  def merge(other: DFAComponents): DFAComponents = DFAComponents(
    states = this.states ++ other.states,
    alphabet = this.alphabet ++ other.alphabet,
    transitions = this.transitions ++ other.transitions,
    initialState = this.initialState.orElse(other.initialState),
    finalStates = this.finalStates ++ other.finalStates,
    computations = this.computations ++ other.computations
  )

  def withComputations(newComps: Seq[Computation]): DFAComponents =
    this.copy(computations = newComps)

  def addComputation(comp: Computation): DFAComponents =
    this.copy(computations = computations :+ comp)

  def toDFA: DFA = initialState match {
    case Some(init) => DFA(states, alphabet, transitions, init, finalStates, computations, Some("DFA"))
    case None => throw new RuntimeException("No initial state defined in the input.")
  }
}
