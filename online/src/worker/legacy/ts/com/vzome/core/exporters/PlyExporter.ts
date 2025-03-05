/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.exporters {
    export class PlyExporter extends com.vzome.core.exporters.GeometryExporter {
        static __static_initialized: boolean = false;
        static __static_initialize() { if (!PlyExporter.__static_initialized) { PlyExporter.__static_initialized = true; PlyExporter.__static_initializer_0(); } }

        static FORMAT: java.text.NumberFormat; public static FORMAT_$LI$(): java.text.NumberFormat { PlyExporter.__static_initialize(); if (PlyExporter.FORMAT == null) { PlyExporter.FORMAT = java.text.NumberFormat.getNumberInstance(java.util.Locale.US); }  return PlyExporter.FORMAT; }

        /*private*/ vertexData: java.util.Map<com.vzome.core.algebra.AlgebraicVector, number>;

        /*private*/ vertices: java.lang.StringBuffer;

        static  __static_initializer_0() {
            if (PlyExporter.FORMAT_$LI$() != null && PlyExporter.FORMAT_$LI$() instanceof <any>java.text.DecimalFormat){
                (<java.text.DecimalFormat>PlyExporter.FORMAT_$LI$()).applyPattern("0.000000E00");
            }
        }

        /**
         * 
         * @param {java.io.File} directory
         * @param {java.io.Writer} writer
         * @param {number} height
         * @param {number} width
         */
        public doExport(directory: java.io.File, writer: java.io.Writer, height: number, width: number) {
            let numPanels: number = 0;
            const panels: java.lang.StringBuffer = new java.lang.StringBuffer();
            this.vertexData = <any>(new java.util.LinkedHashMap<any, any>());
            this.vertices = new java.lang.StringBuffer();
            const output: java.io.PrintWriter = new java.io.PrintWriter(writer);
            output.println$java_lang_Object("ply");
            output.println$java_lang_Object("format ascii 1.0");
            output.println$java_lang_Object("comment   Exported by vZome, http://vzome.com");
            output.println$java_lang_Object("comment     All vertex data is in inches");
            for(let index=this.mModel.iterator();index.hasNext();) {
                let rm = index.next();
                {
                    const man: com.vzome.core.model.Manifestation = rm.getManifestation();
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                        ++numPanels;
                        const vs: java.util.List<number> = <any>(new java.util.ArrayList<any>());
                        for(let index=(<com.vzome.core.model.Panel><any>man).iterator();index.hasNext();) {
                            let vertex = index.next();
                            {
                                vs.add(this.getVertexIndex(vertex));
                            }
                        }
                        panels.append(vs.size());
                        for(let index=vs.iterator();index.hasNext();) {
                            let v = index.next();
                            {
                                panels.append(" ");
                                panels.append(v);
                            }
                        }
                        panels.append("\n");
                    }
                }
            }
            output.println$java_lang_Object("element vertex " + this.vertexData.size());
            output.println$java_lang_Object("property float x");
            output.println$java_lang_Object("property float y");
            output.println$java_lang_Object("property float z");
            output.println$java_lang_Object("element face " + numPanels);
            output.println$java_lang_Object("property list uchar int vertex_indices");
            output.println$java_lang_Object("end_header");
            output.print(this.vertices);
            output.print(panels);
            output.flush();
        }

        getVertexIndex(vertexVector: com.vzome.core.algebra.AlgebraicVector): number {
            let obj: number = this.vertexData.get(vertexVector);
            if (obj == null){
                const key: com.vzome.core.algebra.AlgebraicVector = vertexVector;
                const index: number = this.vertexData.size();
                obj = index;
                this.vertexData.put(key, obj);
                this.vertices.append(this.mModel.renderVector(vertexVector).spacedString() + "\n");
            }
            return obj;
        }

        /**
         * 
         * @return {string}
         */
        public getFileExtension(): string {
            return "ply";
        }

        constructor() {
            super();
            if (this.vertexData === undefined) { this.vertexData = null; }
            if (this.vertices === undefined) { this.vertices = null; }
        }
    }
    PlyExporter["__class"] = "com.vzome.core.exporters.PlyExporter";

}


com.vzome.core.exporters.PlyExporter.__static_initialize();
