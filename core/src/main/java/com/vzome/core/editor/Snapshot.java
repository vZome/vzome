
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.core.editor;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.editor.api.Context;
import com.vzome.core.editor.api.UndoableEdit;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.render.RenderedModel;
import com.vzome.xml.DomUtils;

public class Snapshot extends UndoableEdit
{
	public interface Recorder
	{
		void recordSnapshot( int id );
		
		void actOnSnapshot( int id, SnapshotAction action );
	}
	
	public interface SnapshotAction
	{
		void actOnSnapshot( RenderedModel snapshot );
	}

    @Override
    public boolean isNoOp()
    {
        return false;
    }

    private int id;
    private Recorder recorder;

    @Override
    public void perform() throws Failure
    {
        this .recorder .recordSnapshot( this .id );
    }

    public Snapshot( int id, Recorder controller )
    {
        this .id = id;
        this .recorder = controller;
    }

    @Override
    public void undo() {}

    @Override
    public void redo() {}

    @Override
    public void configure( Map<String,Object> props ) {}

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
        Element xml = doc .createElement( "Snapshot" );
        DomUtils .addAttribute( xml, "id", Integer .toString( this .id ) );
        return xml;
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
        this .id = Integer .parseInt( xml .getAttribute( "id" ) );

        context .performAndRecord( this );
    }

    @Override
    public boolean isSticky()
    {
        return true;
    }

}
