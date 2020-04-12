package org.vorthmann.zome.app.impl;

import java.awt.event.MouseEvent;
import java.util.EnumSet;

import org.vorthmann.ui.DefaultController;

import com.vzome.api.Tool;
import com.vzome.api.Tool.InputBehaviors;
import com.vzome.api.Tool.OutputBehaviors;

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
        EnumSet<InputBehaviors> inputBehaviors = this .tool .getInputBehaviors();
		switch ( action ) {

		case "apply":
			// TODO use the checkbox modes, override with key modifiers
			EnumSet<OutputBehaviors> outputBehaviors = EnumSet.noneOf(OutputBehaviors.class);
			if ( !justSelect )
				outputBehaviors .add( OutputBehaviors.CREATE );
			if ( selectOutputs )
				outputBehaviors .add( OutputBehaviors.SELECT );
			this .tool .apply( inputBehaviors, outputBehaviors );
			break;

        case "hideTool":
            this .tool .setHidden( true );
            break;

        case "selectParams":
            this .tool .selectParameters();
            break;

		case "selectInputs":
			if ( inputBehaviors .contains( InputBehaviors.SELECT ) )
			    inputBehaviors .remove( InputBehaviors.SELECT );
			else
			    inputBehaviors .add( InputBehaviors.SELECT );
			this .tool .setInputBehaviors( inputBehaviors );
			break;

		case "deleteInputs":
		    boolean deleteInputs;
            if ( inputBehaviors .contains( InputBehaviors.DELETE ) ) {
                inputBehaviors .remove( InputBehaviors.DELETE );
                deleteInputs = false;
            }
            else {
                inputBehaviors .add( InputBehaviors.DELETE );
                inputBehaviors .remove( InputBehaviors.SELECT );
                deleteInputs = true;
            }
            this .tool .setInputBehaviors( inputBehaviors );
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
			return Boolean .toString( this .tool .getInputBehaviors() .contains( InputBehaviors.SELECT ) );

		case "deleteInputs":
			return Boolean .toString( this .tool .getInputBehaviors() .contains( InputBehaviors.DELETE ) );

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
				result[ i ] = ! this .tool .getInputBehaviors() .contains( InputBehaviors.DELETE );
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
