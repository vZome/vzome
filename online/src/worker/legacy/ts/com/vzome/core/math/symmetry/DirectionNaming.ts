/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math.symmetry {
    /**
     * @author Scott Vorthmann
     * @param {com.vzome.core.math.symmetry.Direction} dir
     * @param {string} name
     * @class
     */
    export class DirectionNaming {
        static SIGN: string[]; public static SIGN_$LI$(): string[] { if (DirectionNaming.SIGN == null) { DirectionNaming.SIGN = ["+", "-"]; }  return DirectionNaming.SIGN; }

        /*private*/ mName: string;

        /*private*/ mDirection: com.vzome.core.math.symmetry.Direction;

        public constructor(dir: com.vzome.core.math.symmetry.Direction, name: string) {
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
         * @return {com.vzome.core.math.symmetry.Axis}
         */
        public getAxis(axisName: string): com.vzome.core.math.symmetry.Axis {
            return this.mDirection.getAxis$int$int(this.getSign(axisName), this.getInteger(axisName));
        }

        getSign(axisName: string): number {
            if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(axisName, "-"))return com.vzome.core.math.symmetry.Symmetry.MINUS;
            return com.vzome.core.math.symmetry.Symmetry.PLUS;
        }

        getInteger(axisName: string): number {
            if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(axisName, "-") || /* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(axisName, "+"))return javaemul.internal.IntegerHelper.parseInt(axisName.substring(1));
            return javaemul.internal.IntegerHelper.parseInt(axisName);
        }

        public getName$com_vzome_core_math_symmetry_Axis(axis: com.vzome.core.math.symmetry.Axis): string {
            const sign: string = DirectionNaming.SIGN_$LI$()[axis.getSense()];
            return sign + axis.getOrientation();
        }

        public getName(axis?: any): string {
            if (((axis != null && axis instanceof <any>com.vzome.core.math.symmetry.Axis) || axis === null)) {
                return <any>this.getName$com_vzome_core_math_symmetry_Axis(axis);
            } else if (axis === undefined) {
                return <any>this.getName$();
            } else throw new Error('invalid overload');
        }

        public getFullName(axis: com.vzome.core.math.symmetry.Axis): string {
            return this.mName + " " + this.getName$com_vzome_core_math_symmetry_Axis(axis);
        }

        public getDirection(): com.vzome.core.math.symmetry.Direction {
            return this.mDirection;
        }
    }
    DirectionNaming["__class"] = "com.vzome.core.math.symmetry.DirectionNaming";

}

