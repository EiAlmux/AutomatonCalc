package automaton.model

import automaton.model.PDA
import automaton.model.PDATransition
import automaton.model.State

import scala.language.postfixOps

case class CFG(
                variables: Set[State],
                terminals: Set[String],
                productions: Set[CFGProduction],
                startSymbol: State,

                /**
                 * Inherited from Automaton, but unused in CFG semantics.
                 * Retained for compatibility.
                 */
                override val finalStates: Set[State] = Set.empty,
                override val computations: Seq[Computation] // Here are derivations
              ) extends Automaton[CFGProduction, CFG] {

  override def transitions: Set[CFGProduction] = productions

  override def withUpdatedComputations(newComps: Seq[Computation]): CFG =
    this.copy(computations = newComps)

  /**
   * Convert this CFG into an equivalent PDA (empty-stack acceptance).
   * Construction:
   *  - Single state q
   *  - For each production A -> X1 X2 ... Xn, add transition (q, ε, A) -> (q, Xn ... X2 X1)
   *  - For each terminal a, add transition (q, a, a) -> (q, ε)
   *  - Initial stack symbol is the start symbol of the CFG
   *  - Accept by empty stack (no final states needed)
   */
  def toPDA: PDA = {
    // Single control state
    val q = State("q")
    // PDA components
    val states = Set(q)
    val alphabet = terminals
    val stackAlphabet: Set[String] = variables.map(_.label) ++ terminals
    val initialState = q
    val initialStack = startSymbol.label
    val finalStates: Set[State] = Set.empty

    // Build production-pushing transitions
    val prodTransitions: Set[PDATransition] = productions.map { prod =>
      val lhs = prod.lhs.label
      val rhsSymbols = prod.rhs.filter(_ != "ε")
      val toPush = rhsSymbols.reverse.toList
      PDATransition(
        source = q,
        symbol = "ε",
        stackSymbolToPop = lhs,
        symbolsToPush = toPush,
        destination = q
      )
    }

    // Build terminal-matching transitions
    val matchTransitions: Set[PDATransition] = alphabet.map { a =>
      PDATransition(
        source = q,
        symbol = a,
        stackSymbolToPop = a,
        symbolsToPush = Nil,
        destination = q
      )
    }

    val transitions = prodTransitions ++ matchTransitions
    val computations: Seq[Computation] = Seq.empty

    PDA(
      states = states,
      alphabet = alphabet,
      stackAlphabet = stackAlphabet,
      transitions = transitions,
      initialState = initialState,
      initialStack = initialStack,
      finalStates = finalStates,
      computations = computations
    )
  }

  override def testString(input: String): (Boolean, String) = {
    val trace = new StringBuilder
    val nonTerminals = variables.map(_.label)

    def derive(
                current: List[String],
                steps: List[String],
                depth: Int = 0
              ): Option[List[String]] = {
      if (depth > 1000) return None
      val stepFmt = f"Step $depth%2d: ${current.mkString(" ")}%-30s"
      trace.append(stepFmt)
      if (steps.nonEmpty) trace.append(f" [Applied: ${steps.lastOption.getOrElse("")}%-25s]")
      trace.append("\n")

      val terminalsSoFar = current.filterNot(nonTerminals.contains).mkString
      if (!input.startsWith(terminalsSoFar)) {
        trace.append(f"${" " * 10}Pruned (prefix mismatch): '$terminalsSoFar' not prefix of '$input'\n\n")
        return None
      }

      if (current.mkString == input && current.forall(s => !nonTerminals.contains(s)))
        return Some(steps)

      current.indexWhere(nonTerminals.contains) match {
        case -1 =>
          trace.append(f"${" " * 10}Failed (no non-terminals left, but '${current.mkString}' != '$input')\n\n")
          None
        case idx =>
          val (prefix, nt :: suffix) = current.splitAt(idx)
          productions.filter(_.lhs.label == nt).view
            .map { prod =>
              val rhsSyms  = prod.rhs.filter(_ != "ε")
              val nextSeq  = prefix ++ rhsSyms ++ suffix
              val stepDesc = s"${prod.lhs.label} -> ${prod.rhs.mkString(" ")}"
              derive(nextSeq, steps :+ stepDesc, depth + 1)
            }
            .collectFirst { case Some(res) => res }
      }
    }

    val startStep = s"Initial: ${startSymbol.label}"
    derive(List(startSymbol.label), List(startStep)) match {
      case Some(successSteps) =>
        trace.append(s"\nSuccessfully derived '$input'.\n")
        (true, trace.toString)
      case None =>
        trace.append(s"\nFailed to derive '$input'.\n")
        (false, trace.toString)
    }
  }

  override protected def validate(): Unit = {
    require(variables.nonEmpty, "Automaton must have at least one variable")
    require(terminals.nonEmpty, "Terminals must not be empty")
    require(variables.contains(startSymbol), "Start symbol must be in variables")
    validateTransitions()
  }

  validate()

  override def states: Set[State] = variables
  override def alphabet: Set[String] = terminals
  override def initialState: State = startSymbol

  private def validateTransitions(): Unit = {
    productions.foreach { prod =>
      require(
        variables.contains(prod.lhs),
        s"Production $prod has invalid LHS: must be a single non-terminal"
      )
      prod.rhs.foreach { symbol =>
        val isTerminal    = terminals.contains(symbol)
        val isNonTerminal = variables.exists(_.label == symbol)
        val isEpsilon     = symbol == "ε"
        require(
          isTerminal || isNonTerminal || isEpsilon,
          s"Production $prod has invalid symbol '$symbol': must be terminal or non-terminal"
        )
      }
    }
  }
}
