package org.vorthmann.zome.app.impl;

import java.awt.event.MouseEvent;

import org.vorthmann.ui.DefaultController;

import com.vzome.api.Tool;

public class ToolController extends DefaultController
{
	private Tool tool;
	private boolean selectOutputs;
	private boolean justSelect;

	public ToolController( Tool tool )
	{
		this .tool = tool;
		this .selectOutputs = true;
		this .justSelect = false;
	}

	@Override
	public void doAction( String action ) throws Exception
	{
        boolean selectInputs = this .tool .isSelectInputs();
        boolean deleteInputs = this .tool .isDeleteInputs();
		switch ( action ) {

		case "apply":
			// TODO use the checkbox modes, override with key modifiers
			boolean createOutputs = ! justSelect;
			this .tool .apply( selectInputs, deleteInputs, createOutputs, selectOutputs );
			break;

        case "hideTool":
            this .tool .setHidden( true );
            break;

        case "selectParams":
            this .tool .selectParameters();
            break;

		case "selectInputs":
			this .tool .setInputBehaviors( !selectInputs, deleteInputs );
			break;

		case "deleteInputs":
		    deleteInputs = ! deleteInputs;
            this .tool .setInputBehaviors( selectInputs && !deleteInputs, deleteInputs );
			if ( deleteInputs ) {
                this .firePropertyChange( "selectInputs", null, "false" );
			}
			this .firePropertyChange( "deleteInputs", null, Boolean .toString( deleteInputs ) );
			break;

		case "selectOutputs":
			this .selectOutputs = ! this .selectOutputs;
			break;

		case "createOutputs":
			this .justSelect = ! this .justSelect;
			if ( this .justSelect ) {
				this .selectOutputs = true;
				this .firePropertyChange( "selectOutputs", null, "true" );
			}
			this .firePropertyChange( "createOutputs", null, Boolean .toString( ! this .justSelect ) );
			break;

		default:
			super .doAction( action );
		}
	}

	@Override
	public String getProperty( String name )
	{
		switch ( name ) {

        case "id":
            return this .tool .getId();

        case "label":
            return this .tool .getLabel();

		case "kind":
			return this .tool .getCategory();

		case "predefined":
			return Boolean .toString( this .tool .isPredefined() );

		case "selectInputs":
			return Boolean .toString( this .tool .isSelectInputs() );

		case "deleteInputs":
			return Boolean .toString( this .tool .isDeleteInputs() );

		case "selectOutputs":
			return Boolean .toString( this .selectOutputs );

		case "createOutputs":
			return Boolean .toString( ! this .justSelect );

		default:
			return super .getProperty(name);
		}
	}

	@Override
	public void setModelProperty( String name, Object value )
	{
		switch ( name ) {

		case "label":
			this .tool .setLabel( (String) value );
			this .firePropertyChange( "label", null, (String) value );
			return;

		default:
			super .setModelProperty( name, value );
		}
	}

	@Override
	public boolean[] enableContextualCommands( String[] menu, MouseEvent e )
	{
		boolean[] result = new boolean[ menu .length ];
		for (int i = 0; i < menu.length; i++) {
			switch ( menu[ i ] ) {

			case "selectInputs":
				result[ i ] = ! this .tool .isDeleteInputs();
				break;

			case "selectOutputs":
				result[ i ] = ! this .justSelect;
				break;

			default:
				result[ i ] = true;
			}
		}
		return result;
	}
}
