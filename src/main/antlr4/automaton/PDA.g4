grammar PDA ;
import CommonLexer ;

automaton : automatonType '{' section* '}' ;

automatonType : PDA ;

section
    : states
    | inputAlphabet
    | stackAlphabet
    | transitions
    | initialState
    | initialStack
    | finalStates
    | computations
    ;

states : 'states' ':' SYMBOL (',' SYMBOL)* ';' ;
inputAlphabet : 'alphabet' ':' SYMBOL (',' SYMBOL)* ';' ;
stackAlphabet: 'stack alphabet' ':' SYMBOL (',' SYMBOL)* ';' ;

transitions : 'transitions' ':' (transition (';' transition)*)? ';' ;
transition : source ',' inputSymbol ',' stackSymbol '->' destination ',' stackOperation ;
source: SYMBOL ;
destination: SYMBOL ;
inputSymbol : SYMBOL | EPSILON ;
stackSymbol : SYMBOL | EPSILON ;
stackOperation : SYMBOL* | EPSILON ;

initialState : 'initial state' ':' SYMBOL ';' ;
initialStack: 'initial stack' ':' SYMBOL ';' ;

finalStates : 'final' ':' SYMBOL (',' SYMBOL)* ';' ;

computations : 'computations' ':' SYMBOL (',' SYMBOL)* ';' ;
