// Generated from C:/Users/chr18/IdeaProjects/AutomatonCalc/src/main/antlr4/DFA.g4 by ANTLR 4.13.2

package antlr4;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link DFAParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface DFAVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link DFAParser#dfa}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDfa(DFAParser.DfaContext ctx);
	/**
	 * Visit a parse tree produced by {@link DFAParser#section}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSection(DFAParser.SectionContext ctx);
	/**
	 * Visit a parse tree produced by {@link DFAParser#states}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStates(DFAParser.StatesContext ctx);
	/**
	 * Visit a parse tree produced by {@link DFAParser#alphabet}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAlphabet(DFAParser.AlphabetContext ctx);
	/**
	 * Visit a parse tree produced by {@link DFAParser#transitions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTransitions(DFAParser.TransitionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link DFAParser#transition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTransition(DFAParser.TransitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link DFAParser#initialState}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInitialState(DFAParser.InitialStateContext ctx);
	/**
	 * Visit a parse tree produced by {@link DFAParser#finalStates}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFinalStates(DFAParser.FinalStatesContext ctx);
	/**
	 * Visit a parse tree produced by {@link DFAParser#computations}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComputations(DFAParser.ComputationsContext ctx);
}