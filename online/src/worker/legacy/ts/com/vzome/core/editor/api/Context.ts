/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor.api {
    export interface Context {
        createEdit(xml: org.w3c.dom.Element): com.vzome.core.editor.api.UndoableEdit;

        createLegacyCommand(cmdName: string): com.vzome.core.commands.Command;

        performAndRecord(edit: com.vzome.core.editor.api.UndoableEdit);

        doEdit(action: string, props: java.util.Map<string, any>): boolean;
    }
}

