
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.Construction;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class BookmarkTool extends ChangeManifestations implements Tool
{
    private String name;
    
    private final List<Construction> bookmarkedConstructions = new ArrayList<>();
        
    private Tool.Registry tools;
    
    public BookmarkTool( String name, Selection selection, RealizedModel realized, Tool.Registry tools )
    {
        super( selection, realized, false );
        this.name = name;
        this.tools = tools;
        Duplicator duper = new Duplicator( null, null );
        for (Manifestation man : mSelection) {
            Construction result = duper .duplicateConstruction( man );
            bookmarkedConstructions .add( result );
        }
    }

	public boolean isSticky()
    {
        return true;
    }

    public void perform() throws Failure
    {
        defineTool();
    }

    protected void defineTool()
    {
        tools .addTool( this );
    }

    public boolean needsInput()
    {
    	return false;
    }

    public void prepare( ChangeManifestations edit )
    {
        for (Construction con : bookmarkedConstructions) {
            edit .manifestConstruction( con );
        }
        edit .redo();
    }

	public void complete( ChangeManifestations applyTool ) {}

    public void performEdit( Construction c, ChangeManifestations applyTool ) {}
    
	public void performSelect( Manifestation man, ChangeManifestations applyTool ) {}

    public void redo()
    {
        // TODO manifest a symmetry construction... that is why this class extends ChangeConstructions
        // this edit is now sticky (not really undoable)
//        tools .addTool( this );
    }

    public void undo()
    {
        // this edit is now sticky (not really undoable)
//        tools .removeTool( this );
    }

    public String getName()
    {
        return name;
    }

    protected String getXmlElementName()
    {
        return "BookmarkTool";
    }
    
    protected void getXmlAttributes( Element element )
    {
        element .setAttribute( "name", this.name );
    }

    protected void setXmlAttributes( Element element, XmlSaveFormat format ) throws Failure
    {
        this.name = element .getAttribute( "name" );
    }

    public String getCategory()
    {
        return "bookmark";
    }

    public String getDefaultName()
    {
        return "SHOULD NOT HAPPEN";
    }
}
