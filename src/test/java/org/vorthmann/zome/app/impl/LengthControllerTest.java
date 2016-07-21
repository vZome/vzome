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
	public void testInitialState() 
	{
		AlgebraicField field = new PentagonField();
		LengthController controller = new LengthController( field );
		String value = controller .getProperty( "half" );
		assertEquals( "false", value );
		value = controller .getProperty( "scale" );
		assertEquals( "0", value );
		value = controller .getProperty( "unitText" );
		assertEquals( "1", value );
		value = controller .getProperty( "unitIsCustom" );
		assertEquals( "false", value );
		value = controller .getProperty( "lengthText" );
		assertEquals( "1", value );
		AlgebraicNumber result = controller .getValue();
		AlgebraicNumber expected = field .createAlgebraicNumber( 1, 0, 1, 3 );
		assertEquals( expected, result );
	}

	@Test
	public void testSetHalf() 
	{
		AlgebraicField field = new PentagonField();
		LengthController controller = new LengthController( field );
		controller .setProperty( "half", "true" );
		String value = controller .getProperty( "half" );
		assertEquals( "true", value );
		value = controller .getProperty( "scale" );
		assertEquals( "0", value );
		value = controller .getProperty( "unitText" );
		assertEquals( "1", value );
		value = controller .getProperty( "unitIsCustom" );
		assertEquals( "false", value );
		value = controller .getProperty( "lengthText" );
		assertEquals( "1", value );
		AlgebraicNumber result = controller .getValue();
		AlgebraicNumber expected = field .createAlgebraicNumber( 1, 0, 2, 3 );
		assertEquals( expected, result );
	}

	@Test
	public void testSetScale() 
	{
		AlgebraicField field = new PentagonField();
		LengthController controller = new LengthController( field );
		controller .setProperty( "scale", "5" );
		String value = controller .getProperty( "half" );
		assertEquals( "false", value );
		value = controller .getProperty( "scale" );
		assertEquals( "5", value );
		value = controller .getProperty( "unitText" );
		assertEquals( "1", value );
		value = controller .getProperty( "unitIsCustom" );
		assertEquals( "false", value );
		value = controller .getProperty( "lengthText" );
		assertEquals( "3 +5\u03C6", value );
		AlgebraicNumber result = controller .getValue();
		AlgebraicNumber expected = field .createAlgebraicNumber( 1, 0, 1, 8 );
		assertEquals( expected, result );
	}

	@Test
	public void testGetCustomUnit() 
	{
		AlgebraicField field = new PentagonField();
		LengthController controller = new LengthController( field );
		Controller units = controller .getSubController( "unit" );
		units .setProperty( "values", "5 8 3" );
		try {
			controller .doAction( "getCustomUnit", null );
			String value = controller .getProperty( "half" );
			assertEquals( "false", value );
			value = controller .getProperty( "scale" );
			assertEquals( "0", value );
			value = controller .getProperty( "unitText" );
			assertEquals( "5/3 +8/3\u03C6", value );
			value = controller .getProperty( "unitIsCustom" );
			assertEquals( "true", value );
			value = controller .getProperty( "lengthText" );
			assertEquals( "5/3 +8/3\u03C6", value );
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

	@Test
	public void testLong() 
	{
		AlgebraicField field = new PentagonField();
		LengthController controller = new LengthController( field );
		try {
			controller .doAction( "long", null );
			String value = controller .getProperty( "half" );
			assertEquals( "false", value );
			value = controller .getProperty( "scale" );
			assertEquals( "2", value );
			value = controller .getProperty( "unitText" );
			assertEquals( "1", value );
			value = controller .getProperty( "unitIsCustom" );
			assertEquals( "false", value );
			value = controller .getProperty( "lengthText" );
			assertEquals( "1 +\u03C6", value );
			AlgebraicNumber result = controller .getValue();
			AlgebraicNumber expected = field .createAlgebraicNumber( 1, 0, 1, 5 );
			assertEquals( expected, result );
		} catch (Exception e) {
			fail( e .getMessage() );
		}
	}
}
