package automaton.controller

import automaton.antrl4.{CFGLexer, CFGParser, FiniteAutomatonLexer, FiniteAutomatonParser, PDALexer, PDAParser}
import automaton.controller.visitor.{CFGBuilderVisitor, FiniteAutomatonBuilderVisitor, PDABuilderVisitor}
import automaton.model.{Automaton, CFG, PDA}
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.*

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.util.control.NonFatal
import scala.util.matching.Regex
import scala.util.{Failure, Success, Try}

object MainAutomaton {

  def processAutomata(filePath: String): List[Try[Automaton[_, _]]] = {
    val input     = Files.readString(Paths.get(filePath), StandardCharsets.UTF_8)
    val sections  = splitFile(input)

    sections.map { case (content, automatonType) =>
      automatonType match {
        case "CFG" =>
          processGrammar[CFGParser, CFGBuilderVisitor, CFG](
            content,
            new CFGLexer(_),
            new CFGParser(_),
            (v, t) => v.visit(t).toAutomaton
          )
        case "PDA" =>
          processGrammar[PDAParser, PDABuilderVisitor, PDA](
            content,
            new PDALexer(_),
            new PDAParser(_),
            (v, t) => v.visit(t).toAutomaton
          )
        case _ =>
          processGrammar[FiniteAutomatonParser, FiniteAutomatonBuilderVisitor, Automaton[_, _]](
            content,
            new FiniteAutomatonLexer(_),
            new FiniteAutomatonParser(_),
            (v, t) => v.visit(t).toAutomaton
          )
        }

    }
  }

  private def splitFile(input: String): List[(String, String)] = {
    val pattern = """(DFA|NFA|ε-NFA|PDA|CFG)\s*\{""".r
    val matches = pattern.findAllMatchIn(input).toList

    matches match {
      case Nil => List((input.trim, "None"))
      case m :: Nil => List((input.trim, m.group(1)))
      case _ =>
        val sections = matches.sliding(2).map {
          case List(m1, m2) => (input.substring(m1.start, m2.start).trim, m1.group(1))
        }.toList
        sections :+ (input.substring(matches.last.start).trim, matches.last.group(1))
    }
  }

  private def processGrammar[P <: Parser, V, A](
                                                 input: String,
                                                 lexerCtor: CharStream => Lexer,
                                                 parserCtor: CommonTokenStream => P,
                                                 buildAutomaton: (V, ParseTree) => Either[String, A])
                                               (implicit
                                                 visitorCtor: () => V,
                                                 extractTree: P => ParseTree
                                               ): Try[A] = {
    val stream = CharStreams.fromString(input)
    val tokens = new CommonTokenStream(lexerCtor(stream))
    tokens.fill()

    Try {
      val parser = parserCtor(tokens)
      val tree = extractTree(parser)
      val visitor = visitorCtor()

      buildAutomaton(visitor, tree) match {
        case Right(automaton) =>
          automaton

        case Left(errMsg) =>
          throw new IllegalArgumentException(s"Automaton build error: $errMsg")
      }
    } recoverWith {
      case NonFatal(e) =>
        Failure(e)
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
  implicit val cfgVisitorCtor: () => CFGBuilderVisitor =
    () => new CFGBuilderVisitor
  implicit val cfgExtractTree: CFGParser => ParseTree =
    _.automaton()
}


