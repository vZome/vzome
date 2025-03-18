/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor {
    export class SelectToolParameters extends com.vzome.core.editor.api.ChangeManifestations {
        /*private*/ tool: com.vzome.core.editor.Tool;

        /*private*/ tools: com.vzome.core.editor.ToolsModel;

        constructor(tools: com.vzome.core.editor.ToolsModel, tool: com.vzome.core.editor.Tool) {
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
                    const man: com.vzome.core.model.Manifestation = this.manifestConstruction(con);
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
        getXmlAttributes(element: org.w3c.dom.Element) {
            element.setAttribute("name", this.tool.getId());
        }

        /**
         * 
         * @param {*} element
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        setXmlAttributes(element: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            const toolName: string = element.getAttribute("name");
            this.tool = this.tools.get(toolName);
        }
    }
    SelectToolParameters["__class"] = "com.vzome.core.editor.SelectToolParameters";

}

