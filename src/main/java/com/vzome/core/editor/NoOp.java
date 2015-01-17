
//(c) Copyright 2010, Scott Vorthmann.

package com.vzome.core.editor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.Command.Failure;

public class NoOp implements UndoableEdit
{
    public void perform() throws Failure
    {
        throw new IllegalStateException();
    }

    public void undo()
    {
        throw new IllegalStateException();
    }

    public void redo() throws Failure
    {
        throw new IllegalStateException();
    }

    public boolean isVisible()
    {
        return false;
    }

    public boolean isDestructive()
    {
        return false;
    }

    public Element getXml( Document doc )
    {
        throw new IllegalStateException();
    }

    @Override
    public Element getDetailXml( Document doc )
    {
        return getXml( doc );
    }

    public void loadAndPerform( Element xml, XmlSaveFormat format,
            Context context ) throws Failure
    {
        throw new IllegalStateException();
    }

    public void setContext( Context context )
    {
        throw new IllegalStateException();
    }

    public Context getContext()
    {
        throw new IllegalStateException();
    }

    public void releaseState()
    {}

    public boolean isSticky()
    {
        return false;
    }
}
