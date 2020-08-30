
//(c) Copyright 2010, Scott Vorthmann.

package com.vzome.core.editor;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.w3c.dom.Element;

import com.vzome.api.Tool.InputBehaviors;
import com.vzome.api.Tool.OutputBehaviors;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.Construction;
import com.vzome.core.model.Manifestation;

public class ApplyTool extends ChangeManifestations
{
    @Override
    public void perform() throws Failure
    {
        // first, handle the inputs, offering each to the tool (if it needs input).
        //  If the tool does not need input, it operates just on its parameters and
        //  the entire realized model, but it still may add to or replace the current
        //  selection.
        List<Manifestation> inputs = new ArrayList<>();
        for (Manifestation man : mSelection) {
            if ( deleteInputs && tool .needsInput() )
            {
                super .unselect( man, true );
                super .deleteManifestation( man );
            }
            else if ( hideInputs && tool .needsInput() )
            {
                super .unselect( man, true );
                super .hideManifestation( man );
            }
            else if ( ! selectInputs )
                super .unselect( man, true );
            if ( tool .needsInput() )
                inputs .add( man );
        }

        redo();  // get the unselects out of the way, if anything needs to be re-selected

        tool. prepare( this );

        // now, any manifested constructions are outputs
        if ( tool .needsInput() )
        {
            for (Manifestation man : inputs) {
                Construction c = man .toConstruction();
                c .setColor( man .getColor() );

                tool .performEdit( c, this );
            }
        }
        else
        {
            for (Manifestation man : mManifestations) {
                tool .performSelect( man, this );
            }
        }
        tool. complete( this );

        redo();

        super.perform();
    }

    private Tool tool;

    private boolean selectInputs, deselectOutputs, justSelect, hideInputs, deleteInputs, redundantOutputs;

    private final ToolsModel tools;

    public ApplyTool( ToolsModel tools, Tool tool, EnumSet<InputBehaviors> inputAction, EnumSet<OutputBehaviors> outputAction, boolean redundantOutputs )
    {
        super( tools .getEditorModel() .getSelection(), tools .getEditorModel() .getRealizedModel() );
        this.tools = tools;

        this .tool = tool;
        selectInputs = inputAction .contains( InputBehaviors.SELECT );
        deleteInputs = inputAction .contains( InputBehaviors.DELETE );
        hideInputs = false;
        deselectOutputs = ! outputAction .contains( OutputBehaviors.SELECT );
        justSelect = ! outputAction .contains( OutputBehaviors.CREATE );
        this .redundantOutputs = redundantOutputs;
    }

    @Override
    protected String getXmlElementName()
    {
        if ( this .redundantOutputs ) 
            return "ApplyTool"; 
        else
            return "ToolApplied";
    }

    @Override
    protected void getXmlAttributes( Element element )
    {
        element .setAttribute( "name", this .tool .getId() );
        if ( selectInputs )
            element .setAttribute( "selectInputs", "true" );
        if ( deselectOutputs )
            element .setAttribute( "deselectOutputs", "true" );
        if ( justSelect )
            element .setAttribute( "justSelect", "true" );
        if ( hideInputs )
            element .setAttribute( "hideInputs", "true" );
        if ( deleteInputs )
            element .setAttribute( "deleteInputs", "true" );
    }

    @Override
    protected void setXmlAttributes( Element element, XmlSaveFormat format ) throws Failure
    {
        String toolName = element .getAttribute( "name" );
        this .tool = this .tools .get( toolName );
        this .selectInputs = isAttributeTrue( element, "selectInputs" );
        this .deselectOutputs = isAttributeTrue( element, "deselectOutputs" );
        this .justSelect = isAttributeTrue( element, "justSelect" );
        this .hideInputs = isAttributeTrue( element, "hideInputs" );
        this .deleteInputs = isAttributeTrue( element, "deleteInputs" );
    }

    private boolean isAttributeTrue( Element element, String name )
    {
        String value = element .getAttribute( name );
        return value != null && value .equals( "true" );
    }

    @Override
    public Manifestation manifestConstruction( Construction c )
    {
        Manifestation m = getManifestation( c );
        boolean preExistsNotHidden = ( m != null && m .isRendered() );
        if ( justSelect )
        {
            // Pre-existing manifestation, NOT considered an output.
            //  This avoids group overlap.
            if ( preExistsNotHidden )
                super .select( m, false /* == do use groups! */ );
        }
        else if ( redundantOutputs || !preExistsNotHidden )
        {
            // no pre-existing manifestation
            m = super .manifestConstruction( c );
            if ( ! deselectOutputs )
                super .select( m, true /* safe to ignore groups, they won't exist */ );
        }
        return m;
    }

    /**
     * This will be called on any manifestation that should be (shown and) selected in the output.
     */
    @Override
    public void select( Manifestation m )
    {
        if ( this .tool .needsInput() )
            throw new UnsupportedOperationException( "select is not supported within Tool.performEdit" );

        if ( ! m .isRendered() )
            super .showManifestation( m );
        super .select( m, true /* safe to ignore groups, they won't exist */ );
    }

    @Override
    public void unselect( Manifestation man )
    {
        throw new UnsupportedOperationException( "unselect is not supported within Tool.performEdit" );
    }

    @Override
    protected void showManifestation( Manifestation m )
    {
        throw new UnsupportedOperationException( "showManifestation is not supported within Tool.performEdit" );
    }

    @Override
    protected void hideManifestation( Manifestation m )
    {
        throw new UnsupportedOperationException( "hideManifestation is not supported within Tool.performEdit" );
    }
}
