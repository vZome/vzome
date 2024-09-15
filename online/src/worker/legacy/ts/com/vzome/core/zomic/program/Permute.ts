/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.zomic.program {
    /**
     * @author vorth
     * @param {com.vzome.core.math.symmetry.Axis} axis
     * @class
     * @extends com.vzome.core.zomic.program.ZomicStatement
     */
    export abstract class Permute extends com.vzome.core.zomic.program.ZomicStatement {
        /*private*/ m_axis: com.vzome.core.math.symmetry.Axis;

        public constructor(axis: com.vzome.core.math.symmetry.Axis) {
            super();
            if (this.m_axis === undefined) { this.m_axis = null; }
            this.m_axis = axis;
        }

        public setAxis(axis: com.vzome.core.math.symmetry.Axis) {
            this.m_axis = axis;
        }

        public getOrder(): number {
            if (this.m_axis == null)return 2;
            return this.m_axis.getRotationPermutation().getOrder();
        }

        public getAxis(): com.vzome.core.math.symmetry.Axis {
            return this.m_axis;
        }
    }
    Permute["__class"] = "com.vzome.core.zomic.program.Permute";

}

