package com.vzome.core.editor;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.vzome.api.Tool.Factory;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.editor.api.UndoableEdit;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.RealizedModel;

public abstract class AbstractToolFactory implements Factory, SelectionSummary.Listener
{
	private boolean enabled = false;
	private final PropertyChangeSupport pcs;
	private ToolsModel tools;
	private final String label;
	private final String tooltip;
	private final String id;
	private final Symmetry symmetry;
	
	public AbstractToolFactory( ToolsModel tools, Symmetry symmetry, String id, String label, String tooltip )
	{
		this.tools = tools;
		this.symmetry = symmetry;
		this.id = id;
		this.label = label;
		this.tooltip = tooltip;
		this .pcs = new PropertyChangeSupport( this );
	}

	@Override
	public void selectionChanged( int total, int balls, int struts, int panels )
	{
		boolean wasEnabled = enabled;
		if ( countsAreValid( total, balls, struts, panels ) )
			enabled = bindParameters( getSelection() );
		else
			enabled = false;
		if ( wasEnabled != enabled )
			pcs .firePropertyChange( "enabled", wasEnabled, enabled );
	}

	public Symmetry getSymmetry()
	{
		return symmetry;
	}

	public String getId()
	{
		return id;
	}

	public String getLabel()
	{
		return this .label;
	}

	public String getToolTip()
	{
		return this .tooltip;
	}

	protected ToolsModel getToolsModel()
	{
		return this .tools;
	}

	protected EditorModel getEditorModel()
	{
		return this .tools .getEditorModel();
	}
	
	protected Selection getSelection()
	{
		return getEditorModel() .getSelection();
	}
	
	protected RealizedModel getModel()
	{
		return getEditorModel() .getRealizedModel();
	}
	
	@Override
	public boolean isEnabled()
	{
		return this .enabled;
	}

	public void addListener( PropertyChangeListener listener )
	{
		this .pcs .addPropertyChangeListener( listener );
	}
	
	private static final String NEW_PREFIX = "tool-";
	
	@Override
	public Tool createTool()
	{
		int index = this .tools .reserveId();
		Tool tool = createToolInternal( NEW_PREFIX + index );
		tool .setCategory( this .getId() );
		tool .setLabel( this .getId() + " " + index );
		if ( tool instanceof UndoableEdit )
			this .tools .getContext() .performAndRecord( (UndoableEdit) tool );
		else
	        this .tools .put( tool .getId(), tool );
		return tool;
	}

	public Tool createPredefinedTool( String label )
	{
		Tool tool = createToolInternal( this .getId() + ".builtin/" + label );
		tool .setLabel( label );
		tool .setCategory( this .getId() );
		tool .setPredefined( true );
		tool .checkSelection( true );
		this .tools .put( tool .getId(), tool );
		return tool;
	}

	public Tool deserializeTool( String id )
	{
		Tool tool = createToolInternal( id );
		if ( id .startsWith( NEW_PREFIX ) ) {
			int num = Integer .parseInt( id .substring( NEW_PREFIX.length() ) );
			this .tools .setMaxId( num );
		} // else
		   // legacy user tools don't consume NEW_PREFIX id space

        int nextDot = id .indexOf( "." );
        if ( nextDot > 0 ) {
            tool .setCategory( id .substring( 0, nextDot ) );
        } else {
            tool .setCategory( this .getId() );
        }

		// Reattach the label and input behaviors, already loaded separately by ToolsModel
		this .tools .setConfiguration( tool );
		return tool;
	}

	public abstract Tool createToolInternal( String id );

	protected abstract boolean countsAreValid( int total, int balls, int struts, int panels );
	
	protected abstract boolean bindParameters( Selection selection );
}
