import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { GeometryExporter } from "./GeometryExporter.js";
import { Manifestation } from "../model/Manifestation.js";
import { Strut } from "../model/Strut.js";
import { File } from "../../../../java/io/File.js";
import { PrintWriter } from "../../../../java/io/PrintWriter.js";
import { NumberFormat } from "../../../../java/text/NumberFormat.js";

export class PythonBuild123dExporter extends GeometryExporter {
    static __static_initialized: boolean = false;
    static __static_initialize() { if (!PythonBuild123dExporter.__static_initialized) { PythonBuild123dExporter.__static_initialized = true; PythonBuild123dExporter.__static_initializer_0(); } }

    static FORMAT: NumberFormat; public static FORMAT_$LI$(): NumberFormat { PythonBuild123dExporter.__static_initialize(); if (PythonBuild123dExporter.FORMAT == null) { PythonBuild123dExporter.FORMAT = NumberFormat.getNumberInstance(java.util.Locale.US); }  return PythonBuild123dExporter.FORMAT; }

    static  __static_initializer_0() {
        PythonBuild123dExporter.FORMAT_$LI$().setMinimumFractionDigits(6);
        PythonBuild123dExporter.FORMAT_$LI$().setMaximumFractionDigits(6);
    }

    /**
     * 
     * @param {File} directory
     * @param {java.io.Writer} writer
     * @param {number} height
     * @param {number} width
     */
    public doExport(directory: File, writer: java.io.Writer, height: number, width: number) {
        let vertices: java.util.SortedSet<AlgebraicVector> = <any>(new java.util.TreeSet<any>());
        for(let index=this.mModel.iterator();index.hasNext();) {
            let rm = index.next();
            {
                const man: Manifestation = rm.getManifestation();
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                    const strut: Strut = <Strut><any>man;
                    let loc: AlgebraicVector = strut.getLocation();
                    vertices.add(loc);
                    loc = strut.getEnd();
                    vertices.add(loc);
                }
            }
        }
        const sortedVertexList: java.util.ArrayList<AlgebraicVector> = <any>(new java.util.ArrayList<any>(vertices));
        vertices = null;
        this.output = new PrintWriter(writer);
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
                const man: Manifestation = rm.getManifestation();
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                    const strut: Strut = <Strut><any>man;
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
}
PythonBuild123dExporter["__class"] = "com.vzome.core.exporters.PythonBuild123dExporter";

//  Run the Java static initializer eagerly at module load, as the monolithic
//  bundle did.  The lazy _$LI$ accessors are not enough for these classes:
//  e.g. XmlSymmetryFormat registers every format in FORMATS here, and
//  getFormat() reads that map without touching any accessor.
PythonBuild123dExporter.__static_initialize();
