package com.vzome.desktop.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.vzome.api.Tool;

public class ToolFactoryController extends DefaultController implements PropertyChangeListener
{
	private final Tool.Factory factory;

	public ToolFactoryController( Tool.Factory factory )
	{
		this .factory = factory;
		factory .addListener( this );
	}

	@Override
	public void propertyChange( PropertyChangeEvent evt )
	{
		switch ( evt .getPropertyName() ) {

		case "enabled":
			// forward to the UI
			this .firePropertyChange( evt );
			break;

		default:
			break;
		}
	}

	@Override
	public String getProperty( String name )
	{
		switch ( name ) {

		case "title":
			return this .factory .getLabel();

		case "tooltip":
			return this .factory .getToolTip();

		case "enabled":
			return Boolean .toString( this .factory .isEnabled() );

		default:
			return super .getProperty( name );
		}
	}

	@Override
	public void doAction( String action ) throws Exception
	{
		switch ( action ) {

		case "createTool":
			factory .createTool();
			break;

		default:
			super .doAction( action );
		}
	}
}
