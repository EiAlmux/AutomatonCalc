package automaton.controller.builder

import automaton.model.{CFG, CFGProduction, Computation, State}

case class CFGComponents(
                          variables: Set[State] = Set.empty,
                          terminals: Set[String] = Set.empty,
                          productions: Set[CFGProduction] = Set.empty,
                          startSymbol: Option[State] = None,
                          /**
                           * Inherited from AutomatonComponents, but unused in CFG semantics.
                           * Retained for interface compatibility.
                           */
                          finalStates: Set[State] = Set.empty, //UNUSED
                          computations: Seq[Computation] = Seq.empty // Derivations
                        ) extends AutomatonComponents[CFGProduction, CFG] {

  //Override names from automaton
  override def states: Set[State] = variables
  override def alphabet: Set[String] = terminals
  override def transitions: Set[CFGProduction] = productions
  override def initialState: Option[State] = startSymbol

  def merge(other: AutomatonComponents[CFGProduction, CFG]): CFGComponents = other match {
    case g : CFGComponents =>
      CFGComponents(
        variables = this.states ++ g.states,
        terminals = this.alphabet ++ g.alphabet,
        productions = this.transitions ++ g.transitions,
        startSymbol = this.initialState.orElse(g.initialState),
        computations = this.computations ++ g.computations
      )
    case _ => throw new IllegalArgumentException("Can only merge with DFAComponents")
  }

  def withComputations(newComps: Seq[Computation]): CFGComponents =
    this.copy(computations = newComps)

  def toAutomaton:Either[String, CFG] = startSymbol match {
    case Some(init) =>    Right(CFG(variables, terminals, productions, init, finalStates, computations))
    case None       =>    Left("No start symbol defined in the input")
  }

  def toCFG: CFG = toAutomaton match {
    case Right(cfg) => cfg
    case Left(err) => throw new RuntimeException(err)
  }
}
