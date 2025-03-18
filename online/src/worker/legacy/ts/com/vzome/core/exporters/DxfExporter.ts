/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.exporters {
    export class DxfExporter extends com.vzome.core.exporters.GeometryExporter {
        /**
         * 
         * @param {java.io.File} directory
         * @param {java.io.Writer} writer
         * @param {number} height
         * @param {number} width
         */
        public doExport(directory: java.io.File, writer: java.io.Writer, height: number, width: number) {
            this.output = new java.io.PrintWriter(writer);
            this.output.println$java_lang_Object("0");
            this.output.println$java_lang_Object("SECTION");
            this.output.println$java_lang_Object("2");
            this.output.println$java_lang_Object("ENTITIES");
            const format: java.text.NumberFormat = java.text.NumberFormat.getNumberInstance(java.util.Locale.US);
            format.setMaximumFractionDigits(6);
            const inchScaling: number = this.mModel.getCmScaling() / 2.54;
            for(let index=this.mModel.iterator();index.hasNext();) {
                let rm = index.next();
                {
                    const man: com.vzome.core.model.Manifestation = rm.getManifestation();
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                        this.output.println$java_lang_Object("0");
                        this.output.println$java_lang_Object("LINE");
                        this.output.println$java_lang_Object("8");
                        this.output.println$java_lang_Object("vZome");
                        const start: com.vzome.core.algebra.AlgebraicVector = (<com.vzome.core.model.Strut><any>man).getLocation();
                        const end: com.vzome.core.algebra.AlgebraicVector = (<com.vzome.core.model.Strut><any>man).getEnd();
                        let rv: com.vzome.core.math.RealVector = this.mModel.renderVector(start);
                        rv = rv.scale(inchScaling);
                        this.output.println$java_lang_Object("10");
                        this.output.println$java_lang_Object(format.format(rv.x));
                        this.output.println$java_lang_Object("20");
                        this.output.println$java_lang_Object(format.format(rv.y));
                        this.output.println$java_lang_Object("30");
                        this.output.println$java_lang_Object(format.format(rv.z));
                        rv = this.mModel.renderVector(end);
                        rv = rv.scale(inchScaling);
                        this.output.println$java_lang_Object("11");
                        this.output.println$java_lang_Object(format.format(rv.x));
                        this.output.println$java_lang_Object("21");
                        this.output.println$java_lang_Object(format.format(rv.y));
                        this.output.println$java_lang_Object("31");
                        this.output.println$java_lang_Object(format.format(rv.z));
                    }
                }
            }
            this.output.println$java_lang_Object("0");
            this.output.println$java_lang_Object("ENDSEC");
            this.output.println$java_lang_Object("0");
            this.output.println$java_lang_Object("EOF");
            this.output.flush();
        }

        /**
         * 
         * @return {string}
         */
        public getFileExtension(): string {
            return "dxf";
        }
    }
    DxfExporter["__class"] = "com.vzome.core.exporters.DxfExporter";

}

