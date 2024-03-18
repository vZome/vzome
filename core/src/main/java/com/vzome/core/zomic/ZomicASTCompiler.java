package com.vzome.core.zomic;

import static java.lang.Math.abs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.vzome.core.antlr.generated.ZomicLexer;
import com.vzome.core.antlr.generated.ZomicParser;
import com.vzome.core.antlr.generated.ZomicParserBaseListener;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.render.ZomicEventHandler;
import com.vzome.core.zomic.parser.ErrorHandler;
import com.vzome.core.zomic.program.Build;
import com.vzome.core.zomic.program.Label;
import com.vzome.core.zomic.program.Repeat;
import com.vzome.core.zomic.program.Save;
import com.vzome.core.zomic.program.Symmetry;
import com.vzome.core.zomic.program.Untranslatable;
import com.vzome.core.zomic.program.Walk;

/**
 * Created by David Hall on 3/20/2015.
 */
public class ZomicASTCompiler
    extends ZomicParserBaseListener
{
    private ZomicCompilerState state;
    
	public static final String loggerClassName = new Throwable().getStackTrace()[0].getClassName();
    private static final Logger logger = Logger .getLogger( loggerClassName );
	// verboseLoggingLevel is the level at which the logger for this class will generate the majority of its output.
	public static final Level verboseLoggingLevel = Level.FINER; // corresponds to entering() and exiting() which use Level.FINER
	
	public static boolean logging() {
		return logger.isLoggable(verboseLoggingLevel);
	}
	
    public ZomicASTCompiler( IcosahedralSymmetry icosaSymm ) {
        this.state = new ZomicCompilerState( icosaSymm );
    }

	public static Walk compile( File file, IcosahedralSymmetry symm ) {
		String fileName = file.getAbsolutePath();
		Walk program = null;
		ErrorHandler.Default errors = new ErrorHandler.Default();
		ZomicASTCompiler compiler = new ZomicASTCompiler(symm );
		try {
			CharStream fileStream = CharStreams.fromFileName(fileName);
			program = compiler.compile( fileStream, errors );
			if( program != null ) {
				program.setErrors( errors.getErrors() );
			}
		} catch (IOException ex) {
			errors.parseError( ErrorHandler.UNKNOWN, ErrorHandler.UNKNOWN, ex.getMessage() );
		}
        return program;
    }
	
	public static Walk compile( CharStream input, IcosahedralSymmetry symm ) {
		ErrorHandler.Default errors = new ErrorHandler.Default();
		ZomicASTCompiler compiler = new ZomicASTCompiler(symm );
        Walk program = compiler.compile( input, errors );
		if( program != null ) {
			program.setErrors( errors.getErrors() );
		}
        return program;
    }
	
	public static Walk compile( String input, IcosahedralSymmetry symm ) {
		return compile( CharStreams.fromString( input ), symm );
	}
	
	public static Walk compile(InputStream input, IcosahedralSymmetry symm) {
		try {
			return compile(CharStreams.fromStream(input), symm);
		} catch (IOException e) {
			Walk program = new Walk();
			program.setErrors(new String[] {"Unable to compile: " + e.toString()});
			return program;
		}
	}

	public Walk compile( String input, ErrorHandler errors ) {
		return compile( CharStreams.fromString( input ), errors );
	}
	
	public Walk compile( CharStream input, ErrorHandler errors ) {
        try  {
            return compile( input );
        } catch (ParseCancellationException ex) {
			int line = ErrorHandler.UNKNOWN;
			int column = ErrorHandler.UNKNOWN;
			String msg = "Parser Cancelled.";
			Throwable cause = ex.getCause();
			if( cause instanceof InputMismatchException ) {
				InputMismatchException immEx = (InputMismatchException) cause;
				Token offender = immEx.getOffendingToken();
				if( offender != null ) {
					line = offender.getLine();
					column = offender.getCharPositionInLine();
					String txt = offender.getText();
					if(txt != null) {
						msg = " Unexpected Token '" + txt + "'.";
					}
				}
			}
            errors.parseError( line, column, msg );
        }		
        return this .state .getProgram();
    }

	protected void reset() {
		tabs = 0; 
		this.state .reset();
	}
	
	@SuppressWarnings("serial")
	private static class RuntimeWrapperException 
	extends RuntimeException 
	{
		RuntimeWrapperException(LexerNoViableAltException ex) {
			this.initCause(ex);
		}
	}
	
	public static class StrictZomicLexer 
	extends ZomicLexer 
	{
		public StrictZomicLexer(CharStream input) { super(input); }
		@Override
		public void recover(LexerNoViableAltException e) {
			// Bail out of the lexer at the first lexical error instead of trying to recover.
			// Use this in conjunction with BailErrorStrategy.
			// Wrap the LexerNoViableAltException in a RuntimeWrapperException
			// to be sure the lexer doesn't handle the LexerNoViableAltException.
			// The LexerNoViableAltException will be extracted from the RuntimeWrapperException
			// inside the compile() method below and re-thrown without the RuntimeWrapperException
			// since we'll be outside of the lexer rules at that point.
			throw new RuntimeWrapperException(e); 
		}
	}
	
	protected ZomicParser parser;
	protected Walk compile( CharStream inputStream )
		throws ParseCancellationException // thrown by BailErrorStrategy
	{
		try {
			reset(); // in case a single instance compiles more than one program
			// feed input to lexer (either a ZomicLexer or, preferably a StrictZomicLexer)
			ZomicLexer lexer = new StrictZomicLexer( inputStream );
			// get a stream of matched tokens
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			// pass tokens to the parser
			parser = new ZomicParser( tokens );

			if(lexer instanceof StrictZomicLexer) {
			// Use this only in conjunction with the StrictZomicLexer class
			// bail out of the parser upon the first syntax error 
			// instead of using the default error strategy which tries to recover if possible.
				parser.setErrorHandler(new BailErrorStrategy());
			}

			// specify our entry point (top level rule)
			ZomicParser.ProgramContext program = parser.program(); // parse

			// Use the DEFAULT walker to walk from the entry point with this listener attached.
			// In the process, the enter and exit methods of this class will be invoked to populate the statements collection.
			try {
				ParseTreeWalker.DEFAULT.walk(this, program);
			}
			catch (IllegalStateException ex ) {
				logger.log(Level.WARNING, ex.getMessage(), ex);
				// InputMismatchException will give the user an error message with line info from the parser
				InputMismatchException imex = new InputMismatchException(parser);
				logger.log(Level.INFO, imex.getMessage(), imex);
				throw imex;
			}
		}
		catch( RuntimeWrapperException ex ) {
			// unwrap the LexerNoViableAltException from the RuntimeWrapperException 
			// and rethrow it as described in StrictZomicLexer.recover()
			throw (LexerNoViableAltException) ex.getCause();
		}
		// Now we return the statement(s) collected by the listener.
        return this .state .getProgram();
	}
	
	protected static int parseInt( Token token ) {
		return Integer.parseInt( token.getText() );
	}

	private int tabs = 0;
	private String getTabs(boolean add) 	{
		if(logging()) { 
			StringBuilder sb = new StringBuilder("");
			if(!add) { tabs--; }
			for( int i = 0; i < tabs; i++)
			{
				sb.append("   ");
			}
			if(add) { tabs++; }
			return sb.toString();
		}
		return "";
	}

	protected void logContext(ParserRuleContext ctx, boolean isEntering) 	{ 
		if(logging()) { 
			String contextName = ctx.getClass().getSimpleName();
			String strContext = "Context";
			if(contextName.endsWith(strContext)) {
				// strip "Context" from the name for readability
				contextName = contextName.substring(0, contextName.length() - strContext.length());
			}
			log(getTabs(isEntering) + (isEntering ? "--> " : "<-- ") + contextName);
		}
	}

	protected static void log(String msg) {
		if(msg != null) {
			logger.log(verboseLoggingLevel, msg);
		}
	}

/* 
**********************************
* BEGIN Overriding Event Handlers 
**********************************
*/
	
	@Override public void enterProgram(ZomicParser.ProgramContext ctx) { 
		this .state .prepareStatement( new Walk() );
		this .state .prepareStatement( new Walk() );	// old parser had an extra Walk on the stack so we will too
	}
	
	@Override public void exitProgram(ZomicParser.ProgramContext ctx) { 
	    this .state .commitLastStatement();	// old parser had an extra Walk on the stack so we will too
		if( this .state .getProgram() .size() == 0 ) {
			throw new RuntimeException("We should always have a Walk by the time we get here!");		
		}
	}
	
	//@Override public void enterStmt(ZomicParser.StmtContext ctx) { }
	//@Override public void exitStmt(ZomicParser.StmtContext ctx) { }

	@Override public void enterCompound_stmt(ZomicParser.Compound_stmtContext ctx) {
	    this .state .prepareStatement( new Walk() );
	}
	
	@Override public void exitCompound_stmt(ZomicParser.Compound_stmtContext ctx) {
	    this .state .commitLastStatement();
	}
	
	//@Override public void enterDirectCommand(ZomicParser.DirectCommandContext ctx) { }
	//@Override public void exitDirectCommand(ZomicParser.DirectCommandContext ctx) { }
	
//	@Override public void enterNestedCommand(ZomicParser.NestedCommandContext ctx) { }
//	@Override public void exitNestedCommand(ZomicParser.NestedCommandContext ctx) { }
	
	@Override public void enterStrut_stmt(ZomicParser.Strut_stmtContext ctx) { 
	    this .state .prepareMoveTemplate();
	}
	
	@Override public void exitStrut_stmt(ZomicParser.Strut_stmtContext ctx) { 
	    ZomicCompilerState.MoveTemplate template = (ZomicCompilerState.MoveTemplate) this.state .popTemplate();
	    try {
            this .state .commit(template.generate());
        } catch (Exception e) {
            logger .warning( e.getMessage() );
            throw new RuntimeException( e.getMessage(), e );
        }
	}
	
	//@Override public void enterLabel_stmt(ZomicParser.Label_stmtContext ctx) { }
	
	@Override public void exitLabel_stmt(ZomicParser.Label_stmtContext ctx) {
	    this .state .commit( new Label(ctx.IDENT().getText() ) );
	}
	
	@Override public void enterScale_stmt(ZomicParser.Scale_stmtContext ctx) {
	    this .state .prepareScaleTemplate();
	}
	
	@Override public void exitScale_stmt(ZomicParser.Scale_stmtContext ctx) {
		if( ctx.scale != null ) {
			this .state .setCurrentScale(parseInt(ctx.scale));
		}
		ZomicCompilerState.ScaleTemplate template = (ZomicCompilerState.ScaleTemplate) this.state .popTemplate();
		this .state .commit (template.generate());
	}

	//@Override public void enterBuild_stmt(ZomicParser.Build_stmtContext ctx) { }
	
	@Override public void exitBuild_stmt(ZomicParser.Build_stmtContext ctx) { 
	    this .state .commit( new Build(/*build*/ true, /*destroy*/ false) );
	}
	
	//@Override public void enterDestroy_stmt(ZomicParser.Destroy_stmtContext ctx) { }
	
	// remove unsupported feature from the grammar although supporting code is partially in place
//	@Override public void exitDestroy_stmt(ZomicParser.Destroy_stmtContext ctx) { 
//		this .state .commit( new Build(/*build*/ false, /*destroy*/ true) );
//	}
	
	//@Override public void enterMove_stmt(ZomicParser.Move_stmtContext ctx) { }
	
	@Override public void exitMove_stmt(ZomicParser.Move_stmtContext ctx) { 
	    this .state .commit( new Build(/*build*/ false, /*destroy*/ false) );
	}
	
	@Override public void enterRotate_stmt(ZomicParser.Rotate_stmtContext ctx) {
	    this .state .prepareRotateTemplate();
	}

	@Override public void exitRotate_stmt(ZomicParser.Rotate_stmtContext ctx) {
	    try {
	        ZomicCompilerState.RotateTemplate template = (ZomicCompilerState.RotateTemplate) this.state .popTemplate();
	        if(ctx.steps != null) {
	            template.steps = parseInt(ctx.steps);
	        }
	        this .state .commit (template.generate());
        } catch (Exception e) {
            logger .warning( e.getMessage() );
            throw new RuntimeException( e.getMessage(), e );
        }
	}
	
	@Override public void enterReflect_stmt(ZomicParser.Reflect_stmtContext ctx) {
		boolean isThruCenter = ctx.symmetry_center_expr().CENTER() != null;
		this .state .prepareReflectTemplate(isThruCenter);
	}
	
	@Override public void exitReflect_stmt(ZomicParser.Reflect_stmtContext ctx) {
	    ZomicCompilerState.ReflectTemplate template = (ZomicCompilerState.ReflectTemplate) this.state .popTemplate();
		try {
            this .state .commit( template.generate() );
        } catch (Exception e) {
            logger .warning( e.getMessage() );
            throw new RuntimeException( e.getMessage(), e );
        }
	}
	
	@Override public void enterFrom_stmt(ZomicParser.From_stmtContext ctx) {
	    this .state .prepareStatement( new Save(ZomicEventHandler.ACTION) );
	    this .state .prepareStatement( new Walk() );
	    this .state .commit( new Build(/*build*/ false, /*destroy*/ false) );
	}
	
	@Override public void exitFrom_stmt(ZomicParser.From_stmtContext ctx) {
	    this .state .commitLastStatement();
	    this .state .commitLastStatement();
	}
	
	@Override public void enterSymmetry_stmt(ZomicParser.Symmetry_stmtContext ctx) {
	    ZomicCompilerState.SymmetryModeEnum symmetryMode = null;
		if( ctx.axis_expr() != null ) {
			symmetryMode = ZomicCompilerState.SymmetryModeEnum.RotateAroundAxis;
		} else if( ctx.symmetry_center_expr() == null ) {
			symmetryMode = ZomicCompilerState.SymmetryModeEnum.Icosahedral;
		} else if(ctx.symmetry_center_expr().blueAxisIndexNumber != null) {
			symmetryMode = ZomicCompilerState.SymmetryModeEnum.MirrorThroughBlueAxis;
		} else if(ctx.symmetry_center_expr().CENTER() != null) {
			symmetryMode = ZomicCompilerState.SymmetryModeEnum.ReflectThroughOrigin;
		} else {
			throw new IllegalStateException("Unexpected symmetry mode: " + ctx.getText());
		}
		// push a SymmetryTemplate on the Templates stack to collect the Symmetry parameters
		this .state .prepareSymmetryTemplate(symmetryMode);
		// push an actual Symmetry statement on the Statements stack to collect the body
		this .state .prepareStatement(new Symmetry());
	}

	@Override public void exitSymmetry_stmt(ZomicParser.Symmetry_stmtContext ctx) {
	    ZomicCompilerState.SymmetryTemplate template = (ZomicCompilerState.SymmetryTemplate) this.state .popTemplate();
		// SymmetryTemplate.generate() will apply collected template params 
		// to the Symmetry statement that's already on the Statement stack collecting nested statements in its body.
		// The Symmetry statement that's returned is the same object that's still on the stetements stack,
		// so we can just ignore the return value here.
		try {
            template.generate();
        } catch (Exception e) {
            logger .warning( e.getMessage() );
            throw new RuntimeException( e.getMessage(), e );
        }
		// Now commit the Symmetry statement and pop it off of the Statements stack.
		this .state .commitLastStatement();
	}
	
	@Override public void enterRepeat_stmt(ZomicParser.Repeat_stmtContext ctx) {
		// negative numbers are allowed but the sign is silently removed
	    this .state .prepareStatement( new Repeat(abs(parseInt(ctx.count))) );
	}
	
	@Override public void exitRepeat_stmt(ZomicParser.Repeat_stmtContext ctx) {
	    this .state .commitLastStatement();
	}
	
	@Override public void enterBranch_stmt(ZomicParser.Branch_stmtContext ctx) {
	    this .state .prepareStatement(new Save(ZomicEventHandler.LOCATION) );
	}
	
	@Override public void exitBranch_stmt(ZomicParser.Branch_stmtContext ctx) {
	    this .state .commitLastStatement();
	}
	
	@Override public void enterSave_stmt(ZomicParser.Save_stmtContext ctx) {
		int state = 0;
		switch(ctx.state.getText()) {
			case "orientation":
				state = ZomicEventHandler.ORIENTATION;
				break;
			case "scale":
				state = ZomicEventHandler.SCALE;
				break;
			case "location":
				state = ZomicEventHandler.LOCATION;
				break;
			case "build":
				state = ZomicEventHandler.ACTION;
				break;
			case "all":
				state = ZomicEventHandler.ALL;
				break;
			default:
				throw new UnsupportedOperationException("Unexpected save parameter: " + ctx.getText());
		}
		this .state .prepareStatement( new Save(state) );
	}
	
	@Override public void exitSave_stmt(ZomicParser.Save_stmtContext ctx) {
	    this .state .commitLastStatement();
	}
	
	//@Override public void enterSymmetry_center_expr(ZomicParser.Symmetry_center_exprContext ctx) { }
	
	@Override public void exitSymmetry_center_expr(ZomicParser.Symmetry_center_exprContext ctx) {
	    ZomicCompilerState.IHaveAxisInfo elements = (ZomicCompilerState.IHaveAxisInfo) this.state .peekTemplate();
		if ( ctx.blueAxisIndexNumber != null ) { 
			elements.indexNumber(ctx.blueAxisIndexNumber.getText());
		}
	}
	
	//@Override public void enterStrut_length_expr(ZomicParser.Strut_length_exprContext ctx) { }
	
	@Override public void exitStrut_length_expr(ZomicParser.Strut_length_exprContext ctx) { 
		if(ctx.HALF() != null) {
			((ZomicCompilerState.MoveTemplate)this.state .peekTemplate()).denominator = 2;
		}
	}
	
	//@Override public void enterHalf_size_expr(ZomicParser.Half_size_exprContext ctx) { }
	//@Override public void exitHalf_size_expr(ZomicParser.Half_size_exprContext ctx) { }
	
	//@Override public void enterSize_expr(ZomicParser.Size_exprContext ctx) { }
	//@Override public void exitSize_expr(ZomicParser.Size_exprContext ctx) { }
	
	//@Override public void enterExplicit_size_expr(ZomicParser.Explicit_size_exprContext ctx) { }
	
	@Override public void exitExplicit_size_expr(ZomicParser.Explicit_size_exprContext ctx) { 
		if( ctx.scale != null ) {
			this .state .setCurrentScale(parseInt(ctx.scale));
		}
		ZomicCompilerState.MoveTemplate template = (ZomicCompilerState.MoveTemplate)this.state .peekTemplate();
		// remove unsupported feature from the grammar although supporting code is partially in place
//		if( ctx.sizeRef != null ) {
//			String sizeRef = ctx.sizeRef.getText();
//			template.sizeRef = sizeRef;
//			logger.warning("Ignoring undocumented sizeRef = '" + sizeRef + "'.");
//		}
		if( ctx.isVariableLength != null ) {
			template.isVariableLength(true);
		}
	}
	
	@Override public void exitSizeShort(ZomicParser.SizeShortContext ctx) { 
	    this .state .setCurrentScale(ZomicNamingConvention.SHORT);
	}
	
	@Override public void exitSizeLong(ZomicParser.SizeLongContext ctx) { 
	    this .state .setCurrentScale(ZomicNamingConvention.LONG);
	}
	
	@Override public void exitSizeMedium(ZomicParser.SizeMediumContext ctx) { 
	    this .state .setCurrentScale(ZomicNamingConvention.MEDIUM);
	}
	
	//@Override public void enterAxis_expr(ZomicParser.Axis_exprContext ctx) { }
	//@Override public void exitAxis_expr(ZomicParser.Axis_exprContext ctx) { }
	
	//@Override public void enterAxis_index_expr(ZomicParser.Axis_index_exprContext ctx) { }
	
	@Override public void exitAxis_index_expr(ZomicParser.Axis_index_exprContext ctx) {
	    ZomicCompilerState.IHaveAxisInfo elements = (ZomicCompilerState.IHaveAxisInfo)this.state .peekTemplate();
		elements.indexNumber(ctx.indexNumber.getText());
		if ( ctx.handedness != null ) { 
			elements.handedness(ctx.handedness.getText());
		}
	}
	
	//@Override public void enterAxis_name_expr(ZomicParser.Axis_name_exprContext ctx) { }

	/*
	 This convoluted looking code allows new colors and color aliases 
	 to be added to the language without overriding the event handler for each specific color.
	 It's a bit of overkill, but it's nice because it handles aliases automaatically. 
	 e.g. "pent" is an alias for "red". This mechanism handles translating the aliases.
	 The test for ending with strCONTEXT is to avoid using ErrorNodeImpl 
	 as when we parse an invalid color or some other unforeseen invalid context.
	*/
	@Override public void exitAxis_name_expr(ZomicParser.Axis_name_exprContext ctx) {
		final String strCONTEXT = "context";
		String colorContext = "Unexpected Axis Color: '" + ctx.getText() + "'.";
		if(ctx.children != null) {
			colorContext = ctx.getChild(0).getClass().getSimpleName().toLowerCase();
			if(colorContext.endsWith(strCONTEXT)) {
				colorContext = colorContext.replaceFirst(strCONTEXT, "");
				((ZomicCompilerState.IHaveAxisInfo)this.state .peekTemplate()).axisColor(colorContext);
				log(colorContext);
				return;
			}
		} 
		log(colorContext);
		throw new RuntimeException( colorContext );
	} 

	@Override public void exitAlgebraic_number_expr(ZomicParser.Algebraic_number_exprContext ctx) { 
	    ZomicCompilerState.ScaleInfo template = (ZomicCompilerState.ScaleInfo)this.state .peekTemplate();
		template.ones = parseInt(ctx.ones);
		if(ctx.phis != null) {
			template.phis = parseInt(ctx.phis);
		}
	}
	
	@Override public void enterEveryRule(ParserRuleContext ctx) { 
		logContext(ctx, true);
	}
	
	@Override public void exitEveryRule(ParserRuleContext ctx) { 
		logContext(ctx, false);
	}
	
	//@Override public void visitTerminal(TerminalNode node) { }
	
	@Override public void visitErrorNode(ErrorNode node) { 
		String msg = node.getText();
		// TODO: Provide a list of expected tokens 
		// by catching a RecognitionException in the parser and using parser.getExpectedTokens()...
		// See http://stackoverflow.com/questions/25512770/antlr4-get-next-possible-matching-parser-rules-for-the-given-input
		// or "Altering and Redirecting ANTLR Error Messages" from section 9.2 of "The Definitive ANTLR 4 Reference"
		this .state .commit( new Untranslatable(msg) );
	}

}
