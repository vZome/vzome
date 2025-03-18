/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.jsweet {
    export class JsSelection implements com.vzome.core.editor.api.Selection {
        /*private*/ adapter: Object;

        /*private*/ field: com.vzome.jsweet.JsAlgebraicField;

        public constructor(field: com.vzome.jsweet.JsAlgebraicField, adapter: Object) {
            if (this.adapter === undefined) { this.adapter = null; }
            if (this.field === undefined) { this.field = null; }
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
            if (this.adapter == null)return java.util.Collections.emptyIterator<any>();
            const f: Function = <any>(this.adapter["selectedIterator"]);
            const jSiterator: Iterator<number[][][]> = <Iterator<number[][][]>>f.apply(this.adapter);
            return new JsSelection.JsSelection$0(this, jSiterator);
        }

        /**
         * 
         */
        public clear() {
            (<any>(this.adapter["clearSelection"])).apply(this.adapter);
        }

        /**
         * 
         * @param {*} man
         * @return {boolean}
         */
        public manifestationSelected(man: com.vzome.core.model.Manifestation): boolean {
            const vectors: number[][][] = (<com.vzome.jsweet.JsManifestation><any>man).getVectors();
            return <boolean>(<any>(this.adapter["manifestationSelected"])).apply(this.adapter, [vectors]);
        }

        /**
         * 
         * @param {*} man
         */
        public select(man: com.vzome.core.model.Manifestation) {
            const vectors: number[][][] = (<com.vzome.jsweet.JsManifestation><any>man).getVectors();
            (<any>(this.adapter["select"])).apply(this.adapter, [vectors]);
        }

        /**
         * 
         * @param {*} man
         */
        public unselect(man: com.vzome.core.model.Manifestation) {
            const vectors: number[][][] = (<com.vzome.jsweet.JsManifestation><any>man).getVectors();
            (<any>(this.adapter["unselect"])).apply(this.adapter, [vectors]);
        }

        /**
         * 
         * @return {number}
         */
        public size(): number {
            return (<number>(<any>(this.adapter["selectionSize"])).apply(this.adapter)|0);
        }

        /**
         * 
         */
        public gatherGroup() {
            (<any>(this.adapter["createGroup"])).apply(this.adapter);
        }

        /**
         * 
         */
        public scatterGroup() {
            (<any>(this.adapter["disbandGroup"])).apply(this.adapter);
        }

        /**
         * 
         */
        public gatherGroup211() {
            (<any>(this.adapter["createLegacyGroup"])).apply(this.adapter);
        }

        /**
         * 
         */
        public scatterGroup211() {
            (<any>(this.adapter["disbandLegacyGroup"])).apply(this.adapter);
        }

        /**
         * 
         * @param {*} man
         */
        public selectWithGrouping(man: com.vzome.core.model.Manifestation) {
            if (man == null)return;
            const vectors: number[][][] = (<com.vzome.jsweet.JsManifestation><any>man).getVectors();
            (<any>(this.adapter["selectWithGrouping"])).apply(this.adapter, [vectors]);
        }

        /**
         * 
         * @param {*} man
         */
        public unselectWithGrouping(man: com.vzome.core.model.Manifestation) {
            if (man == null)return;
            const vectors: number[][][] = (<com.vzome.jsweet.JsManifestation><any>man).getVectors();
            (<any>(this.adapter["unselectWithGrouping"])).apply(this.adapter, [vectors]);
        }

        /**
         * 
         * @return {boolean}
         */
        public isSelectionAGroup(): boolean {
            return <boolean>(<any>(this.adapter["selectionIsGroup"])).apply(this.adapter);
        }

        /**
         * 
         * @param {java.lang.Class} class1
         * @return {*}
         */
        public getSingleSelection(class1: any): com.vzome.core.model.Manifestation {
            throw new java.lang.RuntimeException("unimplemented getSingleSelection");
        }

        /**
         * 
         * @param {*} bookmarkedSelection
         */
        public copy(bookmarkedSelection: java.util.List<com.vzome.core.model.Manifestation>) {
            throw new java.lang.RuntimeException("unimplemented");
        }
    }
    JsSelection["__class"] = "com.vzome.jsweet.JsSelection";
    JsSelection["__interfaces"] = ["com.vzome.core.editor.api.Selection","java.lang.Iterable"];



    export namespace JsSelection {

        export class JsSelection$0 implements java.util.Iterator<com.vzome.core.model.Manifestation> {
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
        JsSelection$0["__interfaces"] = ["java.util.Iterator"];


    }

}

