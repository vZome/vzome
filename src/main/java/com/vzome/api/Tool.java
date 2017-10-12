package com.vzome.api;

import java.beans.PropertyChangeListener;
import java.util.EnumSet;

public interface Tool
{
	public enum Kind { SYMMETRY, TRANSFORM, LINEAR_MAP };
	
	public interface Factory
	{
		void addListener( PropertyChangeListener listener );

		Tool createTool();

		boolean isEnabled();

		String getToolTip();

		String getLabel();

		String getId();
	}

	public interface Source
	{
		Tool getPredefinedTool( String id );
	}

	public enum InputBehaviors { DELETE, SELECT };
	
	public enum OutputBehaviors { CREATE, SELECT };
	
	public void apply( EnumSet<InputBehaviors> inputAction, EnumSet<OutputBehaviors> outputAction );

	public void selectParameters();

	public boolean isPredefined();

	public String getId();

	public String getCategory();
	
	public String getLabel();

	public void setLabel( String label );

	public EnumSet<InputBehaviors> defaultInputBehaviors();
}
