/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.zomic {
    /**
     * @author Scott Vorthmann
     * @class
     */
    export class Recorder implements com.vzome.core.render.ZomicEventHandler {
        mOutput: Recorder.Output;

        mSaves: java.util.Stack<com.vzome.core.zomic.program.Walk>;

        public setOutput(output: Recorder.Output) {
            this.mOutput = output;
        }

        public record(stmt: com.vzome.core.zomic.program.ZomicStatement) {
            if (!this.mSaves.isEmpty())this.mSaves.peek().addStatement(stmt); else if (this.mOutput != null)this.mOutput.statement(stmt);
        }

        /**
         * 
         * @param {com.vzome.core.math.symmetry.Axis} axis
         * @param {*} length
         */
        public step(axis: com.vzome.core.math.symmetry.Axis, length: com.vzome.core.algebra.AlgebraicNumber) {
            this.record(new com.vzome.core.zomic.program.Move(axis, length));
        }

        /**
         * 
         * @param {com.vzome.core.math.symmetry.Axis} axis
         * @param {number} steps
         */
        public rotate(axis: com.vzome.core.math.symmetry.Axis, steps: number) {
            this.record(new com.vzome.core.zomic.program.Rotate(axis, steps));
        }

        /**
         * 
         * @param {com.vzome.core.math.symmetry.Axis} blueAxis
         */
        public reflect(blueAxis: com.vzome.core.math.symmetry.Axis) {
            const r: com.vzome.core.zomic.program.Reflect = new com.vzome.core.zomic.program.Reflect();
            r.setAxis(blueAxis);
            this.record(r);
        }

        /**
         * 
         * @param {com.vzome.core.math.symmetry.Permutation} permutation
         * @param {number} sense
         */
        public permute(permutation: com.vzome.core.math.symmetry.Permutation, sense: number) {
            this.record(new com.vzome.core.zomic.program.Untranslatable("permutation"));
        }

        /**
         * 
         * @param {*} scale
         */
        public scale(scale: com.vzome.core.algebra.AlgebraicNumber) {
            this.record(new com.vzome.core.zomic.program.Scale(scale));
        }

        /**
         * 
         * @param {number} action
         */
        public action(action: number) {
            this.record(new com.vzome.core.zomic.program.Build((action & com.vzome.core.render.ZomicEventHandler.BUILD) !== 0, (action & com.vzome.core.render.ZomicEventHandler.DESTROY) !== 0));
        }

        /**
         * 
         * @param {number} variables
         * @return {*}
         */
        public save(variables: number): com.vzome.core.render.ZomicEventHandler {
            this.mSaves.push(new com.vzome.core.zomic.program.Walk());
            return this;
        }

        public getLocation(): number[] {
            throw new java.lang.UnsupportedOperationException();
        }

        public getPermutation(): com.vzome.core.math.symmetry.Permutation {
            throw new java.lang.UnsupportedOperationException();
        }

        /**
         * 
         * @param {*} changes
         * @param {number} variables
         */
        public restore(changes: com.vzome.core.render.ZomicEventHandler, variables: number) {
            const walk: com.vzome.core.zomic.program.Walk = this.mSaves.pop();
            const save: com.vzome.core.zomic.program.Save = new com.vzome.core.zomic.program.Save(variables);
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
            statement(stmt: com.vzome.core.zomic.program.ZomicStatement);
        }
    }

}

