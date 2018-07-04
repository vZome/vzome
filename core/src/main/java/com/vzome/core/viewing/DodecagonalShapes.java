
//(c) Copyright 2006, Scott Vorthmann

package com.vzome.core.viewing;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.parts.StrutGeometry;

public class DodecagonalShapes extends AbstractShapes 
{
    public DodecagonalShapes( String pkgName, String name, String alias, Symmetry symm )
    {
        super( pkgName, name, alias, symm );
        AlgebraicField field = symm .getField();
        V0 =    field .createVector( new int[][]{ { 1,1, 0,1}, {0,1, 0,1}, {0,1, 0,1} } );
        V1 =    field .createVector( new int[][]{ { 1,2, 0,1}, {0,1, 1,2}, {0,1, 0,1} } );
        V2 =    field .createVector( new int[][]{ {-1,2, 0,1}, {0,1, 1,2}, {0,1, 0,1} } );
        V3 =    field .createVector( new int[][]{ {-1,1, 0,1}, {0,1, 0,1}, {0,1, 0,1} } );
        V4 =    field .createVector( new int[][]{ {-1,2, 0,1}, {0,1,-1,2}, {0,1, 0,1} } );
        V5 =    field .createVector( new int[][]{ { 1,2, 0,1}, {0,1,-1,2}, {0,1, 0,1} } );
        TWO =   field .createVector( new int[][]{ { 2,1, 0,1}, {0,1, 0,1}, {0,1, 0,1} } );
        GREEN = field .createVector( new int[][]{ { 1,1, 1,2}, {1,2, 0,1}, {0,1, 0,1} } );
        V6 =    field .createVector( new int[][]{ { 1,1, 0,1}, {0,1, 1,3}, {0,1, 0,1} } );
        V7 =    field .createVector( new int[][]{ { 0,1, 2,3}, {0,1, 0,1}, {0,1, 0,1} } );
    }

    private final AlgebraicVector V0;
    private final AlgebraicVector V1;
    private final AlgebraicVector V2;
    private final AlgebraicVector V3;
    private final AlgebraicVector V4;
    private final AlgebraicVector V5;
    private final AlgebraicVector TWO;
    private final AlgebraicVector GREEN;
    private final AlgebraicVector V6;
    private final AlgebraicVector V7;

    @Override
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
        face .add(0);
        face .add(1);
        face .add(2);
        face .add(3);
        face .add(4);
        face .add(5);
        hex .addFace( face );
        face = hex .newFace();
        face .add(5);
        face .add(4);
        face .add(3);
        face .add(2);
        face .add(1);
        face .add(0);
        hex .addFace( face );
        return hex;
    }
    
    @Override
    protected StrutGeometry createStrutGeometry( Direction dir )
    {
        if ( dir .getName() .equals( "blue" ) )
            return new BlueStrutGeometry();
        else if ( dir .getName() .equals( "green" ) )
            return new GreenStrutGeometry();
        else
            return super .createStrutGeometry( dir );
    }
        
    private final class BlueStrutGeometry implements StrutGeometry
    {
        @Override
        public Polyhedron getStrutPolyhedron( AlgebraicNumber length )
        {
            AlgebraicField field = mSymmetry .getField();
            Polyhedron hex = new Polyhedron( field );
            AlgebraicVector strut = field .basisVector( 3, 0 ) .scale( length );
            final AlgebraicNumber THIRD = field .createRational( 1, 3 );
            hex .addVertex( strut .plus( V5 .scale( THIRD ) .minus( TWO ) ) );
            hex .addVertex( strut .plus( V0 .scale( THIRD ) .minus( TWO ) ) );
            hex .addVertex( strut .plus( V1 .scale( THIRD ) .minus( TWO ) ) );
            hex .addVertex( V2 .scale( THIRD ) .plus( TWO ) );
            hex .addVertex( V3 .scale( THIRD ) .plus( TWO ) );
            hex .addVertex( V4 .scale( THIRD ) .plus( TWO ) );
            Polyhedron.Face face = hex .newFace();
            face .add(0);
            face .add(1);
            face .add(2);
            face .add(3);
            face .add(4);
            face .add(5);
            hex .addFace( face );
            face = hex .newFace();
            face .add(5);
            face .add(4);
            face .add(3);
            face .add(2);
            face .add(1);
            face .add(0);
            hex .addFace( face );
            return hex;
        }
    }
    
    private final class GreenStrutGeometry implements StrutGeometry
    {
        @Override
        public Polyhedron getStrutPolyhedron( AlgebraicNumber length )
        {
            AlgebraicField field = mSymmetry .getField();
            AlgebraicVector vector = GREEN .scale( length );
            Polyhedron hex = new Polyhedron( field );
            hex .addVertex( vector .plus( GREEN .plus( V6 ) ) .negate() );
            hex .addVertex( vector .plus( GREEN .negate() ) );
            hex .addVertex( vector .plus( GREEN .plus( V7 ) .negate() ) );
            hex .addVertex( V6 .plus( GREEN ) );
            hex .addVertex( GREEN );
            hex .addVertex( V7 .plus( GREEN ) );
            Polyhedron.Face face = hex .newFace();
            face .add(0);
            face .add(1);
            face .add(2);
            face .add(3);
            face .add(4);
            face .add(5);
            hex .addFace( face );
            face = hex .newFace();
            face .add(5);
            face .add(4);
            face .add(3);
            face .add(2);
            face .add(1);
            face .add(0);
            hex .addFace( face );
            return hex;
        }
    }

}
