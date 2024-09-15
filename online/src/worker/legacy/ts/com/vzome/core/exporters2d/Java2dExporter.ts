/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.exporters2d {
    /**
     * Builds a Java2dSnapshot, for use in rendering to a Snapshot2dPanel
     * or exporting via a SnapshotExporter.
     * @author vorth
     * @class
     */
    export class Java2dExporter {
        /*private*/ viewTransform: com.vzome.core.math.RealMatrix4;

        /*private*/ eyeTrans: com.vzome.core.math.RealMatrix4;

        public render2d(model: com.vzome.core.render.RenderedModel, viewTransform: com.vzome.core.math.RealMatrix4, eyeTransform: com.vzome.core.math.RealMatrix4, lights: com.vzome.core.viewing.Lights, height: number, width: number, drawLines: boolean, doLighting: boolean): com.vzome.core.exporters2d.Java2dSnapshot {
            this.viewTransform = viewTransform;
            this.eyeTrans = eyeTransform;
            const lightDirs: com.vzome.core.math.RealVector[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(lights.size());
            const lightColors: java.awt.Color[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(lights.size());
            let ambientLight: java.awt.Color;
            let background: java.awt.Color;
            const snapshot: com.vzome.core.exporters2d.Java2dSnapshot = new com.vzome.core.exporters2d.Java2dSnapshot();
            for(let i: number = 0; i < lightDirs.length; i++) {{
                lightDirs[i] = lights.getDirectionalLightVector(i).normalize().negate();
                lightColors[i] = new java.awt.Color(lights.getDirectionalLightColor(i).getRGB());
            };}
            ambientLight = new java.awt.Color(lights.getAmbientColor().getRGB());
            background = new java.awt.Color(lights.getBackgroundColor().getRGB());
            snapshot.setStrokeWidth(0.5);
            snapshot.setRect(new java.awt.geom.Rectangle2D.Float(0.0, 0.0, width, height));
            snapshot.setBackgroundColor(background);
            const mappedVertices: java.util.List<com.vzome.core.math.RealVector> = <any>(new java.util.ArrayList<any>(60));
            for(let index=model.iterator();index.hasNext();) {
                let rm = index.next();
                {
                    const shape: com.vzome.core.math.Polyhedron = rm.getShape();
                    const c: com.vzome.core.construction.Color = rm.getColor();
                    const color: java.awt.Color = (c == null) ? java.awt.Color.WHITE_$LI$() : new java.awt.Color(c.getRGB());
                    if (drawLines){
                        const m: com.vzome.core.model.Manifestation = rm.getManifestation();
                        if (m != null && (m.constructor != null && m.constructor["__interfaces"] != null && m.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                            const start: com.vzome.core.algebra.AlgebraicVector = (<com.vzome.core.model.Strut><any>m).getLocation();
                            const end: com.vzome.core.algebra.AlgebraicVector = (<com.vzome.core.model.Strut><any>m).getEnd();
                            const v0: com.vzome.core.math.RealVector = this.mapCoordinates(model.renderVector(start), height, width);
                            const v1: com.vzome.core.math.RealVector = this.mapCoordinates(model.renderVector(end), height, width);
                            snapshot.addLineSegment(color, v0, v1);
                        }
                        continue;
                    }
                    const vertices: java.util.List<com.vzome.core.algebra.AlgebraicVector> = shape.getVertexList();
                    const partOrientation: com.vzome.core.algebra.AlgebraicMatrix = rm.getOrientation();
                    const location: com.vzome.core.math.RealVector = rm.getLocation();
                    if (location == null)continue;
                    mappedVertices.clear();
                    for(let i: number = 0; i < vertices.size(); i++) {{
                        let gv: com.vzome.core.algebra.AlgebraicVector = vertices.get(i);
                        gv = partOrientation.timesColumn(gv);
                        const rv: com.vzome.core.math.RealVector = location.plus(model.renderVector(gv));
                        const v: com.vzome.core.math.RealVector = this.mapCoordinates(rv, height, width);
                        mappedVertices.add(v);
                    };}
                    for(let index=shape.getFaceSet().iterator();index.hasNext();) {
                        let face = index.next();
                        {
                            const arity: number = face.size();
                            const path: com.vzome.core.exporters2d.Java2dSnapshot.Polygon = new com.vzome.core.exporters2d.Java2dSnapshot.Polygon(color);
                            let backFacing: boolean = false;
                            let v1: com.vzome.core.math.RealVector = null;
                            let v2: com.vzome.core.math.RealVector = null;
                            for(let j: number = 0; j < arity; j++) {{
                                const index: number = face.get(j);
                                const v: com.vzome.core.math.RealVector = mappedVertices.get(index);
                                path.addVertex(v);
                                switch((path.size())) {
                                case 1:
                                    v1 = <com.vzome.core.math.RealVector>/* clone */((o: any) => { if (o.clone != undefined) { return (<any>o).clone(); } else { let clone = Object.create(o); for(let p in o) { if (o.hasOwnProperty(p)) clone[p] = o[p]; } return clone; } })(v);
                                    break;
                                case 2:
                                    v2 = <com.vzome.core.math.RealVector>/* clone */((o: any) => { if (o.clone != undefined) { return (<any>o).clone(); } else { let clone = Object.create(o); for(let p in o) { if (o.hasOwnProperty(p)) clone[p] = o[p]; } return clone; } })(v);
                                    break;
                                case 3:
                                    let v3: com.vzome.core.math.RealVector = <com.vzome.core.math.RealVector>/* clone */((o: any) => { if (o.clone != undefined) { return (<any>o).clone(); } else { let clone = Object.create(o); for(let p in o) { if (o.hasOwnProperty(p)) clone[p] = o[p]; } return clone; } })(v);
                                    v3 = v3.minus(v2);
                                    v2 = v2.minus(v1);
                                    const normal: com.vzome.core.math.RealVector = v2.cross(v3);
                                    backFacing = normal.z > 0;
                                    break;
                                default:
                                    break;
                                }
                            };}
                            path.close();
                            if (!backFacing){
                                if (doLighting){
                                    const faceNormal: com.vzome.core.algebra.AlgebraicVector = partOrientation.timesColumn(face.getNormal(vertices));
                                    const normal: com.vzome.core.math.RealVector = model.renderVector(faceNormal).normalize();
                                    let normalV: com.vzome.core.math.RealVector = new com.vzome.core.math.RealVector(normal.x, normal.y, normal.z);
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

        /*private*/ mapCoordinates(rv: com.vzome.core.math.RealVector, height: number, width: number): com.vzome.core.math.RealVector {
            const xscale: number = (<any>Math).fround(width / 2.0);
            rv = this.viewTransform.transform3dPt(rv);
            let p4: number[] = [rv.x, rv.y, rv.z, 1.0];
            p4 = this.eyeTrans.transform4d(p4);
            let x: number = (<any>Math).fround(p4[0] / p4[3]);
            let y: number = (<any>Math).fround(p4[1] / p4[3]);
            const z: number = (<any>Math).fround(p4[2] / p4[3]);
            x = (<any>Math).fround(xscale * ((<any>Math).fround(x + 1.0)));
            y = (<any>Math).fround(((<any>Math).fround(height - ((<any>Math).fround(width * y)))) / 2.0);
            return new com.vzome.core.math.RealVector(x, y, z);
        }

        constructor() {
            if (this.viewTransform === undefined) { this.viewTransform = null; }
            if (this.eyeTrans === undefined) { this.eyeTrans = null; }
        }
    }
    Java2dExporter["__class"] = "com.vzome.core.exporters2d.Java2dExporter";

}

