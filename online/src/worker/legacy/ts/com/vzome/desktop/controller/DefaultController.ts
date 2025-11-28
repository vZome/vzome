/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.desktop.controller {
    export class DefaultController implements com.vzome.desktop.api.Controller {
        /*private*/ pcs: java.beans.PropertyChangeSupport;

        mErrors: Controller.ErrorChannel;

        mNextController: DefaultController;

        /*private*/ subcontrollers: java.util.Map<string, com.vzome.desktop.api.Controller>;

        /*private*/ name: string;

        static logger: java.util.logging.Logger; public static logger_$LI$(): java.util.logging.Logger { if (DefaultController.logger == null) { DefaultController.logger = java.util.logging.Logger.getLogger("org.vorthmann.zome.controller"); }  return DefaultController.logger; }

        properggties(): java.beans.PropertyChangeSupport {
            return this.pcs;
        }

        /**
         * 
         * @param {*} source
         * @param {string} action
         */
        public actionPerformed(source: any, action: string) {
            try {
                if (DefaultController.logger_$LI$().isLoggable(java.util.logging.Level.FINE))DefaultController.logger_$LI$().fine("ACTION: " + this.getPath() + "||" + action);
                this.doAction(action);
            } catch(ex) {
                console.error(ex.message, ex);
                this.mErrors.reportError(com.vzome.desktop.api.Controller.UNKNOWN_ERROR_CODE, [ex, action]);
            }
        }

        /**
         * This is only overridden or called in Javascript code, in vZome Online.
         * @param {*} source
         * @param {string} action
         * @param {java.util.Properties} params
         */
        public paramActionPerformed(source: any, action: string, params: java.util.Properties) {
            try {
                if (DefaultController.logger_$LI$().isLoggable(java.util.logging.Level.FINE))DefaultController.logger_$LI$().fine("PARAM ACTION: " + this.getPath() + "||" + action);
                this.doParamAction(action, params);
            } catch(ex) {
                console.error(ex.message, ex);
                this.mErrors.reportError(com.vzome.desktop.api.Controller.UNKNOWN_ERROR_CODE, [ex]);
            }
        }

        /*private*/ getPath(): string {
            if (this.mNextController == null)return this.name; else return this.mNextController.getPath() + "::" + this.name;
        }

        /**
         * 
         * @param {*} listener
         */
        public addPropertyListener(listener: java.beans.PropertyChangeListener) {
            this.pcs.addPropertyChangeListener$java_beans_PropertyChangeListener(listener);
        }

        /**
         * 
         * @param {*} listener
         */
        public removePropertyListener(listener: java.beans.PropertyChangeListener) {
            this.pcs.removePropertyChangeListener$java_beans_PropertyChangeListener(listener);
        }

        doAction(action: string) {
            if (this.mNextController != null)this.mNextController.doAction(action); else this.mErrors.reportError(com.vzome.desktop.api.Controller.UNKNOWN_ACTION, [action]);
        }

        /**
         * This is only overridden or called in Javascript code, in vZome Online.
         * @param source
         * @param {string} action
         * @param {java.util.Properties} params
         */
        doParamAction(action: string, params: java.util.Properties) {
            if (this.mNextController != null)this.mNextController.doParamAction(action, params); else this.mErrors.reportError(com.vzome.desktop.api.Controller.UNKNOWN_ACTION, [action]);
        }

        /**
         * 
         * @param {string} command
         * @param {java.io.File} file
         */
        public doFileAction(command: string, file: java.io.File) {
            if (this.mNextController != null)this.mNextController.doFileAction(command, file); else this.mErrors.reportError(com.vzome.desktop.api.Controller.UNKNOWN_ACTION, [command]);
        }

        /**
         * 
         * @param {string} command
         * @param {string} script
         */
        public doScriptAction(command: string, script: string) {
            if (this.mNextController != null)this.mNextController.doScriptAction(command, script); else this.mErrors.reportError(com.vzome.desktop.api.Controller.UNKNOWN_ACTION, [command]);
        }

        /**
         * 
         * @param {string} listName
         * @return {java.lang.String[]}
         */
        public getCommandList(listName: string): string[] {
            if (this.mNextController != null)return this.mNextController.getCommandList(listName); else return [];
        }

        /**
         * 
         * @param {string} string
         * @return {boolean[]}
         */
        public getCommandListDefaultStates(string: string): boolean[] {
            return null;
        }

        /**
         * 
         * @param {string} string
         * @return {string}
         */
        public getProperty(string: string): string {
            if (this.mNextController != null)return this.mNextController.getProperty(string);
            return null;
        }

        /**
         * 
         * @param {string} propName
         * @return {boolean}
         */
        public propertyIsTrue(propName: string): boolean {
            return "true" === this.getProperty(propName);
        }

        /**
         * 
         * @param {string} name
         * @param {*} sub
         */
        public addSubController(name: string, sub: com.vzome.desktop.api.Controller) {
            (<DefaultController><any>sub).name = name;
            this.subcontrollers.put(name, sub);
            (<DefaultController><any>sub).setNextController(this, name);
        }

        /**
         * 
         * @param {string} name
         * @return {*}
         */
        public getSubController(name: string): com.vzome.desktop.api.Controller {
            const subc: com.vzome.desktop.api.Controller = this.subcontrollers.get(name);
            if (subc != null)return subc;
            if (this.mNextController != null)return this.mNextController.getSubController(name);
            return null;
        }

        /**
         * 
         * @param {*} errors
         */
        public setErrorChannel(errors: Controller.ErrorChannel) {
            this.mErrors = errors;
        }

        /**
         * 
         * @param {string} name
         * @param {*} value
         */
        public setProperty(name: string, value: any) {
            if (DefaultController.logger_$LI$().isLoggable(java.util.logging.Level.FINE))DefaultController.logger_$LI$().fine("SETPROP: " + this.getPath() + "||" + name + "=" + value);
            this.setModelProperty(name, value);
        }

        public setModelProperty(name: string, value: any) {
            if (this.mNextController != null)this.mNextController.setProperty(name, value);
        }

        /*private*/ setNextController(controller: com.vzome.desktop.api.Controller, name: string) {
            this.name = name;
            this.mNextController = <DefaultController><any>controller;
            this.mNextController.addPropertyListener(new DefaultController.DefaultController$0(this));
            if (this.mNextController != null && this.mNextController instanceof <any>com.vzome.desktop.controller.DefaultController)this.mErrors = this.mNextController.mErrors;
        }

        firePropertyChange$java_beans_PropertyChangeEvent(event: java.beans.PropertyChangeEvent) {
            if (DefaultController.logger_$LI$().isLoggable(java.util.logging.Level.FINE))DefaultController.logger_$LI$().fine("CHGEVENT: " + this.getPath() + "||" + event.getPropertyName() + "=" + event.getNewValue());
            this.pcs.firePropertyChange$java_beans_PropertyChangeEvent(event);
        }

        public firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object(propName: string, oldValue: any, newValue: any) {
            if (DefaultController.logger_$LI$().isLoggable(java.util.logging.Level.FINE))DefaultController.logger_$LI$().fine("CHGEVENT: " + this.getPath() + "||" + propName + "=" + newValue);
            this.pcs.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object(propName, oldValue, newValue);
        }

        public firePropertyChange(propName?: any, oldValue?: any, newValue?: any) {
            if (((typeof propName === 'string') || propName === null) && ((oldValue != null) || oldValue === null) && ((newValue != null) || newValue === null)) {
                return <any>this.firePropertyChange$java_lang_String$java_lang_Object$java_lang_Object(propName, oldValue, newValue);
            } else if (((propName != null && propName instanceof <any>java.beans.PropertyChangeEvent) || propName === null) && oldValue === undefined && newValue === undefined) {
                return <any>this.firePropertyChange$java_beans_PropertyChangeEvent(propName);
            } else throw new Error('invalid overload');
        }

        /**
         * 
         * @param {string} propName
         * @return {boolean}
         */
        public userHasEntitlement(propName: string): boolean {
            if (this.mNextController != null)return this.mNextController.userHasEntitlement(propName);
            return false;
        }

        openApplication(file: java.io.File) {
            if (this.mNextController != null)this.mNextController.openApplication(file);
        }

        runScript(script: string, file: java.io.File) {
            if (this.mNextController != null)this.mNextController.runScript(script, file);
        }

        constructor() {
            this.pcs = new java.beans.PropertyChangeSupport(this);
            if (this.mErrors === undefined) { this.mErrors = null; }
            if (this.mNextController === undefined) { this.mNextController = null; }
            this.subcontrollers = <any>(new java.util.HashMap<any, any>());
            this.name = "";
        }
    }
    DefaultController["__class"] = "com.vzome.desktop.controller.DefaultController";
    DefaultController["__interfaces"] = ["com.vzome.desktop.api.Controller"];



    export namespace DefaultController {

        export class DefaultController$0 implements java.beans.PropertyChangeListener {
            public __parent: any;
            /**
             * 
             * @param {java.beans.PropertyChangeEvent} event
             */
            public propertyChange(event: java.beans.PropertyChangeEvent) {
                this.__parent.pcs.firePropertyChange$java_beans_PropertyChangeEvent(event);
            }

            constructor(__parent: any) {
                this.__parent = __parent;
            }
        }
        DefaultController$0["__interfaces"] = ["java.util.EventListener","java.beans.PropertyChangeListener"];


    }

}

