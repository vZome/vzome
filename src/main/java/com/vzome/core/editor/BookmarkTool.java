
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.vzome.core.commands.Command;
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

	@Override
	public boolean equals( Object that )
	{
		if (this == that) {
			return true;
		}
		if (that == null) {
			return false;
		}
		if (getClass() != that.getClass()) {
			return false;
		}
		BookmarkTool other = (BookmarkTool) that;
		if (bookmarkedConstructions == null) {
			if (other.bookmarkedConstructions != null) {
				return false;
			}
		} else if (!bookmarkedConstructions
				.equals(other.bookmarkedConstructions)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isSticky()
    {
        return true;
    }

    @Override
    public void perform() throws Command.Failure
    {
        defineTool();
    }

    protected void defineTool()
    {
		tools .addTool( this );
    }

    @Override
    public boolean needsInput()
    {
    	return false;
    }

    @Override
    public void prepare( ChangeManifestations edit )
    {
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
    public String getName()
    {
        return name;
    }

    @Override
    protected String getXmlElementName()
    {
        return "BookmarkTool";
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
        return "bookmark";
    }

    public String getDefaultName()
    {
        return "SHOULD NOT HAPPEN";
    }

	@Override
	public boolean isValidForSelection() {
		// TODO Auto-generated method stub
		return false;
	}
}
