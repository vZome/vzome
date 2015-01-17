
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.Command.Failure;

/**
 * Just a marker in the history.
 * @author Scott Vorthmann
 *
 */
public class BeginBlock implements UndoableEdit
{
    private Context context;

	public Element getXml( Document doc )
    {
        return doc .createElement( "BeginBlock" );
    }

    @Override
    public Element getDetailXml( Document doc )
    {
        return getXml( doc );
    }

    public boolean isVisible()
    {
    	return false;
    }

    public boolean isDestructive()
    {
        return false;
    }

    public void redo()
    {}

    public void loadAndPerform( Element xml, XmlSaveFormat format, Context context ) throws Failure
    {
        context .performAndRecord( this );
    }

    public void undo()
    {}

    public void perform()
    {}

	public Context getContext()
	{
		return this .context;
	}

	public void setContext( Context context )
	{
		this .context = context;
	}

    public void releaseState()
    {
        setContext( null );
    }

    public boolean isSticky()
    {
        return false;
    }
}
