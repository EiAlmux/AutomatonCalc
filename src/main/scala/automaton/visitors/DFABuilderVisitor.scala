package automaton.visitors

import antlr4._
import automaton.utils._
import org.antlr.v4.runtime.tree._

import scala.jdk.CollectionConverters._


class DFABuilderVisitor extends DFABaseVisitor[DFAComponents] {

  override def visitDfa(ctx: DFAParser.DfaContext): DFAComponents = {
    val sections = ctx.section().asScala.map(visit)
    sections.foldLeft(DFAComponents())(_ merge _)
  }

  override def visitStates(ctx: DFAParser.StatesContext): DFAComponents = {
    val stateNames = ctx.SYMBOL().asScala.map(_.getText)
    DFAComponents(states = stateNames.map(State).toSeq)
  }

  override def visitAlphabet(ctx: DFAParser.AlphabetContext): DFAComponents = {
    val symbols = ctx.SYMBOL().asScala.map(_.getText)
    DFAComponents(alphabet = symbols.toSeq)
  }

  override def visitTransitions(ctx: DFAParser.TransitionsContext): DFAComponents = {
    val transitions = ctx.transition().asScala.map { t =>
      val from = t.SYMBOL(0).getText
      val symbol = t.SYMBOL(1).getText
      val to = t.SYMBOL(2).getText
      Transition(State(from), symbol, State(to))
    }
    DFAComponents(transitions = transitions.toSeq)
  }

  override def visitInitialState(ctx: DFAParser.InitialStateContext): DFAComponents = {
    val symbol = ctx.SYMBOL().getText
    DFAComponents(initialState = Some(State(symbol)))
  }

  override def visitFinalStates(ctx: DFAParser.FinalStatesContext): DFAComponents = {
    val symbols = ctx.SYMBOL().asScala.map(_.getText)
    DFAComponents(finalStates = symbols.map(State).toSeq)
  }

  override def visitComputations(ctx: DFAParser.ComputationsContext): DFAComponents =
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