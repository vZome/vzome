import { java, javaemul } from "../../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Axis } from "./Axis.js";
import { Direction } from "./Direction.js";
import { Symmetry } from "./Symmetry.js";

/**
 * @author Scott Vorthmann
 * @param {Direction} dir
 * @param {string} name
 * @class
 */
export class DirectionNaming {
    static SIGN: string[]; public static SIGN_$LI$(): string[] { if (DirectionNaming.SIGN == null) { DirectionNaming.SIGN = ["+", "-"]; }  return DirectionNaming.SIGN; }

    /*private*/ mName: string;

    /*private*/ mDirection: Direction;

    public constructor(dir: Direction, name: string) {
        if (this.mName === undefined) { this.mName = null; }
        if (this.mDirection === undefined) { this.mDirection = null; }
        this.mName = name;
        this.mDirection = dir;
    }

    public getName$(): string {
        return this.mName;
    }

    /**
     * Default behavior.
     * @param {string} axisName
     * @return
     * @return {Axis}
     */
    public getAxis(axisName: string): Axis {
        return this.mDirection.getAxis$int$int(this.getSign(axisName), this.getInteger(axisName));
    }

    getSign(axisName: string): number {
        if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(axisName, "-"))return Symmetry.MINUS;
        return Symmetry.PLUS;
    }

    getInteger(axisName: string): number {
        if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(axisName, "-") || /* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(axisName, "+"))return javaemul.internal.IntegerHelper.parseInt(axisName.substring(1));
        return javaemul.internal.IntegerHelper.parseInt(axisName);
    }

    public getName$com_vzome_core_math_symmetry_Axis(axis: Axis): string {
        const sign: string = DirectionNaming.SIGN_$LI$()[axis.getSense()];
        return sign + axis.getOrientation();
    }

    public getName(axis?: any): string {
        if (((axis != null && axis instanceof <any>Axis) || axis === null)) {
            return <any>this.getName$com_vzome_core_math_symmetry_Axis(axis);
        } else if (axis === undefined) {
            return <any>this.getName$();
        } else throw new Error('invalid overload');
    }

    public getFullName(axis: Axis): string {
        return this.mName + " " + this.getName$com_vzome_core_math_symmetry_Axis(axis);
    }

    public getDirection(): Direction {
        return this.mDirection;
    }
}
DirectionNaming["__class"] = "com.vzome.core.math.symmetry.DirectionNaming";
