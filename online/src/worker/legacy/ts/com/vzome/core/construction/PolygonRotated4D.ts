/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @author Scott Vorthmann
     * @param {com.vzome.core.algebra.Quaternion} leftQuaternion
     * @param {com.vzome.core.algebra.Quaternion} rightQuaternion
     * @param {com.vzome.core.construction.Polygon} prototype
     * @class
     * @extends com.vzome.core.construction.Polygon
     */
    export class PolygonRotated4D extends com.vzome.core.construction.Polygon {
        /*private*/ mLeftQuaternion: com.vzome.core.algebra.Quaternion;

        /*private*/ mRightQuaternion: com.vzome.core.algebra.Quaternion;

        /*private*/ mPrototype: com.vzome.core.construction.Polygon;

        public constructor(leftQuaternion: com.vzome.core.algebra.Quaternion, rightQuaternion: com.vzome.core.algebra.Quaternion, prototype: com.vzome.core.construction.Polygon) {
            super(prototype.field);
            if (this.mLeftQuaternion === undefined) { this.mLeftQuaternion = null; }
            if (this.mRightQuaternion === undefined) { this.mRightQuaternion = null; }
            if (this.mPrototype === undefined) { this.mPrototype = null; }
            this.mLeftQuaternion = leftQuaternion;
            this.mRightQuaternion = rightQuaternion;
            this.mPrototype = prototype;
            this.mapParamsToState();
        }

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            if (this.mPrototype.isImpossible())return this.setStateVariable(null, true);
            const vertices: com.vzome.core.algebra.AlgebraicVector[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(this.mPrototype.getVertexCount());
            for(let i: number = 0; i < vertices.length; i++) {{
                let loc: com.vzome.core.algebra.AlgebraicVector = this.mRightQuaternion.leftMultiply(this.mPrototype.getVertex(i));
                loc = this.mLeftQuaternion.rightMultiply(loc);
                vertices[i] = loc;
            };}
            return this.setStateVariable(vertices, false);
        }
    }
    PolygonRotated4D["__class"] = "com.vzome.core.construction.PolygonRotated4D";

}

