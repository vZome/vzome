/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    /**
     * This constructor is only used during deserialization, so it prepares for setXmlAttributes().
     * @param {*} editor
     * @class
     * @extends com.vzome.core.editor.api.ChangeSelection
     * @author David Hall
     */
    export class AdjustSelectionByOrbitLength extends com.vzome.core.editor.api.ChangeSelection {
        /*private*/ orbit: com.vzome.core.math.symmetry.Direction;

        /*private*/ length: com.vzome.core.algebra.AlgebraicNumber;

        /*private*/ symmetry: com.vzome.core.editor.api.OrbitSource;

        /*private*/ strutAction: com.vzome.core.editor.api.ActionEnum;

        /*private*/ panelAction: com.vzome.core.editor.api.ActionEnum;

        /*private*/ editor: com.vzome.core.editor.api.EditorModel;

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor.getSelection());
            if (this.orbit === undefined) { this.orbit = null; }
            if (this.length === undefined) { this.length = null; }
            if (this.symmetry === undefined) { this.symmetry = null; }
            this.strutAction = com.vzome.core.editor.api.ActionEnum.IGNORE;
            this.panelAction = com.vzome.core.editor.api.ActionEnum.IGNORE;
            if (this.editor === undefined) { this.editor = null; }
            this.symmetry = (<com.vzome.core.editor.api.SymmetryAware><any>editor)['getSymmetrySystem$']();
            this.editor = editor;
        }

        /**
         * 
         * @param {*} props
         */
        public configure(props: java.util.Map<string, any>) {
            const mode: string = <string>props.get("mode");
            const strut: com.vzome.core.model.Strut = <com.vzome.core.model.Strut><any>props.get("picked");
            this.orbit = <com.vzome.core.math.symmetry.Direction>props.get("orbit");
            this.length = <com.vzome.core.algebra.AlgebraicNumber><any>props.get("length");
            if (mode != null)switch((mode)) {
            case "selectSimilarStruts":
                this.strutAction = com.vzome.core.editor.api.ActionEnum.SELECT;
                break;
            case "selectSimilarPanels":
                this.panelAction = com.vzome.core.editor.api.ActionEnum.SELECT;
                break;
            case "deselectSimilarStruts":
                this.strutAction = com.vzome.core.editor.api.ActionEnum.DESELECT;
                break;
            case "deselectSimilarPanels":
                this.panelAction = com.vzome.core.editor.api.ActionEnum.DESELECT;
                break;
            }
            if (strut != null){
                const offset: com.vzome.core.algebra.AlgebraicVector = strut.getOffset();
                const zone: com.vzome.core.math.symmetry.Axis = this.symmetry.getAxis(offset);
                this.orbit = zone.getOrbit();
                this.length = zone.getLength(offset);
            }
        }

        /**
         * 
         */
        public perform() {
            const whichManifestationSet: java.lang.Iterable<com.vzome.core.model.Manifestation> = (this.strutAction === com.vzome.core.editor.api.ActionEnum.SELECT || this.panelAction === com.vzome.core.editor.api.ActionEnum.SELECT) ? this.editor.getRealizedModel() : this.mSelection;
            for(let index=whichManifestationSet.iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (man.isRendered()){
                        if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                            const offset: com.vzome.core.algebra.AlgebraicVector = (<com.vzome.core.model.Strut><any>man).getOffset();
                            const zone: com.vzome.core.math.symmetry.Axis = this.symmetry.getAxis(offset);
                            if (zone.getOrbit() === this.orbit){
                                if (this.length == null || /* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(this.length,zone.getLength(offset)))){
                                    this.adjustSelection(man, this.strutAction);
                                }
                            }
                        } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                            const zone: com.vzome.core.math.symmetry.Axis = this.symmetry.getAxis((<com.vzome.core.model.Panel><any>man)['getNormal$']());
                            if (zone.getOrbit() === this.orbit){
                                this.adjustSelection(man, this.panelAction);
                            }
                        }
                    }
                }
            }
            this.redo();
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "AdjustSelectionByOrbitLength";
        }

        /**
         * 
         * @param {*} element
         */
        getXmlAttributes(element: org.w3c.dom.Element) {
            if (this.symmetry != null){
                com.vzome.xml.DomUtils.addAttribute(element, "symmetry", this.symmetry.getName());
            }
            if (this.orbit != null){
                com.vzome.xml.DomUtils.addAttribute(element, "orbit", this.orbit.getName());
            }
            if (this.length != null){
                com.vzome.core.commands.XmlSaveFormat.serializeNumber(element, "length", this.length);
            }
            element.setAttribute("struts", /* Enum.name */com.vzome.core.editor.api.ActionEnum[this.strutAction]);
            element.setAttribute("panels", /* Enum.name */com.vzome.core.editor.api.ActionEnum[this.panelAction]);
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            this.symmetry = (<com.vzome.core.editor.api.SymmetryAware><any>this.editor)['getSymmetrySystem$java_lang_String'](xml.getAttribute("symmetry"));
            this.length = format.parseNumber(xml, "length");
            this.orbit = this.symmetry.getOrbits().getDirection(xml.getAttribute("orbit"));
            if (xml.getLocalName() === ("SelectSimilarSize")){
                this.strutAction = com.vzome.core.editor.api.ActionEnum.SELECT;
                this.panelAction = com.vzome.core.editor.api.ActionEnum.IGNORE;
            } else {
                this.strutAction = /* Enum.valueOf */<any>com.vzome.core.editor.api.ActionEnum[xml.getAttribute("struts")];
                this.panelAction = /* Enum.valueOf */<any>com.vzome.core.editor.api.ActionEnum[xml.getAttribute("panels")];
            }
        }
    }
    AdjustSelectionByOrbitLength["__class"] = "com.vzome.core.edits.AdjustSelectionByOrbitLength";

}

