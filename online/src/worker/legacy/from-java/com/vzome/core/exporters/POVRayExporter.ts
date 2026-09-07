import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicMatrix } from "../algebra/AlgebraicMatrix.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Color } from "../construction/Color.js";
import { DocumentExporter } from "./DocumentExporter.js";
import { Polyhedron } from "../math/Polyhedron.js";
import { RealVector } from "../math/RealVector.js";
import { Embedding } from "../math/symmetry/Embedding.js";
import { CameraIntf } from "../viewing/CameraIntf.js";
import { ResourceLoader } from "../../xml/ResourceLoader.js";
import { File } from "../../../../java/io/File.js";
import { PrintWriter } from "../../../../java/io/PrintWriter.js";
import { NumberFormat } from "../../../../java/text/NumberFormat.js";

/**
 * Renders out to POV-Ray using #declare statements to reuse geometry.
 * @author vorth
 * @class
 * @extends DocumentExporter
 */
export class POVRayExporter extends DocumentExporter {
    static FORMAT: NumberFormat; public static FORMAT_$LI$(): NumberFormat { if (POVRayExporter.FORMAT == null) { POVRayExporter.FORMAT = NumberFormat.getNumberInstance(java.util.Locale.US); }  return POVRayExporter.FORMAT; }

    static PREAMBLE_FILE: string = "com/vzome/core/exporters/povray/preamble.pov";

    public mapViewToWorld(view: CameraIntf, vector: RealVector) {
    }

    /**
     * 
     * @return {boolean}
     */
    public needsManifestations(): boolean {
        return false;
    }

    /**
     * 
     * @param {File} povFile
     * @param {java.io.Writer} writer
     * @param {number} height
     * @param {number} width
     */
    public doExport(povFile: File, writer: java.io.Writer, height: number, width: number) {
        this.output = new PrintWriter(writer);
        const lookDir: RealVector = this.mScene.getLookDirectionRV();
        const upDir: RealVector = this.mScene.getUpDirectionRV();
        POVRayExporter.FORMAT_$LI$().setMaximumFractionDigits(8);
        this.output.println$();
        this.output.println$();
        this.output.println$java_lang_Object("#declare           look_dir = " + this.printTuple3d(lookDir) + ";");
        this.output.println$();
        this.output.println$java_lang_Object("#declare             up_dir = " + this.printTuple3d(upDir) + ";");
        this.output.println$();
        this.output.println$java_lang_Object("#declare viewpoint_distance = " + this.mScene.getViewDistance() + ";");
        this.output.println$();
        this.output.println$java_lang_Object("#declare      look_at_point = " + this.printTuple3d(this.mScene.getLookAtPointRV()) + ";");
        this.output.println$();
        this.output.println$java_lang_Object("#declare      field_of_view = " + this.mScene.getFieldOfView() + ";");
        this.output.println$();
        this.output.println$java_lang_Object("#declare      parallel_proj = " + (this.mScene.isPerspective() ? 0 : 1) + ";");
        this.output.println$();
        const preamble: string = ResourceLoader.loadStringResource(POVRayExporter.PREAMBLE_FILE);
        this.output.println$java_lang_Object(preamble);
        this.output.println$();
        for(let i: number = 0; i < 3; i++) {{
            const color: Color = this.mLights.getDirectionalLightColor(i);
            let rv: RealVector = this.mLights.getDirectionalLightVector(i);
            rv = this.mScene.mapViewToWorld(rv);
            this.output.print("light_source { -light_distance * " + this.printTuple3d(rv));
            this.output.print(" ");
            this.printColor(color);
            this.output.println$java_lang_Object(" * multiplier_light_" + (i + 1) + " }");
            this.output.println$();
        };}
        this.output.print("#declare ambient_color = ");
        this.printColor(this.mLights.getAmbientColor());
        this.output.println$java_lang_Object(";");
        this.output.println$();
        this.output.println$java_lang_Object("#default { texture { finish { phong 0.3 ambient multiplier_ambient * ambient_color diffuse 0.6 } } }");
        this.output.println$();
        this.output.print("background { ");
        this.printColor(this.mLights.getBackgroundColor());
        this.output.println$java_lang_Object(" }");
        this.output.println$();
        const instances: java.lang.StringBuffer = new java.lang.StringBuffer();
        const field: AlgebraicField = this.mModel.getField();
        const embedding: Embedding = this.mModel.getEmbedding();
        let embeddingTransform: string = " ";
        if (!embedding.isTrivial()){
            embeddingTransform = " transform embedding ";
            this.output.print("#declare embedding = transform { matrix < ");
            for(let i: number = 0; i < 3; i++) {{
                const columnSelect: AlgebraicVector = field.basisVector(3, i);
                const columnI: RealVector = embedding.embedInR3(columnSelect);
                //  Through FORMAT, like every other number here: printing the raw float wrote
                //  Java's "1.0, 0.0, 0.04486483" where Javascript wrote the full double,
                //  "1, 0, 0.04486482962965965", so this one line differed on every embedded
                //  design even when the geometry was identical.
                this.output.print(POVRayExporter.FORMAT_$LI$().format(columnI.x));
                this.output.print(", ");
                this.output.print(POVRayExporter.FORMAT_$LI$().format(columnI.y));
                this.output.print(", ");
                this.output.print(POVRayExporter.FORMAT_$LI$().format(columnI.z));
                this.output.print(", ");
            };}
            this.output.println$java_lang_Object(" 0, 0, 0 > }");
            this.output.flush();
        }
        let numTransforms: number = 0;
        const shapes: java.util.HashSet<string> = <any>(new java.util.HashSet<any>());
        //  Keyed on the string form, NOT the object.  This deliberately DIVERGES from the
        //  Java, which keys these maps on the AlgebraicMatrix / Color itself -- correct
        //  there, because those have value-based equals/hashCode.  The transpiled
        //  java.util.HashMap compares by reference instead, so equal matrices each got
        //  their own transN declaration (every identity orientation, for instance),
        //  bloating the output and diverging from Java's.  Do not "fix" this back toward
        //  the Java: the two sides must differ here to produce the same file.
        const transforms: java.util.Map<string, string> = <any>(new java.util.HashMap<any, any>());
        const colors: java.util.Map<string, string> = <any>(new java.util.HashMap<any, any>());
        //  Emit in a deterministic order.  RenderedModel holds its manifestations in a
        //  HashSet keyed on a random per-object guid, so iteration order varied run to
        //  run: the object lines moved, and with them the lazily generated trans0/trans1
        //  names, so exporting one design twice produced two different files.  Sorting by
        //  content -- never by guid -- makes two runs, and the two platforms, agree.
        //  Mirrors POVRayExporter.compareForExport in core/.
        const sorted: any[] = [];
        for(let index=this.mModel.iterator();index.hasNext();) {
            sorted.push(index.next());
        }
        sorted.sort(POVRayExporter.compareForExport);
        for(let sortedIndex = 0; sortedIndex < sorted.length; sortedIndex++) {
            let rm = sorted[sortedIndex];
            {
                const shapeName: string = "S" + /* replaceAll */rm.getShapeKey().replace(new RegExp("[^A-Za-z0-9_]", 'g'),"_");
                if (!shapes.contains(shapeName)){
                    shapes.add(shapeName);
                    this.exportShape(shapeName, rm.getShape());
                }
                const transform: AlgebraicMatrix = rm.getOrientation();
                const transformKey: string = "" + transform;
                let transformName: string = transforms.get(transformKey);
                if (transformName == null){
                    transformName = "trans" + numTransforms++;
                    transforms.put(transformKey, transformName);
                    this.exportTransform(transformName, transform);
                }
                let color: Color = rm.getColor();
                if (color == null)color = Color.WHITE_$LI$();
                const colorKey: string = "" + color;
                let colorName: string = colors.get(colorKey);
                if (colorName == null){
                    colorName = this.nameColor(color);
                    colors.put(colorKey, colorName);
                    this.exportColor(colorName, color);
                }
                instances.append("object { " + shapeName + " transform " + transformName + " translate ");
                instances.append("(<");
                let loc: AlgebraicVector = rm.getLocationAV();
                if (loc == null)loc = rm.getShape().getField().origin(3);
                this.appendVector(loc, instances);
                instances.append(">)");
                instances.append(embeddingTransform + "transform anim texture { " + colorName + " } }");
                instances.append(java.lang.System.getProperty("line.separator"));
            }
        }
        this.output.println$java_lang_Object(instances.toString());
        this.output.flush();
        if (povFile == null)return;
        let filename: string = povFile.getName();
        const index: number = filename.lastIndexOf(".pov");
        if (index > 0){
            filename = filename.substring(0, index);
        }
        const file: File = new File(povFile.getParentFile(), filename + ".ini");
        this.output = new PrintWriter(new java.io.FileWriter(file));
        this.output.println$java_lang_Object("+W" + 600);
        this.output.println$java_lang_Object("+H" + 600);
        this.output.println$java_lang_Object("+A");
        this.output.println$java_lang_Object("Input_File_Name=" + filename + ".pov");
        this.output.println$java_lang_Object("Output_File_Name=" + filename + ".png");
        this.output.close();
    }

    nameColor(color: Color): string {
        return "color_" + /* replace */color.toString().split(',').join('_');
    }

    /*private*/ printTuple3d(t: RealVector): string {
        const buf: java.lang.StringBuilder = new java.lang.StringBuilder("<");
        buf.append(POVRayExporter.FORMAT_$LI$().format(t.x));
        buf.append(",");
        buf.append(POVRayExporter.FORMAT_$LI$().format(t.y));
        buf.append(",");
        buf.append(POVRayExporter.FORMAT_$LI$().format(t.z));
        buf.append(">");
        return buf.toString();
    }

    exportColor(name: string, color: Color) {
        this.output.print("#declare " + /* replace */name.split('.').join('_') + " = texture { pigment { ");
        this.printColor(color);
        this.output.println$java_lang_Object(" } };");
    }

    /*private*/ printColor(color: Color) {
        const doAlpha: boolean = color.getAlpha() < 255;
        if (doAlpha)this.output.print("color rgbf <"); else this.output.print("color rgb <");
        const rgb: number[] = color.getRGBColorComponents([0, 0, 0, 0]);
        this.output.print(POVRayExporter.FORMAT_$LI$().format(rgb[0]) + ",");
        this.output.print(POVRayExporter.FORMAT_$LI$().format(rgb[1]) + ",");
        if (doAlpha){
            this.output.print(POVRayExporter.FORMAT_$LI$().format(rgb[2]) + ",");
            this.output.print(POVRayExporter.FORMAT_$LI$().format(rgb[3]));
        } else {
            this.output.print(POVRayExporter.FORMAT_$LI$().format(rgb[2]));
        }
        this.output.print(">");
    }

    appendVector(loc: AlgebraicVector, buf: java.lang.StringBuffer) {
        const vector: RealVector = loc.toRealVector();
        buf.append(POVRayExporter.FORMAT_$LI$().format(vector.x));
        buf.append(", ");
        buf.append(POVRayExporter.FORMAT_$LI$().format(vector.y));
        buf.append(", ");
        buf.append(POVRayExporter.FORMAT_$LI$().format(vector.z));
    }

    //  The four keys together are exactly what the exporter emits per instance, so ties
    //  are only possible between manifestations that would emit identical lines anyway.
    /*private*/ static compareForExport(a: any, b: any): number {
        let comparison: number = POVRayExporter.nullSafeCompare(a.getShapeKey(), b.getShapeKey());
        if (comparison !== 0) return comparison;

        const aOrientation: AlgebraicMatrix = a.getOrientation();
        const bOrientation: AlgebraicMatrix = b.getOrientation();
        comparison = POVRayExporter.nullSafeCompare(
                aOrientation == null ? null : aOrientation.toString(),
                bOrientation == null ? null : bOrientation.toString());
        if (comparison !== 0) return comparison;

        const aLocation: AlgebraicVector = a.getLocationAV();
        const bLocation: AlgebraicVector = b.getLocationAV();
        if (aLocation != null && bLocation != null) {
            comparison = aLocation.compareTo(bLocation);
            if (comparison !== 0) return comparison;
        } else if (aLocation !== bLocation) {
            return aLocation == null ? -1 : 1;
        }

        const aColor: Color = a.getColor();
        const bColor: Color = b.getColor();
        return POVRayExporter.nullSafeCompare(
                aColor == null ? null : aColor.toString(),
                bColor == null ? null : bColor.toString());
    }

    /*private*/ static nullSafeCompare(a: string, b: string): number {
        if (a == null) return b == null ? 0 : -1;
        if (b == null) return 1;
        return a < b ? -1 : (a > b ? 1 : 0);
    }

    /*private*/ exportShape(shapeName: string, poly: Polyhedron) {
        this.output.print("#declare " + shapeName + " = ");
        const vertices: java.util.List<AlgebraicVector> = poly.getVertexList();
        this.output.println$java_lang_Object("mesh {");
        //  Emit the triangles in a stable order.  Polyhedron holds its faces in a HashSet,
        //  so the order they come out in is an artifact of the container and differs
        //  between Java and the transpiled Javascript (whose HashSet is backed by an
        //  insertion-ordered JS Map).  The triangles are identical either way, so sorting
        //  by the text we are about to write makes both platforms produce the same file
        //  without changing any geometry.
        const triangles: string[] = [];
        for(let index=poly.getTriangleFaces().iterator();index.hasNext();) {
            let face = index.next();
            {
                const buf: java.lang.StringBuffer = new java.lang.StringBuffer("triangle {");
                for(let loopIndex = 0; loopIndex < face.vertices.length; loopIndex++) {
                    let index = face.vertices[loopIndex];
                    {
                        const loc: AlgebraicVector = vertices.get(index);
                        buf.append("<");
                        this.appendVector(loc, buf);
                        buf.append(">");
                    }
                }
                buf.append("}");
                triangles.push(buf.toString());
            }
        }
        triangles.sort();
        for(let t = 0; t < triangles.length; t++) {
            this.output.println$java_lang_Object(triangles[t]);
        }
        this.output.println$java_lang_Object("}");
        this.output.flush();
    }

    /*private*/ exportTransform(name: string, transform: AlgebraicMatrix) {
        const field: AlgebraicField = this.mModel.getField();
        this.output.print("#declare " + name + " = transform { matrix < ");
        const buf: java.lang.StringBuffer = new java.lang.StringBuffer();
        for(let i: number = 0; i < 3; i++) {{
            const columnSelect: AlgebraicVector = field.basisVector(3, i);
            const columnI: AlgebraicVector = transform.timesColumn(columnSelect);
            this.appendVector(columnI, buf);
            buf.append(", ");
        };}
        this.output.print(buf);
        this.output.println$java_lang_Object(" 0, 0, 0 > }");
        this.output.flush();
    }

    /**
     * 
     * @return {string}
     */
    public getFileExtension(): string {
        return "pov";
    }

    constructor() {
        super();
    }
}
POVRayExporter["__class"] = "com.vzome.core.exporters.POVRayExporter";
POVRayExporter["__interfaces"] = ["com.vzome.core.exporters.DocumentExporterIntf"];
