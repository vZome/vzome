/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.desktop.controller {
    /**
     * Transducer: turns trackball roll events into zone (Axis) change events.
     * 
     * @author Scott Vorthmann
     * @class
     */
    export abstract class ZoneVectorBall {
        /*private*/ orbits: com.vzome.core.math.symmetry.OrbitSet;

        /*private*/ zoneVector3d: com.vzome.core.math.RealVector;

        /*private*/ zone: com.vzome.core.math.symmetry.Axis;

        static logger: java.util.logging.Logger; public static logger_$LI$(): java.util.logging.Logger { if (ZoneVectorBall.logger == null) { ZoneVectorBall.logger = java.util.logging.Logger.getLogger("com.vzome.desktop.controller.ZoneVectorBall"); }  return ZoneVectorBall.logger; }

        public initializeZone(orbits: com.vzome.core.math.symmetry.OrbitSet, worldEye: com.vzome.core.math.RealVector): com.vzome.core.math.symmetry.Axis {
            this.orbits = orbits;
            this.zoneVector3d = new com.vzome.core.math.RealVector(worldEye.x, worldEye.y, worldEye.z);
            this.mapVectorToAxis(false);
            return this.zone;
        }

        public trackballRolled(rotation: com.vzome.core.math.RealVector[]) {
            const x: number = rotation[0].dot(this.zoneVector3d);
            const y: number = rotation[1].dot(this.zoneVector3d);
            const z: number = rotation[2].dot(this.zoneVector3d);
            this.zoneVector3d = new com.vzome.core.math.RealVector(x, y, z);
            this.mapVectorToAxis(true);
        }

        /**
         * This is used when we're doing some non-trackball drag
         * to define a new vector, as for the working plane.
         * @param {com.vzome.core.math.RealVector} vector
         */
        public setVector(vector: com.vzome.core.math.RealVector) {
            this.zoneVector3d = vector;
            this.mapVectorToAxis(true);
        }

        /*private*/ mapVectorToAxis(notify: boolean) {
            const vector: com.vzome.core.math.RealVector = new com.vzome.core.math.RealVector(this.zoneVector3d.x, this.zoneVector3d.y, this.zoneVector3d.z);
            const oldAxis: com.vzome.core.math.symmetry.Axis = this.zone;
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

        abstract zoneChanged(oldZone: com.vzome.core.math.symmetry.Axis, newZone: com.vzome.core.math.symmetry.Axis);

        constructor() {
            if (this.orbits === undefined) { this.orbits = null; }
            if (this.zoneVector3d === undefined) { this.zoneVector3d = null; }
            this.zone = null;
        }
    }
    ZoneVectorBall["__class"] = "com.vzome.desktop.controller.ZoneVectorBall";

}

