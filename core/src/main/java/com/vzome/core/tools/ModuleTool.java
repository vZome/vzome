
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.tools;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.Duplicator;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.model.Manifestation;

public class ModuleTool extends Tool
{
	private static final String ID = "module";
	private static final String LABEL = "Create a module tool";
	private static final String TOOLTIP = "<p>" +
    		"Each tool duplicates the original module.<br>" +
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
			return ( total > 0);
		}

		@Override
		public Tool createToolInternal( String id )
		{
			return new ModuleTool( id, getToolsModel() );
		}

		@Override
		protected boolean bindParameters( Selection selection )
		{
			return true;
		}
	}

    private String name;
    
    private final List<Manifestation> bookmarkedSelection = new ArrayList<>();
        
    public ModuleTool( String id, ToolsModel tools )
    {
        super( id, tools );
        mSelection .copy( bookmarkedSelection );
    }

	@Override
	public boolean isSticky()
    {
        return true;
    }

    @Override
    public void prepare( ChangeManifestations applyTool ) {}

    @Override
	public void complete( ChangeManifestations applyTool ) {}

    @Override
    public boolean needsInput()
    {
    	return true;
    }

    @Override
    public void performEdit( Construction c, ChangeManifestations applyTool )
    {
        if ( ! ( c instanceof Point ) )
            return;
        Point p = (Point) c;
        AlgebraicVector loc = p .getLocation();
        Duplicator duper = new Duplicator( applyTool, loc );
        for (Manifestation man : bookmarkedSelection) {
            duper .duplicateManifestation( man );
        }
        applyTool .redo();
    }

    @Override
	public void performSelect( Manifestation man, ChangeManifestations applyTool ) {};

    @Override
    public void redo()
    {
        // TODO manifest a symmetry construction... that is why this class extends ChangeConstructions
        // this edit is now sticky (not really undoable)
//        tools .addTool( this );
    }

    @Override
    public void undo()
    {
        // this edit is now sticky (not really undoable)
//        tools .removeTool( this );
    }

    @Override
    protected String getXmlElementName()
    {
        return "ModuleTool";
    }
    
    @Override
    protected void getXmlAttributes( Element element )
    {
        element .setAttribute( "name", this.name );
    }

    @Override
    protected void setXmlAttributes( Element element, XmlSaveFormat format ) throws Command.Failure
    {
        this.name = element .getAttribute( "name" );
    }

    @Override
    public String getCategory()
    {
        return ID;
    }

	@Override
	protected String checkSelection( boolean prepareTool )
	{
		// TODO Auto-generated method stub
		return null;
	}
}
