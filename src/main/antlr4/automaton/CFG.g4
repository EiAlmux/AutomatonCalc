grammar CFG;
import CommonLexer;

automaton : automatonType '{' section* '}' ;

automatonType : CFG ;

section
    : variables
    | terminals
    | productions
    | startSymbol
    | derivations
    ;

variables : 'variables' ':' SYMBOL (',' SYMBOL)* ';' ;

terminals : 'terminals' ':' SYMBOL (',' SYMBOL)* ';' ;

productions : 'productions' ':' (production (';' production)*)? ';' ;

production : lhs '->' productionRhs ;
lhs: SYMBOL ;

productionRhs : rhsAlternative ('|' rhsAlternative)* ;

rhsAlternative : (SYMBOL | EPSILON)+ ;

startSymbol : 'start' ':' SYMBOL ';' ;

derivations : 'derivations' ':' SYMBOL (',' SYMBOL)* ';' ;