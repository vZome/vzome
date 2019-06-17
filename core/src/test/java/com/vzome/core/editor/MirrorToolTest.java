package com.vzome.core.editor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.PolygonFromVertices;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.math.Projection;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Panel;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

public class MirrorToolTest
{
	@Test
	public void testSelectionValidation()
	{
		// TODO this is an *absurd* amount of code to test the tool.
		// Panel should accept varargs.
		//  Manifestation should not require Constructions.
		AlgebraicField field = new PentagonField();
		AlgebraicVector origin = field .origin( 3 );
		AlgebraicVector x = field .basisVector( 3, 0 );
		AlgebraicVector y = field .basisVector( 3, 1 );
		AlgebraicVector z = field .basisVector( 3, 2 );

		Point originPoint = new FreePoint( origin );
		Point xpoint = new FreePoint( x );
		Point ypoint = new FreePoint( y );
		Point zpoint = new FreePoint( z );
		Polygon polygon1 = new PolygonFromVertices( new Point[]{ originPoint, xpoint, ypoint } );
		Polygon polygon2 = new PolygonFromVertices( new Point[]{ originPoint, xpoint, zpoint } );

		List<AlgebraicVector> vertices = new ArrayList<>();
		vertices .add( origin );
		vertices .add( x );
		vertices .add( y );
		Panel panel1 = new Panel( vertices );
		panel1 .addConstruction( polygon1 );
		
		vertices = new ArrayList<>();
		vertices .add( origin );
		vertices .add( x );
		vertices .add( z );
		Panel panel2 = new Panel( vertices );
		panel2 .addConstruction( polygon2 );
		
		Selection selection = new Selection();
		Tool tool = createTool( selection, originPoint );
		assertNotNull( "(nothing selected)", tool .checkSelection( false ) );

		selection = new Selection();
		selection .select( panel1 );
		tool = createTool( selection, originPoint );
		assertNull( "(just panel selected)", tool .checkSelection( false ) );
		
		selection = new Selection();
		selection .select( panel1 );
		selection .select( panel2 );
		tool = createTool( selection, originPoint );
		assertNotNull( "(two panels selected)", tool .checkSelection( false ) );

		Connector ball1 = new Connector( origin );
		ball1 .addConstruction( originPoint );  // TODO remove this requirement
		Connector ball2 = new Connector( x );
		ball2 .addConstruction( xpoint );
		Segment segment1 = new SegmentJoiningPoints( originPoint, xpoint );
		Strut strut = new Strut( origin, x );
		strut .addConstruction( segment1 );
		Segment segment2 = new SegmentJoiningPoints( originPoint, ypoint );
		Strut strut2 = new Strut( origin, y );
		strut2 .addConstruction( segment2 );
		
		selection = new Selection();
		selection .select( panel1 );
		selection .select( strut );
		tool = createTool( selection, originPoint );
		assertNotNull( "(panel and strut selected)", tool .checkSelection( false ) );
		
		selection = new Selection();
		selection .select( panel1 );
		selection .select( ball1 );
		tool = createTool( selection, originPoint );
		assertNotNull( "(panel and ball selected)", tool .checkSelection( false ) );
		
		selection = new Selection();
		selection .select( ball1 );
		selection .select( strut );
		tool = createTool( selection, originPoint );
		assertNull( "(strut and ball selected)", tool .checkSelection( false ) );
		
		selection = new Selection();
		selection .select( ball1 );
		selection .select( strut );
		selection .select( strut2 );
		tool = createTool( selection, originPoint );
		assertNotNull( "(ball and 2 struts selected)", tool .checkSelection( false ) );
		
		selection = new Selection();
		selection .select( ball1 );
		selection .select( strut );
		selection .select( ball2 );
		tool = createTool( selection, originPoint );
		assertNotNull( "(strut and 2 balls selected)", tool .checkSelection( false ) );
		
		selection = new Selection();
		selection .select( ball1 );
		selection .select( strut );
		selection .select( panel1 );
		tool = createTool( selection, originPoint );
		assertNotNull( "(ball, strut, and panel selected)", tool .checkSelection( false ) );
	}
	
	private Tool createTool( Selection selection, Point originPoint )
	{
		AlgebraicField field = originPoint .getField();
		RealizedModel model = new RealizedModel( field, new Projection.Default( field ) );
		EditorModel editor = new EditorModel( model, selection, false, originPoint, null, null );
		ToolsModel tools = new ToolsModel( null, originPoint );
		tools .setEditorModel( editor );
		return new MirrorTool.Factory( tools ) .createToolInternal( "1/foo" );
	}
}
