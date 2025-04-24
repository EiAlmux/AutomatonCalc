lexer grammar CommonLexer ;
SYMBOL : [a-zA-Z0-9_]+ ;
EPSILON : 'ε' ;
WS : [ \t\r\n]+ -> skip ;