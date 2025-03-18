/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.render {
    /**
     * @author vorth
     * @param {*} symm
     * @class
     */
    export abstract class AbstractZomicEventHandler implements com.vzome.core.render.ZomicEventHandler {
        mSymmetry: com.vzome.core.math.symmetry.Symmetry;

        mOrientation: com.vzome.core.math.symmetry.Permutation;

        mHandedNess: number;

        mScale: com.vzome.core.algebra.AlgebraicNumber;

        mAction: number;

        public constructor(symm: com.vzome.core.math.symmetry.Symmetry) {
            if (this.mSymmetry === undefined) { this.mSymmetry = null; }
            if (this.mOrientation === undefined) { this.mOrientation = null; }
            this.mHandedNess = com.vzome.core.math.symmetry.Symmetry.PLUS;
            if (this.mScale === undefined) { this.mScale = null; }
            this.mAction = com.vzome.core.render.ZomicEventHandler.BUILD;
            this.mSymmetry = symm;
            this.mScale = symm.getField().one();
            this.mOrientation = this.mSymmetry.getPermutation(0);
        }

        getPermutation(): com.vzome.core.math.symmetry.Permutation {
            return this.mOrientation;
        }

        public getDirection(name: string): com.vzome.core.math.symmetry.Direction {
            return this.mSymmetry.getDirection(name);
        }

        /**
         * 
         * @param {com.vzome.core.math.symmetry.Permutation} permutation
         * @param {number} sense
         */
        public permute(permutation: com.vzome.core.math.symmetry.Permutation, sense: number) {
            this.mOrientation = permutation.compose(this.mOrientation);
            this.mHandedNess = (this.mHandedNess + sense) % 2;
        }

        /**
         * 
         * @param {com.vzome.core.math.symmetry.Axis} axis
         * @param {number} steps
         */
        public rotate(axis: com.vzome.core.math.symmetry.Axis, steps: number) {
            axis = this.mOrientation.permute(axis, this.mHandedNess);
            if (axis.getSense() === this.mHandedNess)steps *= -1;
            this.permute(axis.getRotationPermutation().power(steps), com.vzome.core.math.symmetry.Symmetry.PLUS);
        }

        /**
         * 
         * @param {com.vzome.core.math.symmetry.Axis} blueAxis
         */
        public reflect(blueAxis: com.vzome.core.math.symmetry.Axis) {
            if (blueAxis == null)this.permute(this.mSymmetry.getPermutation(0), com.vzome.core.math.symmetry.Symmetry.MINUS); else {
                blueAxis = this.mOrientation.permute(blueAxis, this.mHandedNess);
                this.permute(blueAxis.getRotationPermutation(), com.vzome.core.math.symmetry.Symmetry.MINUS);
            }
        }

        /**
         * 
         * @param {*} scale
         */
        public scale(scale: com.vzome.core.algebra.AlgebraicNumber) {
            this.mScale = this.mScale['times$com_vzome_core_algebra_AlgebraicNumber'](scale);
        }

        /**
         * 
         * @param {number} action
         */
        public action(action: number) {
            this.mAction = action;
        }

        abstract copyLocation(): AbstractZomicEventHandler;

        abstract restoreLocation(changed: AbstractZomicEventHandler);

        /**
         * 
         * @param {number} variables
         * @return {*}
         */
        public save(variables: number): com.vzome.core.render.ZomicEventHandler {
            const newVM: AbstractZomicEventHandler = this.copyLocation();
            newVM.mAction = this.mAction;
            newVM.mOrientation = this.mOrientation;
            newVM.mHandedNess = this.mHandedNess;
            newVM.mScale = this.mScale;
            return newVM;
        }

        /**
         * 
         * @param {*} changes
         * @param {number} variables
         */
        public restore(changes: com.vzome.core.render.ZomicEventHandler, variables: number) {
            const changedVM: AbstractZomicEventHandler = <AbstractZomicEventHandler><any>changes;
            if ((com.vzome.core.render.ZomicEventHandler.LOCATION & variables) === 0)this.restoreLocation(changedVM);
            if ((com.vzome.core.render.ZomicEventHandler.SCALE & variables) === 0)this.mScale = changedVM.mScale;
            if ((com.vzome.core.render.ZomicEventHandler.ORIENTATION & variables) === 0){
                this.mOrientation = changedVM.mOrientation;
                this.mHandedNess = changedVM.mHandedNess;
            }
            if ((com.vzome.core.render.ZomicEventHandler.ACTION & variables) === 0)this.mAction = changedVM.mAction;
        }

        public abstract step(axis?: any, length?: any): any;    }
    AbstractZomicEventHandler["__class"] = "com.vzome.core.render.AbstractZomicEventHandler";
    AbstractZomicEventHandler["__interfaces"] = ["com.vzome.core.render.ZomicEventHandler"];


}

