/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    export abstract class ImportMesh extends com.vzome.core.editor.api.ChangeManifestations {
        meshData: string;

        scale: com.vzome.core.algebra.AlgebraicNumber;

        projection: com.vzome.core.math.Projection;

        editor: com.vzome.core.editor.api.EditorModel;

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
            if (this.meshData === undefined) { this.meshData = null; }
            if (this.scale === undefined) { this.scale = null; }
            if (this.projection === undefined) { this.projection = null; }
            if (this.editor === undefined) { this.editor = null; }
            this.editor = editor;
        }

        /**
         * 
         * @param {*} params
         */
        public configure(params: java.util.Map<string, any>) {
            const field: com.vzome.core.algebra.AlgebraicField = this.mManifestations.getField();
            this.meshData = <string>params.get("vef");
            this.projection = <com.vzome.core.math.Projection><any>params.get("projection");
            this.scale = <com.vzome.core.algebra.AlgebraicNumber><any>params.get("scale");
            if (this.scale == null){
                this.scale = field.one();
            }
            const mode: string = <string>params.getOrDefault("mode", "");
            switch((mode)) {
            case "":
                break;
            case "clipboard":
                if (this.meshData != null && !/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(this.meshData, "vZome VEF") && !/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(this.meshData, "{"))this.meshData = null;
                break;
            case "Quaternion":
                break;
            default:
                this.setProjection(mode, field);
                break;
            }
        }

        /**
         * 
         * @return {boolean}
         */
        public isNoOp(): boolean {
            return this.meshData == null;
        }

        /**
         * 
         * @param {*} element
         */
        getXmlAttributes(element: org.w3c.dom.Element) {
            if (this.scale != null)com.vzome.xml.DomUtils.addAttribute(element, "scale", this.scale.toString(com.vzome.core.algebra.AlgebraicField.ZOMIC_FORMAT));
            if (this.projection != null){
                const name: string = this.projection.getProjectionName();
                if (!("" === name)){
                    com.vzome.xml.DomUtils.addAttribute(element, "projection", this.projection.getProjectionName());
                    this.projection.getXmlAttributes(element);
                }
            }
            const textNode: org.w3c.dom.Node = element.getOwnerDocument().createTextNode(com.vzome.core.commands.XmlSaveFormat.escapeNewlines(this.meshData));
            element.appendChild(textNode);
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            const field: com.vzome.core.algebra.AlgebraicField = format.getField();
            this.scale = format.parseRationalNumber(xml, "scale");
            if (this.scale == null){
                this.scale = field.one();
            }
            this.meshData = xml.getTextContent();
            const projectionName: string = xml.getAttribute("projection");
            if ("" === projectionName){
                const quaternion: com.vzome.core.algebra.AlgebraicVector = format.parseRationalVector(xml, "quaternion");
                this.projection = quaternion == null ? null : new com.vzome.core.math.QuaternionProjection(field, null, quaternion);
            } else {
                this.setProjection(projectionName, field);
            }
            if (this.projection != null){
                this.projection.setXmlAttributes(xml);
            }
        }

        setProjection(projectionName: string, field: com.vzome.core.algebra.AlgebraicField) {
            switch((projectionName)) {
            case "Quaternion":
                this.projection = new com.vzome.core.math.QuaternionProjection(field, null, null);
                break;
            case "SixCube":
                this.projection = new com.vzome.core.math.SixCubeProjection(field);
                break;
            case "Tetrahedral":
                this.projection = new com.vzome.core.math.TetrahedralProjection(field);
                break;
            case "Perspective":
                this.projection = new com.vzome.core.math.PerspectiveProjection(field, null);
                break;
            }
        }

        /**
         * 
         */
        public perform() {
            if (this.meshData == null)return;
            let offset: com.vzome.core.algebra.AlgebraicVector = null;
            let pointFound: boolean = false;
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                        const nextPoint: com.vzome.core.construction.Point = <com.vzome.core.construction.Point>(<com.vzome.core.model.Connector><any>man).getFirstConstruction();
                        if (!pointFound){
                            pointFound = true;
                            offset = nextPoint.getLocation();
                        } else {
                            offset = null;
                            break;
                        }
                    }
                    if (this.deselectInputs())this.unselect$com_vzome_core_model_Manifestation(man);
                }
            }
            const field: com.vzome.core.algebra.AlgebraicField = this.mManifestations.getField();
            const events: com.vzome.core.editor.api.ManifestConstructions = new com.vzome.core.editor.api.ManifestConstructions(this);
            const registry: com.vzome.core.algebra.AlgebraicField.Registry = new ImportMesh.ImportMesh$0(this, field);
            if (this.projection == null)this.projection = new com.vzome.core.math.Projection.Default(field);
            try {
                this.parseMeshData(offset, events, registry);
            } catch(e) {
                throw new com.vzome.core.commands.Command.Failure("Incorrect content for this import:\n" + e.message);
            }
            this.redo();
        }

        abstract parseMeshData(offset: com.vzome.core.algebra.AlgebraicVector, events: com.vzome.core.editor.api.ManifestConstructions, registry: com.vzome.core.algebra.AlgebraicField.Registry);

        deselectInputs(): boolean {
            return true;
        }
    }
    ImportMesh["__class"] = "com.vzome.core.edits.ImportMesh";


    export namespace ImportMesh {

        export class ImportMesh$0 implements com.vzome.core.algebra.AlgebraicField.Registry {
            public __parent: any;
            /**
             * 
             * @param {string} name
             * @return {*}
             */
            public getField(name: string): com.vzome.core.algebra.AlgebraicField {
                if (this.field.supportsSubfield(name))return this.field; else {
                    return null;
                }
            }

            constructor(__parent: any, private field: any) {
                this.__parent = __parent;
            }
        }
        ImportMesh$0["__interfaces"] = ["com.vzome.core.algebra.AlgebraicField.Registry"];


    }

}

