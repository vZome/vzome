package org.vorthmann.zome.app.impl;

import org.vorthmann.ui.Controller;
import org.vorthmann.ui.DefaultController;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;

public class VectorController extends DefaultController
{
	private final NumberController[] coordinates;
	
	private final AlgebraicField field;

	public VectorController( AlgebraicVector initial )
	{
		this .field = initial .getField();
		this .coordinates = new NumberController[ initial .dimension() ];
		for ( int i = 0; i < coordinates.length; i++ ) {
			coordinates[ i ] = new NumberController( initial .getField() );
		}
	}
	
	@Override
	public Controller getSubController( String name )
	{
		switch ( name ) {

		case "w":
			return coordinates[ 0 ];

		case "x":
			return coordinates[ 1 ];

		case "y":
			return coordinates[ 2 ];

		case "z":
			return coordinates[ 3 ];

		default:
			return super.getSubController( name );
		}
	}

	public void setVector( AlgebraicVector vector )
	{
		assert vector .dimension() == coordinates .length;

		for ( int i = 0; i < coordinates.length; i++ ) {
			NumberController numberController = coordinates[i];
			AlgebraicNumber coord = vector .getComponent( i );
			numberController .setValue( coord );
		}
	}

	public AlgebraicVector getVector()
	{
		AlgebraicVector result = field .basisVector( coordinates .length, 0 );
		for ( int i = 0; i < coordinates.length; i++ ) {
			NumberController numberController = coordinates[i];
			AlgebraicNumber coord = numberController .getValue();
			result .setComponent( i, coord );
		}
		return result;
	}

}
