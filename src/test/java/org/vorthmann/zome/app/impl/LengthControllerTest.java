package org.vorthmann.zome.app.impl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.vorthmann.ui.Controller;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.PentagonField;

public class LengthControllerTest
{
	@Test
	public void testValue() 
	{
		AlgebraicField field = new PentagonField();
		LengthController controller = new LengthController( field );
		try {
			AlgebraicNumber result = controller .getValue();
			AlgebraicNumber expected = field .createAlgebraicNumber( 1, 0, 1, 3 );
			assertEquals( expected, result );
		} catch (Exception e) {
			fail( e .getMessage() );
		}
	}

	@Test
	public void testCustom() 
	{
		AlgebraicField field = new PentagonField();
		LengthController controller = new LengthController( field );
		Controller units = controller .getSubController( "unit" );
		try {
			units .setProperty( "values", "5 8 3" );
			controller .doAction( "getCustomUnit", null );
			AlgebraicNumber result = controller .getValue();
			AlgebraicNumber expected = field .createAlgebraicNumber( 5, 8, 3, 3 );
			assertEquals( expected, result );
		} catch (Exception e) {
			fail( e .getMessage() );
		}
	}

	@Test
	public void testScaling() 
	{
		AlgebraicField field = new PentagonField();
		LengthController controller = new LengthController( field );
		try {
			controller .doAction( "long", null );
			AlgebraicNumber result = controller .getValue();
			AlgebraicNumber expected = field .createPower( 5 );
			assertEquals( expected, result );
			
			controller .doAction( "scaleDown", null );
			controller .doAction( "scaleDown", null );
			controller .doAction( "scaleDown", null );
			result = controller .getValue();
			expected = field .createPower( 2 );
			assertEquals( expected, result );
			
			controller .doAction( "reset", null );
			controller .doAction( "toggleHalf", null );
			result = controller .getValue();
			expected = field .createAlgebraicNumber( 1, 0, 2, 3 );
			assertEquals( expected, result );
		} catch (Exception e) {
			fail( e .getMessage() );
		}
	}
}
