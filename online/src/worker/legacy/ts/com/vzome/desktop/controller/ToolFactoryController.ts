/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.desktop.controller {
    export class ToolFactoryController extends com.vzome.desktop.controller.DefaultController implements java.beans.PropertyChangeListener {
        /*private*/ factory: com.vzome.api.Tool.Factory;

        public constructor(factory: com.vzome.api.Tool.Factory) {
            super();
            if (this.factory === undefined) { this.factory = null; }
            this.factory = factory;
            factory.addListener(this);
        }

        /**
         * 
         * @param {java.beans.PropertyChangeEvent} evt
         */
        public propertyChange(evt: java.beans.PropertyChangeEvent) {
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


}

