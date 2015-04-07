package com.vzome.core.zomic;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.antlr.generated.ZomicLexer;
import com.vzome.core.antlr.generated.ZomicParser;
import com.vzome.core.antlr.generated.ZomicParser.ProgramContext;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.zomic.parser.Parser;
import com.vzome.core.zomic.program.Move;
import com.vzome.core.zomic.program.PrintVisitor;
import com.vzome.core.zomic.program.Walk;
import com.vzome.core.zomic.program.ZomicStatement;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javatests.TestSupport.assertNotEquals;
import junit.framework.TestCase;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.misc.IntervalSet;

public class ZomicASTTest extends TestCase
{
	public void testNoOpAlwaysPasses() { 
		// This is here just so the test framework always finds 
		// at least one test to run, 
		// even when I skip over all others by renaming them.
	}

	public ZomicASTTest() {
		initColors();	
	}
	
	private final ArrayList<String> zomicColors = new ArrayList<>();
	private final ArrayList<String> notYetZomicColors = new ArrayList<>();
	private final ArrayList<String> unsupportedColors = new ArrayList<>();
	private final ArrayList<String> nonChiralColors = new ArrayList<>();
	private final ArrayList<String> chiralColors = new ArrayList<>();

	private void initColors() {
		if (zomicColors.size() == 0) {
			// blue and green are first so we can test half length struts easily
			zomicColors.add("blue"); 
			zomicColors.add("green"); 
			zomicColors.add("red"); 
			zomicColors.add("yellow"); 
			zomicColors.add("black");			//chiral
			zomicColors.add("orange"); 
			zomicColors.add("purple");
		};
		if (notYetZomicColors.size() == 0) {
			notYetZomicColors.add("lavender"); 
			notYetZomicColors.add("olive"); 
			notYetZomicColors.add("maroon"); 
			notYetZomicColors.add("rose"); 
			notYetZomicColors.add("navy"); 
			notYetZomicColors.add("coral"); 
			notYetZomicColors.add("sulfur"); 
			notYetZomicColors.add("turquoise");	//chiral
		};
		if (unsupportedColors.size() == 0) {
			unsupportedColors.add("sand");		//chiral
			unsupportedColors.add("apple");		//chiral
			unsupportedColors.add("cinnamon");	//chiral
			unsupportedColors.add("spruce");	//chiral
			unsupportedColors.add("brown");		//chiral
		}
		// chirality (or handedness) is a characteristic of any colored axis 
		// that is NOT on the perimeter of the red-blue-yellow triangle 
		// as seen in the strut direction selector control of the GUI.
		// Re-list them all, grouped as chiral or not.
		if (nonChiralColors.size() == 0) {
			nonChiralColors.add("red"); 
			nonChiralColors.add("blue"); 
			nonChiralColors.add("yellow"); 
			nonChiralColors.add("green"); 
			nonChiralColors.add("orange"); 
			nonChiralColors.add("purple");
//			nonChiralColors.add("lavender"); 
//			nonChiralColors.add("olive"); 
//			nonChiralColors.add("maroon"); 
//			nonChiralColors.add("rose"); 
//			nonChiralColors.add("navy"); 
//			nonChiralColors.add("coral"); 
//			nonChiralColors.add("sulfur"); 
		}
		if (chiralColors.size() == 0) {
			chiralColors.add("black");		//chiral
			//chiralColors.add("turquoise");	// turquoise is chiral, but zomic doesn't know it yet
			
			// the rest are all chiral, but currently unsupported
			//chiralColors.add("sand");		//chiral
			//chiralColors.add("apple");		//chiral
			//chiralColors.add("cinnamon");	//chiral
			//chiralColors.add("spruce");		//chiral
			//chiralColors.add("brown");		//chiral
		}
	}
	
	private ProgramContext parse(String input) {
		System.out.println("--------------------------------------------");
		System.out.println("parse:\n\"" + input + "\"\n");
		CharStream inputStream = new ANTLRInputStream( input );
		// feed input to lexer
		ZomicLexer lexer = new ZomicLexer( inputStream );
		// get a stream of matched tokens
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		// pass tokens to the parser
		ZomicParser parser = new ZomicParser( tokens );

		// specify our entry point (top level rule)
		ProgramContext programContext = parser.program(); // parse
		// TODO: ... whatever ...
		assertTrue(true);
		return programContext;
	}

	private Walk compile(String input) {
		System.out.println("--------------------------------------------");
		System.out.println("compile:\n\"" + input + "\"\n");
		IcosahedralSymmetry symm = new IcosahedralSymmetry( new PentagonField(), "default" );
		boolean showProgressMessages = false; 
		Walk program = ZomicASTCompiler.compile(input, symm, showProgressMessages);
		assertNotNull("ZomicASTCompiler.compile() should never return null", program);
		Integer size = program.size();
		System.out.println("Program contains " + size.toString() + " statement(s).");
		System.out.println("");
		return program;
	}
	
	private Walk oldCompile(String input) {
		ZomicStatement oldProgram = null;
		try {		
			oldProgram = Parser.parse( 
					new ByteArrayInputStream( input.getBytes("UTF8") ), 
					(IcosahedralSymmetry) new PentagonField().getSymmetry( "icosahedral" ) 
			);
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
		}
		return (Walk) oldProgram;
	}
	
	private void compareVisits (String input) {
		String oldWay = printVisit (oldCompile(input));
		String newWay = printVisit (compile(input));
		final String nullStr = "<null>";
		if(oldWay == null) {oldWay = nullStr;}
		if(newWay == null) {newWay = nullStr;}
		if( !oldWay.equals(newWay) ) {
			System.out.println("old way:");
			System.out.println(oldWay);
			System.out.println("new way:");
			System.out.println(newWay);
		}
		assertEquals(oldWay, newWay, "Comparing oldWay to newWay.");
		assertNotEquals(oldWay, nullStr, "Oops! oldWay is null!");
		assertNotEquals(newWay, nullStr, "Oops! newWay is null!");
	}
	
	private String printVisit (ZomicStatement program) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try { 
			// Cool! New try-with-resources syntax auto closes specified resources in implied finally block
			try (PrintWriter out = new PrintWriter( output /* ... or use System.out*/ )) {
				program .accept( new PrintVisitor( out, new IcosahedralSymmetry( new PentagonField(), "solid connectors" )
				) );
			}
		} catch (ZomicException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		return new String(output.toByteArray());
	}
	
	private void assertProgramSize(int size, Walk program) {
		assertEquals("program.size", size, program.size());
	}
	
	////////////////////////////////////////
	// BEGIN test definitions
	////////////////////////////////////////
	// All public void methods 
	//	with names starting with "test" 
	//	having no parameters 
	//	will be run by the test framework.
	// By convention, I add a leading underscore
	//	to the ones I don't want to run. 
	//	e.g. _test_DisableThisTest()
	////////////////////////////////////////

	public void test_ZomicNamingConvention() {
		IcosahedralSymmetry symmetry = new IcosahedralSymmetry( new PentagonField(), "default" );
		String[] colors = symmetry.getDirectionNames();
		ZomicNamingConvention namingConvention = new ZomicNamingConvention(symmetry);
		for ( String color : colors ) {
			// TODO: Loop through all valid permutations of indexes and handedness per color, not just "0"
			//	When that's working, then remove the corresponding run-time test from ZomicASTCompiler.
			String indexName = "0";
			Axis axis = namingConvention.getAxis(color, indexName );
			if(axis == null) {
					String msg = symmetry.getClass().getSimpleName() +
							" colors include '" +
							color + 
							"' which is not yet supported by " +
							namingConvention.getClass().getSimpleName();
					System.out.println(msg);
					assertTrue(msg, unsupportedColors.contains(color));
					assertFalse(msg, notYetZomicColors.contains(color));
					assertFalse(msg, zomicColors.contains(color));
			} else {
				String indexNameCheck = namingConvention.getName( axis );
				if ( axis != namingConvention.getAxis(color, indexNameCheck ) ) {
					String msg = color + " " + indexName + " is unexpectedly mapped to " + indexNameCheck;
					System.out.println(msg);
					assertTrue(msg, false);
				}
				String msg = color;
				assertTrue(msg, zomicColors.contains(color) || notYetZomicColors.contains(color));
				assertFalse(msg, unsupportedColors.contains(color));
			}
		}

	}
	
	public void test_ZomicLexer() {
		try {
			CharStream inputStream = new ANTLRInputStream("red -7");
			ZomicLexer lexer = new ZomicLexer(inputStream);
			ATN atn = lexer.getATN();
			int stateNumber = 0;
			IntervalSet intervalSet = atn.getExpectedTokens(stateNumber, RuleContext.EMPTY);
			// TODO: just playing around to see what's available here...
			assertTrue(intervalSet.size() > 0);
		}
		catch(Exception ex) {
			assertNull(ex.toString(), ex);
		}
	}
	
	public void test_ZomicParser() {
		String input = "size valid_sizeref.123 red 2";
		ProgramContext programContext = parse( input );
		assertNotNull("programContext should not be null", programContext);
		// TODO: ... whatever ...
	}
	
	public void Dont__test_compareVisits() {
		compareVisits( "red 2" );
	}
	
	public void test_EmptyProgram() {
		Walk program = compile("");
		assertProgramSize(0, program);
		
		program = compile("\t");
		assertProgramSize(0, program);
		
		program = compile("\n");
		assertProgramSize(0, program);
		
		program = compile("\r");
		assertProgramSize(1, program); // TODO: should be 0
		
		program = compile("\r\n");
		assertProgramSize(0, program);
		
		program = compile("\n\r");
		assertProgramSize(1, program); // TODO: should be 0
		
		program = compile("{}");
		assertProgramSize(0, program);
		
		program = compile("// single line comment terminated by EOF.");
		assertProgramSize(0, program);

		program = compile("// single line comment terminated by newline.\n");
		assertProgramSize(0, program);

		program = compile("/* multiple line \n comment*/");

		program = compile("{{{/* nested braces */}}}");
		assertProgramSize(0, program);
	}

	public void test_LabelStatement() {
		String label = "valid_lowercase_label.234_whatever";
		Walk program = compile("label " + label);
		assertProgramSize(1, program);
		// TODO: verify the id.
	}
	
	public void test_StrutDefaultValues() {
		Walk program = compile("red 0 /* _test_StrutDefaultValues */");
		assertProgramSize(1, program);
		Iterator it = program .getStatements(); 
		while( it.hasNext() ) {
			ZomicStatement stmt = (ZomicStatement) it.next();
			if(stmt instanceof Move) {
				Move m = (Move)stmt;
				AlgebraicField f = m.getLength().getField();
				String n = f.getName();
				assertEquals(3, f.getOrder());
			}
		}
//		assertEquals("Default strut size should be Medium", 
//				ZomicNamingConvention.MEDIUM,
//				program.getStatements());
	}
		
	public void test_RedAliases() {
		Walk program = compile("red 0");
		assertProgramSize(1, program);

		program = compile("pent 0");
		assertProgramSize(1, program);

		program = compile("pentagon 0");
		assertProgramSize(1, program);
	}
	
	public void test_BlueAliases() {
		Walk program = compile("blue 0");
		assertProgramSize(1, program);

		program = compile("rect 0");
		assertProgramSize(1, program);

		program = compile("rectangle 0");
		assertProgramSize(1, program);
	}
	
	public void test_YellowAliases() {
		Walk program = compile("yellow 0");
		assertProgramSize(1, program);

		program = compile("tri 0");
		assertProgramSize(1, program);

		program = compile("triangle 0");
		assertProgramSize(1, program);
	}
	
	public void test_StrutExplicitSizes() {
		// zero
		Walk program = compile("size 0 red 2");
		assertProgramSize(1, program);
		
		// +zero
		program = compile("size +0 red 2");
		assertProgramSize(1, program);
		
		// TODO: confirm that the sign is not lost 
		// since +0 is different from -0.
		// -zero 
		program = compile("size -0 red 2");
		assertProgramSize(1, program);
		
		// implied sign
		program = compile("size 1 red 2");
		assertProgramSize(1, program);

		// + sign
		program = compile("size +1 red 2");
		assertProgramSize(1, program);

		// - sign
		program = compile("size -1 red 2");
		assertProgramSize(1, program);

		// how big is too big? 123 is huge
		program = compile("size 123 red 2");
		assertProgramSize(1, program);
	}
	
	public void test_StrutFullyExplicitSizes() {
		// zero
		Walk program = compile("size 0 0 0 red 2");
		assertProgramSize(1, program);
		
		// +zero
		program = compile("size +0 +0 +0 red 2");
		assertProgramSize(1, program);
		
		// -zero
		program = compile("size -0 -0 -0 red 2");
		assertProgramSize(1, program);
		
		// implied sign
		program = compile("size 1 1 1 red 2");
		assertProgramSize(1, program);

		// + sign
		program = compile("size +1 +1 +1 red 2");
		assertProgramSize(1, program);

		// - sign
		program = compile("size -1 -1 -1 red 2");
		assertProgramSize(1, program);

		// how big is too big? 123 is huge
		program = compile("size 123 456 789 red 2");
		assertProgramSize(1, program);
	}
	
	public void test_StrutVariableSize() {
		// -99 is the old indicator for variable size.
		Walk program = compile("size -99 red 0");
		assertProgramSize(1, program);
		
		// DJH New proposed alternative to 'size -99' is 'size ?'
		program = compile("size ? red -0");
		assertProgramSize(1, program);	
	}
	
	public void test_StrutSizeRef() {
		String input = "size any_valid_lowercase_sizeref_even_with_digits...0123456789 red +0";

		// TODO: sizeRef was undocumented and seems to be unused by the old code, although it was valid in the old grammar.
		// TODO: I have used it as an alternative variable length indicator.
		// TODO: I hope to eventually use this clearer mechanism to replace the 'size -99' trigger 
		// TODO: since size -99 used only by the strut resources and was also undocumented.
		Walk program = compile( input );
		assertProgramSize(1, program);
}
	
	public void test_StrutNamedSizes() {
		Walk program = compile("long red 0");
		assertProgramSize(1, program);
		
		program = compile("medium yellow 1");
		assertProgramSize(1, program);
		
		program = compile("short blue 2");
		assertProgramSize(1, program);

		// defaults to medium
		program = compile("green 3");
		assertProgramSize(1, program);
	}
	
	public void test_ZomicStrutColors() {
		int tests = 0;
		for ( String color : zomicColors ) {
			Walk program = compile(color + " 0");
			assertProgramSize(1, program);
			tests++; // be sure we tested all colors
		}
		assertTrue("Why didn't we test the other zomicColors?",
				(tests > 0) && (tests == zomicColors.size()) );
	}
	
	public void test_NotYetZomicStrutColors() {
		int tests = 0;
		for ( String color : notYetZomicColors ) {
			String script = color + " 0";
			try {
				compile(script);
				assertFalse("Script '" + script + "' shouldn't get here without throwing an exception.", 
						true);
			} 
			catch (RuntimeException ex) {
				String exMsg = ex.getMessage();
				if(exMsg == null) { exMsg = "msg = <null> for exception: " + ex.toString(); }
				assertTrue("Script '" + script + "' threw a different exception than expected: " + exMsg,
						exMsg.startsWith("bad axis specification") ||
						exMsg.startsWith("Unexpected Axis Color") );
			}
			tests++; // be sure we tested all colors
		}
		assertTrue("Why didn't we test the other notYetZomicColors?",
				(tests > 0) && (tests == notYetZomicColors.size()) );
	}
	
	public void test_UnsupportedStrutColors() {
		int tests = 0;
		for ( String color : unsupportedColors ) {
			String script = color + " 0";
			try {
				compile(script);
				assertFalse("Script '" + script + "' shouldn't get here without throwing an exception.", 
						true);
			} 
			catch (RuntimeException ex) {
				String exMsg = ex.getMessage();
				if(exMsg == null) { exMsg = "msg = <null> for exception: " + ex.toString(); }
				assertTrue("Script '" + script + "' threw a different exception than expected: " + exMsg,
						exMsg.startsWith("bad axis specification") ||
						exMsg.startsWith("Unexpected Axis Color") );
			}
			tests++; // be sure we tested all colors
		}
		assertTrue("Why didn't we test the other unsupportedColors?",
				(tests > 0) && (tests == unsupportedColors.size()) );
	}
	
	public void test_StrutExplicitHalfLengths() {
			// negative axis
			Walk program = compile("size -1 -2 -3 half blue -4");
			assertProgramSize(1, program);
			// default positive axis
			program = compile("size 1 2 3 half green 4");
			assertProgramSize(1, program);
			// explicit positive axis
			program = compile("size +8 +7 +6 half green +0");
			assertProgramSize(1, program);
	}
	
	public void test_StrutHandedness() {			
		String[] signs = { "", "+", "-" };	
		for (int i=0; i<signs.length; i++) {
			int tests = 0;
			for ( String color : chiralColors ) {
				String sign = signs[i];
				String script = color + " " + sign + "0" + sign + " /* _test_StrutHandedness */";
				Walk program = compile(script);
				assertProgramSize(1, program);
				tests++; // be sure we tested all colors
			}
			assertTrue("Why didn't we test the other chiralColors?",
					(tests > 0) && (tests == chiralColors.size()) );
		}
	}
	
	public void test_StrutInvalidHandedness() {			
		String[] signs = { "", "+", "-" };	
		for (int i=0; i<signs.length; i++) {
			int tests = 0;
			for ( String color : nonChiralColors ) {
				String sign = signs[i];
				String script = color + " " + sign + "0" + sign + " /* _test_StrutInvalidHandedness */";
				try {
					Walk program = compile(script);
					assertProgramSize(1, program); // just a convenient breakpoint in case we do get to the next line.
					assertTrue("Script '" + script + "' shouldn't get here without throwing an exception "
							+ "unless sign is omitted.", 
							"".equals(sign)); 
				} 
				catch (RuntimeException ex) {
					String exMsg = ex.getMessage();
					if(exMsg == null) { exMsg = "msg = <null> for exception: " + ex.toString(); }
					assertTrue("Script '" + script + "' threw a different exception than expected: " + exMsg,
							exMsg.startsWith("bad axis specification") );
					assertFalse("Script '" + script + "' shouldn't get here without throwing an exception "
							+ "unless sign is non-blank.", 
							"".equals(sign));
				}
				tests++; // be sure we tested all colors
			}
			assertTrue("Why didn't we test the other nonChiralColors?",
					(tests > 0) && (tests == nonChiralColors.size()) );
		}
	}
	
	public void ThisIsAFutureTest_MisplacedIntegerPolarity() {
		// TODO: these should throw exceptions, but be sure we catch them.
		// negative
		Walk program = compile("size 1- red 1");
		assertProgramSize(1, program);
		// positive
		program = compile("size 2+ red 2");
		assertProgramSize(1, program);
	}
	
	public void test_StrutValidHalfLengths() {
		Walk program = compile("half blue 2");
		assertProgramSize(1, program);
		
		program = compile("half green 3");
		assertProgramSize(1, program);
	}
	
	public void test_StrutInvalidHalfLengths() {
		int tests = 0;
		for ( String color : zomicColors ) {
			switch(color) {
				case "blue":
				case "green":
					break; // tested elsewhere
				default:
					// all other colors should throw exceptions
					String script = "half " + color + " 0";
					try {
						compile(script);
						assertFalse("Script '" + script + "' shouldn't get here without throwing an exception.", 
								true);
					} 
					catch (RuntimeException ex) {
						String exMsg = ex.getMessage();
						if(exMsg == null) { exMsg = "msg = <null> for exception: " + ex.toString(); }
						assertTrue("Script '" + script + "' threw a different exception than expected: " + exMsg,
								//use regex: exMsg.matches() instead of exMsg.equals() in case the exact wording is changed
								exMsg.matches(".*half.*not allowed.*" + color + ".*" ) );
					}
					tests++; // be sure we tested all colors
					break;
			}
		}
		assertTrue("Why didn't we test the half length of other zomicColors?",
				(tests > 0) && (tests == zomicColors.size() - 2) );
	}
	
	public void test_MultipleStruts() {
		String allColors = "";
		int n = 0;
		for ( String color : zomicColors ) {
			n++;
			allColors = allColors + color + " -3 ";
		}
		Walk program = compile(allColors);
		assertProgramSize(n, program);
	}
	
	public void test_BuildModes() {
		Walk program = compile("build");
		assertProgramSize(1, program);

		program = compile("move");
		assertProgramSize(1, program);

		program = compile("destroy");
		assertProgramSize(1, program);
	}
	
	public void test_ScaleStatement() {
		Walk program = compile("scale 1");
		assertProgramSize(1, program);

		program = compile("scale 2 (3)");
		assertProgramSize(1, program);

		program = compile("scale 4 (5 6)");
		assertProgramSize(1, program);

		program = compile("scale -1");
		assertProgramSize(1, program);

		program = compile("scale 0");
		assertProgramSize(1, program);

		program = compile("scale -99");
		assertProgramSize(1, program);
	}
	
	public void test_EmptyBranchStatement() {
		Walk program = compile("branch");
		assertProgramSize(1, program);
		
		program = compile("branch {}");
		assertProgramSize(1, program);
	}
	
	public void test_SimpleBranchStatement() {
		Walk program = compile("branch { } red 0 green -1");
		assertProgramSize(3, program);
		
		program = compile("branch { yellow 1 blue -2} black 1");
		// TODO: Should it be 2 or 4?
		//assertProgramSize(4, program);
	}

	public void test_BranchStatement() {
		Walk program = compile("branch");
		assertProgramSize(1, program);
		
		program = compile("branch { }");
		assertProgramSize(1, program);
		
		program = compile("branch { yellow 1 blue -2} black 1 red 0");
		assertProgramSize(3, program);

		program = compile("branch yellow 1 blue -2 black 1 red 0");
		assertProgramSize(4, program);
	}

	public void test_BranchNesting() {
		Walk program = compile("branch { branch { branch { } } }");
		assertProgramSize(1, program);
		
		program = compile("branch { branch { branch { red 0 } } }");
		assertProgramSize(1, program);

	}

	public void test_FromStatement() {
		Walk program = compile("from");
		assertProgramSize(1, program);
		
		program = compile("from { }");
		assertProgramSize(1, program);
		
		program = compile("from { yellow 1 blue -2} black 1 red 0");
		assertProgramSize(3, program);

		program = compile("from yellow 1 blue -2 black 1 red 0");
		assertProgramSize(4, program);
	}

	public void test_RepeatStatement() {
		Walk program = compile("repeat 0");
		assertProgramSize(1, program);
		
		program = compile("repeat -1"); // non-positive numbers are allowed but should do nothing. They should act as comments
		assertProgramSize(1, program);
		
		program = compile("repeat 1");
		assertProgramSize(1, program);
		
		program = compile("repeat 2 { }");
		assertProgramSize(1, program);
		
		program = compile("repeat 3 { yellow 1 blue -2} black 1 red 0");
		assertProgramSize(3, program);

		program = compile("repeat 3 yellow 1 blue -2 black 1 red 0");
		assertProgramSize(4, program);
	}
		
	public void test_SaveStatement() {
		ArrayList<String> states = new ArrayList<>();
		states.add("location");
		states.add("scale");
		states.add("orientation");
		states.add("build");
		states.add("all");
		int tests = 0;
		for ( String state : states ) {
			Walk program = compile("save " + state);
			assertProgramSize(1, program);

			program = compile("save " + state + " { }");
			assertProgramSize(1, program);

			program = compile("save " + state + " red 0 blue 0 yellow 0");
			assertProgramSize(3, program);

			program = compile("save " + state + " {red 0} blue 0 yellow 0");
			assertProgramSize(3, program);

			program = compile("save " + state + " {red 0 blue 0} yellow 0");
			assertProgramSize(2, program);

			program = compile("save " + state + " {red 0 blue 0 yellow 0}");
			assertProgramSize(1, program);
			tests++; // be sure we tested all states
		}
		assertTrue("Why didn't we test the other save states?",
				(tests > 0) && (tests == states.size()) );
	}
}
