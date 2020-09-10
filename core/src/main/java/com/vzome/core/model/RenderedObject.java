package com.vzome.core.model;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.editor.api.OrbitSource;
import com.vzome.core.editor.api.Shapes;
import com.vzome.core.math.Polyhedron;

public interface RenderedObject
{
    Polyhedron getShape();

    AlgebraicMatrix getOrientation();

    AlgebraicVector getLocationAV();

    String getSymmetryShapes();

    void resetAttributes( OrbitSource orbitSource, Shapes shapes, boolean oneSidedPanels, boolean colorPanels );
}
