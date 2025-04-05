package automaton.utils

import automaton.visitors.DFABuilderVisitor
import antlr4.{DFALexer, DFAParser}
import org.antlr.v4.runtime._
import org.antlr.v4.runtime.tree._

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

object MainAutomaton:

  def processDFA(filePath: String): Option[DFA] =

    DFACache.getDFA match
      case Some(dfa) => return Some(dfa)
      case None =>

    try
      val input = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8)
      val inputStream = CharStreams.fromString(input)
      val lexer = new DFALexer(inputStream)
      val tokens = new CommonTokenStream(lexer)
      val parser = new DFAParser(tokens)
      val tree: ParseTree = parser.dfa()

      val visitor = new DFABuilderVisitor
      val dfaComponents: DFAComponents = visitor.visit(tree)
      val dfa = dfaComponents.toDFA

      DFACache.storeDFA(dfa)


      Some(dfa)

    catch
      case e: Exception =>
        println(s"Error processing DFA: ${e.getMessage}")
        None

  object DFACache:
    private var cachedDFA: Option[DFA] = None

    def getDFA: Option[DFA] = cachedDFA

    def storeDFA(dfa: DFA): Unit =
      cachedDFA = Some(dfa)

    def clearCache(): Unit =
      cachedDFA = None





