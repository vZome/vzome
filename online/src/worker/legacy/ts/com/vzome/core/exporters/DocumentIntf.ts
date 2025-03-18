/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.exporters {
    export interface DocumentIntf {
        getCameraModel(): com.vzome.core.viewing.CameraIntf;

        getSceneLighting(): com.vzome.core.viewing.Lights;

        getRenderedModel(): com.vzome.core.render.RenderedModel;

        getToolsModel(): com.vzome.core.editor.ToolsModel;

        getDetailsXml(dom: org.w3c.dom.Document, b: boolean): org.w3c.dom.Element;

        getEditorModel(): com.vzome.core.editor.api.EditorModel;
    }
}

