/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @author Scott Vorthmann
     * @param {com.vzome.core.construction.Segment} seg
     * @class
     * @extends com.vzome.core.construction.Line
     */
    export class LineExtensionOfSegment extends com.vzome.core.construction.Line {
        /*private*/ mSegment: com.vzome.core.construction.Segment;

        public constructor(seg: com.vzome.core.construction.Segment) {
            super(seg.field);
            if (this.mSegment === undefined) { this.mSegment = null; }
            this.mSegment = seg;
            this.mapParamsToState();
        }

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            if (this.mSegment.isImpossible())return this.setStateVariables(null, null, true);
            return this.setStateVariables(this.mSegment.getStart(), this.mSegment.getOffset(), false);
        }

        /**
         * 
         * @return {com.vzome.core.algebra.Bivector3dHomogeneous}
         */
        public getHomogeneous(): com.vzome.core.algebra.Bivector3dHomogeneous {
            const v1: com.vzome.core.algebra.Vector3dHomogeneous = new com.vzome.core.algebra.Vector3dHomogeneous(this.mSegment.getStart(), this.getField());
            const v2: com.vzome.core.algebra.Vector3dHomogeneous = new com.vzome.core.algebra.Vector3dHomogeneous(this.mSegment.getEnd(), this.getField());
            return v1.outer(v2);
        }
    }
    LineExtensionOfSegment["__class"] = "com.vzome.core.construction.LineExtensionOfSegment";

}

