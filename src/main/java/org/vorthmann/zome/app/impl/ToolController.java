package org.vorthmann.zome.app.impl;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.EnumSet;

import org.vorthmann.ui.DefaultController;

import com.vzome.api.Tool;
import com.vzome.api.Tool.InputBehaviors;
import com.vzome.api.Tool.OutputBehaviors;

public class ToolController extends DefaultController
{
	private Tool tool;
	private boolean deleteInputs;
	private boolean selectInputs;
	private boolean selectOutputs;
	private boolean justSelect;

	public ToolController( Tool tool )
	{
		this .tool = tool;
		EnumSet<InputBehaviors> inputBehaviors = tool .defaultInputBehaviors();
		this .deleteInputs = inputBehaviors .contains( InputBehaviors.DELETE );
		this .selectInputs = inputBehaviors .contains( InputBehaviors.SELECT );
		this .selectOutputs = true;
		this .justSelect = false;
	}

	@Override
	public void actionPerformed( ActionEvent e )
	{
		switch ( e .getActionCommand() ) {

		case "apply":
			// TODO use the checkbox modes, override with key modifiers
			EnumSet<InputBehaviors> inputBehaviors = EnumSet.noneOf(InputBehaviors.class);
			if ( deleteInputs )
				inputBehaviors .add( InputBehaviors.DELETE );
			if ( selectInputs )
				inputBehaviors .add( InputBehaviors.SELECT );
			EnumSet<OutputBehaviors> outputBehaviors = EnumSet.noneOf(OutputBehaviors.class);
			if ( !justSelect )
				outputBehaviors .add( OutputBehaviors.CREATE );
			if ( selectOutputs )
				outputBehaviors .add( OutputBehaviors.SELECT );
			this .tool .apply( inputBehaviors, outputBehaviors );
			break;

		case "selectParams":
			this .tool .selectParameters();
			break;

		case "selectInputs":
			this .selectInputs = ! this .selectInputs;
			break;

		case "deleteInputs":
			this .deleteInputs = ! this .deleteInputs;
			if ( this .deleteInputs ) {
				this .selectInputs = false;
				this .properties() .firePropertyChange( "selectInputs", null, "false" );
			}
			this .properties() .firePropertyChange( "deleteInputs", null, Boolean .toString( this .deleteInputs ) );
			break;

		case "selectOutputs":
			this .selectOutputs = ! this .selectOutputs;
			break;

		case "createOutputs":
			this .justSelect = ! this .justSelect;
			if ( this .justSelect ) {
				this .selectOutputs = true;
				this .properties() .firePropertyChange( "selectOutputs", null, "true" );
			}
			this .properties() .firePropertyChange( "createOutputs", null, Boolean .toString( ! this .justSelect ) );
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
			return this .tool .getLabel();

		case "kind":
			return this .tool .getCategory();

		case "predefined":
			return Boolean .toString( this .tool .isPredefined() );

		case "selectInputs":
			return Boolean .toString( this .selectInputs );

		case "deleteInputs":
			return Boolean .toString( this .deleteInputs );

		case "selectOutputs":
			return Boolean .toString( this .selectOutputs );

		case "createOutputs":
			return Boolean .toString( ! this .justSelect );

		default:
			return super .getProperty(name);
		}
	}

	@Override
	public void setProperty( String name, Object value )
	{
		switch ( name ) {

		case "label":
			this .tool .setLabel( (String) value );
			this .properties() .firePropertyChange( "label", null, (String) value );
			return;

		default:
			super .setProperty( name, value );
		}
	}

	@Override
	public boolean[] enableContextualCommands( String[] menu, MouseEvent e )
	{
		boolean[] result = new boolean[ menu .length ];
		for (int i = 0; i < menu.length; i++) {
			switch ( menu[ i ] ) {

			case "selectInputs":
				result[ i ] = ! this .deleteInputs;
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
