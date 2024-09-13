/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.zomic {
    export class BlackDirectionNaming extends com.vzome.core.math.symmetry.DirectionNaming {
        mMap: com.vzome.core.math.symmetry.Axis[][];

        mRedNames: com.vzome.core.math.symmetry.DirectionNaming;

        mYellowNames: com.vzome.core.math.symmetry.DirectionNaming;

        constructor(dir: com.vzome.core.math.symmetry.Direction, reds: com.vzome.core.math.symmetry.DirectionNaming, yellows: com.vzome.core.math.symmetry.DirectionNaming) {
            super(dir, dir.getName());
            if (this.mMap === undefined) { this.mMap = null; }
            if (this.mRedNames === undefined) { this.mRedNames = null; }
            if (this.mYellowNames === undefined) { this.mYellowNames = null; }
            this.mRedNames = reds;
            this.mYellowNames = yellows;
            this.mMap = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return null; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([2, dir.getSymmetry().getChiralOrder()]);
            for(let sense: number = com.vzome.core.math.symmetry.Symmetry.PLUS; sense <= com.vzome.core.math.symmetry.Symmetry.MINUS; sense++) {for(let i: number = 0; i < this.mMap[com.vzome.core.math.symmetry.Symmetry.PLUS].length; i++) {{
                const axis: com.vzome.core.math.symmetry.Axis = dir.getAxis$int$int(sense, i);
                const ry: string = this.getName$com_vzome_core_math_symmetry_Axis(axis);
                if (this.getSign(ry) === com.vzome.core.math.symmetry.Symmetry.MINUS)continue;
                const minused: boolean = /* endsWith */((str, searchString) => { let pos = str.length - searchString.length; let lastIndex = str.indexOf(searchString, pos); return lastIndex !== -1 && lastIndex === pos; })(ry, "-");
                const index: number = this.getInteger(ry);
                const sign: number = minused ? com.vzome.core.math.symmetry.Symmetry.MINUS : com.vzome.core.math.symmetry.Symmetry.PLUS;
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
         * @return {com.vzome.core.math.symmetry.Axis}
         */
        public getAxis(axisName: string): com.vzome.core.math.symmetry.Axis {
            const minused: boolean = /* endsWith */((str, searchString) => { let pos = str.length - searchString.length; let lastIndex = str.indexOf(searchString, pos); return lastIndex !== -1 && lastIndex === pos; })(axisName, "-");
            const sense: number = this.getSign(axisName);
            const ry: number = this.getInteger(axisName);
            let axis: com.vzome.core.math.symmetry.Axis = this.mMap[minused ? com.vzome.core.math.symmetry.Symmetry.MINUS : com.vzome.core.math.symmetry.Symmetry.PLUS][ry];
            if (sense === com.vzome.core.math.symmetry.Symmetry.MINUS)axis = this.getDirection().getAxis$int$int((axis.getSense() + 1) % 2, axis.getOrientation());
            return axis;
        }

        public getName$com_vzome_core_math_symmetry_Axis(axis: com.vzome.core.math.symmetry.Axis): string {
            let orn: number = axis.getOrientation();
            const redNeighbor: com.vzome.core.math.symmetry.Axis = this.mRedNames.getDirection().getAxis$int$int(axis.getSense(), orn);
            const redName: string = this.mRedNames.getName$com_vzome_core_math_symmetry_Axis(redNeighbor);
            let rot: com.vzome.core.math.symmetry.Permutation = redNeighbor.getRotationPermutation();
            if (axis.getSense() === com.vzome.core.math.symmetry.Symmetry.MINUS)rot = rot.inverse();
            orn = rot.mapIndex(orn);
            const redSign: number = this.getSign(redName);
            const yellowNeighbor: com.vzome.core.math.symmetry.Axis = this.mYellowNames.getDirection().getAxis$int$int(redSign, orn);
            let yellowName: string = this.mYellowNames.getName$com_vzome_core_math_symmetry_Axis(yellowNeighbor).substring(1);
            if (axis.getSense() === redSign)yellowName += "-";
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
    BlackDirectionNaming["__class"] = "com.vzome.core.zomic.BlackDirectionNaming";

}

