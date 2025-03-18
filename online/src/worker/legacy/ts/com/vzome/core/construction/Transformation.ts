/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.construction {
    /**
     * @author Scott Vorthmann
     * @extends com.vzome.core.construction.Construction
     * @class
     */
    export abstract class Transformation extends com.vzome.core.construction.Construction {
        /**
         * 
         * @return {boolean}
         */
        public is3d(): boolean {
            return true;
        }

        /*private*/ mTransform: com.vzome.core.algebra.AlgebraicMatrix;

        mOffset: com.vzome.core.algebra.AlgebraicVector;

        constructor(field: com.vzome.core.algebra.AlgebraicField) {
            super(field);
            if (this.mTransform === undefined) { this.mTransform = null; }
            if (this.mOffset === undefined) { this.mOffset = null; }
        }

        /**
         * 
         * @param {*} that
         * @return {boolean}
         */
        public equals(that: any): boolean {
            if (this === that){
                return true;
            }
            if (that == null){
                return false;
            }
            if (!(that != null && that instanceof <any>com.vzome.core.construction.Transformation)){
                return false;
            }
            const other: Transformation = <Transformation>that;
            if (this.mOffset == null){
                if (other.mOffset != null){
                    return false;
                }
            } else if (!this.mOffset.equals(other.mOffset)){
                return false;
            }
            if (this.mTransform == null){
                if (other.mTransform != null){
                    return false;
                }
            } else if (!this.mTransform.equals(other.mTransform)){
                return false;
            }
            return true;
        }

        setStateVariables(transform: com.vzome.core.algebra.AlgebraicMatrix, offset: com.vzome.core.algebra.AlgebraicVector, impossible: boolean): boolean {
            if (impossible){
                if (this.isImpossible())return false;
                this.setImpossible(true);
                return true;
            }
            if (transform != null && transform.equals(this.mTransform) && offset.equals(this.mOffset) && !this.isImpossible())return false;
            this.mTransform = transform;
            this.mOffset = offset;
            this.setImpossible(false);
            return true;
        }

        public transform$com_vzome_core_algebra_AlgebraicVector(arg: com.vzome.core.algebra.AlgebraicVector): com.vzome.core.algebra.AlgebraicVector {
            arg = arg.minus(this.mOffset);
            arg = this.mTransform.timesColumn(arg);
            arg = arg.plus(this.mOffset);
            return arg;
        }

        public transform(arg?: any): any {
            if (((arg != null && arg instanceof <any>com.vzome.core.algebra.AlgebraicVector) || arg === null)) {
                return <any>this.transform$com_vzome_core_algebra_AlgebraicVector(arg);
            } else if (((arg != null && arg instanceof <any>com.vzome.core.construction.Construction) || arg === null)) {
                return <any>this.transform$com_vzome_core_construction_Construction(arg);
            } else throw new Error('invalid overload');
        }

        public transform$com_vzome_core_construction_Construction(c: com.vzome.core.construction.Construction): com.vzome.core.construction.Construction {
            if (c != null && c instanceof <any>com.vzome.core.construction.Point){
                return new com.vzome.core.construction.TransformedPoint(this, <com.vzome.core.construction.Point>c);
            } else if (c != null && c instanceof <any>com.vzome.core.construction.Segment){
                return new com.vzome.core.construction.TransformedSegment(this, <com.vzome.core.construction.Segment>c);
            } else if (c != null && c instanceof <any>com.vzome.core.construction.Polygon){
                return new com.vzome.core.construction.TransformedPolygon(this, <com.vzome.core.construction.Polygon>c);
            } else {
                return null;
            }
        }

        /**
         * 
         * @param {*} doc
         * @return {*}
         */
        public getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element {
            const result: org.w3c.dom.Element = doc.createElement("transformation");
            return result;
        }
    }
    Transformation["__class"] = "com.vzome.core.construction.Transformation";


    export namespace Transformation {

        export class Identity extends com.vzome.core.construction.Transformation {
            public transform$int_A(arg: number[]): number[] {
                return arg;
            }

            public transform(arg?: any): any {
                if (((arg != null && arg instanceof <any>Array && (arg.length == 0 || arg[0] == null ||(typeof arg[0] === 'number'))) || arg === null)) {
                    return <any>this.transform$int_A(arg);
                } else if (((arg != null && arg instanceof <any>com.vzome.core.algebra.AlgebraicVector) || arg === null)) {
                    return super.transform(arg);
                } else if (((arg != null && arg instanceof <any>com.vzome.core.construction.Construction) || arg === null)) {
                    return <any>this.transform$com_vzome_core_construction_Construction(arg);
                } else throw new Error('invalid overload');
            }

            public constructor(field: com.vzome.core.algebra.AlgebraicField) {
                super(field);
            }

            public attach() {
            }

            public detach() {
            }

            /**
             * 
             * @return {boolean}
             */
            mapParamsToState(): boolean {
                return true;
            }
        }
        Identity["__class"] = "com.vzome.core.construction.Transformation.Identity";

    }

}

