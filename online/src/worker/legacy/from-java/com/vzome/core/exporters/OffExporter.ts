import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { GeometryExporter } from "./GeometryExporter.js";
import { ArrayComparator } from "../generic/ArrayComparator.js";
import { Manifestation } from "../model/Manifestation.js";
import { Panel } from "../model/Panel.js";
import { File } from "../../../../java/io/File.js";
import { PrintWriter } from "../../../../java/io/PrintWriter.js";
import { NumberFormat } from "../../../../java/text/NumberFormat.js";

export class OffExporter extends GeometryExporter {
    static __static_initialized: boolean = false;
    static __static_initialize() { if (!OffExporter.__static_initialized) { OffExporter.__static_initialized = true; OffExporter.__static_initializer_0(); } }

    static FORMAT: NumberFormat; public static FORMAT_$LI$(): NumberFormat { OffExporter.__static_initialize(); if (OffExporter.FORMAT == null) { OffExporter.FORMAT = NumberFormat.getNumberInstance(java.util.Locale.US); }  return OffExporter.FORMAT; }

    static  __static_initializer_0() {
        OffExporter.FORMAT_$LI$().setMinimumFractionDigits(16);
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
        let numStruts: number = 0;
        const arrayComparator: ArrayComparator<AlgebraicVector> = <any>(new ArrayComparator<any>());
        const panelVertices: java.util.SortedSet<AlgebraicVector[]> = <any>(new java.util.TreeSet<any>(<any>(((funcInst: any) => { if (funcInst == null || typeof funcInst == 'function') { return funcInst } return (arg0, arg1) =>  (funcInst['compare'] ? funcInst['compare'] : funcInst) .call(funcInst, arg0, arg1)})(arrayComparator.getLengthFirstArrayComparator()))));
        for(let index=this.mModel.iterator();index.hasNext();) {
            let rm = index.next();
            {
                const man: Manifestation = rm.getManifestation();
                if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                    const loc: AlgebraicVector = man.getLocation();
                    vertices.add(loc);
                } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                    ++numStruts;
                } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                    const panel: Panel = <Panel><any>man;
                    const corners: java.util.ArrayList<AlgebraicVector> = <any>(new java.util.ArrayList<any>(panel.getVertexCount()));
                    for(let index=panel.iterator();index.hasNext();) {
                        let vertex = index.next();
                        {
                            corners.add(vertex);
                        }
                    }
                    vertices.addAll(corners);
                    const cornerArray: AlgebraicVector[] = (s => { let a=[]; while(s-->0) a.push(null); return a; })(corners.size());
                    corners.toArray<any>(cornerArray);
                    panelVertices.add(cornerArray);
                }
            }
        }
        const sortedVertexList: java.util.ArrayList<AlgebraicVector> = <any>(new java.util.ArrayList<any>(vertices));
        vertices = null;
        this.output = new PrintWriter(writer);
        this.output.println$java_lang_Object("OFF");
        this.output.println$java_lang_Object("# numVertices numFaces numEdges (numEdges is ignored)");
        this.output.println$java_lang_Object(sortedVertexList.size() + " " + panelVertices.size() + " " + numStruts + "\n");
        this.output.println$java_lang_Object("# Vertices.  Each line is the XYZ coordinates of one vertex.");
        for(let index=sortedVertexList.iterator();index.hasNext();) {
            let vector = index.next();
            {
                const dv: number[] = this.mModel.renderVectorDouble(vector);
                this.output.print(OffExporter.FORMAT_$LI$().format(dv[0]) + " ");
                this.output.print(OffExporter.FORMAT_$LI$().format(dv[1]) + " ");
                this.output.print(OffExporter.FORMAT_$LI$().format(dv[2]) + "\n");
            }
        }
        this.output.println$();
        this.output.println$java_lang_Object("# Faces.  numCorners vertexIndex[0] ... vertexIndex[numCorners-1]");
        for(let index=panelVertices.iterator();index.hasNext();) {
            let corners = index.next();
            {
                this.output.print(corners.length);
                for(let index = 0; index < corners.length; index++) {
                    let corner = corners[index];
                    {
                        this.output.print(" " + sortedVertexList.indexOf(corner));
                    }
                }
                this.output.println$();
            }
        }
        this.output.flush();
    }

    /**
     * 
     * @return {string}
     */
    public getFileExtension(): string {
        return "off";
    }
}
OffExporter["__class"] = "com.vzome.core.exporters.OffExporter";

//  Run the Java static initializer eagerly at module load, as the monolithic
//  bundle did.  The lazy _$LI$ accessors are not enough for these classes:
//  e.g. XmlSymmetryFormat registers every format in FORMATS here, and
//  getFormat() reads that map without touching any accessor.
OffExporter.__static_initialize();
