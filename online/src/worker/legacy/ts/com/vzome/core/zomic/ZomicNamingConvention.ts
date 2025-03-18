/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.zomic {
    /**
     * @author Scott Vorthmann
     * @param {com.vzome.core.math.symmetry.IcosahedralSymmetry} symm
     * @class
     * @extends com.vzome.core.math.symmetry.NamingConvention
     */
    export class ZomicNamingConvention extends com.vzome.core.math.symmetry.NamingConvention {
        public static SHORT: number = 3;

        public static MEDIUM: number = 4;

        public static LONG: number = 5;

        public constructor(symm: com.vzome.core.math.symmetry.IcosahedralSymmetry) {
            super();
            let dir: com.vzome.core.math.symmetry.Direction = symm.getDirection("red");
            const redNames: com.vzome.core.math.symmetry.DirectionNaming = new com.vzome.core.zomic.ZomodDirectionNaming(dir, [0, 1, 2, 15, 17, 46]);
            this.addDirectionNaming(redNames);
            dir = symm.getDirection("yellow");
            const yellowNames: com.vzome.core.math.symmetry.DirectionNaming = new com.vzome.core.zomic.ZomodDirectionNaming(dir, [6, 9, 12, 0, 3, 1, 14, 5, 24, 17]);
            this.addDirectionNaming(yellowNames);
            dir = symm.getDirection("blue");
            this.addDirectionNaming(new com.vzome.core.zomic.ZomodDirectionNaming(dir, [9, 12, 0, 3, 6, 1, 14, 18, 26, 52, 58, 4, 7, 2, 5]));
            dir = symm.getDirection("olive");
            this.addDirectionNaming(new com.vzome.core.math.symmetry.DirectionNaming(dir, dir.getName()));
            dir = symm.getDirection("maroon");
            this.addDirectionNaming(new com.vzome.core.math.symmetry.DirectionNaming(dir, dir.getName()));
            dir = symm.getDirection("lavender");
            this.addDirectionNaming(new com.vzome.core.math.symmetry.DirectionNaming(dir, dir.getName()));
            dir = symm.getDirection("rose");
            this.addDirectionNaming(new com.vzome.core.math.symmetry.DirectionNaming(dir, dir.getName()));
            dir = symm.getDirection("navy");
            this.addDirectionNaming(new com.vzome.core.math.symmetry.DirectionNaming(dir, dir.getName()));
            dir = symm.getDirection("turquoise");
            this.addDirectionNaming(new com.vzome.core.math.symmetry.DirectionNaming(dir, dir.getName()));
            dir = symm.getDirection("coral");
            this.addDirectionNaming(new com.vzome.core.math.symmetry.DirectionNaming(dir, dir.getName()));
            dir = symm.getDirection("sulfur");
            this.addDirectionNaming(new com.vzome.core.math.symmetry.DirectionNaming(dir, dir.getName()));
            dir = symm.getDirection("green");
            this.addDirectionNaming(new com.vzome.core.zomic.GreenDirectionNaming(dir, redNames, yellowNames));
            dir = symm.getDirection("orange");
            this.addDirectionNaming(new com.vzome.core.zomic.GreenDirectionNaming(dir, redNames, yellowNames));
            dir = symm.getDirection("purple");
            this.addDirectionNaming(new ZomicNamingConvention.ZomicNamingConvention$0(this, dir, redNames, yellowNames));
            dir = symm.getDirection("black");
            this.addDirectionNaming(new com.vzome.core.zomic.BlackDirectionNaming(dir, redNames, yellowNames));
        }
    }
    ZomicNamingConvention["__class"] = "com.vzome.core.zomic.ZomicNamingConvention";


    export namespace ZomicNamingConvention {

        export class ZomicNamingConvention$0 extends com.vzome.core.zomic.GreenDirectionNaming {
            public __parent: any;
            public getName$com_vzome_core_math_symmetry_Axis(axis: com.vzome.core.math.symmetry.Axis): string {
                let orn: number = axis.getOrientation();
                const redNeighbor: com.vzome.core.math.symmetry.Axis = this.mRedNames.getDirection().getAxis$int$int(axis.getSense(), orn);
                const redName: string = this.mRedNames.getName$com_vzome_core_math_symmetry_Axis(redNeighbor);
                const rot: com.vzome.core.math.symmetry.Permutation = redNeighbor.getRotationPermutation();
                orn = rot.mapIndex(rot.mapIndex(orn));
                if (axis.getSense() === com.vzome.core.math.symmetry.Symmetry.MINUS)orn = rot.mapIndex(orn);
                const yellowNeighbor: com.vzome.core.math.symmetry.Axis = this.mYellowNames.getDirection().getAxis$int$int(axis.getSense(), orn);
                const yellowName: string = this.mYellowNames.getName$com_vzome_core_math_symmetry_Axis(yellowNeighbor).substring(1);
                return redName + yellowName;
            }

            /**
             * 
             * @param {com.vzome.core.math.symmetry.Axis} axis
             * @return {string}
             */
            public getName(axis?: any): string {
                if (((axis != null && axis instanceof <any>com.vzome.core.math.symmetry.Axis) || axis === null)) {
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

}

