package automaton.controller.visitor

import automaton.CLIMain
import automaton.antrl4.{TuringMachineBaseVisitor, TuringMachineParser}
import automaton.controller.builder.TMComponents
import automaton.model.{Computation, Direction, State, TMTransition}

import scala.jdk.CollectionConverters.*

class TMBuilderVisitor extends TuringMachineBaseVisitor [TMComponents] {

  private val requiredSections = Set("states", "alphabet", "tapeAlphabet", "transitions", "initialState", "blankSymbol")
  private var seenSections = Set.empty [String]
  private var automatonType: Option [String] = None


  override def visitAutomaton (ctx: TuringMachineParser.AutomatonContext): TMComponents = {
    seenSections = Set.empty
    automatonType = Some(ctx.automatonType().getText.toLowerCase)
    val sections = ctx.section().asScala.map(visit)
    val result = sections.foldLeft(TMComponents())(_ merge _)

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

  override def visitStates (ctx: TuringMachineParser.StatesContext): TMComponents = {
    seenSections += "states"
    val stateNames = ctx.SYMBOL().asScala.map(_.getText)
    if (stateNames.isEmpty) throw new IllegalArgumentException("At least one state is required")
    TMComponents(states = stateNames.map(State.apply).toSet)
  }

  override def visitInputAlphabet (ctx: TuringMachineParser.InputAlphabetContext): TMComponents = {
    seenSections += "alphabet"
    val symbols = ctx.SYMBOL().asScala.map(_.getText)
    if (symbols.isEmpty) throw new IllegalArgumentException("Input Alphabet cannot be empty")
    TMComponents(alphabet = symbols.toSet)
  }

  override def visitTapeAlphabet(ctx: TuringMachineParser.TapeAlphabetContext): TMComponents = {
    seenSections += "tapeAlphabet"
    val symbols = ctx.SYMBOL().asScala.map(_.getText)
    if (symbols.isEmpty) throw new IllegalArgumentException("Tape Alphabet cannot be empty")
    TMComponents(tapeAlphabet = symbols.toSet)
  }

  override def visitTransitions(ctx: TuringMachineParser.TransitionsContext): TMComponents = {
    seenSections += "transitions"
    val transitions = ctx.transition().asScala.map { t =>
      val source = State(t.source().getText)
      val readSymbol = t.readSymbol().getText

      val destination = State(t.destination().getText)
      val writeSymbol = t.writeSymbol().getText

      val direction = Direction(t.direction().getText)

      TMTransition(
        source = source,
        symbol = readSymbol,
        destination = destination,
        symbolWrite = writeSymbol,
        direction = direction
      )
    }.toSet

    TMComponents(transitions = transitions)
  }

  override def visitInitialState (ctx: TuringMachineParser.InitialStateContext): TMComponents = {
    seenSections += "initialState"
    val symbol = ctx.SYMBOL().getText
    if (symbol.isEmpty) throw new IllegalArgumentException("The initial state is required")
    TMComponents(initialState = Some(State(symbol)))
  }

  override def visitBlankSymbol(ctx: TuringMachineParser.BlankSymbolContext): TMComponents = {
    seenSections += "blankSymbol"
    val symbol = ctx.SYMBOL().getText
    if (symbol.isEmpty) throw new IllegalArgumentException("Blank symbol is required")
    TMComponents(blankSymbol = Some(symbol))
  }

  override def visitFinalStates (ctx: TuringMachineParser.FinalStatesContext): TMComponents = {
    seenSections += "finalStates"
    val symbols = ctx.SYMBOL().asScala.map(_.getText)
    TMComponents(finalStates = symbols.map(State.apply).toSet)
  }

  override def visitComputations (ctx: TuringMachineParser.ComputationsContext): TMComponents = {
    seenSections += "computations"
    val strings = ctx.SYMBOL().asScala.map(_.getText)
    TMComponents(computations = strings.map(str => Computation(str)).toSeq)
  }

  override def defaultResult: TMComponents = TMComponents()

  override def aggregateResult(aggregate: TMComponents, nextResult: TMComponents): TMComponents = {
    aggregate.merge(nextResult)
  }


}
