
package com.vzome.core.zomic.program;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Iterator;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.render.ZomicEventHandler;
import com.vzome.core.zomic.ZomicException;
import com.vzome.core.zomic.ZomicNamingConvention;
import com.vzome.core.zomic.parser.Parser;

public class PrintVisitor extends Visitor .Default{

	protected PrintWriter m_out;
	
	protected int indent = 0;
	
	protected boolean atNewLine = true;
	
	private final ZomicNamingConvention naming;

	public  PrintVisitor( PrintWriter out, IcosahedralSymmetry symmetry ) {
		super();

			m_out = out;
			naming = new ZomicNamingConvention( symmetry );
		}

    public  void visitSave( Save save, int state ) throws ZomicException
    {
        if ( state == ZomicEventHandler .LOCATION ){
			print( "branch " );
			super .visitSave( save, state );
			return;
        }
        if ( state == ZomicEventHandler .ACTION ) {
        	ZomicStatement body = save .getBody();
        	if ( body instanceof Walk ){
        		Walk walk = (Walk) body;
        		if ( walk .size() == 2 ){
        			Iterator it = walk .getStatements();
        			ZomicStatement stmt = (ZomicStatement) it .next();
        			if ( stmt instanceof Build
        			&& ((Build) stmt) .justMoving() ) {
        				stmt = (ZomicStatement) it .next();
        				print( "from " );
        				stmt .accept( this );
        				return;
        			}
        		}
        	}
        }
        print( "save " );
        if ( state != ZomicEventHandler .ALL )
            switch ( state ) {
                case ZomicEventHandler .ORIENTATION :
                    print( "orientation " );
                    break;

				case ZomicEventHandler .SCALE :
					print( "scale " );
					break;

				case ZomicEventHandler .ACTION :
					print( "build " );
					break;
            }
        else
        	print( "all " );
		super .visitSave( save, state );
    }

	public  void visitWalk( Walk walk ) throws ZomicException
	{
		if ( walk .size() == 1 ) {
			((ZomicStatement) walk .getStatements() .next()) .accept( this );
			return;
		}
		println( "{" );
		++ indent;
		super .visitWalk( walk );
		-- indent;
		println( "}" );
	}


	public 
	void visitRepeat( Repeat repeated, int repetitions ) throws ZomicException
	{
		print( "  repeat " + repetitions + " " );
		visitNested( repeated );
	}

	public void visitBuild( boolean build, boolean destroy )
	{
		if ( build == destroy )
			println( "move" );
		else if ( build )
			println( "build" );
		else
			println( "destroy" );
	}

	public 
	void visitRotate( Axis axis, int steps )
	{
		println( "rotate " + steps + " around "
				+ naming .getName( axis ) );
	}

	public void visitReflect( Axis blueAxis )
	{
		print( "reflect through " );
		if ( blueAxis == null ){
			println( "center");
			return;
		}
		// TODO this is wrong, prepends "blue "
		println( naming .getName( blueAxis ) );
	}

	public
	void visitMove( Axis axis, AlgebraicNumber length )
	{
		 println( //"/* MOVE NOT PRINTED: */ " + 
				 axis.getDirection().getName() + " " + axis.getOrientation() + " : " + length.toString( AlgebraicField.ZOMIC_FORMAT ) );
	    // TODO translate back to original Zomic naming convention
	}

	public 
	void visitSymmetry( final Symmetry model, Permute permute ) throws ZomicException
	{
		print( "symmetry " );
		if ( permute != null ){
			Axis axis = permute .getAxis();
			if ( permute instanceof Reflect ){
				print( "through " );
				if ( axis == null )
					println( "center");
				else
					// TODO this is wrong, prepends "blue "
					println( naming .getName( axis ) );
			}
			else
				println( "around " + naming .getName( axis ) );
		}
		super .visitSymmetry( model, permute );
	}

	public 
	void visitNested( Nested compound ) throws ZomicException {
		++indent;
		compound .getBody() .accept( this );
		--indent;
	}

	public 
	void visitScale( AlgebraicNumber size )
    {
		println( "scale 0 ( " + size.toString( AlgebraicField.ZOMIC_FORMAT )  + " )" );
	}

	protected void print( String string )
    {
		if ( atNewLine ){
			atNewLine = false;
			for ( int i = 0; i < indent; i++ )
				m_out .print( "  " );
		}
		m_out .print( string );
	}

	protected void println( String string ){
		print( string );
		m_out .println();
		atNewLine = true;
	}
        
	public
	void visitUntranslatable( String message )
	{
	    println( "untranslatable // " + message );
	}

	public void visitLabel( String id )
	{
		println( "label " + id );
	}

	
	public static void main( String[] args )
	{
		try {
			File file = new File( args[0] );
			PentagonField field = new PentagonField();
            IcosahedralSymmetry symmetry = new IcosahedralSymmetry( field, "solid connectors" );
			ZomicStatement program = Parser .parse( new FileInputStream( file ), symmetry );
			PrintWriter out = new PrintWriter( System.out );
			program .accept( new PrintVisitor( out, symmetry ) );
			out .close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ZomicException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}


