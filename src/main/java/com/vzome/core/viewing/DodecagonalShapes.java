
//(c) Copyright 2006, Scott Vorthmann

package com.vzome.core.viewing;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.parts.DefaultStrutGeometry;
import com.vzome.core.parts.StrutGeometry;

public class DodecagonalShapes extends AbstractShapes 
{
    public DodecagonalShapes( String pkgName, String name, String alias, Symmetry symm )
    {
        super( pkgName, name, alias, symm );
    }

    private static final int[] V0 = { 1,1,0,1, 0,1,0,1, 0,1,0,1 };
    private static final int[] V1 = { 1,2,0,1, 0,1,1,2, 0,1,0,1 };
    private static final int[] V2 = { -1,2,0,1, 0,1,1,2, 0,1,0,1 };
    private static final int[] V3 = { -1,1,0,1, 0,1,0,1, 0,1,0,1 };
    private static final int[] V4 = { -1,2,0,1, 0,1,-1,2, 0,1,0,1 };
    private static final int[] V5 = { 1,2,0,1, 0,1,-1,2, 0,1,0,1 };

    protected Polyhedron buildConnectorShape( String pkgName )
    {
        Polyhedron hex = new Polyhedron( mSymmetry .getField() );
        hex .addVertex( V0 );
        hex .addVertex( V1 );
        hex .addVertex( V2 );
        hex .addVertex( V3 );
        hex .addVertex( V4 );
        hex .addVertex( V5 );
        Polyhedron.Face face = hex .newFace();
        face .add( new Integer( 0 ) );
        face .add( new Integer( 1 ) );
        face .add( new Integer( 2 ) );
        face .add( new Integer( 3 ) );
        face .add( new Integer( 4 ) );
        face .add( new Integer( 5 ) );
        hex .addFace( face );
        face = hex .newFace();
        face .add( new Integer( 5 ) );
        face .add( new Integer( 4 ) );
        face .add( new Integer( 3 ) );
        face .add( new Integer( 2 ) );
        face .add( new Integer( 1 ) );
        face .add( new Integer( 0 ) );
        hex .addFace( face );
        return hex;
    }
    
    protected StrutGeometry createStrutGeometry( Direction dir )
    {
        if ( dir .getName() .equals( "blue" ) )
            return new BlueStrutGeometry();
        else if ( dir .getName() .equals( "green" ) )
            return new GreenStrutGeometry();
        else
            return new DefaultStrutGeometry( dir );
    }
    
    private final class NoStrutGeometry implements StrutGeometry
    {
        public Polyhedron getStrutPolyhedron( int[] length )
        {
            // TODO Auto-generated method stub
            return null;
        }
    }
    
    private static final int[] THIRD = { 1,3,0,1 };
    private static final int[] TWO = { 2,1,0,1, 0,1,0,1, 0,1,0,1 };
    
    private final class BlueStrutGeometry implements StrutGeometry
    {
        public Polyhedron getStrutPolyhedron( int[] length )
        {
            AlgebraicField field = mSymmetry .getField();
            Polyhedron hex = new Polyhedron( field );
            int[] strut = field .scaleVector( new int[]{ 1,1,0,1, 0,1,0,1, 0,1,0,1 }, length );
            hex .addVertex( field .add( strut, field .subtract( field .scaleVector( V5, THIRD ), TWO ) ) );
            hex .addVertex( field .add( strut, field .subtract( field .scaleVector( V0, THIRD ), TWO ) ) );
            hex .addVertex( field .add( strut, field .subtract( field .scaleVector( V1, THIRD ), TWO ) ) );
            hex .addVertex( field .add( field .scaleVector( V2, THIRD ), TWO ) );
            hex .addVertex( field .add( field .scaleVector( V3, THIRD ), TWO ) );
            hex .addVertex( field .add( field .scaleVector( V4, THIRD ), TWO ) );
            Polyhedron.Face face = hex .newFace();
            face .add( new Integer( 0 ) );
            face .add( new Integer( 1 ) );
            face .add( new Integer( 2 ) );
            face .add( new Integer( 3 ) );
            face .add( new Integer( 4 ) );
            face .add( new Integer( 5 ) );
            hex .addFace( face );
            face = hex .newFace();
            face .add( new Integer( 5 ) );
            face .add( new Integer( 4 ) );
            face .add( new Integer( 3 ) );
            face .add( new Integer( 2 ) );
            face .add( new Integer( 1 ) );
            face .add( new Integer( 0 ) );
            hex .addFace( face );
            return hex;
        }
    }

    private static final int[] GREEN = { 1,1, 1,2, 1,2, 0,1, 0,1, 0,1 };
    private static final int[] V6 = { 1,1, 0,1, 0,1, 1,3, 0,1, 0,1 };
    private static final int[] V7 = { 0,1, 2,3, 0,1, 0,1, 0,1, 0,1 };
    
    private final class GreenStrutGeometry implements StrutGeometry
    {
        public Polyhedron getStrutPolyhedron( int[] length )
        {
            AlgebraicField field = mSymmetry .getField();
            int[] vector = field .scaleVector( GREEN, length );
            Polyhedron hex = new Polyhedron( field );
            hex .addVertex( field .add( vector, field .negate( field .add( GREEN, V6 ) ) ) );
            hex .addVertex( field .add( vector, field .negate( GREEN ) ) );
            hex .addVertex( field .add( vector, field .negate( field .add( GREEN, V7 ) ) ) );
            hex .addVertex( field .add( V6, GREEN ) );
            hex .addVertex( GREEN );
            hex .addVertex( field .add( V7, GREEN ) );
            Polyhedron.Face face = hex .newFace();
            face .add( new Integer( 0 ) );
            face .add( new Integer( 1 ) );
            face .add( new Integer( 2 ) );
            face .add( new Integer( 3 ) );
            face .add( new Integer( 4 ) );
            face .add( new Integer( 5 ) );
            hex .addFace( face );
            face = hex .newFace();
            face .add( new Integer( 5 ) );
            face .add( new Integer( 4 ) );
            face .add( new Integer( 3 ) );
            face .add( new Integer( 2 ) );
            face .add( new Integer( 1 ) );
            face .add( new Integer( 0 ) );
            hex .addFace( face );
            return hex;
        }
    }

}
