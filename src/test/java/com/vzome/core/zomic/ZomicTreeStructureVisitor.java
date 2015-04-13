package com.vzome.core.zomic;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.zomic.program.Nested;
import com.vzome.core.zomic.program.Permute;
import com.vzome.core.zomic.program.Repeat;
import com.vzome.core.zomic.program.Save;
import com.vzome.core.zomic.program.Symmetry;
import com.vzome.core.zomic.program.Visitor;
import com.vzome.core.zomic.program.Walk;
import com.vzome.core.zomic.program.ZomicStatement;
import java.io.PrintWriter;

/**
 *
 * @author David Hall
 */
public class ZomicTreeStructureVisitor 
	extends Visitor.Default 
{
	protected PrintWriter m_out;
	
	protected int indent = 0;
	
	protected final ZomicNamingConvention naming = new ZomicNamingConvention( new IcosahedralSymmetry( new PentagonField(), "default" ) );

	public ZomicTreeStructureVisitor( PrintWriter out ) {
		super();
		m_out = out;
		println("");
	}
	
	protected void println( String string )
    {
		for ( int i = 0; i < indent; i++ ) {
			m_out .print( "    " );
		}
		m_out .println( string );
	}

	public  void visitSave( Save save, int state ) throws ZomicException
    {
		println("Save( state = " + state + " )" );
		super.visitSave( save, state );
    }

	public  void visitWalk( Walk walk ) throws ZomicException
	{
		println("Walk" );
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


	public  void visitRepeat( Repeat repeated, int repetitions ) throws ZomicException
	{
		println("Repeat( repetitions = " + repetitions + " )" );
		visitNested( repeated );
	}

	public void visitBuild( boolean build, boolean destroy )
	{
		println("Build( build = " + build + ", destroy = " + destroy + " )" );
	}

	public void visitRotate( Axis axis, int steps )
	{
		println( "Rotate( axis = " + naming .getName( axis ) + ", steps = " + steps + " )");
	}

	public void visitReflect( Axis blueAxis )
	{
		println( "Reflect through " + ( blueAxis == null ? "center" : naming .getName( blueAxis ) ) );
	}

	public void visitMove( Axis axis, AlgebraicNumber length )
	{
		 println( "Move( axis = " + axis.getDirection().getName() + " " + axis.getOrientation() + ", length = " + length.toString( AlgebraicField.ZOMIC_FORMAT ) + " )" );
	}

	public void visitSymmetry( final Symmetry model, Permute permute ) throws ZomicException
	{
		println( "Symmetry( ... )" );
//		if ( permute != null ){
//			Axis axis = permute .getAxis();
//			if ( permute instanceof Reflect ){
//				print( "through " );
//				if ( axis == null )
//					println( "center");
//				else
//					// TODO this is wrong, prepends "blue "
//					println( naming .getName( axis ) );
//			}
//			else
//				println( "around " + naming .getName( axis ) );
//		}
		super .visitSymmetry( model, permute );
	}

	public 
	void visitNested( Nested compound ) throws ZomicException {
		println( "Nested" );
		//println( "/* { */" );
		++indent;
		ZomicStatement body = compound.getBody();
		if(body != null) {
			body.accept( this );
		} else {
			println("/* body should never be null */");
		}
		--indent;
		//println( "/* } */" );
	}

	public void visitScale( AlgebraicNumber size )
    {
		println( "Scale 0 ( " + size.toString( AlgebraicField.ZOMIC_FORMAT )  + " )" );
	}

	public void visitUntranslatable( String message )
	{
	    println( "Untranslatable( message = '" + message + "')" );
	}

	public void visitLabel( String id )
	{
		println( "Label( id = " + id + " )");
	}
	
}
