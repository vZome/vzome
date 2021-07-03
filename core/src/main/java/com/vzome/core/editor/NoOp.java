
package com.vzome.core.editor;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.editor.api.Context;
import com.vzome.core.editor.api.UndoableEdit;
import com.vzome.core.commands.XmlSaveFormat;

public class NoOp extends UndoableEdit
{
    @Override
    public void perform() throws Failure
    {
        throw new IllegalStateException();
    }

    @Override
    public boolean isNoOp()
    {
        return true;
    }

    @Override
    public void undo()
    {
        throw new IllegalStateException();
    }

    @Override
    public void configure( Map<String,Object> props ) {}

    @Override
    public void redo() throws Failure
    {
        throw new IllegalStateException();
    }

    @Override
    public boolean isVisible()
    {
        return false;
    }

    @Override
    public boolean isDestructive()
    {
        return false;
    }

    @Override
    public Element getXml( Document doc )
    {
        throw new IllegalStateException();
    }

    @Override
    public Element getDetailXml( Document doc )
    {
        return getXml( doc );
    }

    @Override
    public void loadAndPerform( Element xml, XmlSaveFormat format,
            Context context ) throws Failure
    {
        throw new IllegalStateException();
    }

    @Override
    public boolean isSticky()
    {
        return false;
    }
}
