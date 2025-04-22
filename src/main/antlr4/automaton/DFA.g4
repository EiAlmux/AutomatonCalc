grammar DFA;

@header {
package automaton.antlr4;
}

dfa : 'DFA' '{' section* '}' ;

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
transition : SYMBOL SYMBOL '->' SYMBOL ;

initialState : 'initial' ':' SYMBOL ';' ;
finalStates : 'final' ':' SYMBOL (',' SYMBOL)* ';' ;

computations : 'computations' ':' SYMBOL (',' SYMBOL)* ';' ;

// Lexer rules
SYMBOL : [a-zA-Z0-9_]+ ;
WS : [ \t\r\n]+ -> skip ;
