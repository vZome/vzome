package com.vzome.fields.heptagon;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.HeptagonField;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.AbstractSymmetry;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Permutation;
import com.vzome.core.math.symmetry.Symmetry;

public class HeptagonalAntiprismSymmetry extends AbstractSymmetry
{
    private final double sigmaX2;
    private final double skewFactor;
	private final boolean correctedOrbits;
    private Axis preferredAxis;

    public HeptagonalAntiprismSymmetry( AlgebraicField field, String frameColor)
	{
		this( field, frameColor, false);
	}

	public HeptagonalAntiprismSymmetry( AlgebraicField field, String frameColor, boolean correctedOrbits )
	{
		super( 14, field, frameColor,
					correctedOrbits?
							// reflection in Z (red) will yield the negative zones
							new AlgebraicMatrix( field .basisVector( 3, AlgebraicVector.X ),
									field .basisVector( 3, AlgebraicVector.Y ),
									field .basisVector( 3, AlgebraicVector.Z ) .negate() )
						: null // reflection through origin yields negative zones
				); // calls createInitialPermutations, createFrameOrbit, createOtherOrbits
        sigmaX2 = field.getAffineScalar().times( field.createRational (2) ).evaluate();
        skewFactor = Math .sin( (3.0d/7.0d) * Math.PI ); // TODO: generalize this so that the 3 and 7 are calculated from PolygonField
		this.correctedOrbits = correctedOrbits;
	}

	/**
	 * Called by the super constructor.
	 */
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
	protected void createFrameOrbit( String frameColor )
	{
		// Breaking the bad pattern of orbit initialization in the AbstractSymmetry constructor,
		//   we are just initializing matrices, here.
		
        HeptagonField hf = (HeptagonField) this .mField;

        AlgebraicNumber one = hf .one();
        AlgebraicNumber s = hf .getAffineScalar().reciprocal(); // reciprocal of sigma
        AlgebraicNumber R = hf .createPower( 1 ) .times( s ); // rho / sigma

        //                                   (-s,1)         Y
        //                        +---+------ [2] ---------+-----------+--------+---+
        //                       /   /                    /            (s,R)   /   /
        //                      +---+--------+-----------+--------- [1] ------+---+
        //                     /   /        /           /                    /   /
        //                    /   /        /           /           /        /   /
        //                       /        /           /           /        /   /
        //          (-1,s) [3] -+--------+-----------+-----------+--------+---+
        //                     /        /           /           /        /   /
        //                /   /        /           /           /        /   /
        //               /   /        /           /           /        /   /
        //              /   /        /                       /        /    
        //             +---+--------+---------  O  ---------+--------+- [0] (1,0)  X
        //            /   /        /                       /        /    
        //           /   /        /           /<--- s --->/        /   /
        //          /   /        /           /<-------- R ------->/   /
        //         /            /           /<---------- 1 --------->/
        //        +- [4| ------+-----------+-----------+--------+---+
        //       /    (-R,-s) /           /           /        /   /
        //      /   /        /           /           /        /   /
        //     /   /        /           /           /  (R,-R)    /
        //    +---+--------+-----------+-----------+------ [6] -+
        //   /   /        /                       /            /
        //  +---+--------+--------- [5] ---------+--------+---+
        //                        (0,-1)
        
        AlgebraicVector zAxis = hf .basisVector( 3, AlgebraicVector.Z );
        AlgebraicVector zAxisNeg = zAxis .negate();
        AlgebraicVector axis0 = hf .basisVector( 3, AlgebraicVector.X );
        AlgebraicVector axis1 = hf .origin( 3 )
        		.setComponent( AlgebraicVector.X, s )
				.setComponent( AlgebraicVector.Y, R );
        AlgebraicVector axis2 = hf .origin( 3 )
        		.setComponent( AlgebraicVector.X, s .negate() )
				.setComponent( AlgebraicVector.Y, one );
        AlgebraicVector axis3 = hf .origin( 3 )
        		.setComponent( AlgebraicVector.X, one .negate() )
				.setComponent( AlgebraicVector.Y, s );
        AlgebraicVector axis4 = hf .origin( 3 )
        		.setComponent( AlgebraicVector.X, R .negate() )
				.setComponent( AlgebraicVector.Y, s .negate() );
        AlgebraicVector axis5 = hf .origin( 3 )
				.setComponent( AlgebraicVector.Y, one .negate() );
        AlgebraicVector axis6 = hf .origin( 3 )
        		.setComponent( AlgebraicVector.X, R )
				.setComponent( AlgebraicVector.Y, R .negate() );

        // all mMatrices are mappings of [X,Y,Z] = [ axis0, -axis5, zAxis ]
        mMatrices[  0 ] = hf .identityMatrix( 3 );
        mMatrices[  1 ] = new AlgebraicMatrix( axis1, axis6 .negate(), zAxis );
        mMatrices[  2 ] = new AlgebraicMatrix( axis2, axis0 .negate(), zAxis );
        mMatrices[  3 ] = new AlgebraicMatrix( axis3, axis1 .negate(), zAxis );
        mMatrices[  4 ] = new AlgebraicMatrix( axis4, axis2 .negate(), zAxis );
        mMatrices[  5 ] = new AlgebraicMatrix( axis5, axis3 .negate(), zAxis );
        mMatrices[  6 ] = new AlgebraicMatrix( axis6, axis4 .negate(), zAxis );
        
        mMatrices[  7 ] = new AlgebraicMatrix( axis0, axis2 .negate(), zAxisNeg );
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
		// Breaking the bad pattern of orbit initialization in the AbstractSymmetry constructor
	}

	public HeptagonalAntiprismSymmetry createStandardOrbits( String frameColor )
	{
        Direction redOrbit = createZoneOrbit( "red", 0, 1, this .mField .basisVector( 3, AlgebraicVector.Z ), true );
        redOrbit .setDotLocation( 1d, 0d );
        this .preferredAxis = redOrbit .getAxis( Symmetry.PLUS, 0 );

        Direction blueOrbit = createZoneOrbit( frameColor, 0, 7, this .mField .basisVector( 3, AlgebraicVector.X ), true );
        blueOrbit .setDotLocation( 0d, 1d );

        return this;
    }
    
    @Override
	public Axis getPreferredAxis()
	{
		return this .preferredAxis;
	}

	@Override
	public RealVector embedInR3( AlgebraicVector v )
	{
		RealVector rv = super.embedInR3( v );
        Double x = rv.x + ( rv.y / sigmaX2 );
        Double y = rv.y * skewFactor;
		return new RealVector( x, y, rv.z );
	}
    
    @Override
    public boolean isTrivial()
    {
    	return false; // signals the POV-Ray exporter to generate the tranform
    }

    @Override
    public String getName()
    {
    	if ( this .correctedOrbits )
    		return "heptagonal antiprism corrected";
    	else
    		return "heptagonal antiprism";
    }

	@Override
	public int[] subgroup( String name )
	{
		return null; // TODO
	}
    
    @Override
    public AlgebraicVector[] getOrbitTriangle()
    {
        HeptagonField hf = (HeptagonField) this .getField();
        
        AlgebraicVector blueVertex = hf .basisVector( 3, AlgebraicVector.X );
        AlgebraicVector redVertex = hf .basisVector( 3, AlgebraicVector.Z );
        
        AlgebraicNumber s = hf .getAffineScalar().reciprocal(); // reciprocal of sigma
        AlgebraicVector orthoVertex = hf .origin( 3 )
                .setComponent( AlgebraicVector.X, hf .one() )
                .setComponent( AlgebraicVector.Y, s .negate() );
        
        return new AlgebraicVector[] { blueVertex, redVertex, orthoVertex };
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
