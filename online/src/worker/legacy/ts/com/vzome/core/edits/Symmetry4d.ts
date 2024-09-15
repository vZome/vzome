/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    /**
     * This is a modern replacement for CommandQuaternionSymmetry, which is a legacy command.
     * It duplicates the math from that command, but one key change: only parameter objects that lie
     * in the W=0 plane are transformed.  This makes it safe and predictable to use
     * on objects produced by Polytope4d, which retain their 4D coordinates.
     * 
     * As with CommandQuaternionSymmetry, all transformed vertices are projected to the W=0 plane
     * before being added to the model.
     * 
     * @author vorth
     * @param {*} editor
     * @param {com.vzome.core.math.symmetry.QuaternionicSymmetry} left
     * @param {com.vzome.core.math.symmetry.QuaternionicSymmetry} right
     * @class
     * @extends com.vzome.core.editor.api.ChangeManifestations
     */
    export class Symmetry4d extends com.vzome.core.editor.api.ChangeManifestations {
        /*private*/ left: com.vzome.core.math.symmetry.QuaternionicSymmetry;

        /*private*/ right: com.vzome.core.math.symmetry.QuaternionicSymmetry;

        public constructor(editor?: any, left?: any, right?: any) {
            if (((editor != null && (editor.constructor != null && editor.constructor["__interfaces"] != null && editor.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.EditorModel") >= 0)) || editor === null) && ((left != null && left instanceof <any>com.vzome.core.math.symmetry.QuaternionicSymmetry) || left === null) && ((right != null && right instanceof <any>com.vzome.core.math.symmetry.QuaternionicSymmetry) || right === null)) {
                let __args = arguments;
                super(editor);
                if (this.left === undefined) { this.left = null; } 
                if (this.right === undefined) { this.right = null; } 
                this.left = left;
                this.right = right;
            } else if (((editor != null && (editor.constructor != null && editor.constructor["__interfaces"] != null && editor.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.EditorModel") >= 0)) || editor === null) && left === undefined && right === undefined) {
                let __args = arguments;
                super(editor);
                if (this.left === undefined) { this.left = null; } 
                if (this.right === undefined) { this.right = null; } 
                this.left = (<com.vzome.core.editor.api.SymmetryAware><any>editor).get4dSymmetries().getQuaternionSymmetry("H_4");
                this.right = this.left;
            } else throw new Error('invalid overload');
        }

        /**
         * 
         * @param {*} parameters
         */
        public configure(parameters: java.util.Map<string, any>) {
            this.left = <com.vzome.core.math.symmetry.QuaternionicSymmetry>parameters.get("left");
            this.right = <com.vzome.core.math.symmetry.QuaternionicSymmetry>parameters.get("right");
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "Symmetry4d";
        }

        /*private*/ static inW0hyperplane(v: com.vzome.core.algebra.AlgebraicVector): boolean {
            if (v.dimension() > 3)return v.getComponent(com.vzome.core.algebra.AlgebraicVector.W4).isZero(); else return true;
        }

        /**
         * 
         */
        public perform() {
            const params: java.util.List<com.vzome.core.construction.Construction> = <any>(new java.util.ArrayList<any>());
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    this.unselect$com_vzome_core_model_Manifestation(man);
                    const cs: java.util.Iterator<com.vzome.core.construction.Construction> = man.getConstructions();
                    let useThis: com.vzome.core.construction.Construction = null;
                    if (!cs.hasNext())throw new com.vzome.core.commands.Command.Failure("No construction for this manifestation");
                    for(const iterator: java.util.Iterator<com.vzome.core.construction.Construction> = man.getConstructions(); iterator.hasNext(); ) {{
                        const construction: com.vzome.core.construction.Construction = iterator.next();
                        if (construction != null && construction instanceof <any>com.vzome.core.construction.Point){
                            const p: com.vzome.core.construction.Point = <com.vzome.core.construction.Point>construction;
                            if (!Symmetry4d.inW0hyperplane(p.getLocation()))throw new com.vzome.core.commands.Command.Failure("Some ball is not in the W=0 hyperplane.");
                        } else if (construction != null && construction instanceof <any>com.vzome.core.construction.Segment){
                            const s: com.vzome.core.construction.Segment = <com.vzome.core.construction.Segment>construction;
                            if (!Symmetry4d.inW0hyperplane(s.getStart()))throw new com.vzome.core.commands.Command.Failure("Some strut end is not in the W=0 hyperplane.");
                            if (!Symmetry4d.inW0hyperplane(s.getEnd()))throw new com.vzome.core.commands.Command.Failure("Some strut end is not in the W=0 hyperplane.");
                        } else if (construction != null && construction instanceof <any>com.vzome.core.construction.Polygon){
                            const p: com.vzome.core.construction.Polygon = <com.vzome.core.construction.Polygon>construction;
                            for(let i: number = 0; i < p.getVertexCount(); i++) {{
                                if (!Symmetry4d.inW0hyperplane(p.getVertex(i))){
                                    throw new com.vzome.core.commands.Command.Failure("Some panel vertex is not in the W=0 hyperplane.");
                                }
                            };}
                        } else {
                            throw new com.vzome.core.commands.Command.Failure("Unknown construction type.");
                        }
                        useThis = construction;
                    };}
                    if (useThis != null)params.add(useThis);
                }
            }
            this.redo();
            const leftRoots: com.vzome.core.algebra.Quaternion[] = this.left.getRoots();
            const rightRoots: com.vzome.core.algebra.Quaternion[] = this.right.getRoots();
            for(let index = 0; index < leftRoots.length; index++) {
                let leftRoot = leftRoots[index];
                {
                    for(let index1 = 0; index1 < rightRoots.length; index1++) {
                        let rightRoot = rightRoots[index1];
                        {
                            for(let index2=params.iterator();index2.hasNext();) {
                                let construction = index2.next();
                                {
                                    let result: com.vzome.core.construction.Construction = null;
                                    if (construction != null && construction instanceof <any>com.vzome.core.construction.Point){
                                        result = new com.vzome.core.construction.PointRotated4D(leftRoot, rightRoot, <com.vzome.core.construction.Point>construction);
                                    } else if (construction != null && construction instanceof <any>com.vzome.core.construction.Segment){
                                        result = new com.vzome.core.construction.SegmentRotated4D(leftRoot, rightRoot, <com.vzome.core.construction.Segment>construction);
                                    } else if (construction != null && construction instanceof <any>com.vzome.core.construction.Polygon){
                                        result = new com.vzome.core.construction.PolygonRotated4D(leftRoot, rightRoot, <com.vzome.core.construction.Polygon>construction);
                                    } else {
                                    }
                                    if (result == null)continue;
                                    this.manifestConstruction(result);
                                }
                            }
                        }
                    }
                }
            }
            this.redo();
        }

        rotateAndProject(loc3d: com.vzome.core.algebra.AlgebraicVector, leftQuaternion: com.vzome.core.algebra.Quaternion, rightQuaternion: com.vzome.core.algebra.Quaternion): com.vzome.core.construction.FreePoint {
            let loc: com.vzome.core.algebra.AlgebraicVector = loc3d.inflateTo4d$boolean(true);
            loc = rightQuaternion.leftMultiply(loc);
            loc = leftQuaternion.rightMultiply(loc);
            loc = loc.projectTo3d(true);
            return new com.vzome.core.construction.FreePoint(loc);
        }
    }
    Symmetry4d["__class"] = "com.vzome.core.edits.Symmetry4d";

}

