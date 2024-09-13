/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.desktop.controller {
    export class PolytopesController extends com.vzome.desktop.controller.DefaultController {
        /*private*/ model: com.vzome.core.editor.api.ImplicitSymmetryParameters;

        /*private*/ context: com.vzome.core.editor.api.Context;

        /*private*/ group: string;

        /*private*/ groups: string[];

        /*private*/ generateEdge: boolean[];

        /*private*/ renderEdge: boolean[];

        /*private*/ edgeScales: com.vzome.core.algebra.AlgebraicNumber[];

        /*private*/ field: com.vzome.core.algebra.AlgebraicField;

        /*private*/ defaultScaleFactor: com.vzome.core.algebra.AlgebraicNumber;

        public constructor(model: com.vzome.core.editor.api.ImplicitSymmetryParameters, context: com.vzome.core.editor.api.Context) {
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
            this.defaultScaleFactor = this.field['createPower$int'](com.vzome.core.math.symmetry.Direction.USER_SCALE + 2);
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
                const strut: com.vzome.core.construction.Segment = <com.vzome.core.construction.Segment>this.model.getSelectedConstruction(com.vzome.core.construction.Segment);
                if (strut != null){
                    let vector: com.vzome.core.algebra.AlgebraicVector = strut.getOffset();
                    const symm: com.vzome.core.editor.api.OrbitSource = this.model['getSymmetrySystem$']();
                    const zone: com.vzome.core.math.symmetry.Axis = symm.getAxis(vector);
                    let len: com.vzome.core.algebra.AlgebraicNumber = zone.getLength(vector);
                    len = zone.getOrbit().getLengthInUnits(len);
                    vector = zone.normal().scale(len);
                    const vc: com.vzome.desktop.controller.VectorController = <com.vzome.desktop.controller.VectorController><any>super.getSubController("quaternion");
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
                const vc: com.vzome.desktop.controller.VectorController = <com.vzome.desktop.controller.VectorController><any>super.getSubController("quaternion");
                const quaternion: com.vzome.core.algebra.AlgebraicVector = vc.getVector().scale(this.defaultScaleFactor);
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
         * @param {java.io.File} file
         */
        public doFileAction(command: string, file: java.io.File) {
            try {
                const out: java.io.Writer = new java.io.FileWriter(file);
                try {
                    const index: number = PolytopesController.encodeBits(this.generateEdge);
                    const edgesToRender: number = PolytopesController.encodeBits(this.renderEdge);
                    const vc: com.vzome.desktop.controller.VectorController = <com.vzome.desktop.controller.VectorController><any>super.getSubController("quaternion");
                    let quaternion: com.vzome.core.algebra.AlgebraicVector = vc.getVector().scale(this.defaultScaleFactor);
                    quaternion = quaternion.scale(this.field['createPower$int'](-5));
                    const rightQuat: com.vzome.core.algebra.Quaternion = new com.vzome.core.algebra.Quaternion(this.field, quaternion);
                    const exporter: com.vzome.core.algebra.VefVectorExporter = new com.vzome.core.algebra.VefVectorExporter(out, this.field);
                    this.model.get4dSymmetries().constructPolytope(this.group, index, edgesToRender, this.edgeScales, new PolytopesController.PolytopesController$0(this, rightQuat, exporter));
                    exporter.finishExport();
                } finally {
                    out.close();
                }
            } catch(e) {
                this.mErrors.reportError(com.vzome.desktop.api.Controller.UNKNOWN_ERROR_CODE, [e]);
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
        public getSubController(name: string): com.vzome.desktop.api.Controller {
            switch((name)) {
            default:
                return super.getSubController(name);
            }
        }
    }
    PolytopesController["__class"] = "com.vzome.desktop.controller.PolytopesController";
    PolytopesController["__interfaces"] = ["com.vzome.desktop.api.Controller"];



    export namespace PolytopesController {

        export class PolytopesController$0 implements com.vzome.core.math.symmetry.WythoffConstruction.Listener {
            public __parent: any;
            /**
             * 
             * @param {com.vzome.core.algebra.AlgebraicVector} v
             * @return {*}
             */
            public addVertex(v: com.vzome.core.algebra.AlgebraicVector): any {
                const projected: com.vzome.core.algebra.AlgebraicVector = this.rightQuat.leftMultiply(v);
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
                this.exporter.exportSegment(<com.vzome.core.algebra.AlgebraicVector>p1, <com.vzome.core.algebra.AlgebraicVector>p2);
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

}

