
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.core.editor;

import org.w3c.dom.Element;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.Construction;
import com.vzome.core.editor.Tool.Registry;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class SelectToolParameters extends ChangeManifestations
{
	private TransformationTool tool;
	private Registry registry;

	SelectToolParameters( Selection selection, RealizedModel model, Tool.Registry registry, TransformationTool tool )
	{
		super( selection, model );
		this .registry = registry;
		this .tool = tool;
	}

	@Override
	public void perform()
	{
        for ( Manifestation man : mSelection )
        	super .unselect( man, true );
        redo();  // get the unselects out of the way, if anything needs to be re-selected
        
        for ( Construction con : tool .getParameters() ) {
            Manifestation man = this .manifestConstruction( con );
            this .select( man );
        }
        redo();
    }

	@Override
	protected String getXmlElementName()
	{
		return "SelectToolParameters";
	}
    
    @Override
    protected void getXmlAttributes( Element element )
    {
        element .setAttribute( "name", this.tool .getName() );
    }

    @Override
    protected void setXmlAttributes( Element element, XmlSaveFormat format ) throws Failure
    {
        String toolName = element .getAttribute( "name" );
        this .tool = (TransformationTool) registry .getTool( toolName );
    }

}
