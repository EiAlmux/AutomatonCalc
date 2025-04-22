package automaton.controller

import automaton.antrl4.{FiniteAutomatonLexer, FiniteAutomatonParser}
import automaton.model.Automaton
import automaton.controller.builder.AutomatonComponents
import automaton.controller.visitor.FiniteAutomatonBuilderVisitor
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.*

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

object MainAutomaton {

  def processAutomaton(filePath: String): Option[Automaton] =

    AutomatonCache.getAutomaton match
      case Some(automaton) => return Some(automaton)
      case None =>

    try
      val input = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8)
      val inputStream = CharStreams.fromString(input)
      val lexer = new FiniteAutomatonLexer(inputStream)
      val tokens = new CommonTokenStream(lexer)
      val parser = new FiniteAutomatonParser(tokens)
      val tree: ParseTree = parser.automaton()

      val visitor = new FiniteAutomatonBuilderVisitor
      val automatonComponents: AutomatonComponents = visitor.visit(tree)
      val automaton = automatonComponents.toAutomaton

      AutomatonCache.storeAutomaton(automaton)
      Some(automaton)

    catch
      case e: Exception =>
        println(s"Error processing Automaton: ${e.getMessage}")
        None
}




