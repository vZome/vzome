/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.desktop.api {
    /**
     * Controller portion of model-view-controller architecture.
     * 
     * MVC principles in vZome:
     * 
     * - UI code can know other UI classes, preferably top-down only (no knowledge of parent context)
     * - UI code only knows this generic Controller interface
     * - UI code gets Controllers using Controller .getSubController()
     * - UI code cannot know of any specific Controller subclasses or any model classes
     * - Controller code cannot know any UI classes; ActionListeners let the controller trigger UI effects
     * - Controller code can know other Controller subclasses, and Model classes
     * - Model classes can only know other Model classes, preferably top-down only (no knowledge of parent context)
     * - Model classes can trigger PropertyChangeEvents, but usually the Controllers do it
     * 
     * @author vorth
     * @class
     */
    export interface Controller {
        setErrorChannel(errors: Controller.ErrorChannel);

        getCommandList(listName: string): string[];

        actionPerformed(source: any, action: string);

        getCommandListDefaultStates(string: string): boolean[];

        doFileAction(command: string, file: java.io.File);

        doScriptAction(command: string, script: string);

        getProperty(string: string): string;

        setProperty(cmd: string, value: any);

        propertyIsTrue(propName: string): boolean;

        userHasEntitlement(propName: string): boolean;

        addPropertyListener(listener: java.beans.PropertyChangeListener);

        removePropertyListener(listener: java.beans.PropertyChangeListener);

        getSubController(string: string): Controller;

        addSubController(name: string, sub: Controller);
    }

    export namespace Controller {

        export const USER_ERROR_CODE: string = "user.command.error";

        export const UNKNOWN_ERROR_CODE: string = "unknown.exception";

        export const UNKNOWN_ACTION: string = "unknown.action";

        export const UNKNOWN_PROPERTY: string = "unknown.property";
    }


    export namespace Controller {

        export interface ErrorChannel {
            reportError(errorCode: string, args: any[]);

            clearError();
        }
    }

}

