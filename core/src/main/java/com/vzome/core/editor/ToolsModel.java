package com.vzome.core.editor;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vzome.core.construction.Point;
import com.vzome.core.editor.api.Context;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.editor.api.UndoableEdit;
import com.vzome.xml.DomUtils;

@SuppressWarnings("serial")
public class ToolsModel extends TreeMap<String, Tool> implements Tool.Source
{
	private EditorModel editor;
	private int lastId = 0;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport( this );
	private final Context context;
	private final Point originPoint;
	
	// These are used only during deserialization
    private final Map<String, String> toolLabels = new HashMap<>();
    private final Map<String, Boolean> toolDeleteInputs = new HashMap<>();
    private final Map<String, Boolean> toolSelectInputs = new HashMap<>();
    private final Map<String, Boolean> toolCopyColors = new HashMap<>();
    private final Set<String> hiddenTools = new HashSet<>();
    
	public ToolsModel( Context context, Point originPoint )
	{
		super();
		this .context = context;
		this .originPoint = originPoint;
	}
	
	public int reserveId()
	{
		return this .lastId++;
	}
	
	/**
	 * Only called during load of a document, before any new tool creations with reserveId.
	 * @param id
	 */
	public void setMaxId( int id )
	{
		if ( id >= this .lastId )
			this .lastId = id + 1;
	}

	@Override
	public Tool put( String key, Tool tool )
	{
		this .pcs .firePropertyChange( "tool.instances", null, tool );
		return super .put( key, tool );
	}

	// Create from deserializing
	public UndoableEdit createEdit( String className )
	{
		switch ( className ) {

        case "ToolApplied":
            return new ApplyTool( this, null, false, false, false, false, false, true );

        case "ApplyTool":
            return new ApplyTool( this, null, false, false, false, false, true, true );
        
		case "SelectToolParameters":
		    return new SelectToolParameters( this, null );

        default:
			return null;
		}
	}

	public void applyTool( Tool tool, boolean selectInputs, boolean deleteInputs, boolean createOutputs, boolean selectOutputs, boolean copyColors )
	{
		UndoableEdit edit = new ApplyTool( this, tool, selectInputs, deleteInputs, createOutputs, selectOutputs, true, copyColors );
        this .getContext() .performAndRecord( edit );
	}	

	public void selectToolParameters( Tool tool )
	{
		UndoableEdit edit = new SelectToolParameters( this, tool );
		this .getContext() .performAndRecord( edit );
	}

    public void addPropertyListener( PropertyChangeListener listener )
    {
        pcs .addPropertyChangeListener( listener );
    }

    public void removePropertyListener( PropertyChangeListener listener )
    {
        pcs .removePropertyChangeListener( listener );
    }

	public void setEditorModel( EditorModel editor )
	{
		this.editor = editor;
	}

	public EditorModel getEditorModel()
	{
		return this .editor;
	}

	@Override
	public Tool getPredefinedTool( String id )
	{
		return this .get( id );
	}

	public Context getContext()
	{
		return this .context;
	}

	public Point getOriginPoint()
	{
		return this .originPoint;
	}

	public Element getXml( Document doc )
	{
        Element result = doc .createElement( "Tools" );
        for ( Tool tool : this .values() ) 
        	if ( ! tool .isPredefined() ){
        		Element toolElem = doc .createElement( "Tool" );
        		DomUtils .addAttribute( toolElem, "id", tool .getId() );
        		DomUtils .addAttribute( toolElem, "label", tool .getLabel() );
        		if ( tool .isHidden() )
        		    DomUtils .addAttribute( toolElem, "hidden", "true" );
        		if ( tool .isSelectInputs() )
        		    toolElem .setAttribute( "selectInputs", "true" );
                if ( tool .isDeleteInputs() )
                    toolElem .setAttribute( "deleteInputs", "true" );
                if ( ! tool .isCopyColors() )
                    toolElem .setAttribute( "copyColors", "false" );
        		result .appendChild( toolElem );
        	}
        return result;
    }
    
    void loadFromXml( Element xml )
    {
        NodeList nodes = xml .getChildNodes();
        for ( int i = 0; i < nodes .getLength(); i++ ) {
            Node node = nodes .item( i );
            if ( node instanceof Element ) {
                Element toolElem = (Element) node;

                String id = toolElem .getAttribute( "id" );
                String label = toolElem .getAttribute( "label" );
                this .toolLabels .put( id, label );

                String value = toolElem .getAttribute( "selectInputs" );
                if ( value != null && value .equals( "true" ) )
                    toolSelectInputs .put( id, Boolean.parseBoolean( value ) );
                value = toolElem .getAttribute( "deleteInputs" );
                if ( value != null && value .equals( "true" ) )
                    toolDeleteInputs .put( id, Boolean.parseBoolean( value ) );
                value = toolElem .getAttribute( "copyColors" );
                if ( value != null && value .equals( "false" ) ) // Note, different from the other two... true by default
                    toolCopyColors .put( id, Boolean.parseBoolean( value ) );
                else
                    toolCopyColors .put( id, Boolean.valueOf( true ) );
                
                String hiddenStr = toolElem .getAttribute( "hidden" );
                if ( hiddenStr != null && hiddenStr .equals( "true" ) )
                    this .hiddenTools .add( id );
            }
        }
    }

    public void setConfiguration( Tool tool )
    {
        // update the tool from the maps, deserialized earlier
        String id = tool .getId();
        String label = this .toolLabels .get( id );
        if ( label != null ) // be careful not to override the defaults for legacy commands with no serialized maps
            tool .setLabel( label );

        // be careful not to override the defaults for legacy commands with no serialized maps
        if ( this .toolDeleteInputs .containsKey( id ) || this .toolSelectInputs .containsKey( id ) ) {
            // the map only ever contains "true" values; see loadFromXml() above
            tool .setInputBehaviors( this .toolSelectInputs .containsKey( id ), this .toolDeleteInputs .containsKey( id ) );
        }
        tool .setCopyColors( this .toolCopyColors .get( id ) ); //

        tool .setHidden( this .hiddenTools .contains( id ) );
    }

    public void hideTool( Tool tool )
    {
        this .pcs .firePropertyChange( "tool.instances", tool, null );
    }
}
