package com.vzome.fields.heptagon;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.HeptagonField;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.AbstractSymmetry;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Permutation;

public class HeptagonalAntiprismSymmetry extends AbstractSymmetry
{
    private static final double SIGMA_X_2 = HeptagonField.SIGMA_VALUE * 2.0d;
    private static final double SKEW_FACTOR = Math .sin( (3.0d/7.0d) * Math.PI );

	public HeptagonalAntiprismSymmetry( AlgebraicField field, String frameColor, String defaultStyle )
	{
		super( 14, field, frameColor, defaultStyle );
	}

	@Override
	protected void createFrameOrbit( String frameColor )
	{
        HeptagonField hf = (HeptagonField) this .mField;

        AlgebraicVector zAxis = hf .basisVector( 3, AlgebraicVector.Z );
        AlgebraicVector zAxisNeg = zAxis .negate();
        Direction redOrbit = createZoneOrbit( "red", 0, 1, zAxis, true );
        for ( int i = 0; i < 7; i++ ) {
            redOrbit .createAxis( i, 1, zAxis );
            redOrbit .createAxis( i + 7, 6, zAxisNeg );
        }
        redOrbit .setDotLocation( 1d, 0d );

        AlgebraicVector axis0 = hf .basisVector( 3, AlgebraicVector.X );
        Direction blueOrbit = createZoneOrbit( frameColor, 0, 7, axis0, true );
        blueOrbit .createAxis( 0, 7, axis0 );
        blueOrbit .createAxis( 7, 7, axis0 );
        blueOrbit .setDotLocation( 0d, 1d );

        AlgebraicNumber one = hf .one();
        AlgebraicNumber s = hf .sigmaReciprocal(); // 1 / sigma
        AlgebraicNumber R = hf .createPower( 1 ) .times( hf .sigmaReciprocal() ); // rho / sigma

        AlgebraicVector axis1 = hf .origin( 3 )
        		.setComponent( AlgebraicVector.X, s )
				.setComponent( AlgebraicVector.Y, R );
        blueOrbit .createAxis( 1, 9, axis1 );
        blueOrbit .createAxis( 8, 9, axis1 );

        AlgebraicVector axis2 = hf .origin( 3 )
        		.setComponent( AlgebraicVector.X, s .negate() )
				.setComponent( AlgebraicVector.Y, one );
        blueOrbit .createAxis( 2, 11, axis2 );
        blueOrbit .createAxis( 9, 11, axis2 );

        AlgebraicVector axis3 = hf .origin( 3 )
        		.setComponent( AlgebraicVector.X, one .negate() )
				.setComponent( AlgebraicVector.Y, s );
        blueOrbit .createAxis( 3, 13, axis3 );
        blueOrbit .createAxis( 10, 13, axis3 );

        AlgebraicVector axis4 = hf .origin( 3 )
        		.setComponent( AlgebraicVector.X, R .negate() )
				.setComponent( AlgebraicVector.Y, s .negate() );
        blueOrbit .createAxis( 4, 8, axis4 );
        blueOrbit .createAxis( 11, 8, axis4 );

        AlgebraicVector axis5 = hf .origin( 3 )
				.setComponent( AlgebraicVector.Y, one .negate() );
        blueOrbit .createAxis( 5, 10, axis5 );
        blueOrbit .createAxis( 12, 10, axis5 );

        AlgebraicVector axis6 = hf .origin( 3 )
        		.setComponent( AlgebraicVector.X, R )
				.setComponent( AlgebraicVector.Y, R .negate() );
        blueOrbit .createAxis( 6, 12, axis6 );
        blueOrbit .createAxis( 13, 12, axis6 );

        AlgebraicVector z = hf .basisVector( 3, AlgebraicVector.Z );
        mMatrices[ 0 ] = hf .identityMatrix( 3 );
        mMatrices[ 1 ] = new AlgebraicMatrix( axis1, axis6 .negate(), z );
        mMatrices[ 2 ] = new AlgebraicMatrix( axis2, axis0 .negate(), z );
        mMatrices[ 3 ] = new AlgebraicMatrix( axis3, axis1 .negate(), z );
        mMatrices[ 4 ] = new AlgebraicMatrix( axis4, axis2 .negate(), z );
        mMatrices[ 5 ] = new AlgebraicMatrix( axis5, axis3 .negate(), z );
        mMatrices[ 6 ] = new AlgebraicMatrix( axis6, axis4 .negate(), z );
        AlgebraicVector zNeg = z .negate();
        mMatrices[  7 ] = new AlgebraicMatrix( axis0, axis2 .negate(), zNeg );
        mMatrices[  8 ] = mMatrices[ 1 ] .times( mMatrices[ 7 ] );
        mMatrices[  9 ] = mMatrices[ 2 ] .times( mMatrices[ 7 ] );
        mMatrices[ 10 ] = mMatrices[ 3 ] .times( mMatrices[ 7 ] );
        mMatrices[ 11 ] = mMatrices[ 4 ] .times( mMatrices[ 7 ] );
        mMatrices[ 12 ] = mMatrices[ 5 ] .times( mMatrices[ 7 ] );
        mMatrices[ 13 ] = mMatrices[ 6 ] .times( mMatrices[ 7 ] );
    }

	@Override
	protected void createOtherOrbits()
	{
//		super .createOtherOrbits();
	}

	@Override
	protected void createInitialPermutations()
	{
        mOrientations[0] = new Permutation( this, null );

        // first, define the 7-fold rotation
        int[] map = new int[]{ 1, 2, 3, 4, 5, 6, 0, 8, 9, 10, 11, 12, 13, 7 };
        mOrientations[1] = new Permutation( this, map );

        // then, then 2-fold rotation
        map = new int[]{ 7, 13, 12, 11, 10, 9, 8, 0, 6, 5, 4, 3, 2, 1 };
        mOrientations[7] = new Permutation( this, map );
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

	@Override
	public int[] subgroup( String name )
	{
		return null; // TODO
	}

	@Override
	public Direction getSpecialOrbit( SpecialOrbit which )
	{
        switch ( which ) {

        case BLUE:
            return this .getDirection( "blue" );

        case RED:
            return this .getDirection( "red" );

        case YELLOW:
            return this .getDirection( "blue" );

        default:
            return null;
        }
    }
}
