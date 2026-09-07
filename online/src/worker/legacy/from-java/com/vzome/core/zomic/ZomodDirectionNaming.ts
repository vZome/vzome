import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Axis } from "../math/symmetry/Axis.js";
import { Direction } from "../math/symmetry/Direction.js";
import { DirectionNaming } from "../math/symmetry/DirectionNaming.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";

export class ZomodDirectionNaming extends DirectionNaming {
    /*private*/ mMapping: number[];

    /*private*/ mBackMap: java.util.Map<Axis, string>;

    constructor(dir: Direction, mapping: number[]) {
        super(dir, dir.getName());
        if (this.mMapping === undefined) { this.mMapping = null; }
        this.mBackMap = <any>(new java.util.HashMap<any, any>());
        this.mMapping = mapping;
        for(let i: number = 0; i < this.mMapping.length; i++) {{
            let axis: Axis = dir.getAxis$int$int(Symmetry.PLUS, this.mMapping[i]);
            this.mBackMap.put(axis, "+" + i);
            axis = dir.getAxis$int$int(Symmetry.MINUS, this.mMapping[i]);
            this.mBackMap.put(axis, "-" + i);
        };}
    }

    /**
     * 
     * @param {string} axisName
     * @return {Axis}
     */
    public getAxis(axisName: string): Axis {
        const sense: number = this.getSign(axisName);
        const index: number = this.getInteger(axisName);
        return this.getDirection().getAxis$int$int(sense, this.mMapping[index]);
    }

    public getName$com_vzome_core_math_symmetry_Axis(axis: Axis): string {
        return this.mBackMap.get(axis);
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
ZomodDirectionNaming["__class"] = "com.vzome.core.zomic.ZomodDirectionNaming";
