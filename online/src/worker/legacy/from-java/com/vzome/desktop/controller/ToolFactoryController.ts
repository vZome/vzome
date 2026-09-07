import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Tool } from "../../api/Tool.js";
import { DefaultController } from "./DefaultController.js";
import { PropertyChangeEvent } from "../../../../java/beans/PropertyChangeEvent.js";
import { PropertyChangeListener } from "../../../../java/beans/PropertyChangeListener.js";

export class ToolFactoryController extends DefaultController implements PropertyChangeListener {
    /*private*/ factory: Tool.Factory;

    public constructor(factory: Tool.Factory) {
        super();
        if (this.factory === undefined) { this.factory = null; }
        this.factory = factory;
        factory.addListener(this);
    }

    /**
     * 
     * @param {PropertyChangeEvent} evt
     */
    public propertyChange(evt: PropertyChangeEvent) {
        switch((evt.getPropertyName())) {
        case "enabled":
            this.firePropertyChange$java_beans_PropertyChangeEvent(evt);
            break;
        default:
            break;
        }
    }

    /**
     * 
     * @param {string} name
     * @return {string}
     */
    public getProperty(name: string): string {
        switch((name)) {
        case "title":
            return this.factory.getLabel();
        case "tooltip":
            return this.factory.getToolTip();
        case "enabled":
            return javaemul.internal.BooleanHelper.toString(this.factory.isEnabled());
        default:
            return super.getProperty(name);
        }
    }

    /**
     * 
     * @param {string} action
     */
    public doAction(action: string) {
        switch((action)) {
        case "createTool":
            this.factory.createTool();
            break;
        default:
            super.doAction(action);
        }
    }
}
ToolFactoryController["__class"] = "com.vzome.desktop.controller.ToolFactoryController";
ToolFactoryController["__interfaces"] = ["java.util.EventListener","java.beans.PropertyChangeListener","com.vzome.desktop.api.Controller"];
