/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.desktop.controller {
    export class MeasureController extends com.vzome.desktop.controller.DefaultController implements com.vzome.core.editor.SelectionSummary.Listener {
        /*private*/ selection: com.vzome.core.editor.api.Selection;

        /*private*/ editorModel: com.vzome.core.editor.api.EditorModel;

        /*private*/ measurements: java.util.Map<string, string>;

        /*private*/ renderedModel: com.vzome.core.render.RenderedModel;

        /*private*/ twoPlaces: java.text.NumberFormat;

        /*private*/ fourPlaces: java.text.NumberFormat;

        public constructor(model: com.vzome.core.editor.api.EditorModel, renderedModel: com.vzome.core.render.RenderedModel) {
            super();
            if (this.selection === undefined) { this.selection = null; }
            if (this.editorModel === undefined) { this.editorModel = null; }
            this.measurements = <any>(new java.util.LinkedHashMap<any, any>());
            if (this.renderedModel === undefined) { this.renderedModel = null; }
            this.twoPlaces = java.text.NumberFormat.getInstance();
            this.fourPlaces = java.text.NumberFormat.getInstance();
            this.renderedModel = renderedModel;
            this.selection = model.getSelection();
            this.editorModel = model;
            model.addSelectionSummaryListener(this);
            this.twoPlaces.setMaximumFractionDigits(2);
            this.fourPlaces.setMaximumFractionDigits(4);
        }

        /**
         * 
         * @param {string} listName
         * @return {java.lang.String[]}
         */
        public getCommandList(listName: string): string[] {
            return this.measurements.keySet().toArray<any>((s => { let a=[]; while(s-->0) a.push(null); return a; })(this.measurements.keySet().size()));
        }

        /**
         * 
         * @param {string} key
         * @return {string}
         */
        public getProperty(key: string): string {
            return this.measurements.get(key);
        }

        /**
         * 
         * @param {number} total
         * @param {number} balls
         * @param {number} struts
         * @param {number} panels
         */
        public selectionChanged(total: number, balls: number, struts: number, panels: number) {
            this.measurements.clear();
            if (total !== 0){
                if (balls !== 0){
                    this.measurements.put("balls", /* toString */(''+(balls)));
                }
                if (struts !== 0){
                    this.measurements.put("struts", /* toString */(''+(struts)));
                }
                if (panels !== 0){
                    this.measurements.put("panels", /* toString */(''+(panels)));
                }
                if (total === 1 || total === 2){
                    this.measurements.put("", "");
                }
                if (total === 1){
                    if (panels === 1){
                        const panel: com.vzome.core.model.Panel = com.vzome.core.editor.api.Manifestations.getPanels$java_lang_Iterable(this.selection).next();
                        this.measurements.put("vertices", /* toString */(''+(panel.getVertexCount())));
                    } else if (struts === 1){
                        const strut: com.vzome.core.model.Strut = com.vzome.core.editor.api.Manifestations.getStruts$java_lang_Iterable(this.selection).next();
                        const cm: number = this.renderedModel.measureLengthCm$com_vzome_core_model_Strut(strut);
                        this.measurements.put("length (cm)", this.twoPlaces.format(cm) + " cm");
                        const inches: number = cm / 2.54;
                        this.measurements.put("length (in)", this.twoPlaces.format(inches) + " in");
                    } else if (balls === 1){
                        const conn: com.vzome.core.model.Connector = com.vzome.core.editor.api.Manifestations.getConnectors$java_lang_Iterable(this.selection).next();
                        this.measurements.put("location", conn.getLocation().toString());
                    }
                } else if (total === 2){
                    if (panels === 2){
                        let p1: com.vzome.core.model.Panel = null;
                        let p2: com.vzome.core.model.Panel = null;
                        for(let index=com.vzome.core.editor.api.Manifestations.getPanels$java_lang_Iterable(this.selection).iterator();index.hasNext();) {
                            let panel = index.next();
                            {
                                if (p1 == null)p1 = panel; else p2 = panel;
                            }
                        }
                        const radians: number = this.renderedModel.measureDihedralAngle(p1, p2);
                        this.reportAngles(radians);
                    } else if (struts === 2){
                        let s1: com.vzome.core.model.Strut = null;
                        let s2: com.vzome.core.model.Strut = null;
                        for(let index=com.vzome.core.editor.api.Manifestations.getStruts$java_lang_Iterable(this.selection).iterator();index.hasNext();) {
                            let strut = index.next();
                            {
                                if (s1 == null)s1 = strut; else s2 = strut;
                            }
                        }
                        const points: java.util.Set<com.vzome.core.algebra.AlgebraicVector> = <any>(new java.util.HashSet<any>());
                        points.add(s1.getLocation());
                        points.add(s1.getEnd());
                        points.add(s2.getLocation());
                        points.add(s2.getEnd());
                        if (points.size() > 3){
                            this.measurements.put("coplanar", com.vzome.core.algebra.AlgebraicVectors.areCoplanar(points) ? "yes" : "no");
                        }
                        const radians: number = this.renderedModel.measureAngle(s1, s2);
                        this.reportAngles(radians);
                        this.reportRatio(s1, s2);
                    } else if (balls === 2){
                        let b1: com.vzome.core.model.Connector = null;
                        let b2: com.vzome.core.model.Connector = null;
                        for(let index=com.vzome.core.editor.api.Manifestations.getConnectors$java_lang_Iterable(this.selection).iterator();index.hasNext();) {
                            let conn = index.next();
                            {
                                if (b1 == null)b1 = conn; else b2 = conn;
                            }
                        }
                        const cm: number = this.renderedModel.measureDistanceCm(b1, b2);
                        this.measurements.put("distance (cm)", this.twoPlaces.format(cm) + " cm");
                        const inches: number = cm / 2.54;
                        this.measurements.put("distance (in)", this.twoPlaces.format(inches) + " in");
                    }
                }
            }
            this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object("measures", false, true);
        }

        /*private*/ reportAngles(radians: number) {
            if (/* isFinite */((value) => !isNaN(value) && Number.NEGATIVE_INFINITY !== value && Number.POSITIVE_INFINITY !== value)(radians)){
                const fraction: number = radians / Math.PI;
                const supplement: number = 1.0 - fraction;
                this.measurements.put("radians", this.fourPlaces.format(fraction) + "\u03c0");
                this.measurements.put("radians (supplement)", this.fourPlaces.format(supplement) + "\u03c0");
                this.measurements.put("degrees", this.twoPlaces.format(fraction * 180) + "\u00b0");
                this.measurements.put("degrees (supplement)", this.twoPlaces.format(supplement * 180) + "\u00b0");
            } else {
                this.measurements.put("angle", /* toString */(''+(radians)));
            }
        }

        /*private*/ reportRatio(s1: com.vzome.core.model.Strut, s2: com.vzome.core.model.Strut) {
            const v1: com.vzome.core.algebra.AlgebraicVector = s1.getOffset();
            const v2: com.vzome.core.algebra.AlgebraicVector = s2.getOffset();
            const ss: com.vzome.core.editor.api.OrbitSource = this.editorModel['getSymmetrySystem$']();
            const axis1: com.vzome.core.math.symmetry.Axis = ss.getAxis(v1);
            const axis2: com.vzome.core.math.symmetry.Axis = ss.getAxis(v2);
            const dir1: com.vzome.core.math.symmetry.Direction = axis1.getDirection();
            const dir2: com.vzome.core.math.symmetry.Direction = axis2.getDirection();
            const sameOrbit: boolean = dir1.equals(dir2);
            let name1: string = dir1.getName();
            let name2: string = dir2.getName();
            const auto: string = "auto";
            if (dir1.isAutomatic()){
                name1 = auto + name1;
            }
            if (dir2.isAutomatic()){
                name2 = auto + name2;
            }
            if (sameOrbit){
                name1 += "\u2081";
                name2 += "\u2082";
            }
            const n1n2: string = name1 + " / " + name2;
            const n2n1: string = name2 + " / " + name1;
            this.measurements.put(" ", " ");
            const len1: number = ss.getSymmetry().embedInR3(v1).length();
            const len2: number = ss.getSymmetry().embedInR3(v2).length();
            const length1: number = /* floatValue */len1;
            const length2: number = /* floatValue */len2;
            const ratio: number = (<any>Math).fround(length1 / length2);
            let comparison: string = "equal";
            if (Math.abs((<any>Math).fround(length1 - length2)) > 1.0E-6){
                comparison = name1 + " " + (length1 > length2 ? ">" : "<") + " " + name2;
            }
            this.measurements.put("relative strut lengths", comparison);
            if (!(comparison === ("equal"))){
                const recip: number = (<any>Math).fround(1.0 / ratio);
                this.measurements.put(n1n2 + " (approx)", this.fourPlaces.format(ratio));
                this.measurements.put(n2n1 + " (approx)", this.fourPlaces.format(recip));
                if (sameOrbit){
                    const exactLength1: com.vzome.core.algebra.AlgebraicNumber = axis1.getLength(v1);
                    const exactength2: com.vzome.core.algebra.AlgebraicNumber = axis2.getLength(v2);
                    const exactRatio: com.vzome.core.algebra.AlgebraicNumber = exactLength1.dividedBy(exactength2);
                    const exactRecip: com.vzome.core.algebra.AlgebraicNumber = exactRatio.reciprocal();
                    this.measurements.put(n1n2, exactRatio.toString(com.vzome.core.algebra.AlgebraicField.DEFAULT_FORMAT));
                    this.measurements.put(n2n1, exactRecip.toString(com.vzome.core.algebra.AlgebraicField.DEFAULT_FORMAT));
                }
            }
        }
    }
    MeasureController["__class"] = "com.vzome.desktop.controller.MeasureController";
    MeasureController["__interfaces"] = ["com.vzome.core.editor.SelectionSummary.Listener","com.vzome.desktop.api.Controller"];


}

