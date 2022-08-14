package com.vzome.core.tools;

import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.api.Selection;

public class BookmarkToolFactory extends AbstractToolFactory
{
    public BookmarkToolFactory( ToolsModel tools )
    {
        super( tools, null, BookmarkTool.ID, BookmarkTool.LABEL, BookmarkTool.TOOLTIP );
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