import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { Command } from "../commands/Command.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { XmlSymmetryFormat } from "../commands/XmlSymmetryFormat.js";
import { ChangeManifestations } from "../editor/api/ChangeManifestations.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { OrbitSource } from "../editor/api/OrbitSource.js";
import { SymmetryAware } from "../editor/api/SymmetryAware.js";
import { Axis } from "../math/symmetry/Axis.js";
import { Direction } from "../math/symmetry/Direction.js";
import { Strut } from "../model/Strut.js";
import { DomUtils } from "../../xml/DomUtils.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

/**
 * called from the main menu and when opening a file
 * @param symmetry
 * @param selection
 * @param model
 * @param {*} editor
 * @class
 * @extends ChangeManifestations
 * @author David Hall
 */
export class SelectParallelStruts extends ChangeManifestations {
    /*private*/ symmetry: OrbitSource;

    /*private*/ orbit: Direction;

    /*private*/ axis: Axis;

    /*private*/ editor: EditorModel;

    public constructor(editor: EditorModel) {
        super(editor);
        if (this.symmetry === undefined) { this.symmetry = null; }
        if (this.orbit === undefined) { this.orbit = null; }
        if (this.axis === undefined) { this.axis = null; }
        if (this.editor === undefined) { this.editor = null; }
        this.editor = editor;
        this.symmetry = (<SymmetryAware><any>editor)['getSymmetrySystem$']();
    }

    /**
     * 
     * @param {*} props
     */
    public configure(props: java.util.Map<string, any>) {
        const strut: Strut = <Strut><any>props.get("picked");
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
            const lastStrut: Strut = this.getLastSelectedStrut();
            if (lastStrut != null){
                const offset: AlgebraicVector = lastStrut.getOffset();
                this.orbit = this.symmetry.getAxis(offset).getOrbit();
                this.axis = this.orbit.getAxis$com_vzome_core_algebra_AlgebraicVector(offset);
            }
        }
        if (this.orbit == null || this.axis == null){
            throw new Command.Failure("select a reference strut.");
        }
        this.unselectAll();
        const oppositeAxis: Axis = this.symmetry.getSymmetry().getPrincipalReflection() == null ? this.orbit.getAxis$int$int(((this.axis.getSense() + 1) % 2), this.axis.getOrientation()) : this.orbit.getAxis$int$int$boolean(this.axis.getSense(), this.axis.getOrientation(), !this.axis.isOutbound());
        for(let index=this.getStruts().iterator();index.hasNext();) {
            let strut = index.next();
            {
                const strutAxis: Axis = this.symmetry.getAxis(strut.getOffset());
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
    getXmlAttributes(element: Element) {
        if (this.symmetry != null)DomUtils.addAttribute(element, "symmetry", this.symmetry.getName());
        if (this.orbit != null)DomUtils.addAttribute(element, "orbit", this.orbit.getName());
        if (this.axis != null)XmlSymmetryFormat.serializeAxis(element, "symm", "dir", "index", "sense", this.axis);
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     */
    setXmlAttributes(xml: Element, format: XmlSaveFormat) {
        this.symmetry = (<SymmetryAware><any>this.editor)['getSymmetrySystem$java_lang_String'](xml.getAttribute("symmetry"));
        this.orbit = this.symmetry.getOrbits().getDirection(xml.getAttribute("orbit"));
        this.axis = (<XmlSymmetryFormat>format).parseAxis(xml, "symm", "dir", "index", "sense");
    }
}
SelectParallelStruts["__class"] = "com.vzome.core.edits.SelectParallelStruts";
