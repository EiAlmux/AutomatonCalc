package automaton.controller.visitor

import automaton.antlr4.*
import automaton.controller.builder.DFAComponents
import automaton.model.*
import org.antlr.v4.runtime.tree.*

import scala.jdk.CollectionConverters.*

/*
OLD VERSION FOR DFA ONLY

!!!
USELESS
!!!


class DFABuilderVisitor extends DFABaseVisitor[DFAComponents] {

  private val requiredSections = Set("states", "alphabet", "transitions", "initialState")

  //Tracks sections to determine if a required section has at least one element
  private var seenSections = Set.empty[String]

  override def visitDfa(ctx: DFAParser.DfaContext): DFAComponents = {
    seenSections = Set.empty //Reset, should never be used
    val sections = ctx.section().asScala.map(visit)
    val result = sections.foldLeft(DFAComponents())(_ merge _)

    validateRequiredSections()
    result
  }

  private def validateRequiredSections(): Unit =
    val missing = requiredSections.diff(seenSections)
    if (missing.nonEmpty)
      throw new IllegalArgumentException(
        s"Missing required sections: ${missing.mkString(", ")}"
      )

  override def visitStates(ctx: DFAParser.StatesContext): DFAComponents =
    seenSections += "states"
    val stateNames = ctx.SYMBOL().asScala.map(_.getText)
    if (stateNames.isEmpty) throw new IllegalArgumentException("At least one state is required")
    DFAComponents(states = stateNames.map(State.apply).toSet)

  override def visitAlphabet(ctx: DFAParser.AlphabetContext): DFAComponents =
    seenSections += "alphabet"
    val symbols = ctx.SYMBOL().asScala.map(_.getText)
    if (symbols.isEmpty) throw new IllegalArgumentException("Alphabet cannot be empty")
    DFAComponents(alphabet = symbols.toSet)

  override def visitTransitions(ctx: DFAParser.TransitionsContext): DFAComponents =
    seenSections += "transitions"
    val transitions = ctx.transition().asScala.map { t =>
      val from = t.SYMBOL(0).getText
      val symbol = t.SYMBOL(1).getText
      val to = t.SYMBOL(2).getText
      Transition(State(from), symbol, State(to))
    }
    DFAComponents(transitions = transitions.toSet)


  override def visitInitialState(ctx: DFAParser.InitialStateContext): DFAComponents =
    seenSections += "initialState"
    val symbol = ctx.SYMBOL().getText
    if (symbol.isEmpty) throw new IllegalArgumentException("The initial state is required")
    DFAComponents(initialState = Some(State(symbol)))

  override def visitFinalStates(ctx: DFAParser.FinalStatesContext): DFAComponents =
    seenSections += "finalState"
    val symbols = ctx.SYMBOL().asScala.map(_.getText)
    DFAComponents(finalStates = symbols.map(State.apply).toSet)


  override def visitComputations(ctx: DFAParser.ComputationsContext): DFAComponents =
    seenSections += "computations"
    val strings = ctx.SYMBOL().asScala.map(_.getText)
    DFAComponents(computations = strings.map(str => Computation(str)).toSeq)

  // Ensure other section types are handled by visiting their children
  override def visitSection(ctx: DFAParser.SectionContext): DFAComponents = {
    visitChildren(ctx)
  }

  override def defaultResult: DFAComponents = DFAComponents()

  override def aggregateResult(aggregate: DFAComponents, nextResult: DFAComponents): DFAComponents = {
    aggregate.merge(nextResult)
  }
}
*/