import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { AlgebraicMatrix } from "../algebra/AlgebraicMatrix.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Color } from "../construction/Color.js";
import { GeometryExporter } from "./GeometryExporter.js";
import { Polyhedron } from "../math/Polyhedron.js";
import { RealVector } from "../math/RealVector.js";
import { File } from "../../../../java/io/File.js";
import { PrintWriter } from "../../../../java/io/PrintWriter.js";

/**
 * @author vorth
 * @class
 * @extends GeometryExporter
 */
export class VRMLExporter extends GeometryExporter {
    static PREAMBLE_FILE: string = "com/vzome/core/exporters/vrml/preamble.wrl";

    static SCALE: number = 0.35;

    /*private*/ exportColor(name: string, color: Color) {
        this.output.println$java_lang_Object("PROTO " + name + " [] {");
        this.output.print("    Appearance { material Material { diffuseColor ");
        const rgb: number[] = color.getRGBColorComponents([0, 0, 0]);
        this.output.print(rgb[0] + " ");
        this.output.print(rgb[1] + " ");
        this.output.print(rgb[2]);
        this.output.println$java_lang_Object(" }}}");
    }

    /**
     * 
     * @param {File} directory
     * @param {java.io.Writer} writer
     * @param {number} height
     * @param {number} width
     */
    public doExport(directory: File, writer: java.io.Writer, height: number, width: number) {
        this.output = new PrintWriter(writer);
        this.output.println$java_lang_Object(this.getBoilerplate(VRMLExporter.PREAMBLE_FILE));
        this.output.println$();
        let field: AlgebraicField = null;
        const instances: java.lang.StringBuffer = new java.lang.StringBuffer();
        let numShapes: number = 0;
        const shapes: java.util.HashMap<Polyhedron, string> = <any>(new java.util.HashMap<any, any>());
        const colors: java.util.Map<Color, string> = <any>(new java.util.HashMap<any, any>());
        for(let index=this.mModel.iterator();index.hasNext();) {
            let rm = index.next();
            {
                const shape: Polyhedron = rm.getShape();
                if (field == null)field = shape.getField();
                let shapeName: string = shapes.get(shape);
                if (shapeName == null){
                    shapeName = "shape" + numShapes++;
                    shapes.put(shape, shapeName);
                    this.exportShape(shapeName, shape);
                }
                const transform: AlgebraicMatrix = rm.getOrientation();
                const mx: RealVector = this.mModel.renderVector(transform.timesRow(field.basisVector(3, AlgebraicVector.X)));
                const my: RealVector = this.mModel.renderVector(transform.timesRow(field.basisVector(3, AlgebraicVector.Y)));
                const mz: RealVector = this.mModel.renderVector(transform.timesRow(field.basisVector(3, AlgebraicVector.Z)));
                let x: number = ((<any>Math).fround(mz.y - my.z));
                let y: number = ((<any>Math).fround(mx.z - mz.x));
                let z: number = ((<any>Math).fround(my.x - mx.y));
                const cos: number = ((<any>Math).fround((<any>Math).fround(mx.x + my.y) + mz.z) - 1.0) * 0.5;
                const sin: number = 0.5 * Math.sqrt(x * x + y * y + z * z);
                const angle: number = (<any>Math).fround(Math.atan2(sin, cos));
                if (Math.abs(angle - Math.PI) < 1.0E-5){
                    if ((mx.x >= my.y) && (mx.x >= mz.z)){
                        x = Math.sqrt((<any>Math).fround((<any>Math).fround(mx.x - my.y) - mz.z) + 1.0) * 0.5;
                        y = mx.y / (2.0 * x);
                        z = mx.z / (2.0 * x);
                    } else if ((my.y >= mz.z) && (my.y >= mx.x)){
                        y = Math.sqrt((<any>Math).fround((<any>Math).fround(my.y - mx.x) - mz.z) + 1.0) * 0.5;
                        x = mx.y / (2.0 * y);
                        z = my.z / (2.0 * y);
                    } else {
                        z = Math.sqrt((<any>Math).fround((<any>Math).fround(mz.z - my.y) - mx.x) + 1.0) * 0.5;
                        x = mx.z / (2.0 * z);
                        y = my.z / (2.0 * z);
                    }
                }
                let color: Color = rm.getColor();
                if (color == null){
                    color = Color.WHITE_$LI$();
                }
                let colorName: string = colors.get(color);
                if (colorName == null){
                    colorName = "color_" + /* replace */color.toString().split(',').join('_');
                    colors.put(color, colorName);
                    this.exportColor(colorName, color);
                }
                instances.append("Transform { translation ");
                instances.append(rm.getLocation().scale(VRMLExporter.SCALE).spacedString());
                instances.append(" rotation " + x + " " + y + " " + z + " " + angle);
                instances.append(" children[ Shape{ geometry " + shapeName + "{} appearance " + colorName + "{}}]}\n");
            }
        }
        this.output.println$java_lang_Object(instances.toString());
        this.output.flush();
        this.output.close();
    }

    /*private*/ exportShape(shapeName: string, poly: Polyhedron) {
        this.output.println$java_lang_Object("PROTO " + shapeName + " [] { IndexedFaceSet{ solid FALSE convex FALSE colorPerVertex FALSE");
        this.output.println$java_lang_Object("   coord Coordinate{ point [");
        for(let index=poly.getVertexList().iterator();index.hasNext();) {
            let gv = index.next();
            {
                const v: RealVector = this.mModel.renderVector(gv);
                this.output.println$java_lang_Object(v.scale(VRMLExporter.SCALE).spacedString() + ",");
            }
        }
        this.output.println$java_lang_Object("] } coordIndex [");
        for(let index=poly.getFaceSet().iterator();index.hasNext();) {
            let face = index.next();
            {
                const arity: number = face.size();
                for(let j: number = 0; j < arity; j++) {{
                    const index: number = face.get(j);
                    this.output.print(index + ", ");
                };}
                this.output.println$java_lang_Object("-1,");
            }
        }
        this.output.println$java_lang_Object("]}}");
        this.output.flush();
    }

    /**
     * 
     * @return {string}
     */
    public getFileExtension(): string {
        return "wrl";
    }
}
VRMLExporter["__class"] = "com.vzome.core.exporters.VRMLExporter";
