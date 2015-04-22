package com.vzome.core.zomic;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.antlr.generated.ZomicLexer;
import com.vzome.core.antlr.generated.ZomicParser;
import com.vzome.core.antlr.generated.ZomicParser.ProgramContext;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.math.symmetry.NamingConvention;
import com.vzome.core.zomic.parser.Parser;
import com.vzome.core.zomic.program.Move;
import com.vzome.core.zomic.program.PrintVisitor;
import com.vzome.core.zomic.program.Walk;
import com.vzome.core.zomic.program.ZomicStatement;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
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

	private final IcosahedralSymmetry symmetry;
	private final ZomicNamingConvention namingConvention;
			
	public ZomicASTTest() {
		symmetry = new IcosahedralSymmetry( new PentagonField(), "default" );
		namingConvention = new ZomicNamingConvention(symmetry);
		initColors();	
	}
	
	private final ArrayList<String> zomicColors = new ArrayList<>();
	private final ArrayList<String> notYetZomicColors = new ArrayList<>();
	private final ArrayList<String> unsupportedColors = new ArrayList<>();
	private final ArrayList<String> nonChiralColors = new ArrayList<>();
	private final ArrayList<String> chiralColors = new ArrayList<>();

	private void initColors() {
		if (zomicColors.isEmpty()) {
			// blue and green are first so we can test half length struts easily
			zomicColors.add("blue"); 
			zomicColors.add("green"); 
			zomicColors.add("red"); 
			zomicColors.add("yellow"); 
			zomicColors.add("black");			//chiral
			zomicColors.add("orange"); 
			zomicColors.add("purple");
		}
		if (notYetZomicColors.isEmpty()) {
			notYetZomicColors.add("lavender"); 
			notYetZomicColors.add("olive"); 
			notYetZomicColors.add("maroon"); 
			notYetZomicColors.add("rose"); 
			notYetZomicColors.add("navy"); 
			notYetZomicColors.add("coral"); 
			notYetZomicColors.add("sulfur"); 
			notYetZomicColors.add("turquoise");	//chiral
		}
		if (unsupportedColors.isEmpty()) {
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
		if (nonChiralColors.isEmpty()) {
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
		if (chiralColors.isEmpty()) {
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
		assertNotNull("programContext should not be null.", programContext);
		return programContext;
	}

	private Walk newCompileFile(String fileName) {
		return newCompileFile(fileName, false);
	}

	private Walk newCompileFile(String fileName, boolean expectErrors) {
		System.out.println("--------------------------------------------");
		System.out.println("new way compile file:\n\"" + fileName + "\"\n");
		boolean showProgressMessages = false; // set to true for more detailed output during AST compilation
		Walk program = ZomicASTCompiler.compileFile(fileName, symmetry, showProgressMessages);
		if(!expectErrors) {
			assertNotNull("ZomicASTCompiler.compileFile() should never return null", program);
		}
		if(program != null) {
			System.out.println("New Program contains " + Integer.toString(program.size()) + " statement(s).");
		}
		System.out.println("");
		return program;
	}
	
	private Walk oldCompileFile(String fileName) {
		System.out.println("--------------------------------------------");
		System.out.println("old way compile file:\n\"" + fileName + "\"\n");
		ZomicStatement oldProgram = null;
		File file = new File(fileName);
		try {		
			FileInputStream fileInputStream = new FileInputStream( file );
			oldProgram = Parser.parse( fileInputStream, symmetry );
		} catch (FileNotFoundException ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
		}
		assertNotNull("Assume that Parser.parse() should never return null", oldProgram);
		Walk program = (Walk) oldProgram;
		if(program != null) {
			System.out.println("Old Program contains " + Integer.toString(program.size()) + " statement(s).");
		}
		System.out.println("");
		return program;
	}

	private Walk compileAndCompareFile(File file) {
		return compileAndCompareFile(file, true); 
	}
	
	private Walk compileAndCompareFile(File file, boolean doCompare) {
		return compileAndCompareFile(file, doCompare, false); 
	}
	
	private Walk compileAndCompareFile(File file, boolean doCompare, boolean expectErrors) {
		String fileName = file.getAbsolutePath();
		Walk oldProgram = oldCompileFile(fileName);
		Walk newProgram = newCompileFile(fileName);
		assertExpectedErrors(oldProgram, expectErrors);
		assertExpectedErrors(newProgram, expectErrors);
		compareStructure(oldProgram, newProgram, doCompare);
		return compareContent(oldProgram, newProgram, doCompare);
	}
		
	private Walk newCompile(String input) {
		System.out.println("--------------------------------------------");
		System.out.println("new way compile:\n\"" + input + "\"\n");
		boolean showProgressMessages = false; // set to true for more detailed output during AST compilation
		Walk program = ZomicASTCompiler.compile(input, symmetry, showProgressMessages);
		assertNotNull("ZomicASTCompiler.compile() should never return null", program);
		System.out.println("New Program contains " + Integer.toString(program.size()) + " statement(s).");
		System.out.println("");
		return program;
	}
	
	private Walk oldCompile(String input) {
		System.out.println("--------------------------------------------");
		System.out.println("old way compile:\n\"" + input + "\"\n");
		ZomicStatement oldProgram = null;
		try {
			oldProgram = Parser.parse( new ByteArrayInputStream( input.getBytes("UTF8") ), symmetry );
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
		}
		assertNotNull("Assume that Parser.parse() should never return null", oldProgram);
		Walk program = (Walk) oldProgram;
		if(program != null) {
			System.out.println("Old Program contains " + Integer.toString(program.size()) + " statement(s).");
		}
		System.out.println("");
		return program;
	}
	
	private void newCompileAndShow(File file) {
		String absoluteFilePath = file.getAbsolutePath();
		Walk program = newCompileFile(absoluteFilePath);
		System.out.println("Structure of " + absoluteFilePath);
		System.out.println( printTreeStructure(program) );		
		System.out.println("Contents of " + absoluteFilePath);
		System.out.println( printContents(program) );		
	}
	
	private Walk compileAndCompare(String input) {
		return compileAndCompare (input, true);
	}

	private Walk compileAndCompare(String input, boolean doCompare) {
		return compileAndCompare (input, doCompare, false);
	}
	
	private Walk compileAndCompare(String input, boolean doCompare, boolean expectErrors) {
		Walk oldProgram = oldCompile(input);
		Walk newProgram = newCompile(input);
		assertExpectedErrors(oldProgram, expectErrors);
		assertExpectedErrors(newProgram, expectErrors);
		compareStructure(oldProgram, newProgram, doCompare);
		return compareContent(oldProgram, newProgram, doCompare);
	}

	private Walk compileAndCompareStructure(String input, boolean doCompare) {
		Walk oldProgram = oldCompile(input);
		Walk newProgram = newCompile(input);
		return compareStructure(oldProgram, newProgram, doCompare);
	}
	
	private Walk compareStructure(Walk oldProgram, Walk newProgram, boolean doCompare) {
		String expected = printTreeStructure (oldProgram);
		String actual = printTreeStructure (newProgram);

		final String nullStr = "<null>";
		if(expected == null) {expected = nullStr;}
		if(actual == null) {actual = nullStr;}
		if( !expected.equals(actual) ) {
			System.out.println("old way:");
			System.out.println(expected);
			System.out.println("new way:");
		}
		System.out.println(actual);

		assertNotEquals("Oops! expected is null!", expected, nullStr);
		assertNotEquals("Oops! actual is null!", actual, nullStr);
		if(doCompare) {
			assertEquals( "Comparing program structure generated by oldCompile() and newCompile().", expected, actual);
		}
		
		return newProgram;
	}
	
	private Walk compileAndCompareContent(String input) {
		return compileAndCompareContent(input, true); 
	}
	
	private Walk compileAndCompareContent(String input, boolean doCompare) {
		Walk oldProgram = oldCompile(input);
		Walk newProgram = newCompile(input);
		return compareContent(oldProgram, newProgram, doCompare);
	}
		
	private Walk compareContent(Walk oldProgram, Walk newProgram, boolean doCompare) {
		final String nullStr = "<null>";
		String expected = printContents (oldProgram);
		String actual = printContents (newProgram);

		if(expected == null) {expected = nullStr;}
		if(actual == null) {actual = nullStr;}
		if( !expected.equals(actual) ) {
			System.out.println("old way:");
			System.out.println(expected);
			System.out.println("new way:");
		}
		System.out.println(actual);

		assertNotEquals("Oops! expected is null!", expected, nullStr);
		assertNotEquals("Oops! actual is null!", actual, nullStr);
		if(doCompare) {
			assertEquals( "Comparing program content generated by oldCompile() and newCompile().", expected, actual);
		}
		
		return newProgram;
	}
	
	private String printContents(ZomicStatement program) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try { 
			// Cool! New try-with-resources syntax auto closes specified resources in implied finally block
			try (PrintWriter out = new PrintWriter( output /* ... or use System.out*/ )) {
				program.accept( new PrintVisitor( out, symmetry ) ); 
			}
		} catch (ZomicException ex) {
			//ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		return new String(output.toByteArray());
	}
	
	private String printTreeStructure(ZomicStatement program) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try { 
			// Cool! New try-with-resources syntax auto closes specified resources in implied finally block
			try (PrintWriter out = new PrintWriter( output /* ... or use System.out*/ )) {
				program.accept( new ZomicTreeStructureVisitor( out, symmetry ) );
			}
		} catch (ZomicException ex) {
			//ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		return new String(output.toByteArray());
	}
	
	private void assertProgramSize(int size, Walk program) {
		assertNotNull("program should not be null", program);
		assertEquals("program.size", size, program.size());
	}

	private void assertExpectedErrors(Walk program, boolean expectErrors) {
		assertNotNull("program should not be null", program);
		showErrors(program);
		String[] errors = program.getErrors();
		assertNotNull("program.getErrors() should not be null", errors);
		if(expectErrors) {
			assertTrue("Expected program to have 1 or more errors.", errors.length > 0);
		} else {
			assertEquals("Expected program to have 0 errors.", 0, errors.length);
		}
	}
	
	private void showErrors(Walk program) {
		if(program != null) {
			String[] errors = program.getErrors();
			if(errors != null) {
				for( String error: errors ) {
					System.out.println("Error Reported: '" + error + "'.");
				}
			}
		}
	}
	

	////////////////////////////////////////
	// BEGIN test definitions
	////////////////////////////////////////
	// All public void methods 
	//	having names that start with "test" 
	//	and having no parameters 
	//	will be run by the test framework.
	// By convention, I add a leading underscore or two
	//	to the ones I don't want to run. 
	//	e.g. __test_DisableThisTest()
	//  and I append OK to the word test 
	//  for the ones that have passed but which I don't want 
	//  to execute while I work on another one.
	//	e.g. __testOK___DisableThisTestKnowingThatItPasses()
	////////////////////////////////////////

	public void testOK_ListAllZomicAxes() {
		for (String color : symmetry.getDirectionNames()) {
			Direction direction = symmetry.getDirection(color);
			ArrayList<String> list = new ArrayList<>();
			boolean hasNegativeAxes = false;
			boolean isChiral = false;
			for ( Iterator axes = direction.getAxes(); axes.hasNext(); ) {
				Axis axis = (Axis) axes.next();
				String indexName = namingConvention.getName(axis);
				if (NamingConvention.UNKNOWN_AXIS.equals(indexName)) {
					break; 
				} else {
					list.add(indexName);
					if(indexName.startsWith("-")) { hasNegativeAxes = true; }
					if(indexName.endsWith("+") || indexName.endsWith("-")) { isChiral = true; }
				}
			}
			if(!list.isEmpty()) {
				//System.out.println(color + " " + (isChiral ? "has" : "does NOT have") + " Chiral Axes.");
				//System.out.println(color + " " + (hasNegativeAxes ? "knows" : "does NOT know") + " about Negative Axes.");
				assertTrue(color + ": Fully suppported colors must be able to map to negative axes.", 
						hasNegativeAxes || unsupportedColors.contains(color) || notYetZomicColors.contains(color));
				
				Collections.sort(list);
				for(String axisName: list) {
					String fullName = color + " " + axisName;
					System.out.println(fullName);
					if(hasNegativeAxes) {
						assertTrue(fullName + ": Positive axes should have a corresponding negative axis.", list.contains("+" + axisName.substring(1))); 
						assertTrue(fullName + ": Negative axes should have a corresponding positive axis.", list.contains("-" + axisName.substring(1))); 
						if(isChiral) {
							if(axisName.endsWith("-")) {
								assertTrue(fullName + ": Negative chiral axes should have a corresponding positive chiral axis.", list.contains(axisName.substring(0, axisName.length()-1) + "" )); 
							} else {
								assertTrue(fullName + ": Positive chiral axes should have a corresponding negative chiral axis.", list.contains(axisName + "-" )); 
							}
						}
					}
				}
			}
		}
	}

	public void testOK_ZomicNamingConvention() {
		for (String color : symmetry.getDirectionNames()) {
			Direction direction = symmetry.getDirection(color);
			for (Iterator axes = direction.getAxes(); axes.hasNext();) {
				Axis symmetryAxis = (Axis) axes.next();
				String indexName = namingConvention.getName(symmetryAxis);
				Axis namingAxis = namingConvention.getAxis(color, indexName);
				if (namingAxis == null || NamingConvention.UNKNOWN_AXIS.equals(indexName)) {
					String msg = symmetry.getClass().getSimpleName()
							+ " colors include '"
							+ color
							+ "' which is not yet supported by "
							+ namingConvention.getClass().getSimpleName();
					System.out.println(msg);
					
					assertNull(msg, namingAxis);
					assertEquals(msg, NamingConvention.UNKNOWN_AXIS, indexName);
					
					assertTrue(msg, unsupportedColors.contains(color));
					assertFalse(msg, notYetZomicColors.contains(color));
					assertFalse(msg, zomicColors.contains(color));
					break; // break out of the for(axes) loop;
				} else {
					String indexNameCheck = namingConvention.getName(namingAxis);
					if (namingAxis != namingConvention.getAxis(color, indexNameCheck)) {
						String msg = color + " " + indexName + " is unexpectedly mapped to " + indexNameCheck;
						System.out.println(msg);
					}
					String msg = color;
					assertEquals(msg, namingAxis, namingConvention.getAxis(color, indexNameCheck));
					assertTrue(msg, zomicColors.contains(color) || notYetZomicColors.contains(color));
					assertFalse(msg, unsupportedColors.contains(color));
					//System.out.println(color + " " + indexName);
				}
			}
		}
	}

	public void testOK_ZomicLexer() {
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
	
	public void testOK_ZomicParser() {
		String input = "size valid_sizeref.123 red 2";
		ProgramContext programContext = parse( input );
		assertNotNull("programContext should not be null", programContext);
	}
	
	public void testOK_EmptyProgram() {
		// For now, only use compileAndCompareContent for empty programs
		// not compileAndCompare or compileAndCompareStructure
		Walk program = compileAndCompareContent("");
		assertProgramSize(1, program);
		
		program = compileAndCompareContent("\t");
		assertProgramSize(1, program);
		
		program = compileAndCompareContent("\n");
		assertProgramSize(1, program);
		
		program = compileAndCompareContent("\n\n\n");
		assertProgramSize(1, program);
		
		program = compileAndCompareContent("\r");
		assertProgramSize(1, program);
		
		program = compileAndCompareContent("\r\r\r");
		assertProgramSize(1, program);
		
		program = compileAndCompareContent("\r\n");
		assertProgramSize(1, program);
		
		program = compileAndCompareContent("\r\n\r\n\r\n");
		assertProgramSize(1, program);
		
		program = compileAndCompareContent("\n\r");
		assertProgramSize(1, program);
		
		program = compileAndCompareContent("\n\r\n\r\n\r");
		assertProgramSize(1, program);
		
		program = compileAndCompareContent("{}");
		assertProgramSize(1, program);
		
		// oldCompile throws out of memory exception, so don't use compileAndCompare here
		program = newCompile("// single line comment terminated by EOF."); 
		assertProgramSize(1, program);

		program = compileAndCompareContent("// single line comment terminated by newline.\n");
		assertProgramSize(1, program);

		program = compileAndCompareContent("/* multiple line \n comment*/");
		assertProgramSize(1, program);

		program = compileAndCompareContent("{{{/* nested braces */}}}");
		assertProgramSize(1, program);
	}

	public void testOK_LabelStatement() {
		String label = "valid_lowercase_label.234_whatever";
		Walk program = compileAndCompare("label " + label);
		assertProgramSize(1, program);
	}
	
	public void testOK_StrutDefaultValues() {
		Walk program = compileAndCompare("red 0 /* test StrutDefaultValues */");
		assertProgramSize(1, program);
		Iterator it = program .getStatements(); 
		while( it.hasNext() ) {
			ZomicStatement stmt = (ZomicStatement) it.next();
			if(stmt instanceof Move) {
				Move m = (Move)stmt;
				AlgebraicField algebraicField = m.getLength().getField();
				String n = algebraicField.getName();
				assertEquals(3, algebraicField.getOrder());
			}
		}
//		assertEquals("Default strut size should be Medium", 
//				ZomicNamingConvention.MEDIUM,
//				program.getStatements());
	}
		
	public void testOK_RedAliases() {
		Walk program = compileAndCompare("red 0");
		assertProgramSize(1, program);

		program = compileAndCompare("pent 0");
		assertProgramSize(1, program);

		program = compileAndCompare("pentagon 0");
		assertProgramSize(1, program);
	}
	
	public void testOK_BlueAliases() {
		Walk program = compileAndCompare("blue 0");
		assertProgramSize(1, program);

		program = compileAndCompare("rect 0");
		assertProgramSize(1, program);

		program = compileAndCompare("rectangle 0");
		assertProgramSize(1, program);
	}
	
	public void testOK_YellowAliases() {
		Walk program = compileAndCompare("yellow 0");
		assertProgramSize(1, program);

		program = compileAndCompare("tri 0");
		assertProgramSize(1, program);

		program = compileAndCompare("triangle 0");
		assertProgramSize(1, program);
	}
	
	public void testOK_StrutExplicitSizes() {
		// zero
		Walk program = compileAndCompare("size 0 red 2");
		assertProgramSize(1, program);
		
		// +zero
		program = compileAndCompare("size +0 red 2");
		assertProgramSize(1, program);
		
		// confirm that the sign is not lost 
		// since +0 is different from -0.
		// -zero 
		program = compileAndCompare("size -0 red 2");
		assertProgramSize(1, program);
		
		// implied sign
		program = compileAndCompare("size 1 red 2");
		assertProgramSize(1, program);

		// + sign
		program = compileAndCompare("size +1 red 2");
		assertProgramSize(1, program);

		// - sign
		program = compileAndCompare("size -1 red 2");
		assertProgramSize(1, program);

		// how big is too big? 123 is huge
		program = compileAndCompare("size 123 red 2");
		assertProgramSize(1, program);
	}
	
	public void testOK_StrutFullyExplicitSizes() {
		// zero
		Walk program = compileAndCompare("size 0 0 0 red 2");
		assertProgramSize(1, program);
		
		// +zero
		program = compileAndCompare("size +0 +0 +0 red 2");
		assertProgramSize(1, program);
		
		// -zero
		program = compileAndCompare("size -0 -0 -0 red 2");
		assertProgramSize(1, program);
		
		// implied sign
		program = compileAndCompare("size 1 1 1 red 2");
		assertProgramSize(1, program);

		// + sign
		program = compileAndCompare("size +1 +1 +1 red 2");
		assertProgramSize(1, program);

		// - sign
		program = compileAndCompare("size -1 -1 -1 red 2");
		assertProgramSize(1, program);

		// how big is too big? 123 is huge
		program = compileAndCompare("size 123 456 789 red 2");
		assertProgramSize(1, program);
	}
	
	public void testOK_StrutVariableSize() {
		// -99 is the old undocumented indicator for variable size.
		Walk program = compileAndCompare("size -99 red 0");
		assertProgramSize(1, program);
		
		// New alternative to 'size -99' is 'size ?'
		program = newCompile("size ? red 0"); // can't compare new feature with old compiler
		assertProgramSize(1, program);
		System.out.println(printTreeStructure(program));
		System.out.println(printContents(program));
	}
	
	public void testOK_StrutSizeRef() {
		String input = "size any_valid_lowercase_sizeref_even_with_digits...0123456789 red +0";

		// sizeRef was undocumented and seems to be unused by the old code, although it was valid in the old grammar.
		Walk program = compileAndCompare( input );
		assertProgramSize(1, program);
}
	
	public void testOK_StrutNamedSizes() {
		Walk program = compileAndCompare("long red 0");
		assertProgramSize(1, program);
		
		program = compileAndCompare("medium yellow 1");
		assertProgramSize(1, program);
		
		program = compileAndCompare("short blue 2");
		assertProgramSize(1, program);

		// defaults to medium
		program = compileAndCompare("green 3");
		assertProgramSize(1, program);
	}
	
	public void testOK_ZomicStrutColors() {
		int tests = 0;
		for ( String color : zomicColors ) {
			Walk program = compileAndCompare(color + " 0");
			assertProgramSize(1, program);
			tests++; // be sure we tested all colors
		}
		assertTrue("Why didn't we test the other zomicColors?",
				(tests > 0) && (tests == zomicColors.size()) );
	}
	
	public void testOK_NotYetZomicStrutColors() {
		int tests = 0;
		for ( String color : notYetZomicColors ) {
			compileAndCompare(color + " 0", true, true);
			tests++; // be sure we tested all colors
		}
		assertTrue("Why didn't we test the other notYetZomicColors?",
				(tests > 0) && (tests == notYetZomicColors.size()) );
	}
	
	public void testOK_UnsupportedStrutColors() {
		int tests = 0;
		for ( String color : unsupportedColors ) {
			compileAndCompare(color + " 0", true, true);
			tests++; // be sure we tested all colors
		}
		assertTrue("Why didn't we test the other unsupportedColors?",
				(tests > 0) && (tests == unsupportedColors.size()) );
	}
	
	public void testOK_StrutExplicitHalfLengths() {
			// negative axis
			Walk program = compileAndCompare("size -1 -2 -3 half blue -4");
			assertProgramSize(1, program);
			// default positive axis
			program = compileAndCompare("size 1 2 3 half green 4");
			assertProgramSize(1, program);
			// explicit positive axis
			program = compileAndCompare("size +8 +7 +6 half green +0");
			assertProgramSize(1, program);
	}
	
	public void testOK_StrutHandedness() {			
		String[] signs = { "", "+", "-" };	
		for (String sign1 : signs) {
			int tests = 0;
			for (String color : chiralColors) {
				String sign = sign1;
				String script = color + " " + sign + "0" + sign + " /* __test_StrutHandedness */";
				Walk program = compileAndCompare(script);
				assertProgramSize(1, program);
				tests++; // be sure we tested all colors
			}
			assertTrue("Why didn't we test the other chiralColors?",
					(tests > 0) && (tests == chiralColors.size()) );
		}
	}
	
	public void testOK_StrutInvalidHandedness() {			
		String[] signs = { "", "+", "-" };	
		for (String sign1 : signs) {
			int tests = 0;
			for (String color : nonChiralColors) {
				String sign = sign1;
				String script = color + " " + sign + "0" + sign + " /* __test_StrutInvalidHandedness */";
				try {
					Walk program = compileAndCompare(script);
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
	
	public void testOK_StrutValidHalfLengths() {
		Walk program = compileAndCompare("half blue 2");
		assertProgramSize(1, program);
		
		program = compileAndCompare("half green 3");
		assertProgramSize(1, program);
	}
	
	public void testOK_StrutInvalidHalfLengths() {
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
						compileAndCompare(script);
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
	
	public void testOK_MultipleStruts() {
		String allColors = "";
		int n = 0;
		for ( String color : zomicColors ) {
			n++;
			allColors = allColors + color + " -3 ";
		}
		Walk program = compileAndCompare(allColors);
		assertProgramSize(1, program);
	}
	
	public void testOK_BuildModes() {
		Walk program = compileAndCompare("build");
		assertProgramSize(1, program);

		program = compileAndCompare("move");
		assertProgramSize(1, program);

		program = compileAndCompare("destroy");
		assertProgramSize(1, program);
	}
	
	public void testOK_ScaleStatement() {
		Walk program = compileAndCompare("scale 5 red 0");
		assertProgramSize(1, program);

		program = compileAndCompare("scale 7 {blue 6 yellow 1} red 1");
		assertProgramSize(1, program);

		program = compileAndCompare("scale 2 (3) red 2");
		assertProgramSize(1, program);

		program = compileAndCompare("scale 4 (5 6) red 3");
		assertProgramSize(1, program);

		program = compileAndCompare("scale -1 red 4");
		assertProgramSize(1, program);

		program = compileAndCompare("scale 0 red 5");
		assertProgramSize(1, program);

		program = compileAndCompare("scale -99 blue 6");
		assertProgramSize(1, program);

		program = compileAndCompare("scale 1 red 0");
		assertProgramSize(1, program);
	}
	
	public void testOK_EmptyBranchStatement() {
		// TODO: These aren't necessarily even valid tests except that I want to understand why the lexer balks.
		// This one generates a lexer or parser error message saying:
		// "line 1:6 no viable alternative at input '<EOF>'"
		Walk program = compileAndCompare("branch", false, true);
		assertProgramSize(0, program);
		System.out.println( printTreeStructure (program) );
		
		// old version will compile this, but can't walk it
		// new version omits empty nested branches so don't compare them
		program = newCompile("branch {}");
		assertProgramSize(1, program);
		System.out.println( printTreeStructure (program) );
	}
	
	public void testOK_SimpleBranchStatement() {
		Walk program = compileAndCompare("branch { yellow 1 blue -2} black 3");
		assertProgramSize(1, program);
		
		// old version will compile this, but can't walk it
		// new version omits empty nested branches so don't compare them
		program = newCompile("branch { } red 0 green -1");
		System.out.println( printTreeStructure (program) );
		assertProgramSize(1, program);
	}

	public void testOK_BranchStatement() {
		Walk program = compileAndCompare("branch { yellow 1 blue -2} black 1 red 0");
		assertProgramSize(1, program);

		program = compileAndCompare("branch yellow 1 blue -2 black 1 red 0");
		assertProgramSize(1, program);
	}

	public void testOK_BranchNesting() {
		Walk program = compileAndCompare("branch { red 1 branch { blue 1 branch { yellow 0 } blue -1 } red -1 }");
		assertProgramSize(1, program);
		
		program = compileAndCompare("branch { branch { branch { red 0 } } }");
		assertProgramSize(1, program);
	}

	public void testOK_FromStatement() {
		Walk program = compileAndCompare("from yellow 1 blue 2 black 3 red 4");
		assertProgramSize(1, program);

		program = compileAndCompare("from yellow -1 { blue -2 black -3 } red -4");
		assertProgramSize(1, program);
	}

	public void testOK_RepeatStatement() {
		Walk 
		// TODO: This isn't necessarily even a valid test except that I want to understand why the lexer balks.
		// This one generates a lexer or parser error message saying:
		// "line 1:8 no viable alternative at input '<EOF>'"
//		program = compileAndCompare("repeat 0");
//		assertProgramSize(1, program);
		
		program = compileAndCompare("repeat 0 { red -0 }");
		assertProgramSize(1, program);

		// negative numbers are allowed but the sign is silently removed
		program = compileAndCompare("repeat -1 { red -2 }"); 
		assertProgramSize(1, program);
		
		program = compileAndCompare("repeat 1 { red 2 }");
		assertProgramSize(1, program);
		
		// old version will compile this, but can't walk it
		// new version omits empty nested branches so don't compare them
		program = newCompile("repeat 2 { }");
		System.out.println( printTreeStructure (program) );
		assertProgramSize(1, program);
		
		program = compileAndCompare("repeat 3 { yellow 1 blue -2} black 1 red 0");
		assertProgramSize(1, program);

		program = compileAndCompare("repeat 3 yellow 1 blue -2 black 1 red 0");
		assertProgramSize(1, program);
	}
		
	public void testOK_SaveStatement() {
		ArrayList<String> states = new ArrayList<>();
		states.add("location");
		states.add("scale");
		states.add("orientation");
		states.add("build");
		states.add("all");
		int tests = 0;
		for ( String state : states ) {
			Walk program = compileAndCompare("save " + state + " red 0 blue 0 yellow 0");
			assertProgramSize(1, program);

			program = compileAndCompare("save " + state + " {red 1} blue 1 yellow 1");
			assertProgramSize(1, program);

			program = compileAndCompare("save " + state + " {red 2 blue 2} yellow 2");
			assertProgramSize(1, program);

			program = compileAndCompare("save " + state + " {red 3 blue 3 yellow 3}");
			assertProgramSize(1, program);
			tests++; // be sure we tested all states
		}
		assertTrue("Why didn't we test the other save states?",
				(tests > 0) && (tests == states.size()) );
	}

	public void testOK_RotateStatement() {
		Walk 
		program = compileAndCompare("rotate 2 around yellow +0 red -2");
		assertProgramSize(1, program);
		
		program = compileAndCompare("rotate 0 around red 0 yellow 1");
		assertProgramSize(1, program);
		
		program = compileAndCompare("rotate -1 around red -1 yellow 2"); // non-positive numbers are allowed but should do nothing. They should act as comments
		assertProgramSize(1, program);
		
		program = compileAndCompare("rotate +1 around red +1 yellow 3");
		assertProgramSize(1, program);
		
		program = compileAndCompare("rotate 42 around red 0 { yellow 0 blue -2} black 1 red 0");
		assertProgramSize(1, program);
	}
	
	public void testOK_ReflectStatement() {
		Walk 
		program = compileAndCompare("reflect through center green 0");
		assertProgramSize(1, program);

		// any negative axis index will silently be ignored
		program = compileAndCompare("reflect through -14 green 1");
		assertProgramSize(1, program);

		// old way can't handle ignoring inline comment. new one can, so don't actually compare them
		program = compileAndCompare("reflect through /*comment*/ 2 green 2", false);
		assertProgramSize(1, program);
		
		// old way can't handle ignoring 'blue'. new one can, so don't actually compare them
		String script = "reflect through blue 2 green 3";
		program = oldCompile(script);
		assertProgramSize(1, program);
		assertExpectedErrors(program, true);
		System.out.println(printTreeStructure(program));
		System.out.println(printContents(program));
		// new
		program = newCompile(script);
		assertProgramSize(1, program);
		assertExpectedErrors(program, false);
		System.out.println(printTreeStructure(program));
		System.out.println(printContents(program));
		
		// old way can't handle ignoring 'red'. new one can, so don't actually compare them
		// new way allows ONLY blue and blue alias axes
		program = compileAndCompare("reflect through red 2 green 4", false, true);
		assertProgramSize(0, program);		
	}

	public void testOK_SymmetryStatement() {
		Walk 
		// old version will compile this, but can't walk it
		// new version omits empty nested branches so don't compare them
		program = compileAndCompareStructure("symmetry { }", false);
		assertProgramSize(1, program);

		// Icosahedral (around the origin)
		program = compileAndCompare("symmetry {green -0}");
		assertProgramSize(1, program);

		// ReflectThroughOrigin
		program = compileAndCompare("symmetry through center {green 0}");
		assertProgramSize(1, program);

		// MirrorThroughBlueAxis 
		// implied blue axis is not specified for backward compatibility
		program = compileAndCompare("symmetry through 14 {green 2}");
		assertProgramSize(1, program);
		
		// negative axis index will be silently ignored here, just like the reflect statement
		program = compileAndCompare("symmetry through -14 {green 2}");
		assertProgramSize(1, program);
		
		// Optionaly specifying "blue" axis is a new feature
		// old version won't compile this
		// new version allows but ignores the blue axis spec
		program = newCompile("symmetry through blue -13 {green 1}");
		System.out.println( printTreeStructure (program) );
		assertProgramSize(1, program);

		// RotateAroundAxis
		program = compileAndCompare("symmetry around red 0 {green -0}");
		assertProgramSize(1, program);

		// positive chiral index
		program = compileAndCompare("symmetry around black 1+ {green -1}");
		assertProgramSize(1, program);

		// negative chiral index
		program = compileAndCompare("symmetry around black 1- {green -2}");
		assertProgramSize(1, program);

		// negative direction with positive chiral index
		program = compileAndCompare("symmetry around black -2+ {green -3}");
		assertProgramSize(1, program);

		// negative direction with negative chiral index
		program = compileAndCompare("symmetry around black -2- {green -4}");
		assertProgramSize(1, program);

	}

	public void testOK_MisplacedIntegerPolarity() {
		// These should not parse without errors. 
		// Be sure we catch the errors in the new version.
		// old version silently ignored the error.

		int count = 0;
		String scripts[] = { 
			"size 1- red 1", // negative
			"size 2+ red 2" // positive
		};
		for(String script : scripts) {
			count ++;
			Walk program = oldCompile(script);
			assertProgramSize(1, program);
			assertExpectedErrors(program, false);
			System.out.println(printTreeStructure(program));
			System.out.println(printContents(program));
			// new
			program = newCompile(script);
			assertProgramSize(0, program);
			assertExpectedErrors(program, true);
			System.out.println(printTreeStructure(program));
			System.out.println(printContents(program));
		}
		assertEquals("Expected to test all scripts", scripts.length, count);
	}
	
	private int regressionTestCount = 0;
	public void testOK_RegressionFiles() {
		regressionTestCount = 0;
		traverseDirectory(new File( "src/regression/files/Zomic/" ));
		traverseDirectory(new File( "src/main/resources/com/vzome/core/parts/" ));
		System.out.println("Regression tested " + regressionTestCount + " zomic files.");
	}
	
	private void traverseDirectory(File directory) {		
		assertTrue(directory.getName() + " must be a directory.", directory.isDirectory());
		String[] files = directory.list();
        if (files != null) {
			for (String file : files) {
				File fileOrDirectory = new File(directory, file);
				if( fileOrDirectory.isDirectory()) {
					traverseDirectory(fileOrDirectory);
				} else if (fileOrDirectory.isFile()) {
					verifyZomicFile(fileOrDirectory);
				} else {
					System.out.println("Skipping file: " + fileOrDirectory.getAbsolutePath());
				}
			}
		}
	}
	
	private void verifyZomicFile(File file) {
		if( "zomic".equals(getFileExtension( file ).toLowerCase()) ) {
			String absoluteFilePath = file.getAbsolutePath();
			regressionTestCount++;
			System.out.println("Started # " + regressionTestCount + " file: " + absoluteFilePath);
			switch (file.getName()) {
				// Old way can't handle these, but new way can...
				// All of the files specifically named here are under src/regression/files/Zomic/
				// All of their corresponding zomic-pp files are 0 length since the old way couldn't handle them.
				case "allPurple.zomic":
				case "geodesics.zomic":
				case "orangeStrutModel2.zomic":
				case "pentagonHelix.zomic":
				case "polarGreen1.zomic":
				case "shortRedStrutModel.zomic":
					newCompileAndShow(file);
					break;

				case "system.zomic": 
					// system.zomic has unknown keyword "star". 
					// Old way just quits processing the file at that point with no complaint.
					//	the result of the old way can be seen in system.zomic-pp
					// 
					// Original ZomicASTCompiler using ZomicLexer reports the unexpected keyword 
					//	but recovers and continued to process the rest of the file ignoring the lexer error.
					// New ZomicASTCompiler using StrictZomicLexer throws an exception 
					//  at the first lexer or parser error.
					verifyBailErrorStrategy(file);
					break;
					
			default:
				compileAndCompareFile( file );
				break;
			}
			System.out.println("Ending # " + regressionTestCount + " file: " + absoluteFilePath);
		}
	}
	
	private void verifyBailErrorStrategy(File file) {
		String absoluteFilePath = file.getAbsolutePath();
		final boolean expectErrors = true;
		Walk program = newCompileFile(absoluteFilePath, expectErrors);
		assertProgramSize(0, program);
		assertExpectedErrors(program, true);
		System.out.println(printTreeStructure(program));
		System.out.println(printContents(program));
	}
	
	private static String getFileExtension( File file ) {
		String ext = file.getAbsolutePath();
		return ext.substring( ext.lastIndexOf( '.' ) + 1 );
	}
}
