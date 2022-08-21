package com.vzome.core.tools;

import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;

public class InversionToolFactory extends AbstractToolFactory
{
	private transient Connector center;  // TODO use this in createToolInternal()
	
	public InversionToolFactory( ToolsModel tools )
	{
		super( tools, null, InversionTool.ID, InversionTool.LABEL, InversionTool.TOOLTIP );
	}

	@Override
	protected boolean countsAreValid( int total, int balls, int struts, int panels )
	{
		return ( total == 1 && balls == 1 );
	}

	@Override
	public Tool createToolInternal( String id )
	{
		return new InversionTool( id, getToolsModel() );
	}

    @Override
    public Tool createTool()
    {
        Tool result = super.createTool();
        result .setCopyColors( false ); // Overriding true default, only for newly created tools
        return result;
    }

	@Override
	protected boolean bindParameters( Selection selection )
	{
		assert selection .size() == 1;
    	for ( Manifestation man : selection )
    		center = (Connector) man;
		return true;
	}
}