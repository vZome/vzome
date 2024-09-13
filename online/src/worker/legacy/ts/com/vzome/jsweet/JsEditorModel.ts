/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.jsweet {
    export class JsEditorModel implements com.vzome.core.editor.api.EditorModel, com.vzome.core.editor.api.LegacyEditorModel, com.vzome.core.editor.api.SymmetryAware {
        /*private*/ realizedModel: com.vzome.core.model.RealizedModel;

        /*private*/ selection: com.vzome.core.editor.api.Selection;

        /*private*/ kind: com.vzome.core.math.symmetry.Symmetries4D;

        /*private*/ symmetrySegment: com.vzome.core.construction.Segment;

        /*private*/ symmetryCenter: com.vzome.core.construction.Point;

        /*private*/ symmetries: com.vzome.core.editor.api.OrbitSource;

        /*private*/ symmetrySystems: Object;

        /*private*/ selectionSummary: com.vzome.core.editor.SelectionSummary;

        public constructor(realizedModel: com.vzome.core.model.RealizedModel, selection: com.vzome.core.editor.api.Selection, kind: com.vzome.core.math.symmetry.Symmetries4D, symmetries: com.vzome.core.editor.api.OrbitSource, symmetrySystems: Object) {
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
            this.symmetryCenter = new com.vzome.core.construction.FreePoint(realizedModel.getField().origin(3));
            this.selectionSummary = new com.vzome.core.editor.SelectionSummary(this.selection);
            (<com.vzome.core.editor.SelectionImpl><any>this.selection).addListener(this.selectionSummary);
        }

        public setAdapter(adapter: Object) {
        }

        /**
         * 
         * @return {*}
         */
        public getRealizedModel(): com.vzome.core.model.RealizedModel {
            return this.realizedModel;
        }

        /**
         * 
         * @return {*}
         */
        public getSelection(): com.vzome.core.editor.api.Selection {
            return this.selection;
        }

        /**
         * 
         * @return {*}
         */
        public get4dSymmetries(): com.vzome.core.math.symmetry.Symmetries4D {
            return this.kind;
        }

        /**
         * 
         * @return {com.vzome.core.construction.Segment}
         */
        public getSymmetrySegment(): com.vzome.core.construction.Segment {
            return this.symmetrySegment;
        }

        /**
         * 
         * @return {com.vzome.core.construction.Point}
         */
        public getCenterPoint(): com.vzome.core.construction.Point {
            return this.symmetryCenter;
        }

        /**
         * 
         * @param {com.vzome.core.construction.Construction} cons
         * @return {boolean}
         */
        public hasFailedConstruction(cons: com.vzome.core.construction.Construction): boolean {
            return false;
        }

        public getSymmetrySystem$(): com.vzome.core.editor.api.OrbitSource {
            return this.symmetries;
        }

        public getSymmetrySystem$java_lang_String(name: string): com.vzome.core.editor.api.OrbitSource {
            return <any>(this.symmetrySystems[name]);
        }

        /**
         * 
         * @param {string} name
         * @return {*}
         */
        public getSymmetrySystem(name?: any): com.vzome.core.editor.api.OrbitSource {
            if (((typeof name === 'string') || name === null)) {
                return <any>this.getSymmetrySystem$java_lang_String(name);
            } else if (name === undefined) {
                return <any>this.getSymmetrySystem$();
            } else throw new Error('invalid overload');
        }

        /**
         * 
         * @param {com.vzome.core.construction.Construction} cons
         */
        public addFailedConstruction(cons: com.vzome.core.construction.Construction) {
        }

        /**
         * 
         * @param {com.vzome.core.construction.Construction} point
         */
        public setCenterPoint(point: com.vzome.core.construction.Construction) {
            this.symmetryCenter = <com.vzome.core.construction.Point>point;
        }

        /**
         * 
         * @param {com.vzome.core.construction.Segment} segment
         */
        public setSymmetrySegment(segment: com.vzome.core.construction.Segment) {
            this.symmetrySegment = segment;
        }

        /**
         * 
         * @param {*} listener
         */
        public addSelectionSummaryListener(listener: com.vzome.core.editor.SelectionSummary.Listener) {
            this.selectionSummary.addListener(listener);
        }

        public notifyListeners() {
            this.selectionSummary.notifyListeners();
        }

        /**
         * 
         * @param {java.lang.Class} kind
         * @return {com.vzome.core.construction.Construction}
         */
        public getSelectedConstruction(kind: any): com.vzome.core.construction.Construction {
            let manifestationClass: any;
            if (kind === com.vzome.core.construction.Point)manifestationClass = "com.vzome.core.model.Connector"; else if (kind === com.vzome.core.construction.Segment)manifestationClass = "com.vzome.core.model.Strut"; else return null;
            const focus: com.vzome.core.model.Manifestation = this.selection.getSingleSelection(manifestationClass);
            if (focus != null)return focus.getFirstConstruction();
            return null;
        }
    }
    JsEditorModel["__class"] = "com.vzome.jsweet.JsEditorModel";
    JsEditorModel["__interfaces"] = ["com.vzome.core.editor.api.EditorModel","com.vzome.core.editor.api.LegacyEditorModel","com.vzome.core.editor.api.ImplicitSymmetryParameters","com.vzome.core.editor.api.SymmetryAware"];


}

