import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Tool as ApiTool } from "../../api/Tool.js";
import { SelectionSummary } from "./SelectionSummary.js";
import { Tool as CoreTool } from "./Tool.js";
import { ToolsModel } from "./ToolsModel.js";
import { EditorModel } from "./api/EditorModel.js";
import { Selection } from "./api/Selection.js";
import { UndoableEdit } from "./api/UndoableEdit.js";
import { Symmetry } from "../math/symmetry/Symmetry.js";
import { RealizedModel } from "../model/RealizedModel.js";
import { PropertyChangeListener } from "../../../../java/beans/PropertyChangeListener.js";
import { PropertyChangeSupport } from "../../../../java/beans/PropertyChangeSupport.js";

export abstract class AbstractToolFactory implements ApiTool.Factory, SelectionSummary.Listener {
    /*private*/ enabled: boolean;

    /*private*/ pcs: PropertyChangeSupport;

    /*private*/ tools: ToolsModel;

    /*private*/ label: string;

    /*private*/ tooltip: string;

    /*private*/ id: string;

    /*private*/ symmetry: Symmetry;

    public constructor(tools: ToolsModel, symmetry: Symmetry, id: string, label: string, tooltip: string) {
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
        this.pcs = new PropertyChangeSupport(this);
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

    public getSymmetry(): Symmetry {
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

    getToolsModel(): ToolsModel {
        return this.tools;
    }

    getEditorModel(): EditorModel {
        return this.tools.getEditorModel();
    }

    getSelection(): Selection {
        return this.getEditorModel().getSelection();
    }

    getModel(): RealizedModel {
        return this.getEditorModel().getRealizedModel();
    }

    /**
     * 
     * @return {boolean}
     */
    public isEnabled(): boolean {
        return this.enabled;
    }

    public addListener(listener: PropertyChangeListener) {
        this.pcs.addPropertyChangeListener$java_beans_PropertyChangeListener(listener);
    }

    static NEW_PREFIX: string = "tool-";

    /**
     * 
     * @return {CoreTool}
     */
    public createTool(): CoreTool {
        const index: number = this.tools.reserveId();
        const tool: CoreTool = this.createToolInternal(AbstractToolFactory.NEW_PREFIX + index);
        tool.setCategory(this.getId());
        tool.setLabel(this.getId() + " " + index);
        if (tool != null && tool instanceof <any>UndoableEdit)this.tools.getContext().performAndRecord(<UndoableEdit>tool); else this.tools.put(tool.getId(), tool);
        return tool;
    }

    public createPredefinedTool(label: string): CoreTool {
        const tool: CoreTool = this.createToolInternal(this.getId() + ".builtin/" + label);
        tool.setLabel(label);
        tool.setCategory(this.getId());
        tool.setPredefined(true);
        tool.checkSelection(true);
        this.tools.put(tool.getId(), tool);
        return tool;
    }

    public deserializeTool(id: string): CoreTool {
        const tool: CoreTool = this.createToolInternal(id);
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

    public abstract createToolInternal(id: string): CoreTool;

    abstract countsAreValid(total: number, balls: number, struts: number, panels: number): boolean;

    abstract bindParameters(selection: Selection): boolean;
}
AbstractToolFactory["__class"] = "com.vzome.core.editor.AbstractToolFactory";
AbstractToolFactory["__interfaces"] = ["com.vzome.core.editor.SelectionSummary.Listener","com.vzome.api.Tool.Factory"];
