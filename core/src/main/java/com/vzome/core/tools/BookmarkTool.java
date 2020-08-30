
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.tools;

import java.util.ArrayList;
import java.util.List;

import com.vzome.core.commands.Command;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.ChangeManifestations;
import com.vzome.core.editor.Duplicator;
import com.vzome.core.editor.Selection;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.model.Manifestation;

public class BookmarkTool extends Tool
{    
    private static final String ID = "bookmark";
    private static final String LABEL = "Create a selection bookmark";
    private static final String TOOLTIP = "<p>" +
            "A selection bookmark lets you re-create<br>any selection at a later time." +
            "</p>";

    public static class Factory extends AbstractToolFactory
    {
        public Factory( ToolsModel tools )
        {
            super( tools, null, ID, LABEL, TOOLTIP );
        }

        @Override
        protected boolean countsAreValid( int total, int balls, int struts, int panels )
        {
            return ( total > 0 );
        }

        @Override
        public Tool createToolInternal( String id )
        {
            return new BookmarkTool( id, this .getToolsModel() );
        }

        @Override
        protected boolean bindParameters( Selection selection )
        {
            return true;
        }
    }

    private final List<Construction> bookmarkedConstructions = new ArrayList<>();

    public BookmarkTool( String id, ToolsModel tools )
    {
        super( id, tools );
    }

    @Override
    public boolean isSticky()
    {
        return true;
    }

    @Override
    public void perform() throws Command.Failure
    {
        Duplicator duper = new Duplicator( null, null );
        if ( mSelection .size() == 0 )
            bookmarkedConstructions .add( new FreePoint( this .mManifestations .getField() .origin( 3 ) ) );
        else
            for (Manifestation man : mSelection) {
                Construction result = duper .duplicateConstruction( man );
                bookmarkedConstructions .add( result );
                this .addParameter( result );
            }
        super .perform();
    }

    @Override
    public boolean needsInput()
    {
        return false;
    }

    @Override
    public void prepare( ChangeManifestations edit )
    {
        if ( this .bookmarkedConstructions .isEmpty() ) {
            edit .manifestConstruction( new FreePoint( this .mManifestations .getField() .origin( 3 ) ) );
        }
        else
            for (Construction con : bookmarkedConstructions) {
                edit .manifestConstruction( con );
            }
        edit .redo();
    }

    @Override
    public void complete( ChangeManifestations applyTool ) {}

    @Override
    public void performEdit( Construction c, ChangeManifestations applyTool ) {}

    @Override
    public void performSelect( Manifestation man, ChangeManifestations applyTool ) {}

    @Override
    public void redo()
    {
        // this edit is now sticky (not really undoable)
    }

    @Override
    public void undo()
    {
        // this edit is now sticky (not really undoable)
    }

    @Override
    protected String getXmlElementName()
    {
        return "BookmarkTool";
    }

    @Override
    public String getCategory()
    {
        return ID;
    }

    @Override
    protected String checkSelection( boolean prepareTool )
    {
        return null;
    }
}
