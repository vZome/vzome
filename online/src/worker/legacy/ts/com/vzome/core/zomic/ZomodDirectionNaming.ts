/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.zomic {
    export class ZomodDirectionNaming extends com.vzome.core.math.symmetry.DirectionNaming {
        /*private*/ mMapping: number[];

        /*private*/ mBackMap: java.util.Map<com.vzome.core.math.symmetry.Axis, string>;

        constructor(dir: com.vzome.core.math.symmetry.Direction, mapping: number[]) {
            super(dir, dir.getName());
            if (this.mMapping === undefined) { this.mMapping = null; }
            this.mBackMap = <any>(new java.util.HashMap<any, any>());
            this.mMapping = mapping;
            for(let i: number = 0; i < this.mMapping.length; i++) {{
                let axis: com.vzome.core.math.symmetry.Axis = dir.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, this.mMapping[i]);
                this.mBackMap.put(axis, "+" + i);
                axis = dir.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.MINUS, this.mMapping[i]);
                this.mBackMap.put(axis, "-" + i);
            };}
        }

        /**
         * 
         * @param {string} axisName
         * @return {com.vzome.core.math.symmetry.Axis}
         */
        public getAxis(axisName: string): com.vzome.core.math.symmetry.Axis {
            const sense: number = this.getSign(axisName);
            const index: number = this.getInteger(axisName);
            return this.getDirection().getAxis$int$int(sense, this.mMapping[index]);
        }

        public getName$com_vzome_core_math_symmetry_Axis(axis: com.vzome.core.math.symmetry.Axis): string {
            return this.mBackMap.get(axis);
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
    }
    ZomodDirectionNaming["__class"] = "com.vzome.core.zomic.ZomodDirectionNaming";

}

