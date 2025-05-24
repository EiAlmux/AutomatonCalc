package automaton.controller.visitor


import automaton.CLIMain
import automaton.antrl4.{CFGBaseVisitor, CFGParser}
import automaton.controller.builder.CFGComponents
import automaton.model.{CFGProduction, Computation, State}
import scala.jdk.CollectionConverters._


class CFGBuilderVisitor extends CFGBaseVisitor [CFGComponents] {

  private val requiredSections = Set("variables", "terminals", "productions", "startSymbol")
  private var seenSections = Set.empty [String]
  private var automatonType: Option [String] = None

  override def visitAutomaton(ctx: CFGParser.AutomatonContext): CFGComponents = {
    seenSections = Set.empty
    automatonType = Some(ctx.automatonType().getText.toLowerCase)
    val sections = ctx.section().asScala.map(visit)
    val result = sections.foldLeft(CFGComponents())(_ merge _)

    if (CLIMain.DEBUG == 1)
      println(result)

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

  override def visitVariables(ctx: CFGParser.VariablesContext): CFGComponents = {
    seenSections += "variables"
    val variableNames = ctx.SYMBOL().asScala.map(_.getText)
    if (variableNames.isEmpty) throw new IllegalArgumentException("At least one variable is required")
    CFGComponents(variables = variableNames.map(State.apply).toSet)
  }

  override def visitTerminals(ctx: CFGParser.TerminalsContext): CFGComponents = {
    seenSections += "terminals"
    val symbols = ctx.SYMBOL().asScala.map(_.getText)
    if (symbols.isEmpty) throw new IllegalArgumentException("Terminals cannot be empty")
    CFGComponents(terminals = symbols.toSet)
  }

  override def visitProductions(ctx: CFGParser.ProductionsContext): CFGComponents = {
    seenSections += "productions"

    val prods: Set[CFGProduction] = ctx.production().asScala.flatMap { prodCtx =>
      val lhs = State(prodCtx.SYMBOL().getText)
      prodCtx.productionRhs().rhsAlternative().asScala.map { altCtx =>
        val rhsSymbols: Seq[String] = altCtx.SYMBOL().asScala.map(_.getText).toSeq

        CFGProduction(lhs, rhsSymbols)
      }
    }.toSet

    CFGComponents(productions = prods)
  }

  override def visitStartSymbol(ctx: CFGParser.StartSymbolContext): CFGComponents = {
    seenSections += "startSymbol"
    val start = State(ctx.SYMBOL().getText)
    CFGComponents(startSymbol = Some(start))
  }

  override def visitDerivations(ctx: CFGParser.DerivationsContext): CFGComponents = {
    seenSections += "derivations"
    val inputs: Seq[String] = ctx.SYMBOL().asScala.map(_.getText).toSeq
    val comps: Seq[Computation] = inputs.map(i => Computation(i))
    CFGComponents(computations = comps)
  }


  override def defaultResult: CFGComponents = CFGComponents()

  override def aggregateResult (aggregate: CFGComponents, nextResult: CFGComponents): CFGComponents = {
    aggregate.merge(nextResult)
  }
}
