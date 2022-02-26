package com.vzome.api;

import java.beans.PropertyChangeListener;

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
		
	public void apply( boolean selectInputs, boolean deleteInputs, boolean createOutputs, boolean selectOutputs, boolean copyColors );

	public void selectParameters();

	public boolean isPredefined();

	public String getId();

	public String getCategory();
	
	public String getLabel();

	public void setLabel( String label );
	
	public boolean isSelectInputs();
	
	public boolean isDeleteInputs();

    public boolean isCopyColors();

    public void setInputBehaviors( boolean selectInputs, boolean deleteInputs );

    public void setCopyColors( boolean value );

    boolean isHidden();

    void setHidden( boolean hidden );
}
