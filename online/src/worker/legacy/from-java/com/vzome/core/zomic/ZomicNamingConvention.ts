import { Axis } from "../math/symmetry/Axis.js";
import { Direction } from "../math/symmetry/Direction.js";
import { DirectionNaming } from "../math/symmetry/DirectionNaming.js";
import { IcosahedralSymmetry } from "../math/symmetry/IcosahedralSymmetry.js";
import { NamingConvention } from "../math/symmetry/NamingConvention.js";
import { Permutation } from "../math/symmetry/Permutation.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";
import { BlackDirectionNaming } from "./BlackDirectionNaming.js";
import { GreenDirectionNaming } from "./GreenDirectionNaming.js";
import { ZomodDirectionNaming } from "./ZomodDirectionNaming.js";

/**
 * @author Scott Vorthmann
 * @param {IcosahedralSymmetry} symm
 * @class
 * @extends NamingConvention
 */
export class ZomicNamingConvention extends NamingConvention {
    public static SHORT: number = 3;

    public static MEDIUM: number = 4;

    public static LONG: number = 5;

    public constructor(symm: IcosahedralSymmetry) {
        super();
        let dir: Direction = symm.getDirection("red");
        const redNames: DirectionNaming = new ZomodDirectionNaming(dir, [0, 1, 2, 15, 17, 46]);
        this.addDirectionNaming(redNames);
        dir = symm.getDirection("yellow");
        const yellowNames: DirectionNaming = new ZomodDirectionNaming(dir, [6, 9, 12, 0, 3, 1, 14, 5, 24, 17]);
        this.addDirectionNaming(yellowNames);
        dir = symm.getDirection("blue");
        this.addDirectionNaming(new ZomodDirectionNaming(dir, [9, 12, 0, 3, 6, 1, 14, 18, 26, 52, 58, 4, 7, 2, 5]));
        dir = symm.getDirection("olive");
        this.addDirectionNaming(new DirectionNaming(dir, dir.getName()));
        dir = symm.getDirection("maroon");
        this.addDirectionNaming(new DirectionNaming(dir, dir.getName()));
        dir = symm.getDirection("lavender");
        this.addDirectionNaming(new DirectionNaming(dir, dir.getName()));
        dir = symm.getDirection("rose");
        this.addDirectionNaming(new DirectionNaming(dir, dir.getName()));
        dir = symm.getDirection("navy");
        this.addDirectionNaming(new DirectionNaming(dir, dir.getName()));
        dir = symm.getDirection("turquoise");
        this.addDirectionNaming(new DirectionNaming(dir, dir.getName()));
        dir = symm.getDirection("coral");
        this.addDirectionNaming(new DirectionNaming(dir, dir.getName()));
        dir = symm.getDirection("sulfur");
        this.addDirectionNaming(new DirectionNaming(dir, dir.getName()));
        dir = symm.getDirection("green");
        this.addDirectionNaming(new GreenDirectionNaming(dir, redNames, yellowNames));
        dir = symm.getDirection("orange");
        this.addDirectionNaming(new GreenDirectionNaming(dir, redNames, yellowNames));
        dir = symm.getDirection("purple");
        this.addDirectionNaming(new ZomicNamingConvention.ZomicNamingConvention$0(this, dir, redNames, yellowNames));
        dir = symm.getDirection("black");
        this.addDirectionNaming(new BlackDirectionNaming(dir, redNames, yellowNames));
    }
}
ZomicNamingConvention["__class"] = "com.vzome.core.zomic.ZomicNamingConvention";


export namespace ZomicNamingConvention {

    export class ZomicNamingConvention$0 extends GreenDirectionNaming {
        public __parent: any;
        public getName$com_vzome_core_math_symmetry_Axis(axis: Axis): string {
            let orn: number = axis.getOrientation();
            const redNeighbor: Axis = this.mRedNames.getDirection().getAxis$int$int(axis.getSense(), orn);
            const redName: string = this.mRedNames.getName$com_vzome_core_math_symmetry_Axis(redNeighbor);
            const rot: Permutation = redNeighbor.getRotationPermutation();
            orn = rot.mapIndex(rot.mapIndex(orn));
            if (axis.getSense() === Symmetry.MINUS)orn = rot.mapIndex(orn);
            const yellowNeighbor: Axis = this.mYellowNames.getDirection().getAxis$int$int(axis.getSense(), orn);
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

        constructor(__parent: any, __arg0: any, __arg1: any, __arg2: any) {
            super(__arg0, __arg1, __arg2);
            this.__parent = __parent;
        }
    }

}
