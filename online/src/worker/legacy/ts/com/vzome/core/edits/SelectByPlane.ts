/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class SelectByPlane extends com.vzome.core.edits.SelectByBoundary {
        public static NAME: string = "SelectByPlane";

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return SelectByPlane.NAME;
        }

        /*private*/ plane: com.vzome.core.algebra.Bivector3d;

        /*private*/ anchor: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ desiredOrientation: number;

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
            if (this.plane === undefined) { this.plane = null; }
            if (this.anchor === undefined) { this.anchor = null; }
            if (this.desiredOrientation === undefined) { this.desiredOrientation = 0; }
        }

        /**
         * 
         * @return {string}
         */
        public usage(): string {
            return "This command requires four selected connectors.\n\nThe first three connectors must not be collinear,\nso that they define a plane.\nThe fourth connector must lie outside of that plane,\nand defines which half space you wish to select.\n\nAll parts that are completely within that half-space will be selected.\n";
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
         * @return {string}
         */
        setBoundary(): string {
            let p1: com.vzome.core.algebra.AlgebraicVector = null;
            let p2: com.vzome.core.algebra.AlgebraicVector = null;
            let p3: com.vzome.core.algebra.AlgebraicVector = null;
            let p4: com.vzome.core.algebra.AlgebraicVector = null;
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                        if (p1 == null){
                            p1 = man.getLocation();
                            continue;
                        }
                        if (p2 == null){
                            p2 = man.getLocation();
                            continue;
                        }
                        if (p3 == null){
                            p3 = man.getLocation();
                            continue;
                        }
                        if (p4 == null){
                            p4 = man.getLocation();
                            continue;
                        } else {
                            return "You have selected more than four connectors.";
                        }
                    }
                }
            }
            if (p4 == null)return "You have selected fewer than four connectors.";
            const v1: com.vzome.core.algebra.Vector3d = new com.vzome.core.algebra.Vector3d(p2.minus(p1));
            const v2: com.vzome.core.algebra.Vector3d = new com.vzome.core.algebra.Vector3d(p3.minus(p1));
            this.plane = v1.outer(v2);
            this.anchor = p1;
            this.desiredOrientation = this.orient(p4);
            if (this.desiredOrientation === 0){
                return "Your last selected connector lies in the plane of the other three.";
            }
            return null;
        }

        /*private*/ orient(point: com.vzome.core.algebra.AlgebraicVector): number {
            const diff: com.vzome.core.algebra.AlgebraicVector = point.minus(this.anchor);
            const v: com.vzome.core.algebra.Vector3d = new com.vzome.core.algebra.Vector3d(diff);
            const volume: com.vzome.core.algebra.AlgebraicNumber = this.plane.outer(v);
            if (volume.isZero())return 0; else {
                const volD: number = volume.evaluate();
                return (volD > 0.0) ? 1 : -1;
            }
        }

        public boundaryContains$com_vzome_core_algebra_AlgebraicVector(v: com.vzome.core.algebra.AlgebraicVector): boolean {
            const orientation: number = this.orient(v);
            return (orientation === 0) || (orientation === this.desiredOrientation);
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
    SelectByPlane["__class"] = "com.vzome.core.edits.SelectByPlane";

}

