lexer grammar CommonLexer ;
DFA     : 'DFA';
NFA     : 'NFA';
EPSILON_NFA : 'ε-NFA';
PDA     : 'PDA';
SYMBOL : [a-zA-Z0-9_]+ ;
EPSILON : 'ε' ;
WS : [ \t\r\n]+ -> skip ;