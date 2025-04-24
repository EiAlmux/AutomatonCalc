grammar FiniteAutomaton ;
import CommonLexer ;

automaton : automatonType '{' section* '}' ;

automatonType : 'DFA' | 'NFA' | 'ε-NFA' ;

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
