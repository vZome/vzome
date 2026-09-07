import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Point } from "./Point.js";
import { Polygon } from "./Polygon.js";

export class PolygonFromVertices extends Polygon {
    /*private*/ __com_vzome_core_construction_PolygonFromVertices_mVertices: Point[];

    public constructor(points?: any) {
        if (((points != null && (points.constructor != null && points.constructor["__interfaces"] != null && points.constructor["__interfaces"].indexOf("java.util.List") >= 0)) || points === null)) {
            let __args = arguments;
            {
                let __args = arguments;
                let vertices: any = points.toArray((s => { let a=[]; while(s-->0) a.push(null); return a; })(points.size()));
                super(vertices.length === 0 ? null : vertices[0].field);
                if (this.__com_vzome_core_construction_PolygonFromVertices_mVertices === undefined) { this.__com_vzome_core_construction_PolygonFromVertices_mVertices = null; } 
                this.__com_vzome_core_construction_PolygonFromVertices_mVertices = vertices;
                this.mapParamsToState();
            }
        } else if (((points != null && points instanceof <any>Array && (points.length == 0 || points[0] == null ||(points[0] != null && points[0] instanceof <any>Point))) || points === null)) {
            let __args = arguments;
            let vertices: any = __args[0];
            super(vertices.length === 0 ? null : vertices[0].field);
            if (this.__com_vzome_core_construction_PolygonFromVertices_mVertices === undefined) { this.__com_vzome_core_construction_PolygonFromVertices_mVertices = null; } 
            this.__com_vzome_core_construction_PolygonFromVertices_mVertices = vertices;
            this.mapParamsToState();
        } else throw new Error('invalid overload');
    }

    /**
     * 
     * @return {boolean}
     */
    mapParamsToState(): boolean {
        const locs: AlgebraicVector[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(this.__com_vzome_core_construction_PolygonFromVertices_mVertices.length);
        for(let i: number = 0; i < this.__com_vzome_core_construction_PolygonFromVertices_mVertices.length; i++) {locs[i] = this.__com_vzome_core_construction_PolygonFromVertices_mVertices[i].getLocation();}
        return this.setStateVariable(locs, false);
    }
}
PolygonFromVertices["__class"] = "com.vzome.core.construction.PolygonFromVertices";
