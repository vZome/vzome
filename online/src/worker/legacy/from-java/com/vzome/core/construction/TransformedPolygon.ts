import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Polygon } from "./Polygon.js";
import { Transformation } from "./Transformation.js";

/**
 * @author Scott Vorthmann
 * @param {Transformation} transform
 * @param {Polygon} prototype
 * @class
 * @extends Polygon
 */
export class TransformedPolygon extends Polygon {
    /*private*/ mTransform: Transformation;

    /*private*/ mPrototype: Polygon;

    public constructor(transform: Transformation, prototype: Polygon) {
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
        const vertices: AlgebraicVector[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(this.mPrototype.getVertexCount());
        for(let i: number = 0; i < vertices.length; i++) {{
            vertices[i] = this.mTransform.transform$com_vzome_core_algebra_AlgebraicVector(this.mPrototype.getVertex(i));
        };}
        return this.setStateVariable(vertices, false);
    }
}
TransformedPolygon["__class"] = "com.vzome.core.construction.TransformedPolygon";
