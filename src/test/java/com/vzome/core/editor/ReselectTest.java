package com.vzome.core.editor;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;

public class ReselectTest {

	@Test
	public void testReselect()
	{
		AlgebraicField field = new PentagonField();
		Selection selection = new Selection();
		
		AlgebraicVector v0 = field .basisVector( 3, AlgebraicVector.X );
		AlgebraicVector v1 = field .basisVector( 3, AlgebraicVector.Y );
		AlgebraicVector v2 = field .basisVector( 3, AlgebraicVector.Z );
		
		Manifestation m0 = new Connector( v0 );
		selection .select( m0 );
		Manifestation m1 = new Connector( v1 );
		selection .select( m1 );
		Manifestation m2 = new Connector( v2 );
		selection .select( m2 );
		Manifestation m3 = new Connector( v0 .scale( field .createAlgebraicNumber( 2 ) ) );
		selection .select( m3 );
		Manifestation m4 = new Connector( v1 .scale( field .createAlgebraicNumber( 2 ) ) );
		selection .select( m4 );
		
		Manifestation[] expected = new Manifestation[]{ m0, m1, m2, m3, m4 };
		
		int i = 0;
		for ( Manifestation m : selection ) {
			assertEquals( expected[ i++ ], m );
		}
		
		int i1 = selection .unselect( m1 );
		int i3 = selection .unselect( m3 );
		
		Manifestation[] expected2 = new Manifestation[]{ m0, m2, m4 };
		i = 0;
		for ( Manifestation m : selection ) {
			assertEquals( expected2[ i++ ], m );
		}
		
		// NOTE: reselects must be performed in reverse order
		selection .reselect( m3, i3 );
		selection .reselect( m1, i1 );

		i = 0;
		for ( Manifestation m : selection ) {
			assertEquals( expected[ i++ ], m );
		}
	}
}
