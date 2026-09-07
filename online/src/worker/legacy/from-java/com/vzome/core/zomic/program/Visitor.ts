import { AlgebraicNumber } from "../../algebra/AlgebraicNumber.js";
import { Axis } from "../../math/symmetry/Axis.js";
import { Nested } from "./Nested.js";
import { Permute } from "./Permute.js";
import { Repeat } from "./Repeat.js";
import { Save } from "./Save.js";
import { Symmetry } from "./Symmetry.js";
import { Walk } from "./Walk.js";

export interface Visitor {
    visitWalk(walk: Walk);

    visitLabel(id: string);

    visitNested(compound: Nested);

    visitRepeat(repeated: Repeat, repetitions: number);

    visitRotate(axis: Axis, steps: number);

    visitReflect(blueAxis: Axis);

    visitMove(axis: Axis, length: AlgebraicNumber);

    visitSymmetry(model: Symmetry, permute: Permute);

    visitScale(size: AlgebraicNumber);

    visitSave(body: Save, state: number);

    visitBuild(build: boolean, destroy: boolean);

    /**
     * @param untranslatable
     * @param {string} message
     */
    visitUntranslatable(message: string);
}

export namespace Visitor {

    export class Default implements Visitor {
        /**
         * 
         * @param {Walk} walk
         */
        public visitWalk(walk: Walk) {
            for(let index=walk.iterator();index.hasNext();) {
                let stmt = index.next();
                {
                    stmt.accept(this);
                }
            }
        }

        /**
         * 
         * @param {string} id
         */
        public visitLabel(id: string) {
        }

        /**
         * 
         * @param {Nested} compound
         */
        public visitNested(compound: Nested) {
            compound.getBody().accept(this);
        }

        /**
         * 
         * @param {Repeat} repeated
         * @param {number} repetitions
         */
        public visitRepeat(repeated: Repeat, repetitions: number) {
            for(let i: number = 0; i < repetitions; i++) {{
                this.visitNested(repeated);
            };}
        }

        /**
         * 
         * @param {Axis} axis
         * @param {number} steps
         */
        public visitRotate(axis: Axis, steps: number) {
        }

        /**
         * 
         * @param {Axis} blueAxis
         */
        public visitReflect(blueAxis: Axis) {
        }

        /**
         * 
         * @param {Axis} axis
         * @param {*} length
         */
        public visitMove(axis: Axis, length: AlgebraicNumber) {
        }

        /**
         * 
         * @param {Symmetry} model
         * @param {Permute} permute
         */
        public visitSymmetry(model: Symmetry, permute: Permute) {
            this.visitNested(model);
        }

        /**
         * 
         * @param {Save} stmt
         * @param {number} state
         */
        public visitSave(stmt: Save, state: number) {
            this.visitNested(stmt);
        }

        /**
         * 
         * @param {*} size
         */
        public visitScale(size: AlgebraicNumber) {
        }

        /**
         * 
         * @param {boolean} build
         * @param {boolean} destroy
         */
        public visitBuild(build: boolean, destroy: boolean) {
        }

        /**
         * 
         * @param {string} message
         */
        public visitUntranslatable(message: string) {
        }

        constructor() {
        }
    }
    Default["__class"] = "com.vzome.core.zomic.program.Visitor.Default";
    Default["__interfaces"] = ["com.vzome.core.zomic.program.Visitor"];


}
