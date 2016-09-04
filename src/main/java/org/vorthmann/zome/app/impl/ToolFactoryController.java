package org.vorthmann.zome.app.impl;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.vorthmann.ui.DefaultController;

import com.vzome.core.editor.Tool;
import com.vzome.core.editor.ToolFactory;

public class ToolFactoryController extends DefaultController implements PropertyChangeListener
{
	private final ToolFactory factory;
	private int nextIndex = 0;
	private ToolsController toolsController;

	public ToolFactoryController( ToolFactory factory, ToolsController toolsController )
	{
		this .factory = factory;
		this.toolsController = toolsController;
		factory .addListener( this );
	}

	@Override
	public void propertyChange( PropertyChangeEvent evt )
	{
		switch ( evt .getPropertyName() ) {

		case "enabled":
			// forward to the UI
			this .properties() .firePropertyChange( evt );
			break;

		default:
			break;
		}
	}

	@Override
	public String getProperty( String name )
	{
		switch ( name ) {

		case "enabled":
			return Boolean .toString( this .factory .isEnabled() );

		default:
			return super .getProperty( name );
		}
	}

	@Override
	public void actionPerformed( ActionEvent e )
	{
		switch ( e .getActionCommand() ) {

		case "createTool":
			Tool tool = factory .createTool( ++nextIndex );
			toolsController .addTool( tool );
			break;

		default:
			super .actionPerformed( e );
		}
	}
}
