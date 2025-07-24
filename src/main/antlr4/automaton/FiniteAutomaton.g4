grammar FiniteAutomaton ;
import CommonLexer ;

automaton : automatonType '{' section* '}' ;

automatonType : DFA | NFA | EPSILON_NFA ;

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
transition : source inputSymbol '->' destination ;
source : SYMBOL ;
inputSymbol : SYMBOL | EPSILON ;
destination
    : SYMBOL               # SingleState
    | '{' SYMBOL (',' SYMBOL)* '}'  # StateSet
    ;

initialState : 'initial' ':' SYMBOL ';' ;
finalStates : 'final' ':' SYMBOL (',' SYMBOL)* ';' ;

computations : 'computations' ':' computationitem (',' computationitem)* ';' ;
computationitem : SYMBOL | EPSILON;
