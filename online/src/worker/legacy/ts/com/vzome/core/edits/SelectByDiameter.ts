/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class SelectByDiameter extends com.vzome.core.edits.SelectByBoundary {
        public static NAME: string = "SelectByDiameter";

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return SelectByDiameter.NAME;
        }

        center: com.vzome.core.algebra.AlgebraicVector;

        maxRadiusSquared: com.vzome.core.algebra.AlgebraicNumber;

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
            if (this.center === undefined) { this.center = null; }
            if (this.maxRadiusSquared === undefined) { this.maxRadiusSquared = null; }
        }

        /**
         * 
         * @return {string}
         */
        public usage(): string {
            return "This command requires two connectors which define the\ndiameter of a sphere centered at their midpoint.\n\nAll parts that are completely within the sphere will be selected.\n";
        }

        adjustBoundary(vectors: java.util.List<com.vzome.core.algebra.AlgebraicVector>): string {
            switch((vectors.size())) {
            case 1:
                return null;
            case 2:
                this.center = com.vzome.core.algebra.AlgebraicVectors.calculateCentroid(vectors);
                const v1: com.vzome.core.algebra.AlgebraicVector = vectors.get(0).minus(this.center);
                this.maxRadiusSquared = v1.dot(v1);
                return null;
            }
            return "Too many connectors are selected.";
        }

        /**
         * 
         * @return {string}
         */
        setBoundary(): string {
            this.center = null;
            this.maxRadiusSquared = null;
            const vectors: java.util.List<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.ArrayList<any>());
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                        vectors.add(man.getLocation());
                        const errMsg: string = this.adjustBoundary(vectors);
                        if (errMsg != null){
                            return errMsg;
                        }
                    }
                }
            }
            if (vectors.isEmpty()){
                return "No connectors are selected.";
            }
            if (this.center == null || this.maxRadiusSquared == null){
                const n: number = vectors.size();
                return n === 1 ? "Only one connector is selected." : "Only " + n + " connectors are selected.";
            }
            return null;
        }

        public boundaryContains$com_vzome_core_algebra_AlgebraicVector(v: com.vzome.core.algebra.AlgebraicVector): boolean {
            v = v.minus(this.center);
            const vSq: com.vzome.core.algebra.AlgebraicNumber = v.dot(v);
            return vSq.compareTo(this.maxRadiusSquared) <= 0;
        }

        /**
         * 
         * @param {com.vzome.core.algebra.AlgebraicVector} v
         * @return {boolean}
         */
        public boundaryContains(v?: any): boolean {
            if (((v != null && v instanceof <any>com.vzome.core.algebra.AlgebraicVector) || v === null)) {
                return <any>this.boundaryContains$com_vzome_core_algebra_AlgebraicVector(v);
            } else if (((v != null && (v.constructor != null && v.constructor["__interfaces"] != null && v.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)) || v === null)) {
                return super.boundaryContains(v);
            } else if (((v != null && (v.constructor != null && v.constructor["__interfaces"] != null && v.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)) || v === null)) {
                return <any>this.boundaryContains$com_vzome_core_model_Strut(v);
            } else if (((v != null && (v.constructor != null && v.constructor["__interfaces"] != null && v.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)) || v === null)) {
                return <any>this.boundaryContains$com_vzome_core_model_Panel(v);
            } else throw new Error('invalid overload');
        }
    }
    SelectByDiameter["__class"] = "com.vzome.core.edits.SelectByDiameter";

}

