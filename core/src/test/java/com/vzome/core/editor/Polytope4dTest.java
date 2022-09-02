package com.vzome.core.editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.editor.api.OrbitSource;
import com.vzome.core.edits.Polytope4d;
import com.vzome.core.kinds.GoldenFieldApplication;
import com.vzome.core.math.Projection;
import com.vzome.core.model.RealizedModelImpl;

public class Polytope4dTest
{
	@Test
	public void testOldestConstructor()
	{
		FieldApplication app = new GoldenFieldApplication( new PentagonField() );
		AlgebraicField field = app .getField();
        AlgebraicNumber one = field .one();
		AlgebraicVector x = field .basisVector( 3, 0 );
		AlgebraicVector y = field .basisVector( 3, 1 );
		AlgebraicVector quaternion = y .minus( x ) .inflateTo4d();

		RealizedModelImpl realized = new RealizedModelImpl( field, new Projection .Default( field ) );
		assertEquals( 0, realized .size() );
        EditorModel editorModel = new EditorModelImpl( realized, new FreePoint( field.origin(3) ), app, null, new HashMap<String, OrbitSource>() );

		Polytope4d cmd = new Polytope4d( editorModel );
		
		Map<String, Object> params = new HashMap<>();
        params .put( "groupName", "A4" );
        params .put( "renderGroupName", "A4" );
        params .put( "index", 1 );
        params .put( "edgesToRender", 0 );
        params .put( "edgeScales", new AlgebraicNumber[]{ one, one, one, one } );
        params .put( "quaternion", quaternion );
        cmd .configure( params );
		
		cmd .perform();
		assertEquals( 5, realized .size() );
		// look for ball at (2/5 -4/5φ, 8/5 -6/5φ, -2 +2φ)
		AlgebraicVector target = field .createVector( new int[][]{ {2,5, -4,5}, {8,5, -6,5}, {-2,1, 2,1} } );
		assertNotNull( realized .findConstruction( new FreePoint( target ) ) );
		// look for ball at (12/5 -4/5φ, -12/5 +4/5φ, 0)
		target = field .createVector( new int[][]{ {12, 5, -4, 5}, {-12,5, 4,5}, {0,1, 0,1} } );
		assertNotNull( realized .findConstruction( new FreePoint( target ) ) );
	}
}
