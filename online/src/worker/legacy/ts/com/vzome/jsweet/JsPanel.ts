/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.jsweet {
    export class JsPanel extends com.vzome.jsweet.JsManifestation implements com.vzome.core.model.Panel {
        public constructor(field: com.vzome.core.algebra.AlgebraicField, adapter: Object, coords: number[][][]) {
            super(field, adapter, coords);
        }

        /**
         * 
         * @return {com.vzome.core.construction.Construction}
         */
        public toConstruction(): com.vzome.core.construction.Construction {
            const projected: java.util.List<com.vzome.core.construction.Point> = <any>(new java.util.ArrayList<com.vzome.core.construction.Point>());
            for(let i: number = 0; i < this.vectors.length; i++) {{
                const pt: com.vzome.core.algebra.AlgebraicVector = (<com.vzome.jsweet.JsAlgebraicField><any>this.field).createVectorFromTDs(this.vectors[i]);
                projected.add(new com.vzome.core.construction.FreePoint(this.field.projectTo3d(pt, true)));
            };}
            return new com.vzome.core.construction.PolygonFromVertices(projected);
        }

        /**
         * 
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public getLocation(): com.vzome.core.algebra.AlgebraicVector {
            return null;
        }

        public getNormal$(): com.vzome.core.algebra.AlgebraicVector {
            return this.getZoneVector();
        }

        /**
         * 
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public getZoneVector(): com.vzome.core.algebra.AlgebraicVector {
            const v0: com.vzome.core.algebra.AlgebraicVector = (<com.vzome.jsweet.JsAlgebraicField><any>this.field).createVectorFromTDs(this.vectors[0]);
            const v1: com.vzome.core.algebra.AlgebraicVector = (<com.vzome.jsweet.JsAlgebraicField><any>this.field).createVectorFromTDs(this.vectors[1]);
            const v2: com.vzome.core.algebra.AlgebraicVector = (<com.vzome.jsweet.JsAlgebraicField><any>this.field).createVectorFromTDs(this.vectors[2]);
            return com.vzome.core.algebra.AlgebraicVectors.getNormal$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(v0, v1, v2);
        }

        /**
         * 
         * @return {*}
         */
        public iterator(): java.util.Iterator<com.vzome.core.algebra.AlgebraicVector> {
            return new JsPanel.JsPanel$0(this);
        }

        /**
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} vector
         */
        public setZoneVector(vector: com.vzome.core.algebra.AlgebraicVector) {
        }

        /**
         * 
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public getFirstVertex(): com.vzome.core.algebra.AlgebraicVector {
            return (<com.vzome.jsweet.JsAlgebraicField><any>this.field).createVectorFromTDs(this.vectors[0]);
        }

        /**
         * 
         * @return {number}
         */
        public getVertexCount(): number {
            return this.vectors.length;
        }

        /**
         * 
         * @return {*}
         */
        public getQuadrea(): com.vzome.core.algebra.AlgebraicNumber {
            return this.field.one();
        }

        public getNormal$com_vzome_core_math_symmetry_Embedding(embedding: com.vzome.core.math.symmetry.Embedding): com.vzome.core.math.RealVector {
            throw new java.lang.RuntimeException("unimplemented");
        }

        /**
         * 
         * @param {*} embedding
         * @return {com.vzome.core.math.RealVector}
         */
        public getNormal(embedding?: any): any {
            if (((embedding != null && (embedding.constructor != null && embedding.constructor["__interfaces"] != null && embedding.constructor["__interfaces"].indexOf("com.vzome.core.math.symmetry.Embedding") >= 0)) || embedding === null)) {
                return <any>this.getNormal$com_vzome_core_math_symmetry_Embedding(embedding);
            } else if (embedding === undefined) {
                return <any>this.getNormal$();
            } else throw new Error('invalid overload');
        }
    }
    JsPanel["__class"] = "com.vzome.jsweet.JsPanel";
    JsPanel["__interfaces"] = ["com.vzome.core.model.GroupElement","com.vzome.core.model.Panel","com.vzome.core.model.Manifestation","java.lang.Iterable"];



    export namespace JsPanel {

        export class JsPanel$0 implements java.util.Iterator<com.vzome.core.algebra.AlgebraicVector> {
            public __parent: any;
            i: number;

            /**
             * 
             * @return {boolean}
             */
            public hasNext(): boolean {
                return this.i < this.__parent.vectors.length;
            }

            /**
             * 
             * @return {com.vzome.core.algebra.AlgebraicVector}
             */
            public next(): com.vzome.core.algebra.AlgebraicVector {
                return (<com.vzome.jsweet.JsAlgebraicField><any>this.__parent.field).createVectorFromTDs(this.__parent.vectors[this.i++]);
            }

            constructor(__parent: any) {
                this.__parent = __parent;
                this.i = 0;
            }
        }
        JsPanel$0["__interfaces"] = ["java.util.Iterator"];


    }

}

