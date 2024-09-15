/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.render {
    /**
     * @author vorth
     * @class
     */
    export interface ZomicEventHandler {
        step(axis: com.vzome.core.math.symmetry.Axis, length: com.vzome.core.algebra.AlgebraicNumber);

        /**
         * 
         * @param {com.vzome.core.math.symmetry.Axis} axis
         * @param {number} steps
         */
        rotate(axis: com.vzome.core.math.symmetry.Axis, steps: number);

        /**
         * Reflect through a blue axis, or through the current location point
         * if blueAxis == null.
         * @param {com.vzome.core.math.symmetry.Axis} blueAxis
         */
        reflect(blueAxis: com.vzome.core.math.symmetry.Axis);

        permute(permutation: com.vzome.core.math.symmetry.Permutation, sense: number);

        scale(scale: com.vzome.core.algebra.AlgebraicNumber);

        action(action: number);

        save(variables: number): ZomicEventHandler;

        restore(changes: ZomicEventHandler, variables: number);
    }

    export namespace ZomicEventHandler {

        /**
         * Constants for use with save();
         */
        export const ALL: number = 15;

        /**
         * Constants for use with save();
         */
        export const LOCATION: number = 1;

        /**
         * Constants for use with save();
         */
        export const SCALE: number = 2;

        /**
         * Constants for use with save();
         */
        export const ORIENTATION: number = 4;

        /**
         * Constants for use with save();
         */
        export const ACTION: number = 8;

        /**
         * Constants for use with action().
         */
        export const JUST_MOVE: number = 0;

        /**
         * Constants for use with action().
         */
        export const BUILD: number = 1;

        /**
         * Constants for use with action().
         */
        export const DESTROY: number = 2;
    }

}

