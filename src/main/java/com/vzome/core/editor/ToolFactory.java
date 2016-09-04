package com.vzome.core.editor;

import java.beans.PropertyChangeListener;

public interface ToolFactory
{
	void addListener( PropertyChangeListener listener );

	Tool createTool( int index );

	boolean isEnabled();
}
