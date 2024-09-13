/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor.api {
    export interface Shapes {
        getName(): string;

        getAlias(): string;

        getConnectorShape(): com.vzome.core.math.Polyhedron;

        getStrutShape(dir: com.vzome.core.math.symmetry.Direction, length: com.vzome.core.algebra.AlgebraicNumber): com.vzome.core.math.Polyhedron;

        getPanelShape(vertexCount: number, quadrea: com.vzome.core.algebra.AlgebraicNumber, zone: com.vzome.core.math.symmetry.Axis, vertices: java.lang.Iterable<com.vzome.core.algebra.AlgebraicVector>, oneSidedPanels: boolean): com.vzome.core.math.Polyhedron;

        getSymmetry(): com.vzome.core.math.symmetry.Symmetry;

        getPackage(): string;

        getColor(dir: com.vzome.core.math.symmetry.Direction): com.vzome.core.construction.Color;

        hasColors(): boolean;
    }
}

