package com.vzome.core.tools;

import com.vzome.core.editor.AbstractToolFactory;
import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolsModel;
import com.vzome.core.editor.api.Selection;

public class TranslationToolFactory extends AbstractToolFactory
{
	public TranslationToolFactory( ToolsModel tools )
	{
		super( tools, null, TranslationTool.ID, TranslationTool.LABEL, TranslationTool.TOOLTIP );
	}

	@Override
	protected boolean countsAreValid( int total, int balls, int struts, int panels )
	{
		return ( total == 2 && balls == 2 );
	}

	@Override
	public Tool createToolInternal( String id )
	{
		return new TranslationTool( id, getToolsModel() );
	}

	@Override
	protected boolean bindParameters( Selection selection )
	{
		return true;
	}
}