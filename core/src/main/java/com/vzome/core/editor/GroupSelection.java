
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.math.DomUtils;

public class GroupSelection implements UndoableEdit
{
    protected Selection mSelection;
    
    private boolean mGrouping = false;
    
    private boolean recursiveGroups = true; // 2.1.2 and later, 3.0b1 and later

    private final boolean alreadyGrouped;

    public GroupSelection( EditorModel editor )
    {
        super();
        this.mSelection = editor .getSelection();
        this.alreadyGrouped = this.mSelection .isSelectionAGroup();
    }

    @Override
    public void configure( Map<String,Object> props )
    {
        String mode = (String) props .get( "mode" );
        mGrouping = ( mode == null ) || mode .isEmpty() || mode .equals( "group" );
    }

    @Override
    public boolean isNoOp()
    {
        return this.mGrouping == this.alreadyGrouped;
    }

    @Override
    public Element getXml( Document doc )
    {
        Element elem = doc .createElement( "GroupSelection" );
        if ( ! mGrouping )
            DomUtils .addAttribute( elem, "grouping", "false" );
        return elem;
    }

    @Override
    public Element getDetailXml( Document doc )
    {
        return getXml( doc );
    }

    @Override
    public void loadAndPerform( Element xml, XmlSaveFormat format, Context context ) throws Failure
    {
        String grouping = xml .getAttribute( "grouping" );
        mGrouping = ( grouping == null ) || grouping .isEmpty() || grouping .equals( "true" );
        
        recursiveGroups = format .groupingRecursive();
        
        context .performAndRecord( this );
    }

    @Override
    public boolean isDestructive()
    {
        return true;
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }

    @Override
    public void redo()
    {
        if ( this .isNoOp() )
            return;

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

    @Override
    public void undo()
    {
        if ( this .isNoOp() )
            return;

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

    @Override
    public void perform()
    {
        redo();
    }

    @Override
    public boolean isSticky()
    {
        return false;
    }
}
