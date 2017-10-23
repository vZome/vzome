package com.vzome.core.editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.kinds.GoldenFieldApplication;
import com.vzome.core.math.Projection;
import com.vzome.core.model.RealizedModel;

public class Polytope4dTest
{
	@Test
	public void testOldestConstructor()
	{
		FieldApplication app = new GoldenFieldApplication();
		AlgebraicField field = app .getField();
		AlgebraicVector x = field .basisVector( 3, 0 );
		AlgebraicVector y = field .basisVector( 3, 1 );

		Point xpoint = new FreePoint( x );
		Point ypoint = new FreePoint( y );
		Segment segment = new SegmentJoiningPoints( xpoint, ypoint );

		Selection selection = new Selection();
		RealizedModel realized = new RealizedModel( field, new Projection .Default( field ) );
		assertEquals( 0, realized .size() );

		Polytope4d cmd = new Polytope4d( selection, realized, app, segment, 1, "A4", false );
		cmd .perform();
		assertEquals( 10, realized .size() );
		// look for ball at (2/5 -4/5φ, 8/5 -6/5φ, -2 +2φ)
		AlgebraicVector target = field .createVector( new int[]{ 2, 5, -4, 5, 8, 5, -6, 5, -2, 1, 2, 1 } );
		assertNotNull( realized .findConstruction( new FreePoint( target ) ) );
		// look for ball at (12/5 -4/5φ, -12/5 +4/5φ, 0)
		target = field .createVector( new int[]{ 12, 5, -4, 5, -12, 5, 4, 5, 0, 1, 0, 1 } );
		assertNotNull( realized .findConstruction( new FreePoint( target ) ) );
	}
}
