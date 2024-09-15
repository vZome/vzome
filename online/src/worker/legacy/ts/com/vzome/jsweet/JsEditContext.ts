/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.jsweet {
    export class JsEditContext implements com.vzome.core.editor.api.Context {
        public constructor() {
        }

        /**
         * 
         * @param {*} xml
         * @return {com.vzome.core.editor.api.UndoableEdit}
         */
        public createEdit(xml: org.w3c.dom.Element): com.vzome.core.editor.api.UndoableEdit {
            throw new java.lang.RuntimeException("unimplemented createEdit");
        }

        /**
         * 
         * @param {com.vzome.core.editor.api.UndoableEdit} edit
         */
        public performAndRecord(edit: com.vzome.core.editor.api.UndoableEdit) {
            throw new java.lang.RuntimeException("unimplemented performAndRecord");
        }

        /**
         * 
         * @param {string} cmdName
         * @return {*}
         */
        public createLegacyCommand(cmdName: string): com.vzome.core.commands.Command {
            throw new java.lang.RuntimeException("unimplemented createLegacyCommand");
        }

        /**
         * 
         * @param {string} action
         * @param {*} props
         * @return {boolean}
         */
        public doEdit(action: string, props: java.util.Map<string, any>): boolean {
            throw new java.lang.RuntimeException("unimplemented doEdit");
        }
    }
    JsEditContext["__class"] = "com.vzome.jsweet.JsEditContext";
    JsEditContext["__interfaces"] = ["com.vzome.core.editor.api.Context"];


}

