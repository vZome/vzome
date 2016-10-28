package com.vzome.fields.heptagon;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.HeptagonField;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.OctahedralSymmetry;

public class HeptagonalAntiprismSymmetry extends OctahedralSymmetry
{
    private static final double SIGMA_X_2 = HeptagonField.SIGMA_VALUE * 2.0d;
    private static final double SKEW_FACTOR = Math.sin( (3.0d/7.0d) * Math.PI );

	public HeptagonalAntiprismSymmetry( AlgebraicField field, String frameColor, String defaultStyle )
	{
		super( field, frameColor, defaultStyle );
	}

	@Override
	public RealVector embedInR3( AlgebraicVector v )
	{
		RealVector rv = super.embedInR3( v );
        Double x = rv.x + ( rv.y / SIGMA_X_2 );
        Double y = rv.y * SKEW_FACTOR;
		return new RealVector( x, y, rv.z );
	}

    @Override
    public String getName()
    {
        return "heptagonal antiprism";
    }

    @Override
    public String getDefaultStyle()
    {
        return "heptagonal antiprism";
    }
}
