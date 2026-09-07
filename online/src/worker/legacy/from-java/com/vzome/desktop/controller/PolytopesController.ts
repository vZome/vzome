import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../../core/algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../../core/algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../../core/algebra/AlgebraicVector.js";
import { Quaternion } from "../../core/algebra/Quaternion.js";
import { VefVectorExporter } from "../../core/algebra/VefVectorExporter.js";
import { Segment } from "../../core/construction/Segment.js";
import { Context } from "../../core/editor/api/Context.js";
import { ImplicitSymmetryParameters } from "../../core/editor/api/ImplicitSymmetryParameters.js";
import { OrbitSource } from "../../core/editor/api/OrbitSource.js";
import { Axis } from "../../core/math/symmetry/Axis.js";
import { Direction } from "../../core/math/symmetry/Direction.js";
import { WythoffConstruction } from "../../core/math/symmetry/WythoffConstruction.js";
import { Controller } from "../api/Controller.js";
import { DefaultController } from "./DefaultController.js";
import { VectorController } from "./VectorController.js";
import { File } from "../../../../java/io/File.js";

export class PolytopesController extends DefaultController {
    /*private*/ model: ImplicitSymmetryParameters;

    /*private*/ context: Context;

    /*private*/ group: string;

    /*private*/ groups: string[];

    /*private*/ generateEdge: boolean[];

    /*private*/ renderEdge: boolean[];

    /*private*/ edgeScales: AlgebraicNumber[];

    /*private*/ field: AlgebraicField;

    /*private*/ defaultScaleFactor: AlgebraicNumber;

    public constructor(model: ImplicitSymmetryParameters, context: Context) {
        super();
        if (this.model === undefined) { this.model = null; }
        if (this.context === undefined) { this.context = null; }
        this.group = "H4";
        if (this.groups === undefined) { this.groups = null; }
        this.generateEdge = [false, false, false, true];
        this.renderEdge = [true, true, true, true];
        this.edgeScales = [null, null, null, null];
        if (this.field === undefined) { this.field = null; }
        if (this.defaultScaleFactor === undefined) { this.defaultScaleFactor = null; }
        this.model = model;
        this.context = context;
        this.field = model.getRealizedModel().getField();
        this.defaultScaleFactor = this.field['createPower$int'](Direction.USER_SCALE + 2);
        for(let i: number = 0; i < this.edgeScales.length; i++) {{
            this.edgeScales[i] = this.field.one();
        };}
        if (null == this.field.getGoldenRatio()){
            this.groups = ["A4", "B4/C4", "D4", "F4"];
            this.group = "F4";
        } else {
            this.groups = ["A4", "B4/C4", "D4", "F4", "H4"];
            this.group = "H4";
        }
    }

    /**
     * 
     * @param {string} action
     */
    public doAction(action: string) {
        switch((action)) {
        case "setQuaternion":
            const strut: Segment = <Segment>this.model.getSelectedConstruction(Segment);
            if (strut != null){
                let vector: AlgebraicVector = strut.getOffset();
                const symm: OrbitSource = this.model['getSymmetrySystem$']();
                const zone: Axis = symm.getAxis(vector);
                let len: AlgebraicNumber = zone.getLength(vector);
                len = zone.getOrbit().getLengthInUnits(len);
                vector = zone.normal().scale(len);
                const vc: VectorController = <VectorController><any>super.getSubController("quaternion");
                vc.setVector(vector.inflateTo4d$());
            } else {
            }
            return;
        default:
            break;
        }
        if ("generate" === action){
            const index: number = PolytopesController.encodeBits(this.generateEdge);
            const edgesToRender: number = PolytopesController.encodeBits(this.renderEdge);
            const vc: VectorController = <VectorController><any>super.getSubController("quaternion");
            const quaternion: AlgebraicVector = vc.getVector().scale(this.defaultScaleFactor);
            const params: java.util.Map<string, any> = <any>(new java.util.HashMap<any, any>());
            params.put("groupName", this.group);
            params.put("renderGroupName", this.group);
            params.put("index", index);
            params.put("edgesToRender", edgesToRender);
            params.put("edgeScales", this.edgeScales);
            params.put("quaternion", quaternion);
            this.context.doEdit("Polytope4d", params);
        } else if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(action, "setGroup.")){
            const oldGroup: string = this.group;
            this.group = action.substring("setGroup.".length);
            this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("group", oldGroup, this.group);
        } else if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(action, "edge.")){
            const edgeName: string = action.substring("edge.".length);
            const edge: number = javaemul.internal.IntegerHelper.parseInt(edgeName);
            const state: boolean = this.generateEdge[edge];
            this.generateEdge[edge] = !state;
            this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("edge." + edge, state, this.generateEdge[edge]);
        } else if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(action, "render.")){
            const edgeName: string = action.substring("render.".length);
            const edge: number = javaemul.internal.IntegerHelper.parseInt(edgeName);
            const state: boolean = this.renderEdge[edge];
            this.renderEdge[edge] = !state;
        } else super.doAction(action);
    }

    /*private*/ static encodeBits(bits: boolean[]): number {
        let result: number = 0;
        for(let i: number = 0; i < 4; i++) {{
            if (bits[i])result += 1 << i;
        };}
        return result;
    }

    /**
     * 
     * @param {string} command
     * @param {File} file
     */
    public doFileAction(command: string, file: File) {
        try {
            const out: java.io.Writer = new java.io.FileWriter(file);
            try {
                const index: number = PolytopesController.encodeBits(this.generateEdge);
                const edgesToRender: number = PolytopesController.encodeBits(this.renderEdge);
                const vc: VectorController = <VectorController><any>super.getSubController("quaternion");
                let quaternion: AlgebraicVector = vc.getVector().scale(this.defaultScaleFactor);
                quaternion = quaternion.scale(this.field['createPower$int'](-5));
                const rightQuat: Quaternion = new Quaternion(this.field, quaternion);
                const exporter: VefVectorExporter = new VefVectorExporter(out, this.field);
                this.model.get4dSymmetries().constructPolytope(this.group, index, edgesToRender, this.edgeScales, new PolytopesController.PolytopesController$0(this, rightQuat, exporter));
                exporter.finishExport();
            } finally {
                out.close();
            }
        } catch(e) {
            this.mErrors.reportError(Controller.UNKNOWN_ERROR_CODE, [e]);
        }
    }

    /**
     * 
     * @param {string} listName
     * @return {java.lang.String[]}
     */
    public getCommandList(listName: string): string[] {
        return this.groups;
    }

    /**
     * 
     * @param {string} propName
     * @return {string}
     */
    public getProperty(propName: string): string {
        if ("group" === propName){
            return this.group;
        } else if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(propName, "edge.")){
            const edgeName: string = propName.substring("edge.".length);
            const edge: number = javaemul.internal.IntegerHelper.parseInt(edgeName);
            return javaemul.internal.BooleanHelper.toString(this.generateEdge[edge]);
        } else if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(propName, "render.")){
            const edgeName: string = propName.substring("render.".length);
            const edge: number = javaemul.internal.IntegerHelper.parseInt(edgeName);
            return javaemul.internal.BooleanHelper.toString(this.renderEdge[edge]);
        } else return super.getProperty(propName);
    }

    /**
     * 
     * @param {string} name
     * @return {*}
     */
    public getSubController(name: string): Controller {
        switch((name)) {
        default:
            return super.getSubController(name);
        }
    }
}
PolytopesController["__class"] = "com.vzome.desktop.controller.PolytopesController";
PolytopesController["__interfaces"] = ["com.vzome.desktop.api.Controller"];



export namespace PolytopesController {

    export class PolytopesController$0 implements WythoffConstruction.Listener {
        public __parent: any;
        /**
         * 
         * @param {AlgebraicVector} v
         * @return {*}
         */
        public addVertex(v: AlgebraicVector): any {
            const projected: AlgebraicVector = this.rightQuat.leftMultiply(v);
            this.exporter.exportPoint(projected);
            return projected;
        }

        /**
         * 
         * @param {*} p1
         * @param {*} p2
         * @return {*}
         */
        public addEdge(p1: any, p2: any): any {
            this.exporter.exportSegment(<AlgebraicVector>p1, <AlgebraicVector>p2);
            return null;
        }

        /**
         * 
         * @param {java.lang.Object[]} vertices
         * @return {*}
         */
        public addFace(vertices: any[]): any {
            return null;
        }

        constructor(__parent: any, private rightQuat: any, private exporter: any) {
            this.__parent = __parent;
        }
    }
    PolytopesController$0["__interfaces"] = ["com.vzome.core.math.symmetry.WythoffConstruction.Listener"];


}
