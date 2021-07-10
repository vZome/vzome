

header {

package org.vorthmann.zome.zomod.parser;

import org.vorthmann.xml.ANTLR2XML;
import org.vorthmann.zome.zomic.parser.ErrorHandler;
import org.vorthmann.zome.zomod.ZomodVersion;
import org.vorthmann.zome.math.Constants;

}

class ZomodParser extends Parser;


options {
	k = 2;				// two token lookahead
	buildAST = false;	// doing event-based parsing using ANTLR2XML
}


{
	private ANTLR2XML xml;

	private ErrorHandler mErrors;
	
	private ZomodVersion mVersion;

	public void connectHandlers( ANTLR2XML xml, ErrorHandler errors, ZomodVersion version ){
		this .xml = xml;
		xml .setParser( this );
		mErrors = errors;
		mVersion = version;
	}

	protected void mainRule()
	throws RecognitionException, TokenStreamException{
		program();
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


program

	:		{
				xml .startElement( "model" );
			}
		( stmt )+
			{
				xml .endElement();
			}
	;

stmt

	: (COMMA)? ( BALL
	| FAST
	| ORIENT
	| tok:STEP
		{
			ZomodLexer.ZomodStep step = (ZomodLexer.ZomodStep) tok;
			if ( step.justMove ) {
				xml .startElement( "save" );
					xml .attribute( "state", "build" );
					xml .startElement( "model" );
						xml .startElement( "build" );
							xml .attribute( "mode", "off" );
						xml .endElement();
			}
			xml.startElement( "strut" );
				if ( step .isHalf )
					xml .attribute( "half", "true" );
				xml .attribute( "scale", Integer.toString( step.scale ) );
				xml.startElement( step.color );
					String sense = step.isPlus ? "plus" : "minus";
					xml .attribute( "sense", sense );
					int name = step.index;
					if ( step.color .equals( "green" ) )
						name = mVersion .mapGreenAxisName( name );
					xml .attribute( "index", Integer.toString( name ) );
				xml .endElement();
			xml .endElement();
			if ( step.justMove ) {
					xml .endElement();
				xml .endElement();
			}
		}
	)
	;

class ZomodLexer extends Lexer;

options {
	caseSensitive=false;
    testLiterals=false;    // don't automatically test for literals
    k=4;                   // four characters of lookahead
    charVocabulary='\u0003'..'\uFFFF';
}

{
public static class ZomodStep extends CommonToken {

    public String color;
    public boolean isPlus, justMove, isHalf;
    public int index;
    public int scale;

	public ZomodStep( String color, boolean isPlus, int index, boolean isHalf ) {
		this.color = color;
		this.isPlus = isPlus;
		this.index = index;
		this.isHalf = isHalf;
	}
}
}

COMMA : ',';

BALL : 'b';

FAST : 'f';

SLASH : '/';

ORIENT
	: 'o' axis:AXIS
	;

STEP
		{
			boolean justMove; int size;
			ZomodStep step;
		}
	: justMove=MOVE_OR_STRUT tok:AXIS size=SIZE
		{
			step = (ZomodStep) tok;
			step .justMove = justMove;
			step .scale = size;
		}
	( SLASH DIGIT { step .isHalf = true; }
	)?
		{
			step .setType( STEP );
			$setToken( step );
		}
	;

AXIS
{
	int axisNum = 0;
	boolean isPlus = false;
	String color;
}
	: color=LINE
	  axisNum=AXISNUM
	  ( '+' { isPlus = true; } | '-' { isPlus = false; } )
		{
			ZomodStep step = new ZomodStep( color, isPlus, axisNum, false );
			$setToken( step );
		}
	;

protected
AXISNUM returns [ int index ]
	: (DIGIT)+
		{ index = Integer .valueOf( $getText ) .intValue(); }
	;
	
protected
MOVE_OR_STRUT returns [ boolean justMove ]

	: 'm' { justMove = true; }
	| 's' { justMove = false; }
	;

protected
LINE returns [ String color ]

	: 'r' { color = "blue"; }
	| 'p' { color = "red"; }
	| 't' { color = "yellow"; }
	| 'd' { color = "green"; }
	;

protected
SIZE returns [ int scale ]

	: DIGIT
		{ scale = Integer .valueOf( $getText ) .intValue() + Constants.SHORT - 1; }
	;

protected
DIGIT
	:	'0'..'9'
	;

WS	:	(' '
		|	'\t'
		|	'\f'
		|   (   "\r\n"  // Evil DOS
			|   '\r'    // Macintosh
			|   '\n'    // Unix (the right way)
			)
			{ newline(); }
		)
        { $setType(Token.SKIP); }
	;


// Single-line comments
SL_COMMENT
    :   ";"
        (~('\n'|'\r'))* ('\n'|'\r'('\n')?)
        {$setType(Token.SKIP); newline(); }
    ;   
 
// multiple-line comments
ML_COMMENT
    :   "#|"
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
            { LA(2)!='#' }? '|'
        |   '\r' '\n'       {newline();}
        |   '\r'            {newline();}
        |   '\n'            {newline();}
        |   ~('|'|'\n'|'\r')
        )*
        "|#"
        {$setType(Token.SKIP);}
    ;
