package com.vzome.core.math;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.XmlSaveFormat;

/**
 * @author David Hall
 */
public class TetrahedralProjection implements Projection
{

    private final AlgebraicField field;
    private final AlgebraicVector[] basis;

    public TetrahedralProjection(AlgebraicField field) {
        this.field = field;

        AlgebraicNumber pos = field.one();
        AlgebraicNumber neg = pos.negate();

        basis = new AlgebraicVector[4];
        basis[0] = new AlgebraicVector(pos, pos, pos);  // w
        basis[1] = new AlgebraicVector(pos, neg, neg);  // x
        basis[2] = new AlgebraicVector(neg, pos, neg);  // y
        basis[3] = new AlgebraicVector(neg, neg, pos);  // z
    }

    @Override
    public AlgebraicVector projectImage(AlgebraicVector source, boolean wFirst) {
        AlgebraicVector result = field.origin(basis[0].dimension());
        // TODO: Let VefToModel support more than 4 dimensions then use other directions for each dimension
        // e.g. 6 reds = RhombicTriaconProjection
        // or 10 yellows = EneaconProjection
        // or 15 blues = Icosidodecahedron
        // or 6 red +10 yellow +15 blue = 31 zone zomeball
        // or 60 teals = pentakis trackball?
        // or any set of custom strut directions
        int pos = wFirst ? 0 : basis.length - 1;
        for (AlgebraicVector unitVector : basis) {
            AlgebraicNumber scalar = source.getComponent(pos);
            result = result.plus(unitVector.scale(scalar));
            pos = (pos+1) % basis.length;
        }
        return result;
    }

    @Override
    public void getXmlAttributes( Element element ) {}

    @Override
    public void setXmlAttributes( Element xml, XmlSaveFormat format ) {}

    @Override
    public String getProjectionName() {
        return "Tetrahedral";
    }

}
