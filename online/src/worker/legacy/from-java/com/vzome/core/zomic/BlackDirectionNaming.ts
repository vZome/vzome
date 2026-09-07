import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Axis } from "../math/symmetry/Axis.js";
import { Direction } from "../math/symmetry/Direction.js";
import { DirectionNaming } from "../math/symmetry/DirectionNaming.js";
import { Permutation } from "../math/symmetry/Permutation.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";

export class BlackDirectionNaming extends DirectionNaming {
    mMap: Axis[][];

    mRedNames: DirectionNaming;

    mYellowNames: DirectionNaming;

    constructor(dir: Direction, reds: DirectionNaming, yellows: DirectionNaming) {
        super(dir, dir.getName());
        if (this.mMap === undefined) { this.mMap = null; }
        if (this.mRedNames === undefined) { this.mRedNames = null; }
        if (this.mYellowNames === undefined) { this.mYellowNames = null; }
        this.mRedNames = reds;
        this.mYellowNames = yellows;
        this.mMap = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return null; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([2, dir.getSymmetry().getChiralOrder()]);
        for(let sense: number = Symmetry.PLUS; sense <= Symmetry.MINUS; sense++) {for(let i: number = 0; i < this.mMap[Symmetry.PLUS].length; i++) {{
            const axis: Axis = dir.getAxis$int$int(sense, i);
            const ry: string = this.getName$com_vzome_core_math_symmetry_Axis(axis);
            if (this.getSign(ry) === Symmetry.MINUS)continue;
            const minused: boolean = /* endsWith */((str, searchString) => { let pos = str.length - searchString.length; let lastIndex = str.indexOf(searchString, pos); return lastIndex !== -1 && lastIndex === pos; })(ry, "-");
            const index: number = this.getInteger(ry);
            const sign: number = minused ? Symmetry.MINUS : Symmetry.PLUS;
            this.mMap[sign][index] = axis;
        };};}
    }

    /**
     * 
     * @param {string} axisName
     * @return {number}
     */
    getInteger(axisName: string): number {
        if (/* endsWith */((str, searchString) => { let pos = str.length - searchString.length; let lastIndex = str.indexOf(searchString, pos); return lastIndex !== -1 && lastIndex === pos; })(axisName, "-") || /* endsWith */((str, searchString) => { let pos = str.length - searchString.length; let lastIndex = str.indexOf(searchString, pos); return lastIndex !== -1 && lastIndex === pos; })(axisName, "+"))axisName = axisName.substring(0, axisName.length - 1);
        if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(axisName, "-") || /* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(axisName, "+"))return javaemul.internal.IntegerHelper.parseInt(axisName.substring(1));
        return javaemul.internal.IntegerHelper.parseInt(axisName);
    }

    /**
     * 
     * @param {string} axisName
     * @return {Axis}
     */
    public getAxis(axisName: string): Axis {
        const minused: boolean = /* endsWith */((str, searchString) => { let pos = str.length - searchString.length; let lastIndex = str.indexOf(searchString, pos); return lastIndex !== -1 && lastIndex === pos; })(axisName, "-");
        const sense: number = this.getSign(axisName);
        const ry: number = this.getInteger(axisName);
        let axis: Axis = this.mMap[minused ? Symmetry.MINUS : Symmetry.PLUS][ry];
        if (sense === Symmetry.MINUS)axis = this.getDirection().getAxis$int$int((axis.getSense() + 1) % 2, axis.getOrientation());
        return axis;
    }

    public getName$com_vzome_core_math_symmetry_Axis(axis: Axis): string {
        let orn: number = axis.getOrientation();
        const redNeighbor: Axis = this.mRedNames.getDirection().getAxis$int$int(axis.getSense(), orn);
        const redName: string = this.mRedNames.getName$com_vzome_core_math_symmetry_Axis(redNeighbor);
        let rot: Permutation = redNeighbor.getRotationPermutation();
        if (axis.getSense() === Symmetry.MINUS)rot = rot.inverse();
        orn = rot.mapIndex(orn);
        const redSign: number = this.getSign(redName);
        const yellowNeighbor: Axis = this.mYellowNames.getDirection().getAxis$int$int(redSign, orn);
        let yellowName: string = this.mYellowNames.getName$com_vzome_core_math_symmetry_Axis(yellowNeighbor).substring(1);
        if (axis.getSense() === redSign)yellowName += "-";
        return redName + yellowName;
    }

    /**
     * 
     * @param {Axis} axis
     * @return {string}
     */
    public getName(axis?: any): string {
        if (((axis != null && axis instanceof <any>Axis) || axis === null)) {
            return <any>this.getName$com_vzome_core_math_symmetry_Axis(axis);
        } else if (axis === undefined) {
            return <any>this.getName$();
        } else throw new Error('invalid overload');
    }
}
BlackDirectionNaming["__class"] = "com.vzome.core.zomic.BlackDirectionNaming";
