import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicMatrix } from "../algebra/AlgebraicMatrix.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Color as CoreColor } from "../construction/Color.js";
import { Java2dSnapshot } from "./Java2dSnapshot.js";
import { Polyhedron } from "../math/Polyhedron.js";
import { RealMatrix4 } from "../math/RealMatrix4.js";
import { RealVector } from "../math/RealVector.js";
import { Manifestation } from "../model/Manifestation.js";
import { Strut } from "../model/Strut.js";
import { RenderedModel } from "../render/RenderedModel.js";
import { Lights } from "../viewing/Lights.js";
import { Color as JavaColor } from "../../../../java/awt/Color.js";
import { Rectangle2D } from "../../../../java/awt/geom/Rectangle2D.js";

/**
 * Builds a Java2dSnapshot, for use in rendering to a Snapshot2dPanel
 * or exporting via a SnapshotExporter.
 * @author vorth
 * @class
 */
export class Java2dExporter {
    /*private*/ viewTransform: RealMatrix4;

    /*private*/ eyeTrans: RealMatrix4;

    public render2d(model: RenderedModel, viewTransform: RealMatrix4, eyeTransform: RealMatrix4, lights: Lights, height: number, width: number, drawLines: boolean, doLighting: boolean): Java2dSnapshot {
        this.viewTransform = viewTransform;
        this.eyeTrans = eyeTransform;
        const lightDirs: RealVector[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(lights.size());
        const lightColors: JavaColor[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(lights.size());
        let ambientLight: JavaColor;
        let background: JavaColor;
        const snapshot: Java2dSnapshot = new Java2dSnapshot();
        for(let i: number = 0; i < lightDirs.length; i++) {{
            lightDirs[i] = lights.getDirectionalLightVector(i).normalize().negate();
            lightColors[i] = new JavaColor(lights.getDirectionalLightColor(i).getRGB());
        };}
        ambientLight = new JavaColor(lights.getAmbientColor().getRGB());
        background = new JavaColor(lights.getBackgroundColor().getRGB());
        snapshot.setStrokeWidth(0.5);
        snapshot.setRect(new Rectangle2D.Float(0.0, 0.0, width, height));
        snapshot.setBackgroundColor(background);
        const mappedVertices: java.util.List<RealVector> = <any>(new java.util.ArrayList<any>(60));
        for(let index=model.iterator();index.hasNext();) {
            let rm = index.next();
            {
                const shape: Polyhedron = rm.getShape();
                const c: CoreColor = rm.getColor();
                const color: JavaColor = (c == null) ? JavaColor.WHITE_$LI$() : new JavaColor(c.getRGB());
                if (drawLines){
                    const m: Manifestation = rm.getManifestation();
                    if (m != null && (m.constructor != null && m.constructor["__interfaces"] != null && m.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                        const start: AlgebraicVector = (<Strut><any>m).getLocation();
                        const end: AlgebraicVector = (<Strut><any>m).getEnd();
                        const v0: RealVector = this.mapCoordinates(model.renderVector(start), height, width);
                        const v1: RealVector = this.mapCoordinates(model.renderVector(end), height, width);
                        snapshot.addLineSegment(color, v0, v1);
                    }
                    continue;
                }
                const vertices: java.util.List<AlgebraicVector> = shape.getVertexList();
                const partOrientation: AlgebraicMatrix = rm.getOrientation();
                const location: RealVector = rm.getLocation();
                if (location == null)continue;
                mappedVertices.clear();
                for(let i: number = 0; i < vertices.size(); i++) {{
                    let gv: AlgebraicVector = vertices.get(i);
                    gv = partOrientation.timesColumn(gv);
                    const rv: RealVector = location.plus(model.renderVector(gv));
                    const v: RealVector = this.mapCoordinates(rv, height, width);
                    mappedVertices.add(v);
                };}
                for(let index=shape.getFaceSet().iterator();index.hasNext();) {
                    let face = index.next();
                    {
                        const arity: number = face.size();
                        const path: Java2dSnapshot.Polygon = new Java2dSnapshot.Polygon(color);
                        let backFacing: boolean = false;
                        let v1: RealVector = null;
                        let v2: RealVector = null;
                        for(let j: number = 0; j < arity; j++) {{
                            const index: number = face.get(j);
                            const v: RealVector = mappedVertices.get(index);
                            path.addVertex(v);
                            switch((path.size())) {
                            case 1:
                                v1 = <RealVector>/* clone */((o: any) => { if (o.clone != undefined) { return (<any>o).clone(); } else { let clone = Object.create(o); for(let p in o) { if (o.hasOwnProperty(p)) clone[p] = o[p]; } return clone; } })(v);
                                break;
                            case 2:
                                v2 = <RealVector>/* clone */((o: any) => { if (o.clone != undefined) { return (<any>o).clone(); } else { let clone = Object.create(o); for(let p in o) { if (o.hasOwnProperty(p)) clone[p] = o[p]; } return clone; } })(v);
                                break;
                            case 3:
                                let v3: RealVector = <RealVector>/* clone */((o: any) => { if (o.clone != undefined) { return (<any>o).clone(); } else { let clone = Object.create(o); for(let p in o) { if (o.hasOwnProperty(p)) clone[p] = o[p]; } return clone; } })(v);
                                v3 = v3.minus(v2);
                                v2 = v2.minus(v1);
                                const normal: RealVector = v2.cross(v3);
                                backFacing = normal.z > 0;
                                break;
                            default:
                                break;
                            }
                        };}
                        path.close();
                        if (!backFacing){
                            if (doLighting){
                                const faceNormal: AlgebraicVector = partOrientation.timesColumn(face.getNormal(vertices));
                                const normal: RealVector = model.renderVector(faceNormal).normalize();
                                let normalV: RealVector = new RealVector(normal.x, normal.y, normal.z);
                                normalV = this.viewTransform.transform3dVec(normalV);
                                path.applyLighting(normalV, lightDirs, lightColors, ambientLight);
                            }
                            snapshot.addPolygon(path);
                        }
                    }
                }
            }
        }
        snapshot.depthSort();
        return snapshot;
    }

    /*private*/ mapCoordinates(rv: RealVector, height: number, width: number): RealVector {
        const xscale: number = (<any>Math).fround(width / 2.0);
        rv = this.viewTransform.transform3dPt(rv);
        let p4: number[] = [rv.x, rv.y, rv.z, 1.0];
        p4 = this.eyeTrans.transform4d(p4);
        let x: number = (<any>Math).fround(p4[0] / p4[3]);
        let y: number = (<any>Math).fround(p4[1] / p4[3]);
        const z: number = (<any>Math).fround(p4[2] / p4[3]);
        x = (<any>Math).fround(xscale * ((<any>Math).fround(x + 1.0)));
        y = (<any>Math).fround(((<any>Math).fround(height - ((<any>Math).fround(width * y)))) / 2.0);
        return new RealVector(x, y, z);
    }

    constructor() {
        if (this.viewTransform === undefined) { this.viewTransform = null; }
        if (this.eyeTrans === undefined) { this.eyeTrans = null; }
    }
}
Java2dExporter["__class"] = "com.vzome.core.exporters2d.Java2dExporter";
