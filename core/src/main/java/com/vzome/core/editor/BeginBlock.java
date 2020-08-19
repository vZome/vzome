
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;

/**
 * Just a marker in the history.
 * @author Scott Vorthmann
 *
 */
public class BeginBlock extends UndoableEdit
{
    public BeginBlock( EditorModel editor )
    {
        super();
    }

    @Override
    public boolean isNoOp()
    {
        return false;
    }

    @Override
	public Element getXml( Document doc )
    {
        return doc .createElement( "BeginBlock" );
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
    public void perform()
    {}

    @Override
    public boolean isSticky()
    {
        return false;
    }

    @Override
    public void configure( Map<String,Object> props ) {}
}
