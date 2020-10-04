package com.vzome.core.editor;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.math.Projection;
import com.vzome.core.model.Connector;
import com.vzome.core.model.ConnectorImpl;
import com.vzome.core.model.Panel;
import com.vzome.core.model.PanelImpl;
import com.vzome.core.model.RealizedModelImpl;
import com.vzome.core.model.Strut;
import com.vzome.core.model.StrutImpl;
import com.vzome.core.tools.MirrorTool;

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
		Panel panel1 = new PanelImpl( vertices );
		panel1 .addConstruction( polygon1 );
		
		vertices = new ArrayList<>();
		vertices .add( origin );
		vertices .add( x );
		vertices .add( z );
		Panel panel2 = new PanelImpl( vertices );
		panel2 .addConstruction( polygon2 );
		
		EditorModel editor = createEditor( originPoint );
		Tool tool = createTool( editor, originPoint );
		assertNotNull( "(nothing selected)", tool .checkSelection( false ) );

		editor = createEditor( originPoint );
		editor .getSelection() .select( panel1 );
		tool = createTool( editor, originPoint );
		assertNull( "(just panel selected)", tool .checkSelection( false ) );
		
		editor = createEditor( originPoint );
		editor .getSelection() .select( panel1 );
		editor .getSelection() .select( panel2 );
		tool = createTool( editor, originPoint );
		assertNotNull( "(two panels selected)", tool .checkSelection( false ) );

		Connector ball1 = new ConnectorImpl( origin );
		ball1 .addConstruction( originPoint );  // TODO remove this requirement
		Connector ball2 = new ConnectorImpl( x );
		ball2 .addConstruction( xpoint );
		Segment segment1 = new SegmentJoiningPoints( originPoint, xpoint );
		Strut strut = new StrutImpl( origin, x );
		strut .addConstruction( segment1 );
		Segment segment2 = new SegmentJoiningPoints( originPoint, ypoint );
		Strut strut2 = new StrutImpl( origin, y );
		strut2 .addConstruction( segment2 );
		
		editor = createEditor( originPoint );
		editor .getSelection() .select( panel1 );
		editor .getSelection() .select( strut );
		tool = createTool( editor, originPoint );
		assertNotNull( "(panel and strut selected)", tool .checkSelection( false ) );
		
		editor = createEditor( originPoint );
		editor .getSelection() .select( panel1 );
		editor .getSelection() .select( ball1 );
		tool = createTool( editor, originPoint );
		assertNotNull( "(panel and ball selected)", tool .checkSelection( false ) );
		
		editor = createEditor( originPoint );
		editor .getSelection() .select( ball1 );
		editor .getSelection() .select( strut );
		tool = createTool( editor, originPoint );
		assertNull( "(strut and ball selected)", tool .checkSelection( false ) );
		
		editor = createEditor( originPoint );
		editor .getSelection() .select( ball1 );
		editor .getSelection() .select( strut );
		editor .getSelection() .select( strut2 );
		tool = createTool( editor, originPoint );
		assertNotNull( "(ball and 2 struts selected)", tool .checkSelection( false ) );
		
		editor = createEditor( originPoint );
		editor .getSelection() .select( ball1 );
		editor .getSelection() .select( strut );
		editor .getSelection() .select( ball2 );
		tool = createTool( editor, originPoint );
		assertNotNull( "(strut and 2 balls selected)", tool .checkSelection( false ) );
		
		editor = createEditor( originPoint );
		editor .getSelection() .select( ball1 );
		editor .getSelection() .select( strut );
		editor .getSelection() .select( panel1 );
		tool = createTool( editor, originPoint );
		assertNotNull( "(ball, strut, and panel selected)", tool .checkSelection( false ) );
    }
    
    private EditorModel createEditor( Point originPoint )
    {
        AlgebraicField field = originPoint .getField();
        RealizedModelImpl model = new RealizedModelImpl( field, new Projection.Default( field ) );
        return new EditorModelImpl( model, originPoint, null, null, new HashMap<>() );
    }
    
    private Tool createTool( EditorModel editor, Point originPoint )
    {
		ToolsModel tools = new ToolsModel( null, originPoint );
		tools .setEditorModel( editor );
		return new MirrorTool.Factory( tools ) .createToolInternal( "1/foo" );
	}
}
