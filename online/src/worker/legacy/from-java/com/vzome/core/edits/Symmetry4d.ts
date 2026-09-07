import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Quaternion } from "../algebra/Quaternion.js";
import { Command } from "../commands/Command.js";
import { Construction } from "../construction/Construction.js";
import { FreePoint } from "../construction/FreePoint.js";
import { Point } from "../construction/Point.js";
import { PointRotated4D } from "../construction/PointRotated4D.js";
import { Polygon } from "../construction/Polygon.js";
import { PolygonRotated4D } from "../construction/PolygonRotated4D.js";
import { Segment } from "../construction/Segment.js";
import { SegmentRotated4D } from "../construction/SegmentRotated4D.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { SymmetryAware } from "../editor/api/SymmetryAware.js";
import { QuaternionicSymmetry } from "../math/symmetry/QuaternionicSymmetry.js";

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
 * @param {QuaternionicSymmetry} left
 * @param {QuaternionicSymmetry} right
 * @class
 * @extends ChangeManifestations
 */
export class Symmetry4d extends ChangeManifestations {
    /*private*/ left: QuaternionicSymmetry;

    /*private*/ right: QuaternionicSymmetry;

    public constructor(editor?: any, left?: any, right?: any) {
        if (((editor != null && (editor.constructor != null && editor.constructor["__interfaces"] != null && editor.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.EditorModel") >= 0)) || editor === null) && ((left != null && left instanceof <any>QuaternionicSymmetry) || left === null) && ((right != null && right instanceof <any>QuaternionicSymmetry) || right === null)) {
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
            this.left = (<SymmetryAware><any>editor).get4dSymmetries().getQuaternionSymmetry("H_4");
            this.right = this.left;
        } else throw new Error('invalid overload');
    }

    /**
     * 
     * @param {*} parameters
     */
    public configure(parameters: java.util.Map<string, any>) {
        this.left = <QuaternionicSymmetry>parameters.get("left");
        this.right = <QuaternionicSymmetry>parameters.get("right");
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "Symmetry4d";
    }

    /*private*/ static inW0hyperplane(v: AlgebraicVector): boolean {
        if (v.dimension() > 3)return v.getComponent(AlgebraicVector.W4).isZero(); else return true;
    }

    /**
     * 
     */
    public perform() {
        const params: java.util.List<Construction> = <any>(new java.util.ArrayList<any>());
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            {
                this.unselect$com_vzome_core_model_Manifestation(man);
                const cs: java.util.Iterator<Construction> = man.getConstructions();
                let useThis: Construction = null;
                if (!cs.hasNext())throw new Command.Failure("No construction for this manifestation");
                for(const iterator: java.util.Iterator<Construction> = man.getConstructions(); iterator.hasNext(); ) {{
                    const construction: Construction = iterator.next();
                    if (construction != null && construction instanceof <any>Point){
                        const p: Point = <Point>construction;
                        if (!Symmetry4d.inW0hyperplane(p.getLocation()))throw new Command.Failure("Some ball is not in the W=0 hyperplane.");
                    } else if (construction != null && construction instanceof <any>Segment){
                        const s: Segment = <Segment>construction;
                        if (!Symmetry4d.inW0hyperplane(s.getStart()))throw new Command.Failure("Some strut end is not in the W=0 hyperplane.");
                        if (!Symmetry4d.inW0hyperplane(s.getEnd()))throw new Command.Failure("Some strut end is not in the W=0 hyperplane.");
                    } else if (construction != null && construction instanceof <any>Polygon){
                        const p: Polygon = <Polygon>construction;
                        for(let i: number = 0; i < p.getVertexCount(); i++) {{
                            if (!Symmetry4d.inW0hyperplane(p.getVertex(i))){
                                throw new Command.Failure("Some panel vertex is not in the W=0 hyperplane.");
                            }
                        };}
                    } else {
                        throw new Command.Failure("Unknown construction type.");
                    }
                    useThis = construction;
                };}
                if (useThis != null)params.add(useThis);
            }
        }
        this.redo();
        const leftRoots: Quaternion[] = this.left.getRoots();
        const rightRoots: Quaternion[] = this.right.getRoots();
        for(let index = 0; index < leftRoots.length; index++) {
            let leftRoot = leftRoots[index];
            {
                for(let index1 = 0; index1 < rightRoots.length; index1++) {
                    let rightRoot = rightRoots[index1];
                    {
                        for(let index2=params.iterator();index2.hasNext();) {
                            let construction = index2.next();
                            {
                                let result: Construction = null;
                                if (construction != null && construction instanceof <any>Point){
                                    result = new PointRotated4D(leftRoot, rightRoot, <Point>construction);
                                } else if (construction != null && construction instanceof <any>Segment){
                                    result = new SegmentRotated4D(leftRoot, rightRoot, <Segment>construction);
                                } else if (construction != null && construction instanceof <any>Polygon){
                                    result = new PolygonRotated4D(leftRoot, rightRoot, <Polygon>construction);
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

    rotateAndProject(loc3d: AlgebraicVector, leftQuaternion: Quaternion, rightQuaternion: Quaternion): FreePoint {
        let loc: AlgebraicVector = loc3d.inflateTo4d$boolean(true);
        loc = rightQuaternion.leftMultiply(loc);
        loc = leftQuaternion.rightMultiply(loc);
        loc = loc.projectTo3d(true);
        return new FreePoint(loc);
    }
}
Symmetry4d["__class"] = "com.vzome.core.edits.Symmetry4d";
