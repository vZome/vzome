/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math.symmetry {
    /**
     * A single orbit in a Symmetry group.
     * Consists of a collection of zones (Axis), each of which is an infinite family of parallel lines.
     * There is a prototype zone (Axis) which has index==0; ideally, that zone should
     * have normal vector =~ (1,e,e), for 0 < e << 1, but this is not true, historically.
     * 
     * The orbit is represented by a single "dot" on the fundamental region triangle, and typically
     * struts in the orbit are rendered with a shape and color unique from all other orbits.
     * 
     * @author Scott Vorthmann
     * @param {string} name
     * @param {*} group
     * @param {number} prototype
     * @param {number} rotatedPrototype
     * @param {com.vzome.core.algebra.AlgebraicVector} vector
     * @param {boolean} isStd
     * @class
     */
    export class Direction implements java.lang.Comparable<Direction>, java.lang.Iterable<com.vzome.core.math.symmetry.Axis> {
        /**
         * 
         * @return {number}
         */
        public hashCode(): number {
            const prime: number = 31;
            let result: number = 1;
            result = prime * result + this.index;
            result = prime * result + ((this.mPrototype == null) ? 0 : /* hashCode */(<any>((o: any) => { if (o.hashCode) { return o.hashCode(); } else { return o.toString().split('').reduce((prevHash, currVal) => (((prevHash << 5) - prevHash) + currVal.charCodeAt(0))|0, 0); }})(this.mPrototype)));
            result = prime * result + ((this.mSymmetryGroup == null) ? 0 : /* hashCode */(<any>((o: any) => { if (o.hashCode) { return o.hashCode(); } else { return o.toString().split('').reduce((prevHash, currVal) => (((prevHash << 5) - prevHash) + currVal.charCodeAt(0))|0, 0); }})(this.mSymmetryGroup)));
            return result;
        }

        /**
         * 
         * @param {*} obj
         * @return {boolean}
         */
        public equals(obj: any): boolean {
            if (this === obj){
                return true;
            }
            if (obj == null){
                return false;
            }
            if ((<any>this.constructor) !== (<any>obj.constructor)){
                return false;
            }
            const other: Direction = <Direction>obj;
            if (this.index !== other.index){
                return false;
            }
            if (this.mPrototype == null){
                if (other.mPrototype != null){
                    return false;
                }
            } else if (!this.mPrototype.equals(other.mPrototype)){
                return false;
            }
            if (this.mSymmetryGroup == null){
                if (other.mSymmetryGroup != null){
                    return false;
                }
            } else if (!/* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(this.mSymmetryGroup,other.mSymmetryGroup))){
                return false;
            }
            return true;
        }

        /*private*/ mName: string;

        /*private*/ canonicalName: string;

        /*private*/ zoneNames: com.vzome.core.math.symmetry.Axis[][][];

        /*private*/ zoneVectors: java.util.Map<string, com.vzome.core.math.symmetry.Axis>;

        /*private*/ mSymmetryGroup: com.vzome.core.math.symmetry.Symmetry;

        /*private*/ mPrototype: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ mStandard: boolean;

        /*private*/ mAutomatic: boolean;

        /*private*/ __hasHalfSizes: boolean;

        /*private*/ scaleNames: string[];

        public scales: com.vzome.core.algebra.AlgebraicNumber[];

        /*private*/ unitLength: com.vzome.core.algebra.AlgebraicNumber;

        /*private*/ unitLengthReciprocal: com.vzome.core.algebra.AlgebraicNumber;

        /*private*/ dotX: number;

        /*private*/ dotY: number;

        static logger: java.util.logging.Logger; public static logger_$LI$(): java.util.logging.Logger { if (Direction.logger == null) { Direction.logger = java.util.logging.Logger.getLogger("com.vzome.core.math.symmetry.Orbit"); }  return Direction.logger; }

        public setAutomatic(auto: boolean) {
            this.mAutomatic = auto;
        }

        public isAutomatic(): boolean {
            return this.mAutomatic;
        }

        public isStandard(): boolean {
            return this.mStandard;
        }

        static globalIndex: number = 0;

        /*private*/ index: number;

        /*private*/ prototype: number;

        /*private*/ rotatedPrototype: number;

        public canonicalize: number;

        /*private*/ needsCanonicalization: boolean;

        public constructor(name: string, group: com.vzome.core.math.symmetry.Symmetry, prototype: number, rotatedPrototype: number, vector: com.vzome.core.algebra.AlgebraicVector, isStd: boolean) {
            if (this.mName === undefined) { this.mName = null; }
            if (this.canonicalName === undefined) { this.canonicalName = null; }
            if (this.zoneNames === undefined) { this.zoneNames = null; }
            this.zoneVectors = <any>(new java.util.HashMap<any, any>());
            if (this.mSymmetryGroup === undefined) { this.mSymmetryGroup = null; }
            if (this.mPrototype === undefined) { this.mPrototype = null; }
            if (this.mStandard === undefined) { this.mStandard = false; }
            if (this.mAutomatic === undefined) { this.mAutomatic = false; }
            if (this.__hasHalfSizes === undefined) { this.__hasHalfSizes = false; }
            this.scaleNames = ["shorter", "short", "medium", "long"];
            this.scales = (s => { let a=[]; while(s-->0) a.push(null); return a; })(this.scaleNames.length);
            if (this.unitLength === undefined) { this.unitLength = null; }
            if (this.unitLengthReciprocal === undefined) { this.unitLengthReciprocal = null; }
            this.dotX = -999.0;
            this.dotY = -999.0;
            if (this.index === undefined) { this.index = 0; }
            if (this.prototype === undefined) { this.prototype = 0; }
            if (this.rotatedPrototype === undefined) { this.rotatedPrototype = 0; }
            this.canonicalize = 0;
            this.needsCanonicalization = false;
            this.prototype = prototype;
            this.rotatedPrototype = rotatedPrototype;
            this.index = Direction.globalIndex++;
            this.mStandard = isStd;
            this.mName = name;
            this.canonicalName = null;
            this.mSymmetryGroup = group;
            for(let i: number = 0; i < this.scales.length; i++) {{
                this.scales[i] = this.mSymmetryGroup.getField()['createPower$int'](i - 1);
            };}
            this.mPrototype = vector;
            const order: number = group.getChiralOrder();
            this.zoneNames = <any> (function(dims) { let allocate = function(dims) { if (dims.length === 0) { return null; } else { let array = []; for(let i = 0; i < dims[0]; i++) { array.push(allocate(dims.slice(1))); } return array; }}; return allocate(dims);})([2, 2, order]);
        }

        /*private*/ getZoneVectors(): java.util.Map<string, com.vzome.core.math.symmetry.Axis> {
            if (this.zoneVectors.isEmpty()){
                if (Direction.logger_$LI$().isLoggable(java.util.logging.Level.FINER))Direction.logger_$LI$().finer("creating zone vectors: " + this.toString());
                for(let i: number = 0; i < this.mSymmetryGroup.getChiralOrder(); i++) {{
                    const transform: com.vzome.core.algebra.AlgebraicMatrix = this.mSymmetryGroup.getMatrix(i);
                    const perm: com.vzome.core.math.symmetry.Permutation = this.mSymmetryGroup.getPermutation(i);
                    const j: number = perm.mapIndex(this.prototype);
                    const rotated: number = perm.mapIndex(this.rotatedPrototype);
                    const normal: com.vzome.core.algebra.AlgebraicVector = transform.timesColumn(this.mPrototype);
                    const rot: number = this.mSymmetryGroup.getMapping(j, rotated);
                    this.createAxis$int$int$com_vzome_core_algebra_AlgebraicVector(j, rot, normal);
                };}
            }
            return this.zoneVectors;
        }

        /**
         * 
         * @return {string}
         */
        public toString(): string {
            return this.mSymmetryGroup.getName() + " " + this.mName;
        }

        public getPrototype(): com.vzome.core.algebra.AlgebraicVector {
            return this.mPrototype;
        }

        /**
         * 
         * @return {*}
         */
        public iterator(): java.util.Iterator<com.vzome.core.math.symmetry.Axis> {
            return this.getZoneVectors().values().iterator();
        }

        public getSymmetry(): com.vzome.core.math.symmetry.Symmetry {
            return this.mSymmetryGroup;
        }

        public getName(): string {
            return this.mName;
        }

        public getCanonicalName(): string {
            if (this.canonicalName == null){
                const canonicalAxis: com.vzome.core.math.symmetry.Axis = this.getCanonicalAxis(0, 0);
                const vector: com.vzome.core.algebra.AlgebraicVector = canonicalAxis.normal();
                const x: com.vzome.core.algebra.AlgebraicNumber = vector.getComponent(0);
                let y: com.vzome.core.algebra.AlgebraicNumber = vector.getComponent(1);
                let z: com.vzome.core.algebra.AlgebraicNumber = vector.getComponent(2).negate();
                if (x.isZero()){
                    this.canonicalName = this.mName;
                } else {
                    y = y.dividedBy(x);
                    z = z.dividedBy(x);
                    this.canonicalName = "[" + /* replace */java.util.Arrays.toString(y.toTrailingDivisor()).split(" ").join("") + "," + /* replace */java.util.Arrays.toString(z.toTrailingDivisor()).split(" ").join("") + "]";
                    Direction.logger_$LI$().finer("Direction.canonicalName: " + this.canonicalName);
                }
            }
            return this.canonicalName;
        }

        public getAxis$com_vzome_core_algebra_AlgebraicVector(vector: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.math.symmetry.Axis {
            for(let index=this.getZoneVectors().values().iterator();index.hasNext();) {
                let axis = index.next();
                {
                    const normal: com.vzome.core.algebra.AlgebraicVector = axis.normal();
                    if (com.vzome.core.algebra.AlgebraicVectors.areParallel(normal, vector)){
                        const dotProd: com.vzome.core.algebra.AlgebraicNumber = normal.dot(vector);
                        if (dotProd.evaluate() > 0){
                            return axis;
                        } else {
                            const principalReflection: com.vzome.core.algebra.AlgebraicMatrix = this.mSymmetryGroup.getPrincipalReflection();
                            if (principalReflection == null){
                                const opp: number = (axis.getSense() + 1) % 2;
                                return this.getAxis$int$int(opp, axis.getOrientation());
                            } else {
                                return this.getAxis$int$int$boolean(axis.getSense(), axis.getOrientation(), !axis.isOutbound());
                            }
                        }
                    }
                }
            }
            return null;
        }

        public getAxis$com_vzome_core_math_RealVector(vector: com.vzome.core.math.RealVector): com.vzome.core.math.symmetry.Axis {
            return this.getSymmetry()['getAxis$com_vzome_core_math_RealVector$java_util_Collection'](vector, java.util.Collections.singleton<any>(this));
        }

        getChiralAxis(vector: com.vzome.core.math.RealVector): com.vzome.core.math.symmetry.Axis {
            if (com.vzome.core.math.RealVector.ORIGIN_$LI$().equals(vector)){
                return null;
            }
            const vectorLength: number = vector.length();
            const checked: java.util.Set<com.vzome.core.math.symmetry.Axis> = <any>(new java.util.HashSet<any>());
            let closestOrientation: number = 0;
            let closestSense: number = com.vzome.core.math.symmetry.Symmetry.PLUS;
            let closestAxis: com.vzome.core.math.symmetry.Axis = this.getCanonicalAxis(com.vzome.core.math.symmetry.Symmetry.PLUS, 0);
            checked.add(closestAxis);
            let axisV: com.vzome.core.math.RealVector = closestAxis.normal().toRealVector();
            let maxCosine: number = vector.dot(axisV) / (vectorLength * axisV.length());
            if (maxCosine < 0){
                closestAxis = this.getCanonicalAxis(com.vzome.core.math.symmetry.Symmetry.MINUS, 0);
                closestSense = com.vzome.core.math.symmetry.Symmetry.MINUS;
                checked.add(closestAxis);
                axisV = closestAxis.normal().toRealVector();
                maxCosine = vector.dot(axisV) / (vectorLength * axisV.length());
            }
            const finished: boolean = false;
            while((!finished)) {{
                const incidentOrientations: number[] = this.getSymmetry().getIncidentOrientations(closestOrientation);
                if (incidentOrientations == null){
                    break;
                }
                const reverseSense: number = (closestSense + 1) % 2;
                for(let index = 0; index < incidentOrientations.length; index++) {
                    let i = incidentOrientations[index];
                    {
                        const neighbor: com.vzome.core.math.symmetry.Axis = this.getCanonicalAxis(reverseSense, i);
                        if (checked.contains(neighbor))continue;
                        checked.add(neighbor);
                        axisV = neighbor.normal().toRealVector();
                        const cosine: number = vector.dot(axisV) / (vectorLength * axisV.length());
                        if (cosine > maxCosine){
                            maxCosine = cosine;
                            closestAxis = neighbor;
                            closestOrientation = i;
                            closestSense = reverseSense;
                        }
                    }
                }
                if (reverseSense !== closestSense){
                    return closestAxis;
                }
            }};
            return this.getAxisBruteForce(vector);
        }

        getAxisBruteForce(vector: com.vzome.core.math.RealVector): com.vzome.core.math.symmetry.Axis {
            let closestAxis: com.vzome.core.math.symmetry.Axis = null;
            let maxCosine: number = -1.0;
            for(let index=this.iterator();index.hasNext();) {
                let axis = index.next();
                {
                    const axisV: com.vzome.core.math.RealVector = axis.normal().toRealVector();
                    const cosine: number = vector.dot(axisV) / (vector.length() * axisV.length());
                    if (cosine > maxCosine){
                        maxCosine = cosine;
                        closestAxis = axis;
                    }
                }
            }
            return closestAxis;
        }

        zoneInitialized(sense: number, unit: number): boolean {
            return this.zoneNames[1][sense][unit] != null;
        }

        public getAxis$int$int(sense: number, index: number): com.vzome.core.math.symmetry.Axis {
            return this.getAxis$int$int$boolean(sense, index, true);
        }

        public getAxis$int$int$boolean(sense: number, index: number, outbound: boolean): com.vzome.core.math.symmetry.Axis {
            this.getZoneVectors();
            return this.zoneNames[outbound ? 1 : 0][sense][index];
        }

        public getAxis(sense?: any, index?: any, outbound?: any): com.vzome.core.math.symmetry.Axis {
            if (((typeof sense === 'number') || sense === null) && ((typeof index === 'number') || index === null) && ((typeof outbound === 'boolean') || outbound === null)) {
                return <any>this.getAxis$int$int$boolean(sense, index, outbound);
            } else if (((typeof sense === 'number') || sense === null) && ((typeof index === 'number') || index === null) && outbound === undefined) {
                return <any>this.getAxis$int$int(sense, index);
            } else if (((sense != null && sense instanceof <any>com.vzome.core.algebra.AlgebraicVector) || sense === null) && index === undefined && outbound === undefined) {
                return <any>this.getAxis$com_vzome_core_algebra_AlgebraicVector(sense);
            } else if (((sense != null && sense instanceof <any>com.vzome.core.math.RealVector) || sense === null) && index === undefined && outbound === undefined) {
                return <any>this.getAxis$com_vzome_core_math_RealVector(sense);
            } else throw new Error('invalid overload');
        }

        public withCorrection(): Direction {
            this.needsCanonicalization = true;
            return this;
        }

        /**
         * Get the axis that protrudes from the canonical direction on the zome ball.
         * Many Directions (orbits) are created without regard to whether "axis 0" actually sticks out
         * of the ball in the fundamental domain with index 0.
         * @param {number} sense
         * @param {number} index
         * @return
         * @return {com.vzome.core.math.symmetry.Axis}
         */
        public getCanonicalAxis(sense: number, index: number): com.vzome.core.math.symmetry.Axis {
            if (this.needsCanonicalization){
                const treatedAs0: com.vzome.core.math.symmetry.Axis = this.getAxisBruteForce(com.vzome.core.math.RealVector.DIRECTION_0_$LI$());
                this.canonicalize = treatedAs0.getOrientation();
                if (treatedAs0.getSense() === com.vzome.core.math.symmetry.Symmetry.MINUS)this.canonicalize *= -1;
                this.needsCanonicalization = false;
            }
            if (this.canonicalize !== 0){
                if (this.canonicalize < 0)sense = (sense + 1) % 2;
                const target: com.vzome.core.math.symmetry.Permutation = this.mSymmetryGroup.getPermutation(index);
                index = target.mapIndex(Math.abs(this.canonicalize));
            }
            return this.getAxis$int$int(sense, index);
        }

        public createAxis$int$int$int_A_A(orientation: number, rotation: number, norm: number[][]) {
            const aNorm: com.vzome.core.algebra.AlgebraicVector = this.mSymmetryGroup.getField().createVector(norm);
            this.createAxis$int$int$com_vzome_core_algebra_AlgebraicVector(orientation, rotation, aNorm);
        }

        public createAxis(orientation?: any, rotation?: any, norm?: any) {
            if (((typeof orientation === 'number') || orientation === null) && ((typeof rotation === 'number') || rotation === null) && ((norm != null && norm instanceof <any>Array && (norm.length == 0 || norm[0] == null ||norm[0] instanceof Array)) || norm === null)) {
                return <any>this.createAxis$int$int$int_A_A(orientation, rotation, norm);
            } else if (((typeof orientation === 'number') || orientation === null) && ((typeof rotation === 'number') || rotation === null) && ((norm != null && norm instanceof <any>com.vzome.core.algebra.AlgebraicVector) || norm === null)) {
                return <any>this.createAxis$int$int$com_vzome_core_algebra_AlgebraicVector(orientation, rotation, norm);
            } else throw new Error('invalid overload');
        }

        public createAxis$int$int$com_vzome_core_algebra_AlgebraicVector(orientation: number, rotation: number, norm: com.vzome.core.algebra.AlgebraicVector) {
            let perm: com.vzome.core.math.symmetry.Permutation = this.mSymmetryGroup.getPermutation(rotation);
            this.recordZone(this, orientation, com.vzome.core.math.symmetry.Symmetry.PLUS, rotation, perm, norm, true);
            const inversion: com.vzome.core.algebra.AlgebraicMatrix = this.mSymmetryGroup.getPrincipalReflection();
            if (inversion == null){
                if (perm != null)perm = perm.inverse();
                this.recordZone(this, orientation, com.vzome.core.math.symmetry.Symmetry.MINUS, rotation, perm, norm.negate(), true);
            } else {
                let reverseRotation: number = rotation;
                let reversePerm: com.vzome.core.math.symmetry.Permutation = perm;
                if (perm != null){
                    reversePerm = perm.inverse();
                    reverseRotation = perm.mapIndex(0);
                }
                this.recordZone(this, orientation, com.vzome.core.math.symmetry.Symmetry.PLUS, reverseRotation, reversePerm, norm.negate(), false);
                let reflectedNorm: com.vzome.core.algebra.AlgebraicVector = inversion.timesColumn(norm);
                this.recordZone(this, orientation, com.vzome.core.math.symmetry.Symmetry.MINUS, reverseRotation, reversePerm, reflectedNorm, true);
                reflectedNorm = reflectedNorm.negate();
                this.recordZone(this, orientation, com.vzome.core.math.symmetry.Symmetry.MINUS, rotation, perm, reflectedNorm, false);
            }
        }

        /*private*/ recordZone(dir: Direction, orientation: number, sense: number, rotation: number, rotPerm: com.vzome.core.math.symmetry.Permutation, normal: com.vzome.core.algebra.AlgebraicVector, outbound: boolean) {
            let zone: com.vzome.core.math.symmetry.Axis = this.zoneVectors.get(normal.toString());
            if (zone == null){
                zone = new com.vzome.core.math.symmetry.Axis(this, orientation, sense, rotation, rotPerm, normal, outbound);
                this.zoneVectors.put(normal.toString(), zone);
                if (Direction.logger_$LI$().isLoggable(java.util.logging.Level.FINER))Direction.logger_$LI$().finer("creating zone " + zone.toString() + " " + normal.toString());
            } else {
                if (outbound && !zone.isOutbound()){
                    const oldName: string = zone.toString();
                    zone.rename(sense, orientation, true);
                    if (Direction.logger_$LI$().isLoggable(java.util.logging.Level.FINER))Direction.logger_$LI$().finer("zone " + oldName + " upgraded to " + zone.toString());
                } else if (zone.isOutbound() && !outbound){
                    if (Direction.logger_$LI$().isLoggable(java.util.logging.Level.FINEST))Direction.logger_$LI$().finest("zone " + zone.toString() + " aliased as " + ((sense === com.vzome.core.math.symmetry.Axis.MINUS) ? "-" : "") + orientation + (outbound ? "" : "i"));
                } else if (sense === com.vzome.core.math.symmetry.Axis.PLUS && (zone.getSense() === com.vzome.core.math.symmetry.Axis.MINUS)){
                    const oldName: string = zone.toString();
                    zone.rename(sense, orientation, outbound);
                    if (Direction.logger_$LI$().isLoggable(java.util.logging.Level.FINER))Direction.logger_$LI$().finer("zone " + oldName + " upgraded to " + zone.toString());
                } else {
                    if (Direction.logger_$LI$().isLoggable(java.util.logging.Level.FINEST))Direction.logger_$LI$().finest("zone " + zone.toString() + " aliased as " + ((sense === com.vzome.core.math.symmetry.Axis.MINUS) ? "-" : "") + orientation + (outbound ? "" : "i"));
                }
            }
            this.zoneNames[outbound ? 1 : 0][sense][orientation] = zone;
        }

        /**
         * 
         * @param {com.vzome.core.math.symmetry.Direction} other
         * @return {number}
         */
        public compareTo(other: Direction): number {
            return this.index - other.index;
        }

        public setHalfSizes(value: boolean) {
            this.__hasHalfSizes = value;
        }

        public hasHalfSizes(): boolean {
            return this.__hasHalfSizes;
        }

        public setScaleNames(names: string[]) {
            for(let i: number = 0; i < names.length; i++) {{
                this.scaleNames[i] = names[i];
            };}
        }

        public getScaleName(scale: number): string {
            if (scale < this.scaleNames.length && scale >= 0)return this.scaleNames[scale]; else return "scale " + (scale - 1);
        }

        public setUnitLength(unitLength: com.vzome.core.algebra.AlgebraicNumber) {
            this.unitLength = unitLength;
            this.unitLengthReciprocal = unitLength.reciprocal();
        }

        public getUnitLength(): com.vzome.core.algebra.AlgebraicNumber {
            if (this.unitLength == null)return this.mSymmetryGroup.getField().one(); else return this.unitLength;
        }

        public static USER_SCALE: number = 3;

        public getLengthInUnits(rawLength: com.vzome.core.algebra.AlgebraicNumber): com.vzome.core.algebra.AlgebraicNumber {
            const field: com.vzome.core.algebra.AlgebraicField = this.mSymmetryGroup.getField();
            const scaledLength: com.vzome.core.algebra.AlgebraicNumber = rawLength['times$com_vzome_core_algebra_AlgebraicNumber'](field['createPower$int'](-Direction.USER_SCALE));
            if (this.unitLength == null)return scaledLength; else return scaledLength['times$com_vzome_core_algebra_AlgebraicNumber'](this.unitLengthReciprocal);
        }

        public getLengthName(length: com.vzome.core.algebra.AlgebraicNumber): string {
            for(let i: number = 0; i < this.scales.length; i++) {{
                if (/* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(this.scales[i],length))){
                    return this.scaleNames[i];
                }
            };}
            return "";
        }

        public getLengthExpression(buf: java.lang.StringBuffer, length: com.vzome.core.algebra.AlgebraicNumber) {
            const bufLen: number = buf.length();
            buf.append(this.getLengthName(length));
            if (buf.length() === bufLen){
                buf.append(" ");
            }
            buf.append(":");
            length.getNumberExpression(buf, com.vzome.core.algebra.AlgebraicField.EXPRESSION_FORMAT);
        }

        public getDotX(): number {
            return this.dotX;
        }

        public getDotY(): number {
            return this.dotY;
        }

        public setDotLocation(x: number, y: number) {
            this.dotX = x;
            this.dotY = y;
        }
    }
    Direction["__class"] = "com.vzome.core.math.symmetry.Direction";
    Direction["__interfaces"] = ["java.lang.Comparable","java.lang.Iterable"];


}

