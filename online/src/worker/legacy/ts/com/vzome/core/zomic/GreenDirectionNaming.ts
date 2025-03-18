/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.zomic {
    export class GreenDirectionNaming extends com.vzome.core.math.symmetry.DirectionNaming {
        mMap: com.vzome.core.math.symmetry.Axis[];

        mRedNames: com.vzome.core.math.symmetry.DirectionNaming;

        mYellowNames: com.vzome.core.math.symmetry.DirectionNaming;

        constructor(dir: com.vzome.core.math.symmetry.Direction, reds: com.vzome.core.math.symmetry.DirectionNaming, yellows: com.vzome.core.math.symmetry.DirectionNaming) {
            super(dir, dir.getName());
            if (this.mMap === undefined) { this.mMap = null; }
            if (this.mRedNames === undefined) { this.mRedNames = null; }
            if (this.mYellowNames === undefined) { this.mYellowNames = null; }
            this.mRedNames = reds;
            this.mYellowNames = yellows;
            this.mMap = (s => { let a=[]; while(s-->0) a.push(null); return a; })(dir.getSymmetry().getChiralOrder());
            for(let i: number = 0; i < this.mMap.length; i++) {{
                let axis: com.vzome.core.math.symmetry.Axis = dir.getAxis$int$int(com.vzome.core.math.symmetry.Symmetry.PLUS, i);
                const ry: string = this.getName$com_vzome_core_math_symmetry_Axis(axis);
                const sense: number = this.getSign(ry);
                const index: number = this.getInteger(ry);
                if (sense === com.vzome.core.math.symmetry.Symmetry.MINUS)axis = dir.getAxis$int$int(sense, i);
                this.mMap[index] = axis;
            };}
        }

        /**
         * 
         * @param {string} axisName
         * @return {com.vzome.core.math.symmetry.Axis}
         */
        public getAxis(axisName: string): com.vzome.core.math.symmetry.Axis {
            const sense: number = this.getSign(axisName);
            const ry: number = this.getInteger(axisName);
            let axis: com.vzome.core.math.symmetry.Axis = this.mMap[ry];
            if (sense === com.vzome.core.math.symmetry.Symmetry.MINUS)axis = this.getDirection().getAxis$int$int(sense, axis.getOrientation());
            return axis;
        }

        public getName$com_vzome_core_math_symmetry_Axis(axis: com.vzome.core.math.symmetry.Axis): string {
            const redNeighbor: com.vzome.core.math.symmetry.Axis = this.mRedNames.getDirection().getAxis$int$int(axis.getSense(), axis.getOrientation());
            const redName: string = this.mRedNames.getName$com_vzome_core_math_symmetry_Axis(redNeighbor);
            const yellowNeighbor: com.vzome.core.math.symmetry.Axis = this.mYellowNames.getDirection().getAxis$int$int(axis.getSense(), axis.getOrientation());
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
    }
    GreenDirectionNaming["__class"] = "com.vzome.core.zomic.GreenDirectionNaming";

}

