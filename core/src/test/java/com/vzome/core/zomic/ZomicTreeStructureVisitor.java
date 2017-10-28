package com.vzome.core.zomic;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.zomic.program.Nested;
import com.vzome.core.zomic.program.Permute;
import com.vzome.core.zomic.program.Reflect;
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
	
	private final IcosahedralSymmetry symmetry;
	private final ZomicNamingConvention namingConvention;

	public ZomicTreeStructureVisitor( PrintWriter out, IcosahedralSymmetry symm ) {
		super();
		m_out = out;
		symmetry = symm;
		namingConvention = new ZomicNamingConvention( symmetry );
		println("");
	}
	
	protected final void println( String string )
    {
		for ( int i = 0; i < indent; i++ ) {
			m_out.print( "    " );
		}
		m_out.println( string );
	}
	
	@Override
	public  void visitSave( Save save, int state ) throws ZomicException
    {
		println("Save( state = " + state + " )" );
		super.visitSave( save, state );
    }

	@Override
	public  void visitWalk( Walk walk ) throws ZomicException
	{
		println("Walk" );
		if ( walk .size() == 1 ) {
			walk .iterator().next() .accept( this );
			return;
		}
		println( "{" );
		++ indent;
		super .visitWalk( walk );
		-- indent;
		println( "}" );
	}

	@Override
	public  void visitRepeat( Repeat repeated, int repetitions ) throws ZomicException
	{
		println("Repeat( repetitions = " + repetitions + " )" );
		visitNested( repeated );
	}

	@Override
	public void visitBuild( boolean build, boolean destroy )
	{
		println("Build( build = " + build + ", destroy = " + destroy + " )" );
	}

	@Override
	public void visitRotate( Axis axis, int steps )
	{
		String axisName = namingConvention.getName(axis);
		println( "Rotate( axis = " + axisName + ", steps = " + steps + " )");
	}

	@Override
	public void visitReflect( Axis blueAxis )
	{
		String axisName = "center";
		if(blueAxis != null) {
			axisName = namingConvention.getName(blueAxis);
		}
		println( "Reflect through " + axisName );
	}

	@Override
	public void visitMove( Axis axis, AlgebraicNumber length )
	{
		String axisName = namingConvention.getName(axis);
		String len = length.toString( AlgebraicField.ZOMIC_FORMAT );
		println( "Move( axis = " + axisName + ", length = " + len + " )" );
	}

	@Override
	public void visitSymmetry( final Symmetry model, Permute permute ) throws ZomicException
	{
		StringBuilder msg = new StringBuilder("Symmetry");
		if ( permute != null ){
			msg.append(" ( ");
			Axis axis = permute .getAxis();
			if ( permute instanceof Reflect ){
				msg.append("through ");
				if ( axis == null ) {
					msg.append("center");
				} else {
					String axisName = namingConvention.getName(axis);
					msg.append(axisName);
				}
			} else {
				String axisName = namingConvention.getName(axis);
				msg.append("around ").append(axisName);
			}
			msg.append(" )");
		}
		println(msg.toString());
		super .visitSymmetry( model, permute );
	}

	@Override
	public void visitNested( Nested compound ) throws ZomicException {
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

	@Override
	public void visitScale( AlgebraicNumber size )
    {
		println( "Scale 0 ( " + size.toString( AlgebraicField.ZOMIC_FORMAT )  + " )" );
	}

	@Override
	public void visitUntranslatable( String message )
	{
	    println( "Untranslatable( '" + message + "' )" );
	}

	@Override
	public void visitLabel( String id )
	{
		println( "Label( id = " + id + " )");
	}
	
}
