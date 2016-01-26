
//(c) Copyright 2010, Scott Vorthmann.

package com.vzome.core.editor;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;

import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.Construction;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

public class ApplyTool extends ChangeManifestations
{
    public void perform() throws Failure
    {
        registry .useTool( tool );
        
        // first, handle the inputs, offering each to the tool (if it needs input).
        //  If the tool does not need input, it operates just on its parameters and
        //  the entire realized model, but it still may add to or replace the current
        //  selection.
        List<Manifestation> inputs = new ArrayList<>();
        for ( Iterator<Manifestation> mans = mSelection .iterator(); mans .hasNext(); ) {
            Manifestation man = mans .next();
            if ( hideInputs && tool .needsInput() )
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
        	for ( Iterator<Manifestation> mans = inputs .iterator(); mans .hasNext(); ) {
        		Manifestation man = mans .next();
        		Construction c = man .getConstructions() .next();

        		tool .performEdit( c, this );
        	}
        }
        else
        {
        	for ( Iterator<Manifestation> mans = mManifestations .iterator(); mans .hasNext(); ) {
        		Manifestation man = mans .next();
        		tool .performSelect( man, this );
        	}
        }
    	tool. complete( this );

    	redo();
                                
        super.perform();
    }

    private Tool tool;
    
    private Tool.Registry registry;
    
    private boolean selectInputs, deselectOutputs, justSelect, hideInputs, redundantOutputs;

//    public ApplyTool( Selection selection, RealizedModel realized, ToolEvent event )
//    {
//    	this( selection, realized, event .getTool(), event .getRegistry(), event .getModes(), true );
//    }
    
    public ApplyTool( Selection selection, RealizedModel realized, Tool.Registry registry, boolean redundantOutputs )
    {
        this( selection, realized, null, registry, 0, redundantOutputs );
    }
    
    public ApplyTool( Selection selection, RealizedModel realized, Tool tool, Tool.Registry registry, int modes, boolean redundantOutputs )
    {
        super( selection, realized, false );
        
        this.tool = tool;
        this.registry = registry;
        selectInputs = ( modes & ActionEvent.SHIFT_MASK ) != 0;
        deselectOutputs = ( modes & ActionEvent.ALT_MASK ) != 0;
        justSelect = ( modes & ActionEvent.META_MASK ) != 0;
        hideInputs = ( modes & ActionEvent.CTRL_MASK ) != 0;
        this .redundantOutputs = redundantOutputs;
    }

    protected String getXmlElementName()
    {
    	if ( this .redundantOutputs ) 
    		return "ApplyTool"; 
    	else
    		return "ToolApplied";
    }
    
    protected void getXmlAttributes( Element element )
    {
        element .setAttribute( "name", this.tool .getName() );
        if ( selectInputs )
            element .setAttribute( "selectInputs", "true" );
        if ( deselectOutputs )
            element .setAttribute( "deselectOutputs", "true" );
        if ( justSelect )
            element .setAttribute( "justSelect", "true" );
        if ( hideInputs )
            element .setAttribute( "hideInputs", "true" );
    }

    protected void setXmlAttributes( Element element, XmlSaveFormat format ) throws Failure
    {
        String toolName = element .getAttribute( "name" );
        this .tool = registry .getTool( toolName );
        this .selectInputs = isAttributeTrue( element, "selectInputs" );
        this .deselectOutputs = isAttributeTrue( element, "deselectOutputs" );
        this .justSelect = isAttributeTrue( element, "justSelect" );
        this .hideInputs = isAttributeTrue( element, "hideInputs" );
    }
    
    private boolean isAttributeTrue( Element element, String name )
    {
        String value = element .getAttribute( name );
        return value != null && value .equals( "true" );
    }
    
    public Manifestation manifestConstruction( Construction c )
    {
    	Manifestation m = getManifestation( c );
        boolean preExistsNotHidden = ( m != null && m .getRenderedObject() != null );
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
    public void select( Manifestation m )
    {
    	if ( this .tool .needsInput() )
    		throw new UnsupportedOperationException( "select is not supported within Tool.performEdit" );

        if ( m .getRenderedObject() == null )
        	super .showManifestation( m );
        super .select( m, true /* safe to ignore groups, they won't exist */ );
    }
    
    public void unselect( Manifestation man )
    {
    	throw new UnsupportedOperationException( "unselect is not supported within Tool.performEdit" );
    }

    protected void showManifestation( Manifestation m )
    {
    	throw new UnsupportedOperationException( "showManifestation is not supported within Tool.performEdit" );
    }
    
    protected void hideManifestation( Manifestation m )
    {
    	throw new UnsupportedOperationException( "hideManifestation is not supported within Tool.performEdit" );
    }
}
