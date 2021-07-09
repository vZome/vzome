package com.vzome.core.math.symmetry;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;
import com.vzome.core.algebra.PolygonField;
import com.vzome.core.math.RealVector;

/**
 * @author David Hall
 * This class is a generalized implementation initially based on the HeptagonalAntiprismSymmetry by Scott Vorthmann
 */
public class AntiprismSymmetry extends AbstractSymmetry {

    private Axis preferredAxis;
    private final boolean useShear;
    private final Matrix3d shearTransform;

    public AntiprismSymmetry(PolygonField field) {
        super(field.polygonSides() * 2, field, "blue",
                field.isEven() ? null   // reflection through origin yields negative zones for even-gons
                : new AlgebraicMatrix(  // reflection in Z (red) will yield the negative zones for odd-gons
                        field.basisVector(3, AlgebraicVector.X), 
                        field.basisVector(3, AlgebraicVector.Y),
                        field.basisVector(3, AlgebraicVector.Z).negate())
                );
        final int nSides = field.polygonSides();
        double m10 = 0;
        double m11 = 1;
        useShear = field.isOdd();
        if (useShear) {
            m10 = field.getUnitDiagonal(field.diagonalCount() - 1).reciprocal().evaluate() / 2.0d;
            m11 = Math.cos(Math.PI / (2.0d * nSides));
        }

        // Shear transformation will only be applied in the Y direction. X and Z will be unchanged.
        shearTransform = new Matrix3d(
                1, m10, 0, 
                0, m11, 0, 
                0,   0, 1
            );
    }

    @Override
    public PolygonField getField() {
        // This cast to PolygonField is safe because we require a PolygonField in our c'tor
        return (PolygonField) super.getField();
    }

    /**
     * Called by the super constructor.
     */
    @Override
    protected void createInitialPermutations() {
        final int nSides = getField().polygonSides();
        mOrientations[0] = new Permutation(this, null);

        // first, define the N-fold rotation
        // for example, when nSides == 7 then map looks like this...
        // { 1, 2,  3,  4,  5,  6, 0,
        //   8, 9, 10, 11, 12, 13, 7 };
        int[] map1 = new int[nSides * 2];
        for (int i = 0; i < nSides; i++) {
            map1[i] = (i + 1) % nSides;
            map1[i + nSides] = map1[i] + nSides;
        }
        mOrientations[1] = new Permutation(this, map1);

        // then, then 2-fold rotation
        // be sure to use a new array, don't just reorder the one used by mOrientations[1] above
        // for example, when nSides == 7, and step == 1 then map2 looks like this...
        // { 7, 13, 12, 11, 10, 9, 8,
        //   0,  6,  5,  4,  3, 2, 1 };
        // map2 is just map1 in reverse order
        int[] map2 = new int[map1.length];
        int n = nSides * 2;
        for (int i = 0; i < map2.length; i++) {
            n--;
            map2[i] = map1[n];
        }
        mOrientations[nSides] = new Permutation(this, map2);
    }

//  // TODO: Change this method to logPermutations() using LOGGER and move it to the base class
//    public void printPermutations() {
//        for(int i = 0; i < mOrientations.length ; i++) {
//            Permutation p = mOrientations[i];
//            if(p == null) continue;
//            System.out.print(p + "\torder " + p.getOrder() + ":\t[");
//            int[] map = p.getJsonValue();
//            String delim = "";
//            for(int j = 0; j < map.length; j++) {
//                System.out.print(delim + map[j]);
//                if(j+1 == map.length/2) {
//                    System.out.print( "    ");
//                }
//                delim = ",";
//            }
//            System.out.println("]");
//        }
//    }

    @Override
    protected void createFrameOrbit(String frameColor) {
        // The following drawing uses the 7-gon as an example of the general case for an odd-gon.
        // S = 1/sigma, R = rho/sigma and 1 = sigma/sigma. 
        //
        //                                                  Y
        //                                (-S,1)        (0,1)
        //                       +---+------ [2] ------- [f] ---------+--------+---+
        //                      /(-R,R)      /           /        (S,R)       /   /
        //                     +- [g] ------+-----------+--------- [1] ------+---+
        //                    /   /        /           /           /        /   /
        //                   /   /        /           /           /        /   /
        //              (-1,S)  /        /           /           /     (R,S)  /
        //                [3] -+--------+-----------+-----------+------ [e] -+
        //                /   /        /           /           /        /   /
        //               /   /        /           /           /        /   /
        //              /   /        /           /           /        /   /
        //             /   /        /           /           /        /   /
        // -X (-1,0) [a] -+--------+---------- 0 ----------+--------+- [0] (1,0) X
        //           /   /        /           /           /        /   /
        //          /   /        /           /<--- S --->/        /   /
        //         /   /        /           /<--- R ------------>/   /
        //        /   /        /           /<--- 1 ---------------->/
        //       +- [4| ------+-----------+-----------+--------+- [d]
        //      /(-R,-S)     /           /           /        / (1,-S)
        //     /   /        /           /           /        /   /
        //    /   /        /           /           /        /   /
        //   +---+------ [b] ---------+-----------+------ [6] -+
        //  /   /     (-R,-R)        /           /      (R,-R)/
        // +---+--------+--------- [5] ------- [c] ------+---+
        //                       (0,-1)      (S,-1)
        //                       -Y
        //
        // The rotation will map v[0] to v[1]. v[0].x = 1 and v[0].y = 0, so this is the X unit vector.
        // The rotation will map v[f] to v[g]. v[f].x = 0 and v[f].y = 1, so this is the Y unit vector.

//        printPermutations();

        final PolygonField field = getField();
        final int nSides = field.polygonSides();
        final int nDiags = field.diagonalCount();
        
        // 1/nSides of a rotation around the Z axis with an eigenvector (basis vector) on the X axis
        final AlgebraicMatrix rotationMatrix = getRotationMatrix();

        // All mMatrices are mappings of the 3D standard basis unit vectors.
        // e.g. [X,Y,Z] = field .identityMatrix( 3 );
        AlgebraicVector vX = field.basisVector(3, AlgebraicVector.X);
        AlgebraicVector vY = field.basisVector(3, AlgebraicVector.Y);
        AlgebraicVector vZ = field.basisVector(3, AlgebraicVector.Z);

        for (int i = 0; i < nSides * 2; i++) {
            if(i == nSides) {
                // all 3 vectors are back to their initial values after having made the first full rotation.
                // vX is already correct.
                // Both vY and vZ need to be adjusted for the second time around.
                vY = vY.negate();
                if(field.isOdd()) {
                    vY = vY.setComponent(AlgebraicVector.X, field.getUnitDiagonal(nDiags - 1).reciprocal());
                }
                vZ = vZ.negate();
            }
            mMatrices[i] = new AlgebraicMatrix(vX, vY, vZ);
//            System.out.println(i + ":\t" + mMatrices[i]); // TODO: Log this using base class LOGGER
            vX = rotationMatrix.timesColumn(vX);
            vY = rotationMatrix.timesColumn(vY);
        }
    }

    private AlgebraicMatrix rotationMatrix = null;  
    public AlgebraicMatrix getRotationMatrix() {
        if(rotationMatrix == null) {
            final PolygonField field = getField();
            final int diagCount = field.diagonalCount();
            // getUnitDiagonal() returns zero when its argument is negative 
            final AlgebraicNumber p_x = field.getUnitDiagonal(diagCount - 3);
            final AlgebraicNumber q_y = field.getUnitDiagonal(diagCount - (field.isEven() ? 3 : 2));
            final AlgebraicNumber den = field.getUnitDiagonal(diagCount - 1);
            final AlgebraicNumber num = field.getUnitDiagonal(1);
    
            final AlgebraicVector p = field.origin(3)
                    .setComponent(AlgebraicVector.X, p_x.dividedBy(den))
                    .setComponent(AlgebraicVector.Y, num.dividedBy(den));
            final AlgebraicVector q = field.origin(3)
                    .setComponent(AlgebraicVector.X, num.dividedBy(den).negate())
                    .setComponent(AlgebraicVector.Y, q_y.dividedBy(den));
            final AlgebraicVector zAxis = field.basisVector(3, AlgebraicVector.Z);
    
            rotationMatrix = new AlgebraicMatrix(p, q, zAxis);
        }
        return rotationMatrix;
    }

    @Override
    protected void createOtherOrbits() {
        // Breaking the bad pattern of orbit initialization in the AbstractSymmetry constructor
    }

    public AntiprismSymmetry createStandardOrbits(String frameColor) {
        Direction redOrbit = createZoneOrbit("red", 0, 1, this.mField.basisVector(3, AlgebraicVector.Z), true);
        this.preferredAxis = redOrbit.getAxis(Symmetry.PLUS, 0);

        AlgebraicVector blueFrameVector = mField .basisVector( 3, AlgebraicVector.X );
        final int nSides = getField().polygonSides();
        Direction blueOrbit = createZoneOrbit(frameColor, 0, nSides, blueFrameVector, true);

        AlgebraicVector greenVector;
        if(getField().isOdd()) {
            AlgebraicVector blueRotatedVector = blueOrbit.getAxis(PLUS, (nSides+1)/2).normal();
            greenVector = blueFrameVector.minus(blueRotatedVector);
        } else {
            AlgebraicVector blueRotatedVector = blueOrbit.getAxis(PLUS, 1).normal();
            greenVector = blueFrameVector.plus(blueRotatedVector);
        }
        
        // green and yellow orbit directions for a Polygon4 roughly correspond to the same orbits in octahedral symmetry 
        createZoneOrbit( "green", 0, nSides, greenVector, false);

        AlgebraicVector yellowVector = greenVector.plus(redOrbit.getAxis(PLUS, 1).normal());
        createZoneOrbit( "yellow", 0, nSides, yellowVector, false);
        return this;
    }

    @Override
    public Axis getPreferredAxis() {
        return this.preferredAxis;
    }

    @Override
    public RealVector embedInR3(AlgebraicVector v) {
        RealVector rv = super.embedInR3(v);
        if (useShear) {
            Vector3d v3d = new Vector3d(rv.x, rv.y, rv.z);
            shearTransform.transform(v3d);
            return new RealVector(v3d.x, v3d.y, v3d.z);
        }
        return rv;
    }

    @Override
    public boolean isTrivial() {
        return !useShear; // signals the POV-Ray exporter to generate the tranform
    }

    @Override
    public String getName() {
        return ( "antiprism" + getField().polygonSides());
    }

    @Override
    public int[] subgroup(String name) {
        return null;
    }

    /**
     * These three vertices represent the corners of the canonical orbit triangle.
     * They must correspond to the three "special" orbits returned by getSpecialOrbit().
     * All other canonical direction prototype vectors
     * must intersect this plane at a unique point within the triangle.
     * 
     * OrbitDotLocator will use the three vectors to locate the dots in this order:
     * AlgebraicVector[] triangle = getOrbitTriangle();
     * triangle[0] .. // SpecialOrbit.BLUE   = orthoVertex
     * triangle[1] .. // SpecialOrbit.RED    = sideVertex
     * triangle[2] .. // SpecialOrbit.YELLOW = topVertex
     * 
     * These variable names and their position in the array
     * correspond to the positions where they will be shown in the orbit triangle
     * rather than any specific colors.
     * The SpecialOrbit names originally matched the color position in the icosa orbit triangle
     * but other symmetries don't necessarily have any such corellation. 
     * 
     *  top
     *   @
     *   | `\ 
     *   |    `\
     *   @-------`@
     * ortho     side
     * 
     * AntiprismTrackball also uses these 3 vertices to locate the trackball orbit triangle hints.
     */
    @Override
    public AlgebraicVector[] getOrbitTriangle()
    {
        final PolygonField field = this.getField();
        final int diagCount = field.diagonalCount();
        
        AlgebraicVector sideVertex = field .basisVector( 3, AlgebraicVector.Z );
        Direction blueOrbit = getSpecialOrbit(SpecialOrbit.BLUE);
        AlgebraicVector topVertex = blueOrbit.getAxis(PLUS, diagCount  ).normal(); // last orbit before the -X axis
        AlgebraicVector bottomVert = blueOrbit.getAxis(PLUS, diagCount+1).normal();// next orbit is on or past the -X axis
        AlgebraicVector orthoVertex = AlgebraicVectors.getCentroid(new AlgebraicVector[] {topVertex, bottomVert});

        return new AlgebraicVector[] { orthoVertex, sideVertex, topVertex };
    }
    
    @Override
    public Direction getSpecialOrbit(SpecialOrbit which) {
        switch (which) {

        case BLUE:
            return this.getDirection("blue");
            
        case RED:
            return this.getDirection("red");

        case YELLOW:
            return this.getDirection(this.getField().isEven() ? "green" : "blue"); 

        default:
            return null;
        }
    }

}
