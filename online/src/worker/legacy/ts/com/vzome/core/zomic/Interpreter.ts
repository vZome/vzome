/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.zomic {
    /**
     * Implements the Zomic execution model while visiting a program.
     * 
     * @author Scott Vorthmann 2003
     * @param {*} renderer
     * @param {*} symmetry
     * @class
     * @extends com.vzome.core.zomic.program.Visitor.Default
     */
    export class Interpreter extends com.vzome.core.zomic.program.Visitor.Default {
        mEvents: com.vzome.core.render.ZomicEventHandler;

        mSymmetry: com.vzome.core.math.symmetry.Symmetry;

        public constructor(renderer: com.vzome.core.render.ZomicEventHandler, symmetry: com.vzome.core.math.symmetry.Symmetry) {
            super();
            if (this.mEvents === undefined) { this.mEvents = null; }
            if (this.mSymmetry === undefined) { this.mSymmetry = null; }
            this.mEvents = renderer;
            this.mSymmetry = symmetry;
        }

        /**
         * 
         * @param {com.vzome.core.math.symmetry.Axis} axis
         * @param {*} length
         */
        public visitMove(axis: com.vzome.core.math.symmetry.Axis, length: com.vzome.core.algebra.AlgebraicNumber) {
            this.mEvents.step(axis, length);
        }

        /**
         * 
         * @param {com.vzome.core.math.symmetry.Axis} axis
         * @param {number} steps
         */
        public visitRotate(axis: com.vzome.core.math.symmetry.Axis, steps: number) {
            this.mEvents.rotate(axis, steps);
        }

        /**
         * 
         * @param {com.vzome.core.math.symmetry.Axis} blueAxis
         */
        public visitReflect(blueAxis: com.vzome.core.math.symmetry.Axis) {
            this.mEvents.reflect(blueAxis);
        }

        /**
         * 
         * @param {com.vzome.core.zomic.program.Symmetry} model
         * @param {com.vzome.core.zomic.program.Permute} permute
         */
        public visitSymmetry(model: com.vzome.core.zomic.program.Symmetry, permute: com.vzome.core.zomic.program.Permute) {
            if (permute != null){
                const repetitions: number = permute.getOrder();
                if (repetitions === 1)throw new java.lang.RuntimeException("no rotation symmetry around extended axes");
                for(let i: number = 0; i < repetitions; i++) {{
                    this.saveAndNest(model, com.vzome.core.render.ZomicEventHandler.ORIENTATION);
                    permute.accept(this);
                };}
            } else {
                for(let i: number = 0; i < this.mSymmetry.getChiralOrder(); i++) {{
                    const current: com.vzome.core.math.symmetry.Permutation = this.mSymmetry.getPermutation(i);
                    const saved: com.vzome.core.render.ZomicEventHandler = this.mEvents;
                    this.mEvents = this.mEvents.save(com.vzome.core.render.ZomicEventHandler.ALL);
                    this.mEvents.permute(current, com.vzome.core.math.symmetry.Symmetry.PLUS);
                    try {
                        this.visitNested(model);
                    } catch(e) {
                        throw new java.lang.RuntimeException("error in global symmetry");
                    }
                    saved.restore(this.mEvents, com.vzome.core.render.ZomicEventHandler.ALL);
                    this.mEvents = saved;
                };}
            }
        }

        /*private*/ saveAndNest(stmt: com.vzome.core.zomic.program.Nested, state: number) {
            const saved: com.vzome.core.render.ZomicEventHandler = this.mEvents;
            this.mEvents = this.mEvents.save(state);
            this.visitNested(stmt);
            saved.restore(this.mEvents, state);
            this.mEvents = saved;
        }

        /**
         * 
         * @param {com.vzome.core.zomic.program.Save} stmt
         * @param {number} state
         */
        public visitSave(stmt: com.vzome.core.zomic.program.Save, state: number) {
            this.saveAndNest(stmt, state);
        }

        /**
         * 
         * @param {*} size
         */
        public visitScale(size: com.vzome.core.algebra.AlgebraicNumber) {
            this.mEvents.scale(size);
        }

        /**
         * 
         * @param {boolean} build
         * @param {boolean} destroy
         */
        public visitBuild(build: boolean, destroy: boolean) {
            let action: number = com.vzome.core.render.ZomicEventHandler.JUST_MOVE;
            if (build)action |= com.vzome.core.render.ZomicEventHandler.BUILD;
            if (destroy)action |= com.vzome.core.render.ZomicEventHandler.DESTROY;
            this.mEvents.action(action);
        }
    }
    Interpreter["__class"] = "com.vzome.core.zomic.Interpreter";
    Interpreter["__interfaces"] = ["com.vzome.core.zomic.program.Visitor"];


}

