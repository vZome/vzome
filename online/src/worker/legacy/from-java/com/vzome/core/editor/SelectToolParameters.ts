import { XmlSaveFormat } from "../commands/XmlSaveFormat.js";
import { Tool } from "./Tool.js";
import { ToolsModel } from "./ToolsModel.js";
import { ChangeManifestations } from "./api/ChangeManifestations.js";
import { Manifestation } from "../model/Manifestation.js";
import { Element } from "../../../../org/w3c/dom/Element.js";

export class SelectToolParameters extends ChangeManifestations {
    /*private*/ tool: Tool;

    /*private*/ tools: ToolsModel;

    constructor(tools: ToolsModel, tool: Tool) {
        super(tools.getEditorModel());
        if (this.tool === undefined) { this.tool = null; }
        if (this.tools === undefined) { this.tools = null; }
        this.tools = tools;
        this.tool = tool;
    }

    /**
     * 
     */
    public perform() {
        for(let index=this.mSelection.iterator();index.hasNext();) {
            let man = index.next();
            super.unselect$com_vzome_core_model_Manifestation$boolean(man, true)
        }
        this.redo();
        for(let index=this.tool.getParameters().iterator();index.hasNext();) {
            let con = index.next();
            {
                const man: Manifestation = this.manifestConstruction(con);
                this.select$com_vzome_core_model_Manifestation(man);
            }
        }
        this.redo();
    }

    /**
     * 
     * @return {string}
     */
    getXmlElementName(): string {
        return "SelectToolParameters";
    }

    /**
     * 
     * @param {*} element
     */
    getXmlAttributes(element: Element) {
        element.setAttribute("name", this.tool.getId());
    }

    /**
     * 
     * @param {*} element
     * @param {XmlSaveFormat} format
     */
    setXmlAttributes(element: Element, format: XmlSaveFormat) {
        const toolName: string = element.getAttribute("name");
        this.tool = this.tools.get(toolName);
    }
}
SelectToolParameters["__class"] = "com.vzome.core.editor.SelectToolParameters";
