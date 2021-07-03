package com.vzome.core.viewing;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.PolygonField;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.symmetry.AntiprismSymmetry;

/**
 * @author David Hall
 */
public class AntiprismShapes extends AbstractShapes {

    public AntiprismShapes(String pkgName, String name, AntiprismSymmetry symm) {
        super(pkgName, name, null, symm);
    }

    @Override
    public AntiprismSymmetry getSymmetry() {
        // This cast to AntiprismSymmetry is safe because we require an
        // AntiprismSymmetry in our c'tor
        return (AntiprismSymmetry) super.getSymmetry();
    }

    @Override
    protected Polyhedron buildConnectorShape(String pkgName) {
        final AntiprismSymmetry symm = getSymmetry();
        final PolygonField field = symm.getField();
        final int nSides = field.polygonSides();
        final Polyhedron antiprism = new Polyhedron(field);
        final AlgebraicNumber topX = field.one();
        final AlgebraicNumber topY = field.zero();
        final AlgebraicNumber maxTerm = field.getUnitDiagonal(field.diagonalCount() - 1);
        final AlgebraicNumber botX = field.getUnitDiagonal(field.diagonalCount() - 2).dividedBy(maxTerm);
        final AlgebraicNumber botY = maxTerm.reciprocal();
        final AlgebraicNumber halfHeight = (pkgName == "thin") ? field.getUnitDiagonal(field.diagonalCount() - 1).reciprocal() : field.one();
        final AlgebraicMatrix rotationMatrix = symm.getRotationMatrix();

        // Add vertices
        // Top and bottom vertices will alternate in the list
        AlgebraicVector vTop = new AlgebraicVector(topX, topY, halfHeight);
        AlgebraicVector vBot = new AlgebraicVector(botX, botY, halfHeight.negate());
        for (int i = 0; i < nSides; i++) {
            antiprism.addVertex(vTop);
            antiprism.addVertex(vBot);
            vTop = rotationMatrix.timesColumn(vTop);
            vBot = rotationMatrix.timesColumn(vBot);
        }

        Polyhedron.Face topFace = antiprism.newFace();
        Polyhedron.Face botFace = antiprism.newFace();
        // top rotation is counter-clockwise
        for (int i = 0; i < nSides * 2; i += 2) {
            topFace.add(i);
        }
        // bottom rotation is opposite of top
        for (int i = nSides * 2 - 1; i >= 0; i -= 2) {
            botFace.add(i);
        }
        antiprism.addFace(topFace);
        antiprism.addFace(botFace);

        // add all of the triangular faces around the perimeter
        int nVertices = nSides * 2;
        // top first
        for (int i = 0; i < nSides * 2; i += 2) {
            Polyhedron.Face face = antiprism.newFace();
            face.add(i);
            face.add((i + 1) % nVertices);
            face.add((i + 2) % nVertices);
            antiprism.addFace(face);
        }
        // then the bottom with the points swapped so they face outward
        for (int i = 1; i < nSides * 2; i += 2) {
            Polyhedron.Face face = antiprism.newFace();
            face.add(i);
            face.add((i + 2) % nVertices);
            face.add((i + 1) % nVertices);
            antiprism.addFace(face);
        }

        return antiprism;
    }
}
