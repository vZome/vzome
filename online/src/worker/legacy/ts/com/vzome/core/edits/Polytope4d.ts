/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export class Polytope4d extends com.vzome.core.editor.api.ChangeManifestations {
        /*private*/ index: number;

        /*private*/ field: com.vzome.core.algebra.AlgebraicField;

        /*private*/ proj: com.vzome.core.math.Projection;

        /*private*/ quaternion: com.vzome.core.algebra.AlgebraicVector;

        /*private*/ groupName: string;

        /*private*/ edgesToRender: number;

        /*private*/ edgeScales: com.vzome.core.algebra.AlgebraicNumber[];

        /*private*/ renderGroupName: string;

        /*private*/ symmetries: com.vzome.core.math.symmetry.Symmetries4D;

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
            if (this.index === undefined) { this.index = 0; }
            if (this.field === undefined) { this.field = null; }
            if (this.proj === undefined) { this.proj = null; }
            if (this.quaternion === undefined) { this.quaternion = null; }
            if (this.groupName === undefined) { this.groupName = null; }
            this.edgesToRender = 15;
            this.edgeScales = [null, null, null, null];
            if (this.renderGroupName === undefined) { this.renderGroupName = null; }
            if (this.symmetries === undefined) { this.symmetries = null; }
            this.symmetries = (<com.vzome.core.editor.api.SymmetryAware><any>editor).get4dSymmetries();
            this.field = editor.getRealizedModel().getField();
            for(let i: number = 0; i < this.edgeScales.length; i++) {{
                this.edgeScales[i] = this.field.one();
            };}
        }

        /**
         * 
         * @param {*} params
         */
        public configure(params: java.util.Map<string, any>) {
            this.groupName = <string>params.get("groupName");
            this.renderGroupName = <string>params.get("renderGroupName");
            this.index = (<number>params.get("index")|0);
            this.edgesToRender = (<number>params.get("edgesToRender")|0);
            this.edgeScales = <com.vzome.core.algebra.AlgebraicNumber[]>params.get("edgeScales");
            this.quaternion = <com.vzome.core.algebra.AlgebraicVector>params.get("quaternion");
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "Polytope4d";
        }

        /**
         * 
         * @param {*} xml
         */
        public getXmlAttributes(xml: org.w3c.dom.Element) {
            if (this.quaternion != null)com.vzome.xml.DomUtils.addAttribute(xml, "quaternion", this.quaternion.toParsableString());
            com.vzome.xml.DomUtils.addAttribute(xml, "group", this.groupName);
            com.vzome.xml.DomUtils.addAttribute(xml, "wythoff", com.vzome.xml.DomUtils.byteToBinary(this.index));
            if (this.edgesToRender !== 15)com.vzome.xml.DomUtils.addAttribute(xml, "renderEdges", com.vzome.xml.DomUtils.byteToBinary(this.edgesToRender));
            if (!(this.renderGroupName === this.groupName))com.vzome.xml.DomUtils.addAttribute(xml, "renderGroup", this.renderGroupName);
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        public setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            const binary: string = xml.getAttribute("wythoff");
            this.index = javaemul.internal.IntegerHelper.parseInt(binary, 2);
            const renderString: string = xml.getAttribute("renderEdges");
            this.edgesToRender = (renderString == null || /* isEmpty */(renderString.length === 0)) ? this.index : javaemul.internal.IntegerHelper.parseInt(renderString, 2);
            this.groupName = xml.getAttribute("group");
            const rgString: string = xml.getAttribute("renderGroup");
            this.renderGroupName = (rgString == null || /* isEmpty */(rgString.length === 0)) ? this.groupName : rgString;
            let quatString: string = xml.getAttribute("quaternion");
            if (quatString != null && !("" === quatString)){
                if (/* contains */(quatString.indexOf("+") != -1)){
                    quatString = /* replace */quatString.split(',').join(' ');
                    quatString = /* replace */quatString.split('(').join(' ');
                    quatString = /* replace */quatString.split(')').join(' ');
                    quatString = /* replace */quatString.split('+').join(' ');
                    const irrat: string = this.field['getIrrational$int'](0).charAt(0);
                    quatString = /* replace */quatString.split(irrat).join(' ');
                    quatString = quatString + " 0 0 0";
                }
                this.quaternion = this.field.parseVector(quatString);
            } else {
                let segment: com.vzome.core.construction.Segment = null;
                if (format.commandEditsCompacted())segment = format.parseSegment$org_w3c_dom_Element$java_lang_String$java_lang_String(xml, "start", "end"); else {
                    const attrs: com.vzome.core.commands.AttributeMap = format.loadCommandAttributes$org_w3c_dom_Element(xml);
                    segment = <com.vzome.core.construction.Segment>attrs.get("rotation");
                }
                if (segment != null)this.quaternion = segment.getOffset().inflateTo4d$();
            }
        }

        /**
         * 
         */
        public perform() {
            if (this.quaternion == null)this.proj = new com.vzome.core.math.Projection.Default(this.field); else this.proj = new com.vzome.core.math.QuaternionProjection(this.field, null, this.quaternion.scale(this.field['createPower$int'](-5)));
            this.symmetries.constructPolytope(this.groupName, this.index, this.edgesToRender, this.edgeScales, new Polytope4d.WythoffListener(this));
            this.redo();
        }

        public static getSupportedGroups(): string[] {
            return ["A4", "B4/C4", "D4", "F4", "H4"];
        }
    }
    Polytope4d["__class"] = "com.vzome.core.edits.Polytope4d";


    export namespace Polytope4d {

        export class WythoffListener implements com.vzome.core.math.symmetry.WythoffConstruction.Listener {
            public __parent: any;
            numVertices: number;

            vertices: java.util.Map<string, com.vzome.core.construction.Point>;

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
                let p: com.vzome.core.construction.Point = this.vertices.get(vertex.toString());
                if (p == null){
                    let projected: com.vzome.core.algebra.AlgebraicVector = vertex;
                    if (this.__parent.proj != null)projected = this.__parent.proj.projectImage(vertex, true);
                    projected = projected.scale(this.__parent.field['createPower$int'](5));
                    p = new com.vzome.core.construction.FreePoint(projected);
                    p.setIndex(this.numVertices++);
                    this.__parent.manifestConstruction(p);
                    this.vertices.put(vertex.toString(), p);
                }
                return p;
            }

            constructor(__parent: any) {
                this.__parent = __parent;
                this.numVertices = 0;
                this.vertices = <any>(new java.util.HashMap<any, any>());
            }
        }
        WythoffListener["__class"] = "com.vzome.core.edits.Polytope4d.WythoffListener";
        WythoffListener["__interfaces"] = ["com.vzome.core.math.symmetry.WythoffConstruction.Listener"];


    }

}

