import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { GeometryExporter } from "./GeometryExporter.js";
import { Manifestation } from "../model/Manifestation.js";
import { Panel } from "../model/Panel.js";
import { File } from "../../../../java/io/File.js";
import { PrintWriter } from "../../../../java/io/PrintWriter.js";
import { DecimalFormat } from "../../../../java/text/DecimalFormat.js";
import { NumberFormat } from "../../../../java/text/NumberFormat.js";

export class PlyExporter extends GeometryExporter {
    static __static_initialized: boolean = false;
    static __static_initialize() { if (!PlyExporter.__static_initialized) { PlyExporter.__static_initialized = true; PlyExporter.__static_initializer_0(); } }

    static FORMAT: NumberFormat; public static FORMAT_$LI$(): NumberFormat { PlyExporter.__static_initialize(); if (PlyExporter.FORMAT == null) { PlyExporter.FORMAT = NumberFormat.getNumberInstance(java.util.Locale.US); }  return PlyExporter.FORMAT; }

    /*private*/ vertexData: java.util.Map<AlgebraicVector, number>;

    /*private*/ vertices: java.lang.StringBuffer;

    static  __static_initializer_0() {
        if (PlyExporter.FORMAT_$LI$() != null && PlyExporter.FORMAT_$LI$() instanceof <any>DecimalFormat){
            (<DecimalFormat>PlyExporter.FORMAT_$LI$()).applyPattern("0.000000E00");
        }
    }

    /**
     * 
     * @param {File} directory
     * @param {java.io.Writer} writer
     * @param {number} height
     * @param {number} width
     */
    public doExport(directory: File, writer: java.io.Writer, height: number, width: number) {
        let numPanels: number = 0;
        const panels: java.lang.StringBuffer = new java.lang.StringBuffer();
        this.vertexData = <any>(new java.util.LinkedHashMap<any, any>());
        this.vertices = new java.lang.StringBuffer();
        const output: PrintWriter = new PrintWriter(writer);
        output.println$java_lang_Object("ply");
        output.println$java_lang_Object("format ascii 1.0");
        output.println$java_lang_Object("comment   Exported by vZome, http://vzome.com");
        output.println$java_lang_Object("comment     All vertex data is in inches");
        for(let index=this.mModel.iterator();index.hasNext();) {
            let rm = index.next();
            {
                const man: Manifestation = rm.getManifestation();
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                    ++numPanels;
                    const vs: java.util.List<number> = <any>(new java.util.ArrayList<any>());
                    for(let index=(<Panel><any>man).iterator();index.hasNext();) {
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

    getVertexIndex(vertexVector: AlgebraicVector): number {
        let obj: number = this.vertexData.get(vertexVector);
        if (obj == null){
            const key: AlgebraicVector = vertexVector;
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

//  Run the Java static initializer eagerly at module load, as the monolithic
//  bundle did.  The lazy _$LI$ accessors are not enough for these classes:
//  e.g. XmlSymmetryFormat registers every format in FORMATS here, and
//  getFormat() reads that map without touching any accessor.
PlyExporter.__static_initialize();
