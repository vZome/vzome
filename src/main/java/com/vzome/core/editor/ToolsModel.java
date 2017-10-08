package com.vzome.core.editor;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EnumSet;
import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vzome.api.Tool.InputBehaviors;
import com.vzome.api.Tool.OutputBehaviors;
import com.vzome.core.construction.Point;
import com.vzome.core.math.DomUtils;

public class ToolsModel extends TreeMap<String, Tool> implements Tool.Source
{
	private EditorModel editor;
	private int lastId = 0;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport( this );
	private final UndoableEdit.Context context;
	private final Point originPoint;
    
	public ToolsModel( UndoableEdit.Context context, Point originPoint )
	{
		super();
		this .context = context;
		this .originPoint = originPoint;
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
				Tool tool = this .get( id );
				tool .setLabel( label );
			}
		}
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
		if ( id > this .lastId )
			this .lastId = id;
	}

	@Override
	public Tool put( String key, Tool tool )
	{
		this .pcs .firePropertyChange( "tool.instances", null, tool );
		return super .put( key, tool );
	}

	public UndoableEdit createEdit( Element xml )
	{
		UndoableEdit edit = null;
        String className = xml .getLocalName();
		switch ( className ) {

        case "ToolApplied":
			edit = new ApplyTool( this, null, EnumSet.noneOf( InputBehaviors.class ), EnumSet.noneOf( OutputBehaviors.class ), false );
			return edit;

        case "ApplyTool":
			edit = new ApplyTool( this, null, EnumSet.noneOf( InputBehaviors.class ), EnumSet.noneOf( OutputBehaviors.class ), true );
			return edit;
        
		case "SelectToolParameters":
			edit = new SelectToolParameters( this, null );
			return edit;

        default:
			return null;
		}
	}

	public void applyTool( Tool tool, EnumSet<InputBehaviors> inputAction, EnumSet<OutputBehaviors> outputAction )
	{
		UndoableEdit edit = new ApplyTool( this, tool, inputAction, outputAction, true );
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

	public UndoableEdit.Context getContext()
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
        		result .appendChild( toolElem );
        	}
        return result;
    }
}
