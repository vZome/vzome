package com.vzome.core.editor;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import com.vzome.core.tools.BookmarkTool;
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
    private final Map<String, Integer> toolOrder = new HashMap<>();
    private final Set<String> hiddenTools = new HashSet<>();
    
    // Adding these for the web client
    private final List<String> customTools = new ArrayList<>();
    private final List<String> customBookmarks = new ArrayList<>();
    
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
		Tool result = super .put( key, tool );
		// PCE listeners may access this map!
        this .pcs .firePropertyChange( "tool.instances", null, tool );

        // Give a newly created custom tool an explicit toolbar order at the end, so it sorts after
        //  existing tools. (When LOADING, setConfiguration() overwrites this with the saved order,
        //  or leaves it if the file predates ordering.)
        if ( ! tool .isPredefined() && tool .getOrder() < 0 )
            tool .setOrder( this .nextToolOrder() );

        // Adding this for the web client, which is quite nicely generic React or SolidJS
        if ( ! tool .isPredefined() && ! tool .isHidden() ) {
            if ( tool .getCategory() .equals( BookmarkTool.ID ) ) {
                this .customBookmarks .add( key );
                this .pcs .firePropertyChange( "customBookmarks", null, this .getToolIDs( true ) );
            }
            else {
                this .customTools .add( key );
                this .pcs .firePropertyChange( "customTools", null, this .getToolIDs( false ) );
            }
        }
        return result;
	}
	
	// The pinned (visible) custom tools or bookmarks, in user-controlled toolbar order.
	public String[] getToolIDs( boolean bookmarks )
	{
	    List<String> ids = bookmarks? this .customBookmarks : this .customTools;
	    List<Tool> tools = new ArrayList<>();
	    for ( String id : ids ) {
	        Tool tool = this .get( id );
	        if ( tool != null )
	            tools .add( tool );
	    }
	    tools .sort( TOOLBAR_ORDER );
	    List<String> result = new ArrayList<>();
	    for ( Tool tool : tools )
	        result .add( tool .getId() );
	    return result .toArray( new String[]{} );
	}

	// The complete roster of user-created (non-predefined) tools, INCLUDING hidden (unpinned)
	//  ones, unlike getToolIDs() which tracks only the currently-pinned customs.  The web
	//  client's "more tools" overflow derives from this to offer unpinned customs for re-pinning.
	//  Returned in user-controlled toolbar order: tools with an explicit order (getOrder() >= 0)
	//  sort first by that order; tools with no saved order (-1, e.g. legacy files) fall back to
	//  id/creation order after them.  Legacy files (all unset) thus keep today's id order exactly.
	public String[] getAllCustomToolIDs( boolean bookmarks )
	{
	    List<Tool> tools = new ArrayList<>();
	    for ( Tool tool : this .values() )
	        if ( ! tool .isPredefined()
	                && tool .getCategory() .equals( BookmarkTool.ID ) == bookmarks )
	            tools .add( tool );
	    tools .sort( TOOLBAR_ORDER );
	    List<String> result = new ArrayList<>();
	    for ( Tool tool : tools )
	        result .add( tool .getId() );
	    return result .toArray( new String[]{} );
	}

	// The next toolbar-order value to assign to a newly created custom tool: one past the highest
	//  order currently in use (0 if none), so new tools land at the end.
	private int nextToolOrder()
	{
	    int max = -1;
	    for ( Tool tool : this .values() )
	        if ( ! tool .isPredefined() && tool .getOrder() > max )
	            max = tool .getOrder();
	    return max + 1;
	}

	// Sort by explicit toolbar order when set; unset (-1) tools go last, tie-broken by id so the
	//  order is stable and matches legacy id-order when nothing has an explicit order.
	private static final java.util.Comparator<Tool> TOOLBAR_ORDER = ( a, b ) -> {
	    int oa = a .getOrder(), ob = b .getOrder();
	    boolean sa = oa >= 0, sb = ob >= 0;
	    if ( sa && sb && oa != ob ) return Integer .compare( oa, ob );
	    if ( sa != sb ) return sa? -1 : 1; // set tools before unset
	    return a .getId() .compareTo( b .getId() );
	};

	// Assign a new toolbar order to the given custom tools (or bookmarks). The client sends the
	//  full desired sequence of ids; we stamp order = 0,1,2,... by position, then refresh the
	//  client-facing lists. This is a non-undoable UI-layout change, like hide/unhide.
	public void reorderTools( boolean bookmarks, String[] orderedIds )
	{
	    for ( int i = 0; i < orderedIds .length; i++ ) {
	        Tool tool = this .get( orderedIds[ i ] );
	        if ( tool != null )
	            tool .setOrder( i );
	    }
	    if ( bookmarks )
	        this .pcs .firePropertyChange( "customBookmarks", null, this .getToolIDs( true ) );
	    else
	        this .pcs .firePropertyChange( "customTools", null, this .getToolIDs( false ) );
	    // The full roster (which drives the client's ordered list) also changed.
	    this .pcs .firePropertyChange( bookmarks? "allCustomBookmarks" : "allCustomTools", null,
	            this .getAllCustomToolIDs( bookmarks ) );
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
        		// User-controlled toolbar order (additive; absent in files that predate ordering).
        		if ( tool .getOrder() >= 0 )
        		    DomUtils .addAttribute( toolElem, "order", Integer .toString( tool .getOrder() ) );
        		// Always be explicit, to override implicit legacy default behaviors in loadFromXml() and setConfiguration()
        		toolElem .setAttribute( "selectInputs", Boolean.toString( tool .isSelectInputs() ) );
        		toolElem .setAttribute( "deleteInputs", Boolean.toString( tool .isDeleteInputs() ) );
        		toolElem .setAttribute( "copyColors", Boolean.toString( tool .isCopyColors() ) );
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
                if ( value != null && ! value .equals( "" ) )
                    toolSelectInputs .put( id, Boolean.parseBoolean( value ) );
                value = toolElem .getAttribute( "deleteInputs" );
                if ( value != null && ! value .equals( "" ) )
                    toolDeleteInputs .put( id, Boolean.parseBoolean( value ) );
                value = toolElem .getAttribute( "copyColors" );
                if ( value != null && ! value .equals( "" ) )
                    toolCopyColors .put( id, Boolean.parseBoolean( value ) );
                
                String hiddenStr = toolElem .getAttribute( "hidden" );
                if ( hiddenStr != null && hiddenStr .equals( "true" ) )
                    this .hiddenTools .add( id );

                String orderStr = toolElem .getAttribute( "order" );
                if ( orderStr != null && ! orderStr .equals( "" ) )
                    this .toolOrder .put( id, Integer .parseInt( orderStr ) );
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
            boolean deleteInputs = this .toolDeleteInputs .containsKey( id )? this .toolDeleteInputs .get( id ) : true;
            boolean selectInputs = this .toolSelectInputs .containsKey( id )? this .toolSelectInputs .get( id ) : false;
            tool .setInputBehaviors( selectInputs, deleteInputs );
        }
        if ( this .toolCopyColors .containsKey( id ) ) {
            tool .setCopyColors( this .toolCopyColors .get( id ) );
        }

        // Apply the saved toolbar order if present; absent (legacy files) leaves the order that
        //  put() assigned at creation, so those keep today's id/creation order.
        if ( this .toolOrder .containsKey( id ) )
            tool .setOrder( this .toolOrder .get( id ) );

        tool .setHidden( this .hiddenTools .contains( id ) );
    }

    public void hideTool( Tool tool )
    {
        this .pcs .firePropertyChange( "tool.instances", tool, null );

        if ( tool .getCategory() .equals( BookmarkTool.ID ) ) {
            this .customBookmarks .remove( tool .getId() );
            this .pcs .firePropertyChange( "customBookmarks", null, this .getToolIDs( true ) );
        }
        else {
            this .customTools .remove( tool .getId() );
            this .pcs .firePropertyChange( "customTools", null, this .getToolIDs( false ) );
        }
    }

    public void unhideTool( Tool tool )
    {
        // Symmetric to hideTool: re-pin a previously hidden tool.
        this .pcs .firePropertyChange( "tool.instances", null, tool );

        // Predefined tools are not tracked in the custom lists; they reach the
        //  toolbar via SymmetryController's builtIn*Tools, so only re-add customs here.
        if ( ! tool .isPredefined() ) {
            if ( tool .getCategory() .equals( BookmarkTool.ID ) ) {
                if ( ! this .customBookmarks .contains( tool .getId() ) )
                    this .customBookmarks .add( tool .getId() );
                this .pcs .firePropertyChange( "customBookmarks", null, this .getToolIDs( true ) );
            }
            else {
                if ( ! this .customTools .contains( tool .getId() ) )
                    this .customTools .add( tool .getId() );
                this .pcs .firePropertyChange( "customTools", null, this .getToolIDs( false ) );
            }
        }
    }
}
