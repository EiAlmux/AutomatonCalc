package automaton.model

import scala.language.postfixOps

/**
 * A class representing a Context-Free Grammar (CFG) as a specialized type of Automaton.
 *
 * The class provides functionality to:
 * - Convert the CFG to an equivalent Pushdown Automaton (PDA)
 * - Test whether a string can be derived from the grammar
 * - Validate the structure of the grammar
 *
 * @constructor Creates a new Context-Free Grammar
 * @param variables    The set of non-terminal symbols (variables)
 * @param terminals    The set of terminal symbols
 * @param productions  The set of production rules
 * @param startSymbol  The starting symbol for derivations
 * @param finalStates  Inherited from Automaton but unused in CFG semantics (retained for compatibility)
 * @param computations Sequence of computations (derivations) performed by the grammar
 */
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

  /**
   * Returns all production rules as transitions (inherited from Automaton)
   *
   * @return The set of all production rules
   */
  override def transitions: Set[CFGProduction] = productions

  /**
   * Creates a new CFG with updated computations
   *
   * @param newComps The new sequence of computations to use
   * @return A new CFG instance with the updated computations
   */
  override def withUpdatedComputations(newComps: Seq[Computation]): CFG =
    this.copy(computations = newComps)

  /**
   * Convert this CFG into an equivalent PDA (empty-stack acceptance).
   *
   * @return A PDA that recognizes the same language as this CFG
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

  /**
   * Tests whether a given input string can be derived from this grammar.
   *
   * Performs a depth-limited search (max 1000 steps) to find a derivation.
   * Provides detailed tracing of the derivation attempts.
   *
   * @param input The string to test for membership in the language
   * @return A tuple containing:
   *         - Boolean indicating whether the string can be derived
   *         - String containing the detailed derivation trace
   */
  override def testString(input: String): (Boolean, String) = {
    val trace = new StringBuilder
    val nonTerminals = variables.map(_.label)

    def derive(
                current: List[String],
                steps: List[String],
                depth: Int = 0
              ): Option[List[String]] = {
      if (depth > 1000) return None
      val stepFmt = f"Step $depth%2d: ${current.mkString(" ")}%-50s"
      trace.append(stepFmt)
      if (steps.nonEmpty) trace.append(f" Applied: ${steps.lastOption.getOrElse("")}%-10s")
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
          val (prefix, nt :: suffix) = current.splitAt(idx): @unchecked
          productions.filter(_.lhs.label == nt).view
            .map { prod =>
              val rhsSymbols = prod.rhs.filter(_ != "ε")
              val nextSeq = prefix ++ rhsSymbols ++ suffix
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

  /**
   * For compatibility with trait Automaton
   */
  override def states: Set[State] = variables

  /**
   * For compatibility with trait Automaton
   */
  override def alphabet: Set[String] = terminals

  /**
   * For compatibility with trait Automaton
   */
  override def initialState: State = startSymbol


  validate()

  /**
   * Validates the structure of the CFG.
   *
   * Checks that:
   * - There is at least one variable
   * - Terminals set is not empty
   * - Start symbol is in the variables set
   * - All productions are valid
   *
   * @throws IllegalArgumentException if any validation fails
   */
  override protected def validate(): Unit = {
    require(variables.nonEmpty, "Automaton must have at least one variable")
    require(terminals.nonEmpty, "Terminals must not be empty")
    require(variables.contains(startSymbol), "Start symbol must be in variables")
    validateTransitions()
  }

  /**
   * Validates all production rules in the grammar.
   *
   * Checks that:
   * - Each production's LHS is a single non-terminal
   * - Each symbol in RHS is either a terminal, non-terminal, or ε
   *
   * @throws IllegalArgumentException if any production is invalid
   */
  private def validateTransitions(): Unit = {
    productions.foreach { prod =>
      require(
        variables.contains(prod.lhs),
        s"Production $prod has invalid LHS: must be a single non-terminal"
      )
      prod.rhs.foreach { symbol =>
        val isTerminal = terminals.contains(symbol)
        val isNonTerminal = variables.exists(_.label == symbol)
        val isEpsilon = symbol == "ε"
        require(
          isTerminal || isNonTerminal || isEpsilon,
          s"Production $prod has invalid symbol '$symbol': must be terminal or non-terminal"
        )
      }
    }
  }
}
