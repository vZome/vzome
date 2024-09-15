/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.desktop.controller {
    export class ToolsController extends com.vzome.desktop.controller.DefaultController implements java.beans.PropertyChangeListener {
        /*private*/ tools: com.vzome.core.editor.ToolsModel;

        public constructor(tools: com.vzome.core.editor.ToolsModel) {
            super();
            if (this.tools === undefined) { this.tools = null; }
            this.tools = tools;
            tools.addPropertyListener(this);
        }

        /**
         * 
         * @param {string} name
         * @return {*}
         */
        public getSubController(name: string): com.vzome.desktop.api.Controller {
            const tool: com.vzome.api.Tool = this.tools.get(name);
            if (tool != null){
                const controller: com.vzome.desktop.api.Controller = new com.vzome.desktop.controller.ToolController(tool);
                this.addSubController(name, controller);
                return controller;
            }
            return null;
        }

        public addTool(tool: com.vzome.api.Tool) {
            const controller: com.vzome.desktop.api.Controller = new com.vzome.desktop.controller.ToolController(tool);
            this.addSubController(tool.getId(), controller);
            this.firePropertyChange$java_beans_PropertyChangeEvent(new java.beans.PropertyChangeEvent(this, "tool.added", null, controller));
        }

        /**
         * 
         * @param {java.beans.PropertyChangeEvent} evt
         */
        public propertyChange(evt: java.beans.PropertyChangeEvent) {
            switch((evt.getPropertyName())) {
            case "customTools":
            case "customBookmarks":
                this.firePropertyChange$java_beans_PropertyChangeEvent(new java.beans.PropertyChangeEvent(this, evt.getPropertyName(), null, evt.getNewValue()));
                break;
            case "tool.instances":
                if (evt.getOldValue() == null){
                    const tool: com.vzome.api.Tool = <com.vzome.api.Tool><any>evt.getNewValue();
                    if (tool.isPredefined() || tool.isHidden())return;
                    const controller: com.vzome.desktop.api.Controller = new com.vzome.desktop.controller.ToolController(tool);
                    this.firePropertyChange$java_beans_PropertyChangeEvent(new java.beans.PropertyChangeEvent(this, "tool.added", null, controller));
                } else {
                    const tool: com.vzome.api.Tool = <com.vzome.api.Tool><any>evt.getOldValue();
                    this.firePropertyChange$java_beans_PropertyChangeEvent(new java.beans.PropertyChangeEvent(this, "tool.hidden", tool.getId(), null));
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
            default:
                break;
            }
            return super.getCommandList(listName);
        }
    }
    ToolsController["__class"] = "com.vzome.desktop.controller.ToolsController";
    ToolsController["__interfaces"] = ["java.util.EventListener","java.beans.PropertyChangeListener","com.vzome.desktop.api.Controller"];


}

