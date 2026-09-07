import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { Axis } from "../math/symmetry/Axis.js";
import { Permutation } from "../math/symmetry/Permutation.js";
import { ZomicEventHandler } from "../render/ZomicEventHandler.js";
import { Build } from "./program/Build.js";
import { Move } from "./program/Move.js";
import { Reflect } from "./program/Reflect.js";
import { Rotate } from "./program/Rotate.js";
import { Save } from "./program/Save.js";
import { Scale } from "./program/Scale.js";
import { Untranslatable } from "./program/Untranslatable.js";
import { Walk } from "./program/Walk.js";
import { ZomicStatement } from "./program/ZomicStatement.js";

/**
 * @author Scott Vorthmann
 * @class
 */
export class Recorder implements ZomicEventHandler {
    mOutput: Recorder.Output;

    mSaves: java.util.Stack<Walk>;

    public setOutput(output: Recorder.Output) {
        this.mOutput = output;
    }

    public record(stmt: ZomicStatement) {
        if (!this.mSaves.isEmpty())this.mSaves.peek().addStatement(stmt); else if (this.mOutput != null)this.mOutput.statement(stmt);
    }

    /**
     * 
     * @param {Axis} axis
     * @param {*} length
     */
    public step(axis: Axis, length: AlgebraicNumber) {
        this.record(new Move(axis, length));
    }

    /**
     * 
     * @param {Axis} axis
     * @param {number} steps
     */
    public rotate(axis: Axis, steps: number) {
        this.record(new Rotate(axis, steps));
    }

    /**
     * 
     * @param {Axis} blueAxis
     */
    public reflect(blueAxis: Axis) {
        const r: Reflect = new Reflect();
        r.setAxis(blueAxis);
        this.record(r);
    }

    /**
     * 
     * @param {Permutation} permutation
     * @param {number} sense
     */
    public permute(permutation: Permutation, sense: number) {
        this.record(new Untranslatable("permutation"));
    }

    /**
     * 
     * @param {*} scale
     */
    public scale(scale: AlgebraicNumber) {
        this.record(new Scale(scale));
    }

    /**
     * 
     * @param {number} action
     */
    public action(action: number) {
        this.record(new Build((action & ZomicEventHandler.BUILD) !== 0, (action & ZomicEventHandler.DESTROY) !== 0));
    }

    /**
     * 
     * @param {number} variables
     * @return {*}
     */
    public save(variables: number): ZomicEventHandler {
        this.mSaves.push(new Walk());
        return this;
    }

    public getLocation(): number[] {
        throw new java.lang.UnsupportedOperationException();
    }

    public getPermutation(): Permutation {
        throw new java.lang.UnsupportedOperationException();
    }

    /**
     * 
     * @param {*} changes
     * @param {number} variables
     */
    public restore(changes: ZomicEventHandler, variables: number) {
        const walk: Walk = this.mSaves.pop();
        const save: Save = new Save(variables);
        save.setBody(walk);
        this.record(save);
    }

    constructor() {
        if (this.mOutput === undefined) { this.mOutput = null; }
        this.mSaves = <any>(new java.util.Stack<any>());
    }
}
Recorder["__class"] = "com.vzome.core.zomic.Recorder";
Recorder["__interfaces"] = ["com.vzome.core.render.ZomicEventHandler"];



export namespace Recorder {

    export interface Output {
        statement(stmt: ZomicStatement);
    }
}
