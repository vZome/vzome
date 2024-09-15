/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor {
    export abstract class Tool extends com.vzome.core.editor.api.ChangeManifestations implements com.vzome.api.Tool {
        /*private*/ id: string;

        /*private*/ tools: com.vzome.core.editor.ToolsModel;

        parameters: java.util.List<com.vzome.core.construction.Construction>;

        /*private*/ category: string;

        /*private*/ predefined: boolean;

        /*private*/ hidden: boolean;

        /*private*/ label: string;

        /*private*/ selectInputs: boolean;

        /*private*/ deleteInputs: boolean;

        /*private*/ copyColors: boolean;

        /*private*/ pcs: java.beans.PropertyChangeSupport;

        public constructor(id: string, tools: com.vzome.core.editor.ToolsModel) {
            super(tools.getEditorModel());
            if (this.id === undefined) { this.id = null; }
            if (this.tools === undefined) { this.tools = null; }
            this.parameters = <any>(new java.util.ArrayList<any>());
            if (this.category === undefined) { this.category = null; }
            if (this.predefined === undefined) { this.predefined = false; }
            if (this.hidden === undefined) { this.hidden = false; }
            if (this.label === undefined) { this.label = null; }
            if (this.selectInputs === undefined) { this.selectInputs = false; }
            if (this.deleteInputs === undefined) { this.deleteInputs = false; }
            if (this.copyColors === undefined) { this.copyColors = false; }
            this.pcs = new java.beans.PropertyChangeSupport(this);
            this.tools = tools;
            this.id = id;
            this.selectInputs = true;
            this.deleteInputs = false;
            this.copyColors = true;
        }

        public isSelectInputs(): boolean {
            return this.selectInputs;
        }

        public isDeleteInputs(): boolean {
            return this.deleteInputs;
        }

        public isCopyColors(): boolean {
            return this.copyColors;
        }

        public setInputBehaviors(selectInputs: boolean, deleteInputs: boolean) {
            this.selectInputs = selectInputs;
            this.deleteInputs = deleteInputs;
        }

        public setCopyColors(value: boolean) {
            this.copyColors = value;
        }

        public addPropertyChangeListener(listener: java.beans.PropertyChangeListener) {
            this.pcs.addPropertyChangeListener$java_beans_PropertyChangeListener(listener);
        }

        public setCategory(category: string) {
            this.category = category;
        }

        addParameter(c: com.vzome.core.construction.Construction) {
            this.parameters.add(c);
        }

        public getParameters(): java.util.List<com.vzome.core.construction.Construction> {
            return this.parameters;
        }

        setPredefined(value: boolean) {
            this.predefined = value;
        }

        /**
         * 
         * @return {boolean}
         */
        public isNoOp(): boolean {
            return false;
        }

        /**
         * 
         * @return {boolean}
         */
        public isSticky(): boolean {
            return true;
        }

        /**
         * 
         */
        public redo() {
        }

        /**
         * 
         */
        public undo() {
        }

        /**
         * 
         */
        public perform() {
            const error: string = this.checkSelection(true);
            if (error != null)throw new com.vzome.core.commands.Command.Failure(error);
            this.tools.put(this.getId(), this);
        }

        /**
         * 
         * @param {*} element
         */
        getXmlAttributes(element: org.w3c.dom.Element) {
            element.setAttribute("name", this.id);
        }

        /**
         * Check the selection applicability for this tool, and possibly record the tool parameters.
         * @param {boolean} prepareTool is true when actually creating a tool, whether interactively or when loading a file.
         * It is false when just validating the selection.
         * @return
         * @return {string}
         */
        abstract checkSelection(prepareTool: boolean): string;

        abstract prepare(applyTool: com.vzome.core.editor.api.ChangeManifestations);

        abstract performEdit(c: com.vzome.core.construction.Construction, edit: com.vzome.core.editor.api.ChangeManifestations);

        abstract performSelect(man: com.vzome.core.model.Manifestation, applyTool: com.vzome.core.editor.api.ChangeManifestations);

        abstract complete(applyTool: com.vzome.core.editor.api.ChangeManifestations);

        abstract needsInput(): boolean;

        /**
         * 
         * @return {string}
         */
        public getId(): string {
            return this.id;
        }

        /**
         * 
         * @return {string}
         */
        public getCategory(): string {
            return this.category;
        }

        /**
         * 
         * @param {boolean} selectInputs
         * @param {boolean} deleteInputs
         * @param {boolean} createOutputs
         * @param {boolean} selectOutputs
         * @param {boolean} copyColors
         */
        public apply(selectInputs: boolean, deleteInputs: boolean, createOutputs: boolean, selectOutputs: boolean, copyColors: boolean) {
            this.tools.applyTool(this, selectInputs, deleteInputs, createOutputs, selectOutputs, copyColors);
        }

        /**
         * 
         */
        public selectParameters() {
            this.tools.selectToolParameters(this);
        }

        /**
         * 
         * @return {boolean}
         */
        public isPredefined(): boolean {
            return this.predefined;
        }

        /**
         * 
         * @return {string}
         */
        public getLabel(): string {
            return this.label;
        }

        /**
         * 
         * @param {string} label
         */
        public setLabel(label: string) {
            this.label = label;
        }

        /**
         * 
         * @return {boolean}
         */
        public isHidden(): boolean {
            return this.hidden;
        }

        /**
         * 
         * @param {boolean} hidden
         */
        public setHidden(hidden: boolean) {
            this.hidden = hidden;
            this.tools.hideTool(this);
        }
    }
    Tool["__class"] = "com.vzome.core.editor.Tool";
    Tool["__interfaces"] = ["com.vzome.api.Tool"];


}

