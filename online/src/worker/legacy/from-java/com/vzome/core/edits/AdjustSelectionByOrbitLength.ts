import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicNumber } from "../algebra/AlgebraicNumber.js";
import { AlgebraicVector } from "../algebra/AlgebraicVector.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { ActionEnum } from "../editor/api/ActionEnum.js";
import { ChangeSelection } from "../editor/api/ChangeSelection.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { OrbitSource } from "../editor/api/OrbitSource.js";
import { SymmetryAware } from "../editor/api/SymmetryAware.js";
import { Axis } from "../math/symmetry/Axis.js";
import { Direction } from "../math/symmetry/Direction.js";
import { Manifestation } from "../model/Manifestation.js";
import { Panel } from "../model/Panel.js";
import { Strut } from "../model/Strut.js";
import { DomUtils } from "../../xml/DomUtils.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

/**
 * This constructor is only used during deserialization, so it prepares for setXmlAttributes().
 * @param {*} editor
 * @class
 * @extends ChangeSelection
 * @author David Hall
 */
export class AdjustSelectionByOrbitLength extends ChangeSelection {
    /*private*/ orbit: Direction;

    /*private*/ length: AlgebraicNumber;

    /*private*/ symmetry: OrbitSource;

    /*private*/ strutAction: ActionEnum;

    /*private*/ panelAction: ActionEnum;

    /*private*/ editor: EditorModel;

    public constructor(editor: EditorModel) {
        super(editor.getSelection());
        if (this.orbit === undefined) { this.orbit = null; }
        if (this.length === undefined) { this.length = null; }
        if (this.symmetry === undefined) { this.symmetry = null; }
        this.strutAction = ActionEnum.IGNORE;
        this.panelAction = ActionEnum.IGNORE;
        if (this.editor === undefined) { this.editor = null; }
        this.symmetry = (<SymmetryAware><any>editor)['getSymmetrySystem$']();
        this.editor = editor;
    }

    /**
     * 
     * @param {*} props
     */
    public configure(props: java.util.Map<string, any>) {
        const mode: string = <string>props.get("mode");
        const strut: Strut = <Strut><any>props.get("picked");
        this.orbit = <Direction>props.get("orbit");
        this.length = <AlgebraicNumber><any>props.get("length");
        if (mode != null)switch((mode)) {
        case "selectSimilarStruts":
            this.strutAction = ActionEnum.SELECT;
            break;
        case "selectSimilarPanels":
            this.panelAction = ActionEnum.SELECT;
            break;
        case "deselectSimilarStruts":
            this.strutAction = ActionEnum.DESELECT;
            break;
        case "deselectSimilarPanels":
            this.panelAction = ActionEnum.DESELECT;
            break;
        }
        if (strut != null){
            const offset: AlgebraicVector = strut.getOffset();
            const zone: Axis = this.symmetry.getAxis(offset);
            this.orbit = zone.getOrbit();
            this.length = zone.getLength(offset);
        }
    }

    /**
     * 
     */
    public perform() {
        const whichManifestationSet: java.lang.Iterable<Manifestation> = (this.strutAction === ActionEnum.SELECT || this.panelAction === ActionEnum.SELECT) ? this.editor.getRealizedModel() : this.mSelection;
        for(let index=whichManifestationSet.iterator();index.hasNext();) {
            let man = index.next();
            {
                if (man.isRendered()){
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                        const offset: AlgebraicVector = (<Strut><any>man).getOffset();
                        const zone: Axis = this.symmetry.getAxis(offset);
                        if (zone.getOrbit() === this.orbit){
                            if (this.length == null || /* equals */(<any>((o1: any, o2: any) => { if (o1 && o1.equals) { return o1.equals(o2); } else { return o1 === o2; } })(this.length,zone.getLength(offset)))){
                                this.adjustSelection(man, this.strutAction);
                            }
                        }
                    } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                        const zone: Axis = this.symmetry.getAxis((<Panel><any>man)['getNormal$']());
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
    getXmlAttributes(element: Element) {
        if (this.symmetry != null){
            DomUtils.addAttribute(element, "symmetry", this.symmetry.getName());
        }
        if (this.orbit != null){
            DomUtils.addAttribute(element, "orbit", this.orbit.getName());
        }
        if (this.length != null){
            XmlSaveFormat.serializeNumber(element, "length", this.length);
        }
        element.setAttribute("struts", /* Enum.name */ActionEnum[this.strutAction]);
        element.setAttribute("panels", /* Enum.name */ActionEnum[this.panelAction]);
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     */
    setXmlAttributes(xml: Element, format: XmlSaveFormat) {
        this.symmetry = (<SymmetryAware><any>this.editor)['getSymmetrySystem$java_lang_String'](xml.getAttribute("symmetry"));
        this.length = format.parseNumber(xml, "length");
        this.orbit = this.symmetry.getOrbits().getDirection(xml.getAttribute("orbit"));
        if (xml.getLocalName() === ("SelectSimilarSize")){
            this.strutAction = ActionEnum.SELECT;
            this.panelAction = ActionEnum.IGNORE;
        } else {
            this.strutAction = /* Enum.valueOf */<any>ActionEnum[xml.getAttribute("struts")];
            this.panelAction = /* Enum.valueOf */<any>ActionEnum[xml.getAttribute("panels")];
        }
    }
}
AdjustSelectionByOrbitLength["__class"] = "com.vzome.core.edits.AdjustSelectionByOrbitLength";
