// $ANTLR 2.7.2: "zomic.g" -> "ZomicParser.java"$


package com.vzome.core.zomic.parser;

import antlr.NoViableAltException;
import antlr.ParserSharedInputState;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenBuffer;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.collections.impl.BitSet;

import com.vzome.core.antlr.ANTLR2XML;
import com.vzome.core.math.symmetry.Constants;

public class ZomicParser extends antlr.LLkParser       implements ZomicParserTokenTypes
 {

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
	

protected ZomicParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public ZomicParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected ZomicParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public ZomicParser(TokenStream lexer) {
  this(lexer,2);
}

public ZomicParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
}

	public final void compound_stmt() throws RecognitionException, TokenStreamException {
		
		
		
						xml .startElement( "model" );
					
		{
		int _cnt3=0;
		_loop3:
		do {
			if ((_tokenSet_0.member(LA(1)))) {
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
	
	public final void stmt() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case LITERAL_label:
		case LITERAL_scale:
		case INT:
		case LITERAL_build:
		case LITERAL_destroy:
		case LITERAL_move:
		case LITERAL_rotate:
		case LITERAL_reflect:
		case LITERAL_from:
		case LITERAL_symmetry:
		case LITERAL_repeat:
		case LITERAL_branch:
		case LITERAL_save:
		case LITERAL_half:
		case LITERAL_size:
		case LITERAL_short:
		case LITERAL_medium:
		case LITERAL_long:
		case LITERAL_green:
		case LITERAL_orange:
		case LITERAL_purple:
		case LITERAL_black:
		case LITERAL_red:
		case LITERAL_pent:
		case LITERAL_pentagon:
		case LITERAL_blue:
		case LITERAL_rect:
		case LITERAL_rectangle:
		case LITERAL_yellow:
		case LITERAL_tri:
		case LITERAL_triangle:
		{
			nonblock_stmt();
			break;
		}
		case LBRACE:
		{
			match(LBRACE);
			compound_stmt();
			match(RBRACE);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void nonblock_stmt() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case LITERAL_label:
		case LITERAL_scale:
		case INT:
		case LITERAL_build:
		case LITERAL_destroy:
		case LITERAL_move:
		case LITERAL_rotate:
		case LITERAL_reflect:
		case LITERAL_half:
		case LITERAL_size:
		case LITERAL_short:
		case LITERAL_medium:
		case LITERAL_long:
		case LITERAL_green:
		case LITERAL_orange:
		case LITERAL_purple:
		case LITERAL_black:
		case LITERAL_red:
		case LITERAL_pent:
		case LITERAL_pentagon:
		case LITERAL_blue:
		case LITERAL_rect:
		case LITERAL_rectangle:
		case LITERAL_yellow:
		case LITERAL_tri:
		case LITERAL_triangle:
		{
			direct_statement();
			break;
		}
		case LITERAL_from:
		case LITERAL_symmetry:
		case LITERAL_repeat:
		case LITERAL_branch:
		case LITERAL_save:
		{
			nested_stmt();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void direct_statement() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case INT:
		case LITERAL_half:
		case LITERAL_size:
		case LITERAL_short:
		case LITERAL_medium:
		case LITERAL_long:
		case LITERAL_green:
		case LITERAL_orange:
		case LITERAL_purple:
		case LITERAL_black:
		case LITERAL_red:
		case LITERAL_pent:
		case LITERAL_pentagon:
		case LITERAL_blue:
		case LITERAL_rect:
		case LITERAL_rectangle:
		case LITERAL_yellow:
		case LITERAL_tri:
		case LITERAL_triangle:
		{
			strut_stmt();
			break;
		}
		case LITERAL_rotate:
		{
			rotate_stmt();
			break;
		}
		case LITERAL_reflect:
		{
			reflect_stmt();
			break;
		}
		case LITERAL_scale:
		{
			scale_stmt();
			break;
		}
		case LITERAL_build:
		{
			build_stmt();
			break;
		}
		case LITERAL_move:
		{
			move_stmt();
			break;
		}
		case LITERAL_destroy:
		{
			destroy_stmt();
			break;
		}
		case LITERAL_label:
		{
			label_stmt();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void nested_stmt() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case LITERAL_from:
		{
			from_stmt();
			break;
		}
		case LITERAL_branch:
		{
			branch_stmt();
			break;
		}
		case LITERAL_repeat:
		{
			repeat_stmt();
			break;
		}
		case LITERAL_symmetry:
		{
			symmetry_stmt();
			break;
		}
		case LITERAL_save:
		{
			save_stmt();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void strut_stmt() throws RecognitionException, TokenStreamException {
		
		
		
						xml.startElement( "strut" );
					
		size_expr();
		{
		switch ( LA(1)) {
		case INT:
		{
			length_expr();
			break;
		}
		case LITERAL_half:
		case LITERAL_green:
		case LITERAL_orange:
		case LITERAL_purple:
		case LITERAL_black:
		case LITERAL_red:
		case LITERAL_pent:
		case LITERAL_pentagon:
		case LITERAL_blue:
		case LITERAL_rect:
		case LITERAL_rectangle:
		case LITERAL_yellow:
		case LITERAL_tri:
		case LITERAL_triangle:
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
		case LITERAL_half:
		{
			half_size();
			break;
		}
		case LITERAL_green:
		case LITERAL_orange:
		case LITERAL_purple:
		case LITERAL_black:
		case LITERAL_red:
		case LITERAL_pent:
		case LITERAL_pentagon:
		case LITERAL_blue:
		case LITERAL_rect:
		case LITERAL_rectangle:
		case LITERAL_yellow:
		case LITERAL_tri:
		case LITERAL_triangle:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		axis_expr();
		
						xml .endElement();
					
	}
	
	public final void rotate_stmt() throws RecognitionException, TokenStreamException {
		
		Token  i = null;
		
		match(LITERAL_rotate);
		
						xml .startElement( "rotate" );
					
		{
		switch ( LA(1)) {
		case INT:
		{
			i = LT(1);
			match(INT);
			
							String size = i .getText();
							if ( size .endsWith( "+" ) || size .endsWith( "-" ) )
								size = size .substring( 0, size.length()-1 );
							xml .attribute( "steps", size );
						
			break;
		}
		case LITERAL_around:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		match(LITERAL_around);
		axis_expr();
		
						xml .endElement();
					
	}
	
	public final void reflect_stmt() throws RecognitionException, TokenStreamException {
		
		
		match(LITERAL_reflect);
		
						xml .startElement( "reflect" );
					
		reflection();
		
						xml .endElement();
					
	}
	
	public final void scale_stmt() throws RecognitionException, TokenStreamException {
		
		Token  i = null;
		
		match(LITERAL_scale);
		i = LT(1);
		match(INT);
		
						xml .startElement( "scale" );
						String size = i .getText();
						if ( size .endsWith( "+" ) || size .endsWith( "-" ) )
							size = size .substring( 0, size.length()-1 );
						xml .attribute( "size", size );
					
		{
		switch ( LA(1)) {
		case LPAREN:
		{
			match(LPAREN);
			length_expr();
			match(RPAREN);
			break;
		}
		case LBRACE:
		case RBRACE:
		case LITERAL_label:
		case LITERAL_scale:
		case INT:
		case LITERAL_build:
		case LITERAL_destroy:
		case LITERAL_move:
		case LITERAL_rotate:
		case LITERAL_reflect:
		case LITERAL_from:
		case LITERAL_symmetry:
		case LITERAL_repeat:
		case LITERAL_branch:
		case LITERAL_save:
		case LITERAL_half:
		case LITERAL_size:
		case LITERAL_short:
		case LITERAL_medium:
		case LITERAL_long:
		case LITERAL_green:
		case LITERAL_orange:
		case LITERAL_purple:
		case LITERAL_black:
		case LITERAL_red:
		case LITERAL_pent:
		case LITERAL_pentagon:
		case LITERAL_blue:
		case LITERAL_rect:
		case LITERAL_rectangle:
		case LITERAL_yellow:
		case LITERAL_tri:
		case LITERAL_triangle:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		
						xml .endElement();
					
	}
	
	public final void build_stmt() throws RecognitionException, TokenStreamException {
		
		
		match(LITERAL_build);
		
						xml .startElement( "build" );
						xml .attribute( "build", "on" );
						xml .endElement();
					
	}
	
	public final void move_stmt() throws RecognitionException, TokenStreamException {
		
		
		match(LITERAL_move);
		
						xml .startElement( "build" );
						xml .endElement();
					
	}
	
	public final void destroy_stmt() throws RecognitionException, TokenStreamException {
		
		
		match(LITERAL_destroy);
		
						xml .startElement( "build" );
						xml .attribute( "destroy", "on" );
						xml .endElement();
					
	}
	
	public final void label_stmt() throws RecognitionException, TokenStreamException {
		
		Token  id = null;
		
		match(LITERAL_label);
		id = LT(1);
		match(IDENT);
		
						xml .startElement( "label" );
						xml .attribute( "id", id.getText() );
						xml .endElement();
					
	}
	
	public final void from_stmt() throws RecognitionException, TokenStreamException {
		
		
		match(LITERAL_from);
		
						xml .startElement( "save" );
						xml .attribute( "state", "build" );
							xml .startElement( "model" );
								xml .startElement( "build" );
								xml .attribute( "mode", "off" );
								xml .endElement();
					
		stmt();
		
							xml .endElement();
						xml .endElement();
					
	}
	
	public final void branch_stmt() throws RecognitionException, TokenStreamException {
		
		
		match(LITERAL_branch);
		
						xml .startElement( "save" );
						xml .attribute( "state", "location" );
					
		stmt();
		
						xml .endElement();
					
	}
	
	public final void repeat_stmt() throws RecognitionException, TokenStreamException {
		
		Token  i = null;
		
		match(LITERAL_repeat);
		i = LT(1);
		match(INT);
		
						xml .startElement( "repeat" );
							String axisNum = i.getText();
							if ( axisNum .startsWith( "-" ) || axisNum .startsWith( "+" ) )
								axisNum = axisNum .substring( 1 );
							if ( axisNum .endsWith( "+" ) || axisNum .endsWith( "-" ) )
								axisNum = axisNum .substring( 0, axisNum.length()-1 );
						xml .attribute( "count", axisNum );
					
		stmt();
		
						xml .endElement();
					
	}
	
	public final void symmetry_stmt() throws RecognitionException, TokenStreamException {
		
		
		match(LITERAL_symmetry);
		
						xml .startElement( "symmetry" );
					
		{
		switch ( LA(1)) {
		case LITERAL_around:
		{
			match(LITERAL_around);
			
							xml .startElement( "around" );
						
			axis_expr();
			
							xml .endElement();
						
			break;
		}
		case LITERAL_through:
		{
			reflection();
			break;
		}
		case LBRACE:
		case LITERAL_label:
		case LITERAL_scale:
		case INT:
		case LITERAL_build:
		case LITERAL_destroy:
		case LITERAL_move:
		case LITERAL_rotate:
		case LITERAL_reflect:
		case LITERAL_from:
		case LITERAL_symmetry:
		case LITERAL_repeat:
		case LITERAL_branch:
		case LITERAL_save:
		case LITERAL_half:
		case LITERAL_size:
		case LITERAL_short:
		case LITERAL_medium:
		case LITERAL_long:
		case LITERAL_green:
		case LITERAL_orange:
		case LITERAL_purple:
		case LITERAL_black:
		case LITERAL_red:
		case LITERAL_pent:
		case LITERAL_pentagon:
		case LITERAL_blue:
		case LITERAL_rect:
		case LITERAL_rectangle:
		case LITERAL_yellow:
		case LITERAL_tri:
		case LITERAL_triangle:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		stmt();
		
						xml .endElement();
					
	}
	
	public final void save_stmt() throws RecognitionException, TokenStreamException {
		
		
		match(LITERAL_save);
		
						xml .startElement( "save" );
					
		{
		switch ( LA(1)) {
		case LITERAL_location:
		{
			match(LITERAL_location);
			
							xml .attribute( "state", "location" );
						
			break;
		}
		case LITERAL_scale:
		{
			match(LITERAL_scale);
			
							xml .attribute( "state", "scale" );
						
			break;
		}
		case LITERAL_orientation:
		{
			match(LITERAL_orientation);
			
							xml .attribute( "state", "orientation" );
						
			break;
		}
		case LITERAL_build:
		{
			match(LITERAL_build);
			
							xml .attribute( "state", "build" );
						
			break;
		}
		case LITERAL_all:
		{
			match(LITERAL_all);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		stmt();
		
						xml .endElement();
					
	}
	
	public final void size_expr() throws RecognitionException, TokenStreamException {
		
		Token  i = null;
		Token  id = null;
		
		switch ( LA(1)) {
		case LITERAL_size:
		{
			match(LITERAL_size);
			{
			switch ( LA(1)) {
			case INT:
			{
				i = LT(1);
				match(INT);
				
									String axisNum = i.getText();
									if ( axisNum .endsWith( "+" ) || axisNum .endsWith( "-" ) )
										axisNum = axisNum .substring( 0, axisNum.length()-1 );
													xml .attribute( "scale", axisNum );
												
				break;
			}
			case IDENT:
			{
				id = LT(1);
				match(IDENT);
				xml .attribute( "sizeRef", id.getText() );
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			break;
		}
		case LITERAL_short:
		{
			match(LITERAL_short);
			xml .attribute( "scale", Integer.toString( Constants.SHORT ) );
			break;
		}
		case INT:
		case LITERAL_half:
		case LITERAL_medium:
		case LITERAL_green:
		case LITERAL_orange:
		case LITERAL_purple:
		case LITERAL_black:
		case LITERAL_red:
		case LITERAL_pent:
		case LITERAL_pentagon:
		case LITERAL_blue:
		case LITERAL_rect:
		case LITERAL_rectangle:
		case LITERAL_yellow:
		case LITERAL_tri:
		case LITERAL_triangle:
		{
			{
			switch ( LA(1)) {
			case INT:
			case LITERAL_half:
			case LITERAL_green:
			case LITERAL_orange:
			case LITERAL_purple:
			case LITERAL_black:
			case LITERAL_red:
			case LITERAL_pent:
			case LITERAL_pentagon:
			case LITERAL_blue:
			case LITERAL_rect:
			case LITERAL_rectangle:
			case LITERAL_yellow:
			case LITERAL_tri:
			case LITERAL_triangle:
			{
				{
				}
				break;
			}
			case LITERAL_medium:
			{
				match(LITERAL_medium);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			xml .attribute( "scale", Integer.toString( Constants.MEDIUM ) );
			break;
		}
		case LITERAL_long:
		{
			match(LITERAL_long);
			xml .attribute( "scale", Integer.toString( Constants.LONG ) );
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void length_expr() throws RecognitionException, TokenStreamException {
		
		Token  ones = null;
		Token  phis = null;
		
		ones = LT(1);
		match(INT);
		String value = ones.getText();
													if ( value .endsWith( "+" ) || value .endsWith( "-" ) )
														value = value .substring( 0, value.length()-1 );
													xml .attribute( "lengthOnes", value );
												
		{
		switch ( LA(1)) {
		case INT:
		{
			phis = LT(1);
			match(INT);
			value = phis.getText();
														if ( value .endsWith( "+" ) || value .endsWith( "-" ) )
															value = value .substring( 0, value.length()-1 );
														xml .attribute( "lengthPhis", value );
													
			break;
		}
		case RPAREN:
		case LITERAL_half:
		case LITERAL_green:
		case LITERAL_orange:
		case LITERAL_purple:
		case LITERAL_black:
		case LITERAL_red:
		case LITERAL_pent:
		case LITERAL_pentagon:
		case LITERAL_blue:
		case LITERAL_rect:
		case LITERAL_rectangle:
		case LITERAL_yellow:
		case LITERAL_tri:
		case LITERAL_triangle:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void half_size() throws RecognitionException, TokenStreamException {
		
		
		match(LITERAL_half);
		xml .attribute( "half", "true" );
	}
	
	public final void axis_expr() throws RecognitionException, TokenStreamException {
		
		Token  num = null;
		
		axis_name();
		num = LT(1);
		match(INT);
		
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
	
	public final void reflection() throws RecognitionException, TokenStreamException {
		
		Token  num = null;
		
		match(LITERAL_through);
		
						xml .startElement( "through" );
					
		{
		switch ( LA(1)) {
		case LITERAL_center:
		{
			match(LITERAL_center);
			
								// leave the index attribute off
							
			break;
		}
		case INT:
		{
			num = LT(1);
			match(INT);
			
								String axisNum = num.getText();
								if ( axisNum .startsWith( "-" ) || axisNum .startsWith( "+" ) ) {
									axisNum = axisNum .substring( 1 );
								}
								if ( axisNum .endsWith( "+" ) || axisNum .endsWith( "-" ) )
									axisNum = axisNum .substring( 0, axisNum.length()-1 );
								xml .attribute( "index", axisNum );
							
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		
						xml .endElement();
					
	}
	
	public final void axis_name() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case LITERAL_red:
		case LITERAL_pent:
		case LITERAL_pentagon:
		{
			red_axis();
			xml .startElement( "red" );
			break;
		}
		case LITERAL_blue:
		case LITERAL_rect:
		case LITERAL_rectangle:
		{
			blue_axis();
			xml .startElement( "blue" );
			break;
		}
		case LITERAL_yellow:
		case LITERAL_tri:
		case LITERAL_triangle:
		{
			yellow_axis();
			xml .startElement( "yellow" );
			break;
		}
		case LITERAL_green:
		{
			match(LITERAL_green);
			xml .startElement( "green" );
			break;
		}
		case LITERAL_orange:
		{
			match(LITERAL_orange);
			xml .startElement( "orange" );
			break;
		}
		case LITERAL_purple:
		{
			match(LITERAL_purple);
			xml .startElement( "purple" );
			break;
		}
		case LITERAL_black:
		{
			match(LITERAL_black);
			xml .startElement( "black" );
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void red_axis() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case LITERAL_red:
		{
			match(LITERAL_red);
			break;
		}
		case LITERAL_pent:
		{
			match(LITERAL_pent);
			break;
		}
		case LITERAL_pentagon:
		{
			match(LITERAL_pentagon);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void blue_axis() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case LITERAL_blue:
		{
			match(LITERAL_blue);
			break;
		}
		case LITERAL_rect:
		{
			match(LITERAL_rect);
			break;
		}
		case LITERAL_rectangle:
		{
			match(LITERAL_rectangle);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void yellow_axis() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case LITERAL_yellow:
		{
			match(LITERAL_yellow);
			break;
		}
		case LITERAL_tri:
		{
			match(LITERAL_tri);
			break;
		}
		case LITERAL_triangle:
		{
			match(LITERAL_triangle);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"LBRACE",
		"RBRACE",
		"\"label\"",
		"IDENT",
		"\"scale\"",
		"INT",
		"LPAREN",
		"RPAREN",
		"\"build\"",
		"\"destroy\"",
		"\"move\"",
		"\"rotate\"",
		"\"around\"",
		"\"reflect\"",
		"\"from\"",
		"\"symmetry\"",
		"\"through\"",
		"\"center\"",
		"\"repeat\"",
		"\"branch\"",
		"\"save\"",
		"\"location\"",
		"\"orientation\"",
		"\"all\"",
		"\"half\"",
		"\"size\"",
		"\"short\"",
		"\"medium\"",
		"\"long\"",
		"\"green\"",
		"\"orange\"",
		"\"purple\"",
		"\"black\"",
		"\"red\"",
		"\"pent\"",
		"\"pentagon\"",
		"\"blue\"",
		"\"rect\"",
		"\"rectangle\"",
		"\"yellow\"",
		"\"tri\"",
		"\"triangle\"",
		"STAR",
		"SEMI",
		"DIGIT",
		"WS",
		"SL_COMMENT",
		"ML_COMMENT"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 70368506082128L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	
	}
