/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @author Scott Vorthmann
     * @param {com.vzome.core.construction.Transformation} transform
     * @param {com.vzome.core.construction.Polygon} prototype
     * @class
     * @extends com.vzome.core.construction.Polygon
     */
    export class TransformedPolygon extends com.vzome.core.construction.Polygon {
        /*private*/ mTransform: com.vzome.core.construction.Transformation;

        /*private*/ mPrototype: com.vzome.core.construction.Polygon;

        public constructor(transform: com.vzome.core.construction.Transformation, prototype: com.vzome.core.construction.Polygon) {
            super(prototype.field);
            if (this.mTransform === undefined) { this.mTransform = null; }
            if (this.mPrototype === undefined) { this.mPrototype = null; }
            this.mTransform = transform;
            this.mPrototype = prototype;
            this.mapParamsToState();
        }

        /**
         * 
         * @return {boolean}
         */
        mapParamsToState(): boolean {
            const vertices: com.vzome.core.algebra.AlgebraicVector[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(this.mPrototype.getVertexCount());
            for(let i: number = 0; i < vertices.length; i++) {{
                vertices[i] = this.mTransform.transform$com_vzome_core_algebra_AlgebraicVector(this.mPrototype.getVertex(i));
            };}
            return this.setStateVariable(vertices, false);
        }
    }
    TransformedPolygon["__class"] = "com.vzome.core.construction.TransformedPolygon";

}

