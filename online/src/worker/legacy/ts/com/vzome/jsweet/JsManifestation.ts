/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.jsweet {
    export abstract class JsManifestation implements com.vzome.core.model.Manifestation {
        vectors: number[][][];

        field: com.vzome.core.algebra.AlgebraicField;

        adapter: Object;

        public constructor(field: com.vzome.core.algebra.AlgebraicField, adapter: Object, vectors: number[][][]) {
            if (this.vectors === undefined) { this.vectors = null; }
            if (this.field === undefined) { this.field = null; }
            if (this.adapter === undefined) { this.adapter = null; }
            this.field = field;
            this.adapter = adapter;
            this.vectors = vectors;
        }

        public getVectors(): number[][][] {
            return this.vectors;
        }

        /**
         * 
         * @return {com.vzome.core.construction.Color}
         */
        public getColor(): com.vzome.core.construction.Color {
            const colorful: boolean = <boolean>(<any>(this.adapter["manifestationHasColor"])).apply(this.adapter, [this.vectors]);
            if (!colorful)return null;
            const rgb: number = (<number>(<any>(this.adapter["manifestationColor"])).apply(this.adapter, [this.vectors])|0);
            return new com.vzome.core.construction.Color(rgb);
        }

        /**
         * 
         * @return {string}
         */
        public getLabel(): string {
            return null;
        }

        /**
         * 
         * @param {string} label
         */
        public setLabel(label: string) {
        }

        /**
         * 
         * @param {com.vzome.core.construction.Color} color
         */
        public setColor(color: com.vzome.core.construction.Color) {
            if (color != null)(<any>(this.adapter["setManifestationColor"])).apply(this.adapter, [this.vectors, color.getRGB()]);
        }

        /**
         * 
         * @return {boolean}
         */
        public isRendered(): boolean {
            return <boolean>(<any>(this.adapter["manifestationRendered"])).apply(this.adapter, [this.vectors]);
        }

        /**
         * 
         * @param {com.vzome.core.construction.Construction} mConstruction
         */
        public addConstruction(mConstruction: com.vzome.core.construction.Construction) {
        }

        /**
         * 
         * @param {com.vzome.core.construction.Construction} mConstruction
         */
        public removeConstruction(mConstruction: com.vzome.core.construction.Construction) {
        }

        /**
         * 
         * @return {boolean}
         */
        public isUnnecessary(): boolean {
            return true;
        }

        /**
         * 
         * @return {com.vzome.core.construction.Construction}
         */
        public getFirstConstruction(): com.vzome.core.construction.Construction {
            return this.toConstruction();
        }

        public static canonicalizeNumbers(...ns: com.vzome.core.algebra.AlgebraicNumber[]): number[][] {
            return <number[][]>java.util.Arrays.stream<any>(ns).map<any>((n) => n.toTrailingDivisor()).toArray();
        }

        /**
         * Note: this does NOT order the vectors canonically in the outermost array
         * @param {com.vzome.core.algebra.AlgebraicVector[]} vs
         * @return
         * @return {int[][][]}
         */
        public static canonicalizeVectors(...vs: com.vzome.core.algebra.AlgebraicVector[]): number[][][] {
            return <number[][][]>java.util.Arrays.stream<any>(vs).map<any>((v) => JsManifestation.canonicalizeNumbers.apply(this, v.getComponents())).toArray();
        }

        public static canonicalizeConstruction(c: com.vzome.core.construction.Construction): number[][][] {
            if (c != null && c instanceof <any>com.vzome.core.construction.Point){
                const p: com.vzome.core.construction.Point = <com.vzome.core.construction.Point>c;
                return JsManifestation.canonicalizeVectors(p.getLocation());
            } else if (c != null && c instanceof <any>com.vzome.core.construction.Segment){
                const s: com.vzome.core.construction.Segment = <com.vzome.core.construction.Segment>c;
                return JsManifestation.canonicalizeVectors(s.getStart(), s.getEnd());
            } else if (c != null && c instanceof <any>com.vzome.core.construction.Polygon){
                return JsManifestation.canonicalizeVectors.apply(this, (<com.vzome.core.construction.Polygon>c).getVertices());
            }
            return null;
        }

        public static manifest(vectors: number[][][], field: com.vzome.core.algebra.AlgebraicField, adapter: Object): com.vzome.core.model.Manifestation {
            switch((vectors.length)) {
            case 1:
                return new com.vzome.jsweet.JsBall(field, adapter, vectors);
            case 2:
                const strut: com.vzome.jsweet.JsStrut = new com.vzome.jsweet.JsStrut(field, adapter, vectors);
                if (strut.getOffset().isOrigin())return null; else return strut;
            default:
                return new com.vzome.jsweet.JsPanel(field, adapter, vectors);
            }
        }

        /**
         * 
         * @param {boolean} b
         */
        public setHidden(b: boolean) {
        }

        /**
         * 
         * @return {boolean}
         */
        public isHidden(): boolean {
            return <boolean>(<any>(this.adapter["manifestationHidden"])).apply(this.adapter, [this.vectors]);
        }

        /**
         * 
         * @param {com.vzome.core.model.Group} container
         */
        public setContainer(container: com.vzome.core.model.Group) {
            throw new java.lang.RuntimeException("unimplemented");
        }

        /**
         * 
         * @return {com.vzome.core.model.Group}
         */
        public getContainer(): com.vzome.core.model.Group {
            const members: number[][][][] = <number[][][][]>(<any>(this.adapter["getLargestGroup"])).apply(this.adapter, [this.vectors]);
            if (members == null)return null;
            const group: com.vzome.core.model.Group = new com.vzome.core.model.Group();
            for(let i: number = 0; i < members.length; i++) {{
                group.add(JsManifestation.manifest(members[i], this.field, this.adapter));
            };}
            return group;
        }

        /**
         * 
         * @param {*} renderedObject
         */
        public setRenderedObject(renderedObject: com.vzome.core.model.RenderedObject) {
        }

        /**
         * 
         * @return {*}
         */
        public getConstructions(): java.util.Iterator<com.vzome.core.construction.Construction> {
            throw new java.lang.RuntimeException("unimplemented");
        }

        /**
         * 
         * @param {*} doc
         * @return {*}
         */
        public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
            throw new java.lang.RuntimeException("unimplemented");
        }

        /**
         * 
         * @return {com.vzome.core.algebra.AlgebraicVector}
         */
        public getCentroid(): com.vzome.core.algebra.AlgebraicVector {
            throw new java.lang.RuntimeException("unimplemented");
        }

        public abstract getLocation(): any;
        public abstract toConstruction(): any;    }
    JsManifestation["__class"] = "com.vzome.jsweet.JsManifestation";
    JsManifestation["__interfaces"] = ["com.vzome.core.model.GroupElement","com.vzome.core.model.Manifestation"];


}

