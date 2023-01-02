
package com.vzome.core.math.symmetry;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;

/**
 * @author Scott Vorthmann
 * 
 */
public class OctahedralSymmetry extends AbstractSymmetry
{
    private final static int ORDER = 24;

    public final Permutation IDENTITY = new Permutation( this, null );
    
    protected final String frameColor;

    public OctahedralSymmetry( AlgebraicField field ) {
        this( field, "blue");
    }

    // Only a derived class is allowed to specify a different frame color
    // Currently only the RootTwoFieldApplication.synestructicsSymmetry does so.
    protected OctahedralSymmetry( AlgebraicField field, String frameColor )
    {
        super( ORDER, field, frameColor, null );
        this.frameColor = frameColor;
        tetrahedralSubgroup = closure( new int[] { 0, 2, 4 } );
    }
    
    @Override
    public Direction getSpecialOrbit( SpecialOrbit which )
    {
        switch ( which ) {

        case BLUE:
            return this .getDirection( this .frameColor );

        case RED:
            return this .getDirection( "green" );

        case YELLOW:
            return this .getDirection( "yellow" );

        default:
            return null; // TODO pick/define a chiral orbit with no index correction (0 means 0)
        }
    }

    @Override
    public boolean reverseOrbitTriangle()
    {
        return true;
    }

    @Override
    public AlgebraicVector[] getOrbitTriangle()
    {
        AlgebraicVector greenVertex = this .getDirection( "green" ) .getPrototype();
        AlgebraicVector blueVertex = this .getDirection( "blue" ) .getPrototype();
        AlgebraicVector yellowVertex = this .getDirection( "yellow" ) .getPrototype();
        return new AlgebraicVector[] { greenVertex, blueVertex, yellowVertex };
    }

    @Override
    protected void createInitialPermutations()
    {
        mOrientations[0] = IDENTITY;
        // first, compute the rotation around the X axis (blue)
        int[] map = new int[ORDER];
        for ( int i = 0; i < 6; i++ )
            for ( int j = 0; j < 4; j++ )
                map[i * 4 + j] = i * 4 + ( ( j + 1 ) % 4 );
        mOrientations[1] = new Permutation( this, map );

        // now, the rotation around the first octant... yellow axis
        map = new int[ORDER];
        int[][] cycles = new int[][] { { 0, 4, 8 }, { 1, 11, 17 }, { 2, 16, 22 }, { 3, 21, 5 }, { 6, 20, 14 }, { 7, 13, 9 },
                { 10, 12, 18 }, { 19, 15, 23 } };
        for (int[] cycle : cycles) {
            for (int j = 0; j < cycle.length; j++) {
                map[cycle[j]] = cycle[( j + 1 ) % 3];
            }
        }
        mOrientations[4] = new Permutation( this, map );

        // finally, a rotation around a green axis...
        map = new int[ORDER];
        cycles = new int[][] { { 0, 5 }, { 1, 8 }, { 4, 9 }, { 15, 20 }, { 12, 19 }, { 16, 23 }, { 2, 17 }, { 13, 10 },
                { 21, 6 }, { 22, 3 }, { 7, 14 }, { 11, 18 } };
        for (int[] cycle : cycles) {
            for (int j = 0; j < cycle.length; j++) {
                map[cycle[j]] = cycle[( j + 1 ) % 2];
            }
        }
        mOrientations[5] = new Permutation( this, map );
    }

    @Override
	protected void createOtherOrbits()
    {
        AlgebraicVector xAxis = mField.basisVector( 3, AlgebraicVector.X );
        AlgebraicVector yAxis = mField.basisVector( 3, AlgebraicVector.Y );
        AlgebraicVector zAxis = mField.basisVector( 3, AlgebraicVector.Z );
        AlgebraicVector green = xAxis .plus( yAxis );
        createZoneOrbit( "green", 1, 8, green, true );
        AlgebraicVector yellow = green .plus( zAxis );
        createZoneOrbit( "yellow", 0, 4, yellow, true );
    }

    @Override
    protected void createFrameOrbit( String frameColor )
    {
        AlgebraicVector xAxis = mField.basisVector( 3, AlgebraicVector.X );
        AlgebraicVector yAxis = mField.basisVector( 3, AlgebraicVector.Y );
        AlgebraicVector zAxis = mField.basisVector( 3, AlgebraicVector.Z );
        Direction dir;
        if ( mField .doubleFrameVectors() )
            dir = createZoneOrbit( frameColor, 0, 1, xAxis, true, true, mField .createRational( 2 ) );
        else
            dir = createZoneOrbit( frameColor, 0, 1, xAxis, true );

        createBasisAxes( dir, xAxis, 0 );
        createBasisAxes( dir, xAxis .negate(), 12 );
        createBasisAxes( dir, yAxis, 5 );
        createBasisAxes( dir, yAxis .negate(), 7 );
        createBasisAxes( dir, zAxis, 4 );
        createBasisAxes( dir, zAxis .negate(), 6 );
        for ( int p = 0; p < ORDER; p++ ) {
            int x = mOrientations[p].mapIndex( 0 );
            int y = mOrientations[p].mapIndex( 8 );
            int z = mOrientations[p].mapIndex( 4 );
            mMatrices[p] = new AlgebraicMatrix( dir.getAxis( PLUS, x ).normal(), dir.getAxis( PLUS, y ).normal(),
                    dir.getAxis( PLUS, z ).normal() );

            Axis axis = dir.getAxis( PLUS, p );
            AlgebraicVector norm = mMatrices[p] .timesColumn( xAxis );
            if ( ! norm .equals( axis.normal() ) )
                throw new IllegalStateException( "matrix wrong: " + p );
        }
    }

    private void createBasisAxes( Direction dir, AlgebraicVector norm, int orientation )
    {
        for ( int i = 0; i < 4; i++ ) {
            int prototype = mOrientations[orientation].mapIndex( i );
            int rotatedPrototype = mOrientations[orientation].mapIndex( ( i + 1 ) % 4 );
            int rotation = this.getMapping( prototype, rotatedPrototype );
            dir.createAxis( prototype, rotation, norm );
        }
    }

    @Override
    public String getName()
    {
        return "octahedral";
    }

    private final int[] tetrahedralSubgroup;

    @Override
    public int[] subgroup( String name )
    {
        if ( TETRAHEDRAL.equals( name ) )
            return tetrahedralSubgroup;
        return null;
    }
}
