/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math.symmetry {
    /**
     * @author Scott Vorthmann
     * @param {string} name
     * @param {string} rootsResource
     * @param {*} field
     * @class
     */
    export class QuaternionicSymmetry {
        /*private*/ mRoots: com.vzome.core.algebra.Quaternion[];

        /*private*/ mName: string;

        public constructor(name: string, rootsResource: string, field: com.vzome.core.algebra.AlgebraicField) {
            if (this.mRoots === undefined) { this.mRoots = null; }
            if (this.mName === undefined) { this.mName = null; }
            this.mName = name;
            const vefData: string = com.vzome.xml.ResourceLoader.loadStringResource(rootsResource);
            const parser: QuaternionicSymmetry.RootParser = new QuaternionicSymmetry.RootParser(field);
            parser.parseVEF(vefData, field);
            this.mRoots = parser.getQuaternions();
        }

        public getRoots(): com.vzome.core.algebra.Quaternion[] {
            return this.mRoots;
        }

        public getName(): string {
            return this.mName;
        }
    }
    QuaternionicSymmetry["__class"] = "com.vzome.core.math.symmetry.QuaternionicSymmetry";


    export namespace QuaternionicSymmetry {

        export class RootParser extends com.vzome.core.math.VefParser {
            mRoots: com.vzome.core.algebra.Quaternion[];

            __com_vzome_core_math_symmetry_QuaternionicSymmetry_RootParser_field: com.vzome.core.algebra.AlgebraicField;

            constructor(field: com.vzome.core.algebra.AlgebraicField) {
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

            public getQuaternions(): com.vzome.core.algebra.Quaternion[] {
                return this.mRoots;
            }

            HALF: com.vzome.core.algebra.AlgebraicNumber;

            /**
             * 
             * @param {number} index
             * @param {com.vzome.core.algebra.AlgebraicVector} location
             */
            addVertex(index: number, location: com.vzome.core.algebra.AlgebraicVector) {
                this.mRoots[index] = new com.vzome.core.algebra.Quaternion(this.__com_vzome_core_math_symmetry_QuaternionicSymmetry_RootParser_field, location.scale(this.HALF));
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

}

