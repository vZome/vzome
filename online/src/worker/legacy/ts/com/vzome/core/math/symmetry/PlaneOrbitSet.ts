/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.math.symmetry {
    export class PlaneOrbitSet extends com.vzome.core.math.symmetry.OrbitSet {
        /*private*/ delegate: com.vzome.core.math.symmetry.OrbitSet;

        /*private*/ normal: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ __zones: java.util.Set<com.vzome.core.math.symmetry.Axis>;

        public constructor(delegate: com.vzome.core.math.symmetry.OrbitSet, normal: com.vzome.core.algebra.AlgebraicVector) {
            super(delegate.getSymmetry());
            if (this.delegate === undefined) { this.delegate = null; }
            if (this.normal === undefined) { this.normal = null; }
            this.__zones = <any>(new java.util.HashSet<any>());
            this.delegate = delegate;
            this.normal = normal;
            for(let index=delegate.getDirections().iterator();index.hasNext();) {
                let dir = index.next();
                {
                    for(let index=dir.iterator();index.hasNext();) {
                        let axis = index.next();
                        {
                            if (axis.normal().dot(this.normal).isZero())this.__zones.add(axis);
                        }
                    }
                }
            }
        }

        public zones(): java.util.Iterator<com.vzome.core.math.symmetry.Axis> {
            return this.__zones.iterator();
        }

        /**
         * 
         * @param {com.vzome.core.math.RealVector} vector
         * @return {com.vzome.core.math.symmetry.Axis}
         */
        public getAxis(vector: com.vzome.core.math.RealVector): com.vzome.core.math.symmetry.Axis {
            if (com.vzome.core.math.RealVector.ORIGIN_$LI$().equals(vector)){
                return null;
            }
            let maxCosine: number = -1.0;
            let closest: com.vzome.core.math.symmetry.Axis = null;
            for(let index=this.__zones.iterator();index.hasNext();) {
                let axis = index.next();
                {
                    const axisV: com.vzome.core.math.RealVector = axis.normal().toRealVector();
                    const cosine: number = vector.dot(axisV) / (vector.length() * axisV.length());
                    if (cosine > maxCosine){
                        maxCosine = cosine;
                        closest = axis;
                    }
                }
            }
            return closest;
        }

        /**
         * 
         * @param {string} name
         * @return {com.vzome.core.math.symmetry.Direction}
         */
        public getDirection(name: string): com.vzome.core.math.symmetry.Direction {
            return this.delegate.getDirection(name);
        }
    }
    PlaneOrbitSet["__class"] = "com.vzome.core.math.symmetry.PlaneOrbitSet";

}

