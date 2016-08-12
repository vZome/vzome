package org.vorthmann.zome.app.impl;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import org.vorthmann.ui.DefaultController;

import com.vzome.core.editor.DocumentModel;
import com.vzome.core.editor.Tool;

public class ToolController extends DefaultController
{
	private Tool tool;
	private DocumentModel applier;  // TODO this should be a ToolsModel
	private final String kind;
	private final String label;

	public ToolController( Tool tool, DocumentModel applier )
	{
		this .tool = tool;
		this .applier = applier;

		String idAndName = tool .getName(); // will be "kind.N/label"
		int delim = idAndName .indexOf( "." );
		this .kind = idAndName .substring( 0, delim );
		delim = idAndName .indexOf( "/" );
		this .label = idAndName .substring( delim + 1 );
	}

	@Override
	public void actionPerformed( ActionEvent e )
	{
		switch ( e .getActionCommand() ) {

		case "apply":
			this .applier .applyTool( tool, this .applier, e .getModifiers() );
			break;

		case "selectParams":
//			this .applier .selectToolParameters( tool );
			break;

		case "help":
			break;

		default:
			super .actionPerformed( e );
		}
	}

	@Override
	public String getProperty( String name )
	{
		switch ( name ) {

		case "label":
			return this .label;

		case "kind":
			return this .kind;

		default:
			return super .getProperty(name);
		}
	}

	@Override
	public boolean[] enableContextualCommands( String[] menu, MouseEvent e )
	{
		boolean[] result = new boolean[ menu .length ];
		for (int i = 0; i < result.length; i++) {
			result[ i ] = true;
		}
		return result;
	}
}
