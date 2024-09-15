/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.exporters {
    export class PythonBuild123dExporter extends com.vzome.core.exporters.GeometryExporter {
        static __static_initialized: boolean = false;
        static __static_initialize() { if (!PythonBuild123dExporter.__static_initialized) { PythonBuild123dExporter.__static_initialized = true; PythonBuild123dExporter.__static_initializer_0(); } }

        static FORMAT: java.text.NumberFormat; public static FORMAT_$LI$(): java.text.NumberFormat { PythonBuild123dExporter.__static_initialize(); if (PythonBuild123dExporter.FORMAT == null) { PythonBuild123dExporter.FORMAT = java.text.NumberFormat.getNumberInstance(java.util.Locale.US); }  return PythonBuild123dExporter.FORMAT; }

        static  __static_initializer_0() {
            PythonBuild123dExporter.FORMAT_$LI$().setMinimumFractionDigits(6);
            PythonBuild123dExporter.FORMAT_$LI$().setMaximumFractionDigits(6);
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
            for(let index=this.mModel.iterator();index.hasNext();) {
                let rm = index.next();
                {
                    const man: com.vzome.core.model.Manifestation = rm.getManifestation();
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                        const strut: com.vzome.core.model.Strut = <com.vzome.core.model.Strut><any>man;
                        let loc: com.vzome.core.algebra.AlgebraicVector = strut.getLocation();
                        vertices.add(loc);
                        loc = strut.getEnd();
                        vertices.add(loc);
                    }
                }
            }
            const sortedVertexList: java.util.ArrayList<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.ArrayList<any>(vertices));
            vertices = null;
            this.output = new java.io.PrintWriter(writer);
            const prelude: string = this.getBoilerplate("com/vzome/core/exporters/mesh-prelude.py");
            this.output.print(prelude);
            this.output.println$java_lang_Object("vertices = [");
            for(let index=sortedVertexList.iterator();index.hasNext();) {
                let vector = index.next();
                {
                    const dv: number[] = this.mModel.renderVectorDouble(vector);
                    this.output.print("( ");
                    this.output.print(PythonBuild123dExporter.FORMAT_$LI$().format(dv[0]) + ", ");
                    this.output.print(PythonBuild123dExporter.FORMAT_$LI$().format(dv[1]) + ", ");
                    this.output.println$java_lang_Object(PythonBuild123dExporter.FORMAT_$LI$().format(dv[2]) + " ),");
                }
            }
            this.output.println$java_lang_Object("]");
            this.output.println$();
            this.output.println$java_lang_Object("edges = [");
            for(let index=this.mModel.iterator();index.hasNext();) {
                let rm = index.next();
                {
                    const man: com.vzome.core.model.Manifestation = rm.getManifestation();
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                        const strut: com.vzome.core.model.Strut = <com.vzome.core.model.Strut><any>man;
                        this.output.println$java_lang_Object("[ " + sortedVertexList.indexOf(strut.getLocation()) + ", " + sortedVertexList.indexOf(strut.getEnd()) + " ],");
                    }
                }
            }
            this.output.println$java_lang_Object("]");
            const postlude: string = this.getBoilerplate("com/vzome/core/exporters/mesh-postlude.py");
            this.output.print(postlude);
            this.output.flush();
        }

        /**
         * 
         * @return {string}
         */
        public getFileExtension(): string {
            return "py";
        }

        constructor() {
            super();
        }
    }
    PythonBuild123dExporter["__class"] = "com.vzome.core.exporters.PythonBuild123dExporter";
    PythonBuild123dExporter["__interfaces"] = ["com.vzome.core.render.RealZomeScaling"];


}


com.vzome.core.exporters.PythonBuild123dExporter.__static_initialize();
