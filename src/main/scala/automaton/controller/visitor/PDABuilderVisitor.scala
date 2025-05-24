package automaton.controller.visitor

import automaton.CLIMain
import automaton.antrl4.*
import automaton.controller.builder.PDAComponents
import automaton.model.*
import org.antlr.v4.runtime.tree.*

import scala.jdk.CollectionConverters.*

class PDABuilderVisitor extends PDABaseVisitor [PDAComponents] {

  private val requiredSections = Set("states", "alphabet", "stackAlphabet", "transitions", "initialState", "initialStack")
  private var seenSections = Set.empty [String]
  private var automatonType: Option [String] = None

  override def visitAutomaton (ctx: PDAParser.AutomatonContext): PDAComponents = {
    seenSections = Set.empty
    automatonType = Some(ctx.automatonType().getText.toLowerCase)
    val sections = ctx.section().asScala.map(visit)
    val result = sections.foldLeft(PDAComponents())(_ merge _)

    if (CLIMain.DEBUG == 1)
      println(result)

    validateRequiredSections()
    result
  }

  private def validateRequiredSections (): Unit = {
    val missing = requiredSections.diff(seenSections)
    if (missing.nonEmpty)
      throw new IllegalArgumentException(
        s"Missing required sections: ${missing.mkString(", ")}"
        )
  }

  override def visitStates (ctx: PDAParser.StatesContext): PDAComponents = {
    seenSections += "states"
    val stateNames = ctx.SYMBOL().asScala.map(_.getText)
    if (stateNames.isEmpty) throw new IllegalArgumentException("At least one state is required")
    PDAComponents(states = stateNames.map(State.apply).toSet)
  }

  override def visitInputAlphabet (ctx: PDAParser.InputAlphabetContext): PDAComponents = {
    seenSections += "alphabet"
    val symbols = ctx.SYMBOL().asScala.map(_.getText)
    if (symbols.isEmpty) throw new IllegalArgumentException("Alphabet cannot be empty")
    PDAComponents(alphabet = symbols.toSet)
  }

  override def visitStackAlphabet (ctx: PDAParser.StackAlphabetContext): PDAComponents = {
    seenSections += "stackAlphabet"
    val symbols = ctx.SYMBOL().asScala.map(_.getText)
    if (symbols.isEmpty) throw new IllegalArgumentException("Stack Alphabet cannot be empty")
    PDAComponents(_stackAlphabet = symbols.toSet)
  }

  override def visitTransitions(ctx: PDAParser.TransitionsContext): PDAComponents = {
    seenSections += "transitions"
    val transitions = ctx.transition().asScala.map { t =>
      val source = State(t.SYMBOL(0).getText)
      val inputSymbol = t.inputSymbol().getText
      val stackSymbolToPop = t.stackSymbol().getText

      val destination = State(t.SYMBOL(1).getText)
      val symbolsToPush = t.stackOperation().getText match {
        case "ε" => List("ε")
        case symbols => symbols.split("").toList.filter(_ != "")
      }

      PDATransition(
        source = source,
        symbol = inputSymbol,
        stackSymbolToPop = stackSymbolToPop,
        destination = destination,
        symbolsToPush = symbolsToPush
        )
    }.toSet

    PDAComponents(transitions = transitions)
  }

  override def visitInitialState (ctx: PDAParser.InitialStateContext): PDAComponents = {
    seenSections += "initialState"
    val symbol = ctx.SYMBOL().getText
    if (symbol.isEmpty) throw new IllegalArgumentException("The initial state is required")
    PDAComponents(initialState = Some(State(symbol)))
  }

  override def visitInitialStack (ctx: PDAParser.InitialStackContext): PDAComponents = {
    seenSections += "initialStack"
    val symbol = ctx.SYMBOL().getText
    if (symbol.isEmpty) throw new IllegalArgumentException("The initial stack symbol is required")
    PDAComponents(_initialStack = Some(symbol))
  }

  override def visitFinalStates (ctx: PDAParser.FinalStatesContext): PDAComponents = {
    seenSections += "finalStates"
    val symbols = ctx.SYMBOL().asScala.map(_.getText)
    PDAComponents(finalStates = symbols.map(State.apply).toSet)
  }

  override def visitComputations (ctx: PDAParser.ComputationsContext): PDAComponents = {
    seenSections += "computations"
    val strings = ctx.SYMBOL().asScala.map(_.getText)
    PDAComponents(computations = strings.map(str => Computation(str)).toSeq)
  }

  override def defaultResult: PDAComponents = PDAComponents()

  override def aggregateResult (aggregate: PDAComponents, nextResult: PDAComponents): PDAComponents = {
    aggregate.merge(nextResult)
  }
}
