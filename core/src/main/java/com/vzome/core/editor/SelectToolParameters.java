
package com.vzome.core.editor;

import org.w3c.dom.Element;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.Construction;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.model.Manifestation;

public class SelectToolParameters extends ChangeManifestations
{
	private Tool tool;
	private ToolsModel tools;

	SelectToolParameters( ToolsModel tools, Tool tool )
	{
		super( tools .getEditorModel() );
		this.tools = tools;
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
        element .setAttribute( "name", this.tool .getId() );
    }

    @Override
    protected void setXmlAttributes( Element element, XmlSaveFormat format ) throws Failure
    {
        String toolName = element .getAttribute( "name" );
        this .tool = tools .get( toolName );
    }

}
