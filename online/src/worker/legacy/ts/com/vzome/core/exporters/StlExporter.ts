/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.exporters {
    export class StlExporter extends com.vzome.core.exporters.GeometryExporter {
        static FORMAT: java.text.NumberFormat; public static FORMAT_$LI$(): java.text.NumberFormat { if (StlExporter.FORMAT == null) { StlExporter.FORMAT = java.text.NumberFormat.getNumberInstance(java.util.Locale.US); }  return StlExporter.FORMAT; }

        /**
         * 
         * @param {java.io.File} directory
         * @param {java.io.Writer} writer
         * @param {number} height
         * @param {number} width
         */
        public doExport(directory: java.io.File, writer: java.io.Writer, height: number, width: number) {
            if (StlExporter.FORMAT_$LI$() != null && StlExporter.FORMAT_$LI$() instanceof <any>java.text.DecimalFormat){
                (<java.text.DecimalFormat>StlExporter.FORMAT_$LI$()).applyPattern("0.000000E00");
            }
            this.output = new java.io.PrintWriter(writer);
            this.output.println$java_lang_Object("solid vcg");
            for(let index=this.mModel.iterator();index.hasNext();) {
                let rm = index.next();
                {
                    const man: com.vzome.core.model.Manifestation = rm.getManifestation();
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                        const panel: com.vzome.core.model.Panel = <com.vzome.core.model.Panel><any>man;
                        const norm: com.vzome.core.math.RealVector = this.mModel.renderVector(panel['getNormal$']()).normalize();
                        let v0: com.vzome.core.math.RealVector = null;
                        let v1: com.vzome.core.math.RealVector = null;
                        for(let index=panel.iterator();index.hasNext();) {
                            let vert = index.next();
                            {
                                let vertex: com.vzome.core.math.RealVector = this.mModel.renderVector(vert);
                                vertex = vertex.scale(com.vzome.core.render.RealZomeScaling.RZOME_MM_SCALING);
                                if (v0 == null)v0 = vertex; else if (v1 == null)v1 = vertex; else {
                                    this.output.print("  facet normal ");
                                    this.output.println$java_lang_Object(StlExporter.FORMAT_$LI$().format(norm.x) + " " + StlExporter.FORMAT_$LI$().format(norm.y) + " " + StlExporter.FORMAT_$LI$().format(norm.z));
                                    this.output.println$java_lang_Object("    outer loop");
                                    this.output.println$java_lang_Object("      vertex " + StlExporter.FORMAT_$LI$().format(v0.x) + " " + StlExporter.FORMAT_$LI$().format(v0.y) + " " + StlExporter.FORMAT_$LI$().format(v0.z));
                                    this.output.println$java_lang_Object("      vertex " + StlExporter.FORMAT_$LI$().format(v1.x) + " " + StlExporter.FORMAT_$LI$().format(v1.y) + " " + StlExporter.FORMAT_$LI$().format(v1.z));
                                    this.output.println$java_lang_Object("      vertex " + StlExporter.FORMAT_$LI$().format(vertex.x) + " " + StlExporter.FORMAT_$LI$().format(vertex.y) + " " + StlExporter.FORMAT_$LI$().format(vertex.z));
                                    this.output.println$java_lang_Object("    endloop");
                                    this.output.println$java_lang_Object("  endfacet");
                                    v1 = vertex;
                                }
                            }
                        }
                    }
                }
            }
            this.output.println$java_lang_Object("endsolid vcg");
            this.output.flush();
        }

        /**
         * 
         * @return {string}
         */
        public getFileExtension(): string {
            return "stl";
        }

        constructor() {
            super();
        }
    }
    StlExporter["__class"] = "com.vzome.core.exporters.StlExporter";
    StlExporter["__interfaces"] = ["com.vzome.core.render.RealZomeScaling"];


}

