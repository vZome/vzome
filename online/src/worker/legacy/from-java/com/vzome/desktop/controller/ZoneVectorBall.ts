import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { RealVector } from "../../core/math/RealVector.js";
import { Axis } from "../../core/math/symmetry/Axis.js";
import { OrbitSet } from "../../core/math/symmetry/OrbitSet.js";

/**
 * Transducer: turns trackball roll events into zone (Axis) change events.
 * 
 * @author Scott Vorthmann
 * @class
 */
export abstract class ZoneVectorBall {
    /*private*/ orbits: OrbitSet;

    /*private*/ zoneVector3d: RealVector;

    /*private*/ zone: Axis;

    static logger: java.util.logging.Logger; public static logger_$LI$(): java.util.logging.Logger { if (ZoneVectorBall.logger == null) { ZoneVectorBall.logger = java.util.logging.Logger.getLogger("com.vzome.desktop.controller.ZoneVectorBall"); }  return ZoneVectorBall.logger; }

    public initializeZone(orbits: OrbitSet, worldEye: RealVector): Axis {
        this.orbits = orbits;
        this.zoneVector3d = new RealVector(worldEye.x, worldEye.y, worldEye.z);
        this.mapVectorToAxis(false);
        return this.zone;
    }

    public trackballRolled(rotation: RealVector[]) {
        const x: number = rotation[0].dot(this.zoneVector3d);
        const y: number = rotation[1].dot(this.zoneVector3d);
        const z: number = rotation[2].dot(this.zoneVector3d);
        this.zoneVector3d = new RealVector(x, y, z);
        this.mapVectorToAxis(true);
    }

    /**
     * This is used when we're doing some non-trackball drag
     * to define a new vector, as for the working plane.
     * @param {RealVector} vector
     */
    public setVector(vector: RealVector) {
        this.zoneVector3d = vector;
        this.mapVectorToAxis(true);
    }

    /*private*/ mapVectorToAxis(notify: boolean) {
        const vector: RealVector = new RealVector(this.zoneVector3d.x, this.zoneVector3d.y, this.zoneVector3d.z);
        const oldAxis: Axis = this.zone;
        this.zone = this.orbits.getAxis(vector);
        if (this.zone == null && oldAxis == null){
            if (ZoneVectorBall.logger_$LI$().isLoggable(java.util.logging.Level.FINER))ZoneVectorBall.logger_$LI$().finer("mapVectorToAxis null zone for " + vector);
            return;
        }
        if (this.zone != null && this.zone.equals(oldAxis)){
            if (ZoneVectorBall.logger_$LI$().isLoggable(java.util.logging.Level.FINER))ZoneVectorBall.logger_$LI$().finer("mapVectorToAxis  zone " + this.zone + " unchanged for " + vector);
            return;
        }
        if (ZoneVectorBall.logger_$LI$().isLoggable(java.util.logging.Level.FINER))ZoneVectorBall.logger_$LI$().finer("preview finished at  " + this.zone + " for " + vector);
        if (notify)this.zoneChanged(oldAxis, this.zone);
    }

    abstract zoneChanged(oldZone: Axis, newZone: Axis);

    constructor() {
        if (this.orbits === undefined) { this.orbits = null; }
        if (this.zoneVector3d === undefined) { this.zoneVector3d = null; }
        this.zone = null;
    }
}
ZoneVectorBall["__class"] = "com.vzome.desktop.controller.ZoneVectorBall";
