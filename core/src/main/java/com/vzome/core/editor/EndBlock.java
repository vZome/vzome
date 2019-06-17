
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.Command.Failure;

/**
 * Just a marker in the history.
 * @author Scott Vorthmann
 *
 */
public class EndBlock implements UndoableEdit
{
    @Override
	public Element getXml( Document doc )
    {
        return doc .createElement( "EndBlock" );
    }

    @Override
    public Element getDetailXml( Document doc )
    {
        return getXml( doc );
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
    public void redo()
    {}

    @Override
    public void loadAndPerform( Element xml, XmlSaveFormat format, Context context ) throws Failure
    {
        context .performAndRecord( this );
    }

    @Override
    public void undo()
    {}

    @Override
    public void configure( Properties props ) {}

    @Override
    public void perform()
    {}

    @Override
    public boolean isSticky()
    {
        return false;
    }
}
