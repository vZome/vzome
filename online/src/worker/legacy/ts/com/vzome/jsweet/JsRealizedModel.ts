/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.jsweet {
    export class JsRealizedModel implements com.vzome.core.model.RealizedModel {
        /*private*/ field: com.vzome.core.algebra.AlgebraicField;

        /*private*/ adapter: Object;

        public constructor(field: com.vzome.core.algebra.AlgebraicField, adapter: Object) {
            if (this.field === undefined) { this.field = null; }
            if (this.adapter === undefined) { this.adapter = null; }
            this.field = field;
            this.adapter = adapter;
        }

        setAdapter(adapter: Object) {
            this.adapter = adapter;
        }

        /**
         * 
         * @return {*}
         */
        public iterator(): java.util.Iterator<com.vzome.core.model.Manifestation> {
            const f: Function = <any>(this.adapter["allIterator"]);
            const jSiterator: Iterator<number[][][]> = <Iterator<number[][][]>>f.apply(this.adapter);
            return new JsRealizedModel.JsRealizedModel$0(this, jSiterator);
        }

        /**
         * 
         * @return {*}
         */
        public getField(): com.vzome.core.algebra.AlgebraicField {
            return this.field;
        }

        /**
         * 
         * @param {com.vzome.core.construction.Construction} c
         * @return {*}
         */
        public findConstruction(c: com.vzome.core.construction.Construction): com.vzome.core.model.Manifestation {
            if (c == null)return null;
            let vectors: number[][][] = com.vzome.jsweet.JsManifestation.canonicalizeConstruction(c);
            if (vectors == null)return null;
            vectors = <number[][][]>(<any>(this.adapter["findOrCreateManifestation"])).apply(this.adapter, [vectors]);
            if (vectors == null)return null;
            return com.vzome.jsweet.JsManifestation.manifest(vectors, this.field, this.adapter);
        }

        /**
         * 
         * @param {*} man
         */
        public remove(man: com.vzome.core.model.Manifestation) {
            const vectors: number[][][] = (<com.vzome.jsweet.JsManifestation><any>man).getVectors();
            (<any>(this.adapter["delete"])).apply(this.adapter, [vectors]);
        }

        /**
         * 
         * @param {com.vzome.core.construction.Construction} c
         * @return {*}
         */
        public getManifestation(c: com.vzome.core.construction.Construction): com.vzome.core.model.Manifestation {
            return this.findConstruction(c);
        }

        /**
         * 
         * @param {*} man
         */
        public show(man: com.vzome.core.model.Manifestation) {
            const vectors: number[][][] = (<com.vzome.jsweet.JsManifestation><any>man).getVectors();
            (<any>(this.adapter["showManifestation"])).apply(this.adapter, [vectors]);
        }

        /**
         * 
         * @param {*} man
         */
        public hide(man: com.vzome.core.model.Manifestation) {
            const vectors: number[][][] = (<com.vzome.jsweet.JsManifestation><any>man).getVectors();
            (<any>(this.adapter["hideManifestation"])).apply(this.adapter, [vectors]);
        }

        /**
         * 
         * @param {*} man
         * @param {com.vzome.core.construction.Color} color
         */
        public setColor(man: com.vzome.core.model.Manifestation, color: com.vzome.core.construction.Color) {
            (<com.vzome.jsweet.JsManifestation><any>man).setColor(color);
        }

        /**
         * 
         * @param {*} man
         * @param {string} label
         */
        public setLabel(man: com.vzome.core.model.Manifestation, label: string) {
            (<com.vzome.jsweet.JsManifestation><any>man).setLabel(label);
        }

        /**
         * 
         * @param {*} man
         */
        public add(man: com.vzome.core.model.Manifestation) {
            const vectors: number[][][] = (<com.vzome.jsweet.JsManifestation><any>man).getVectors();
            (<any>(this.adapter["showManifestation"])).apply(this.adapter, [vectors]);
        }

        /**
         * 
         * @param {string} signature
         * @return {*}
         */
        public findPerEditManifestation(signature: string): com.vzome.core.model.Manifestation {
            return null;
        }

        /**
         * 
         * @param {string} signature
         * @param {*} m
         */
        public addPerEditManifestation(signature: string, m: com.vzome.core.model.Manifestation) {
        }

        /**
         * 
         */
        public clearPerEditManifestations() {
        }

        /**
         * 
         * @param {com.vzome.core.construction.Construction} c
         * @return {*}
         */
        public removeConstruction(c: com.vzome.core.construction.Construction): com.vzome.core.model.Manifestation {
            throw new java.lang.RuntimeException("unimplemented");
        }

        /**
         * 
         * @return {number}
         */
        public size(): number {
            throw new java.lang.RuntimeException("unimplemented");
        }
    }
    JsRealizedModel["__class"] = "com.vzome.jsweet.JsRealizedModel";
    JsRealizedModel["__interfaces"] = ["com.vzome.core.model.RealizedModel","java.lang.Iterable"];



    export namespace JsRealizedModel {

        export class JsRealizedModel$0 implements java.util.Iterator<com.vzome.core.model.Manifestation> {
            public __parent: any;
            peek: IteratorResult<number[][][]>;

            /**
             * 
             * @return {boolean}
             */
            public hasNext(): boolean {
                return !this.peek.done;
            }

            /**
             * 
             * @return {*}
             */
            public next(): com.vzome.core.model.Manifestation {
                const result: com.vzome.core.model.Manifestation = com.vzome.jsweet.JsManifestation.manifest(this.peek.value, this.__parent.field, this.__parent.adapter);
                this.peek = this.jSiterator.next();
                return result;
            }

            constructor(__parent: any, private jSiterator: any) {
                this.__parent = __parent;
                this.peek = this.jSiterator.next();
            }
        }
        JsRealizedModel$0["__interfaces"] = ["java.util.Iterator"];


    }

}

