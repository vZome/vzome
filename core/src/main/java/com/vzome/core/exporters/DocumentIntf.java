package com.vzome.core.exporters;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.CameraIntf;
import com.vzome.core.viewing.Lights;

public interface DocumentIntf
{
    CameraIntf getCameraModel();

    Lights getSceneLighting();

    RenderedModel getRenderedModel();

    ToolsModel getToolsModel();

    Element getDetailsXml( Document dom, boolean b );

    EditorModel getEditorModel();
}
