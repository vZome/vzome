
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.api;

import java.util.ArrayList;
import java.util.List;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;

public class Command {
	
	public interface Delegate 
	{
		AlgebraicField getField();
		
		Selection getInputs();
		
		com.vzome.core.model.Connector addBall( AlgebraicVector loc );
		
		com.vzome.core.model.Strut addStrut( AlgebraicVector start, AlgebraicVector end );
		
		com.vzome.core.model.Panel addPanel( AlgebraicVector... vertices );
		
		void select( Manifestation man );
	}
	
	private final List<Object> selection;
	private final Delegate delegate;

	public Command( Delegate delegate )
	{
		this.delegate = delegate;
		this.selection = new ArrayList<>();
        for (Manifestation man : delegate .getInputs()) {
            if ( man instanceof Connector )
            {
                this .selection .add( new Ball( (Connector) man ) );
               
            }
            else if ( man instanceof com.vzome.core.model.Strut )
            {
                this .selection .add( new Strut( (com.vzome.core.model.Strut) man ) );
            }
            else if ( man instanceof com.vzome.core.model.Panel )
            {
                this .selection .add( new Panel( (com.vzome.core.model.Panel) man ) );
            }
        }
	}
	
	public Number newNumber( int... coords )
	{
		return new Number( this .delegate .getField(), coords );
	}
	
	public void log( String text )
	{
		System .out .println( text );
	}
	
	public List<Object> selection()
	{
		return this .selection;
	}
	
	public void select( Object obj )
	{
		if ( obj instanceof Ball )
			this .delegate .select( ((Ball) obj) .getManifestation() );
		else if ( obj instanceof Strut )
			this .delegate .select( ((Strut) obj) .getManifestation() );
	}
	
	public Ball addBall( Vector loc )
	{
		return new Ball( this .delegate .addBall( loc .getAlgebraicVector() ) );
	}
	
	public Strut addStrut( Vector start, Vector end )
	{
		return new Strut( this .delegate .addStrut( start .getAlgebraicVector(), end .getAlgebraicVector() ) );
	}
	
	public Panel addPanel( Vector... vertices )
	{
	    AlgebraicVector[] vertexInts = new AlgebraicVector[ vertices .length ];
		for ( int i = 0; i < vertices.length; i++) {
			vertexInts[ i ] = vertices[ i ] .getAlgebraicVector();
		}
		return new Panel( this .delegate .addPanel( vertexInts ) );
	}
}
