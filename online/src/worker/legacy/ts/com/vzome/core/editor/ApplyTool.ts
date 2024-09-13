/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor {
    export class ApplyTool extends com.vzome.core.editor.api.ChangeManifestations {
        static logger: java.util.logging.Logger; public static logger_$LI$(): java.util.logging.Logger { if (ApplyTool.logger == null) { ApplyTool.logger = java.util.logging.Logger.getLogger("com.vzome.core.editor.ApplyTool"); }  return ApplyTool.logger; }

        /**
         * 
         */
        public perform() {
            if (ApplyTool.logger_$LI$().isLoggable(java.util.logging.Level.FINE))ApplyTool.logger_$LI$().fine("performing ApplyTool " + this.tool.getId() + " :: " + this.tool.getCategory());
            const inputs: java.util.List<com.vzome.core.model.Manifestation> = <any>(new java.util.ArrayList<any>());
            for(let index=this.mSelection.iterator();index.hasNext();) {
                let man = index.next();
                {
                    if (this.deleteInputs && this.tool.needsInput()){
                        super.unselect$com_vzome_core_model_Manifestation$boolean(man, true);
                        super.deleteManifestation(man);
                        if (ApplyTool.logger_$LI$().isLoggable(java.util.logging.Level.FINEST))ApplyTool.logger_$LI$().finest("ApplyTool - unselect and delete " + man.toString());
                    } else if (this.hideInputs && this.tool.needsInput()){
                        super.unselect$com_vzome_core_model_Manifestation$boolean(man, true);
                        super.hideManifestation(man);
                        if (ApplyTool.logger_$LI$().isLoggable(java.util.logging.Level.FINEST))ApplyTool.logger_$LI$().finest("ApplyTool - unselect and hide " + man.toString());
                    } else if (!this.selectInputs){
                        super.unselect$com_vzome_core_model_Manifestation$boolean(man, true);
                        if (ApplyTool.logger_$LI$().isLoggable(java.util.logging.Level.FINEST))ApplyTool.logger_$LI$().finest("ApplyTool - unselect " + man.toString());
                    }
                    if (this.tool.needsInput())inputs.add(man);
                }
            }
            this.redo();
            this.tool.prepare(this);
            if (this.tool.needsInput()){
                for(let index=inputs.iterator();index.hasNext();) {
                    let man = index.next();
                    {
                        const c: com.vzome.core.construction.Construction = man.toConstruction();
                        c.setColor(this.copyColors ? man.getColor() : null);
                        this.tool.performEdit(c, this);
                    }
                }
            } else {
                for(let index=this.mManifestations.iterator();index.hasNext();) {
                    let man = index.next();
                    {
                        this.tool.performSelect(man, this);
                    }
                }
            }
            this.tool.complete(this);
            this.redo();
            super.perform();
        }

        /*private*/ tool: com.vzome.core.editor.Tool;

        /*private*/ selectInputs: boolean;

        /*private*/ deselectOutputs: boolean;

        /*private*/ justSelect: boolean;

        /*private*/ hideInputs: boolean;

        /*private*/ deleteInputs: boolean;

        /*private*/ redundantOutputs: boolean;

        /*private*/ copyColors: boolean;

        /*private*/ tools: com.vzome.core.editor.ToolsModel;

        public constructor(tools: com.vzome.core.editor.ToolsModel, tool: com.vzome.core.editor.Tool, selectInputs: boolean, deleteInputs: boolean, createOutputs: boolean, selectOutputs: boolean, redundantOutputs: boolean, copyColors: boolean) {
            super(tools.getEditorModel());
            if (this.tool === undefined) { this.tool = null; }
            if (this.selectInputs === undefined) { this.selectInputs = false; }
            if (this.deselectOutputs === undefined) { this.deselectOutputs = false; }
            if (this.justSelect === undefined) { this.justSelect = false; }
            if (this.hideInputs === undefined) { this.hideInputs = false; }
            if (this.deleteInputs === undefined) { this.deleteInputs = false; }
            if (this.redundantOutputs === undefined) { this.redundantOutputs = false; }
            if (this.copyColors === undefined) { this.copyColors = false; }
            if (this.tools === undefined) { this.tools = null; }
            this.tools = tools;
            this.tool = tool;
            this.selectInputs = selectInputs;
            this.deleteInputs = deleteInputs;
            this.copyColors = copyColors;
            this.hideInputs = false;
            this.deselectOutputs = !selectOutputs;
            this.justSelect = !createOutputs;
            this.redundantOutputs = redundantOutputs;
        }

        /**
         * 
         * @return {string}
         */
        getXmlElementName(): string {
            if (this.redundantOutputs)return "ApplyTool"; else return "ToolApplied";
        }

        /**
         * 
         * @param {*} element
         */
        getXmlAttributes(element: org.w3c.dom.Element) {
            element.setAttribute("name", this.tool.getId());
            if (this.selectInputs)element.setAttribute("selectInputs", "true");
            if (this.deselectOutputs)element.setAttribute("deselectOutputs", "true");
            if (this.justSelect)element.setAttribute("justSelect", "true");
            if (this.hideInputs)element.setAttribute("hideInputs", "true");
            if (this.deleteInputs)element.setAttribute("deleteInputs", "true");
            element.setAttribute("copyColors", javaemul.internal.BooleanHelper.toString(this.copyColors));
        }

        /**
         * 
         * @param {*} element
         * @param {com.vzome.core.commands.XmlSaveFormat} format
         */
        setXmlAttributes(element: org.w3c.dom.Element, format: com.vzome.core.commands.XmlSaveFormat) {
            const toolName: string = element.getAttribute("name");
            this.tool = this.tools.get(toolName);
            this.selectInputs = this.isAttributeTrue(element, "selectInputs");
            this.deselectOutputs = this.isAttributeTrue(element, "deselectOutputs");
            this.justSelect = this.isAttributeTrue(element, "justSelect");
            this.hideInputs = this.isAttributeTrue(element, "hideInputs");
            this.deleteInputs = this.isAttributeTrue(element, "deleteInputs");
            const value: string = element.getAttribute("copyColors");
            this.copyColors = value == null || !(value === ("false"));
        }

        /*private*/ isAttributeTrue(element: org.w3c.dom.Element, name: string): boolean {
            const value: string = element.getAttribute(name);
            return value != null && (value === ("true"));
        }

        /**
         * 
         * @param {com.vzome.core.construction.Construction} c
         * @return {*}
         */
        public manifestConstruction(c: com.vzome.core.construction.Construction): com.vzome.core.model.Manifestation {
            let m: com.vzome.core.model.Manifestation = this.getManifestation(c);
            const preExistsNotHidden: boolean = (m != null && m.isRendered());
            if (this.justSelect){
                if (preExistsNotHidden)super.select$com_vzome_core_model_Manifestation$boolean(m, false);
            } else if (this.redundantOutputs || !preExistsNotHidden){
                m = super.manifestConstruction(c);
                if (!this.deselectOutputs)super.select$com_vzome_core_model_Manifestation$boolean(m, true);
            }
            return m;
        }

        public select(man?: any, ignoreGroups?: any) {
            if (((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || man === null) && ((typeof ignoreGroups === 'boolean') || ignoreGroups === null)) {
                super.select(man, ignoreGroups);
            } else if (((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || man === null) && ignoreGroups === undefined) {
                return <any>this.select$com_vzome_core_model_Manifestation(man);
            } else throw new Error('invalid overload');
        }

        public select$com_vzome_core_model_Manifestation(m: com.vzome.core.model.Manifestation) {
            if (this.tool.needsInput())throw new java.lang.UnsupportedOperationException("select is not supported within Tool.performEdit");
            if (!m.isRendered())super.showManifestation(m);
            super.select$com_vzome_core_model_Manifestation$boolean(m, true);
        }

        public unselect(man?: any, ignoreGroups?: any) {
            if (((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || man === null) && ((typeof ignoreGroups === 'boolean') || ignoreGroups === null)) {
                super.unselect(man, ignoreGroups);
            } else if (((man != null && (man.constructor != null && man.constructor["__interfaces"] != null && man.constructor["__interfaces"].indexOf("com.vzome.core.model.Manifestation") >= 0)) || man === null) && ignoreGroups === undefined) {
                return <any>this.unselect$com_vzome_core_model_Manifestation(man);
            } else throw new Error('invalid overload');
        }

        public unselect$com_vzome_core_model_Manifestation(man: com.vzome.core.model.Manifestation) {
            throw new java.lang.UnsupportedOperationException("unselect is not supported within Tool.performEdit");
        }

        /**
         * 
         * @param {*} m
         */
        showManifestation(m: com.vzome.core.model.Manifestation) {
            throw new java.lang.UnsupportedOperationException("showManifestation is not supported within Tool.performEdit");
        }

        /**
         * 
         * @param {*} m
         */
        hideManifestation(m: com.vzome.core.model.Manifestation) {
            throw new java.lang.UnsupportedOperationException("hideManifestation is not supported within Tool.performEdit");
        }
    }
    ApplyTool["__class"] = "com.vzome.core.editor.ApplyTool";

}

