//(c) Copyright 2006, Scott Vorthmann

package com.vzome.core.viewing;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.RationalVectors;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.symmetry.Symmetry;

public class OctahedralShapes extends AbstractShapes
{
    public OctahedralShapes( String pkgName, String name, Symmetry symm )
    {
        super( pkgName, name, symm );
    }

    protected Polyhedron buildConnectorShape( String pkgName )
    {
        AlgebraicField field = mSymmetry.getField();
        Polyhedron cube = new Polyhedron( field );
        int[] scale = field.createPower( - 2 );
        scale = field.multiply( field.createRational( new int[] { 2, 1 } ), scale );
        int[] x = field.basisVector( 3, RationalVectors.X );
        int[] y = field.basisVector( 3, RationalVectors.Y );
        int[] z = field.basisVector( 3, RationalVectors.Z );
        cube.addVertex( field.scaleVector( x, scale ) );
        cube.addVertex( field.scaleVector( field.negate( x ), scale ) );
        cube.addVertex( field.scaleVector( y, scale ) );
        cube.addVertex( field.scaleVector( field.negate( y ), scale ) );
        cube.addVertex( field.scaleVector( z, scale ) );
        cube.addVertex( field.scaleVector( field.negate( z ), scale ) );
        Polyhedron.Face face = cube.newFace();
        face.add( new Integer( 0 ) );
        face.add( new Integer( 2 ) );
        face.add( new Integer( 4 ) );
        cube.addFace( face );
        face = cube.newFace();
        face.add( new Integer( 0 ) );
        face.add( new Integer( 5 ) );
        face.add( new Integer( 2 ) );
        cube.addFace( face );
        face = cube.newFace();
        face.add( new Integer( 0 ) );
        face.add( new Integer( 3 ) );
        face.add( new Integer( 5 ) );
        cube.addFace( face );
        face = cube.newFace();
        face.add( new Integer( 0 ) );
        face.add( new Integer( 4 ) );
        face.add( new Integer( 3 ) );
        cube.addFace( face );
        face = cube.newFace();
        face.add( new Integer( 1 ) );
        face.add( new Integer( 4 ) );
        face.add( new Integer( 2 ) );
        cube.addFace( face );
        face = cube.newFace();
        face.add( new Integer( 1 ) );
        face.add( new Integer( 2 ) );
        face.add( new Integer( 5 ) );
        cube.addFace( face );
        face = cube.newFace();
        face.add( new Integer( 1 ) );
        face.add( new Integer( 5 ) );
        face.add( new Integer( 3 ) );
        cube.addFace( face );
        face = cube.newFace();
        face.add( new Integer( 1 ) );
        face.add( new Integer( 3 ) );
        face.add( new Integer( 4 ) );
        cube.addFace( face );
        return cube;
    }

}
