package com.vzome.core.zomic;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.PentagonField;
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
	
	private final ZomicNamingConvention namingConvention;

	public ZomicTreeStructureVisitor( PrintWriter out ) {
		super();
		m_out = out;
		PentagonField field = new PentagonField();
		IcosahedralSymmetry symmetry = new IcosahedralSymmetry( field, "solid connectors" );
		namingConvention = new ZomicNamingConvention( symmetry );
		println("");
	}
	
	protected void println( String string )
    {
		for ( int i = 0; i < indent; i++ ) {
			m_out.print( "    " );
		}
		m_out.println( string );
	}

	protected String getAxisName( Axis axis) {
		String axisName = "<null>";
		if(axis != null) {
			axisName = namingConvention.getName( axis );
			// TODO: namingConvention always returns UNKNOWN_AXIS. 
			// TODO: Fix that problem then get rid of the rest of this hack.
			if(ZomicNamingConvention.UNKNOWN_AXIS.equals(axisName)) {
				axisName = axis.getDirection().getName() + " " + Integer.toString(axis.getOrientation());
				if(axis.getSense() == 1) {
					axisName += "-";
				}
			} else {
				println("TODO: This is just a breakpoint for debugging. We should always get here. Why don't we???");
			}
		}
		return axisName;
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
		String axisName = getAxisName(axis);
		println( "Rotate( axis = " + axisName + ", steps = " + steps + " )");
	}

	public void visitReflect( Axis blueAxis )
	{
		String axisName = "center";
		if(blueAxis != null) {
			axisName = getAxisName(blueAxis);
		}
		println( "Reflect through " + axisName );
	}

	public void visitMove( Axis axis, AlgebraicNumber length )
	{
		String axisName = getAxisName(axis);
		String len = length.toString( AlgebraicField.ZOMIC_FORMAT );
		println( "Move( axis = " + axisName + ", length = " + len + " )" );
	}

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
					String axisName = getAxisName(axis);
					msg.append(axisName);
				}
			} else {
				String axisName = getAxisName(axis);
				msg.append("around " + axisName );
			}
			msg.append(" )");
		}
		println(msg.toString());
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
	    println( "Untranslatable( '" + message + "' )" );
	}

	public void visitLabel( String id )
	{
		println( "Label( id = " + id + " )");
	}
	
}
