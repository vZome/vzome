
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.core.editor;

import static com.vzome.core.editor.ChangeSelection.ActionEnum.DESELECT;
import static com.vzome.core.editor.ChangeSelection.ActionEnum.IGNORE;
import static com.vzome.core.editor.ChangeSelection.ActionEnum.SELECT;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.OutputStream;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
import com.vzome.core.editor.ManifestationColorMappers.CentroidNearestSpecialOrbitColorMap;
import com.vzome.core.editor.ManifestationColorMappers.ManifestationColorMapper;
import com.vzome.core.editor.ManifestationColorMappers.NearestSpecialOrbitColorMap;
import com.vzome.core.editor.ManifestationColorMappers.SystemCentroidColorMap;
import com.vzome.core.editor.ManifestationColorMappers.SystemColorMap;
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

public class DocumentModel implements Snapshot .Recorder, UndoableEdit .Context
{
	private final RealizedModel mRealizedModel;

	private final Point originPoint;

	private final Selection mSelection;

	private final EditorModel mEditorModel;
	
    private final EditHistory mHistory;
    
    private final LessonModel lesson = new LessonModel();
    
	private final AlgebraicField field;
	
	private final ToolsModel tools;
	
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

	private final FieldApplication kind;

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

	public DocumentModel( final FieldApplication kind, Command.FailureChannel failures, Element xml, final Application app )
	{
		super();
		this .kind = kind;
		this .field = kind .getField();
		AlgebraicVector origin = this .field .origin( 3 );
		this .originPoint = new FreePoint( origin );
		this .failures = failures;
		this .mXML = xml;
		this .commands = app .getCommands();
		this .sceneLighting = app .getLights();
		
		this .coreVersion = app .getCoreVersion();

		this .mRealizedModel = new RealizedModel( this .field, new Projection.Default( this .field ) );
		
		this .mSelection = new Selection();

		if ( xml != null ) {
	        NodeList nl = xml .getElementsByTagName( "SymmetrySystem" );
	        if ( nl .getLength() != 0 )
	            xml = (Element) nl .item( 0 );
	        else
	            xml = null;
		}
		FieldApplication.SymmetryPerspective symmPerspective = kind .getDefaultSymmetryPerspective();
        if ( xml != null ) {
            String symmName = xml .getAttribute( "name" );	
            symmPerspective = kind .getSymmetryPerspective( symmName );
        }
		
		this .tools = new ToolsModel( this, this .originPoint );

        Collection<FieldApplication.SymmetryPerspective> symms = kind .getSymmetryPerspectives();
        for ( FieldApplication.SymmetryPerspective symmPerspective1 : symms )
        {
            SymmetrySystem osm = new SymmetrySystem( null, symmPerspective1, app .getColors(), true );
            // one of these will be overwritten below, if we are loading from a file that has it set
            this .symmetrySystems .put( osm .getName(), osm );
        }

        SymmetrySystem symmetrySystem = new SymmetrySystem( xml, symmPerspective, app .getColors(), true );
        this .symmetrySystems .put( symmPerspective .getName(), symmetrySystem );

        this .renderedModel = new RenderedModel( this .field, symmetrySystem );

		this .mRealizedModel .addListener( this .renderedModel ); // just setting the default
		// the renderedModel must either be disabled, or have shapes here, so the origin ball gets rendered
		this .mEditorModel = new EditorModel( this .mRealizedModel, this .mSelection, /*oldGroups*/ false, originPoint, symmetrySystem );
		this .tools .setEditorModel( this .mEditorModel );
		
		// cannot be done in the constructors
		for ( SymmetrySystem symmetrySys : this .symmetrySystems .values()) {
			symmetrySys .createToolFactories( this .tools );
		}

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

		switch (name) {
		case "Snapshot":
			edit = new Snapshot( -1, this );
			break;
		case "Branch":
			edit = new Branch( this );
			break;
		case "Delete":
			edit = new Delete( this.mSelection, this.mRealizedModel );
			break;
		case "ShowPoint":
			edit = new ShowPoint( null, this.mSelection, this.mRealizedModel, groupInSelection );
			break;
		case "setItemColor":
			edit = new ColorManifestations( this.mSelection, this.mRealizedModel, null, groupInSelection );
			break;
		case "MapToColor":
			// Different Color Mappers require different parameters, including symmetry,
			//  so they are all handled together in getColorMapper()
			// See the note below about deserializing symmetry from XML.
			 edit = getColorMapper(xml.getAttribute("colorMapper"), xml.getAttribute("symmetry"));
			break;
		case "ShowHidden":
			edit = new ShowHidden( this.mSelection, this.mRealizedModel, groupInSelection );
			break;
		case "CrossProduct":
			edit = new CrossProduct( this.mSelection, this.mRealizedModel, groupInSelection );
			break;
		case "AffinePentagon":
			edit = new AffinePentagon( this.mSelection, this.mRealizedModel, groupInSelection );
			break;
		case "AffineHeptagon":
			edit = new AffineHeptagon( this.mSelection, this.mRealizedModel );
			break;
		case "AffineTransformAll":
			edit = new AffineTransformAll( this.mSelection, this.mRealizedModel, this.mEditorModel.getCenterPoint(), groupInSelection );
			break;
		case "HeptagonSubdivision":
			edit = new HeptagonSubdivision( this.mSelection, this.mRealizedModel, groupInSelection );
			break;
		case "DodecagonSymmetry":
			edit = new DodecagonSymmetry( this.mSelection, this.mRealizedModel, this.mEditorModel.getCenterPoint(), this .mEditorModel .getSymmetrySystem() .getSymmetry(), groupInSelection );
			break;
		case "GhostSymmetry24Cell":
			edit = new GhostSymmetry24Cell( this.mSelection, this.mRealizedModel, this.mEditorModel.getSymmetrySegment(), this .mEditorModel .getSymmetrySystem() .getSymmetry(), groupInSelection );
			break;
		case "StrutCreation":
			edit = new StrutCreation( null, null, null, this.mRealizedModel );
			break;
		case "BnPolyope":
		case "B4Polytope":
			edit = new B4Polytope( this.mSelection, this.mRealizedModel, this .field, null, 0, groupInSelection );
			break;
		case "Polytope4d":
			edit = new Polytope4d( this.mSelection, this.mRealizedModel, null, 0, null, groupInSelection );
			break;
		case "LoadVEF":
			edit = new LoadVEF( this.mSelection, this.mRealizedModel, null, null, null );
			break;
		case "GroupSelection":
			edit = new GroupSelection( this.mSelection, false );
			break;
		case "BeginBlock":
			edit = new BeginBlock();
			break;
		case "EndBlock":
			edit = new EndBlock();
			break;
		case "InvertSelection":
			edit = new InvertSelection( this.mSelection, this.mRealizedModel, groupInSelection );
			break;
		case "JoinPoints":
			edit = new JoinPoints( this.mSelection, this.mRealizedModel, groupInSelection );
			break;
		case "NewCentroid":
			edit = new Centroid( this.mSelection, this.mRealizedModel, groupInSelection );
			break;
		case "StrutIntersection":
			edit = new StrutIntersection( this.mSelection, this.mRealizedModel, groupInSelection );
			break;
		case "LinePlaneIntersect":
			edit = new LinePlaneIntersect( this.mSelection, this.mRealizedModel, groupInSelection );
			break;
		case "SelectAll":
			edit = new SelectAll( this.mSelection, this.mRealizedModel, groupInSelection );
			break;
		case "DeselectAll":
			edit = new DeselectAll( this.mSelection, groupInSelection );
			break;
		case "AdjustSelectionByClass":
		case "DeselectByClass": // legacy command now handled by AdjustSelectionByClass
			edit = new AdjustSelectionByClass( this.mSelection, this.mRealizedModel, IGNORE, IGNORE, IGNORE);
			break;
		case "SelectManifestation":
			edit = new SelectManifestation( null, false, this.mSelection, this.mRealizedModel, groupInSelection );
			break;
		case "SelectNeighbors":
			edit = new SelectNeighbors( this.mSelection, this.mRealizedModel, groupInSelection );
			break;
		case "AdjustSelectionByOrbitLength":
		case "SelectSimilarSize": // legacy command now handled by AdjustSelectionByOrbitLength
			/*
            The normal pattern is to have the edits deserialize their own parameters from the XML in setXmlAttributes()
            but in the case of persisting the symmetry, it must be read here and passed into the c'tor
            since this is where the map from a name to an actual SymmetrySystem is maintained.
            These edits are still responsible for saving the symmetry name to XML in getXmlAttributes().
            Also see the comments of commit # 8c8cb08a1e4d71f91f24669b203fef0378230b19 on 3/8/2015
            */

			edit = new AdjustSelectionByOrbitLength( this .symmetrySystems .get( xml .getAttribute( "symmetry" ) ),
                    null, null, this .mSelection, this .mRealizedModel, IGNORE, IGNORE );
			break;
		case "SelectParallelStruts":
			// See the note above about deserializing symmetry from XML.
			edit = new SelectParallelStruts( this .symmetrySystems .get( xml .getAttribute( "symmetry" ) ),
                    this .mSelection, this .mRealizedModel );
			break;
		case "SelectAutomaticStruts":
			// See the note above about deserializing symmetry from XML.
			edit = new SelectAutomaticStruts( this .symmetrySystems .get( xml .getAttribute( "symmetry" ) ),
                    this .mSelection, this .mRealizedModel );
			break;
		case "SelectCollinear":
			edit = new SelectCollinear( this .mSelection, this .mRealizedModel );
			break;
		case "ValidateSelection":
			edit = new ValidateSelection( this.mSelection );
			break;
		case "SymmetryCenterChange":
			edit = new SymmetryCenterChange( this.mEditorModel, null );
			break;
		case "SymmetryAxisChange":
			edit = new SymmetryAxisChange( this.mEditorModel, null );
			break;
		case "RunZomicScript":
			edit = new RunZomicScript( this.mSelection, this.mRealizedModel, null, mEditorModel.getCenterPoint(),
						(IcosahedralSymmetry) this .mEditorModel .getSymmetrySystem() .getSymmetry() );
			break;
		case "RunPythonScript":
			edit = new RunPythonScript( this.mSelection, this.mRealizedModel, null, mEditorModel.getCenterPoint() );
			break;
		case RealizeMetaParts.NAME:
			edit = new RealizeMetaParts( mSelection, mRealizedModel );
			break;
		case ShowVertices.NAME:
			edit = new ShowVertices( mSelection, mRealizedModel );
			break;
		case "Symmetry4d":
			QuaternionicSymmetry h4symm = this .kind .getQuaternionSymmetry( "H_4" );
			edit = new Symmetry4d( this.mSelection, this.mRealizedModel, h4symm, h4symm );
			break;
		}
		if ( edit != null ) return edit;

		edit = this .tools .createEdit( xml );

		if ( edit != null ) return edit;

		for ( SymmetrySystem system : this .symmetrySystems .values() ) {
			edit = system .createToolEdit( xml );
			if ( edit != null ) return edit;
		}

		// any command unknown (i.e. from a newer version of vZome) becomes a CommandEdit
		edit = new CommandEdit( null, mEditorModel, groupInSelection );
		
		return edit;
	}
	
	private MapColors getColorMapper(String mapperName, String symmetrySystemName) {
        SymmetrySystem symm = symmetrySystems.get(symmetrySystemName);
        ManifestationColorMapper colorMapper
                = mapperName.equals("SystemColorMap") ? new SystemColorMap(symm)
                : mapperName.equals("SystemCentroidColorMap") ? new SystemCentroidColorMap(symm)
                : mapperName.equals("NearestSpecialOrbitColorMap") ? new NearestSpecialOrbitColorMap(symm)
                : mapperName.equals("CentroidNearestSpecialOrbitColorMap") ? new CentroidNearestSpecialOrbitColorMap(symm)
                : ManifestationColorMappers.getColorMapper(mapperName);
        
        if (colorMapper == null) {
            // Provide a do-nothing placeholder for an unrecognized (future???) mapper
            colorMapper = new ManifestationColorMappers.Identity();
            logger.warning("Substituting " + colorMapper.getName() + " for unknown mapperName " + mapperName);
        }
        return new MapColors(mSelection, mRealizedModel, colorMapper);
    }
	
	public String copySelectionVEF()
	{
		StringWriter out = new StringWriter();
		Exporter exporter = new VefModelExporter( out, field );
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
    	
//      if ( action.equals( "sixLattice" ) )
//      edit = new SixLattice( mSelection, mRealizedModel, mDerivationModel );

  // not supported currently, so I don't have to deal with the mTargetManifestation problem
//  if ( action .equals( "reversePanel" ) )
//      edit = new ReversePanel( mTargetManifestation, mSelection, mRealizedModel, mDerivationModel );

        UndoableEdit edit = null;
        switch (action) {
		case "selectAll":
			edit = mEditorModel.selectAll();
			break;
		case "unselectAll":
			edit = mEditorModel.unselectAll();
			break;
		case "unselectBalls":
			edit = mEditorModel.unselectConnectors(); // legacy
			break;
		case "unselectStruts":
			edit = mEditorModel.unselectStrutsAndPanels(); // legacy
			break;
		case "deselectBalls":
			edit = mEditorModel.deselectConnectors();
			break;
		case "deselectStruts":
			edit = mEditorModel.deselectStruts();
			break;
		case "deselectPanels":
			edit = mEditorModel.deselectPanels();
			break;
		case "selectBalls":
			edit = mEditorModel.selectConnectors();
			break;
		case "selectStruts":
			edit = mEditorModel.selectStruts();
			break;
		case "selectPanels":
			edit = mEditorModel.selectPanels();
			break;
		case "selectNeighbors":
			edit = mEditorModel.selectNeighbors();
			break;
		case "SelectAutomaticStruts":
			edit = mEditorModel.selectAutomaticStruts();
			break;
		case "SelectCollinear":
			edit = mEditorModel.selectCollinear();
			break;
		case "SelectParallelStruts":
			edit = mEditorModel.selectParallelStruts();
			break;
		case "invertSelection":
			edit = mEditorModel.invertSelection();
			break;
		case "group":
			edit = mEditorModel.groupSelection();
			break;
		case "ungroup":
			edit = mEditorModel.ungroupSelection();
			break;
		case "assertSelection":
			edit = new ValidateSelection( mSelection );
			break;
		case "delete":
			edit = new Delete( mSelection, mRealizedModel );
			break;
		case "createStrut":
			edit = new StrutCreation( null, null, null, mRealizedModel );
			break;
		case "joinballs":
			edit = new JoinPoints( mSelection, mRealizedModel, false, JoinPoints.JoinModeEnum.CLOSED_LOOP );
			break;
		case "joinBallsAllToFirst":
			edit = new JoinPoints( mSelection, mRealizedModel, false, JoinPoints.JoinModeEnum.ALL_TO_FIRST );
			break;
		case "joinBallsAllToLast":
			edit = new JoinPoints( mSelection, mRealizedModel, false, JoinPoints.JoinModeEnum.ALL_TO_LAST );
			break;
		case "joinBallsAllPossible":
			edit = new JoinPoints( mSelection, mRealizedModel, false, JoinPoints.JoinModeEnum.ALL_POSSIBLE );
			break;
		case "ballAtOrigin":
			edit = new ShowPoint( originPoint, mSelection, mRealizedModel, false );
			break;
		case "ballAtSymmCenter":
			edit = new ShowPoint( mEditorModel.getCenterPoint(), mSelection, mRealizedModel, false );
			break;
		case "linePlaneIntersect":
			edit = new LinePlaneIntersect( mSelection, mRealizedModel, false );
			break;
		case "lineLineIntersect":
			edit = new StrutIntersection( mSelection, mRealizedModel, false );
			break;
		case "heptagonDivide":
			edit = new HeptagonSubdivision( mSelection, mRealizedModel, false );
			break;
		case "crossProduct":
			edit = new CrossProduct( mSelection, mRealizedModel, false );
			break;
		case "centroid":
			edit = new Centroid( mSelection, mRealizedModel, false );
			break;
		case "showHidden":
			edit = new ShowHidden( mSelection, mRealizedModel, false );
			break;
		case RealizeMetaParts.NAME:
			edit = new RealizeMetaParts( mSelection, mRealizedModel );
			break;
		case "affinePentagon":
			edit = new AffinePentagon( mSelection, mRealizedModel );
			break;
		case "affineHeptagon":
			edit = new AffineHeptagon( mSelection, mRealizedModel );
			break;
		case "affineTransformAll":
			edit = new AffineTransformAll( mSelection, mRealizedModel, mEditorModel.getCenterPoint(), false );
			break;
		case "dodecagonsymm":
			edit = new DodecagonSymmetry( mSelection, mRealizedModel, mEditorModel.getCenterPoint(),
					this .mEditorModel .getSymmetrySystem() .getSymmetry(), false );
			break;
		case "ghostsymm24cell":
			edit = new GhostSymmetry24Cell( mSelection, mRealizedModel, mEditorModel.getSymmetrySegment(), this .mEditorModel .getSymmetrySystem() .getSymmetry(), false );
			break;
		case "apiProxy":
			edit = new ApiEdit( this .mSelection, this .mRealizedModel, this .originPoint );
			break;

		default:
			if ( action.startsWith( "polytope_" ) ) {
	            int beginIndex = "polytope_".length();
	            String group = action.substring( beginIndex, beginIndex + 2 );
	            String suffix = action.substring( beginIndex + 2 );
	            System.out.println( "computing " + group + " " + suffix );
	            int index = Integer.parseInt( suffix, 2 );
	            edit = new Polytope4d( mSelection, mRealizedModel, mEditorModel.getSymmetrySegment(),
	                    index, group, false );
	    	} 
			else if ( action.startsWith( "setItemColor/" ) ) {
	            String value = action .substring( "setItemColor/" .length() );
	            edit = new ColorManifestations( mSelection, mRealizedModel, new Color( value ), false );
	    	} 
			else if ( action.startsWith( "MapToColor/" ) ) {
				String mapperName = action.substring("MapToColor/".length());
				String symmetrySystemName = mEditorModel .getSymmetrySystem().getName();
				edit = getColorMapper(mapperName, symmetrySystemName);
	        } 
			else if ( ShowVertices.NAME .toLowerCase() .equals( action .toLowerCase() ) ) {
	    		edit = new ShowVertices( mSelection, mRealizedModel );
	    	} 
			else if ( action .toLowerCase() .equals( "chainballs" ) ) {
	    		edit = new JoinPoints( mSelection, mRealizedModel, false, JoinPoints.JoinModeEnum.CHAIN_BALLS );
	    	}
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
                this .mEditorModel .notifyListeners();
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
			return this .renderedModel .renderVector( ( (Point) target ).getLocation() );
		else if ( target instanceof Segment )
			return this .renderedModel .renderVector( ( (Segment) target ).getStart() );
		else if ( target instanceof Polygon )
			return this .renderedModel .renderVector( ( (Polygon) target ).getVertices()[ 0 ] );
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
        UndoableEdit edit = new SelectParallelStruts( mEditorModel .getSymmetrySystem(), mSelection, mRealizedModel, strut );
        this .performAndRecord( edit );
    }

    public void selectSimilarStruts( Direction orbit, AlgebraicNumber length )
    {
        UndoableEdit edit = new AdjustSelectionByOrbitLength(mEditorModel .getSymmetrySystem(), orbit, length, mSelection, mRealizedModel, SELECT, IGNORE);
        this .performAndRecord( edit );
    }
    
    public void deselectSimilarStruts( Direction orbit, AlgebraicNumber length )
    {
        UndoableEdit edit = new AdjustSelectionByOrbitLength(mEditorModel .getSymmetrySystem(), orbit, length, mSelection, mRealizedModel, DESELECT, IGNORE);
        this .performAndRecord( edit );
    }

    public void selectSimilarPanels( Direction orbit )
    {
        UndoableEdit edit = new AdjustSelectionByOrbitLength(mEditorModel .getSymmetrySystem(), orbit, null, mSelection, mRealizedModel, IGNORE, SELECT);
        this .performAndRecord( edit );
    }

    public void deselectSimilarPanels( Direction orbit )
    {
        UndoableEdit edit = new AdjustSelectionByOrbitLength(mEditorModel .getSymmetrySystem(), orbit, null, mSelection, mRealizedModel, IGNORE, DESELECT);
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
                return kind .getQuaternionSymmetry( name);
            }
        };
        
        String writerVersion = mXML .getAttribute( "version" );
        String buildNum = mXML .getAttribute( "buildNumber" );
        if ( buildNum != null )
        	writerVersion += " build " + buildNum;
        format .initialize( field, orbitSetField, scale, writerVersion, new Properties() );

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
        vZomeRoot .setAttribute( "field", field.getName() );
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
        vZomeRoot .setAttribute( "field", field.getName() );

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

        childElement = this .mEditorModel .getSymmetrySystem() .getXml( doc );
        vZomeRoot .appendChild( childElement );

        DomUtils .serialize( doc, out );
    }
    
    public void doScriptAction( String command, String script )
    {
    	UndoableEdit edit = null;
    	if ( command.equals( "runZomicScript" ) || command.equals( "zomic" ) )
    	{
    		edit = new RunZomicScript( mSelection, mRealizedModel, script, mEditorModel.getCenterPoint(),
					(IcosahedralSymmetry) this .mEditorModel .getSymmetrySystem() .getSymmetry() );
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
    			quat = quat .scale( field .createPower( - 5 ) );
    		AlgebraicNumber scale = field .createPower( 5 );
    		edit = new LoadVEF( mSelection, mRealizedModel, script, quat, scale );
    		this .performAndRecord( edit );
    	}
    }

    public AlgebraicField getField()
    {
    	return this .field;
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

            if ( field instanceof PentagonField)
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
        this .mEditorModel .notifyListeners();
	}

	public void redo( boolean useBlocks ) throws Command.Failure
	{
		mHistory .redo( useBlocks );
        this .mEditorModel .notifyListeners();
	}

	public void undo()
	{
		mHistory .undo();
        this .mEditorModel .notifyListeners();
	}

	public void redo() throws Command.Failure
	{
		mHistory .redo();
        this .mEditorModel .notifyListeners();
	}

	public void undoToBreakpoint()
	{
		mHistory .undoToBreakpoint();
        this .mEditorModel .notifyListeners();
	}

	public void undoToManifestation( Manifestation man )
	{
		mHistory .undoToManifestation( man );
        this .mEditorModel .notifyListeners();
	}

	public void redoToBreakpoint() throws Command.Failure
	{
		mHistory .redoToBreakpoint();
        this .mEditorModel .notifyListeners();
	}

	public void setBreakpoint()
	{
		mHistory .setBreakpoint();
	}

	public void undoAll()
	{
		mHistory .undoAll();
        this .mEditorModel .notifyListeners();
	}

	public void redoAll( int i ) throws Command .Failure
	{
		mHistory .redoAll( i );
        this .mEditorModel .notifyListeners();
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

    public ToolsModel getToolsModel()
    {
        return this .tools;
    }
    
    public SymmetrySystem getSymmetrySystem()
    {
        return this .mEditorModel .getSymmetrySystem();
    }
    
    public SymmetrySystem getSymmetrySystem( String name )
    {
        return this .symmetrySystems .get( name );
    }
    
    public void setSymmetrySystem( String name )
    {
    	SymmetrySystem system = this .symmetrySystems .get( name );
    	this .mEditorModel .setSymmetrySystem( system );
    }

	public FieldApplication getFieldApplication()
	{
		return this .kind;
	}
}
