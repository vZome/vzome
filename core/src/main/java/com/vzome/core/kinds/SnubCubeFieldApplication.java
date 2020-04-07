package com.vzome.core.kinds;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.SnubCubeField;
import com.vzome.core.math.symmetry.AbstractSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.viewing.AbstractShapes;
import com.vzome.core.viewing.ExportedVEFShapes;

public class SnubCubeFieldApplication extends DefaultFieldApplication {

    public SnubCubeFieldApplication() {
        super(new SnubCubeField());

        OctahedralSymmetryPerspective symmPerspective = (OctahedralSymmetryPerspective) super.getDefaultSymmetryPerspective();
        symmPerspective.setModelResourcePath("org/vorthmann/zome/app/snubCubeTrackball-vef.vZome");
        AbstractSymmetry symm = symmPerspective.getSymmetry();
        AlgebraicField field = this.getField();
        
        // According to the comment at the top of com.vzome.core.math.symmetry.Direction,
        // the X component of these vectors should equal one and the others should be between 0 and 1.
        // These prototypes all meet that criteria so we don't need withCorrection() after each call to createZoneOrbit().
        // These prototypes have the additional characteristic that x > y > z, 
        // just because I think that's "more canonical"... DJH
        AlgebraicVector vSnubSquare          = field.createVector(new int[][]{ { 1,1, 0,1, 0,1 }, { 0,1, 2,1,-1,1 }, { 0,1, 0,1, 0,1 } } );
        AlgebraicVector vSnubTriangle        = field.createVector(new int[][]{ { 1,1, 0,1, 0,1 }, { 1,2, 1,1,-1,2 }, { 1,2,-1,1, 1,2 } } );
        AlgebraicVector vSnubDiagonal        = field.createVector(new int[][]{ { 1,1, 0,1, 0,1 }, { 0,1,-1,2, 1,2 }, { 0,1,-1,2, 1,2 } } );
        AlgebraicVector vSnubFaceNormal      = field.createVector(new int[][]{ { 1,1, 0,1, 0,1 }, {-2,7, 2,7, 1,7 }, {-1,7,-6,7, 4,7 } } );
        AlgebraicVector vSnubVertex          = field.createVector(new int[][]{ { 1,1, 0,1, 0,1 }, {-1,1,-1,1, 1,1 }, { 0,1, 2,1,-1,1 } } );
        AlgebraicVector vSnubSquareMidEdge   = field.createVector(new int[][]{ { 1,1, 0,1, 0,1 }, {-1,2, 1,2, 0,1 }, {-1,2,-3,2, 1,1 } } );
        AlgebraicVector vSnubTriangleMidEdge = field.createVector(new int[][]{ { 1,1, 0,1, 0,1 }, {-1,1, 1,1, 0,1 }, {-1,1,-1,1, 1,1 } } );

        // these scalars will adjust the vector lengths to the desired lengths relative to the blue prototype
        AlgebraicNumber nSnubSquare          = field.createAlgebraicNumber(new int[] {-1, 1, 0 } );
        AlgebraicNumber nSnubTriangle        = field.createAlgebraicNumber(new int[] { 1,-2, 1 } );
        AlgebraicNumber nSnubDiagonal        = field.createAlgebraicNumber(new int[] { 0, 4,-2 } );
        AlgebraicNumber nSnubFaceNormal      = field.createAlgebraicNumber(new int[] { 2, 3,-2 } );
        AlgebraicNumber nSnubVertex          = field.one();
        AlgebraicNumber nSnubSquareMidEdge   = field.one();
        AlgebraicNumber nSnubTriangleMidEdge = field.createAlgebraicNumber(new int[] { 0,-1, 1 } ).dividedBy(field.createRational(2));

        // actual colors are assigned in /core/src/main/resources/com/vzome/core/editor/defaultPrefs.properties
        // I'm reusing the same labels (colors) as snubDodec where applicable
        // but I have to be careful not to load the snubDodec versions of ExportedVEFShapes below
        symm.createZoneOrbit( "snubSquare",     0, Symmetry.NO_ROTATION, vSnubSquare,         false, false, nSnubSquare);
        symm.createZoneOrbit( "snubTriangle",   0, Symmetry.NO_ROTATION, vSnubTriangle,       false, false, nSnubTriangle);
        symm.createZoneOrbit( "snubDiagonal",   0, Symmetry.NO_ROTATION, vSnubDiagonal,       false, false, nSnubDiagonal);
        
        // snubFaceNormal is a standard direction since it's the easiest one to use for making the dual of a SnubCube
        symm.createZoneOrbit("snubFaceNormal",  0, Symmetry.NO_ROTATION, vSnubFaceNormal,      true,  false, nSnubFaceNormal);
        // snubVertex is a standard direction since it's the easiest one to use for making a SnubCube
        symm.createZoneOrbit("snubVertex",      0, Symmetry.NO_ROTATION, vSnubVertex,          true,  false, nSnubVertex);

        symm.createZoneOrbit("snubSquareMid",   0, Symmetry.NO_ROTATION, vSnubSquareMidEdge,   false, false, nSnubSquareMidEdge);
        symm.createZoneOrbit("snubTriangleMid", 0, Symmetry.NO_ROTATION, vSnubTriangleMidEdge, false, false, nSnubTriangleMidEdge);
        
        // Add custom default shapes instead of just using the default OctahedralShapes
        final AbstractShapes defaultShapes = (AbstractShapes) symmPerspective.getDefaultGeometry();
        // Be careful not to load the snubDodec versions of the ExportedVEFShapes for zones with shared names
        String resPath = "snubCube/";
        final AbstractShapes snubCubeRhShapes = new ExportedVEFShapes(null, resPath, "snub cube right", null, symm, defaultShapes, false);
        final AbstractShapes snubCubeLhShapes = new ExportedVEFShapes(null, resPath,  "snub cube left",  null, symm, defaultShapes, true);
        resPath = "snubCube/dual/"; // pentagonal icositetrahedron is the dual of the snub cube
        final AbstractShapes snubDualRhShapes = new ExportedVEFShapes(null, resPath, "snub cube dual right", null, symm, defaultShapes, false);
        final AbstractShapes snubDualLhShapes = new ExportedVEFShapes(null, resPath,  "snub cube dual left",  null, symm, defaultShapes, true);
        resPath = "snubCube/disdyakisDodec/";
        final AbstractShapes disdyakisDodec = new ExportedVEFShapes(null, resPath, "disdyakis dodec", null, symm, defaultShapes, false);
        
        // this is the order they will be shown on the dialog
        // octahedral is already the default. No need to change it
        symmPerspective.addShapes(disdyakisDodec); 
        symmPerspective.addShapes(snubCubeRhShapes); 
        symmPerspective.addShapes(snubCubeRhShapes); 
        symmPerspective.addShapes(snubCubeLhShapes); 
        symmPerspective.addShapes(snubDualRhShapes); 
        symmPerspective.addShapes(snubDualLhShapes); 
    }

    // This is just to help get the zone orbit vectors compliant with the comment in symmetry.Direction.
//    private void printUnitVector(AlgebraicVector v) {
//        AlgebraicNumber recip = v.getComponent(0);
//        recip = recip.isZero() ? v.getComponent(0) : recip.reciprocal();
//        AlgebraicVector  vs = v.scale(recip);
//        System.out.println(v + "\t*\t" + recip + "\t=\t" + vs);
//        for(int i = 0; i < 3; i++) {
//            System.out.print(v.getComponent(i).evaluate() + ", ");
//        }
//        System.out.print("\t*\t" + recip.evaluate() + "\t=\t");
//        for(int i = 0; i < 3; i++) {
//            System.out.print("\t" + vs.getComponent(i).evaluate() + ", ");
//        }
//        System.out.println("\n");
//    }
}
