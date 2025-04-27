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

  def processFiniteAutomaton(filePath: String): Option[Automaton[_,_]] =

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
      val automatonComponents = visitor.visit(tree)
      automatonComponents.toAutomaton match {
        case Right(automaton) =>
          AutomatonCache.storeAutomaton(automaton)
          Some(automaton)
        case Left(error) =>
          println(s"Error creating automaton: $error")
          None
      }

    catch
      case e: Exception =>
        println(s"Error processing Automaton: ${e.getMessage}")
        None
}




