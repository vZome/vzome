
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class ModuleTool extends ChangeManifestations implements Tool
{
    private String name;
    
    private final List<Manifestation> bookmarkedSelection = new ArrayList<>();
        
    private Tool.Registry tools;

    public ModuleTool( String name, Selection selection, RealizedModel realized, Tool.Registry tools )
    {
        super( selection, realized, false );
        this.name = name;
        this.tools = tools;
        mSelection .copy( bookmarkedSelection );
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
		ModuleTool other = (ModuleTool) that;
		if (bookmarkedSelection == null) {
			if (other.bookmarkedSelection != null) {
				return false;
			}
		} else if (!bookmarkedSelection.equals(other.bookmarkedSelection)) {
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
    public String getName()
    {
        return name;
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
        return "module";
    }

    public String getDefaultName()
    {
        return "module";
    }

	@Override
	public boolean isValidForSelection() {
		// TODO Auto-generated method stub
		return false;
	}
}
