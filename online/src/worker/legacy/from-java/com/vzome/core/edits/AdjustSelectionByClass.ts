import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { ActionEnum } from "../editor/api/ActionEnum.js";
import { ChangeSelection } from "../editor/api/ChangeSelection.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Connector } from "../model/Connector.js";
import { Manifestation } from "../model/Manifestation.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

/**
 * @author David Hall
 * This class is designed to be a generalized replacement for the legacy DeselectByClass
 * It allows balls, struts and/or panels to be selected, deselected or ignored by class
 * It can be used in place of DeselectByClass including the ability to parse the legacy XML.
 * DeselectByClass has been renamed as AdjustSelectionByClass and modified with the additional functionality.
 * @param {*} editor
 * @class
 * @extends ChangeSelection
 */
export class AdjustSelectionByClass extends ChangeSelection {
    /*private*/ ballAction: ActionEnum;

    /*private*/ strutAction: ActionEnum;

    /*private*/ panelAction: ActionEnum;

    /*private*/ editor: EditorModel;

    /*private*/ originLast: boolean;

    public constructor(editor: EditorModel) {
        super(editor.getSelection());
        this.ballAction = ActionEnum.IGNORE;
        this.strutAction = ActionEnum.IGNORE;
        this.panelAction = ActionEnum.IGNORE;
        if (this.editor === undefined) { this.editor = null; }
        this.originLast = true;
        this.editor = editor;
    }

    /**
     * 
     * @param {*} props
     */
    public configure(props: java.util.Map<string, any>) {
        const mode: string = <string>props.get("mode");
        if (mode != null)switch((mode)) {
        case "selectBalls":
            this.ballAction = ActionEnum.SELECT;
            break;
        case "selectStruts":
            this.strutAction = ActionEnum.SELECT;
            break;
        case "selectPanels":
            this.panelAction = ActionEnum.SELECT;
            break;
        case "deselectBalls":
        case "unselectBalls":
            this.ballAction = ActionEnum.DESELECT;
            break;
        case "deselectStruts":
            this.strutAction = ActionEnum.DESELECT;
            break;
        case "deselectPanels":
            this.panelAction = ActionEnum.DESELECT;
            break;
        case "unselectStruts":
        case "unselectStrutsAndPanels":
            this.strutAction = ActionEnum.DESELECT;
            this.panelAction = ActionEnum.DESELECT;
            break;
        }
    }

    /**
     * 
     */
    public perform() {
        const whichManifestationSet: java.lang.Iterable<Manifestation> = (this.ballAction === ActionEnum.SELECT || this.strutAction === ActionEnum.SELECT || this.panelAction === ActionEnum.SELECT) ? this.editor.getRealizedModel() : this.mSelection;
        let originBall: Connector = null;
        for(let index=whichManifestationSet.iterator();index.hasNext();) {
            let man = index.next();
            {
                if (man.isRendered()){
                    if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)){
                        if (this.originLast && originBall == null && this.ballAction === ActionEnum.SELECT && man.getLocation().isOrigin()){
                            originBall = <Connector><any>man;
                        } else {
                            this.adjustSelection(man, this.ballAction);
                        }
                    } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Strut") >= 0)){
                        this.adjustSelection(man, this.strutAction);
                    } else if (man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Panel") >= 0)){
                        this.adjustSelection(man, this.panelAction);
                    }
                }
            }
        }
        if (originBall != null){
            const ignoreGroups: boolean = true;
            if (this.mSelection.manifestationSelected(originBall)){
                this.unselect$com_vzome_core_model_Manifestation$boolean(originBall, ignoreGroups);
                this.redo();
            }
            this.select$com_vzome_core_model_Manifestation$boolean(originBall, ignoreGroups);
        }
        this.redo();
    }

    /**
     * 
     * @param {*} element
     */
    getXmlAttributes(element: Element) {
        element.setAttribute("balls", /* Enum.name */ActionEnum[this.ballAction]);
        element.setAttribute("struts", /* Enum.name */ActionEnum[this.strutAction]);
        element.setAttribute("panels", /* Enum.name */ActionEnum[this.panelAction]);
        if (this.originLast){
            element.setAttribute("originLast", "true");
        }
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     */
    setXmlAttributes(xml: Element, format: XmlSaveFormat) {
        if (xml.getLocalName() === ("DeselectByClass")){
            if (xml.getAttribute("class") === ("balls")){
                this.ballAction = ActionEnum.DESELECT;
                this.strutAction = ActionEnum.IGNORE;
                this.panelAction = ActionEnum.IGNORE;
            } else {
                this.ballAction = ActionEnum.IGNORE;
                this.strutAction = ActionEnum.DESELECT;
                this.panelAction = ActionEnum.DESELECT;
            }
        } else {
            this.ballAction = /* Enum.valueOf */<any>ActionEnum[xml.getAttribute("balls")];
            this.strutAction = /* Enum.valueOf */<any>ActionEnum[xml.getAttribute("struts")];
            this.panelAction = /* Enum.valueOf */<any>ActionEnum[xml.getAttribute("panels")];
        }
        const mode: string = xml.getAttribute("originLast");
        this.originLast = "true" === mode;
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "AdjustSelectionByClass";
    }
}
AdjustSelectionByClass["__class"] = "com.vzome.core.edits.AdjustSelectionByClass";
