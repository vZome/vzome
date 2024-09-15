/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math.symmetry {
    export interface Symmetries4D {
        constructPolytope(groupName: string, index: number, edgesToRender: number, edgeScales: com.vzome.core.algebra.AlgebraicNumber[], listener: com.vzome.core.math.symmetry.WythoffConstruction.Listener);

        getQuaternionSymmetry(name: string): com.vzome.core.math.symmetry.QuaternionicSymmetry;
    }
}

