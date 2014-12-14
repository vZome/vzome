
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.core.editor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.math.DomUtils;
import com.vzome.core.render.RenderedModel;

public class Snapshot implements UndoableEdit
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
	
    private int id;
    private Recorder recorder;
    private Context context;

    public void perform() throws Failure
    {
        this .recorder .recordSnapshot( this .id );
    }

    public Snapshot( int id, Recorder controller )
    {
        this .id = id;
        this .recorder = controller;
    }

    public void undo() {}

    public void redo() {}

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
        Element xml = doc .createElement( "Snapshot" );
        DomUtils .addAttribute( xml, "id", Integer .toString( this .id ) );
        return xml;
    }

    public void loadAndPerform( Element xml, XmlSaveFormat format,
            Context context ) throws Failure
    {
        this .id = Integer .parseInt( xml .getAttribute( "id" ) );

        context .performAndRecord( this );
    }

    public void setContext( Context context )
    {
        this .context = context;
    }

    public Context getContext()
    {
        return this .context;
    }

    public void releaseState()
    {
        this .recorder = null;
    }

    public boolean isSticky()
    {
        return true;
    }

}
