// $ANTLR 2.7.2: "zomod.g" -> "ZomodLexer.java"$


package com.vzome.core.zomod.parser;

import java.io.InputStream;
import java.io.Reader;
import java.util.Hashtable;

import com.vzome.core.math.symmetry.Constants;

import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.CommonToken;
import antlr.InputBuffer;
import antlr.LexerSharedInputState;
import antlr.NoViableAltForCharException;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.collections.impl.BitSet;

public class ZomodLexer extends antlr.CharScanner implements ZomodParserTokenTypes, TokenStream
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
public ZomodLexer(InputStream in) {
	this(new ByteBuffer(in));
}
public ZomodLexer(Reader in) {
	this(new CharBuffer(in));
}
public ZomodLexer(InputBuffer ib) {
	this(new LexerSharedInputState(ib));
}
public ZomodLexer(LexerSharedInputState state) {
	super(state);
	caseSensitiveLiterals = true;
	setCaseSensitive(false);
	literals = new Hashtable();
}

public Token nextToken() throws TokenStreamException {
	Token theRetToken=null;
tryAgain:
	for (;;) {
		Token _token = null;
		int _ttype = Token.INVALID_TYPE;
		resetText();
		try {   // for char stream error handling
			try {   // for lexical error handling
				switch ( LA(1)) {
				case ',':
				{
					mCOMMA(true);
					theRetToken=_returnToken;
					break;
				}
				case 'b':
				{
					mBALL(true);
					theRetToken=_returnToken;
					break;
				}
				case 'f':
				{
					mFAST(true);
					theRetToken=_returnToken;
					break;
				}
				case '/':
				{
					mSLASH(true);
					theRetToken=_returnToken;
					break;
				}
				case 'o':
				{
					mORIENT(true);
					theRetToken=_returnToken;
					break;
				}
				case 'd':  case 'p':  case 'r':  case 't':
				{
					mAXIS(true);
					theRetToken=_returnToken;
					break;
				}
				case 'm':  case 's':
				{
					mSTEP(true);
					theRetToken=_returnToken;
					break;
				}
				case '\t':  case '\n':  case '\u000c':  case '\r':
				case ' ':
				{
					mWS(true);
					theRetToken=_returnToken;
					break;
				}
				case ';':
				{
					mSL_COMMENT(true);
					theRetToken=_returnToken;
					break;
				}
				case '#':
				{
					mML_COMMENT(true);
					theRetToken=_returnToken;
					break;
				}
				default:
				{
					if (LA(1)==EOF_CHAR) {uponEOF(); _returnToken = makeToken(Token.EOF_TYPE);}
				else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				}
				if ( _returnToken==null ) continue tryAgain; // found SKIP token
				_ttype = _returnToken.getType();
				_returnToken.setType(_ttype);
				return _returnToken;
			}
			catch (RecognitionException e) {
				throw new TokenStreamRecognitionException(e);
			}
		}
		catch (CharStreamException cse) {
			if ( cse instanceof CharStreamIOException ) {
				throw new TokenStreamIOException(((CharStreamIOException)cse).io);
			}
			else {
				throw new TokenStreamException(cse.getMessage());
			}
		}
	}
}

	public final void mCOMMA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = COMMA;
		int _saveIndex;
		
		match(',');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mBALL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = BALL;
		int _saveIndex;
		
		match('b');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mFAST(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = FAST;
		int _saveIndex;
		
		match('f');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSLASH(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SLASH;
		int _saveIndex;
		
		match('/');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mORIENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ORIENT;
		int _saveIndex;
		Token axis=null;
		
		match('o');
		mAXIS(true);
		axis=_returnToken;
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mAXIS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = AXIS;
		int _saveIndex;
		
			int axisNum = 0;
			boolean isPlus = false;
			String color;
		
		
		color=mLINE(false);
		axisNum=mAXISNUM(false);
		{
		switch ( LA(1)) {
		case '+':
		{
			match('+');
			isPlus = true;
			break;
		}
		case '-':
		{
			match('-');
			isPlus = false;
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		
					ZomodStep step = new ZomodStep( color, isPlus, axisNum, false );
					_token =  step;
				
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSTEP(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = STEP;
		int _saveIndex;
		Token tok=null;
		
					boolean justMove; int size;
					ZomodStep step;
				
		
		justMove=mMOVE_OR_STRUT(false);
		mAXIS(true);
		tok=_returnToken;
		size=mSIZE(false);
		
					step = (ZomodStep) tok;
					step .justMove = justMove;
					step .scale = size;
				
		{
		if ((LA(1)=='/')) {
			mSLASH(false);
			mDIGIT(false);
			step .isHalf = true;
		}
		else {
		}
		
		}
		
					step .setType( STEP );
					_token =  step;
				
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final  boolean  mMOVE_OR_STRUT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		 boolean justMove ;
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MOVE_OR_STRUT;
		int _saveIndex;
		
		switch ( LA(1)) {
		case 'm':
		{
			match('m');
			justMove = true;
			break;
		}
		case 's':
		{
			match('s');
			justMove = false;
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
		return justMove ;
	}
	
	protected final  int  mSIZE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		 int scale ;
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SIZE;
		int _saveIndex;
		
		mDIGIT(false);
		scale = Integer .valueOf( new String(text.getBuffer(),_begin,text.length()-_begin) ) .intValue() + Constants.SHORT - 1;
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
		return scale ;
	}
	
	protected final void mDIGIT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DIGIT;
		int _saveIndex;
		
		matchRange('0','9');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final  String  mLINE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		 String color ;
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LINE;
		int _saveIndex;
		
		switch ( LA(1)) {
		case 'r':
		{
			match('r');
			color = "blue";
			break;
		}
		case 'p':
		{
			match('p');
			color = "red";
			break;
		}
		case 't':
		{
			match('t');
			color = "yellow";
			break;
		}
		case 'd':
		{
			match('d');
			color = "green";
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
		return color ;
	}
	
	protected final  int  mAXISNUM(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		 int index ;
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = AXISNUM;
		int _saveIndex;
		
		{
		int _cnt18=0;
		_loop18:
		do {
			if (((LA(1) >= '0' && LA(1) <= '9'))) {
				mDIGIT(false);
			}
			else {
				if ( _cnt18>=1 ) { break _loop18; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			
			_cnt18++;
		} while (true);
		}
		index = Integer .valueOf( new String(text.getBuffer(),_begin,text.length()-_begin) ) .intValue();
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
		return index ;
	}
	
	public final void mWS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = WS;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case ' ':
		{
			match(' ');
			break;
		}
		case '\t':
		{
			match('\t');
			break;
		}
		case '\u000c':
		{
			match('\f');
			break;
		}
		case '\n':  case '\r':
		{
			{
			if ((LA(1)=='\r') && (LA(2)=='\n')) {
				match("\r\n");
			}
			else if ((LA(1)=='\r') && (true)) {
				match('\r');
			}
			else if ((LA(1)=='\n')) {
				match('\n');
			}
			else {
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			
			}
			newline();
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		_ttype = Token.SKIP;
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSL_COMMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SL_COMMENT;
		int _saveIndex;
		
		match(";");
		{
		_loop29:
		do {
			if ((_tokenSet_0.member(LA(1)))) {
				{
				match(_tokenSet_0);
				}
			}
			else {
				break _loop29;
			}
			
		} while (true);
		}
		{
		switch ( LA(1)) {
		case '\n':
		{
			match('\n');
			break;
		}
		case '\r':
		{
			match('\r');
			{
			if ((LA(1)=='\n')) {
				match('\n');
			}
			else {
			}
			
			}
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		_ttype = Token.SKIP; newline();
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mML_COMMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ML_COMMENT;
		int _saveIndex;
		
		match("#|");
		{
		_loop35:
		do {
			if ((LA(1)=='\r') && (LA(2)=='\n') && ((LA(3) >= '\u0003' && LA(3) <= '\uffff')) && ((LA(4) >= '\u0003' && LA(4) <= '\uffff'))) {
				match('\r');
				match('\n');
				newline();
			}
			else if (((LA(1)=='|') && ((LA(2) >= '\u0003' && LA(2) <= '\uffff')) && ((LA(3) >= '\u0003' && LA(3) <= '\uffff')))&&( LA(2)!='#' )) {
				match('|');
			}
			else if ((LA(1)=='\r') && ((LA(2) >= '\u0003' && LA(2) <= '\uffff')) && ((LA(3) >= '\u0003' && LA(3) <= '\uffff')) && (true)) {
				match('\r');
				newline();
			}
			else if ((LA(1)=='\n')) {
				match('\n');
				newline();
			}
			else if ((_tokenSet_1.member(LA(1)))) {
				{
				match(_tokenSet_1);
				}
			}
			else {
				break _loop35;
			}
			
		} while (true);
		}
		match("|#");
		_ttype = Token.SKIP;
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	
	private static final long[] mk_tokenSet_0() {
		long[] data = new long[2048];
		data[0]=-9224L;
		for (int i = 1; i<=1023; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = new long[2048];
		data[0]=-9224L;
		data[1]=-1152921504606846977L;
		for (int i = 2; i<=1023; i++) { data[i]=-1L; }
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	
	}
