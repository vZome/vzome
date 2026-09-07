import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { AlgebraicVectors } from "../algebra/AlgebraicVectors.js";
import { Construction } from "./Construction.js";
import { Line } from "./Line.js";
import { LineExtensionOfSegment } from "./LineExtensionOfSegment.js";
import { LineFromPointAndVector } from "./LineFromPointAndVector.js";
import { LineLineIntersectionPoint } from "./LineLineIntersectionPoint.js";
import { Point } from "./Point.js";
import { Segment } from "./Segment.js";
import { Transformation } from "./Transformation.js";

export class LineReflection extends Transformation {
    /*private*/ two: AlgebraicNumber;

    /*private*/ mMirrorLine: Line;

    /*private*/ mStart: AlgebraicVector;

    /*private*/ mEnd: AlgebraicVector;

    public constructor(axis: Segment) {
        super(axis.field);
        if (this.two === undefined) { this.two = null; }
        if (this.mMirrorLine === undefined) { this.mMirrorLine = null; }
        if (this.mStart === undefined) { this.mStart = null; }
        if (this.mEnd === undefined) { this.mEnd = null; }
        this.two = this.field['createRational$long'](2);
        this.mMirrorLine = new LineExtensionOfSegment(axis);
        this.mStart = axis.getStart();
        this.mEnd = axis.getEnd();
    }

    /**
     * 
     * @return {boolean}
     */
    mapParamsToState(): boolean {
        return true;
    }

    public transform$com_vzome_core_algebra_AlgebraicVector(arg: AlgebraicVector): AlgebraicVector {
        const norm1: AlgebraicVector = AlgebraicVectors.getNormal$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(this.mStart, this.mEnd, arg);
        if (norm1.isOrigin()){
            return arg;
        }
        const norm2: AlgebraicVector = AlgebraicVectors.getNormal$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector$com_vzome_core_algebra_AlgebraicVector(this.mStart, this.mEnd, this.mEnd.plus(norm1));
        const line2: Line = new LineFromPointAndVector(arg, norm2);
        const point: Point = new LineLineIntersectionPoint(this.mMirrorLine, line2);
        const intersection: AlgebraicVector = point.getLocation();
        const translation: AlgebraicVector = intersection.minus(arg).scale(this.two);
        return arg.plus(translation);
    }

    /**
     * 
     * @param {AlgebraicVector} arg
     * @return {AlgebraicVector}
     */
    public transform(arg?: any): any {
        if (((arg != null && arg instanceof <any>AlgebraicVector) || arg === null)) {
            return <any>this.transform$com_vzome_core_algebra_AlgebraicVector(arg);
        } else if (((arg != null && arg instanceof <any>Construction) || arg === null)) {
            return <any>this.transform$com_vzome_core_construction_Construction(arg);
        } else throw new Error('invalid overload');
    }
}
LineReflection["__class"] = "com.vzome.core.construction.LineReflection";
