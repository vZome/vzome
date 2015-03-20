/**
 * Define a lexer grammar called ZomicLexer
 */

lexer grammar ZomicLexer;

tokens {
	EOF,		// Built into AN-TLR, but specified here so that it can be resolved by some AST viewer plug-ins
	EMPTY,
	NULL
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
ALL				: 'all';
HALF			: 'half';
SIZE			: 'size';
SHORT			: 'short';
MEDIUM			: 'medium';
LONG			: 'long';
GREEN			: 'green';
ORANGE			: 'orange';
PURPLE			: 'purple';
BLACK			: 'black';
RED				: 'red';
PENT			: 'pent';
PENTAGON		: 'pentagon';
BLUE			: 'blue';
RECT			: 'rect';
RECTANGLE		: 'rectangle';
YELLOW			: 'yellow';
TRI				: 'tri';
TRIANGLE		: 'triangle';

LBRACE			: '{' ;
RBRACE			: '}' ;

LPAREN			: '(' ;
RPAREN			: ')' ;

ML_COMMENT_START	:	'/*';
ML_COMMENT_END		:	'*/';

// order of evaluation is important here!
INT			: ( '+' | '-' )? NUMBER; 
NUMBER		: DIGIT DIGIT*; // Use of DIGIT? doesn't seem to work right in some of the AST visualizers. 
POLARITY	:	( '+' | '-' );// (~[0-9]); // e.g. used by handedness 
DIGIT		: '0'..'9';

// white space
WS			: ([ \t\f] | EOL )	-> skip; 

// end of line
EOL			: '\r'? '\n';

fragment 
COMMENT		:	(~[\r\n]) ;
				
/**
 * SL_COMMENT and ML_COMMENT are specifically piped to -> channel(HIDDEN) instead of -> skip.
 * This is so that a higher level stream parser or an interpreter could parse things we are ignoring (e.g. additional commands, etc.)
 */

// Single-line comment
SL_COMMENT	:   '//' COMMENT* (EOL | EOF) -> channel(HIDDEN);

// Multi-line comment
ML_COMMENT	:   ML_COMMENT_START (COMMENT | EOL)*? ML_COMMENT_END -> channel(HIDDEN);

fragment 
IDENT_START	: [a-z_.];

// Make sure that "greedy" patterns like IDENT don't get defined until after the more specific ones, like literals or keywords
IDENT		:	// options {testLiterals=true;} // TODO: KEYWORD only
    		IDENT_START	(IDENT_START | NUMBER)*;

	
// -----------------
// Illegal Character
//
// This is an illegal character trap which is always the last rule in the
// lexer specification. It matches a single character of any value and being
// the last rule in the file will match when no other rule knows what to do
// about the character. It is reported as an error but is not passed on to the
// parser. This means that the parser to deal with the grammar file anyway
// but we will not try to analyze or code generate from a file with lexical
// errors.
//
UNEXPECTED_CHAR
	:	.//	-> channel(HIDDEN)
	;
	
	