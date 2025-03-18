/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.exporters {
    export class VefExporter extends com.vzome.core.exporters.GeometryExporter {
        /**
         * 
         * @param {java.io.File} directory
         * @param {java.io.Writer} writer
         * @param {number} height
         * @param {number} width
         */
        public doExport(directory: java.io.File, writer: java.io.Writer, height: number, width: number) {
            const field: com.vzome.core.algebra.AlgebraicField = this.mModel.getField();
            const exporter: com.vzome.core.model.VefModelExporter = new com.vzome.core.model.VefModelExporter(writer, field);
            for(let index=this.mModel.iterator();index.hasNext();) {
                let rm = index.next();
                {
                    const man: com.vzome.core.model.Manifestation = rm.getManifestation();
                    exporter.exportManifestation(man);
                }
            }
            exporter.finish();
        }

        /**
         * 
         * @return {string}
         */
        public getFileExtension(): string {
            return "vef";
        }
    }
    VefExporter["__class"] = "com.vzome.core.exporters.VefExporter";

}

