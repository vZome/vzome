package com.vzome.core.math.symmetry;

import java.util.HashSet;
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

        for (Direction dir : delegate) {
            for (Axis axis : dir) {
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
        for (Axis axis : this .zones) {
            RealVector axisV = axis .normal() .toRealVector(); // TODO invert the Embedding to get this right
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
