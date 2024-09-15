/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor {
    export abstract class AbstractToolFactory implements com.vzome.api.Tool.Factory, com.vzome.core.editor.SelectionSummary.Listener {
        /*private*/ enabled: boolean;

        /*private*/ pcs: java.beans.PropertyChangeSupport;

        /*private*/ tools: com.vzome.core.editor.ToolsModel;

        /*private*/ label: string;

        /*private*/ tooltip: string;

        /*private*/ id: string;

        /*private*/ symmetry: com.vzome.core.math.symmetry.Symmetry;

        public constructor(tools: com.vzome.core.editor.ToolsModel, symmetry: com.vzome.core.math.symmetry.Symmetry, id: string, label: string, tooltip: string) {
            this.enabled = false;
            if (this.pcs === undefined) { this.pcs = null; }
            if (this.tools === undefined) { this.tools = null; }
            if (this.label === undefined) { this.label = null; }
            if (this.tooltip === undefined) { this.tooltip = null; }
            if (this.id === undefined) { this.id = null; }
            if (this.symmetry === undefined) { this.symmetry = null; }
            this.tools = tools;
            this.symmetry = symmetry;
            this.id = id;
            this.label = label;
            this.tooltip = tooltip;
            this.pcs = new java.beans.PropertyChangeSupport(this);
        }

        /**
         * 
         * @param {number} total
         * @param {number} balls
         * @param {number} struts
         * @param {number} panels
         */
        public selectionChanged(total: number, balls: number, struts: number, panels: number) {
            const wasEnabled: boolean = this.enabled;
            if (this.countsAreValid(total, balls, struts, panels))this.enabled = this.bindParameters(this.getSelection()); else this.enabled = false;
            if (wasEnabled !== this.enabled)this.pcs.firePropertyChange$java_lang_String$boolean$boolean("enabled", wasEnabled, this.enabled);
        }

        public getSymmetry(): com.vzome.core.math.symmetry.Symmetry {
            return this.symmetry;
        }

        public getId(): string {
            return this.id;
        }

        public getLabel(): string {
            return this.label;
        }

        public getToolTip(): string {
            return this.tooltip;
        }

        getToolsModel(): com.vzome.core.editor.ToolsModel {
            return this.tools;
        }

        getEditorModel(): com.vzome.core.editor.api.EditorModel {
            return this.tools.getEditorModel();
        }

        getSelection(): com.vzome.core.editor.api.Selection {
            return this.getEditorModel().getSelection();
        }

        getModel(): com.vzome.core.model.RealizedModel {
            return this.getEditorModel().getRealizedModel();
        }

        /**
         * 
         * @return {boolean}
         */
        public isEnabled(): boolean {
            return this.enabled;
        }

        public addListener(listener: java.beans.PropertyChangeListener) {
            this.pcs.addPropertyChangeListener$java_beans_PropertyChangeListener(listener);
        }

        static NEW_PREFIX: string = "tool-";

        /**
         * 
         * @return {com.vzome.core.editor.Tool}
         */
        public createTool(): com.vzome.core.editor.Tool {
            const index: number = this.tools.reserveId();
            const tool: com.vzome.core.editor.Tool = this.createToolInternal(AbstractToolFactory.NEW_PREFIX + index);
            tool.setCategory(this.getId());
            tool.setLabel(this.getId() + " " + index);
            if (tool != null && tool instanceof <any>com.vzome.core.editor.api.UndoableEdit)this.tools.getContext().performAndRecord(<com.vzome.core.editor.api.UndoableEdit>tool); else this.tools.put(tool.getId(), tool);
            return tool;
        }

        public createPredefinedTool(label: string): com.vzome.core.editor.Tool {
            const tool: com.vzome.core.editor.Tool = this.createToolInternal(this.getId() + ".builtin/" + label);
            tool.setLabel(label);
            tool.setCategory(this.getId());
            tool.setPredefined(true);
            tool.checkSelection(true);
            this.tools.put(tool.getId(), tool);
            return tool;
        }

        public deserializeTool(id: string): com.vzome.core.editor.Tool {
            const tool: com.vzome.core.editor.Tool = this.createToolInternal(id);
            if (/* startsWith */((str, searchString, position = 0) => str.substr(position, searchString.length) === searchString)(id, AbstractToolFactory.NEW_PREFIX)){
                const num: number = javaemul.internal.IntegerHelper.parseInt(id.substring(AbstractToolFactory.NEW_PREFIX.length));
                this.tools.setMaxId(num);
            }
            const nextDot: number = id.indexOf(".");
            if (nextDot > 0){
                tool.setCategory(id.substring(0, nextDot));
            } else {
                tool.setCategory(this.getId());
            }
            this.tools.setConfiguration(tool);
            return tool;
        }

        public abstract createToolInternal(id: string): com.vzome.core.editor.Tool;

        abstract countsAreValid(total: number, balls: number, struts: number, panels: number): boolean;

        abstract bindParameters(selection: com.vzome.core.editor.api.Selection): boolean;
    }
    AbstractToolFactory["__class"] = "com.vzome.core.editor.AbstractToolFactory";
    AbstractToolFactory["__interfaces"] = ["com.vzome.core.editor.SelectionSummary.Listener","com.vzome.api.Tool.Factory"];


}

