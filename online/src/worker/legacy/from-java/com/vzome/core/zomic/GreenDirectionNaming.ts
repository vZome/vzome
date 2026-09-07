import { Axis } from "../math/symmetry/Axis.js";
import { Direction } from "../math/symmetry/Direction.js";
import { DirectionNaming } from "../math/symmetry/DirectionNaming.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";

export class GreenDirectionNaming extends DirectionNaming {
    mMap: Axis[];

    mRedNames: DirectionNaming;

    mYellowNames: DirectionNaming;

    constructor(dir: Direction, reds: DirectionNaming, yellows: DirectionNaming) {
        super(dir, dir.getName());
        if (this.mMap === undefined) { this.mMap = null; }
        if (this.mRedNames === undefined) { this.mRedNames = null; }
        if (this.mYellowNames === undefined) { this.mYellowNames = null; }
        this.mRedNames = reds;
        this.mYellowNames = yellows;
        this.mMap = (s => { let a=[]; while(s-->0) a.push(null); return a; })(dir.getSymmetry().getChiralOrder());
        for(let i: number = 0; i < this.mMap.length; i++) {{
            let axis: Axis = dir.getAxis$int$int(Symmetry.PLUS, i);
            const ry: string = this.getName$com_vzome_core_math_symmetry_Axis(axis);
            const sense: number = this.getSign(ry);
            const index: number = this.getInteger(ry);
            if (sense === Symmetry.MINUS)axis = dir.getAxis$int$int(sense, i);
            this.mMap[index] = axis;
        };}
    }

    /**
     * 
     * @param {string} axisName
     * @return {Axis}
     */
    public getAxis(axisName: string): Axis {
        const sense: number = this.getSign(axisName);
        const ry: number = this.getInteger(axisName);
        let axis: Axis = this.mMap[ry];
        if (sense === Symmetry.MINUS)axis = this.getDirection().getAxis$int$int(sense, axis.getOrientation());
        return axis;
    }

    public getName$com_vzome_core_math_symmetry_Axis(axis: Axis): string {
        const redNeighbor: Axis = this.mRedNames.getDirection().getAxis$int$int(axis.getSense(), axis.getOrientation());
        const redName: string = this.mRedNames.getName$com_vzome_core_math_symmetry_Axis(redNeighbor);
        const yellowNeighbor: Axis = this.mYellowNames.getDirection().getAxis$int$int(axis.getSense(), axis.getOrientation());
        const yellowName: string = this.mYellowNames.getName$com_vzome_core_math_symmetry_Axis(yellowNeighbor).substring(1);
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
GreenDirectionNaming["__class"] = "com.vzome.core.zomic.GreenDirectionNaming";
