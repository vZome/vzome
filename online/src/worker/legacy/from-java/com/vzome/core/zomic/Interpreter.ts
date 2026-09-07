import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { Axis } from "../math/symmetry/Axis.js";
import { Permutation } from "../math/symmetry/Permutation.js";
import { Symmetry as CoreSymmetry } from "../math/symmetry/Symmetry.js";
import { ZomicEventHandler } from "../render/ZomicEventHandler.js";
import { Nested } from "./program/Nested.js";
import { Permute } from "./program/Permute.js";
import { Save } from "./program/Save.js";
import { Symmetry as CoreSymmetry2 } from "./program/Symmetry.js";
import { Visitor } from "./program/Visitor.js";

/**
 * Implements the Zomic execution model while visiting a program.
 * 
 * @author Scott Vorthmann 2003
 * @param {*} renderer
 * @param {*} symmetry
 * @class
 * @extends Visitor.Default
 */
export class Interpreter extends Visitor.Default {
    mEvents: ZomicEventHandler;

    mSymmetry: CoreSymmetry;

    public constructor(renderer: ZomicEventHandler, symmetry: CoreSymmetry) {
        super();
        if (this.mEvents === undefined) { this.mEvents = null; }
        if (this.mSymmetry === undefined) { this.mSymmetry = null; }
        this.mEvents = renderer;
        this.mSymmetry = symmetry;
    }

    /**
     * 
     * @param {Axis} axis
     * @param {*} length
     */
    public visitMove(axis: Axis, length: AlgebraicNumber) {
        this.mEvents.step(axis, length);
    }

    /**
     * 
     * @param {Axis} axis
     * @param {number} steps
     */
    public visitRotate(axis: Axis, steps: number) {
        this.mEvents.rotate(axis, steps);
    }

    /**
     * 
     * @param {Axis} blueAxis
     */
    public visitReflect(blueAxis: Axis) {
        this.mEvents.reflect(blueAxis);
    }

    /**
     * 
     * @param {CoreSymmetry2} model
     * @param {Permute} permute
     */
    public visitSymmetry(model: CoreSymmetry2, permute: Permute) {
        if (permute != null){
            const repetitions: number = permute.getOrder();
            if (repetitions === 1)throw new java.lang.RuntimeException("no rotation symmetry around extended axes");
            for(let i: number = 0; i < repetitions; i++) {{
                this.saveAndNest(model, ZomicEventHandler.ORIENTATION);
                permute.accept(this);
            };}
        } else {
            for(let i: number = 0; i < this.mSymmetry.getChiralOrder(); i++) {{
                const current: Permutation = this.mSymmetry.getPermutation(i);
                const saved: ZomicEventHandler = this.mEvents;
                this.mEvents = this.mEvents.save(ZomicEventHandler.ALL);
                this.mEvents.permute(current, CoreSymmetry.PLUS);
                try {
                    this.visitNested(model);
                } catch(e) {
                    throw new java.lang.RuntimeException("error in global symmetry");
                }
                saved.restore(this.mEvents, ZomicEventHandler.ALL);
                this.mEvents = saved;
            };}
        }
    }

    /*private*/ saveAndNest(stmt: Nested, state: number) {
        const saved: ZomicEventHandler = this.mEvents;
        this.mEvents = this.mEvents.save(state);
        this.visitNested(stmt);
        saved.restore(this.mEvents, state);
        this.mEvents = saved;
    }

    /**
     * 
     * @param {Save} stmt
     * @param {number} state
     */
    public visitSave(stmt: Save, state: number) {
        this.saveAndNest(stmt, state);
    }

    /**
     * 
     * @param {*} size
     */
    public visitScale(size: AlgebraicNumber) {
        this.mEvents.scale(size);
    }

    /**
     * 
     * @param {boolean} build
     * @param {boolean} destroy
     */
    public visitBuild(build: boolean, destroy: boolean) {
        let action: number = ZomicEventHandler.JUST_MOVE;
        if (build)action |= ZomicEventHandler.BUILD;
        if (destroy)action |= ZomicEventHandler.DESTROY;
        this.mEvents.action(action);
    }
}
Interpreter["__class"] = "com.vzome.core.zomic.Interpreter";
Interpreter["__interfaces"] = ["com.vzome.core.zomic.program.Visitor"];
