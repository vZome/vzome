package com.vzome.core.render;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.junit.Test;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.HeptagonField;
import com.vzome.core.editor.FieldApplication.SymmetryPerspective;
import com.vzome.core.editor.SymmetrySystem;
import com.vzome.core.kinds.HeptagonFieldApplication;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.OrbitSet;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Strut;

/**
 * While creating HeptagonalAntiprismSymmetry, with inbound/outbound zones,
 * I had a defect in Direction.getAxis() that was very hard to track down,
 * since the PreviewStrut reported the right expected Axis, but that
 * is not how the RenderedModel.resetAttributes() turned out.
 * 
 * @author vorth
 *
 */
public class RenderedModelTest
{
	private final class ZoneChecker implements RenderingChanges
	{
		private Axis expected;

		public void setExpectedZone( Axis expected )
		{
			this.expected = expected;
		}

		@Override
		public void manifestationAdded( RenderedManifestation rm )
		{
			int sense = rm .getStrutSense();
			int zoneNum = rm .getStrutZone();
			Direction orbit = rm .getStrutOrbit();
			// inbound axis results in moving the RM location to the end of the Strut
			boolean outbound = ! rm .getLocationAV() .equals( expected .normal() );
			Axis actual = orbit .getAxis( sense, zoneNum, outbound );
			assertEquals( expected, actual );
		}

		@Override
		public void shapeChanged( RenderedManifestation manifestation )
		{
			fail( "should not be called" );
		}

		@Override
		public void reset()
		{
			fail( "should not be called" );
		}

		@Override
		public void orientationChanged( RenderedManifestation manifestation )
		{
			fail( "should not be called" );
		}

		@Override
		public void manifestationSwitched( RenderedManifestation from, RenderedManifestation to )
		{
			fail( "should not be called" );
		}

		@Override
		public void manifestationRemoved( RenderedManifestation manifestation )
		{
			fail( "should not be called" );
		}

		@Override
		public void locationChanged( RenderedManifestation manifestation )
		{
			fail( "should not be called" );
		}

		@Override
		public void glowChanged( RenderedManifestation manifestation )
		{
			fail( "should not be called" );
		}

		@Override
		public void colorChanged( RenderedManifestation manifestation )
		{
			fail( "should not be called" );
		}
	}

	@Test
	public void testZoneConsistency()
	{
		HeptagonFieldApplication app = new HeptagonFieldApplication();
		HeptagonField field = (HeptagonField) app .getField();
		AlgebraicVector origin = field .origin( 3 );

		SymmetryPerspective perspective = app .getDefaultSymmetryPerspective();
		Symmetry symmetry = perspective .getSymmetry();
		
		SymmetrySystem sys = new SymmetrySystem( null, perspective, null, new Colors( new Properties() ), true );
		
		RenderedModel model = new RenderedModel( field, sys );
		ZoneChecker checker = new ZoneChecker();
		model .addListener( checker );

		// create an automatic orbit just off the X-axis, as if we did "join points"
		AlgebraicNumber small = field .sigmaReciprocal();
		small = small .times( small );
		AlgebraicVector vector = new AlgebraicVector( field .one(), small, small );
		Axis zone = sys .getAxis( vector );
	    assertNotNull( zone );
	    OrbitSet oneOrbit = new OrbitSet( symmetry );
	    oneOrbit .add( zone .getDirection() );

	    // now simulate a PreviewStrut
	    RealVector rv = new RealVector( 3d, 6d, -1d );
	    Axis expected = oneOrbit .getAxis( rv );
	    assertNotNull( expected );
	    checker .setExpectedZone( expected );
	    // Scaling the normal just to rock the boat... works either way.
	    Strut strut = new Strut( field .origin( 3 ), expected .normal() .scale( field .createPower( 2 ) ) );
		model .manifestationAdded( strut ); // the original bug: the strut did not map back to the expected Axis

	    rv = new RealVector( 3d, 6d, 1d );
	    expected = oneOrbit .getAxis( rv );
	    assertNotNull( expected );
	    checker .setExpectedZone( expected );
	    strut = new Strut( origin, expected .normal() );
		model .manifestationAdded( strut );

	    rv = new RealVector( -3d, -6d, -1d );
	    expected = oneOrbit .getAxis( rv );
	    assertNotNull( expected );
	    checker .setExpectedZone( expected );
	    strut = new Strut( origin, expected .normal() );
		model .manifestationAdded( strut );

	    rv = new RealVector( -3d, -6d, 1d );
	    expected = oneOrbit .getAxis( rv );
	    assertNotNull( expected );
	    checker .setExpectedZone( expected );
	    strut = new Strut( origin, expected .normal() );
		model .manifestationAdded( strut );

	    rv = new RealVector( -6d, -3d, 1d );
	    expected = oneOrbit .getAxis( rv );
	    assertNotNull( expected );
	    checker .setExpectedZone( expected );
	    strut = new Strut( origin, expected .normal() );
		model .manifestationAdded( strut );

	    rv = new RealVector( -3d, 1d, -6d );
	    expected = oneOrbit .getAxis( rv );
	    assertNotNull( expected );
	    checker .setExpectedZone( expected );
	    strut = new Strut( origin, expected .normal() );
		model .manifestationAdded( strut );
	}
}
