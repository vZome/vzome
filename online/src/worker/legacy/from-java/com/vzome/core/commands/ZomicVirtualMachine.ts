import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { AnchoredSegment } from "../construction/AnchoredSegment.js";
import { Construction } from "../construction/Construction.js";
import { ConstructionChanges } from "../construction/ConstructionChanges.js";
import { Point } from "../construction/Point.js";
import { Segment } from "../construction/Segment.js";
import { SegmentEndPoint } from "../construction/SegmentEndPoint.js";
import { Axis } from "../math/symmetry/Axis.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";
import { AbstractZomicEventHandler } from "../render/AbstractZomicEventHandler.js";
import { ZomicEventHandler } from "../render/ZomicEventHandler.js";

export class ZomicVirtualMachine extends AbstractZomicEventHandler {
    /*private*/ mLocation: Point;

    /*private*/ mEffects: ConstructionChanges;

    public getLocation(): AlgebraicVector {
        return this.mLocation.getLocation();
    }

    /**
     * @return
     * @return {Construction}
     */
    public getLastPoint(): Construction {
        return this.mLocation;
    }

    /**
     * 
     * @param {Axis} axis
     * @param {*} length
     */
    public step(axis: Axis, length: AlgebraicNumber) {
        axis = this.mOrientation.permute(axis, this.mHandedNess);
        length = length['times$com_vzome_core_algebra_AlgebraicNumber'](this.mScale);
        const segment: Segment = new AnchoredSegment(axis, length, this.mLocation);
        const pt2: Point = new SegmentEndPoint(segment);
        if (this.mAction !== ZomicEventHandler.JUST_MOVE){
            this.mEffects['constructionAdded$com_vzome_core_construction_Construction'](this.mLocation);
            this.mEffects['constructionAdded$com_vzome_core_construction_Construction'](segment);
            this.mEffects['constructionAdded$com_vzome_core_construction_Construction'](pt2);
        }
        this.mLocation = pt2;
    }

    public constructor(start: Point, effects: ConstructionChanges, symm: Symmetry) {
        super(symm);
        if (this.mLocation === undefined) { this.mLocation = null; }
        if (this.mEffects === undefined) { this.mEffects = null; }
        this.mLocation = start;
        this.mEffects = effects;
    }

    /**
     * 
     * @return {AbstractZomicEventHandler}
     */
    copyLocation(): AbstractZomicEventHandler {
        return new ZomicVirtualMachine(this.mLocation, this.mEffects, this.mSymmetry);
    }

    /**
     * 
     * @param {AbstractZomicEventHandler} changed
     */
    restoreLocation(changed: AbstractZomicEventHandler) {
        this.mLocation = (<ZomicVirtualMachine>changed).mLocation;
    }
}
ZomicVirtualMachine["__class"] = "com.vzome.core.commands.ZomicVirtualMachine";
ZomicVirtualMachine["__interfaces"] = ["com.vzome.core.render.ZomicEventHandler"];
