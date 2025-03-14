/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.exporters {
    export class OffExporter extends com.vzome.core.exporters.GeometryExporter {
        static __static_initialized: boolean = false;
        static __static_initialize() { if (!OffExporter.__static_initialized) { OffExporter.__static_initialized = true; OffExporter.__static_initializer_0(); } }

        static FORMAT: java.text.NumberFormat; public static FORMAT_$LI$(): java.text.NumberFormat { OffExporter.__static_initialize(); if (OffExporter.FORMAT == null) { OffExporter.FORMAT = java.text.NumberFormat.getNumberInstance(java.util.Locale.US); }  return OffExporter.FORMAT; }

        static  __static_initializer_0() {
            OffExporter.FORMAT_$LI$().setMinimumFractionDigits(16);
        }

        /**
         * 
         * @param {java.io.File} directory
         * @param {java.io.Writer} writer
         * @param {number} height
         * @param {number} width
         */
        public doExport(directory: java.io.File, writer: java.io.Writer, height: number, width: number) {
            let vertices: java.util.SortedSet<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.TreeSet<any>());
            let numStruts: number = 0;
            const arrayComparator: com.vzome.core.generic.ArrayComparator<com.vzome.core.algebra.AlgebraicVector> = <any>(new com.vzome.core.generic.ArrayComparator<any>());
            const panelVertices: java.util.SortedSet<com.vzome.core.algebra.AlgebraicVector[]> = <any>(new java.util.TreeSet<any>(<any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0, arg1) =>  (funcInst['compare'] ? funcInst['compare'] : funcInst) .call(funcInst, arg0, arg1)})(arrayComparator.getLengthFirstArrayComparator()))));
            for(let index=this.mModel.iterator();index.hasNext();) {
                let rm = index.next();
                {
                    const man: com.vzome.core.model.Manifestation = rm.getManifestation();
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                        const loc: com.vzome.core.algebra.AlgebraicVector = man.getLocation();
                        vertices.add(loc);
                    } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                        ++numStruts;
                    } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                        const panel: com.vzome.core.model.Panel = <com.vzome.core.model.Panel><any>man;
                        const corners: java.util.ArrayList<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.ArrayList<any>(panel.getVertexCount()));
                        for(let index=panel.iterator();index.hasNext();) {
                            let vertex = index.next();
                            {
                                corners.add(vertex);
                            }
                        }
                        vertices.addAll(corners);
                        const cornerArray: com.vzome.core.algebra.AlgebraicVector[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(corners.size());
                        corners.toArray<any>(cornerArray);
                        panelVertices.add(cornerArray);
                    }
                }
            }
            const sortedVertexList: java.util.ArrayList<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.ArrayList<any>(vertices));
            vertices = null;
            this.output = new java.io.PrintWriter(writer);
            this.output.println$java_lang_Object("OFF");
            this.output.println$java_lang_Object("# numVertices numFaces numEdges (numEdges is ignored)");
            this.output.println$java_lang_Object(sortedVertexList.size() + " " + panelVertices.size() + " " + numStruts + "\n");
            this.output.println$java_lang_Object("# Vertices.  Each line is the XYZ coordinates of one vertex.");
            for(let index=sortedVertexList.iterator();index.hasNext();) {
                let vector = index.next();
                {
                    const dv: number[] = this.mModel.renderVectorDouble(vector);
                    this.output.print(OffExporter.FORMAT_$LI$().format(dv[0]) + " ");
                    this.output.print(OffExporter.FORMAT_$LI$().format(dv[1]) + " ");
                    this.output.print(OffExporter.FORMAT_$LI$().format(dv[2]) + "\n");
                }
            }
            this.output.println$();
            this.output.println$java_lang_Object("# Faces.  numCorners vertexIndex[0] ... vertexIndex[numCorners-1]");
            for(let index=panelVertices.iterator();index.hasNext();) {
                let corners = index.next();
                {
                    this.output.print(corners.length);
                    for(let index = 0; index < corners.length; index++) {
                        let corner = corners[index];
                        {
                            this.output.print(" " + sortedVertexList.indexOf(corner));
                        }
                    }
                    this.output.println$();
                }
            }
            this.output.flush();
        }

        /**
         * 
         * @return {string}
         */
        public getFileExtension(): string {
            return "off";
        }
    }
    OffExporter["__class"] = "com.vzome.core.exporters.OffExporter";

}


com.vzome.core.exporters.OffExporter.__static_initialize();
