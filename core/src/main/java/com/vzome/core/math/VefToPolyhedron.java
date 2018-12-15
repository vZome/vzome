package com.vzome.core.math;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;

public class VefToPolyhedron extends VefParser
{
	protected Polyhedron polyhedron;
	
    public static Polyhedron importPolyhedron( AlgebraicField field, String vef )
    {
    	Polyhedron result = new Polyhedron( field );
    	VefToPolyhedron parser = new VefToPolyhedron( result );
    	parser .parseVEF( vef, field );
    	return result;
    }

	public VefToPolyhedron( Polyhedron polyhedron )
	{
		this .polyhedron = polyhedron;
	}

	@Override
	protected void addVertex( int index, AlgebraicVector location )
	{
		this .polyhedron .addVertex( this .getField() .projectTo3d( location, true ) );
	}

	@Override
	protected void addFace( int index, int[] verts )
	{
		Polyhedron.Face face = this .polyhedron .newFace();
		for ( int i : verts )
			face .add( i );
		this .polyhedron .addFace( face );
	}

	@Override
	protected void startVertices( int numVertices ) {}

	@Override
	protected void startFaces( int numFaces ) {}

	@Override
	protected void startEdges( int numEdges ) {}

	@Override
	protected void addEdge( int index, int v1, int v2 ) {}

	@Override
	protected void startBalls( int numVertices ) {}

	@Override
	protected void addBall( int index, int vertex ) {}

}
