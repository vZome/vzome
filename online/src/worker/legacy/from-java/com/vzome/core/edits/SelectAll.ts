import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { ChangeSelection } from "../editor/api/ChangeSelection.js";
import { EditorModel } from "../editor/api/EditorModel.js";
import { Connector } from "../model/Connector.js";
import { RealizedModel } from "../model/RealizedModel.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

export class SelectAll extends ChangeSelection {
    /*private*/ realizedModel: RealizedModel;

    /*private*/ originLast: boolean;

    /**
     * 
     */
    public perform() {
        if (this.originLast){
            let originBall: Connector = null;
            const ignoreGroups: boolean = true;
            for(let index=this.realizedModel.iterator();index.hasNext();) {
                let m = index.next();
                {
                    if (m.isRendered()){
                        if (originBall == null && (m != null && (m.constructor != null && m.constructor["__interfaces"] != null && m.constructor["__interfaces"].indexOf("com.vzome.core.model.Connector") >= 0)) && m.getLocation().isOrigin()){
                            originBall = <Connector><any>m;
                        } else if (!this.mSelection.manifestationSelected(m)){
                            this.select$com_vzome_core_model_Manifestation$boolean(m, ignoreGroups);
                        }
                    }
                }
            }
            if (originBall != null){
                if (this.mSelection.manifestationSelected(originBall)){
                    this.unselect$com_vzome_core_model_Manifestation$boolean(originBall, ignoreGroups);
                    this.redo();
                }
                this.select$com_vzome_core_model_Manifestation$boolean(originBall, ignoreGroups);
            }
        } else {
            for(let index=this.realizedModel.iterator();index.hasNext();) {
                let m = index.next();
                {
                    if (m.isRendered()){
                        if (!this.mSelection.manifestationSelected(m))this.select$com_vzome_core_model_Manifestation$boolean(m, true);
                    }
                }
            }
        }
        super.perform();
    }

    public constructor(editor: EditorModel) {
        super(editor.getSelection());
        if (this.realizedModel === undefined) { this.realizedModel = null; }
        this.originLast = true;
        this.realizedModel = editor.getRealizedModel();
    }

    /**
     * 
     * @param {*} xml
     * @param {XmlSaveFormat} format
     */
    setXmlAttributes(xml: Element, format: XmlSaveFormat) {
        const mode: string = xml.getAttribute("originLast");
        this.originLast = "true" === mode;
    }

    /**
     * 
     * @param {*} element
     */
    getXmlAttributes(element: Element) {
        if (this.originLast)element.setAttribute("originLast", "true");
    }

    /**
     * 
     * @return {boolean}
     */
    groupingAware(): boolean {
        return true;
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "SelectAll";
    }
}
SelectAll["__class"] = "com.vzome.core.edits.SelectAll";
