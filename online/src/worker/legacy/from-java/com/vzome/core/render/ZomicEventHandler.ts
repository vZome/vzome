import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { Axis } from "../math/symmetry/Axis.js";
import { Permutation } from "../math/symmetry/Permutation.js";

/**
 * @author vorth
 * @class
 */
export interface ZomicEventHandler {
    step(axis: Axis, length: AlgebraicNumber);

    /**
     * 
     * @param {Axis} axis
     * @param {number} steps
     */
    rotate(axis: Axis, steps: number);

    /**
     * Reflect through a blue axis, or through the current location point
     * if blueAxis == null.
     * @param {Axis} blueAxis
     */
    reflect(blueAxis: Axis);

    permute(permutation: Permutation, sense: number);

    scale(scale: AlgebraicNumber);

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
