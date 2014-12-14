
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.math.DomUtils;

public class GroupSelection implements UndoableEdit
{
    protected Selection mSelection;
    
    private boolean mGrouping;
    
    private boolean recursiveGroups; // 2.1.2 and later, 3.0b1 and later

	private Context context;

    public GroupSelection( Selection selection, boolean groupThem )
    {
        mSelection = selection;        
        mGrouping = groupThem;
        recursiveGroups = true;
    }

    public Element getXml( Document doc )
    {
        Element elem = doc .createElement( "GroupSelection" );
        if ( ! mGrouping )
            DomUtils .addAttribute( elem, "grouping", "false" );
        return elem;
    }

    public void loadAndPerform( Element xml, XmlSaveFormat format, Context context ) throws Failure
    {
        String grouping = xml .getAttribute( "grouping" );
        mGrouping = ( grouping == null ) || grouping .isEmpty() || grouping .equals( "true" );
        
        recursiveGroups = format .groupingRecursive();
        
        context .performAndRecord( this );
    }

    public boolean isDestructive()
    {
        return true;
    }

    public boolean isVisible()
    {
        return true;
    }

    public void redo()
    {
        if ( mGrouping )
            if ( recursiveGroups )
                mSelection .gatherGroup();
            else
                mSelection .gatherGroup211();
        else
            if ( recursiveGroups )
                mSelection .scatterGroup();
            else
                mSelection .scatterGroup211();
    }

    public void undo()
    {
        if ( !mGrouping )
            if ( recursiveGroups )
                mSelection .gatherGroup();
            else
                mSelection .gatherGroup211();
        else
            if ( recursiveGroups )
                mSelection .scatterGroup();
            else
                mSelection .scatterGroup211();
    }

    public void perform()
    {
        redo();
    }

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
        mSelection = null;
    }

    public boolean isSticky()
    {
        return false;
    }
}
