package automaton.model

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
                override val computations: Seq[Computation] //Here are derivations
              ) extends Automaton[CFGProduction, CFG] {

  override def transitions: Set[CFGProduction] = productions

  override def withUpdatedComputations(newComps: Seq[Computation]): CFG =
    this.copy(computations = newComps)

  override def testString(input: String): (Boolean, String) = {
    val trace = new StringBuilder
    trace.append(s"Start symbol: ${startSymbol.label}\n\n")

    val nonTerminals = variables.map(_.label)

    /**
     * Try to derive `input` from `current`.
     * `steps` is the path taken so far.
     * Returns the successful list of steps, or None.
     */
    def derive(
                current: List[String],
                steps: List[String],
                depth: Int = 0
              ): Option[List[String]] = {
      if (depth > 1000) return None  // making sure it doesn't enter into infinite recursive

      if (current.mkString == input && current.forall(s => !nonTerminals.contains(s)))
        return Some(steps)

      current.indexWhere(nonTerminals.contains) match {
        case -1 => None
        case idx =>
          val (prefix, nt :: suffix) = current.splitAt(idx): @unchecked
          productions
            .filter(_.lhs.label == nt)
            .iterator
            .flatMap { prod =>
              val rhsSymbols = prod.rhs.filter(_ != "ε")
              val nextSeq     = prefix ++ rhsSymbols ++ suffix
              val stepDesc    =
                s"${nextSeq.mkString(" ")} \t\tapplied ${prod.lhs.label} -> ${prod.rhs.mkString(" ")}"
              derive(nextSeq, steps :+ stepDesc, depth + 1)
            }
            .nextOption()
      }
    }

    val startStep = s"${startSymbol.label}"
    derive(List(startSymbol.label), List(startStep)) match {
      case Some(successSteps) =>
        successSteps.zipWithIndex.foreach { case (step, i) =>
          trace.append(f"Step $i%2d: $step\n")
        }
        trace.append(s"\nSuccessfully derived '$input' in ${successSteps.size - 1} steps.\n")
        (true, trace.toString)

      case None =>
        trace.append(s"Failed to derive '$input'\n")
        (false, trace.toString)
    }
  }


  override protected def validate(): Unit = {
    require(states.nonEmpty, "Automaton must have at least one variable")
    require(alphabet.nonEmpty, "Terminals must not be empty")
    require(states.contains(initialState), "Initial symbol must be in variables")
    validateTransitions()
  }


  validate()

  //Override names from automaton
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




