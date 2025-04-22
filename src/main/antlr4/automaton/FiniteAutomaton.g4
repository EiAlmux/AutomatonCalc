grammar FiniteAutomaton;

@header {
package automaton.antlr4;
}

automaton : automatonType '{' section* '}' ;

automatonType : 'DFA' | 'NFA' | 'eNFA' ;

section
    : states
    | alphabet
    | transitions
    | initialState
    | finalStates
    | computations
    ;

states : 'states' ':' SYMBOL (',' SYMBOL)* ';' ;
alphabet : 'alphabet' ':' SYMBOL (',' SYMBOL)* ';' ;

transitions : 'transitions' ':' (transition (';' transition)*)? ';' ;
transition : SYMBOL inputSymbol '->' transitionTarget ;
inputSymbol : SYMBOL | EPSILON ;
transitionTarget
    : SYMBOL               # SingleState
    | '{' SYMBOL (',' SYMBOL)* '}'  # StateSet
    ;

initialState : 'initial' ':' SYMBOL ';' ;
finalStates : 'final' ':' SYMBOL (',' SYMBOL)* ';' ;

computations : 'computations' ':' SYMBOL (',' SYMBOL)* ';' ;

// Lexer rules
SYMBOL : [a-zA-Z0-9_]+ ;
EPSILON : 'ε' ;
WS : [ \t\r\n]+ -> skip ;