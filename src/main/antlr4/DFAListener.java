// Generated from C:/Users/chr18/IdeaProjects/AutomatonCalc/src/main/antlr4/DFA.g4 by ANTLR 4.13.2

package antlr4;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link DFAParser}.
 */
public interface DFAListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link DFAParser#dfa}.
	 * @param ctx the parse tree
	 */
	void enterDfa(DFAParser.DfaContext ctx);
	/**
	 * Exit a parse tree produced by {@link DFAParser#dfa}.
	 * @param ctx the parse tree
	 */
	void exitDfa(DFAParser.DfaContext ctx);
	/**
	 * Enter a parse tree produced by {@link DFAParser#section}.
	 * @param ctx the parse tree
	 */
	void enterSection(DFAParser.SectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link DFAParser#section}.
	 * @param ctx the parse tree
	 */
	void exitSection(DFAParser.SectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link DFAParser#states}.
	 * @param ctx the parse tree
	 */
	void enterStates(DFAParser.StatesContext ctx);
	/**
	 * Exit a parse tree produced by {@link DFAParser#states}.
	 * @param ctx the parse tree
	 */
	void exitStates(DFAParser.StatesContext ctx);
	/**
	 * Enter a parse tree produced by {@link DFAParser#alphabet}.
	 * @param ctx the parse tree
	 */
	void enterAlphabet(DFAParser.AlphabetContext ctx);
	/**
	 * Exit a parse tree produced by {@link DFAParser#alphabet}.
	 * @param ctx the parse tree
	 */
	void exitAlphabet(DFAParser.AlphabetContext ctx);
	/**
	 * Enter a parse tree produced by {@link DFAParser#transitions}.
	 * @param ctx the parse tree
	 */
	void enterTransitions(DFAParser.TransitionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link DFAParser#transitions}.
	 * @param ctx the parse tree
	 */
	void exitTransitions(DFAParser.TransitionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link DFAParser#transition}.
	 * @param ctx the parse tree
	 */
	void enterTransition(DFAParser.TransitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link DFAParser#transition}.
	 * @param ctx the parse tree
	 */
	void exitTransition(DFAParser.TransitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link DFAParser#initialState}.
	 * @param ctx the parse tree
	 */
	void enterInitialState(DFAParser.InitialStateContext ctx);
	/**
	 * Exit a parse tree produced by {@link DFAParser#initialState}.
	 * @param ctx the parse tree
	 */
	void exitInitialState(DFAParser.InitialStateContext ctx);
	/**
	 * Enter a parse tree produced by {@link DFAParser#finalStates}.
	 * @param ctx the parse tree
	 */
	void enterFinalStates(DFAParser.FinalStatesContext ctx);
	/**
	 * Exit a parse tree produced by {@link DFAParser#finalStates}.
	 * @param ctx the parse tree
	 */
	void exitFinalStates(DFAParser.FinalStatesContext ctx);
	/**
	 * Enter a parse tree produced by {@link DFAParser#computations}.
	 * @param ctx the parse tree
	 */
	void enterComputations(DFAParser.ComputationsContext ctx);
	/**
	 * Exit a parse tree produced by {@link DFAParser#computations}.
	 * @param ctx the parse tree
	 */
	void exitComputations(DFAParser.ComputationsContext ctx);
}