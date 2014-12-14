
/*
header {

package org.vorthmann.zome.zomic.parser;

import org.vorthmann.xml.ANTLR2XML;
import org.vorthmann.zome.math.symmetry.Constants;
import org.vorthmann.xml .ANTLRContentSupplier;


}

class ZomicParser extends Parser;


options {
	defaultErrorHandler = false;
	k = 2;				// two token lookahead
	buildAST = false;	// doing event-based parsing using ANTLR2XML
}

*/

grammar ZomicParser;

tokens {
	BLOCK; MODIFIERS; OBJBLOCK; SLIST; CTOR_DEF; METHOD_DEF; VARIABLE_DEF;
	INSTANCE_INIT; STATIC_INIT; TYPE; CLASS_DEF; INTERFACE_DEF;
	PACKAGE_DEF; ARRAY_DECLARATOR; EXTENDS_CLAUSE; IMPLEMENTS_CLAUSE;
	PARAMETERS; PARAMETER_DEF; LABELED_STAT; TYPECAST; INDEX_OP;
	POST_INC; POST_DEC; METHOD_CALL; EXPR; ARRAY_INIT;
	UNARY_MINUS; UNARY_PLUS; CASE_GROUP; ELIST; FOR_INIT; FOR_CONDITION;
	FOR_ITERATOR; EMPTY_STAT; SUPER_CTOR_CALL; CTOR_CALL;
}

@members {
	private ANTLR2XML xml;

	private ErrorHandler mErrors;

	public void connectHandlers( ANTLR2XML xml, ErrorHandler errors ){
		this .xml = xml;
		xml .setParser( this );
		mErrors = errors;
	}

	protected void mainRule()
	throws RecognitionException, TokenStreamException{
		compound_stmt();
	}

	public void reportError( RecognitionException re ) {
		mErrors .parseError(re.getLine(), re.getColumn(), re.getMessage() );
	}
	
	public void reportError( String err ) {
		mErrors .parseError( 0, 0, err );
	}
	
	public void reportWarning( String err ) {
		mErrors .parseError( 0, 0, "warning: " + err );
	}
	
}

compound_stmt

	:		{
				xml .startElement( "model" );
			}
		( stmt )+
			{
				xml .endElement();
			}
	;


stmt

	:	nonblock_stmt
	|	LBRACE compound_stmt RBRACE
	;


nonblock_stmt

	:	direct_statement
	|	nested_stmt
	;


direct_statement

	:	strut_stmt
	|	rotate_stmt
	|	reflect_stmt
	|	scale_stmt
	|	build_stmt
	|	move_stmt
	|	destroy_stmt
	|   label_stmt
	;


nested_stmt

	:	from_stmt
	|	branch_stmt
	|	repeat_stmt
	|	symmetry_stmt
	|	save_stmt
	;


strut_stmt

	:
			{
				xml.startElement( "strut" );
			}
		size_expr
		( length_expr )?
		( half_size )?
		axis_expr
			{
				xml .endElement();
			}
	;


label_stmt

	:	'label' id=IDENT
			{
				xml .startElement( "label" );
				xml .attribute( "id", id.getText() );
				xml .endElement();
			}
	;


scale_stmt

	:	'scale' i=INT
			{
				xml .startElement( "scale" );
				String size = i .getText();
				if ( size .endsWith( "+" ) || size .endsWith( "-" ) )
					size = size .substring( 0, size.length()-1 );
				xml .attribute( "size", size );
			}
		( LPAREN length_expr RPAREN )?
			{
				xml .endElement();
			}
	;


build_stmt

	:	'build'
			{
				xml .startElement( "build" );
				xml .attribute( "build", "on" );
				xml .endElement();
			}
	;


destroy_stmt

	:	'destroy'
			{
				xml .startElement( "build" );
				xml .attribute( "destroy", "on" );
				xml .endElement();
			}
	;


move_stmt

	:	'move'
			{
				xml .startElement( "build" );
				xml .endElement();
			}
	;


rotate_stmt

	:	'rotate'
			{
				xml .startElement( "rotate" );
			}
		( i=INT
			{
				String size = i .getText();
				if ( size .endsWith( "+" ) || size .endsWith( "-" ) )
					size = size .substring( 0, size.length()-1 );
				xml .attribute( "steps", size );
			}
		)?
		'around' axis_expr //(SEMI)?
			{
				xml .endElement();
			}
	;


reflect_stmt

	:	'reflect'
			{
				xml .startElement( "reflect" );
			}
		reflection
			{
				xml .endElement();
			}
	;


from_stmt
	: 'from'
			{
				xml .startElement( "save" );
				xml .attribute( "state", "build" );
					xml .startElement( "model" );
						xml .startElement( "build" );
						xml .attribute( "mode", "off" );
						xml .endElement();
			}
		stmt
			{
					xml .endElement();
				xml .endElement();
			}
	;


symmetry_stmt

	:	'symmetry'
			{
				xml .startElement( "symmetry" );
			}
		( 'around'
			{
				xml .startElement( "around" );
			} 
		  axis_expr
			{
				xml .endElement();
			}

		| reflection
		)?
		stmt
			{
				xml .endElement();
			}
	;


reflection

	:   'through'
			{
				xml .startElement( "through" );
			}
			( 'center'
				{
					// leave the index attribute off
				}
			| num=INT
				{
					String axisNum = num.getText();
					if ( axisNum .startsWith( "-" ) || axisNum .startsWith( "+" ) ) {
						axisNum = axisNum .substring( 1 );
					}
					if ( axisNum .endsWith( "+" ) || axisNum .endsWith( "-" ) )
						axisNum = axisNum .substring( 0, axisNum.length()-1 );
					xml .attribute( "index", axisNum );
				}
			)
			{
				xml .endElement();
			}
	;

repeat_stmt

	:	'repeat' i=INT
			{
				xml .startElement( "repeat" );
					String axisNum = i.getText();
					if ( axisNum .startsWith( "-" ) || axisNum .startsWith( "+" ) )
						axisNum = axisNum .substring( 1 );
					if ( axisNum .endsWith( "+" ) || axisNum .endsWith( "-" ) )
						axisNum = axisNum .substring( 0, axisNum.length()-1 );
				xml .attribute( "count", axisNum );
			}
		stmt
			{
				xml .endElement();
			}
	;


branch_stmt

	:	'branch'
			{
				xml .startElement( "save" );
				xml .attribute( "state", "location" );
			}
		stmt
			{
				xml .endElement();
			}
	;


save_stmt

	:	'save'
			{
				xml .startElement( "save" );
			}
		( 'location'
			{
				xml .attribute( "state", "location" );
			}
		| 'scale'
			{
				xml .attribute( "state", "scale" );
			}
		| 'orientation'
			{
				xml .attribute( "state", "orientation" );
			}
		| 'build'
			{
				xml .attribute( "state", "build" );
			}
		| 'all'
		)
		stmt
			{
				xml .endElement();
			}
	;


half_size

	: 'half'			{ xml .attribute( "half", "true" ); }
	;



length_expr

	:  ones=INT		{ String value = ones.getText();
											if ( value .endsWith( "+" ) || value .endsWith( "-" ) )
												value = value .substring( 0, value.length()-1 );
											xml .attribute( "lengthOnes", value );
										}
				( phis=INT			{ value = phis.getText();
											if ( value .endsWith( "+" ) || value .endsWith( "-" ) )
												value = value .substring( 0, value.length()-1 );
											xml .attribute( "lengthPhis", value );
										}
				)?
	;
	

size_expr

	: 'size' ( i=INT			{
					String axisNum = i.getText();
					if ( axisNum .endsWith( "+" ) || axisNum .endsWith( "-" ) )
						axisNum = axisNum .substring( 0, axisNum.length()-1 );
									xml .attribute( "scale", axisNum );
								}
				 | id=IDENT 	{ xml .attribute( "sizeRef", id.getText() ); }
			)
	| 'short'				{ xml .attribute( "scale", Integer.toString( Constants.SHORT ) ); }
	| (() | 'medium')		{ xml .attribute( "scale", Integer.toString( Constants.MEDIUM ) ); }
	| 'long'				{ xml .attribute( "scale", Integer.toString( Constants.LONG ) ); }
	;


axis_expr

	: axis_name num=INT
		{
			String axisNum = num.getText();
	/*
			if ( axisNum .startsWith( "-" ) ) {
				xml .attribute( "sense", "minus" );
				axisNum = axisNum .substring( 1 );
			}
			else {
				xml .attribute( "sense", "plus" );
				if ( axisNum .startsWith( "+" ) )
					axisNum = axisNum .substring( 1 );
			}
			if ( axisNum .endsWith( "-" ) ) {
				axisNum = axisNum .substring( 0, axisNum.length()-1 );
				xml.attribute( "handedness", "minus" );
			}
			else if ( axisNum .endsWith( "+" ) )
				axisNum = axisNum .substring( 0, axisNum.length()-1 );
*/
			xml .attribute( "index", axisNum );
			// balancing startElement in axis_name
			xml .endElement();
		}
	;


axis_name

	: red_axis				{ xml .startElement( "red" ); }
	| blue_axis				{ xml .startElement( "blue" ); }
	| yellow_axis			{ xml .startElement( "yellow" ); }
	| 'green'				{ xml .startElement( "green" ); }
	| 'orange'				{ xml .startElement( "orange" ); }
	| 'purple'				{ xml .startElement( "purple" ); }
	| 'black'				{ xml .startElement( "black" ); }
	;


red_axis
	: 'red' | 'pent' | 'pentagon'
	;


blue_axis
	: 'blue' | 'rect' | 'rectangle'
	;


yellow_axis
	: 'yellow' | 'tri' | 'triangle'
	;


class ZomicLexer extends Lexer;

options {
	caseSensitive=false;
    testLiterals=false;    // don't automatically test for literals
    k=4;                   // four characters of lookahead
    charVocabulary='\u0003'..'\uFFFF';
}


// an identifier.  Note that testLiterals is set to true!  This means
// that after we match the rule, we look in the literals table to see
// if it's a literal or really an identifer
IDENT   
    options {testLiterals=true;}
    :   ('a'..'z'|'_'|'.') ('a'..'z'|'_'|'0'..'9'|'.')*
    ;   


LBRACE:	'{'
	;

RBRACE:	'}'
	;

LPAREN:	'('
	;

RPAREN:	')'
	;

STAR:	'*'
	;

SEMI:	';'
	;

protected
DIGIT
	:	'0'..'9'
	;

INT
	:	( '+' | '-' )? (DIGIT)+ ( '+' | '-' )? 
	;


WS	:	(' '
		|	'\t'
		|	'\f'
		|   (   '\r\n'  // Evil DOS
			|   '\r'    // Macintosh
			|   '\n'    // Unix (the right way)
			)
			{ newline(); }
		)
        { $setType(Token.SKIP); }
	;


// Single-line comments
SL_COMMENT
    :   '//'
        (~('\n'|'\r'))* ('\n'|'\r'('\n')?)
        {$setType(Token.SKIP); newline(); }
    ;   
 
// multiple-line comments
ML_COMMENT
    :   '/*'
        (   /*  '\r' '\n' can be matched in one alternative or by matching
                '\r' in one iteration and '\n' in another.  I am trying to
                handle any flavor of newline that comes in, but the language
                that allows both "\r\n" and "\r" and "\n" to all be valid
                newline is ambiguous.  Consequently, the resulting grammar
                must be ambiguous.  I'm shutting this warning off.
             */
            options {
                generateAmbigWarnings=false;
            }
        :
            { LA(2)!='/' }? '*'
        |   '\r' '\n'       {newline();}
        |   '\r'            {newline();}
        |   '\n'            {newline();}
        |   ~('*'|'\n'|'\r')
        )*
        '*/'
        {$setType(Token.SKIP);}
    ;
