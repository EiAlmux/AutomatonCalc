package automaton.controller

import automaton.antrl4.{FiniteAutomatonLexer, FiniteAutomatonParser, PDALexer, PDAParser}
import automaton.model.{Automaton, PDA}
import automaton.controller.visitor.{FiniteAutomatonBuilderVisitor, PDABuilderVisitor}

import scala.jdk.CollectionConverters.ListHasAsScala
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.*
import scala.util.control.NonFatal
import scala.util.matching.Regex
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

object MainAutomaton {

//  def processMultipleAutomata(filePath: String): Option[List[Automaton[_, _]]] = {
//    val input = Files.readString(Paths.get(filePath), StandardCharsets.UTF_8)
//
//    val sections = splitFile(input)
//  }

  private def splitFile(input: String): List[(String, String)] = {

    val pattern = """(DFA|NFA|EPSILON_NFA|PDA)\b""".r

    val matches = pattern.findAllMatchIn(input).toList

    if (matches.isEmpty) return List((input, "DFA")) //used as default

    val sections = matches.sliding (2).map {
      case List (m1, m2) => (input.substring (m1.start, m2.start), m1.group (1))
    }.toList :+
      ((input.substring (matches.last.start), matches.last.group (1)))

    sections
  }

  def processAutomaton (filePath: String): Option [Automaton [_, _]] = {
    val input = Files.readString(Paths.get(filePath), StandardCharsets.UTF_8)

    val probeStream = CharStreams.fromString(input)
    val probeLexer = new FiniteAutomatonLexer(probeStream)
    val probeTokens = new CommonTokenStream(probeLexer)
    probeTokens.fill()

    val kindToken = probeTokens.getTokens.asScala
      .find(t => Set(
        FiniteAutomatonLexer.DFA,
        FiniteAutomatonLexer.NFA,
        FiniteAutomatonLexer.EPSILON_NFA,
        FiniteAutomatonLexer.PDA
        ).contains(t.getType))
      .getOrElse(throw new IllegalArgumentException("No automaton type specified."))

    kindToken.getType match {
      //PDA 
      case FiniteAutomatonLexer.PDA =>
        processGrammar [PDAParser, PDABuilderVisitor, PDA](
          input,
          new PDALexer(_),
          new PDAParser(_),
          (v, t) => v.visit(t).toAutomaton, AutomatonCache.storePDA)

      case _ =>
        // Finite automaton (DFA, NFA, eNFA)
        processGrammar [FiniteAutomatonParser, FiniteAutomatonBuilderVisitor, Automaton [_, _]](
          input,
          new FiniteAutomatonLexer(_),
          new FiniteAutomatonParser(_),
          (v, t) => v.visit(t).toAutomaton, AutomatonCache.storeAutomaton)

    }

  }

  private def processGrammar[P <: Parser, V, A] (
                                                  input: String,
                                                  lexerCtor: CharStream => Lexer,
                                                  parserCtor: CommonTokenStream => P,
                                                  buildAutomaton: (V, ParseTree) => Either [String, A],
                                                  store: A => Unit
                                                )(implicit
                                                  visitorCtor: () => V,
                                                  extractTree: P => ParseTree
                                                ): Option [A] = {

    val stream = CharStreams.fromString(input)
    val tokens = new CommonTokenStream(lexerCtor(stream))
    tokens.fill()

    try {
      val parser = parserCtor(tokens)
      val tree = extractTree(parser)
      val visitor = visitorCtor()
      buildAutomaton(visitor, tree) match {
        case Right(automaton) =>
          store(automaton)
          Some(automaton)
        case Left(err) =>
          println(s"Error building automaton: $err")
          None
      }
    } catch {
      case NonFatal(e) =>
        println(s"Parsing/visiting error: ${e.getMessage}")
        None
    }
  }


  implicit val faVisitorCtor: () => FiniteAutomatonBuilderVisitor =
    () => new FiniteAutomatonBuilderVisitor
  implicit val faExtractTree: FiniteAutomatonParser => ParseTree =
    _.automaton()
  implicit val pdaVisitorCtor: () => PDABuilderVisitor =
    () => new PDABuilderVisitor
  implicit val pdaExtractTree: PDAParser => ParseTree =
    _.automaton()
}


