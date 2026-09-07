import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { GeometryExporter } from "./GeometryExporter.js";
import { RealVector } from "../math/RealVector.js";
import { Manifestation } from "../model/Manifestation.js";
import { Panel } from "../model/Panel.js";
import { File } from "../../../../java/io/File.js";
import { PrintWriter } from "../../../../java/io/PrintWriter.js";
import { DecimalFormat } from "../../../../java/text/DecimalFormat.js";
import { NumberFormat } from "../../../../java/text/NumberFormat.js";

export class StlExporter extends GeometryExporter {
    static FORMAT: NumberFormat; public static FORMAT_$LI$(): NumberFormat { if (StlExporter.FORMAT == null) { StlExporter.FORMAT = NumberFormat.getNumberInstance(java.util.Locale.US); }  return StlExporter.FORMAT; }

    /**
     * 
     * @param {File} directory
     * @param {java.io.Writer} writer
     * @param {number} height
     * @param {number} width
     */
    public doExport(directory: File, writer: java.io.Writer, height: number, width: number) {
        if (StlExporter.FORMAT_$LI$() != null && StlExporter.FORMAT_$LI$() instanceof <any>DecimalFormat){
            (<DecimalFormat>StlExporter.FORMAT_$LI$()).applyPattern("0.000000E00");
        }
        const mmScaling: number = this.mModel.getCmScaling() * 10.0;
        this.output = new PrintWriter(writer);
        this.output.println$java_lang_Object("solid vcg");
        for(let index=this.mModel.iterator();index.hasNext();) {
            let rm = index.next();
            {
                const man: Manifestation = rm.getManifestation();
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                    const panel: Panel = <Panel><any>man;
                    const norm: RealVector = this.mModel.renderVector(panel['getNormal$']()).normalize();
                    let v0: RealVector = null;
                    let v1: RealVector = null;
                    for(let index=panel.iterator();index.hasNext();) {
                        let vert = index.next();
                        {
                            let vertex: RealVector = this.mModel.renderVector(vert);
                            vertex = vertex.scale(mmScaling);
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
}
StlExporter["__class"] = "com.vzome.core.exporters.StlExporter";
