/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    /**
     * This is only used when opening legacy files.  The UI and controllers now use the generic Polytope4d.
     * @author Scott Vorthmann
     * @param {*} editorModel
     * @param {com.vzome.core.construction.Segment} symmAxis
     * @param {number} index
     * @class
     * @extends com.vzome.core.editor.api.ChangeManifestations
     */
    export class B4Polytope extends com.vzome.core.editor.api.ChangeManifestations {
        /*private*/ index: number;

        /*private*/ proj: com.vzome.core.math.Projection;

        /*private*/ symmAxis: com.vzome.core.construction.Segment;

        /*private*/ field: com.vzome.core.algebra.AlgebraicField;

        public constructor(editorModel?: any, symmAxis?: any, index?: any) {
            if (((editorModel != null && (editorModel.constructor != null && editorModel.constructor["__interfaces"] != null && editorModel.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.EditorModel") >= 0)) || editorModel === null) && ((symmAxis != null && symmAxis instanceof <any>com.vzome.core.construction.Segment) || symmAxis === null) && ((typeof index === 'number') || index === null)) {
                let __args = arguments;
                super(editorModel);
                if (this.index === undefined) { this.index = 0; } 
                if (this.proj === undefined) { this.proj = null; } 
                if (this.symmAxis === undefined) { this.symmAxis = null; } 
                if (this.field === undefined) { this.field = null; } 
                this.field = this.mManifestations.getField();
                this.index = index;
                this.symmAxis = symmAxis;
            } else if (((editorModel != null && (editorModel.constructor != null && editorModel.constructor["__interfaces"] != null && editorModel.constructor["__interfaces"].indexOf("com.vzome.core.editor.api.EditorModel") >= 0)) || editorModel === null) && symmAxis === undefined && index === undefined) {
                let __args = arguments;
                {
                    let __args = arguments;
                    let symmAxis: any = null;
                    let index: any = 0;
                    super(editorModel);
                    if (this.index === undefined) { this.index = 0; } 
                    if (this.proj === undefined) { this.proj = null; } 
                    if (this.symmAxis === undefined) { this.symmAxis = null; } 
                    if (this.field === undefined) { this.field = null; } 
                    this.field = this.mManifestations.getField();
                    this.index = index;
                    this.symmAxis = symmAxis;
                }
            } else throw new Error('invalid overload');
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "B4Polytope";
        }

        /**
         * 
         * @param {*} result
         */
        public getXmlAttributes(result: org.w3c.dom.Element) {
            com.vzome.xml.DomUtils.addAttribute(result, "dynkin", com.vzome.xml.DomUtils.byteToBinary(this.index));
            if (this.symmAxis != null)com.vzome.core.commands.XmlSaveFormat.serializeSegment(result, "start", "end", this.symmAxis);
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        public setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            const binary: string = xml.getAttribute("dynkin");
            this.index = javaemul.internal.IntegerHelper.parseInt(binary, 2);
            if (format.commandEditsCompacted())this.symmAxis = format.parseSegment$org_w3c_dom_Element$java_lang_String$java_lang_String(xml, "start", "end"); else {
                const attrs: com.vzome.core.commands.AttributeMap = format.loadCommandAttributes$org_w3c_dom_Element(xml);
                this.symmAxis = <com.vzome.core.construction.Segment>attrs.get("rotation");
            }
        }

        /**
         * 
         */
        public perform() {
            if (this.symmAxis == null)this.proj = new com.vzome.core.math.Projection.Default(this.field); else {
                const scale: com.vzome.core.algebra.AlgebraicNumber = this.field['createPower$int'](-5);
                this.proj = new com.vzome.core.math.QuaternionProjection(this.field, null, this.symmAxis.getOffset().scale(scale));
            }
            const edgeScales: com.vzome.core.algebra.AlgebraicNumber[] = [null, null, null, null];
            for(let i: number = 0; i < edgeScales.length; i++) {{
                edgeScales[i] = this.field.one();
            };}
            const group: com.vzome.core.math.symmetry.CoxeterGroup = new com.vzome.core.math.symmetry.B4Group(this.field);
            com.vzome.core.math.symmetry.WythoffConstruction.constructPolytope(group, this.index, this.index, edgeScales, group, new B4Polytope.WythoffListener(this, this.field));
            this.redo();
        }
    }
    B4Polytope["__class"] = "com.vzome.core.edits.B4Polytope";


    export namespace B4Polytope {

        export class WythoffListener implements com.vzome.core.math.symmetry.WythoffConstruction.Listener {
            public __parent: any;
            numVertices: number;

            scale: com.vzome.core.algebra.AlgebraicNumber;

            public constructor(__parent: any, field: com.vzome.core.algebra.AlgebraicField) {
                this.__parent = __parent;
                this.numVertices = 0;
                if (this.scale === undefined) { this.scale = null; }
                this.scale = field['createPower$int'](5);
            }

            /**
             * 
             * @param {*} p1
             * @param {*} p2
             * @return {*}
             */
            public addEdge(p1: any, p2: any): any {
                const edge: com.vzome.core.construction.Segment = new com.vzome.core.construction.SegmentJoiningPoints(<com.vzome.core.construction.Point>p1, <com.vzome.core.construction.Point>p2);
                this.__parent.manifestConstruction(edge);
                return edge;
            }

            /**
             * 
             * @param {java.lang.Object[]} vertices
             * @return {*}
             */
            public addFace(vertices: any[]): any {
                return null;
            }

            /**
             * 
             * @param {com.vzome.core.algebra.AlgebraicVector} vertex
             * @return {*}
             */
            public addVertex(vertex: com.vzome.core.algebra.AlgebraicVector): any {
                let projected: com.vzome.core.algebra.AlgebraicVector = vertex;
                if (this.__parent.proj != null)projected = this.__parent.proj.projectImage(vertex, true);
                const p: com.vzome.core.construction.Point = new com.vzome.core.construction.FreePoint(projected.scale(this.scale));
                p.setIndex(this.numVertices++);
                this.__parent.manifestConstruction(p);
                return p;
            }
        }
        WythoffListener["__class"] = "com.vzome.core.edits.B4Polytope.WythoffListener";
        WythoffListener["__interfaces"] = ["com.vzome.core.math.symmetry.WythoffConstruction.Listener"];


    }

}

