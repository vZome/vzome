import { Construction } from "../../construction/Construction.js";
import { Point } from "../../construction/Point.js";
import { Segment } from "../../construction/Segment.js";
import { EditorModel } from "./EditorModel.js";

export interface ImplicitSymmetryParameters extends EditorModel {
    getCenterPoint(): Point;

    setCenterPoint(point: Construction);

    getSymmetrySegment(): Segment;

    setSymmetrySegment(segment: Segment);

    getSelectedConstruction(kind: any): Construction;
}
