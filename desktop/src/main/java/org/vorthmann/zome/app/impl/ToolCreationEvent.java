
package org.vorthmann.zome.app.impl;

import java.awt.event.ActionEvent;

import com.vzome.core.math.symmetry.Symmetry;

public class ToolCreationEvent extends ActionEvent {

	private final ToolsController toolsController;
	private final String name;
	private final Symmetry symmetry;
	private final String group;

	public ToolCreationEvent( String name, String group, ToolsController toolsController, Symmetry symmetry )
	{
		super( toolsController, 0, "newTool" );
		this.name = name;
		this.group = group;
		this.toolsController = toolsController;
		this.symmetry = symmetry;
	}

	public ToolsController getToolsController()
	{
		return toolsController;
	}

	public String getName()
	{
		return name;
	}

	public Symmetry getSymmetry()
	{
		return symmetry;
	}

	public String getGroup()
	{
		return group;
	}

}
