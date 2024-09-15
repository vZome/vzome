/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.commands {
    export class ZomicVirtualMachine extends com.vzome.core.render.AbstractZomicEventHandler {
        /*private*/ mLocation: com.vzome.core.construction.Point;

        /*private*/ mEffects: com.vzome.core.construction.ConstructionChanges;

        public getLocation(): com.vzome.core.algebra.AlgebraicVector {
            return this.mLocation.getLocation();
        }

        /**
         * @return
         * @return {com.vzome.core.construction.Construction}
         */
        public getLastPoint(): com.vzome.core.construction.Construction {
            return this.mLocation;
        }

        /**
         * 
         * @param {com.vzome.core.math.symmetry.Axis} axis
         * @param {*} length
         */
        public step(axis: com.vzome.core.math.symmetry.Axis, length: com.vzome.core.algebra.AlgebraicNumber) {
            axis = this.mOrientation.permute(axis, this.mHandedNess);
            length = length['times$com_vzome_core_algebra_AlgebraicNumber'](this.mScale);
            const segment: com.vzome.core.construction.Segment = new com.vzome.core.construction.AnchoredSegment(axis, length, this.mLocation);
            const pt2: com.vzome.core.construction.Point = new com.vzome.core.construction.SegmentEndPoint(segment);
            if (this.mAction !== com.vzome.core.render.ZomicEventHandler.JUST_MOVE){
                this.mEffects['constructionAdded$com_vzome_core_construction_Construction'](this.mLocation);
                this.mEffects['constructionAdded$com_vzome_core_construction_Construction'](segment);
                this.mEffects['constructionAdded$com_vzome_core_construction_Construction'](pt2);
            }
            this.mLocation = pt2;
        }

        public constructor(start: com.vzome.core.construction.Point, effects: com.vzome.core.construction.ConstructionChanges, symm: com.vzome.core.math.symmetry.Symmetry) {
            super(symm);
            if (this.mLocation === undefined) { this.mLocation = null; }
            if (this.mEffects === undefined) { this.mEffects = null; }
            this.mLocation = start;
            this.mEffects = effects;
        }

        /**
         * 
         * @return {com.vzome.core.render.AbstractZomicEventHandler}
         */
        copyLocation(): com.vzome.core.render.AbstractZomicEventHandler {
            return new ZomicVirtualMachine(this.mLocation, this.mEffects, this.mSymmetry);
        }

        /**
         * 
         * @param {com.vzome.core.render.AbstractZomicEventHandler} changed
         */
        restoreLocation(changed: com.vzome.core.render.AbstractZomicEventHandler) {
            this.mLocation = (<ZomicVirtualMachine>changed).mLocation;
        }
    }
    ZomicVirtualMachine["__class"] = "com.vzome.core.commands.ZomicVirtualMachine";
    ZomicVirtualMachine["__interfaces"] = ["com.vzome.core.render.ZomicEventHandler"];


}

