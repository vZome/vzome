package com.vzome.core.math.symmetry;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.math.RealVector;

public class PlaneOrbitSet extends OrbitSet
{
	private final OrbitSet delegate;
	private final int[] normal;
	private final Set<Axis> zones = new HashSet<Axis>();

	public PlaneOrbitSet( OrbitSet delegate, int[] normal )
	{
		super( delegate .getSymmetry() );
		this .delegate = delegate;
		
		AlgebraicField field = delegate .getSymmetry() .getField();
		this .normal = normal;

		@SuppressWarnings("unchecked")
		Iterator<Direction> dirs = delegate .iterator();
        while ( dirs .hasNext() ) {
            Direction dir = (Direction) dirs .next();
            // now iterate over axes
			for ( @SuppressWarnings("unchecked")
			Iterator<Axis> axes = dir .getAxes(); axes .hasNext(); ) {
				Axis axis = (Axis) axes .next();
				if ( field .isZero( axis .dotNormal( this .normal ) ) )
					this .zones .add( axis );
			}
        }
	}

	@Override
	public Axis getAxis( RealVector vector )
	{
		if ( RealVector .ORIGIN .equals( vector ) ) {
			return null;
		}
		AlgebraicField field = delegate .getSymmetry() .getField();
		// largest cosine means smallest angle
		//  and cosine is (a . b ) / |a| |b|
		double maxCosine = - 1d;
		Axis closest = null;
		for ( Iterator<Axis> axes = this .zones .iterator(); axes .hasNext(); ) {
			Axis axis = (Axis) axes .next();
			RealVector axisV = field .getRealVector( axis .normal() );
			double cosine = vector .dot( axisV ) / (vector .length() * axisV .length());
			if ( cosine > maxCosine ) {
				maxCosine = cosine;
				closest = axis;
			}
		}
		return closest;
	}

	@Override
	public Direction getDirection( String name )
	{
		return this .delegate .getDirection( name );
	}
}
