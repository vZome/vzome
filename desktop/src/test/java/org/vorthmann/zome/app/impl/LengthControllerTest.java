package org.vorthmann.zome.app.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vzome.controller.ControllerTesting;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.PentagonField;
import com.vzome.desktop.api.Controller;
import com.vzome.desktop.awt.LengthController;

public class LengthControllerTest
{    
    private static void assertStateAfter( LengthController controller, String[] actions, String[] propertyValues, AlgebraicNumber expectedValue )
    {
        ControllerTesting .assertStateAfter( controller, actions, propertyValues );
        AlgebraicNumber result = controller .getValue();
        assertEquals( expectedValue, result );
    }
	
	@Test
	public void testInitialState() 
	{
		AlgebraicField field = new PentagonField();
		LengthController controller = new LengthController( field );
		assertStateAfter( controller, null, new String[]{ "false", "0", "1", "false", "1" },
							field .createAlgebraicNumber( 1, 0, 1, 3 ) );
	}

	@Test
	public void testSetHalf() 
	{
		AlgebraicField field = new PentagonField();
		LengthController controller = new LengthController( field );
		assertStateAfter( controller, new String[]{ "half=true" },
							new String[]{ "true", "0", "1", "false", "1" },
							field .createAlgebraicNumber( 1, 0, 2, 3 ) );
	}

	@Test
	public void testToggleHalf() 
	{
		AlgebraicField field = new PentagonField();
		LengthController controller = new LengthController( field );
		assertStateAfter( controller, new String[]{ "half=true, toggleHalf" },
							new String[]{ "false", "0", "1", "false", "1" },
							field .createAlgebraicNumber( 1, 0, 1, 3 ) );
	}

	@Test
	public void testSetScale() 
	{
		AlgebraicField field = new PentagonField();
		LengthController controller = new LengthController( field );
		assertStateAfter( controller, new String[]{ "scale=5" },
							new String[]{ "false", "5", "1", "false", "3 +5\u03C6" },
							field .createAlgebraicNumber( 1, 0, 1, 8 ) );
	}

	@Test
	public void testGetCustomUnit() 
	{
		AlgebraicField field = new PentagonField();
		LengthController controller = new LengthController( field );
		Controller units = controller .getSubController( "unit" );
		units .setProperty( "values", "5 8 3" );
		assertStateAfter( controller, new String[]{ "getCustomUnit" },
							new String[]{ "false", "0", "5/3 +8/3\u03C6", "true", "5/3 +8/3\u03C6" },
							field .createAlgebraicNumber( 5, 8, 3, 3 ) );
	}

	@Test
	public void testLong() 
	{
		AlgebraicField field = new PentagonField();
		LengthController controller = new LengthController( field );
		assertStateAfter( controller, new String[]{ "long" },
							new String[]{ "false", "2", "1", "false", "1 +\u03C6" },
							field .createAlgebraicNumber( 1, 0, 1, 5 ) );
	}

	@Test
	public void testScaling() 
	{
		AlgebraicField field = new PentagonField();
		LengthController controller = new LengthController( field );
		assertStateAfter( controller, new String[]{ "long", "scaleDown", "scaleDown", "scaleDown" },
							new String[]{ "false", "-1", "1", "false", "-1 +\u03C6" },
							field .createPower( 2 ) );
	}

	@Test
	public void testNewZeroScale() 
	{
		AlgebraicField field = new PentagonField();
		LengthController controller = new LengthController( field );
		Controller units = controller .getSubController( "unit" );
		units .setProperty( "values", "3 0 1" );
		assertStateAfter( controller, new String[]{ "getCustomUnit", "scaleUp", "scaleUp", "newZeroScale" },
							new String[]{ "false", "0", "3 +3\u03C6", "true", "3 +3\u03C6" },
							field .createAlgebraicNumber( 3, 0, 1, 5 ) );
	}

	@Test
	public void testReset() 
	{
		AlgebraicField field = new PentagonField();
		LengthController controller = new LengthController( field );
		Controller units = controller .getSubController( "unit" );
		units .setProperty( "values", "5 2 7" );
		assertStateAfter( controller, new String[]{ "getCustomUnit", "scaleUp", "newZeroScale", "reset" },
							new String[]{ "false", "0", "1", "false", "1" },
							field .createAlgebraicNumber( 1, 0, 1, 3 ) );
	}
}
