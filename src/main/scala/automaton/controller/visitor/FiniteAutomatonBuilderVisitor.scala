package automaton.controller.visitor


import automaton.model.*
import automaton.antlr4.*
import automaton.controller.builder.AutomatonComponents
import org.antlr.v4.runtime.tree.*

import scala.jdk.CollectionConverters.*

class FiniteAutomatonBuilderVisitor extends FiniteAutomatonBaseVisitor[AutomatonComponents] {

  private val requiredSections = Set("states", "alphabet", "transitions", "initialState")
  private var seenSections = Set.empty[String]
  private var automatonType: Option[String] = None

  override def visitAutomaton(ctx: FiniteAutomatonParser.AutomatonContext): AutomatonComponents = {
    seenSections = Set.empty
    val initialType = Some(ctx.automatonType().getText)
    val sections = ctx.section().asScala.map(visit)
    val result = sections.foldLeft(AutomatonComponents(automatonType = initialType))(_ merge _)
    //DEBUG
    //println(result)

    validateRequiredSections()
    result
  }

  private def validateRequiredSections(): Unit = {
    val missing = requiredSections.diff(seenSections)
    if (missing.nonEmpty)
      throw new IllegalArgumentException(
        s"Missing required sections: ${missing.mkString(", ")}"
      )
  }

  override def visitStates(ctx: FiniteAutomatonParser.StatesContext): AutomatonComponents = {
    seenSections += "states"
    val stateNames = ctx.SYMBOL().asScala.map(_.getText)
    if (stateNames.isEmpty) throw new IllegalArgumentException("At least one state is required")
    AutomatonComponents(states = stateNames.map(State.apply).toSet)
  }

  override def visitAlphabet(ctx: FiniteAutomatonParser.AlphabetContext): AutomatonComponents = {
    seenSections += "alphabet"
    val symbols = ctx.SYMBOL().asScala.map(_.getText)
    if (symbols.isEmpty) throw new IllegalArgumentException("Alphabet cannot be empty")
    AutomatonComponents(alphabet = symbols.toSet)
  }

  override def visitTransitions(ctx: FiniteAutomatonParser.TransitionsContext): AutomatonComponents = {
    seenSections += "transitions"
    val transitions = ctx.transition().asScala.flatMap { t =>
      val from = State(t.SYMBOL.getText)
      val symbol = t.inputSymbol().getText

      t.transitionTarget() match {
        // Single state
        case single: FiniteAutomatonParser.SingleStateContext =>
          Set(Transition(from, symbol, State(single.SYMBOL.getText)))

        // State set
        case set: FiniteAutomatonParser.StateSetContext =>
          set.SYMBOL().asScala.map(s =>
            Transition(from, symbol, State(s.getText))
          ).toSet
      }
    }.toSet

    AutomatonComponents(transitions = transitions.toSet)
  }

  override def visitInitialState(ctx: FiniteAutomatonParser.InitialStateContext): AutomatonComponents = {
    seenSections += "initialState"
    val symbol = ctx.SYMBOL().getText
    if (symbol.isEmpty) throw new IllegalArgumentException("The initial state is required")
    AutomatonComponents(initialState = Some(State(symbol)))
  }

  override def visitFinalStates(ctx: FiniteAutomatonParser.FinalStatesContext): AutomatonComponents = {
    seenSections += "finalStates"
    val symbols = ctx.SYMBOL().asScala.map(_.getText)
    AutomatonComponents(finalStates = symbols.map(State.apply).toSet)
  }

  override def visitComputations(ctx: FiniteAutomatonParser.ComputationsContext): AutomatonComponents = {
    seenSections += "computations"
    val strings = ctx.SYMBOL().asScala.map(_.getText)
    AutomatonComponents(computations = strings.map(str => Computation(str)).toSeq)
  }

  override def visitSection(ctx: FiniteAutomatonParser.SectionContext): AutomatonComponents = {
    visitChildren(ctx)
  }

  override def defaultResult: AutomatonComponents = AutomatonComponents()

  override def aggregateResult(aggregate: AutomatonComponents, nextResult: AutomatonComponents): AutomatonComponents = {
    aggregate.merge(nextResult)
  }
}