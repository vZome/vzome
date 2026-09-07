import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { GeometryExporter } from "./GeometryExporter.js";
import { RealVector } from "../math/RealVector.js";
import { Manifestation } from "../model/Manifestation.js";
import { Strut } from "../model/Strut.js";
import { File } from "../../../../java/io/File.js";
import { DecimalFormat } from "../../../../java/text/DecimalFormat.js";
import { NumberFormat } from "../../../../java/text/NumberFormat.js";

export class SegExporter extends GeometryExporter {
    /**
     * 
     * @param {File} directory
     * @param {java.io.Writer} writer
     * @param {number} height
     * @param {number} width
     */
    public doExport(directory: File, writer: java.io.Writer, height: number, width: number) {
        this.field = this.mModel.getField();
        this.vertices = new java.lang.StringBuffer();
        this.struts = new java.lang.StringBuffer();
        if (this.format != null && this.format instanceof <any>DecimalFormat){
            (<DecimalFormat>this.format).applyPattern("0.0000");
        }
        for(let index=this.mModel.iterator();index.hasNext();) {
            let rm = index.next();
            {
                const man: Manifestation = rm.getManifestation();
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                    const strut: Strut = <Strut><any>man;
                    this.struts.append("s ");
                    this.struts.append(this.getVertexIndex(strut.getLocation()));
                    this.struts.append(" ");
                    this.struts.append(this.getVertexIndex(strut.getEnd()));
                    this.struts.append("\n");
                }
            }
        }
        writer.append(this.vertices.toString());
        writer.append(this.struts.toString());
        writer.close();
    }

    /**
     * 
     * @return {string}
     */
    public getFileExtension(): string {
        return "seg";
    }

    /*private*/ vertexData: java.util.Map<AlgebraicVector, number>;

    /*private*/ vertices: java.lang.StringBuffer;

    /*private*/ struts: java.lang.StringBuffer;

    field: AlgebraicField;

    /*private*/ format: NumberFormat;

    getVertexIndex(vertexVector: AlgebraicVector): number {
        let val: number = this.vertexData.get(vertexVector);
        if (val == null){
            const key: AlgebraicVector = vertexVector;
            const index: number = this.vertexData.size();
            val = index;
            this.vertexData.put(key, val);
            this.vertices.append("v ");
            const vertex: RealVector = this.mModel.renderVector(vertexVector);
            this.vertices.append(this.format.format(vertex.x) + " ");
            this.vertices.append(this.format.format(vertex.y) + " ");
            this.vertices.append(this.format.format(vertex.z) + " ");
            this.vertices.append("\n");
        }
        return val;
    }

    constructor() {
        super();
        this.vertexData = <any>(new java.util.HashMap<any, any>());
        if (this.vertices === undefined) { this.vertices = null; }
        if (this.struts === undefined) { this.struts = null; }
        if (this.field === undefined) { this.field = null; }
        this.format = NumberFormat.getNumberInstance(java.util.Locale.US);
    }
}
SegExporter["__class"] = "com.vzome.core.exporters.SegExporter";
