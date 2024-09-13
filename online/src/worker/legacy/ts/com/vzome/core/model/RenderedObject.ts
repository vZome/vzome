/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.model {
    export interface RenderedObject {
        getShape(): com.vzome.core.math.Polyhedron;

        getOrientation(): com.vzome.core.algebra.AlgebraicMatrix;

        getLocationAV(): com.vzome.core.algebra.AlgebraicVector;

        getSymmetryShapes(): string;

        resetAttributes(oneSidedPanels: boolean, colorPanels: boolean);
    }
}

