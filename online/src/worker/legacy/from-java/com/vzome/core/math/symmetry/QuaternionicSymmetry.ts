import { java, javaemul } from "../../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../../algebra/AlgebraicField.js";
import { AlgebraicNumber } from "../../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../../algebra/AlgebraicVector.js";
import { Quaternion } from "../../algebra/Quaternion.js";
import { VefParser } from "../VefParser.js";
import { ResourceLoader } from "../../../xml/ResourceLoader.js";

/**
 * @author Scott Vorthmann
 * @param {string} name
 * @param {string} rootsResource
 * @param {*} field
 * @class
 */
export class QuaternionicSymmetry {
    /*private*/ mRoots: Quaternion[];

    /*private*/ mName: string;

    public constructor(name: string, rootsResource: string, field: AlgebraicField) {
        if (this.mRoots === undefined) { this.mRoots = null; }
        if (this.mName === undefined) { this.mName = null; }
        this.mName = name;
        const vefData: string = ResourceLoader.loadStringResource(rootsResource);
        const parser: QuaternionicSymmetry.RootParser = new QuaternionicSymmetry.RootParser(field);
        parser.parseVEF(vefData, field);
        this.mRoots = parser.getQuaternions();
    }

    public getRoots(): Quaternion[] {
        return this.mRoots;
    }

    public getName(): string {
        return this.mName;
    }
}
QuaternionicSymmetry["__class"] = "com.vzome.core.math.symmetry.QuaternionicSymmetry";


export namespace QuaternionicSymmetry {

    export class RootParser extends VefParser {
        mRoots: Quaternion[];

        __com_vzome_core_math_symmetry_QuaternionicSymmetry_RootParser_field: AlgebraicField;

        constructor(field: AlgebraicField) {
            super();
            if (this.mRoots === undefined) { this.mRoots = null; }
            if (this.__com_vzome_core_math_symmetry_QuaternionicSymmetry_RootParser_field === undefined) { this.__com_vzome_core_math_symmetry_QuaternionicSymmetry_RootParser_field = null; }
            if (this.HALF === undefined) { this.HALF = null; }
            this.__com_vzome_core_math_symmetry_QuaternionicSymmetry_RootParser_field = field;
            this.HALF = field['createRational$long$long'](1, 2);
        }

        /**
         * 
         * @param {number} numVertices
         */
        startVertices(numVertices: number) {
            this.mRoots = (s => { let a=[]; while(s-->0) a.push(null); return a; })(numVertices);
        }

        public getQuaternions(): Quaternion[] {
            return this.mRoots;
        }

        HALF: AlgebraicNumber;

        /**
         * 
         * @param {number} index
         * @param {AlgebraicVector} location
         */
        addVertex(index: number, location: AlgebraicVector) {
            this.mRoots[index] = new Quaternion(this.__com_vzome_core_math_symmetry_QuaternionicSymmetry_RootParser_field, location.scale(this.HALF));
        }

        /**
         * 
         * @param {number} numEdges
         */
        startEdges(numEdges: number) {
        }

        /**
         * 
         * @param {number} index
         * @param {number} v1
         * @param {number} v2
         */
        addEdge(index: number, v1: number, v2: number) {
        }

        /**
         * 
         * @param {number} numFaces
         */
        startFaces(numFaces: number) {
        }

        /**
         * 
         * @param {number} index
         * @param {int[]} verts
         */
        addFace(index: number, verts: number[]) {
        }

        /**
         * 
         * @param {number} index
         * @param {number} vertex
         */
        addBall(index: number, vertex: number) {
        }

        /**
         * 
         * @param {number} numVertices
         */
        startBalls(numVertices: number) {
        }

        /**
         * 
         * @param {java.util.StringTokenizer} tokens
         */
        endFile(tokens: java.util.StringTokenizer) {
        }
    }
    RootParser["__class"] = "com.vzome.core.math.symmetry.QuaternionicSymmetry.RootParser";

}
