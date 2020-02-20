
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.math.symmetry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.PentagonField;


/**
 * @author Scott Vorthmann
 *
 */
public class IcosahedralSymmetry extends AbstractSymmetry
{
    private final int[][] INCIDENCES = new int[60][3];
    
    @JsonIgnore
	public final Permutation IDENTITY = new Permutation( this, null );

	private Axis preferredAxis;
    
	public static void main(String[] args) {
		new IcosahedralSymmetry( new PentagonField(), "" );
	}
	
    public IcosahedralSymmetry( AlgebraicField field, String defaultStyle )
    {
        super( 60, field, "blue", defaultStyle );
        
        for ( int i = 0; i < this.INCIDENCES.length; i++ ) {
            this .INCIDENCES[ i ][ 0 ] = getPermutation( i ) .mapIndex( 30 );
            this .INCIDENCES[ i ][ 1 ] = getPermutation( i ) .mapIndex( 45 );
            this .INCIDENCES[ i ][ 2 ] = getPermutation( i ) .mapIndex( 42 );
        }
        
        // These were derived manually by examining the orientation key dodec.
        // 0 -> 1 is a yellow axis rotation, and 0 -> 15 is a blue axis rotation.
        //
        //  The second and third columns of arguments correspond to yellow axis and
        //  blue axis rotations, respectively.  The yellow and blue axes selected
        //  are those that surround the pentagon face containing element 0.
        //  For yellow, the axes of rotation are, in order: 9, 12, 0, 3, 6.
        //  For blue, the axes of rotation are, in order: 0, 3, 6, 9, 12.
        tetrahedralSubgroup[ 0 ] = closure( new int[]{ 1, 15 } );
        tetrahedralSubgroup[ 1 ] = closure( new int[]{ 11, 20 } );
        tetrahedralSubgroup[ 2 ] = closure( new int[]{ 27, 58 } );
        tetrahedralSubgroup[ 3 ] = closure( new int[]{ 17, 4 } );
        tetrahedralSubgroup[ 4 ] = closure( new int[]{ 55, 14 } );

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
                    // For yellow, this does not work; a single yellow axis does not uniquely
                    //  determine an oriented tetrahedron.  I'm keeping it here to support
                    //  existing files that create a tetrahedral tool using a yellow axis.
                    //  The UI will prevent creating such tools, henceforward.
                    if ( tetrahedralSubgroup[ j ][ k ] == yellowZone .getRotation() )
                        yellowTetrahedral[ i ] = j;
                }
            }
        }
        
        // The tetrahedral subgroups above correspond to blue rotations around 0, 3, 6, 9, 12,
        //  and those will preserve green axes 6, 9, 12, 0, 3, respectively.
        int[] greenSeeds = new int[]{ 6, 9, 12, 0, 3 };
        for ( int j = 0; j < tetrahedralSubgroup.length; j++ ) {
        	int seedAxis = greenSeeds[ j ];
            // for each orientation in this subgroup, map the seed to another green axis
            for ( int k = 0; k < tetrahedralSubgroup[ j ] .length; k++ ) {
            	int mappedAxis = mOrientations[ tetrahedralSubgroup[ j ][ k ] ] .mapIndex( seedAxis );
            	greenTetrahedral[ mappedAxis ] = j;
            }
        }
    }
    
    @Override
    public int[] getIncidentOrientations( int orientation )
    {
        return this .INCIDENCES[ orientation ];
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
    
    
    @Override
    public String getName()
    {
        return "icosahedral";
    }

    @Override
    protected AlgebraicVector[] getOrbitTriangle()
    {
        AlgebraicNumber twice = mField .createRational( 2 );
        AlgebraicVector blueVertex = this .getDirection( "blue" ) .getPrototype() .scale( twice );
        AlgebraicVector redVertex = this .getDirection( "red" ) .getPrototype();
        AlgebraicNumber phiInv = mField.getGoldenRatio().reciprocal();
        AlgebraicVector yellowVertex = this .getDirection( "yellow" ) .getPrototype() .scale( phiInv );
        return new AlgebraicVector[] { blueVertex, redVertex, yellowVertex };
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
            return this .getDirection( "yellow" );

        default:
            return this .getDirection( "black" );  // turqouise is more central, but black needs no index correction
        }
    }

    @Override
    protected void createFrameOrbit( String frameColor )
    {
        AlgebraicVector xAxis = mField .basisVector( 3, AlgebraicVector .X );
        
        // mMatrices not yet initialized, so this should create no zone (Axis)
        Direction dir = createZoneOrbit( frameColor, 0, 15, xAxis, true, true, mField .createRational( 2 ) );
        
        dir .setScaleNames( new String[]{ "b0", "b1", "b2", "b3" } );
        createBlueAxes( dir, 0, 15, xAxis );
        createBlueAxes( dir, 9, 13, mField .createVector( new int[][]{ {1,2 ,0,1}, {0,1, 1,2}, {-1,2, 1,2} } ) );
        createBlueAxes( dir, 6, 49, mField .createVector( new int[][]{ {1,2 ,0,1}, {0,1, 1,2}, { 1,2,-1,2} } ) );
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
    
    @Override
	public Axis getPreferredAxis()
	{
		return this .preferredAxis;
	}

    /**
     * @see com.vzome.core.math.symmetry.AbstractSymmetry#createOtherOrbits()
     * 
     * @see com.vzome.core.algebra.AlgebraicField#createVector()
     * 
     * @see com.vzome.core.math.symmetry.AbstractSymmetry#createZoneOrbit(String, int, int, AlgebraicVector, boolean, boolean, AlgebraicNumber)
     *
     */
    @Override
    protected void createOtherOrbits()
    {
        // Here, {a,b,c,d} results in an AlgebraicNumber = a/b + 𝜑 * c/d .
        //  Since b=d=1 for everything here, that's a+c𝜑.

        Direction dir;
        dir = createZoneOrbit( "red",      0, 3,           mField .createVector( new int[][]{ {0,1, 1,1}, {1,1, 0,1}, {0,1, 0,1} } ), true );
        dir .setScaleNames( new String[]{ "r0", "r1", "r2", "r3" } );

        this .preferredAxis = dir .getAxis( Symmetry.PLUS, 1 );

        dir = createZoneOrbit( "yellow",   0, 27,          mField .createVector( new int[][]{ { 1,1, 1,1}, {0,1, 0,1}, {-1,1, 0,1} } ), true, false, mField .createPower( -1 ) );
        dir .setScaleNames( new String[]{ "y0", "y1", "y2", "y3" } );

        dir = createZoneOrbit( "green",    6, NO_ROTATION, mField .createVector( new int[][]{ { 1,1, 0,1}, {1,1, 0,1}, {0,1, 0,1} } ), true, true, mField .createRational( 2 ) );
        dir .setScaleNames( new String[]{ "g0", "g1", "g2", "g3" } );

        createZoneOrbit( "orange",    6, NO_ROTATION, mField .createVector( new int[][]{ { 1,1, 0,1}, { 0,1, 1,1}, { 0,1, 0,1} } ) );

        createZoneOrbit( "purple",    0, NO_ROTATION, mField .createVector( new int[][]{ { 1,1, 1,1}, { 1,1, 0,1}, { 0,1, 0,1} } ), false, false, mField .createPower( -1 ) );

        createZoneOrbit( "black",     3, NO_ROTATION, mField .createVector( new int[][]{ { 0,1, 1,1}, { 1,1, 0,1}, { 1,1,-1,1} } ) );

        createZoneOrbit( "lavender",  0, NO_ROTATION, mField .createVector( new int[][]{ { 2,1,-1,1}, { 0,1, 1,1}, { 2,1,-1,1} } ) ) .withCorrection();

        createZoneOrbit( "olive",     0, NO_ROTATION, mField .createVector( new int[][]{ { 0,1, 1,1}, { 0,1, 1,1}, { 2,1,-1,1} } ) ) .withCorrection();

        createZoneOrbit( "maroon",    0, NO_ROTATION, mField .createVector( new int[][]{ {-1,1, 1,1}, { 3,1,-1,1}, { 1,1,-1,1} } ) ) .withCorrection();

        createZoneOrbit( "rose",      0, NO_ROTATION, mField .createVector( new int[][]{ { 2,1,-1,1}, {-1,1, 2,1}, { 0,1, 0,1} } ) ) .withCorrection();

        createZoneOrbit( "navy",      0, NO_ROTATION, mField .createVector( new int[][]{ {-1,1, 2,1}, { 1,1, 1,1}, { 0,1, 0,1} } ), false, false, mField .createPower( -1 ) ) .withCorrection();

        createZoneOrbit( "turquoise", 0, NO_ROTATION, mField .createVector( new int[][]{ { 2,1, 0,1}, { 2,1,-1,1}, {-3,1, 2,1} } ) ) .withCorrection();

        createZoneOrbit( "coral",     0, NO_ROTATION, mField .createVector( new int[][]{ {-3,1, 3,1}, { 0,1, 0,1}, { 1,1, 0,1} } ) ) .withCorrection();

        createZoneOrbit( "sulfur",    0, NO_ROTATION, mField .createVector( new int[][]{ {-3,1, 3,1}, { 2,1,-1,1}, { 0,1, 0,1} } ) ) .withCorrection();

        createZoneOrbit( "sand",      0, NO_ROTATION, mField .createVector( new int[][]{ {-2,1, 2,1}, {-2,1, 2,1}, { 2,1, 0,1} } ) ) .withCorrection();

        createZoneOrbit( "apple",     0, NO_ROTATION, mField .createVector( new int[][]{ { 5,1,-3,1}, { 1,1, 0,1}, { 0,1, 1,1} } ) ) .withCorrection();

        createZoneOrbit( "cinnamon",  0, NO_ROTATION, mField .createVector( new int[][]{ { 5,1,-3,1}, { 2,1,-1,1}, { 2,1, 0,1} } ) ) .withCorrection();

        createZoneOrbit( "spruce",    0, NO_ROTATION, mField .createVector( new int[][]{ {-3,1, 2,1}, {-3,1, 2,1}, { 5,1,-2,1} } ) ) .withCorrection();

        createZoneOrbit( "brown",     0, NO_ROTATION, mField .createVector( new int[][]{ {-1,1, 1,1}, {-1,1, 1,1}, {-2,1, 2,1} } ) ) .withCorrection();
    }


    @Override
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
        for (int[] start : starts) {
            for (int j = 0; j < start.length; j++) {
                for (int k = 0; k < 5; k++) {
                    map[start[j] + k*3] = start[(j+1)%3] + k*3;
                }
            }
        }
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
        for (int[] cycle : cycles) {
            for (int j = 0; j < cycle.length; j++) {
                map[cycle[j]] = cycle[(j+1)%5];
            }
        }
        mOrientations[ 3 ] = new Permutation( this, map );
    }

    private final int[][] tetrahedralSubgroup = new int[ 5 ][];
    
    private final int[] blueTetrahedral = new int[ 60 ];
    private final int[] greenTetrahedral = new int[ 60 ];
    private final int[] yellowTetrahedral = new int[ 60 ]; // not really correct, but retained for legacy support
    
    @Override
    public int[] subgroup( String name )
    {
        if ( TETRAHEDRAL .equals( name ) )
            return tetrahedralSubgroup[ 0 ];
        return null;
    }

    @Override
    public final Direction getDirection( String color )
    {
        if ( "spring" .equals( color ) )
            color = "apple";
        if ( "tan" .equals( color ) )
            color = "sand";
        return super .getDirection( color );
    }


    public int[] subgroup( String name, Axis zone )
    {
    	return subgroup( name, zone, true );
    }


    public int[] subgroup( String name, Axis zone, boolean allowYellow )
    {
    	// TODO: don't assume name is TETRAHEDRAL
        int orientation = zone .getOrientation();
        Direction orbit = zone .getDirection();
        String orbitName = orbit .getName();
        if ( orbitName .equals( "blue" ) )
        {
            int subgroup = blueTetrahedral[ orientation ];
            return tetrahedralSubgroup[ subgroup ];
        }
        else if ( orbitName .equals( "green" ) )
        {
            int subgroup = greenTetrahedral[ orientation ];
            return tetrahedralSubgroup[ subgroup ];
        }
        else if ( allowYellow && orbitName .equals( "yellow" ) )
        {
        	// not really correct, but retained for legacy support
            int subgroup = yellowTetrahedral[ orientation ];
            return tetrahedralSubgroup[ subgroup ];
        }
        return null;
    }

	public int blueTetrahedralFromGreen( int greenIndex )
	{
		int subgroup = this .greenTetrahedral[ greenIndex ];
		for (int i = 0; i < this .blueTetrahedral .length; i++) {
			if ( this .blueTetrahedral[ i ] == subgroup )
				return i;
		}
		return 0;
	}
}
