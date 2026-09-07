import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { Axis } from "../math/symmetry/Axis.js";
import { Direction } from "../math/symmetry/Direction.js";
import { Permutation } from "../math/symmetry/Permutation.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";
import { ZomicEventHandler } from "./ZomicEventHandler.js";

/**
 * @author vorth
 * @param {*} symm
 * @class
 */
export abstract class AbstractZomicEventHandler implements ZomicEventHandler {
    mSymmetry: Symmetry;

    mOrientation: Permutation;

    mHandedNess: number;

    mScale: AlgebraicNumber;

    mAction: number;

    public constructor(symm: Symmetry) {
        if (this.mSymmetry === undefined) { this.mSymmetry = null; }
        if (this.mOrientation === undefined) { this.mOrientation = null; }
        this.mHandedNess = Symmetry.PLUS;
        if (this.mScale === undefined) { this.mScale = null; }
        this.mAction = ZomicEventHandler.BUILD;
        this.mSymmetry = symm;
        this.mScale = symm.getField().one();
        this.mOrientation = this.mSymmetry.getPermutation(0);
    }

    getPermutation(): Permutation {
        return this.mOrientation;
    }

    public getDirection(name: string): Direction {
        return this.mSymmetry.getDirection(name);
    }

    /**
     * 
     * @param {Permutation} permutation
     * @param {number} sense
     */
    public permute(permutation: Permutation, sense: number) {
        this.mOrientation = permutation.compose(this.mOrientation);
        this.mHandedNess = (this.mHandedNess + sense) % 2;
    }

    /**
     * 
     * @param {Axis} axis
     * @param {number} steps
     */
    public rotate(axis: Axis, steps: number) {
        axis = this.mOrientation.permute(axis, this.mHandedNess);
        if (axis.getSense() === this.mHandedNess)steps *= -1;
        this.permute(axis.getRotationPermutation().power(steps), Symmetry.PLUS);
    }

    /**
     * 
     * @param {Axis} blueAxis
     */
    public reflect(blueAxis: Axis) {
        if (blueAxis == null)this.permute(this.mSymmetry.getPermutation(0), Symmetry.MINUS); else {
            blueAxis = this.mOrientation.permute(blueAxis, this.mHandedNess);
            this.permute(blueAxis.getRotationPermutation(), Symmetry.MINUS);
        }
    }

    /**
     * 
     * @param {*} scale
     */
    public scale(scale: AlgebraicNumber) {
        this.mScale = this.mScale['times$com_vzome_core_algebra_AlgebraicNumber'](scale);
    }

    /**
     * 
     * @param {number} action
     */
    public action(action: number) {
        this.mAction = action;
    }

    abstract copyLocation(): AbstractZomicEventHandler;

    abstract restoreLocation(changed: AbstractZomicEventHandler);

    /**
     * 
     * @param {number} variables
     * @return {*}
     */
    public save(variables: number): ZomicEventHandler {
        const newVM: AbstractZomicEventHandler = this.copyLocation();
        newVM.mAction = this.mAction;
        newVM.mOrientation = this.mOrientation;
        newVM.mHandedNess = this.mHandedNess;
        newVM.mScale = this.mScale;
        return newVM;
    }

    /**
     * 
     * @param {*} changes
     * @param {number} variables
     */
    public restore(changes: ZomicEventHandler, variables: number) {
        const changedVM: AbstractZomicEventHandler = <AbstractZomicEventHandler><any>changes;
        if ((ZomicEventHandler.LOCATION & variables) === 0)this.restoreLocation(changedVM);
        if ((ZomicEventHandler.SCALE & variables) === 0)this.mScale = changedVM.mScale;
        if ((ZomicEventHandler.ORIENTATION & variables) === 0){
            this.mOrientation = changedVM.mOrientation;
            this.mHandedNess = changedVM.mHandedNess;
        }
        if ((ZomicEventHandler.ACTION & variables) === 0)this.mAction = changedVM.mAction;
    }

    public abstract step(axis?: any, length?: any): any;    }
AbstractZomicEventHandler["__class"] = "com.vzome.core.render.AbstractZomicEventHandler";
AbstractZomicEventHandler["__interfaces"] = ["com.vzome.core.render.ZomicEventHandler"];
