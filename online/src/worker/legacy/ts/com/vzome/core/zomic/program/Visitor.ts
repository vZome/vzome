/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.zomic.program {
    export interface Visitor {
        visitWalk(walk: com.vzome.core.zomic.program.Walk);

        visitLabel(id: string);

        visitNested(compound: com.vzome.core.zomic.program.Nested);

        visitRepeat(repeated: com.vzome.core.zomic.program.Repeat, repetitions: number);

        visitRotate(axis: com.vzome.core.math.symmetry.Axis, steps: number);

        visitReflect(blueAxis: com.vzome.core.math.symmetry.Axis);

        visitMove(axis: com.vzome.core.math.symmetry.Axis, length: com.vzome.core.algebra.AlgebraicNumber);

        visitSymmetry(model: com.vzome.core.zomic.program.Symmetry, permute: com.vzome.core.zomic.program.Permute);

        visitScale(size: com.vzome.core.algebra.AlgebraicNumber);

        visitSave(body: com.vzome.core.zomic.program.Save, state: number);

        visitBuild(build: boolean, destroy: boolean);

        /**
         * @param untranslatable
         * @param {string} message
         */
        visitUntranslatable(message: string);
    }

    export namespace Visitor {

        export class Default implements com.vzome.core.zomic.program.Visitor {
            /**
             * 
             * @param {com.vzome.core.zomic.program.Walk} walk
             */
            public visitWalk(walk: com.vzome.core.zomic.program.Walk) {
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
             * @param {com.vzome.core.zomic.program.Nested} compound
             */
            public visitNested(compound: com.vzome.core.zomic.program.Nested) {
                compound.getBody().accept(this);
            }

            /**
             * 
             * @param {com.vzome.core.zomic.program.Repeat} repeated
             * @param {number} repetitions
             */
            public visitRepeat(repeated: com.vzome.core.zomic.program.Repeat, repetitions: number) {
                for(let i: number = 0; i < repetitions; i++) {{
                    this.visitNested(repeated);
                };}
            }

            /**
             * 
             * @param {com.vzome.core.math.symmetry.Axis} axis
             * @param {number} steps
             */
            public visitRotate(axis: com.vzome.core.math.symmetry.Axis, steps: number) {
            }

            /**
             * 
             * @param {com.vzome.core.math.symmetry.Axis} blueAxis
             */
            public visitReflect(blueAxis: com.vzome.core.math.symmetry.Axis) {
            }

            /**
             * 
             * @param {com.vzome.core.math.symmetry.Axis} axis
             * @param {*} length
             */
            public visitMove(axis: com.vzome.core.math.symmetry.Axis, length: com.vzome.core.algebra.AlgebraicNumber) {
            }

            /**
             * 
             * @param {com.vzome.core.zomic.program.Symmetry} model
             * @param {com.vzome.core.zomic.program.Permute} permute
             */
            public visitSymmetry(model: com.vzome.core.zomic.program.Symmetry, permute: com.vzome.core.zomic.program.Permute) {
                this.visitNested(model);
            }

            /**
             * 
             * @param {com.vzome.core.zomic.program.Save} stmt
             * @param {number} state
             */
            public visitSave(stmt: com.vzome.core.zomic.program.Save, state: number) {
                this.visitNested(stmt);
            }

            /**
             * 
             * @param {*} size
             */
            public visitScale(size: com.vzome.core.algebra.AlgebraicNumber) {
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

}

