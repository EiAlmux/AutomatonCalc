grammar TuringMachine ;
import CommonLexer ;

automaton : automatonType '{' section* '}' ;

automatonType : TuringMachine ;

section
    : states
    | inputAlphabet
    | tapeAlphabet
    | transitions
    | initialState
    | blankSymbol
    | finalStates
    | computations
    ;

states : 'states' ':' SYMBOL (',' SYMBOL)* ';' ;
inputAlphabet : 'input alphabet' ':' SYMBOL (',' SYMBOL)* ';' ;
tapeAlphabet: 'tape alphabet' ':' SYMBOL (',' SYMBOL)* ';' ;

transitions : 'transitions' ':' (transition (';' transition)*)? ';' ;

transition : source ',' readSymbol '->' destination ',' writeSymbol ',' direction ;
source: SYMBOL ;
destination: SYMBOL ;
readSymbol : SYMBOL ;
writeSymbol : SYMBOL ;
direction: LEFT | RIGHT ;
LEFT : 'LEFT' ;
RIGHT : 'RIGHT' ;

initialState : 'initial state' ':' SYMBOL ';' ;
blankSymbol: 'blank symbol' ':' SYMBOL ';' ;

finalStates : 'final' ':' SYMBOL (',' SYMBOL)* ';' ;

computations : 'computations' ':' SYMBOL (',' SYMBOL)* ';' ;
