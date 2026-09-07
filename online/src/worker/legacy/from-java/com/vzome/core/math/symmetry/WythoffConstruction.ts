import { java, javaemul } from "../../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../../algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../../algebra/AlgebraicVector.js";
import { CoxeterGroup } from "./CoxeterGroup.js";
import { PrintWriter } from "../../../../../java/io/PrintWriter.js";

export class WythoffConstruction {
    public static constructPolytope(group: CoxeterGroup, index: number, edgesToRender: number, edgeScales: AlgebraicNumber[], renderingGroup: CoxeterGroup, listener: WythoffConstruction.Listener) {
        const neighbors: AlgebraicVector[] = [null, null, null, null];
        const chiral: boolean = false;
        if (chiral){
            index = edgesToRender = 15;
        }
        const origin: AlgebraicVector = group.getOrigin();
        let model: AlgebraicVector = origin;
        let bits: number = index;
        for(let i: number = 0; i < 4; i++) {{
            if (bits % 2 === 1)model = model.plus(group.getWeight(i).scale(edgeScales[i]));
            bits >>= 1;
        };}
        bits = index;
        for(let i: number = 0; i < 4; i++) {{
            if ((bits % 2 === 1) && (edgesToRender % 2 === 1))neighbors[i] = model.minus(group.getSimpleRoot(i).scale(edgeScales[i])); else neighbors[i] = origin;
            bits >>= 1;
            edgesToRender >>= 1;
        };}
        const order: number = renderingGroup.getOrder();
        if (chiral){
            for(let i: number = 0; i < order; i++) {{
                const vector: AlgebraicVector = renderingGroup.chiralSubgroupAction(model, i);
                if (vector == null)continue;
                for(let e: number = 0; e < 4; e++) {for(let f: number = e + 1; f < 4; f++) {{
                    const v1: AlgebraicVector = renderingGroup.chiralSubgroupAction(neighbors[e], i);
                    const p1: any = listener.addVertex(v1);
                    const v2: AlgebraicVector = renderingGroup.chiralSubgroupAction(neighbors[f], i);
                    const p2: any = listener.addVertex(v2);
                    listener.addEdge(p1, p2);
                };};}
            };}
        } else for(let i: number = 0; i < order; i++) {{
            const vector: AlgebraicVector = renderingGroup.groupAction(model, i);
            const p: any = listener.addVertex(vector);
            for(let e: number = 0; e < 4; e++) {{
                if (neighbors[e].equals(origin))continue;
                const other: AlgebraicVector = renderingGroup.groupAction(neighbors[e], i);
                if (!other.equals(vector)){
                    const p2: any = listener.addVertex(other);
                    listener.addEdge(p, p2);
                }
            };}
        };}
    }
}
WythoffConstruction["__class"] = "com.vzome.core.math.symmetry.WythoffConstruction";


export namespace WythoffConstruction {

    export interface Listener {
        addVertex(v: AlgebraicVector): any;

        addEdge(p1: any, p2: any): any;

        addFace(vertices: any[]): any;
    }

    export class VefPrinter implements WythoffConstruction.Listener {
        vefVertices: java.lang.StringBuffer;

        vefEdges: java.lang.StringBuffer;

        numEdges: number;

        numVertices: number;

        field: AlgebraicField;

        public constructor(field2: AlgebraicField) {
            this.vefVertices = new java.lang.StringBuffer();
            this.vefEdges = new java.lang.StringBuffer();
            this.numEdges = 0;
            this.numVertices = 0;
            if (this.field === undefined) { this.field = null; }
            this.field = field2;
        }

        /**
         * 
         * @param {*} p1
         * @param {*} p2
         * @return {*}
         */
        public addEdge(p1: any, p2: any): any {
            this.vefEdges.append((<number>p1) + "\t" + (<number>p2) + "\n");
            ++this.numEdges;
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

        /**
         * 
         * @param {AlgebraicVector} gv
         * @return {*}
         */
        public addVertex(gv: AlgebraicVector): any {
            gv.getVectorExpression$java_lang_StringBuffer$int(this.vefVertices, AlgebraicField.VEF_FORMAT);
            this.vefVertices.append("\n");
            return this.numVertices++;
        }

        public print(out: PrintWriter) {
            out.println$java_lang_Object("vZome VEF 5");
            out.println$java_lang_Object(this.numVertices);
            out.println$java_lang_Object(this.vefVertices.toString());
            out.println$java_lang_Object(this.numEdges);
            out.println$java_lang_Object(this.vefEdges.toString());
            out.close();
        }
    }
    VefPrinter["__class"] = "com.vzome.core.math.symmetry.WythoffConstruction.VefPrinter";
    VefPrinter["__interfaces"] = ["com.vzome.core.math.symmetry.WythoffConstruction.Listener"];


}
