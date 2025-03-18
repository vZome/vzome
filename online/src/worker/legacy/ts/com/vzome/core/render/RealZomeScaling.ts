/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.render {
    /**
     * This is no longer used statically everywhere.  Now, any Shapes subclass can override
     * getCmScaling(), and RenderedModel exposes that in its own methods.  See the default
     * implementation in AbstractShapes.
     * @class
     */
    export interface RealZomeScaling {    }

    export namespace RealZomeScaling {

        export const VZOME_BLUE_DIAMETER: number = 2.0;

        export const RZOME_BLUE_DIAMETER_INCHES: number = 0.6958;

        export const RZOME_BLUE_DIAMETER_CM: number = 1.7673;

        export const RZOME_INCH_SCALING: number = RealZomeScaling.RZOME_BLUE_DIAMETER_INCHES / RealZomeScaling.VZOME_BLUE_DIAMETER;

        export const RZOME_CM_SCALING: number = RealZomeScaling.RZOME_BLUE_DIAMETER_CM / RealZomeScaling.VZOME_BLUE_DIAMETER;

        export const RZOME_MM_SCALING: number = RealZomeScaling.RZOME_CM_SCALING * 10.0;

        export const VZOME_STRUT_MODEL_BALL_DIAMETER: number = 44.36;

        export const VZOME_STRUT_MODEL_INCH_SCALING: number = RealZomeScaling.RZOME_BLUE_DIAMETER_INCHES / RealZomeScaling.VZOME_STRUT_MODEL_BALL_DIAMETER;
    }

}

