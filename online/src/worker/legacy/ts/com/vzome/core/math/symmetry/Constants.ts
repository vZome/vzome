/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math.symmetry {
    /**
     * @deprecated
     * @author Scott Vorthmann
     * @class
     */
    export interface Constants {    }

    export namespace Constants {

        export const RED: number = 0;

        export const YELLOW: number = 1;

        export const BLUE: number = 2;

        export const GREEN: number = 3;

        export const ORANGE: number = 4;

        export const PURPLE: number = 5;

        export const BLACK: number = 6;

        export const NO_AXIS: number = 7;

        /**
         * Blue axes of basis.
         */
        export const X: number = 2;

        /**
         * Blue axes of basis.
         */
        export const Y: number = 5;

        /**
         * Blue axes of basis.
         */
        export const Z: number = 13;

        export const SHORT: number = 3;

        export const MEDIUM: number = 4;

        export const LONG: number = 5;

        export const JUST_RED: number = 1 << Constants.RED;

        export const JUST_YELLOW: number = 1 << Constants.YELLOW;

        export const JUST_BLUE: number = 1 << Constants.BLUE;

        export const JUST_GREEN: number = 1 << Constants.GREEN;

        export const JUST_ORANGE: number = 1 << Constants.ORANGE;

        export const JUST_PURPLE: number = 1 << Constants.PURPLE;

        export const JUST_BLACK: number = 1 << Constants.BLACK;

        export const ORIGINAL_STRUTS: number = Constants.JUST_RED | Constants.JUST_YELLOW | Constants.JUST_BLUE;

        export const ALL_STRUTS: number = Constants.ORIGINAL_STRUTS | Constants.JUST_GREEN | Constants.JUST_ORANGE | Constants.JUST_PURPLE | Constants.JUST_BLACK;

        export const RED_AXIS_YELLOW_NEIGHBORS: number[][] = [[0, 1, 2, 3, 4], [1, 0, 5, -8, 6], [2, 1, 6, -9, 7], [3, 2, 7, -5, 8], [4, 3, 8, -6, 9], [0, 4, 9, -7, 5]];

        export const AXIS_SYMMETRY: number[] = [5, 3, 2, 1, 1, 1, 1];
    }

}

