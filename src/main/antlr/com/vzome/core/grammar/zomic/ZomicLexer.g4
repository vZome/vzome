/**
 * Define a lexer grammar called ZomicLexer
 */

lexer grammar ZomicLexer;

tokens {
	EOF		// Built into ANTLR, but specified here so that it can be resolved by some AST viewer plug-ins
}

/**
 * Key Words.
 * Be sure these are defined (and therefore evaluated) before greedy tokens like IDENT.
 */
LABEL			: 'label';
SCALE			: 'scale';
BUILD			: 'build';
DESTROY			: 'destroy'; 
MOVE			: 'move';
ROTATE			: 'rotate';
AROUND			: 'around';
REFLECT			: 'reflect';
FROM			: 'from';
SYMMETRY		: 'symmetry';
THROUGH			: 'through';
CENTER			: 'center';
REPEAT			: 'repeat';
BRANCH			: 'branch';
SAVE			: 'save';
LOCATION		: 'location';
ORIENTATION		: 'orientation';
ALL			    : 'all';

// sizes
HALF			: 'half';
SIZE			: 'size';
SHORT			: 'short';
MEDIUM			: 'medium';
LONG			: 'long';

// colors
GREEN			: 'green';
ORANGE			: 'orange';
PURPLE			: 'purple';
BLACK			: 'black';
RED			    : 'red';
PENT			: 'pent';
PENTAGON		: 'pentagon';
BLUE			: 'blue';
RECT			: 'rect';
RECTANGLE		: 'rectangle';
YELLOW			: 'yellow';
TRI			    : 'tri';
TRIANGLE		: 'triangle';

// Maybe new Zomic colors in the future?
// Adjust the unit tests when these are uncommented
//LAVENDER 		: 'lavender'; 
//OLIVE			: 'olive'; 
//MAROON 			: 'maroon'; 
//ROSE 			: 'rose'; 
//NAVY 			: 'navy'; 
//TURQUOISE		: 'turquoise'; 
//CORAL 			: 'coral'; 
//SULFUR 			: 'sulfur'; 
// unsupported colors... why are they not listed in ZomicNamingConvention
//SAND			: 'sand'; 
//APPLE			: 'apple'; 
//CINNAMON		: 'cinnamon'; 
//SPRUCE			: 'spruce'; 
//BROWN			: 'brown'; 

LBRACE			: '{' ;
RBRACE			: '}' ;

LPAREN			: '(' ;
RPAREN			: ')' ;

QUESTIONMARK	: '?' ;

WS			    : ([ \t\f] | EOL )	-> skip; // skip white space
EOL			    : ('\r'? '\n') | ('\n'? '\r'); // end of line

fragment DIGIT  : '0'..'9';
fragment NUMBER	: DIGIT+; // 1 or more digits

INT		        : ( '+' | '-' )? NUMBER+;
POLARITY	    : '+' | '-' ;    // Used by axis_expr.handedness

fragment COMMENT		    : (~[\r\n]);
fragment SL_COMMENT_START	: '//';
fragment ML_COMMENT_START	: '/*';
fragment ML_COMMENT_END		: '*/';
/**
 * SL_COMMENT and ML_COMMENT are specifically piped to -> channel(HIDDEN) instead of -> skip.
 * This is so that a higher level stream parser or an interpreter could parse things we are ignoring (e.g. additional commands, etc.)
 */
// Single-line comment
SL_COMMENT	    : SL_COMMENT_START COMMENT* (EOL | EOF) -> channel(HIDDEN);
// Multi-line comment
ML_COMMENT	    : ML_COMMENT_START (COMMENT | EOL)*? ML_COMMENT_END -> channel(HIDDEN);


fragment IDENT_START	    : [a-z_.];
// Make sure that "greedy" patterns like IDENT don't get defined until after the more specific ones, like literals or keywords
IDENT           : IDENT_START ( IDENT_START | NUMBER )*;

	
// -----------------
// Illegal Character
//
// This is an illegal character trap which is always the last rule in the
// lexer specification. It matches a single character of any value and being
// the last rule in the file will match when no other rule knows what to do
// about the character. It is reported as an error but is not passed on to the
// parser. This means that the parser has to deal with the grammar file anyway
// but we will not try to analyze or code generate from a file with lexical
// errors.
//
UNEXPECTED_CHAR : .;
