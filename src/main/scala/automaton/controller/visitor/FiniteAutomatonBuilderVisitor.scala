package automaton.controller.visitor

import automaton.CLIMain
import automaton.antrl4.{FiniteAutomatonBaseVisitor, FiniteAutomatonParser}
import automaton.controller.builder.{AutomatonComponents, DFAComponents, NFAComponents, eNFAComponents}
import automaton.model.*
import org.antlr.v4.runtime.tree.*

import scala.jdk.CollectionConverters.*

class FiniteAutomatonBuilderVisitor
  extends FiniteAutomatonBaseVisitor[AutomatonComponents[_, _]] {

  private val requiredSections = Set("states", "alphabet", "transitions", "initialState")
  private var seenSections = Set.empty[String]
  private var automatonType: Option[String] = None

  //Chooses the builder for the correct type of finite automaton
  private var currentBuilder: AutomatonComponents[_, _] = _

  override def visitAutomaton(ctx: FiniteAutomatonParser.AutomatonContext): AutomatonComponents[_, _] = {
    seenSections = Set.empty
    automatonType = Some(ctx.automatonType().getText.toLowerCase)
    currentBuilder = automatonType match {
      case Some("dfa") => DFAComponents()
      case Some("nfa") => NFAComponents()
      case Some("ε-nfa") => eNFAComponents()
      case _ => throw new IllegalArgumentException(s"Unknown automaton type: ${automatonType.getOrElse("none")}")
    }
    val initialType = Some(ctx.automatonType().getText)
    val sections = ctx.section().asScala.map(visit)

    if (CLIMain.DEBUG == 1)
      println(currentBuilder)

    validateRequiredSections()
    currentBuilder
  }

  private def validateRequiredSections(): Unit = {
    val missing = requiredSections.diff(seenSections)
    if (missing.nonEmpty)
      throw new IllegalArgumentException(
        s"Missing required sections: ${missing.mkString(", ")}"
      )
  }

  override def visitStates(ctx: FiniteAutomatonParser.StatesContext): AutomatonComponents[_, _] = {
    seenSections += "states"
    val stateNames = ctx.SYMBOL().asScala.map(_.getText)
    if (stateNames.isEmpty) throw new IllegalArgumentException("At least one state is required")
    currentBuilder = currentBuilder match {
      case d: DFAComponents => d.copy(states = stateNames.map(State.apply).toSet)
      case n: NFAComponents => n.copy(states = stateNames.map(State.apply).toSet)
      case e: eNFAComponents => e.copy(states = stateNames.map(State.apply).toSet)
      case _ => throw new IllegalStateException("Unsupported builder type")
    }
    currentBuilder
  }

  override def visitAlphabet(ctx: FiniteAutomatonParser.AlphabetContext): AutomatonComponents[_, _] = {
    seenSections += "alphabet"
    val symbols = ctx.SYMBOL().asScala.map(_.getText)
    if (symbols.isEmpty) throw new IllegalArgumentException("Alphabet cannot be empty")

    currentBuilder = currentBuilder match {
      case d: DFAComponents => d.copy(alphabet = symbols.toSet)
      case n: NFAComponents => n.copy(alphabet = symbols.toSet)
      case e: eNFAComponents => e.copy(alphabet = symbols.toSet)
      case _ => throw new IllegalStateException("Unsupported builder type")
    }
    currentBuilder
  }

  override def visitTransitions(ctx: FiniteAutomatonParser.TransitionsContext): AutomatonComponents[_, _] = {
    seenSections += "transitions"

    val transitions = ctx.transition().asScala.flatMap { t =>
      val from = State(t.source.getText)
      val symbol = t.inputSymbol().getText

      t.destination() match {
        case single: FiniteAutomatonParser.SingleStateContext =>
          Set(Transition(from, symbol, State(single.SYMBOL.getText)))
        case set: FiniteAutomatonParser.StateSetContext =>
          set.SYMBOL().asScala.map(s => Transition(from, symbol, State(s.getText))).toSet
      }
    }.toSet

    currentBuilder = currentBuilder match {
      case d: DFAComponents => d.copy(transitions = d.transitions ++ transitions)
      case n: NFAComponents => n.copy(transitions = n.transitions ++ transitions)
      case e: eNFAComponents => e.copy(transitions = e.transitions ++ transitions)
      case _ => throw new IllegalStateException("Unsupported builder type")
    }
    currentBuilder
  }

  override def visitInitialState(ctx: FiniteAutomatonParser.InitialStateContext): AutomatonComponents[_, _] = {
    seenSections += "initialState"
    val symbol = ctx.SYMBOL().getText
    if (symbol.isEmpty) throw new IllegalArgumentException("The initial state is required")

    currentBuilder = currentBuilder match {
      case d: DFAComponents => d.copy(initialState = Some(State(symbol)))
      case n: NFAComponents => n.copy(initialState = Some(State(symbol)))
      case e: eNFAComponents => e.copy(initialState = Some(State(symbol)))
      case _ => throw new IllegalStateException("Unsupported builder type")
    }
    currentBuilder
  }

  override def visitFinalStates(ctx: FiniteAutomatonParser.FinalStatesContext): AutomatonComponents[_, _] = {
    seenSections += "finalStates"
    val symbols = ctx.SYMBOL().asScala.map(_.getText)

    currentBuilder = currentBuilder match {
      case d: DFAComponents => d.copy(finalStates = d.finalStates ++ symbols.map(State.apply))
      case n: NFAComponents => n.copy(finalStates = n.finalStates ++ symbols.map(State.apply))
      case e: eNFAComponents => e.copy(finalStates = e.finalStates ++ symbols.map(State.apply))
      case _ => throw new IllegalStateException("Unsupported builder type")
    }
    currentBuilder
  }

  override def visitComputations(ctx: FiniteAutomatonParser.ComputationsContext): AutomatonComponents[_, _] = {
    seenSections += "computations"
    val strings = ctx.SYMBOL().asScala.map(_.getText)

    currentBuilder = currentBuilder match {
      case d: DFAComponents =>
        d.copy(computations = d.computations ++ strings.map(str => Computation(str)))
      case n: NFAComponents =>
        n.copy(computations = n.computations ++ strings.map(str => Computation(str)))
      case e: eNFAComponents =>
        e.copy(computations = e.computations ++ strings.map(str => Computation(str)))
      case _ => throw new IllegalStateException("Unsupported builder type")
    }
    currentBuilder
  }

  override def visitSection(ctx: FiniteAutomatonParser.SectionContext): AutomatonComponents[_, _] = {
    visitChildren(ctx)
    currentBuilder
  }
}