import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Quaternion } from "../algebra/Quaternion.js";
import { Polygon } from "./Polygon.js";

/**
 * @author Scott Vorthmann
 * @param {Quaternion} leftQuaternion
 * @param {Quaternion} rightQuaternion
 * @param {Polygon} prototype
 * @class
 * @extends Polygon
 */
export class PolygonRotated4D extends Polygon {
    /*private*/ mLeftQuaternion: Quaternion;

    /*private*/ mRightQuaternion: Quaternion;

    /*private*/ mPrototype: Polygon;

    public constructor(leftQuaternion: Quaternion, rightQuaternion: Quaternion, prototype: Polygon) {
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
        const vertices: AlgebraicVector[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(this.mPrototype.getVertexCount());
        for(let i: number = 0; i < vertices.length; i++) {{
            let loc: AlgebraicVector = this.mRightQuaternion.leftMultiply(this.mPrototype.getVertex(i));
            loc = this.mLeftQuaternion.rightMultiply(loc);
            vertices[i] = loc;
        };}
        return this.setStateVariable(vertices, false);
    }
}
PolygonRotated4D["__class"] = "com.vzome.core.construction.PolygonRotated4D";
