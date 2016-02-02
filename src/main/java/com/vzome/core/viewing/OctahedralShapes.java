//(c) Copyright 2006, Scott Vorthmann

package com.vzome.core.viewing;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
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
        AlgebraicNumber scale = field.createPower( - 2 );
        scale = field.createRational( 2 ) .times( scale );
        AlgebraicVector x = field.basisVector( 3, AlgebraicVector.X );
        AlgebraicVector y = field.basisVector( 3, AlgebraicVector.Y );
        AlgebraicVector z = field.basisVector( 3, AlgebraicVector.Z );
        cube.addVertex( x .scale( scale ) );
        cube.addVertex( x .negate() .scale( scale ) );
        cube.addVertex( y .scale( scale ) );
        cube.addVertex( y .negate() .scale( scale ) );
        cube.addVertex( z .scale( scale ) );
        cube.addVertex( z.negate() .scale( scale ) );
        Polyhedron.Face face = cube.newFace();
        face.add(0);
        face.add(2);
        face.add(4);
        cube.addFace( face );
        face = cube.newFace();
        face.add(0);
        face.add(5);
        face.add(2);
        cube.addFace( face );
        face = cube.newFace();
        face.add(0);
        face.add(3);
        face.add(5);
        cube.addFace( face );
        face = cube.newFace();
        face.add(0);
        face.add(4);
        face.add(3);
        cube.addFace( face );
        face = cube.newFace();
        face.add(1);
        face.add(4);
        face.add(2);
        cube.addFace( face );
        face = cube.newFace();
        face.add(1);
        face.add(2);
        face.add(5);
        cube.addFace( face );
        face = cube.newFace();
        face.add(1);
        face.add(5);
        face.add(3);
        cube.addFace( face );
        face = cube.newFace();
        face.add(1);
        face.add(3);
        face.add(4);
        cube.addFace( face );
        return cube;
    }

}
