/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.edits {
    /**
     * called from the main menu and when opening a file
     * @param symmetry
     * @param selection
     * @param model
     * @param {*} editor
     * @class
     * @extends com.vzome.core.editor.api.ChangeManifestations
     * @author David Hall
     */
    export class SelectParallelStruts extends com.vzome.core.editor.api.ChangeManifestations {
        /*private*/ symmetry: com.vzome.core.editor.api.OrbitSource;

        /*private*/ orbit: com.vzome.core.math.symmetry.Direction;

        /*private*/ axis: com.vzome.core.math.symmetry.Axis;

        /*private*/ editor: com.vzome.core.editor.api.EditorModel;

        public constructor(editor: com.vzome.core.editor.api.EditorModel) {
            super(editor);
            if (this.symmetry === undefined) { this.symmetry = null; }
            if (this.orbit === undefined) { this.orbit = null; }
            if (this.axis === undefined) { this.axis = null; }
            if (this.editor === undefined) { this.editor = null; }
            this.editor = editor;
            this.symmetry = (<com.vzome.core.editor.api.SymmetryAware><any>editor)['getSymmetrySystem$']();
        }

        /**
         * 
         * @param {*} props
         */
        public configure(props: java.util.Map<string, any>) {
            const strut: com.vzome.core.model.Strut = <com.vzome.core.model.Strut><any>props.get("picked");
            if (strut != null){
                this.axis = this.symmetry.getAxis(strut.getOffset());
                this.orbit = this.axis.getOrbit();
            }
        }

        /**
         * 
         */
        public perform() {
            if (this.orbit == null || this.axis == null){
                const lastStrut: com.vzome.core.model.Strut = this.getLastSelectedStrut();
                if (lastStrut != null){
                    const offset: com.vzome.core.algebra.AlgebraicVector = lastStrut.getOffset();
                    this.orbit = this.symmetry.getAxis(offset).getOrbit();
                    this.axis = this.orbit.getAxis$com_vzome_core_algebra_AlgebraicVector(offset);
                }
            }
            if (this.orbit == null || this.axis == null){
                throw new com.vzome.core.commands.Command.Failure("select a reference strut.");
            }
            this.unselectAll();
            const oppositeAxis: com.vzome.core.math.symmetry.Axis = this.symmetry.getSymmetry().getPrincipalReflection() == null ? this.orbit.getAxis$int$int(((this.axis.getSense() + 1) % 2), this.axis.getOrientation()) : this.orbit.getAxis$int$int$boolean(this.axis.getSense(), this.axis.getOrientation(), !this.axis.isOutbound());
            for(let index=this.getStruts().iterator();index.hasNext();) {
                let strut = index.next();
                {
                    const strutAxis: com.vzome.core.math.symmetry.Axis = this.symmetry.getAxis(strut.getOffset());
                    if (strutAxis != null && strutAxis.getOrbit().equals(this.orbit)){
                        if (strutAxis.equals(this.axis) || strutAxis.equals(oppositeAxis)){
                            this.select$com_vzome_core_model_Manifestation(strut);
                        }
                    }
                }
            }
            super.perform();
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            return "SelectParallelStruts";
        }

        /**
         * 
         * @param {*} element
         */
        getXmlAttributes(element: org.w3c.dom.Element) {
            if (this.symmetry != null)com.vzome.xml.DomUtils.addAttribute(element, "symmetry", this.symmetry.getName());
            if (this.orbit != null)com.vzome.xml.DomUtils.addAttribute(element, "orbit", this.orbit.getName());
            if (this.axis != null)com.vzome.core.commands.XmlSymmetryFormat.serializeAxis(element, "symm", "dir", "index", "sense", this.axis);
        }

        /**
         * 
         * @param {*} xml
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        setXmlAttributes(xml: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            this.symmetry = (<com.vzome.core.editor.api.SymmetryAware><any>this.editor)['getSymmetrySystem$java_lang_String'](xml.getAttribute("symmetry"));
            this.orbit = this.symmetry.getOrbits().getDirection(xml.getAttribute("orbit"));
            this.axis = (<com.vzome.core.commands.XmlSymmetryFormat>format).parseAxis(xml, "symm", "dir", "index", "sense");
        }
    }
    SelectParallelStruts["__class"] = "com.vzome.core.edits.SelectParallelStruts";

}

