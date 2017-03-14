package com.vzome.fields.heptagon;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.HeptagonField;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.OctahedralSymmetry;

public class TriangularAntiprismSymmetry extends OctahedralSymmetry
{
    private final Matrix3d SHEAR;

	public TriangularAntiprismSymmetry( AlgebraicField field, String frameColor, String defaultStyle )
	{
		super( field, frameColor, defaultStyle );
        
        double a_over_h = Math.sqrt( ( 1d + HeptagonField.RHO_VALUE + HeptagonField.SIGMA_VALUE ) /
        							( 2d + 2*HeptagonField.SIGMA_VALUE - HeptagonField.RHO_VALUE ) );
        double sqrt2 = Math.sqrt( 2d );
        double g = ( sqrt2 + 2*a_over_h ) / ( sqrt2 - a_over_h );
        double scale = 1d / 9d; // reducing the enlarging due to use of (1,1,g) basis, since g > 9.
        SHEAR = new Matrix3d( g * scale, scale, scale, scale, g * scale, scale, scale, scale, g * scale );
	}

	@Override
	public RealVector embedInR3( AlgebraicVector v )
	{
		RealVector rv = super.embedInR3( v );
        Vector3d v3d = new Vector3d( rv.x, rv.y, rv.z );
    	SHEAR .transform( v3d );
		return new RealVector( v3d.x, v3d.y, v3d.z );
	}
    
    @Override
    public boolean isTrivial()
    {
    	return false; // signals the POV-Ray exporter to generate the tranform
    }

    @Override
    public String getName()
    {
        return "triangular antiprism";
    }

    @Override
    public String getDefaultStyle()
    {
        return "triangular antiprism";
    }
}
