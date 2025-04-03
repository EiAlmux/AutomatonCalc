import org.antlr.v4.runtime._
import org.antlr.v4.runtime.tree._
import antlr4._
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

//OLD VERSION, NOT UPDATED
object CLIMain {
  def main(args: Array[String]): Unit = {
    if (args.length != 1) {
      println("Usage: DFAMain <path to DFA file>")
      sys.exit(1)
    }

    val filePath = args(0)
    val input = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8)

    //Check antlr runtime version
    //println("ANTLR Runtime Version: " + org.antlr.v4.runtime.RuntimeMetaData.getRuntimeVersion)


    // Create an ANTLR input stream
    val inputStream = CharStreams.fromString(input)

    // Create a lexer
    val lexer = new DFALexer(inputStream)
    val tokens = new CommonTokenStream(lexer)

    /*
    tokens.fill() // load all tokens
    tokens.getTokens.forEach { token =>
      println(token.toString)
      }*/

    // Create a parser
    val parser = new DFAParser(tokens)

    // Parse the DFA definition
    val tree: ParseTree = parser.dfa()

    // Print the parse tree (for debugging purposes)
    println(tree.toStringTree(parser))
  }
}