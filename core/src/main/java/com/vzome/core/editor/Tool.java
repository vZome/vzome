
package com.vzome.core.editor;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.vzome.core.commands.Command;
import com.vzome.core.construction.Construction;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.model.Manifestation;

public abstract class Tool extends ChangeManifestations implements com.vzome.api.Tool
{
    private final String id;
    private final ToolsModel tools;
    protected final List<Construction> parameters = new ArrayList<>();
    private String category;

    private boolean predefined, hidden;
    private String label;
    private boolean selectInputs, deleteInputs, copyColors;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport( this );

    public Tool( String id, ToolsModel tools )
    {
        super( tools .getEditorModel() );
        this .tools = tools;
        this .id = id;
        this .selectInputs = true;
        this .deleteInputs = false;
        this .copyColors = true;
    }

    public boolean isSelectInputs()
    {
        return this.selectInputs;
    }
    
    public boolean isDeleteInputs()
    {
        return this.deleteInputs;
    }

    public boolean isCopyColors()
    {
        return this.copyColors;
    }

    public void setInputBehaviors( boolean selectInputs, boolean deleteInputs )
    {
        this.selectInputs = selectInputs;
        this.deleteInputs = deleteInputs;
    }

    public void setCopyColors( boolean value )
    {
        this .copyColors = value;
    }

    public void addPropertyChangeListener( PropertyChangeListener listener )
    {
        this .pcs .addPropertyChangeListener( listener );
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
    public boolean isNoOp()
    {
        return false;
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
        this .tools .put( this .getId(), this ); // will trigger ToolController creation
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

    protected abstract void prepare( ChangeManifestations applyTool );

    protected abstract void performEdit( Construction c, ChangeManifestations edit );

    protected abstract void performSelect( Manifestation man, ChangeManifestations applyTool );

    protected abstract void complete( ChangeManifestations applyTool );

    protected abstract boolean needsInput();


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
    public void apply( boolean selectInputs, boolean deleteInputs, boolean createOutputs, boolean selectOutputs, boolean copyColors )
    {
        this .tools .applyTool( this, selectInputs, deleteInputs, createOutputs, selectOutputs, copyColors );
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
    public boolean isHidden()
    {
        return hidden;
    }

    @Override
    public void setHidden( boolean hidden )
    {
        this.hidden = hidden;
        this .tools .hideTool( this );
    }
}
