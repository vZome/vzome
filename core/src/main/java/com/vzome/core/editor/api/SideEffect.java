package com.vzome.core.editor.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface SideEffect
{
    public void undo();
    
    public Element getXml( Document doc );

    public void redo();
}