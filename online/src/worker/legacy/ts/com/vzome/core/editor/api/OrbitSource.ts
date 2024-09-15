/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor.api {
    export interface OrbitSource {
        getSymmetry(): com.vzome.core.math.symmetry.Symmetry;

        getAxis(vector: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.math.symmetry.Axis;

        getColor(orbit: com.vzome.core.math.symmetry.Direction): com.vzome.core.construction.Color;

        getVectorColor(vector: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.construction.Color;

        getOrbits(): com.vzome.core.math.symmetry.OrbitSet;

        getShapes(): com.vzome.core.editor.api.Shapes;

        getName(): string;

        getZone(orbit: string, orientation: number): com.vzome.core.math.symmetry.Axis;

        getEmbedding(): number[];

        getOrientations(rowMajor?: any): number[][];
    }
}

