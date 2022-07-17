
package com.vzome.core.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.Construction;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.model.Manifestation;

public class ApplyTool extends ChangeManifestations
{
    private static final Logger logger = Logger .getLogger( "com.vzome.core.editor.ApplyTool" );

    @Override
    public void perform() throws Failure
    {
        if ( logger .isLoggable( Level .FINE ) )
            logger .fine( "performing ApplyTool " + tool .getId() + " :: " + tool .getCategory() );

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
                if ( logger .isLoggable( Level .FINEST ) )
                    logger .finest( "ApplyTool - unselect and delete " + man .toString() );
            }
            else if ( hideInputs && tool .needsInput() )
            {
                super .unselect( man, true );
                super .hideManifestation( man );
                if ( logger .isLoggable( Level .FINEST ) )
                    logger .finest( "ApplyTool - unselect and hide " + man .toString() );
            }
            else if ( ! selectInputs ) {
                super .unselect( man, true );
                if ( logger .isLoggable( Level .FINEST ) )
                    logger .finest( "ApplyTool - unselect " + man .toString() );
            }
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
                c .setColor( copyColors? man .getColor() : null );

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

    private boolean selectInputs, deselectOutputs, justSelect, hideInputs, deleteInputs, redundantOutputs, copyColors;

    private final ToolsModel tools;

    public ApplyTool( ToolsModel tools, Tool tool, boolean selectInputs, boolean deleteInputs, boolean createOutputs, boolean selectOutputs, boolean redundantOutputs, boolean copyColors )
    {
        super( tools .getEditorModel() );
        this.tools = tools;

        this .tool = tool;
        this .selectInputs = selectInputs;
        this .deleteInputs = deleteInputs;
		this .copyColors = copyColors;
        this .hideInputs = false;
        this .deselectOutputs = ! selectOutputs;
        this .justSelect = ! createOutputs;
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
        
        // Let's be explicit for new configurations, true or false
        element .setAttribute( "copyColors", Boolean.toString( this.copyColors ) );
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
        
        String value = element .getAttribute( "copyColors" );
        this .copyColors = value == null || ! value .equals( "false" );
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
