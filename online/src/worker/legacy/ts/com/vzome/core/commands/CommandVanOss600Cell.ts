/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    export class CommandVanOss600Cell extends com.vzome.core.commands.CommandImportVEFData {
        /**
         * 
         * @param {com.vzome.core.construction.ConstructionList} parameters
         * @param {com.vzome.core.commands.AttributeMap} attributes
         * @param {*} effects
         * @return {com.vzome.core.construction.ConstructionList}
         */
        public apply(parameters: com.vzome.core.construction.ConstructionList, attributes: com.vzome.core.commands.AttributeMap, effects: com.vzome.core.construction.ConstructionChanges): com.vzome.core.construction.ConstructionList {
            try {
                const input: java.io.InputStream = (<any>this.constructor).getClassLoader().getResourceAsStream("com/vzome/core/commands/600cell.vef");
                const out: java.io.ByteArrayOutputStream = new java.io.ByteArrayOutputStream();
                const buf: number[] = (s => { let a=[]; while(s-->0) a.push(0); return a; })(1024);
                let num: number;
                while(((num = input.read(buf, 0, 1024)) > 0)) {out.write(buf, 0, num)};
                const vefData: string = <string>new String(out.toByteArray());
                const result: com.vzome.core.construction.ConstructionList = new com.vzome.core.construction.ConstructionList();
                let field: com.vzome.core.algebra.AlgebraicField = <com.vzome.core.algebra.AlgebraicField><any>attributes.get(com.vzome.core.commands.CommandImportVEFData.FIELD_ATTR_NAME);
                if (field == null)field = <com.vzome.core.algebra.AlgebraicField><any>attributes.get(com.vzome.core.commands.Command.FIELD_ATTR_NAME);
                new CommandVanOss600Cell.VefToModel(this, null, effects).parseVEF(vefData, field);
                return result;
            } catch(exc) {
                throw new com.vzome.core.commands.Command.Failure(exc);
            }
        }

        constructor() {
            super();
        }
    }
    CommandVanOss600Cell["__class"] = "com.vzome.core.commands.CommandVanOss600Cell";
    CommandVanOss600Cell["__interfaces"] = ["com.vzome.core.commands.Command"];



    export namespace CommandVanOss600Cell {

        export class VefToModel extends com.vzome.core.math.VefParser {
            public __parent: any;
            mProjection: com.vzome.core.math.QuaternionProjection;

            mVertices: com.vzome.core.construction.Point[];

            mEffects: com.vzome.core.construction.ConstructionChanges;

            mLocations: com.vzome.core.algebra.AlgebraicVector[];

            public constructor(__parent: any, quaternion: com.vzome.core.algebra.Quaternion, effects: com.vzome.core.construction.ConstructionChanges) {
                super();
                this.__parent = __parent;
                if (this.mProjection === undefined) { this.mProjection = null; }
                if (this.mVertices === undefined) { this.mVertices = null; }
                if (this.mEffects === undefined) { this.mEffects = null; }
                if (this.mLocations === undefined) { this.mLocations = null; }
                this.mEffects = effects;
            }

            /**
             * 
             * @param {number} numVertices
             */
            startVertices(numVertices: number) {
                this.mVertices = (s => { let a=[]; while(s-->0) a.push(null); return a; })(numVertices);
                this.mLocations = (s => { let a=[]; while(s-->0) a.push(null); return a; })(numVertices);
                this.mProjection = null;
            }

            /**
             * 
             * @param {number} index
             * @param {com.vzome.core.algebra.AlgebraicVector} location
             */
            addVertex(index: number, location: com.vzome.core.algebra.AlgebraicVector) {
                this.mLocations[index] = location;
            }

            /**
             * 
             */
            endVertices() {
                const field: com.vzome.core.algebra.AlgebraicField = this.getField();
                const half: com.vzome.core.algebra.AlgebraicNumber = field['createRational$long$long'](1, 2);
                const quarter: com.vzome.core.algebra.AlgebraicNumber = field['createRational$long$long'](1, 4);
                const centroid: com.vzome.core.algebra.AlgebraicVector = this.mLocations[0].plus(this.mLocations[48]).plus(this.mLocations[50]).plus(this.mLocations[64]).scale(quarter);
                const edgeCenter: com.vzome.core.algebra.AlgebraicVector = this.mLocations[0].plus(this.mLocations[48]).scale(half);
                const vertex: com.vzome.core.algebra.AlgebraicVector = this.mLocations[50];
                const edgeToVertex: com.vzome.core.algebra.AlgebraicVector = vertex.minus(edgeCenter);
                const edgeToCenter: com.vzome.core.algebra.AlgebraicVector = centroid.minus(edgeCenter);
                const symmCenter1: com.vzome.core.algebra.AlgebraicVector = edgeCenter.plus(edgeToCenter.scale(field['createAlgebraicNumber$int$int$int$int'](0, 3, 5, 0)));
                const symmCenter2: com.vzome.core.algebra.AlgebraicVector = edgeCenter.plus(edgeToVertex.scale(field['createAlgebraicNumber$int$int$int$int'](0, 2, 5, 0)));
                const direction: com.vzome.core.algebra.AlgebraicVector = symmCenter2.minus(symmCenter1);
                const target: com.vzome.core.algebra.AlgebraicVector = symmCenter1.plus(direction.scale(field['createAlgebraicNumber$int$int$int$int'](0, 1, 1, 0)));
                this.mProjection = new com.vzome.core.math.QuaternionProjection(field, null, target);
                const power5: com.vzome.core.algebra.AlgebraicNumber = field['createPower$int'](5);
                for(let i: number = 0; i < this.mLocations.length; i++) {{
                    let location: com.vzome.core.algebra.AlgebraicVector = this.mLocations[i].scale(power5);
                    location = this.mProjection.projectImage(location, this.wFirst());
                    this.mVertices[i] = new com.vzome.core.construction.FreePoint(location);
                    this.mEffects['constructionAdded$com_vzome_core_construction_Construction'](this.mVertices[i]);
                };}
            }

            /**
             * 
             * @param {number} index
             * @param {number} v1
             * @param {number} v2
             */
            addEdge(index: number, v1: number, v2: number) {
                const p1: com.vzome.core.construction.Point = this.mVertices[v1];
                const p2: com.vzome.core.construction.Point = this.mVertices[v2];
                if (p1 == null || p2 == null){
                    console.info("skipping " + v1 + " " + v2);
                    return;
                }
                const seg: com.vzome.core.construction.Segment = new com.vzome.core.construction.SegmentJoiningPoints(p1, p2);
                this.mEffects['constructionAdded$com_vzome_core_construction_Construction'](seg);
            }

            /**
             * 
             * @param {number} numEdges
             */
            startEdges(numEdges: number) {
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
        }
        VefToModel["__class"] = "com.vzome.core.commands.CommandVanOss600Cell.VefToModel";

    }

}

