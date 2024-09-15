/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class SelectByRadius extends com.vzome.core.edits.SelectByDiameter {
        public static NAME: string = "SelectByRadius";

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return SelectByRadius.NAME;
        }

        minRadiusSquared: com.vzome.core.algebra.AlgebraicNumber;

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
            if (this.minRadiusSquared === undefined) { this.minRadiusSquared = null; }
        }

        /**
         * 
         * @return {string}
         */
        public usage(): string {
            return "This command requires either two or three selected connectors.\n\nThe first connector marks the center of a sphere.\nThe second connector defines its radius.\nAn optional third connector defines the radius of\n a second sphere with the same center.\n\nAll parts that are completely within the larger sphere will be selected.\n\nIf a second sphere is defined then any parts\n     within the smaller sphere, even partially, will be excluded.\n";
        }

        /**
         * 
         */
        public perform() {
            this.setOrderedSelection(true);
            super.perform();
        }

        /**
         * 
         * @param {*} vectors
         * @return {string}
         */
        adjustBoundary(vectors: java.util.List<com.vzome.core.algebra.AlgebraicVector>): string {
            const v: com.vzome.core.algebra.AlgebraicVector = vectors.get(vectors.size() - 1);
            switch((vectors.size())) {
            case 1:
                this.center = v;
                this.maxRadiusSquared = null;
                this.minRadiusSquared = null;
                return null;
            case 2:
                const v2: com.vzome.core.algebra.AlgebraicVector = v.minus(this.center);
                this.maxRadiusSquared = v2.dot(v2);
                return null;
            case 3:
                const v3: com.vzome.core.algebra.AlgebraicVector = v.minus(this.center);
                this.minRadiusSquared = v3.dot(v3);
                if (this.maxRadiusSquared.compareTo(this.minRadiusSquared) < 0){
                    const temp: com.vzome.core.algebra.AlgebraicNumber = this.maxRadiusSquared;
                    this.maxRadiusSquared = this.minRadiusSquared;
                    this.minRadiusSquared = temp;
                }
                return null;
            }
            return super.adjustBoundary(vectors);
        }

        public boundaryContains$com_vzome_core_algebra_AlgebraicVector(v: com.vzome.core.algebra.AlgebraicVector): boolean {
            if (super.boundaryContains$com_vzome_core_algebra_AlgebraicVector(v)){
                if (this.minRadiusSquared != null){
                    const v1: com.vzome.core.algebra.AlgebraicVector = v.minus(this.center);
                    return v1.dot(v1).compareTo(this.minRadiusSquared) >= 0;
                }
                return true;
            }
            return false;
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
    SelectByRadius["__class"] = "com.vzome.core.edits.SelectByRadius";

}

