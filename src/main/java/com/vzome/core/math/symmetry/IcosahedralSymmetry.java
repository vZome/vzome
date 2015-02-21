
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.math.symmetry;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.BigRational;
import com.vzome.core.algebra.SnubDodecField;


/**
 * @author Scott Vorthmann
 *
 */
public class IcosahedralSymmetry extends AbstractSymmetry
{    
	public final Permutation IDENTITY = new Permutation( this, null );
    
    public IcosahedralSymmetry( AlgebraicField field, String defaultStyle )
    {
        super( 60, field, "blue", defaultStyle );
        
        // These were derived manually by examining the orientation key dodec.
        // 0 -> 1 is a yellow axis rotation, and 0 -> 15 is a blue axis rotation
        tetrahedralSubgroup[ 0 ] = closure( new int[]{ 0, 1, 15 } );
        tetrahedralSubgroup[ 1 ] = closure( new int[]{ 0, 11, 20 } );
        tetrahedralSubgroup[ 2 ] = closure( new int[]{ 0, 27, 58 } );
        tetrahedralSubgroup[ 3 ] = closure( new int[]{ 0, 4, 17 } );
        tetrahedralSubgroup[ 4 ] = closure( new int[]{ 0, 55, 14 } );

        Direction blueOrbit = getDirection( "blue" );
        Direction yellowOrbit = getDirection( "yellow" );
        for ( int i = 0; i < 60; i++ ) {
            Axis blueZone = blueOrbit .getAxis( Symmetry .PLUS, i );
            Axis yellowZone = yellowOrbit .getAxis( Symmetry .PLUS, i );
            for ( int j = 0; j < tetrahedralSubgroup.length; j++ ) {
                // for each orientation in this subgroup,
                for ( int k = 0; k < tetrahedralSubgroup[j].length; k++ ) {
                    // if the orientation is the rotation around blueZone,
                    if ( tetrahedralSubgroup[ j ][ k ] == blueZone .getRotation() )
                        // mark blueZone as a rotation axis of this subgroup
                        blueTetrahedral[ i ] = j;
                    if ( tetrahedralSubgroup[ j ][ k ] == yellowZone .getRotation() )
                        yellowTetrahedral[ i ] = j;
                }
            }
        }
    }


    /**
     * Create a collection of blue-axis normals from a prototype,
     * by applying known rotations.
     */
    private void createBlueAxes( Direction dir, int prototype, int rotated, AlgebraicVector xyz )
    {
    	int orientation = 0;
    	boolean[] reflect = { false, false, false };
        for ( int i = 0; i < 3; i++ ){
        	for ( int k = 0; k < 2; k++ ) {
        		for ( int l = 0; l < 2; l++ ) {
        			int unit = mOrientations[ orientation ] .mapIndex( prototype );
        			if ( dir .getAxis( PLUS, unit ) == null ) {
        				int rot = mOrientations[ orientation ] .mapIndex( rotated );
        				int rotation = getMapping( unit, rot );
                        AlgebraicVector norm = mField .origin( 3 );
                        for ( int m = 0; m < 3; m++ ) {
                            int offset = ((m+3-i)%3);
                            if ( reflect[m] ) {
                                norm .setComponent( m, xyz .getComponent( offset ) .negate() );
                            }
                            else {
                                norm .setComponent( m, xyz .getComponent( offset ) );
                            }
                        }
        				dir .createAxis( unit, rotation, norm );
        				dir .createAxis( rot, rotation, norm );
        			}
        			orientation = mOrientations[ 45 ] .mapIndex( orientation ); // around Y
                    reflect[0] = ! reflect[0]; reflect[2] = ! reflect[2];
        		}
        		orientation = mOrientations[ 15 ] .mapIndex( orientation ); // around X
                reflect[1] = ! reflect[1]; reflect[2] = ! reflect[2];
        	}
        	orientation = mOrientations[ 1 ] .mapIndex( orientation );
        }
    }
    
    
    public String getName()
    {
        return "icosahedral";
    }

    /**
     * Return an "inflated" vector, sized appropriately for the field, with the 1 and tau
     * components (for X, Y, and Z) set from the canonical input.
     * @param canonical
     * @return
     */
    private AlgebraicVector vector( int[] canonical )
    {
        int order = mField .getOrder();
        if ( order == 2 )
            return mField .createVector( canonical );
        AlgebraicVector result = mField .origin( 3 );
        for (int i = 0; i < 3; i++)
        {
            BigRational[] factors = new BigRational[ canonical.length / 6 ];
            for (int j = 0; j < factors.length; j++) {
				factors[ j ] = new BigRational( canonical[ i*4 + j*2 + 0 ], canonical[ i*4 + j*2 + 1 ] );
			}
            result .setComponent( i, mField .createAlgebraicNumber( factors ) );
        }
        return result;
    }

    /**
     * Make a rational vector with unit denominators from an integer vector.
     * @param canonical
     * @return
     */
    private AlgebraicVector rationalVector( int[] integers )
    {
        AlgebraicVector result = mField .origin( 3 );
        for (int i = 0; i < 3; i++)
        {
            int[] factors = new int[ integers.length / 3 ];
            for (int j = 0; j < factors.length; j++) {
				factors[ j ] = integers[ i * factors.length + j ];
			}
            result .setComponent( i, mField .createAlgebraicNumber( factors ) );
        }
        return result;
    }

    protected void createFrameOrbit( String frameColor )
    {
        AlgebraicVector xAxis = mField .basisVector( 3, AlgebraicVector .X );
        
        Direction dir = createZoneOrbit( frameColor, 0, 15, xAxis, true, true, mField .createRational( new int[]{ 2, 1 } ) );
//        Direction dir = new Direction( "blue", this, 0, 15, xAxis, true );
//        dir .setHalfSizes( true );
//        dir .setUnitLength( mField .createRational( new int[]{ 2, 1 } ) );
//        mDirectionList .add( dir );
//        mDirectionMap .put( dir .getName(), dir );
//        orbitSet .add( dir );
        
        dir .setScaleNames( new String[]{ "b0", "b1", "b2", "b3" } );
        createBlueAxes( dir, 0, 15, xAxis );
        createBlueAxes( dir, 9, 13, vector( new int[]{ 1,2,0,1, 0,1,1,2, -1,2,1,2 } ) );
        createBlueAxes( dir, 6, 49, vector( new int[]{ 1,2,0,1, 0,1,1,2, 1,2,-1,2 } ) );
        for ( int p = 0; p < mOrientations.length; p++ ) {
            int x = mOrientations[ p ] .mapIndex( 0 );
            int y = mOrientations[ p ] .mapIndex( 1 );
            int z = mOrientations[ p ] .mapIndex( 2 );
            mMatrices[ p ] = new AlgebraicMatrix(
                    dir .getAxis( PLUS, x ) .normal(),
                    dir .getAxis( PLUS, y ) .normal(),
                    dir .getAxis( PLUS, z ) .normal() );
            
            Axis axis = dir .getAxis( PLUS, p );
            AlgebraicVector norm = mMatrices[ p ] .timesColumn( xAxis );
            if ( ! norm .equals( axis .normal() ) )
                throw new IllegalStateException( "matrix wrong: " + p );
        }
    }
    
    protected void createOtherOrbits()
    {
        
    	Direction dir;
    	dir = createZoneOrbit( "red",      0, 3,           vector( new int[]{ 0,1,1,1, 1,1,0,1, 0,1,0,1 } ), true );
        dir .setScaleNames( new String[]{ "r0", "r1", "r2", "r3" } );
        
        dir = createZoneOrbit( "yellow",   0, 27,          vector( new int[]{ 1,1,1,1, 0,1,0,1, -1,1,0,1 } ), true, false, mField .createPower( -1 ) );
        dir .setScaleNames( new String[]{ "y0", "y1", "y2", "y3" } );

        dir = createZoneOrbit( "green",    6, NO_ROTATION, vector( new int[]{ 1,1,0,1, 1,1,0,1, 0,1,0,1 } ), true, true, mField .createRational( new int[]{ 2, 1 } ) );
        dir .setScaleNames( new String[]{ "g0", "g1", "g2", "g3" } );
        
        createZoneOrbit( "orange",   6, NO_ROTATION, vector( new int[]{ 1,1,0,1, 0,1,1,1, 0,1,0,1 } ) );

        createZoneOrbit( "purple",   0, NO_ROTATION, vector( new int[]{ 1,1,1,1, 1,1,0,1, 0,1,0,1 } ), false, false, mField .createPower( -1 ) );

        createZoneOrbit( "black",    3, NO_ROTATION, vector( new int[]{ 0,1,1,1, 1,1,0,1, 1,1,-1,1 } ) );
        
        createZoneOrbit( "lavender", 0, NO_ROTATION, vector( new int[]{ 2,1,-1,1, 0,1,1,1, 2,1,-1,1 } ) );
        
        createZoneOrbit( "olive",    0, NO_ROTATION, vector( new int[]{ 0,1,1,1, 0,1,1,1, 2,1,-1,1 } ) );
        
        createZoneOrbit( "maroon",   0, NO_ROTATION, vector( new int[]{ -1,1,1,1, 3,1,-1,1, 1,1,-1,1 } ) );
        
        createZoneOrbit( "rose",     0, NO_ROTATION, vector( new int[]{ 2,1,-1,1, -1,1,2,1, 0,1,0,1 } ) );
        
        createZoneOrbit( "navy",     0, NO_ROTATION, vector( new int[]{ -1,1,2,1, 1,1,1,1, 0,1,0,1 } ), false, false, mField .createPower( -1 ) );
        
        createZoneOrbit( "turquoise", 0, NO_ROTATION, vector( new int[]{ 2,1,0,1, 2,1,-1,1, -3,1,2,1 } ) );
        
        createZoneOrbit( "coral",    0, NO_ROTATION, vector( new int[]{ -3,1,3,1, 0,1,0,1, 1,1,0,1 } ) );

        createZoneOrbit( "sulfur",   0, NO_ROTATION, vector( new int[]{ -3,1,3,1, 2,1,-1,1, 0,1,0,1 } ) );

        createZoneOrbit( "sand",     0, NO_ROTATION, vector( new int[]{ -2,1,2,1, -2,1,2,1, 2,1,0,1 } ) );

        createZoneOrbit( "apple",    0, NO_ROTATION, vector( new int[]{ 5,1,-3,1, 1,1,0,1, 0,1,1,1 } ) );

        createZoneOrbit( "cinnamon", 0, NO_ROTATION, vector( new int[]{ 5,1,-3,1, 2,1,-1,1, 2,1,0,1 } ) );

        createZoneOrbit( "spruce",   0, NO_ROTATION, vector( new int[]{ -3,1,2,1, -3,1,2,1, 5,1,-2,1 } ) );
        
        createZoneOrbit( "brown", 0, NO_ROTATION, vector( new int[] { - 1, 1, 1, 1, - 1, 1, 1, 1, - 2, 1, 2, 1 } ) );

        if ( mField instanceof SnubDodecField )
        {
/*
 * 

PENTAGON
4 + tau*-4 + xi*0 + tau*xi*0 + xi^2*-2 + tau*xi^2*2, -4 + tau*0 + xi*0 + tau*xi*0 + xi^2*2 + tau*xi^2*0, 0 + tau*0 + xi*0 + tau*xi*0 + xi^2*0 + tau*xi^2*2
4 -4 0 0 -2 2 -4 0 0 0 2 0 0 0 0 0 0 2
(2,-2,0,0,-4,4) (0,2,0,0,0,-4) (2,0,0,0,0,0)


TRIANGLE
0 + tau*-4 + xi*-2 + tau*xi*0 + xi^2*0 + tau*xi^2*2, -4 + tau*4 + xi*0 + tau*xi*-2 + xi^2*2 + tau*xi^2*-2, -4 + tau*0 + xi*-2 + tau*xi*-2 + xi^2*2 + tau*xi^2*0
0 -4 -2 0 0 2 -4 4 0 -2 2 -2 -4 0 -2 -2 2 0
(2,0,0,-2,-4,0) (-2,2,-2,0,4,-4) (0,2,-2,-2,0,-4)


DIAGONAL
8 + tau*0 + xi*0 + tau*xi*4 + xi^2*-4 + tau*xi^2*0, 0 + tau*-4 + xi*0 + tau*xi*0 + xi^2*0 + tau*xi^2*0, 0 + tau*0 + xi*0 + tau*xi*0 + xi^2*0 + tau*xi^2*0
8 0 0 4 -4 0 0 -4 0 0 0 0 0 0 0 0 0 0
(0,-4,4,0,0,8) (0,0,0,0,-4,0) (0,0,0,0,0,0)

 */      
            AlgebraicNumber scale = mField .createPower( -3 );
            createZoneOrbit( "snubPentagon", 0, NO_ROTATION, rationalVector( new int[]{ 4,-4,0,0,-2,2,  -4,0,0,0,2,0,  0,0,0,0,0,2 } ), false, false, scale );
            createZoneOrbit( "snubTriangle", 0, NO_ROTATION, rationalVector( new int[]{ 0,-4,-2,0,0,2,  -4,4,0,-2,2,-2,  -4,0,-2,-2,2,0 } ), false, false, scale );
            createZoneOrbit( "snubDiagonal", 0, NO_ROTATION, rationalVector( new int[]{ 8,0,0,4,-4,0,  0,-4,0,0,0,0,  0,0,0,0,0,0 } ), false, false, scale );
        }
    }


    protected void createInitialPermutations()
    {
        final int ORDER = 60;
        mOrientations[ 0 ] = IDENTITY;
        // first, compute the rotation around the X axis
        int[] map = new int[ORDER];
        for ( int i = 0; i < 15; i++ ) {
            map[ i ] = i+15;
            map[ i+15 ] = i;
            map[ i+30 ] = i+45;
            map[ i+45 ] = i+30;
        }
        mOrientations[ 15 ] = new Permutation( this, map );
        // now, compute the rotation around the first octant
        map = new int[ORDER];
        int[][] starts = new int[][]{ {0,1,2}, {15,46,32}, {16,47,30}, {17,45,31} }; // NOTE: could even do this algorithmically!
        for ( int i = 0; i < starts.length; i++ )
            for ( int j = 0; j < starts[i].length; j++ )
                for ( int k = 0; k < 5; k++ )
                    map[ starts[i][j] + k*3 ] = starts[i][ (j+1)%3 ] + k*3;
        mOrientations[ 1 ] = new Permutation( this, map );
        // finally, a rotation around a red axis... this one cannot be done algorithmically
        map = new int[ORDER];
        int[][] cycles = new int[][] {
                { 0, 3, 6, 9, 12 },
                { 30, 42, 39, 36, 33 },
                { 2, 21, 29, 55, 4},
                { 5, 24, 17, 58, 7 },
                { 8, 27, 20, 46, 10 },
                { 11, 15, 23, 49, 13 },
                { 1, 14, 18, 26, 52 },
                { 16, 50, 57, 38, 40 },
                { 19, 53, 45, 41, 43 },
                { 22, 56, 48, 44, 31 },
                { 25, 59, 51, 32, 34 },
                { 28, 47, 54, 35, 37 }
                };
        for ( int i = 0; i < cycles.length; i++ )
            for ( int j = 0; j < cycles[i].length; j++ )
                    map[ cycles[i][j] ] = cycles[i][ (j+1)%5 ];
        mOrientations[ 3 ] = new Permutation( this, map );
    }

    private final int[][] tetrahedralSubgroup = new int[ 5 ][];
    
    private final int[] yellowTetrahedral = new int[ 60 ];
    private final int[] blueTetrahedral = new int[ 60 ];
    
    public int[] subgroup( String name )
    {
        if ( TETRAHEDRAL .equals( name ) )
            return tetrahedralSubgroup[ 0 ];
        return null;
    }

    public Direction getDirection( String color )
    {
        if ( "spring" .equals( color ) )
            color = "apple";
        if ( "tan" .equals( color ) )
            color = "sand";
        return super .getDirection( color );
    }


    public int[] subgroup( String name, Axis zone )
    {
        int orientation = zone .getOrientation();
        Direction orbit = zone .getDirection();
        String orbitName = orbit .getName();
        if ( orbitName .equals( "blue" ) )
        {
            int subgroup = blueTetrahedral[ orientation ];
            return tetrahedralSubgroup[ subgroup ];
        }
        else if ( orbitName .equals( "yellow" ) )
        {
            int subgroup = yellowTetrahedral[ orientation ];
            return tetrahedralSubgroup[ subgroup ];
        }
        return null;
    }
}
