package com.vzome.core.edits;

import java.util.HashSet;
import java.util.Set;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;

public class SelectCoplanar extends SelectByBoundary {

    public SelectCoplanar(EditorModel editor) {
        super(editor);
    }

    @Override
    public String usage() {
        return "Select any combination of connectors, struts or panels to specify\n"
                + "3 or more points that are all coplanar, but not simply collinear.\n\n"
                + "All parts that are completely on the corresponding plane will be selected.\n";
    }

    // Some manifestations may share common vectors (e.g. a ball at the end of a strut).
    // Collect them all in a set to remove duplicates.
    protected Set<AlgebraicVector> vectors = new HashSet<>();
    protected AlgebraicVector pointOnPlane = null;
    protected AlgebraicVector normal = null;

    @Override
    protected String setBoundary() {
        for (Manifestation man : mSelection) {
            if (man instanceof Connector) {
                vectors.add(man.getLocation());
            } else if (man instanceof Strut) {
                vectors.add(man.getLocation());
                vectors.add(((Strut) man).getEnd());
            } else if (man instanceof Panel) {
                for (AlgebraicVector v : (Panel) man) {
                    vectors.add(v);
                }
            } else {
                // future-proof... shouldn't ever happen
                throw new IllegalStateException("Unknown manifestation: " + man.getClass().getSimpleName());
            }
        }
        if (vectors.size() < 3) {
            return "Additional connectors, struts or panels must be selected to define a plane.";
        }
        if (AlgebraicVectors.areCollinear(vectors)) {
            return "Selected items are collinear. Select another non-collinear ball to specify the plane.";
        }
        if (!AlgebraicVectors.areCoplanar(vectors)) {
            return "Selected items are not coplanar.";
        }
        // All validated. Now just save the values to be used later in boundaryContains()
        for (AlgebraicVector v : vectors) {
            pointOnPlane = v;
            break; // We can use any one of the vectors in the set
        }
        normal = AlgebraicVectors.getNormal(vectors);
        return null;
    }

    @Override
    protected boolean boundaryContains(AlgebraicVector v) {
        return vectors.contains(v) || pointOnPlane.minus(v).dot(normal).isZero();
    }

    public static final String NAME = "SelectCoplanar";

    @Override
    protected String getXmlElementName() {
        return NAME;
    }
}
