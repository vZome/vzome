
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.core.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.OutputStream;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.commands.AbstractCommand;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentCrossProduct;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.editor.Snapshot.SnapshotAction;
import com.vzome.core.exporters.Exporter3d;
import com.vzome.core.exporters.OpenGLExporter;
import com.vzome.core.exporters.POVRayExporter;
import com.vzome.core.exporters.PartGeometryExporter;
import com.vzome.core.math.DomUtils;
import com.vzome.core.math.Projection;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.math.symmetry.OrbitSet;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Exporter;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.ManifestationChanges;
import com.vzome.core.model.Panel;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;
import com.vzome.core.model.VefModelExporter;
import com.vzome.core.render.Color;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.render.RenderedModel.OrbitSource;
import com.vzome.core.viewing.Camera;
import com.vzome.core.viewing.Lights;

public class DocumentModel implements Snapshot .Recorder, UndoableEdit .Context, Tool .Registry
{
	private final RealizedModel mRealizedModel;

	private final Point originPoint;

	private final Selection mSelection;

	private final EditorModel mEditorModel;
	
	private SymmetrySystem symmetrySystem;

    private final EditHistory mHistory;
    
    private final LessonModel lesson = new LessonModel();
    
	private final AlgebraicField mField;
	
	private final Map<String, Tool> tools = new LinkedHashMap<>();
	
	private final Command.FailureChannel failures;

	private int changes = 0;
	
	private boolean migrated = false;

	private final Element mXML;
	
	private RenderedModel renderedModel;
	
	private Camera defaultView;
	
	private final String coreVersion;

    private static final Logger logger = Logger .getLogger( "com.vzome.core.editor" );
    private static final Logger thumbnailLogger = Logger.getLogger( "com.vzome.core.thumbnails" );

    // 2013-05-26
    //  I thought about leaving these two in EditorModel, but reconsidered.  Although they are in-memory
    //  state only, not saved in the file, they are still necessary for non-interactive use such as lesson export.
    
    private RenderedModel[] snapshots = new RenderedModel[8];
    
    private int numSnapshots = 0;

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport( this );

	private final Map<String, Object> commands; // TODO: DJH: Don't allow non-Command objects in this Map.

    private final Map<String,SymmetrySystem> symmetrySystems = new HashMap<>();

	private final Lights sceneLighting;

    public void addPropertyChangeListener( PropertyChangeListener listener )
    {
    	propertyChangeSupport .addPropertyChangeListener( listener );
    }

    public void removePropertyChangeListener( PropertyChangeListener listener )
    {
    	propertyChangeSupport .removePropertyChangeListener( listener );
    }

    protected void firePropertyChange( String propertyName, Object oldValue, Object newValue )
    {
    	propertyChangeSupport .firePropertyChange( propertyName, oldValue, newValue );
    }

    protected void firePropertyChange( String propertyName, int oldValue, int newValue )
    {
    	propertyChangeSupport .firePropertyChange( propertyName, oldValue, newValue );
    }

	public DocumentModel( final AlgebraicField field, Command.FailureChannel failures, Element xml, final Application app )
	{
		super();

		this .mField = field;
		AlgebraicVector origin = field .origin( 3 );
		this .originPoint = new FreePoint( origin );
		this .failures = failures;
		this .mXML = xml;
		this .commands = app .getCommands();
		this .sceneLighting = app .getLights();
		
		this .coreVersion = app .getCoreVersion();

		this .mRealizedModel = new RealizedModel( field, new Projection.Default( field ) );
		
		this .mSelection = new Selection();

        Symmetry[] symms = field .getSymmetries();
        for (Symmetry symm : symms) {
            SymmetrySystem osm = new SymmetrySystem( null, symm, app .getColors(), app .getGeometries( symm ), true );
            // one of these will be overwritten below, if we are loading from a file that has it set
            this .symmetrySystems .put( osm .getName(), osm );
            
            try {
            	String name = symm .getName();
				makeTool( name + ".builtin/" + name + " around origin", name, this, symm ) .perform();
			} catch ( Command.Failure e ) {
				logger .severe( "Failed to create " + symm .getName() + " tool." );
			}
        }

		if ( xml != null ) {
	        NodeList nl = xml .getElementsByTagName( "SymmetrySystem" );
	        if ( nl .getLength() != 0 )
	            xml = (Element) nl .item( 0 );
	        else
	            xml = null;
		}
        String symmName = ( xml != null )? xml .getAttribute( "name" ) : symms[ 0 ] .getName();

        Symmetry symmetry = field .getSymmetry( symmName );
        this .symmetrySystem = new SymmetrySystem( xml, symmetry, app .getColors(), app .getGeometries( symmetry ), true );
        this .symmetrySystems .put( symmName, this .symmetrySystem );

        try {
			makeTool( "tetrahedral.builtin/tetrahedral around origin", "tetrahedral", this, symmetry ) .perform();
			makeTool( "point reflection.builtin/reflection through origin", "point reflection", this, symmetry ) .perform();
			makeTool( "scaling.builtin/scale up", "scale up", this, symmetry ) .perform();
			makeTool( "scaling.builtin/scale down", "scale down", this, symmetry ) .perform();
			makeTool( "translation.builtin/move right", "translation", this, symmetry ) .perform();
			makeTool( "mirror.builtin/reflection through XY plane", "mirror", this, symmetry ) .perform();
			makeTool( "bookmark.builtin/ball at origin", "bookmark", this, symmetry ) .perform();
			if ( symmetry instanceof IcosahedralSymmetry ) {
				makeTool( "rotation.builtin/rotate around red through origin", "rotation", this, symmetry ) .perform();
				makeTool( "axial symmetry.builtin/symmetry around red through origin", "axial symmetry", this, symmetry ) .perform();
			}
		} catch ( Command.Failure e ) {
			logger .severe( "Failed to create built-in tools." );
		}

        this .renderedModel = new RenderedModel( field, this .symmetrySystem );

		this .mRealizedModel .addListener( this .renderedModel ); // just setting the default
		// the renderedModel must either be disabled, or have shapes here, so the origin ball gets rendered
		this .mEditorModel = new EditorModel( this .mRealizedModel, this .mSelection, /*oldGroups*/ false, originPoint );

		this .defaultView = new Camera();
        Element views = ( this .mXML == null )? null : (Element) this .mXML .getElementsByTagName( "Viewing" ) .item( 0 );
        if ( views != null ) {
            NodeList nodes = views .getChildNodes();
            for ( int i = 0; i < nodes .getLength(); i++ ) {
                Node node = nodes .item( i );
        		if ( node instanceof Element ) {
        			Element viewElem = (Element) node;
        			String name = viewElem .getAttribute( "name" );
        			if ( ( name == null || name .isEmpty() )
        		    || ( "default" .equals( name ) ) )
        			{
        				this .defaultView = new Camera( viewElem );
        			}
        		}
            }
        }

        mHistory = new EditHistory();
        mHistory .setListener( new EditHistory.Listener() {
			
			@Override
			public void showCommand( Element xml, int editNumber )
			{
				String str = editNumber + ": " + DomUtils .toString( xml );
				DocumentModel.this .firePropertyChange( "current.edit.xml", null, str );
			}
		});

        lesson .addPropertyChangeListener( new PropertyChangeListener()
        {
            @Override
			public void propertyChange( PropertyChangeEvent change )
			{
				if ( "currentSnapshot" .equals( change .getPropertyName() ) )
				{
					int id = ((Integer) change .getNewValue());
	                RenderedModel newSnapshot = snapshots[ id ];
	                firePropertyChange( "currentSnapshot", null, newSnapshot );
				}
				else if ( "currentView" .equals( change .getPropertyName() ) )
				{
				    // forward to doc listeners
                    firePropertyChange( "currentView", change .getOldValue(), change .getNewValue() );
				}
				else if ( "thumbnailChanged" .equals( change .getPropertyName() ) )
				{
				    // forward to doc listeners
                    firePropertyChange( "thumbnailChanged", change .getOldValue(), change .getNewValue() );
				}
			}
		} );
	}
		
	public int getChangeCount()
	{
		return this .changes;
	}
	
	public boolean isMigrated()
	{
	    return this .migrated;
	}
	
	public void setRenderedModel( RenderedModel renderedModel )
	{
		this .mRealizedModel .removeListener( this .renderedModel );
		this .renderedModel = renderedModel;
		this .mRealizedModel .addListener( renderedModel );
		
		// "re-render" the origin
        Manifestation m = this .mRealizedModel .findConstruction( originPoint );
		m .setRenderedObject( null );
		this .mRealizedModel .show( m );
	}

    @Override
	public UndoableEdit createEdit( Element xml, boolean groupInSelection )
	{
		UndoableEdit edit = null;
		String name = xml .getLocalName();

		if ( "Snapshot" .equals( name ) )
			edit = new Snapshot( -1, this );

		else if ( "Branch" .equals( name ) )
			edit = new Branch( this );

        else if ( "Delete" .equals( name ) )
            edit = new Delete( this.mSelection, this.mRealizedModel );

        else if ( "ShowPoint".equals( name ) )
			edit = new ShowPoint( null, this.mSelection, this.mRealizedModel, groupInSelection );

		else if ( "setItemColor".equals( name ) )
			edit = new ColorManifestations( this.mSelection, this.mRealizedModel, null, groupInSelection );

		else if ( "ShowHidden".equals( name ) )
			edit = new ShowHidden( this.mSelection, this.mRealizedModel, groupInSelection );

		else if ( "CrossProduct".equals( name ) )
			edit = new CrossProduct( this.mSelection, this.mRealizedModel, groupInSelection );

		else if ( "AffinePentagon".equals( name ) )
			edit = new AffinePentagon( this.mSelection, this.mRealizedModel, groupInSelection );

		else if ( "AffineTransformAll".equals( name ) )
			edit = new AffineTransformAll( this.mSelection, this.mRealizedModel, this.mEditorModel.getCenterPoint(), groupInSelection );

		else if ( "HeptagonSubdivision".equals( name ) )
			edit = new HeptagonSubdivision( this.mSelection, this.mRealizedModel, groupInSelection );

		else if ( "DodecagonSymmetry".equals( name ) )
			edit = new DodecagonSymmetry( this.mSelection, this.mRealizedModel, this.mEditorModel.getCenterPoint(), groupInSelection );

		else if ( "GhostSymmetry24Cell".equals( name ) )
			edit = new GhostSymmetry24Cell( this.mSelection, this.mRealizedModel, this.mEditorModel.getSymmetrySegment(),
					groupInSelection );

		else if ( "StrutCreation".equals( name ) )
			edit = new StrutCreation( null, null, null, this.mRealizedModel );

		else if ( "BnPolyope".equals( name ) || "B4Polytope".equals( name ) )
			edit = new B4Polytope( this.mSelection, this.mRealizedModel, this .mField, null, 0, groupInSelection );

		else if ( "Polytope4d".equals( name ) )
			edit = new Polytope4d( this.mSelection, this.mRealizedModel, null, 0, null, groupInSelection );

		else if ( "LoadVEF".equals( name ) )
			edit = new LoadVEF( this.mSelection, this.mRealizedModel, null, null, null );

		else if ( "GroupSelection".equals( name ) )
			edit = new GroupSelection( this.mSelection, false );

		else if ( "BeginBlock".equals( name ) )
			edit = new BeginBlock();

		else if ( "EndBlock".equals( name ) )
			edit = new EndBlock();

		else if ( "InvertSelection".equals( name ) )
			edit = new InvertSelection( this.mSelection, this.mRealizedModel, groupInSelection );

		else if ( "JoinPoints".equals( name ) )
			edit = new JoinPoints( this.mSelection, this.mRealizedModel, groupInSelection );

		else if ( "NewCentroid" .equals( name ) )
			edit = new Centroid( this.mSelection, this.mRealizedModel, groupInSelection );

		else if ( "StrutIntersection".equals( name ) )
			edit = new StrutIntersection( this.mSelection, this.mRealizedModel, groupInSelection );

		else if ( "LinePlaneIntersect".equals( name ) )
			edit = new LinePlaneIntersect( this.mSelection, this.mRealizedModel, groupInSelection );

		else if ( "SelectAll".equals( name ) )
			edit = new SelectAll( this.mSelection, this.mRealizedModel, groupInSelection );

		else if ( "DeselectAll".equals( name ) )
			edit = new DeselectAll( this.mSelection, groupInSelection );

		else if ( "DeselectByClass".equals( name ) )
			edit = new DeselectByClass( this.mSelection, false );

		else if ( "SelectManifestation".equals( name ) )
			edit = new SelectManifestation( null, false, this.mSelection, this.mRealizedModel, groupInSelection );

		else if ( "SelectNeighbors".equals( name ) )
			edit = new SelectNeighbors( this.mSelection, this.mRealizedModel, groupInSelection );

		else if ( "SelectSimilarSize".equals( name ) )
		{
            /*
            The normal pattern is to have the edits deserialize their own parameters from the XML in setXmlAttributes()
            but in the case of persisting the symmetry, it must be read here and passed into the c'tor
            since this is where the map from a name to an actual SymmetrySystem is maintained.
            These edits are still responsible for saving the symmetry name to XML in getXmlAttributes().
            Also see the comments of commit # 8c8cb08a1e4d71f91f24669b203fef0378230b19 on 3/8/2015
            */
		    SymmetrySystem symmetry = this .symmetrySystems .get( xml .getAttribute( "symmetry" ) );
            edit = new SelectSimilarSizeStruts( symmetry, null, null, this .mSelection, this .mRealizedModel );
		}
		else if ( "SelectParallelStruts".equals( name ) )
		{
            // See the note above about deserializing symmetry from XML.
		    SymmetrySystem symmetry = this .symmetrySystems .get( xml .getAttribute( "symmetry" ) );
            edit = new SelectParallelStruts( symmetry, this .mSelection, this .mRealizedModel );
		}
		else if ( "SelectAutomaticStruts".equals( name ) )
		{
            // See the note above about deserializing symmetry from XML.
		    SymmetrySystem symmetry = this .symmetrySystems .get( xml .getAttribute( "symmetry" ) );
            edit = new SelectAutomaticStruts( symmetry, this .mSelection, this .mRealizedModel );
		}
		else if ( "SelectCollinear".equals( name ) )
		{
            edit = new SelectCollinear( this .mSelection, this .mRealizedModel );
		}
		else if ( "ValidateSelection".equals( name ) )
			edit = new ValidateSelection( this.mSelection );

		else if ( "SymmetryCenterChange".equals( name ) )
			edit = new SymmetryCenterChange( this.mEditorModel, null );

		else if ( "SymmetryAxisChange".equals( name ) )
			edit = new SymmetryAxisChange( this.mEditorModel, null );

		else if ( "RunZomicScript".equals( name ) )
			edit = new RunZomicScript( this.mSelection, this.mRealizedModel, null, mEditorModel.getCenterPoint() );

		else if ( "RunPythonScript".equals( name ) )
			edit = new RunPythonScript( this.mSelection, this.mRealizedModel, null, mEditorModel.getCenterPoint() );

		else if ( "BookmarkTool".equals( name ) )
			edit = new BookmarkTool( name, this.mSelection, this.mRealizedModel, this );

		else if ( "ModuleTool" .equals( name ) )
			edit = new ModuleTool( null, mSelection, mRealizedModel, this );

		else if ( "PlaneSelectionTool" .equals( name ) )
			edit = new PlaneSelectionTool( null, mSelection, mField, this );

		else if ( "SymmetryTool".equals( name ) )
			edit = new SymmetryTool( null, null, this.mSelection, this.mRealizedModel, this, this.originPoint );

		else if ( "ScalingTool".equals( name ) )
			edit = new ScalingTool( name, null, this.mSelection, this.mRealizedModel, this, this.originPoint );

		else if ( "RotationTool".equals( name ) )
			edit = new RotationTool( name, null, this.mSelection, this.mRealizedModel, this, this.originPoint );

		else if ( "InversionTool".equals( name ) )
			edit = new InversionTool( name, this.mSelection, this.mRealizedModel, this, this.originPoint );

		else if ( "MirrorTool".equals( name ) )
			edit = new MirrorTool( name, this.mSelection, this.mRealizedModel, this, this.originPoint );

		else if ( "TranslationTool".equals( name ) )
			edit = new TranslationTool( name, this.mSelection, this.mRealizedModel, this, this.originPoint );

		else if ( "LinearMapTool".equals( name ) )
			edit = new LinearMapTool( name, this.mSelection, this.mRealizedModel, this, this.originPoint, true );

		else if ( "AxialStretchTool".equals( name ) ) {
			IcosahedralSymmetry symmetry = (IcosahedralSymmetry) mField .getSymmetry( "icosahedral" );
			edit = new AxialStretchTool( name, symmetry, mSelection, mRealizedModel, this, false, true, false );
		}
		else if ( "LinearTransformTool".equals( name ) )
			edit = new LinearMapTool( name, this.mSelection, this.mRealizedModel, this, this.originPoint, false );

		else if ( "ToolApplied".equals( name ) )
			edit = new ApplyTool( this.mSelection, this.mRealizedModel, this, false );

		else if ( "ApplyTool".equals( name ) )
			edit = new ApplyTool( this.mSelection, this.mRealizedModel, this, true );

        else if ( RealizeMetaParts.NAME .equals( name ) )
            edit = new RealizeMetaParts( mSelection, mRealizedModel );

        else if ( ShowVertices.NAME .equals( name ) )
            edit = new ShowVertices( mSelection, mRealizedModel );

		else if ( "Symmetry4d".equals( name ) ) {
            QuaternionicSymmetry h4symm = this .mField .getQuaternionSymmetry( "H_4" ); 
			edit = new Symmetry4d( this.mSelection, this.mRealizedModel, h4symm, h4symm );
		}

		if ( edit == null )
			// any command unknown (i.e. from a newer version of vZome) becomes a CommandEdit
			edit = new CommandEdit( null, mEditorModel, groupInSelection );
		
		return edit;
	}
	
	public String copySelectionVEF()
	{
		StringWriter out = new StringWriter();
		Exporter exporter = new VefModelExporter( out, mField );
        for (Manifestation man : mSelection) {
            exporter .exportManifestation( man );
        }
		exporter .finish();
		return out .toString();
	}

	public void pasteVEF( String vefContent )
	{
        if( vefContent != null && vefContent.startsWith("vZome VEF" )) {
            // Although older VEF formats don't all include the header and could possibly be successfully pasted here,
            // we're going to limit it to at least something that includes a valid VEF header.
            // We won't check the version number so we can still paste formats older than VERSION_W_FIRST
            // as long as they at least include the minimal header.
            UndoableEdit edit = new LoadVEF( this.mSelection, this.mRealizedModel, vefContent, null, null );
            performAndRecord( edit );
        }
	}

	public void applyTool( Tool tool, Tool.Registry registry, int modes )
	{
		UndoableEdit edit = new ApplyTool( this.mSelection, this.mRealizedModel, tool, registry, modes, true );
        performAndRecord( edit );
	}
	
	public void selectToolParameters( TransformationTool tool )
	{
		UndoableEdit edit = new SelectToolParameters( this.mSelection, this.mRealizedModel, this, tool );
        performAndRecord( edit );
	}
	
	public void applyQuaternionSymmetry( QuaternionicSymmetry left, QuaternionicSymmetry right )
	{
		UndoableEdit edit = new Symmetry4d( this.mSelection, this.mRealizedModel, left, right );
        performAndRecord( edit );
	}

    public boolean doEdit( String action )
    {
    	// TODO break all these cases out as dedicated DocumentModel methods
    	
    	if ( this .mEditorModel .mSelection .isEmpty() && action .equals( "hideball" ) ) {
    		action = "showHidden";
		}

    	Command command = (Command) commands .get( action );
    	if ( command != null )
    	{
    		CommandEdit edit = new CommandEdit( (AbstractCommand) command, mEditorModel, false );
            this .performAndRecord( edit );
            return true;
    	}
    	
        UndoableEdit edit = null;
        if ( action.equals( "selectAll" ) )
            edit = mEditorModel.selectAll();
        else if ( action.equals( "unselectAll" ) )
            edit = mEditorModel.unselectAll();
        else if ( action.equals( "unselectBalls" ) )
            edit = mEditorModel.unselectConnectors();
        else if ( action.equals( "unselectStruts" ) )
            edit = mEditorModel.unselectStruts();
        else if ( action.equals( "selectNeighbors" ) )
            edit = mEditorModel.selectNeighbors();
        else if ( action.equals( "SelectAutomaticStruts" ) )
            edit = mEditorModel.selectAutomaticStruts(symmetrySystem);
        else if ( action.equals( "SelectCollinear" ) )
            edit = mEditorModel.selectCollinear();
        else if ( action.equals( "SelectParallelStruts" ) )
            edit = mEditorModel.selectParallelStruts(symmetrySystem);
        else if ( action.equals( "invertSelection" ) )
            edit = mEditorModel.invertSelection();
        else if ( action.equals( "group" ) )
            edit = mEditorModel.groupSelection();
        else if ( action.equals( "ungroup" ) )
            edit = mEditorModel.ungroupSelection();

        else if ( action.equals( "assertSelection" ) )
            edit = new ValidateSelection( mSelection );

        else if ( action.equals( "delete" ) )
            edit = new Delete( mSelection, mRealizedModel );

//        else if ( action.equals( "sixLattice" ) )
//            edit = new SixLattice( mSelection, mRealizedModel, mDerivationModel );

        // not supported currently, so I don't have to deal with the mTargetManifestation problem
//        else if ( action .equals( "reversePanel" ) )
//            edit = new ReversePanel( mTargetManifestation, mSelection, mRealizedModel, mDerivationModel );
        else if ( action.equals( "createStrut" ) )
            edit = new StrutCreation( null, null, null, mRealizedModel );
        else if ( action.startsWith( "setItemColor/" ) )
        {
            String value = action .substring( "setItemColor/" .length() );
            edit = new ColorManifestations( mSelection, mRealizedModel, new Color( value ), false );
        }
        else if ( action.equals( "joinballs" ) )
            edit = new JoinPoints( mSelection, mRealizedModel, false, JoinPoints.JoinModeEnum.CLOSED_LOOP );
        else if ( action .toLowerCase() .equals( "chainballs" ) )
            edit = new JoinPoints( mSelection, mRealizedModel, false, JoinPoints.JoinModeEnum.CHAIN_BALLS );
        else if ( action.equals( "joinBallsAllToFirst" ) )
            edit = new JoinPoints( mSelection, mRealizedModel, false, JoinPoints.JoinModeEnum.ALL_TO_FIRST );
        else if ( action.equals( "joinBallsAllToLast" ) )
            edit = new JoinPoints( mSelection, mRealizedModel, false, JoinPoints.JoinModeEnum.ALL_TO_LAST );
        else if ( action.equals( "joinBallsAllPossible" ) )
            edit = new JoinPoints( mSelection, mRealizedModel, false, JoinPoints.JoinModeEnum.ALL_POSSIBLE );
        else if ( ShowVertices.NAME .toLowerCase() .equals( action .toLowerCase() ) )
            edit = new ShowVertices( mSelection, mRealizedModel );
        else if ( action.equals( "ballAtOrigin" ) )
            edit = new ShowPoint( originPoint, mSelection, mRealizedModel, false );
        else if ( action.equals( "ballAtSymmCenter" ) )
            edit = new ShowPoint( mEditorModel.getCenterPoint(), mSelection, mRealizedModel, false );
        else if ( action.equals( "linePlaneIntersect" ) )
            edit = new LinePlaneIntersect( mSelection, mRealizedModel, false );
        else if ( action.equals( "lineLineIntersect" ) )
            edit = new StrutIntersection( mSelection, mRealizedModel, false );
        else if ( action.equals( "heptagonDivide" ) )
            edit = new HeptagonSubdivision( mSelection, mRealizedModel, false );
        else if ( action.equals( "crossProduct" ) )
            edit = new CrossProduct( mSelection, mRealizedModel, false );
        else if ( action.equals( "centroid" ) )
            edit = new Centroid( mSelection, mRealizedModel, false );
        else if ( action.equals( "showHidden" ) )
            edit = new ShowHidden( mSelection, mRealizedModel, false );
        else if ( action.equals( RealizeMetaParts.NAME ) )
            edit = new RealizeMetaParts( mSelection, mRealizedModel );
        else if ( action.equals( "affinePentagon" ) )
            edit = new AffinePentagon( mSelection, mRealizedModel );
        else if ( action.equals( "affineHeptagon" ) )
            edit = new AffineHeptagon( mSelection, mRealizedModel );
        else if ( action.equals( "affineTransformAll" ) )
        	edit = new AffineTransformAll( mSelection, mRealizedModel, mEditorModel.getCenterPoint(), false );
        else if ( action.equals( "dodecagonsymm" ) )
            edit = new DodecagonSymmetry( mSelection, mRealizedModel, mEditorModel.getCenterPoint(), false );
        else if ( action.equals( "ghostsymm24cell" ) )
            edit = new GhostSymmetry24Cell( mSelection, mRealizedModel, mEditorModel.getSymmetrySegment(), false );
		else if ( action.equals( "apiProxy" ) )
			edit = new ApiEdit( this .mSelection, this .mRealizedModel, this .originPoint );

		else if ( action.startsWith( "polytope_" ) )
        {
            int beginIndex = "polytope_".length();
            String group = action.substring( beginIndex, beginIndex + 2 );
            String suffix = action.substring( beginIndex + 2 );
            System.out.println( "computing " + group + " " + suffix );
            int index = Integer.parseInt( suffix, 2 );
            edit = new Polytope4d( mSelection, mRealizedModel, mEditorModel.getSymmetrySegment(),
                    index, group, false );
        }

        if ( edit == null )
        {
        	logger .warning( "no DocumentModel action for : " + action );
        	return false;
        }
        this .performAndRecord( edit );
        return true;
    }

    @Override
	public void performAndRecord( UndoableEdit edit )
	{
        if ( edit == null )
            return;
        if ( edit instanceof NoOp )
        	return;

        try {
            synchronized ( this .mHistory ) {
                edit .perform();
                this .mHistory .mergeSelectionChanges();
                this .mHistory .addEdit( edit, DocumentModel.this );
            }
        }
        catch ( RuntimeException re )
        {
            Throwable cause = re.getCause();
            if ( cause instanceof Command.Failure )
            	this .failures .reportFailure( (Command.Failure) cause );
            else if ( cause != null )
            	this .failures .reportFailure( new Command.Failure( cause ) );
            else
            	this .failures .reportFailure( new Command.Failure( re ) );
        }
        catch ( Command.Failure failure )
        {
        	this .failures .reportFailure( failure );
        }
        this .changes++;
    }

	public void setParameter( Construction singleConstruction, String paramName ) throws Command.Failure
	{
    	UndoableEdit edit = null;
    	if ( "ball" .equals( paramName ) )
    		edit = mEditorModel .setSymmetryCenter( singleConstruction );
    	else if ( "strut" .equals( paramName ) )
    		edit = mEditorModel .setSymmetryAxis( (Segment) singleConstruction );
    	if ( edit != null )
    		this .performAndRecord( edit );
	}
	
	public RealVector getLocation( Construction target )
	{
		if ( target instanceof Point)
			return ( (Point) target ).getLocation() .toRealVector();
		else if ( target instanceof Segment )
			return ( (Segment) target ).getStart() .toRealVector();
		else if ( target instanceof Polygon )
			return ( (Polygon) target ).getVertices()[ 0 ] .toRealVector();
		else
			return new RealVector( 0, 0, 0 );
	}

	public RealVector getParamLocation( String string )
	{
		if ( "ball" .equals( string ) )
		{
	    	Point ball = mEditorModel .getCenterPoint();
	    	return ball .getLocation() .toRealVector();
		}
		return new RealVector( 0, 0, 0 );
	}

    public void selectCollinear( Strut strut )
    {
        UndoableEdit edit = new SelectCollinear( mSelection, mRealizedModel, strut );
        this .performAndRecord( edit );
    }
    
    public void selectParallelStruts( Strut strut )
    {
        UndoableEdit edit = new SelectParallelStruts( this.symmetrySystem, mSelection, mRealizedModel, strut );
        this .performAndRecord( edit );
    }

    public void selectSimilarStruts( Direction orbit, AlgebraicNumber length )
    {
        UndoableEdit edit = new SelectSimilarSizeStruts( this.symmetrySystem, orbit, length, mSelection, mRealizedModel );
        this .performAndRecord( edit );
    }
    
    public Color getSelectionColor()
    {
    	Manifestation last = null;
        for (Manifestation man : mSelection) {
            last = man;
        }
        return last == null ? null : last .getRenderedObject() .getColor();
    }
    
    public void finishLoading( boolean openUndone, boolean asTemplate ) throws Command.Failure
    {
    	if ( mXML == null )
    		return;

        // TODO: record the edition, version, and revision on the format, so we can report a nice
        //   error if we fail to understand some command in the history.  If the revision is
        //   greater than Version .SVN_REVISION:
        //    "This document was created using $file.edition $file.version, and contains commands that
        //      $Version.edition does not understand.  You may need a newer version of
        //      $Version.edition, or a copy of $file.edition $file.version."
        //   (Adjust that if $Version.edition == $file.edition, to avoid confusion.)

        String tns = mXML .getNamespaceURI();
        XmlSaveFormat format = XmlSaveFormat.getFormat( tns );
        if ( format == null )
            return; // already checked and reported version compatibility,
        // up in the constructor

        int scale = 0;
        String scaleStr = mXML .getAttribute( "scale" );
        if ( ! scaleStr .isEmpty() )
            scale = Integer.parseInt( scaleStr );
        OrbitSet.Field orbitSetField = new OrbitSet.Field()
        {
            @Override
            public OrbitSet getGroup( String name )
            {
                SymmetrySystem system = symmetrySystems .get( name );
            	return system .getOrbits();
            }

            @Override
            public QuaternionicSymmetry getQuaternionSet( String name )
            {
                return mField .getQuaternionSymmetry( name);
            }
        };
        
        String writerVersion = mXML .getAttribute( "version" );
        String buildNum = mXML .getAttribute( "buildNumber" );
        if ( buildNum != null )
        	writerVersion += " build " + buildNum;
        format .initialize( mField, orbitSetField, scale, writerVersion, new Properties() );

        Element hist = (Element) mXML .getElementsByTagName( "EditHistory" ) .item( 0 );
        if ( hist == null )
            hist = (Element) mXML .getElementsByTagName( "editHistory" ) .item( 0 );
        int editNum = Integer.parseInt( hist .getAttribute( "editNumber" ) );
        
        List<Integer> implicitSnapshots = new ArrayList<>();

        // if we're opening a template document, we don't want to inherit its lesson or saved views
        if ( !asTemplate )
        {
        	Map<String, Camera> viewPages = new HashMap<>();
            Element views = (Element) mXML .getElementsByTagName( "Viewing" ) .item( 0 );
            if ( views != null ) {
                // make a notes page for each saved view
                //  ("edited" property change will be fired, to trigger migration semantics)
            	// migrate saved views to notes pages
                NodeList nodes = views .getChildNodes();
                for ( int i = 0; i < nodes .getLength(); i++ ) {
                    Node node = nodes .item( i );
            		if ( node instanceof Element ) {
            			Element viewElem = (Element) node;
            			String name = viewElem .getAttribute( "name" );
            			if ( name != null && ! name .isEmpty() && ! "default" .equals( name ) )
            			{
                			Camera view = new Camera( viewElem );
            				viewPages .put( name, view ); // named view to migrate to a lesson page
            			}
            		}
            	}
            }

            Element notesXml = (Element) mXML .getElementsByTagName( "notes" ) .item( 0 );
            if ( notesXml != null ) 
            	lesson .setXml( notesXml, editNum, this .defaultView );
            
            // add migrated views to the end of the lesson
            for (Entry<String, Camera> namedView : viewPages .entrySet()) {
                lesson .addPage( namedView .getKey(), "This page was a saved view created by an older version of vZome.", namedView .getValue(), -editNum );
            }
            for (PageModel page : lesson) {
                int snapshot = page .getSnapshot();
                if ( ( snapshot < 0 ) && ( ! implicitSnapshots .contains(-snapshot) ) )
                    implicitSnapshots .add(-snapshot);
            }

            Collections .sort( implicitSnapshots );
            
            for (PageModel page : lesson) {
                int snapshot = page .getSnapshot();
                if ( snapshot < 0 )
                    page .setSnapshot( implicitSnapshots .indexOf(-snapshot) );
            }
        }

        UndoableEdit[] explicitSnapshots = null;
        if ( ! implicitSnapshots .isEmpty() )
        {
            Integer highest = implicitSnapshots .get( implicitSnapshots .size() - 1 );
            explicitSnapshots = new UndoableEdit[ highest + 1 ];
            for (int i = 0; i < implicitSnapshots .size(); i++)
            {
                Integer editNumInt = implicitSnapshots .get( i );
                explicitSnapshots[ editNumInt ] = new Snapshot( i, this );
            }
        }
        
        try {
            int lastDoneEdit = openUndone? 0 : Integer.parseInt( hist .getAttribute( "editNumber" ) );
            String lseStr = hist .getAttribute( "lastStickyEdit" );
            int lastStickyEdit = ( ( lseStr == null ) || lseStr .isEmpty() )? -1 : Integer .parseInt( lseStr );
            NodeList nodes = hist .getChildNodes();
            for ( int i = 0; i < nodes .getLength(); i++ ) {
                Node kid = nodes .item( i );
                if ( kid instanceof Element ) {
                    Element editElem = (Element) kid;
                    mHistory .loadEdit( format, editElem, this );
                }
            }
            mHistory .synchronize( lastDoneEdit, lastStickyEdit, explicitSnapshots );
        } catch ( Throwable t )
        {
        	String fileVersion = mXML .getAttribute( "coreVersion" );
        	if ( this .fileIsTooNew( fileVersion ) ) {
        		String message = "This file was authored with a newer version, " + format .getToolVersion( mXML );
            	throw new Command.Failure( message, t );
        	} else {
        		String message = "There was a problem opening this file.  Please send the file to bugs@vzome.com.";
            	throw new Command.Failure( message, t );
        	}
        }

        this .migrated = openUndone || format.isMigration() || ! implicitSnapshots .isEmpty();
    }
    
    boolean fileIsTooNew( String fileVersion )
    {
    	if ( fileVersion == null || "" .equals( fileVersion ) )
    		return false;
    	String[] fvTokens = fileVersion .split( "\\." );
    	String[] cvTokens = this .coreVersion .split( "\\." );
    	for (int i = 0; i < cvTokens.length; i++) {
    		try {
    			int codepart = Integer .parseInt( cvTokens[ i ] );
    			int filepart = Integer .parseInt( fvTokens[ i ] );
    			if ( filepart > codepart )
    				return true;
			} catch ( NumberFormatException e ) {
				return false;
			}
		}
    	return false;
    }

    public Element getDetailsXml( Document doc )
    {
        Element vZomeRoot = doc .createElementNS( XmlSaveFormat.CURRENT_FORMAT, "vzome:vZome" );
        vZomeRoot .setAttribute( "xmlns:vzome", XmlSaveFormat.CURRENT_FORMAT );
        vZomeRoot .setAttribute( "field", mField.getName() );
        Element result = mHistory .getDetailXml( doc );
        vZomeRoot .appendChild( result );
        return vZomeRoot;
    }

    /**
     * For backward-compatibility
     * @param out
     * @throws Exception
     */
    public void serialize( OutputStream out ) throws Exception
    {
    	Properties props = new Properties();
    	props .setProperty( "edition", "vZome" );
    	props .setProperty( "version", "5.0" );
    	this .serialize( out, props );
    }

    public void serialize( OutputStream out, Properties editorProps ) throws Exception
    {
    	DocumentBuilderFactory factory = DocumentBuilderFactory .newInstance();
    	factory .setNamespaceAware( true );
    	DocumentBuilder builder = factory .newDocumentBuilder();
        Document doc = builder .newDocument();

        Element vZomeRoot = doc .createElementNS( XmlSaveFormat.CURRENT_FORMAT, "vzome:vZome" );
        vZomeRoot .setAttribute( "xmlns:vzome", XmlSaveFormat.CURRENT_FORMAT );
        String value = editorProps .getProperty( "edition" );
        if ( value != null )
        	vZomeRoot .setAttribute( "edition", value );
        value = editorProps .getProperty( "version" );
        if ( value != null )
        	vZomeRoot .setAttribute( "version", value );
        value = editorProps .getProperty( "buildNumber" );
        if ( value != null )
        	vZomeRoot .setAttribute( "buildNumber", value );
        vZomeRoot .setAttribute( "coreVersion", this .coreVersion );
        vZomeRoot .setAttribute( "field", mField.getName() );

        Element childElement;
        {
            childElement = mHistory .getXml( doc );
            int edits = 0, lastStickyEdit=-1;
            for (UndoableEdit undoable : mHistory) {
                childElement .appendChild( undoable .getXml( doc ) );
                ++ edits;
                if ( undoable .isSticky() )
                    lastStickyEdit = edits;
            }
            childElement .setAttribute( "lastStickyEdit", Integer .toString( lastStickyEdit ) );
        }
        vZomeRoot .appendChild( childElement );
        doc .appendChild( vZomeRoot );

        childElement = lesson .getXml( doc );
        vZomeRoot .appendChild( childElement );

        childElement = sceneLighting .getXml( doc );
        vZomeRoot .appendChild( childElement );

        childElement = doc .createElement( "Viewing" );
        Element viewXml = this .defaultView .getXML( doc );
        childElement .appendChild( viewXml );
        vZomeRoot .appendChild( childElement );

        childElement = this .symmetrySystem .getXml( doc );
        vZomeRoot .appendChild( childElement );

        DomUtils .serialize( doc, out );
    }
    
    public void doScriptAction( String command, String script )
    {
    	UndoableEdit edit = null;
    	if ( command.equals( "runZomicScript" ) || command.equals( "zomic" ) )
    	{
    		edit = new RunZomicScript( mSelection, mRealizedModel, script, mEditorModel.getCenterPoint() );
    		this .performAndRecord( edit );
    	}
    	else if ( command.equals( "runPythonScript" ) || command.equals( "py" ) )
    	{
    		edit = new RunPythonScript( mSelection, mRealizedModel, script, mEditorModel.getCenterPoint() );
    		this .performAndRecord( edit );
    	}
    	//    else if ( command.equals( "import.zomod" ) )
    	//        edit = new RunZomodScript( mSelection, mRealizedModel, script, mEditorModel.getCenterPoint(), mField .getSymmetry( "icosahedral" ) );
    	else if ( command.equals( "import.vef" ) || command.equals( "vef" ) )
    	{
    		Segment symmAxis = mEditorModel .getSymmetrySegment();
    		AlgebraicVector quat = ( symmAxis == null ) ? null : symmAxis.getOffset();
    		if ( quat != null )
    			quat = quat .scale( mField .createPower( - 5 ) );
    		AlgebraicNumber scale = mField .createPower( 5 );
    		edit = new LoadVEF( mSelection, mRealizedModel, script, quat, scale );
    		this .performAndRecord( edit );
    	}
    }

    @Override
    public void addTool( Tool tool )
    {
    	String name = tool .getName();
    	tools .put( name, tool );
        firePropertyChange( "tool.instances", null, name );
    }

    @Override
    public Tool findEquivalent( Tool tool )
    {
    	for ( Map.Entry<String, Tool> entry : tools .entrySet() )
    	{
    		if ( entry .getValue() .equals( tool ) )
    			return entry .getValue();
		}
    	return null;
    }

    @Override
    public Tool getTool( String toolName )
    {
    	return tools .get( toolName );
    }

    public AlgebraicField getField()
    {
    	return this .mField;
    }

	public void addSelectionListener( ManifestationChanges listener )
	{
		this .mSelection .addListener( listener );
	}

	private static final NumberFormat FORMAT = NumberFormat .getNumberInstance( Locale .US );

    /**
     * @deprecated As of 8/11/2016:
     * This code will continue to function properly,
     * but its functionality has been replicated in vzome-desktop.
     *
     * Formatting such information for display should not be a function of vzome-core.
     *
     * When all references to this method have been replaced,
     *   then it should be removed from vzome-core.
     */
    @Deprecated
    public String getManifestationProperties( Manifestation man, OrbitSource symmetry )
	{
        if ( man instanceof Connector )
        {
            StringBuffer buf;
            AlgebraicVector loc = man .getLocation();

            System .out .println( loc .getVectorExpression( AlgebraicField.EXPRESSION_FORMAT ) );
            System .out .println( loc .getVectorExpression( AlgebraicField.ZOMIC_FORMAT ) );
            System .out .println( loc .getVectorExpression( AlgebraicField.VEF_FORMAT ) );
            
            buf = new StringBuffer();
            buf .append( "location: " );
            loc .getVectorExpression( buf, AlgebraicField.DEFAULT_FORMAT );
            return buf.toString();
        }
        else if ( man instanceof Strut ) {
            StringBuffer buf = new StringBuffer();
            buf.append( "start: " );
            Strut strut = Strut.class.cast(man);
            strut .getLocation() .getVectorExpression( buf, AlgebraicField.DEFAULT_FORMAT );
            buf.append( "\n\noffset: " );
            AlgebraicVector offset = strut .getOffset();

            System .out .println( offset .getVectorExpression( AlgebraicField.EXPRESSION_FORMAT ) );
            System .out .println( offset .getVectorExpression( AlgebraicField.ZOMIC_FORMAT ) );
            System .out .println( offset .getVectorExpression( AlgebraicField.VEF_FORMAT ) );
            
            offset .getVectorExpression( buf, AlgebraicField.DEFAULT_FORMAT );
            buf.append( "\n\nnorm squared: " );
            AlgebraicNumber normSquared = offset .dot( offset );
            double norm2d = normSquared .evaluate();
            normSquared .getNumberExpression( buf, AlgebraicField.DEFAULT_FORMAT );
            buf.append( " = " );
            buf.append( FORMAT.format( norm2d ) );

            if ( offset .isOrigin() )
                return "zero length!";
            Axis zone = symmetry .getAxis( offset );
            
            AlgebraicNumber len = zone .getLength( offset );
            len = zone .getOrbit() .getLengthInUnits( len );

            buf.append( "\n\nlength in orbit units: " );
            len .getNumberExpression( buf, AlgebraicField.DEFAULT_FORMAT );

            if ( mField instanceof PentagonField)
            {
                buf.append( "\n\nlength in Zome b1 struts: " );
                if (FORMAT instanceof DecimalFormat) {
                    ((DecimalFormat) FORMAT) .applyPattern( "0.0000" );
                }
                buf.append( FORMAT.format( Math.sqrt( norm2d ) / PentagonField.B1_LENGTH ) );
            }
            return buf .toString();
        }
        else if ( man instanceof Panel ) {
            Panel panel = Panel.class.cast(man);
            StringBuffer buf = new StringBuffer();

            buf .append( "vertices: " );
            buf .append( panel.getVertexCount() );

            String delim = "";
            for( AlgebraicVector vertex : panel) {
                buf.append(delim);
                buf.append("\n  ");
                vertex .getVectorExpression( buf, AlgebraicField.DEFAULT_FORMAT );
                delim = ",";
            }

            AlgebraicVector normal = panel .getNormal();
            buf .append( "\n\nnormal: " );
            normal .getVectorExpression( buf, AlgebraicField.DEFAULT_FORMAT );

            buf.append("\n\nnorm squared: ");
            AlgebraicNumber normSquared = normal.dot(normal);
            double norm2d = normSquared.evaluate();
            normSquared.getNumberExpression(buf, AlgebraicField.DEFAULT_FORMAT);
            buf.append(" = ");
            buf.append(FORMAT.format(norm2d));

            Axis zone = symmetry .getAxis( normal );
            Direction direction = zone.getDirection();
            buf.append( "\n\ndirection: " );
            if( direction.isAutomatic() ) {
                buf.append( "Automatic " );
            }
            buf.append( direction.getName() );


            return buf.toString();
        }
        return man.getClass().getSimpleName();
    }

	public void undo( boolean useBlocks )
	{
		mHistory .undo( useBlocks );
	}

	public void redo( boolean useBlocks ) throws Command.Failure
	{
		mHistory .redo( useBlocks );
	}

	public void undo()
	{
		mHistory .undo();
	}

	public void redo() throws Command.Failure
	{
		mHistory .redo();
	}

	public void undoToBreakpoint()
	{
		mHistory .undoToBreakpoint();
	}

	public void undoToManifestation( Manifestation man )
	{
		mHistory .undoToManifestation( man );
	}

	public void redoToBreakpoint() throws Command.Failure
	{
		mHistory .redoToBreakpoint();
	}

	public void setBreakpoint()
	{
		mHistory .setBreakpoint();
	}

	public void undoAll()
	{
		mHistory .undoAll();
	}

	public void redoAll( int i ) throws Command .Failure
	{
		mHistory .redoAll( i );
	}

    public UndoableEdit deselectAll()
    {
        return mEditorModel .unselectAll();
    }

    public UndoableEdit selectManifestation( Manifestation target, boolean replace )
    {
        return mEditorModel .selectManifestation( target, replace );
    }

    public void createStrut( Point point, Axis zone, AlgebraicNumber length )
    {
    	UndoableEdit edit = new StrutCreation( point, zone, length, this .mRealizedModel );
        this .performAndRecord( edit );
    }
    
    public boolean isToolEnabled( String group, Symmetry symmetry )
    {
        Tool tool = (Tool) makeTool( group + ".0/UNUSED", group, null, symmetry ); // TODO: get rid of isAutomatic() and isTetrahedral() silliness, so the string won't get parsed
    	return tool != null && tool .isValidForSelection();
    }

	public void createTool( String name, String group, Tool.Registry tools, Symmetry symmetry )
	{
        UndoableEdit edit = makeTool( name, group, tools, symmetry );
        performAndRecord( edit );
	}
	
	private UndoableEdit makeTool( String name, String group, Tool.Registry tools, Symmetry symmetry )
	{
        UndoableEdit edit = null;
        switch (group) {

        case "bookmark":
			edit = new BookmarkTool( name, mSelection, mRealizedModel, tools );
			break;
		case "point reflection":
			edit = new InversionTool( name, mSelection, mRealizedModel, tools, originPoint );
			break;
		case "mirror":
			edit = new MirrorTool( name, mSelection, mRealizedModel, tools, originPoint );
			break;
		case "translation":
			edit = new TranslationTool( name, mSelection, mRealizedModel, tools, originPoint );
			break;
		case "linear map":
			edit = new LinearMapTool( name, mSelection, mRealizedModel, tools, originPoint, false );
			break;
		case "rotation":
			edit = new RotationTool( name, symmetry, mSelection, mRealizedModel, tools, originPoint, false );
			break;
		case "axial symmetry":
			edit = new RotationTool( name, symmetry, mSelection, mRealizedModel, tools, originPoint, true );
			break;
		case "scaling":
			edit = new ScalingTool( name, symmetry, mSelection, mRealizedModel, tools, originPoint );
			break;
		case "scale up":
			edit = new ScalingTool( name, tools, getField() .createPower( 1 ), originPoint );
			break;
		case "scale down":
			edit = new ScalingTool( name, tools, getField() .createPower( -1 ), originPoint );
			break;
		case "tetrahedral":
			edit = new SymmetryTool( name, symmetry, mSelection, mRealizedModel, tools, originPoint );
			break;
		case "module":
			edit = new ModuleTool( name, mSelection, mRealizedModel, tools );
			break;
		case "plane":
			edit = new PlaneSelectionTool( name, mSelection, mField, tools );
			break;
		case "yellowstretch":
			if ( symmetry instanceof IcosahedralSymmetry )
				edit = new AxialStretchTool( name, (IcosahedralSymmetry) symmetry, mSelection, mRealizedModel, tools, true, false, false );
			break;
		case "yellowsquash":
			if ( symmetry instanceof IcosahedralSymmetry )
				edit = new AxialStretchTool( name, (IcosahedralSymmetry) symmetry, mSelection, mRealizedModel, tools, false, false, false );
			break;
		case "redstretch1":
			if ( symmetry instanceof IcosahedralSymmetry )
				edit = new AxialStretchTool( name, (IcosahedralSymmetry) symmetry, mSelection, mRealizedModel, tools, true, true, true );
			break;
		case "redsquash1":
			if ( symmetry instanceof IcosahedralSymmetry )
				edit = new AxialStretchTool( name, (IcosahedralSymmetry) symmetry, mSelection, mRealizedModel, tools, false, true, true );
			break;
		case "redstretch2":
			if ( symmetry instanceof IcosahedralSymmetry )
				edit = new AxialStretchTool( name, (IcosahedralSymmetry) symmetry, mSelection, mRealizedModel, tools, true, true, false );
			break;
		case "redsquash2":
			if ( symmetry instanceof IcosahedralSymmetry )
				edit = new AxialStretchTool( name, (IcosahedralSymmetry) symmetry, mSelection, mRealizedModel, tools, false, true, false );
			break;
		default:
			edit = new SymmetryTool( name, symmetry, mSelection, mRealizedModel, tools, originPoint );
			break;
		}
        return edit;
	}

	public Exporter3d getNaiveExporter( String format, Camera view, Colors colors, Lights lights, RenderedModel currentSnapshot )
	{
        Exporter3d exporter = null;
        if ( format.equals( "pov" ) )
            exporter = new POVRayExporter( view, colors, lights, currentSnapshot );
        else if ( format.equals( "opengl" ) )
        	exporter = new OpenGLExporter( view, colors, lights, currentSnapshot );

        boolean inArticleMode = (renderedModel != currentSnapshot);
        if(exporter != null && exporter.needsManifestations() && inArticleMode ) {
            throw new IllegalStateException("The " + format + " exporter can only operate on the current model, not article pages.");
        }
        return exporter;
    }

	/*
	 * These exporters fall in two categories: rendering and geometry.  The ones that support the currentSnapshot
	 * (the current article page, or the main model) can do rendering export, and can work with just a rendered
	 * model (a snapshot), which has lost its attached Manifestation objects.
	 * 
	 * The ones that require mRenderedModel need access to the RealizedModel objects hanging from it (the
	 * Manifestations).  These are the geometry exporters.  They can be aware of the structure of field elements,
	 * as well as the orbits and zones.
	 * 
	 * POV-Ray is a bit of a special case, but only because the .pov language supports coordinate values as expressions,
	 * and supports enough modeling that the different strut shapes can be defined, and so on.
     * OpenGL and WebGL (Web3d/json) could as well, since I can control how the data is stored and rendered.
	 * 
	 * The POV-Ray export reuses shapes, etc. just as vZome does, so really works just with the RenderedManifestations
	 * (except when the Manifestation is available for structured coordinate expressions).  Again, any rendering exporter
	 * could apply the same reuse tricks, working just with RenderedManifestations, so the current limitations to
	 * mRenderedModel for many of these is spurious.
     *
     * The base Exporter3d class now has a boolean needsManifestations() method which subclasses should override
     * if they don't rely on Manifestations and therefore can operate on article pages.
     */
	
	// TODO move all the parameters inside this object!
	
	public Exporter3d getStructuredExporter( String format, Camera view, Colors colors, Lights lights, RenderedModel mRenderedModel )
	{
        if ( format.equals( "partgeom" ) )
        	return new PartGeometryExporter( view, colors, lights, mRenderedModel, mSelection );
        else
        	return null;
	}

	public LessonModel getLesson()
	{
		return lesson;
	}

    @Override
    public void recordSnapshot( int id )
    {
    	RenderedModel snapshot = ( renderedModel == null )? null : renderedModel .snapshot();
    	if ( thumbnailLogger .isLoggable( Level.FINER ) )
    		thumbnailLogger .finer( "recordSnapshot: " + id );
    	numSnapshots = Math .max( numSnapshots, id + 1 );
    	if ( id >= snapshots.length )
    	{
    		int newLength = Math .max( 2 * snapshots .length, numSnapshots );
    		snapshots = Arrays .copyOf( snapshots, newLength );
    	}
    	snapshots[ id ] = snapshot;
    }

    @Override
	public void actOnSnapshot( int id, SnapshotAction action )
	{
        RenderedModel snapshot = snapshots[ id ];
        action .actOnSnapshot( snapshot );
	}

	public void addSnapshotPage( Camera view )
	{
        int id = numSnapshots;
        this .performAndRecord( new Snapshot( id, this ) );
        lesson .newSnapshotPage( id, view );
	}

	public RenderedModel getRenderedModel()
	{
		return this .renderedModel;
	}
	
	public Camera getViewModel()
	{
	    return this .defaultView;
	}

    public void generatePolytope( String group, String renderGroup, int index, int edgesToRender, AlgebraicVector quaternion, AlgebraicNumber[] edgeScales )
    {
        UndoableEdit edit = new Polytope4d( mSelection, mRealizedModel, quaternion, index, group, edgesToRender, edgeScales, renderGroup );
        this .performAndRecord( edit );
    }

    public void generatePolytope( String group, String renderGroup, int index, int edgesToRender, AlgebraicNumber[] edgeScales )
    {
        UndoableEdit edit = new Polytope4d( mSelection, mRealizedModel, mEditorModel.getSymmetrySegment(), index, group, edgesToRender, edgeScales, renderGroup );
        this .performAndRecord( edit );
    }
    
    public Segment getSelectedSegment()
    {
    	return (Segment) mEditorModel .getSelectedConstruction( Segment.class );
    }
    
    public Segment getSymmetryAxis()
    {
    	return mEditorModel .getSymmetrySegment();
    }

	public Segment getPlaneAxis( Polygon panel )
	{
		AlgebraicVector[] vertices = panel.getVertices();
		FreePoint p0 = new FreePoint( vertices[ 0 ] );
		FreePoint p1 = new FreePoint( vertices[ 1 ] );
		FreePoint p2 = new FreePoint( vertices[ 2 ] );
		Segment s1 = new SegmentJoiningPoints( p0, p1 );
		Segment s2 = new SegmentJoiningPoints( p1, p2 );
		return new SegmentCrossProduct( s1, s2 );
	}

	public RealizedModel getRealizedModel()
	{
		return this .mRealizedModel;
	}

    public Iterable<Tool> getTools()
    {
        return this .tools .values();
    }
    
    public SymmetrySystem getSymmetrySystem()
    {
        return this .symmetrySystem;
    }
    
    public SymmetrySystem getSymmetrySystem( String name )
    {
        return this .symmetrySystems .get( name );
    }
    
    public void setSymmetrySystem( String name )
    {
    	this .symmetrySystem = this .symmetrySystems .get( name );
    }
}
