import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { GeometryExporter } from "./GeometryExporter.js";
import { RealVector } from "../math/RealVector.js";
import { Manifestation } from "../model/Manifestation.js";
import { Strut } from "../model/Strut.js";
import { File } from "../../../../java/io/File.js";
import { PrintWriter } from "../../../../java/io/PrintWriter.js";
import { NumberFormat } from "../../../../java/text/NumberFormat.js";

export class DxfExporter extends GeometryExporter {
    /**
     * 
     * @param {File} directory
     * @param {java.io.Writer} writer
     * @param {number} height
     * @param {number} width
     */
    public doExport(directory: File, writer: java.io.Writer, height: number, width: number) {
        this.output = new PrintWriter(writer);
        this.output.println$java_lang_Object("0");
        this.output.println$java_lang_Object("SECTION");
        this.output.println$java_lang_Object("2");
        this.output.println$java_lang_Object("ENTITIES");
        const format: NumberFormat = NumberFormat.getNumberInstance(java.util.Locale.US);
        format.setMaximumFractionDigits(6);
        const inchScaling: number = this.mModel.getCmScaling() / 2.54;
        for(let index=this.mModel.iterator();index.hasNext();) {
            let rm = index.next();
            {
                const man: Manifestation = rm.getManifestation();
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                    this.output.println$java_lang_Object("0");
                    this.output.println$java_lang_Object("LINE");
                    this.output.println$java_lang_Object("8");
                    this.output.println$java_lang_Object("vZome");
                    const start: AlgebraicVector = (<Strut><any>man).getLocation();
                    const end: AlgebraicVector = (<Strut><any>man).getEnd();
                    let rv: RealVector = this.mModel.renderVector(start);
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
