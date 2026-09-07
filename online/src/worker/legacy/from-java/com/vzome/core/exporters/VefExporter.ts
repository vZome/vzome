import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { GeometryExporter } from "./GeometryExporter.js";
import { Manifestation } from "../model/Manifestation.js";
import { VefModelExporter } from "../model/VefModelExporter.js";
import { File } from "../../../../java/io/File.js";

export class VefExporter extends GeometryExporter {
    /**
     * 
     * @param {File} directory
     * @param {java.io.Writer} writer
     * @param {number} height
     * @param {number} width
     */
    public doExport(directory: File, writer: java.io.Writer, height: number, width: number) {
        const field: AlgebraicField = this.mModel.getField();
        const exporter: VefModelExporter = new VefModelExporter(writer, field);
        for(let index=this.mModel.iterator();index.hasNext();) {
            let rm = index.next();
            {
                const man: Manifestation = rm.getManifestation();
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
