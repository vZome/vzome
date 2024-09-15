/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.editor.api {
    export interface SideEffect {
        undo();

        getXml(doc: org.w3c.dom.Document): org.w3c.dom.Element;

        redo();
    }
}

