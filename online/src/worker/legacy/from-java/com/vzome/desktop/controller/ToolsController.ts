import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Tool } from "../../api/Tool.js";
import { ToolsModel } from "../../core/editor/ToolsModel.js";
import { Controller } from "../api/Controller.js";
import { DefaultController } from "./DefaultController.js";
import { ToolController } from "./ToolController.js";
import { PropertyChangeEvent } from "../../../../java/beans/PropertyChangeEvent.js";
import { PropertyChangeListener } from "../../../../java/beans/PropertyChangeListener.js";
import { Properties } from "../../../../java/util/Properties.js";

export class ToolsController extends DefaultController implements PropertyChangeListener {
    /*private*/ tools: ToolsModel;

    public constructor(tools: ToolsModel) {
        super();
        if (this.tools === undefined) { this.tools = null; }
        this.tools = tools;
        tools.addPropertyListener(this);
    }

    /**
     * 
     * @param {string} action
     * @param {Properties} params
     */
    doParamAction(action: string, params: Properties) {
        switch((action)) {
        case "reorderTools":
        case "reorderBookmarks":
            const order: string = params.getProperty("order");
            const ids: string[] = (order == null || /* isEmpty */(order.length === 0)) ? [] : order.split(",");
            this.tools.reorderTools(action === ("reorderBookmarks"), ids);
            break;
        default:
            super.doParamAction(action, params);
        }
    }

    /**
     * 
     * @param {string} name
     * @return {*}
     */
    public getSubController(name: string): Controller {
        const tool: Tool = this.tools.get(name);
        if (tool != null){
            const controller: Controller = new ToolController(tool);
            this.addSubController(name, controller);
            return controller;
        }
        return null;
    }

    public addTool(tool: Tool) {
        const controller: Controller = new ToolController(tool);
        this.addSubController(tool.getId(), controller);
        this.firePropertyChange$java_beans_PropertyChangeEvent(new PropertyChangeEvent(this, "tool.added", null, controller));
    }

    /**
     * 
     * @param {PropertyChangeEvent} evt
     */
    public propertyChange(evt: PropertyChangeEvent) {
        switch((evt.getPropertyName())) {
        case "customTools":
            this.firePropertyChange$java_beans_PropertyChangeEvent(new PropertyChangeEvent(this, evt.getPropertyName(), null, evt.getNewValue()));
            this.firePropertyChange$java_beans_PropertyChangeEvent(new PropertyChangeEvent(this, "allCustomTools", null, this.tools.getAllCustomToolIDs(false)));
            break;
        case "customBookmarks":
            this.firePropertyChange$java_beans_PropertyChangeEvent(new PropertyChangeEvent(this, evt.getPropertyName(), null, evt.getNewValue()));
            this.firePropertyChange$java_beans_PropertyChangeEvent(new PropertyChangeEvent(this, "allCustomBookmarks", null, this.tools.getAllCustomToolIDs(true)));
            break;
        case "tool.instances":
            if (evt.getOldValue() == null){
                const tool: Tool = <Tool><any>evt.getNewValue();
                if (tool.isPredefined() || tool.isHidden())return;
                const controller: Controller = new ToolController(tool);
                this.firePropertyChange$java_beans_PropertyChangeEvent(new PropertyChangeEvent(this, "tool.added", null, controller));
            } else {
                const tool: Tool = <Tool><any>evt.getOldValue();
                this.firePropertyChange$java_beans_PropertyChangeEvent(new PropertyChangeEvent(this, "tool.hidden", tool.getId(), null));
            }
            break;
        default:
            break;
        }
    }

    /**
     * 
     * @param {string} listName
     * @return {java.lang.String[]}
     */
    public getCommandList(listName: string): string[] {
        switch((listName)) {
        case "customTools":
            return this.tools.getToolIDs(false);
        case "customBookmarks":
            return this.tools.getToolIDs(true);
        case "allCustomTools":
            return this.tools.getAllCustomToolIDs(false);
        case "allCustomBookmarks":
            return this.tools.getAllCustomToolIDs(true);
        default:
            break;
        }
        return super.getCommandList(listName);
    }
}
ToolsController["__class"] = "com.vzome.desktop.controller.ToolsController";
ToolsController["__interfaces"] = ["java.util.EventListener","java.beans.PropertyChangeListener","com.vzome.desktop.api.Controller"];
