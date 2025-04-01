/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.exporters {
    /**
     * This only exports shapes, no instances yet.
     * 
     * @author vorth
     * @class
     * @extends com.vzome.core.exporters.GeometryExporter
     */
    export class STEPExporter extends com.vzome.core.exporters.GeometryExporter {
        static PREAMBLE_FILE: string = "com/vzome/core/exporters/step/preamble.step";

        static POSTLUDE: string = "ENDSEC;\nEND-ISO-10303-21;";

        static START_INDEX: number = 300;

        /**
         * 
         * @param {java.io.File} file
         * @param {java.io.Writer} writer
         * @param {number} height
         * @param {number} width
         */
        public doExport(file: java.io.File, writer: java.io.Writer, height: number, width: number) {
            this.output = new java.io.PrintWriter(writer);
            const preamble: string = com.vzome.xml.ResourceLoader.loadStringResource(STEPExporter.PREAMBLE_FILE);
            this.output.println$java_lang_Object(preamble);
            this.output.println$();
            const FORMAT: java.text.NumberFormat = java.text.NumberFormat.getNumberInstance(java.util.Locale.US);
            FORMAT.setMaximumFractionDigits(19);
            let vertices: java.util.SortedSet<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.TreeSet<any>());
            com.vzome.core.editor.api.Manifestations.sortVertices(this.mModel.getManifestations(), vertices);
            const sortedVertexList: java.util.ArrayList<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.ArrayList<any>(vertices));
            vertices = null;
            const inchScaling: number = this.mModel.getCmScaling() / 2.54;
            let index: number = STEPExporter.START_INDEX - 1;
            const realVectors: java.util.ArrayList<com.vzome.core.math.RealVector> = <any>(new java.util.ArrayList<any>());
            for(let index1=sortedVertexList.iterator();index1.hasNext();) {
                let gv = index1.next();
                {
                    const v: com.vzome.core.math.RealVector = this.mModel.renderVector(gv).scale(inchScaling);
                    realVectors.add(v);
                    const cpIndex: number = ++index;
                    this.output.println$java_lang_Object("#" + cpIndex + " = CARTESIAN_POINT ( \'NONE\',  ( " + v.toString$java_text_NumberFormat(FORMAT) + " ) ) ;");
                    this.output.println$java_lang_Object("#" + (++index) + " = VERTEX_POINT ( \'NONE\', #" + cpIndex + " ) ;");
                    this.output.println$();
                }
            }
            this.output.println$();
            const faceIndices: java.util.ArrayList<number> = <any>(new java.util.ArrayList<any>());
            for(let index1=this.mModel.getManifestations().iterator();index1.hasNext();) {
                let man = index1.next();
                {
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                        const panel: com.vzome.core.model.Panel = <com.vzome.core.model.Panel><any>man;
                        const arity: number = panel.getVertexCount();
                        const vIndices: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(arity);
                        let k: number = 0;
                        for(let index1=panel.iterator();index1.hasNext();) {
                            let av = index1.next();
                            {
                                const vIndex: number = sortedVertexList.indexOf(av);
                                vIndices[k++] = vIndex;
                            }
                        }
                        const edgeIndices: java.util.ArrayList<number> = <any>(new java.util.ArrayList<any>());
                        let point1: number = 0;
                        let dir1: com.vzome.core.math.RealVector = null;
                        let dir2: com.vzome.core.math.RealVector = null;
                        for(let j: number = 0; j < arity; j++) {{
                            let vindex: number = vIndices[j];
                            const rv1index: number = vindex;
                            let rv1: com.vzome.core.math.RealVector = realVectors.get(rv1index);
                            point1 = rv1index * 2 + STEPExporter.START_INDEX;
                            const vertex1: number = point1 + 1;
                            vindex = vIndices[(j + 1) % arity];
                            const rv2index: number = vindex;
                            let rv2: com.vzome.core.math.RealVector = realVectors.get(rv2index);
                            const vertex2: number = rv2index * 2 + STEPExporter.START_INDEX + 1;
                            const direction1: number = ++index;
                            rv1 = rv2.minus(rv1);
                            rv2 = rv1.normalize();
                            if (dir2 == null){
                                if (dir1 == null)dir1 = rv2; else {
                                    dir2 = dir1.cross(rv2).normalize();
                                    dir1 = dir2.cross(dir1);
                                }
                            }
                            this.output.println$java_lang_Object("#" + direction1 + " = DIRECTION ( \'NONE\',  ( " + rv2.toString$java_text_NumberFormat(FORMAT) + " ) ) ;");
                            const vector: number = ++index;
                            this.output.println$java_lang_Object("#" + vector + " = VECTOR ( \'NONE\', #" + direction1 + ", 39.37 ) ;");
                            const line: number = ++index;
                            this.output.println$java_lang_Object("#" + line + " = LINE ( \'NONE\', #" + point1 + ", #" + vector + " ) ;");
                            const edgeCurve: number = ++index;
                            this.output.println$java_lang_Object("#" + edgeCurve + " = EDGE_CURVE ( \'NONE\', #" + vertex1 + ", #" + vertex2 + ", #" + line + ", .T. ) ;");
                            const orientedEdge: number = ++index;
                            edgeIndices.add(orientedEdge);
                            this.output.println$java_lang_Object("#" + orientedEdge + " = ORIENTED_EDGE ( \'NONE\', *, *, #" + edgeCurve + ", ." + ((j === 0) ? "T" : "T") + ". ) ;");
                        };}
                        const edgeLoop: number = ++index;
                        this.output.print("#" + edgeLoop + " = EDGE_LOOP ( \'NONE\', ( ");
                        let delim: string = "";
                        for(let index1=edgeIndices.iterator();index1.hasNext();) {
                            let i = index1.next();
                            {
                                this.output.print(delim + "#" + i);
                                delim = ", ";
                            }
                        }
                        this.output.println$java_lang_Object(" ) ) ;");
                        const direction1: number = ++index;
                        this.output.println$java_lang_Object("#" + direction1 + " = DIRECTION ( \'NONE\',  ( " + dir2.toString$java_text_NumberFormat(FORMAT) + " ) ) ;");
                        const direction2: number = ++index;
                        this.output.println$java_lang_Object("#" + direction2 + " = DIRECTION ( \'NONE\',  ( " + dir1.toString$java_text_NumberFormat(FORMAT) + " ) ) ;");
                        const axisPlacement3d: number = ++index;
                        this.output.println$java_lang_Object("#" + axisPlacement3d + " = AXIS2_PLACEMENT_3D ( \'NONE\', #" + point1 + ", #" + direction1 + ", #" + direction2 + " ) ;");
                        const plane: number = ++index;
                        this.output.println$java_lang_Object("#" + plane + " = PLANE ( \'NONE\', #" + axisPlacement3d + " ) ;");
                        const faceOuterBound: number = ++index;
                        this.output.println$java_lang_Object("#" + faceOuterBound + " = FACE_OUTER_BOUND ( \'NONE\', #" + edgeLoop + ", .T. ) ;");
                        const advancedFace: number = ++index;
                        this.output.println$java_lang_Object("#" + advancedFace + " = ADVANCED_FACE ( \'NONE\', ( #" + faceOuterBound + " ), #" + plane + ", .T. ) ;");
                        this.output.println$();
                        faceIndices.add(advancedFace);
                    }
                }
            }
            this.output.print("#299 = CLOSED_SHELL ( \'NONE\', ( ");
            let delim: string = "";
            for(let index1=faceIndices.iterator();index1.hasNext();) {
                let i = index1.next();
                {
                    this.output.print(delim + "#" + i);
                    delim = ", ";
                }
            }
            this.output.println$java_lang_Object(" ) ) ;");
            this.output.println$java_lang_Object(STEPExporter.POSTLUDE);
            this.output.flush();
            this.output.close();
        }

        /**
         * 
         * @return {string}
         */
        public getFileExtension(): string {
            return "step";
        }
    }
    STEPExporter["__class"] = "com.vzome.core.exporters.STEPExporter";

}

