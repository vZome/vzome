
//(c) Copyright 2010, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.w3c.dom.Element;

import com.vzome.core.commands.Command;
import com.vzome.core.construction.Construction;
import com.vzome.core.model.Manifestation;

public abstract class Tool extends ChangeManifestations implements com.vzome.api.Tool
{
	private final String id;
	private final ToolsModel tools;
    protected final List<Construction> parameters = new ArrayList<>();
	private String category;

	private boolean predefined;
	private String label;

	public Tool( String id, ToolsModel tools )
	{
		super( tools .getEditorModel() .getSelection(), tools .getEditorModel() .getRealizedModel(), false );
		this .tools = tools;
		this .id = id;
	}

	public void setCategory( String category )
	{
		this.category = category;
	}

	protected void addParameter( Construction c )
    {
    	this .parameters .add( c );
	}

	List<Construction> getParameters()
	{
		return this .parameters;
	}
	
	void setPredefined( boolean value )
	{
		this .predefined = value;
	}
	
    @Override
	public boolean isSticky()
    {
        return true;
    }

    @Override
    public void redo()
    {
        // this edit is sticky (not really undoable)
    }

    @Override
    public void undo()
    {
        // this edit is sticky (not really undoable)
    }

    @Override
    public void perform() throws Command.Failure
    {
    	String error = checkSelection( true );
    	if ( error != null )
    		// the old way of creating tools, validating the selection after the user action
    		throw new Command.Failure( error );
	}

    @Override
    protected void getXmlAttributes( Element element )
    {
        element .setAttribute( "name", this.id );
    }

    /**
     * Check the selection applicability for this tool, and possibly record the tool parameters.
     * @param prepareTool is true when actually creating a tool, whether interactively or when loading a file.
     * It is false when just validating the selection.
     * @return
     */
    protected abstract String checkSelection( boolean prepareTool );

	abstract void prepare( ChangeManifestations applyTool );
    
	abstract void performEdit( Construction c, ChangeManifestations edit );

	abstract void performSelect( Manifestation man, ChangeManifestations applyTool );

	abstract void complete( ChangeManifestations applyTool );
    
	abstract boolean needsInput();

	
	//=================  Here begins the set of Tool interface implementations  =================================
	
	@Override
	public String getId()
	{
		return this .id;
	}

	@Override
	public String getCategory()
	{
		return this .category;
	}

	@Override
	public void apply( EnumSet<InputBehaviors> inputAction, EnumSet<OutputBehaviors> outputAction )
	{
		this .tools .applyTool( this, inputAction, outputAction );
	}

	@Override
	public void selectParameters()
	{
		this .tools .selectToolParameters( this );
	}

	@Override
	public boolean isPredefined()
	{
		return this .predefined;
	}

	@Override
	public String getLabel()
	{
		return this .label;
	}

	@Override
	public void setLabel( String label )
	{
		this .label = label;
	}

	@Override
	public EnumSet<InputBehaviors> defaultInputBehaviors()
	{
		return EnumSet.of( InputBehaviors.SELECT );
	}
}
