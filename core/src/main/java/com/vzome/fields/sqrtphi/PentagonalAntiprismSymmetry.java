package com.vzome.fields.sqrtphi;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.symmetry.AbstractSymmetry;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Permutation;
import com.vzome.core.math.symmetry.Symmetry;

public class PentagonalAntiprismSymmetry extends AbstractSymmetry
{
    // All vectors and matrices here were constructed and captured using the octahedral system.
    //  See https://www.dropbox.com/s/qvtp8tpy1roc8pp/symmetry-axis-study.vZome?dl=0
    //   or Dropbox/vZome/attachments/2018/05-May/09-Scott-kostick-symmetry/symmetry-axis-study.vZome
    
    private static final int[][] FIVEFOLD_AXIS = new int[][]{ {-3,1, 0,1, -5,1, 0,1}, {3,1, 0,1, 5,1, 0,1}, {0,1, 1,1, 0,1, 2,1} };

    private static final int[][] TWOFOLD_AXIS = new int[][]{ {0,1}, {2,1, 0,1, 3,1, 0,1}, {0,1, -3,1, 0,1, -5,1} };
    
    // AlgebraicMatrix is stored in row-major order, and the data here
    //  is also represented that way, but remember that the
    //  AlgebraicMatrix constructor requires column vectors.
    
    private static final int[][][] FIVEFOLD_ROTATION = new int[][][]
    		{{{-1,1,0,1,1,1,0,1},{0,1,0,1,0,1,0,1},{0,1,1,1,0,1,-1,1}},
         {{1,1,0,1,-1,1,0,1},{-1,1,0,1,1,1,0,1},{0,1,-2,1,0,1,1,1}},
         {{0,1,2,1,0,1,-1,1},{0,1,-1,1,0,1,1,1},{2,1,0,1,-1,1,0,1}}};
	
    private static final int[][][] TWOFOLD_ROTATION = new int[][][]
        {{{-1,1,0,1,0,1,0,1},{0,1,0,1,0,1,0,1},{0,1,0,1,0,1,0,1}},
         {{0,1,0,1,0,1,0,1},{1,1,0,1,-1,1,0,1},{0,1,1,1,0,1,-1,1}},
         {{0,1,0,1,0,1,0,1},{0,1,1,1,0,1,-1,1},{-1,1,0,1,1,1,0,1}}};

    private static final int[][][] PRINCIPAL_REFLECTION = new int[][][]
        	{{{7,5,0,1,-4,5,0,1},{-2,5,0,1,4,5,0,1},{0,1,-8,5,0,1,6,5}},
        {{-2,5,0,1,4,5,0,1},{7,5,0,1,-4,5,0,1},{0,1,8,5,0,1,-6,5}},
        {{0,1,-8,5,0,1,6,5},{0,1,8,5,0,1,-6,5},{-9,5,0,1,8,5,0,1}}};

    private Axis preferredAxis;
        
    public PentagonalAntiprismSymmetry( AlgebraicField field, String frameColor, String defaultStyle )
	{
		super( 10, field, frameColor, defaultStyle, field .createMatrix( PRINCIPAL_REFLECTION ) );
		// calls createInitialPermutations, createFrameOrbit, createOtherOrbits
	}

	/**
	 * Called by the super constructor.
	 */
	@Override
	protected void createInitialPermutations()
	{
        mOrientations[0] = new Permutation( this, null );

        // first, define the 5-fold rotation
        int[] map = new int[]{ 1, 2, 3, 4, 0, 6, 7, 8, 9, 5 };
        mOrientations[1] = new Permutation( this, map );

        // then, then 2-fold rotation
        map = new int[]{ 5, 9, 8, 7, 6, 0, 4, 3, 2, 1 };
        mOrientations[5] = new Permutation( this, map );
    }

	@Override
	protected void createFrameOrbit( String frameColor )
	{
		// Breaking the bad pattern of orbit initialization in the AbstractSymmetry constructor,
		//   we are just initializing matrices, here.
		
        mMatrices[ 0 ] = this .mField .identityMatrix( 3 );
        mMatrices[ 1 ] = this .mField .createMatrix( FIVEFOLD_ROTATION );
        mMatrices[ 2 ] = mMatrices[ 1 ] .times( mMatrices[ 1 ] );
        mMatrices[ 3 ] = mMatrices[ 1 ] .times( mMatrices[ 2 ] );
        mMatrices[ 4 ] = mMatrices[ 1 ] .times( mMatrices[ 3 ] );
        
        mMatrices[ 5 ] = this .mField .createMatrix( TWOFOLD_ROTATION );
        mMatrices[ 6 ] = mMatrices[ 1 ] .times( mMatrices[ 5 ] );
        mMatrices[ 7 ] = mMatrices[ 2 ] .times( mMatrices[ 5 ] );
        mMatrices[ 8 ] = mMatrices[ 3 ] .times( mMatrices[ 5 ] );
        mMatrices[ 9 ] = mMatrices[ 4 ] .times( mMatrices[ 5 ] );
	}

	@Override
	protected void createOtherOrbits()
	{
		// Breaking the bad pattern of orbit initialization in the AbstractSymmetry constructor
	}

	public PentagonalAntiprismSymmetry createStandardOrbits( String frameColor )
	{
		Direction redOrbit = createZoneOrbit( "red", 0, 1, this .mField .createVector( FIVEFOLD_AXIS ), true );
        redOrbit .setDotLocation( 1d, 0d );
        this .preferredAxis = redOrbit .getAxis( Symmetry.PLUS, 0 );

        Direction greenOrbit = createZoneOrbit( "green", 0, 5, this .mField .createVector( TWOFOLD_AXIS ), true );
        greenOrbit .setDotLocation( 0d, 1d );
        
		Direction blueOrbit = createZoneOrbit( "blue", 0, -1, this .mField .basisVector( 3, AlgebraicVector.X ), true );
		blueOrbit .setDotLocation( 1d, 1d );

        return this;
    }
    
    @Override
	public Axis getPreferredAxis()
	{
		return this .preferredAxis;
	}
    
    @Override
    public boolean isTrivial()
    {
        return false; // signals the POV-Ray exporter to generate the tranform
    }

    @Override
    public String getName()
    {
    	    return "pentagonal antiprism";
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
            return this .getDirection( "green" );

        default:
            return null;
        }
    }
}
