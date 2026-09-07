import { java, javaemul } from "../../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../../algebra/AlgebraicVector.js";
import { RealVector } from "../RealVector.js";
import { Axis } from "./Axis.js";
import { Direction } from "./Direction.js";
import { OrbitSet } from "./OrbitSet.js";

export class PlaneOrbitSet extends OrbitSet {
    /*private*/ delegate: OrbitSet;

    /*private*/ normal: AlgebraicVector;

    /*private*/ __zones: java.util.Set<Axis>;

    public constructor(delegate: OrbitSet, normal: AlgebraicVector) {
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

    public zones(): java.util.Iterator<Axis> {
        return this.__zones.iterator();
    }

    /**
     * 
     * @param {RealVector} vector
     * @return {Axis}
     */
    public getAxis(vector: RealVector): Axis {
        if (RealVector.ORIGIN_$LI$().equals(vector)){
            return null;
        }
        let maxCosine: number = -1.0;
        let closest: Axis = null;
        for(let index=this.__zones.iterator();index.hasNext();) {
            let axis = index.next();
            {
                const axisV: RealVector = axis.normal().toRealVector();
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
     * @return {Direction}
     */
    public getDirection(name: string): Direction {
        return this.delegate.getDirection(name);
    }
}
PlaneOrbitSet["__class"] = "com.vzome.core.math.symmetry.PlaneOrbitSet";
