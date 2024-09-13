/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @author Scott Vorthmann
     * @param {com.vzome.core.construction.Segment} seg
     * @param {boolean} start
     * @class
     * @extends com.vzome.core.construction.Point
     */
    export class SegmentEndPoint extends com.vzome.core.construction.Point {
        /*private*/ mSegment: com.vzome.core.construction.Segment;

        /*private*/ start: boolean;

        public constructor(seg?: any, start?: any) {
            if (((seg != null && seg instanceof <any>com.vzome.core.construction.Segment) || seg === null) && ((typeof start === 'boolean') || start === null)) {
                let __args = arguments;
                super(seg.field);
                if (this.mSegment === undefined) { this.mSegment = null; } 
                this.start = false;
                this.mSegment = seg;
                this.start = start;
                this.mapParamsToState();
            } else if (((seg != null && seg instanceof <any>com.vzome.core.construction.Segment) || seg === null) && start === undefined) {
                let __args = arguments;
                {
                    let __args = arguments;
                    let start: any = false;
                    super(seg.field);
                    if (this.mSegment === undefined) { this.mSegment = null; } 
                    this.start = false;
                    this.mSegment = seg;
                    this.start = start;
                    this.mapParamsToState();
                }
            } else throw new Error('invalid overload');
        }

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            if (this.mSegment.isImpossible())return this.setStateVariable(null, true);
            const loc: com.vzome.core.algebra.AlgebraicVector = this.start ? this.mSegment.getStart() : this.mSegment.getEnd();
            return this.setStateVariable(loc, false);
        }
    }
    SegmentEndPoint["__class"] = "com.vzome.core.construction.SegmentEndPoint";

}

