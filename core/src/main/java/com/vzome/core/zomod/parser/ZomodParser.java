// $ANTLR 2.7.2: "zomod.g" -> "ZomodParser.java"$


package com.vzome.core.zomod.parser;

import com.vzome.core.zomic.parser.ErrorHandler;

import antlr.NoViableAltException;
import antlr.ParserSharedInputState;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenBuffer;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.collections.impl.BitSet;

@SuppressWarnings( "deprecation" )
public class ZomodParser extends antlr.LLkParser       implements ZomodParserTokenTypes
 {

	private com.vzome.core.antlr.ANTLR2XML xml;

	private ErrorHandler mErrors;
	
	private ZomodVersion mVersion;

	public void connectHandlers( com.vzome.core.antlr.ANTLR2XML xml, ErrorHandler errors, ZomodVersion version ){
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
	

protected ZomodParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public ZomodParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected ZomodParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public ZomodParser(TokenStream lexer) {
  this(lexer,2);
}

public ZomodParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
}

	public final void program() throws RecognitionException, TokenStreamException {
		
		
		try {      // for error handling
			
							xml .startElement( "model" );
						
			{
			int _cnt3=0;
			_loop3:
			do {
				if (((LA(1) >= COMMA && LA(1) <= STEP))) {
					stmt();
				}
				else {
					if ( _cnt3>=1 ) { break _loop3; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt3++;
			} while (true);
			}
			
							xml .endElement();
						
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_0);
		}
	}
	
	public final void stmt() throws RecognitionException, TokenStreamException {
		
		Token  tok = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case COMMA:
			{
				match(COMMA);
				break;
			}
			case BALL:
			case FAST:
			case ORIENT:
			case STEP:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case BALL:
			{
				match(BALL);
				break;
			}
			case FAST:
			{
				match(FAST);
				break;
			}
			case ORIENT:
			{
				match(ORIENT);
				break;
			}
			case STEP:
			{
				tok = LT(1);
				match(STEP);
				
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
						
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_1);
		}
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"COMMA",
		"BALL",
		"FAST",
		"ORIENT",
		"STEP",
		"SLASH",
		"AXIS",
		"AXISNUM",
		"MOVE_OR_STRUT",
		"LINE",
		"SIZE",
		"DIGIT",
		"WS",
		"SL_COMMENT",
		"ML_COMMENT"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 498L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	
	}
