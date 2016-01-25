package com.vzome.core.math.symmetry;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.RealVector;

public class PlaneOrbitSet extends OrbitSet
{
	private final OrbitSet delegate;
	private final AlgebraicVector normal;
	private final Set<Axis> zones = new HashSet<>();

	public PlaneOrbitSet( OrbitSet delegate, AlgebraicVector normal )
	{
		super( delegate .getSymmetry() );
		this .delegate = delegate;
		
		this .normal = normal;

		Iterator<Direction> dirs = delegate .iterator();
        while ( dirs .hasNext() ) {
            Direction dir = dirs .next();
            // now iterate over axes
			for ( Iterator<Axis> axes = dir .getAxes(); axes .hasNext(); ) {
				Axis axis = axes .next();
				if ( axis .normal() .dot( this .normal ) .isZero() )
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
		// largest cosine means smallest angle
		//  and cosine is (a . b ) / |a| |b|
		double maxCosine = - 1d;
		Axis closest = null;
		for ( Iterator<Axis> axes = this .zones .iterator(); axes .hasNext(); ) {
			Axis axis = axes .next();
			RealVector axisV = axis .normal() .toRealVector();
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
