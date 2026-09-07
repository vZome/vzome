import { java, javaemul } from "../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Construction } from "../core/construction/Construction.js";
import { FreePoint } from "../core/construction/FreePoint.js";
import { Point } from "../core/construction/Point.js";
import { Segment } from "../core/construction/Segment.js";
import { SelectionImpl } from "../core/editor/SelectionImpl.js";
import { SelectionSummary } from "../core/editor/SelectionSummary.js";
import { EditorModel } from "../core/editor/api/EditorModel.js";
import { LegacyEditorModel } from "../core/editor/api/LegacyEditorModel.js";
import { OrbitSource } from "../core/editor/api/OrbitSource.js";
import { Selection } from "../core/editor/api/Selection.js";
import { SymmetryAware } from "../core/editor/api/SymmetryAware.js";
import { Symmetries4D } from "../core/math/symmetry/Symmetries4D.js";
import { Manifestation } from "../core/model/Manifestation.js";
import { RealizedModel } from "../core/model/RealizedModel.js";

export class JsEditorModel implements EditorModel, LegacyEditorModel, SymmetryAware {
    /*private*/ realizedModel: RealizedModel;

    /*private*/ selection: Selection;

    /*private*/ kind: Symmetries4D;

    /*private*/ symmetrySegment: Segment;

    /*private*/ symmetryCenter: Point;

    /*private*/ symmetries: OrbitSource;

    /*private*/ symmetrySystems: Object;

    /*private*/ selectionSummary: SelectionSummary;

    public constructor(realizedModel: RealizedModel, selection: Selection, kind: Symmetries4D, symmetries: OrbitSource, symmetrySystems: Object) {
        if (this.realizedModel === undefined) { this.realizedModel = null; }
        if (this.selection === undefined) { this.selection = null; }
        if (this.kind === undefined) { this.kind = null; }
        if (this.symmetrySegment === undefined) { this.symmetrySegment = null; }
        if (this.symmetryCenter === undefined) { this.symmetryCenter = null; }
        if (this.symmetries === undefined) { this.symmetries = null; }
        if (this.symmetrySystems === undefined) { this.symmetrySystems = null; }
        if (this.selectionSummary === undefined) { this.selectionSummary = null; }
        this.realizedModel = realizedModel;
        this.selection = selection;
        this.kind = kind;
        this.symmetries = symmetries;
        this.symmetrySystems = symmetrySystems;
        this.symmetryCenter = new FreePoint(realizedModel.getField().origin(3));
        this.selectionSummary = new SelectionSummary(this.selection);
        (<SelectionImpl><any>this.selection).addListener(this.selectionSummary);
    }

    public setAdapter(adapter: Object) {
    }

    /**
     * 
     * @return {*}
     */
    public getRealizedModel(): RealizedModel {
        return this.realizedModel;
    }

    /**
     * 
     * @return {*}
     */
    public getSelection(): Selection {
        return this.selection;
    }

    /**
     * 
     * @return {*}
     */
    public get4dSymmetries(): Symmetries4D {
        return this.kind;
    }

    /**
     * 
     * @return {Segment}
     */
    public getSymmetrySegment(): Segment {
        return this.symmetrySegment;
    }

    /**
     * 
     * @return {Point}
     */
    public getCenterPoint(): Point {
        return this.symmetryCenter;
    }

    /**
     * 
     * @param {Construction} cons
     * @return {boolean}
     */
    public hasFailedConstruction(cons: Construction): boolean {
        return false;
    }

    public getSymmetrySystem$(): OrbitSource {
        return this.symmetries;
    }

    public getSymmetrySystem$java_lang_String(name: string): OrbitSource {
        return <any>(this.symmetrySystems[name]);
    }

    /**
     * 
     * @param {string} name
     * @return {*}
     */
    public getSymmetrySystem(name?: any): OrbitSource {
        if (((typeof name === 'string') || name === null)) {
            return <any>this.getSymmetrySystem$java_lang_String(name);
        } else if (name === undefined) {
            return <any>this.getSymmetrySystem$();
        } else throw new Error('invalid overload');
    }

    /**
     * 
     * @param {Construction} cons
     */
    public addFailedConstruction(cons: Construction) {
    }

    /**
     * 
     * @param {Construction} point
     */
    public setCenterPoint(point: Construction) {
        this.symmetryCenter = <Point>point;
    }

    /**
     * 
     * @param {Segment} segment
     */
    public setSymmetrySegment(segment: Segment) {
        this.symmetrySegment = segment;
    }

    /**
     * 
     * @param {*} listener
     */
    public addSelectionSummaryListener(listener: SelectionSummary.Listener) {
        this.selectionSummary.addListener(listener);
    }

    public notifyListeners() {
        this.selectionSummary.notifyListeners();
    }

    /**
     * 
     * @param {java.lang.Class} kind
     * @return {Construction}
     */
    public getSelectedConstruction(kind: any): Construction {
        let manifestationClass: any;
        if (kind === Point)manifestationClass = "com.vzome.core.model.Connector"; else if (kind === Segment)manifestationClass = "com.vzome.core.model.Strut"; else return null;
        const focus: Manifestation = this.selection.getSingleSelection(manifestationClass);
        if (focus != null)return focus.getFirstConstruction();
        return null;
    }
}
JsEditorModel["__class"] = "com.vzome.jsweet.JsEditorModel";
JsEditorModel["__interfaces"] = ["com.vzome.core.editor.api.EditorModel","com.vzome.core.editor.api.LegacyEditorModel","com.vzome.core.editor.api.ImplicitSymmetryParameters","com.vzome.core.editor.api.SymmetryAware"];
