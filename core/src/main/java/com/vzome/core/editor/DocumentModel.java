
//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.core.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

import com.vzome.api.Tool;
import com.vzome.api.Tool.Factory;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.AbstractCommand;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.commands.XmlSymmetryFormat;
import com.vzome.core.construction.Color;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;
import com.vzome.core.editor.Snapshot.SnapshotAction;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.editor.api.OrbitSource;
import com.vzome.core.editor.api.UndoableEdit;
import com.vzome.core.exporters.Exporter3d;
import com.vzome.core.exporters.POVRayExporter;
import com.vzome.core.exporters.PartGeometryExporter;
import com.vzome.core.exporters.ShapesJsonExporter;
import com.vzome.core.exporters2d.Java2dExporter;
import com.vzome.core.exporters2d.Java2dSnapshot;
import com.vzome.core.exporters2d.SnapshotExporter;
import com.vzome.core.math.Projection;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.OrbitSet;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;
import com.vzome.core.model.ColoredMeshJson;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Exporter;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.ManifestationChanges;
import com.vzome.core.model.ManifestationImpl;
import com.vzome.core.model.Panel;
import com.vzome.core.model.RealizedModelImpl;
import com.vzome.core.model.Strut;
import com.vzome.core.model.VefModelExporter;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.tools.BookmarkTool;
import com.vzome.core.viewing.Camera;
import com.vzome.core.viewing.Lights;
import com.vzome.xml.DomSerializer;

public class DocumentModel implements Snapshot .Recorder, UndoableEdit .Context
{
    private final RealizedModelImpl mRealizedModel;

    private final Point originPoint;

    private final EditorModelImpl editorModel;

    private final EditHistory mHistory;

    private final LessonModel lesson = new LessonModel();

    private final AlgebraicField field;

    private final ToolsModel tools;

    private final Command.FailureChannel failures;

    private int changes = 0;

    private boolean migrated = false;

    private final Element mXML;

    private RenderedModel renderedModel;

    private Camera defaultCamera;

    private final String coreVersion;

    // The factories are here just for deserialization, not for use by controllers
    private final Map<String,Tool.Factory> toolFactories = new HashMap<>();

    private static final Logger logger = Logger .getLogger( "com.vzome.core.editor" );
    private static final Logger thumbnailLogger = Logger.getLogger( "com.vzome.core.thumbnails" );

    // 2013-05-26
    //  I thought about leaving these two in EditorModel, but reconsidered.  Although they are in-memory
    //  state only, not saved in the file, they are still necessary for non-interactive use such as lesson export.

    private RenderedModel[] snapshots = new RenderedModel[8];

    private int numSnapshots = 0;

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport( this );

    private final Map<String,OrbitSource> symmetrySystems = new HashMap<>();

    private final Lights sceneLighting;

    private final FieldApplication kind;

    private final Application app;

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
        this .app = app;
        this .field = kind .getField();
        AlgebraicVector origin = this .field .origin( 3 );
        this .originPoint = new FreePoint( origin );
        this .failures = failures;
        this .mXML = xml;
        this .sceneLighting = new Lights( app .getLights() );
        if ( this .mXML != null ) {
            Element lightsXml = (Element) this .mXML .getElementsByTagName( "sceneModel" ) .item( 0 );
            if ( lightsXml != null ) {
                String colorString = lightsXml .getAttribute( "background" );
                this .sceneLighting .setBackgroundColor( Color .parseColor( colorString ) );
            }
        }

        this .coreVersion = app .getCoreVersion();

     // TODO: vvv - most of this can be moved into EditorModel initialization
        
        this .mRealizedModel = new RealizedModelImpl( this .field, new Projection.Default( this .field ) );

        if ( this .mXML != null ) {
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

        // Initialize the default SymmetrySystems from the FieldApplication
        Collection<FieldApplication.SymmetryPerspective> symms = kind .getSymmetryPerspectives();
        for ( FieldApplication.SymmetryPerspective symmPerspective1 : symms )
        {
            SymmetrySystem osm = new SymmetrySystem( null, symmPerspective1, this, app .getColors(), true );
            this .symmetrySystems .put( osm .getName(), osm );
        }
        // Now overwrite some or all of those default SymmetrySystems with those stored in the file.
        //   This is important mostly for the automatic orbits, but can also carry color overrides.
        //
        SymmetrySystem symmetrySystem = new SymmetrySystem( xml, symmPerspective, this, app .getColors(), true );
        this .symmetrySystems .put( symmPerspective .getName(), symmetrySystem );
        if ( this .mXML != null ) {
            NodeList nl = this .mXML .getElementsByTagName( "OtherSymmetries" );
            if ( nl .getLength() != 0 ) {
                xml = (Element) nl .item( 0 );
                NodeList nodes = xml .getElementsByTagName( "SymmetrySystem" );
                for ( int i = 0; i < nodes .getLength(); i++ ) {
                    Node node = nodes .item( i );
                    if ( node instanceof Element ) {
                        Element symmElem = (Element) node;
                        String symmName = symmElem .getAttribute( "name" );  
                        symmPerspective = kind .getSymmetryPerspective( symmName );
                        SymmetrySystem otherSymmetrySystem = new SymmetrySystem( symmElem, symmPerspective, this, app .getColors(), true );
                        this .symmetrySystems .put( symmName, otherSymmetrySystem );
                    }
                }
            }
        }

        this .renderedModel = new RenderedModel( this .field, symmetrySystem );

        this .mRealizedModel .addListener( this .renderedModel ); // just setting the default
        // the renderedModel must either be disabled, or have shapes here, so the origin ball gets rendered
        this .editorModel = new EditorModelImpl( this .mRealizedModel, originPoint, kind, symmetrySystem, this .symmetrySystems );
        this .tools .setEditorModel( this .editorModel );

        // cannot be done in the constructors
        for ( OrbitSource symmetrySys : this .symmetrySystems .values()) {
            ((SymmetrySystem) symmetrySys) .createToolFactories( this .tools );
            for ( Tool.Kind toolkind : Tool.Kind.values() )
            {
                List<Tool.Factory> list = ((SymmetrySystem) symmetrySys) .getToolFactories( toolkind );
                for ( Tool.Factory factory : list ) {
                    this .editorModel .addSelectionSummaryListener( (SelectionSummary.Listener) factory );
                }
            }
        }

        kind .registerToolFactories( this .toolFactories, this .tools );

        this .bookmarkFactory = new BookmarkTool.Factory( tools );
        this .editorModel .addSelectionSummaryListener( this .bookmarkFactory );

        this .bookmarkFactory .createPredefinedTool( "ball at origin" );

     // TODO: ^^^ - most of this can be moved into EditorModel initialization
        
        this .defaultCamera = new Camera();
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
                        this .defaultCamera = new Camera( viewElem );
                    }
                }
            }
        }

        mHistory = new EditHistory();
        mHistory .setListener( new EditHistory.Listener() {

            @Override
            public void showCommand( Element xml, int editNumber )
            {
                String str = editNumber + ": " + DomSerializer .toString( xml );
                DocumentModel.this .firePropertyChange( "current.edit.xml", null, str );
            }

            @Override
            public void publishChanges()
            {
                editorModel .notifyListeners();
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
        ManifestationImpl m = (ManifestationImpl) this .mRealizedModel .findConstruction( originPoint );
        m .setRenderedObject( null );
        this .mRealizedModel .show( m );
    }

    @Override
    public UndoableEdit createEdit( Element xml )
    {
        String name = xml .getLocalName();
        UndoableEdit edit = this .tools .createEdit( name );
        if ( edit != null )
            return edit;

        String toolId = xml .getAttribute( "name" );
        if ( toolId != null && ! "" .equals( toolId ) )
        {
            AbstractToolFactory factory = (AbstractToolFactory) this .toolFactories .get( name );
            if ( factory != null )
                edit = factory .deserializeTool( toolId );
            if ( edit != null )
                return edit;
        }

        switch (name) {
        case "Snapshot":
            return new Snapshot( -1, this );
        case "Branch":
            return new Branch( this );
        }
        
        edit = this .editorModel .createEdit( name );
        if ( edit != null ) return edit;

        // this is only relevant for deserialization, so it cannot go inside the prior createEdit call
        return new CommandEdit( null, this .editorModel );
    }

    public String copySelectionVEF(boolean withOffset)
    {
        StringWriter out = new StringWriter();
        Exporter exporter = new VefModelExporter( out, field, null, withOffset);
        for ( Manifestation man : editorModel .getSelection() ) {
            exporter .exportManifestation( man );
        }
        exporter .finish();
        return out .toString();
    }

    public String copyRenderedModel( String format )
    {
        StringWriter out = new StringWriter();
        switch ( format ) {

        case "cmesh":
            try {
                ColoredMeshJson .generate( this .editorModel .getSelection(), this .field, out );
            } catch (IOException e) {
                // TODO fail better here
                e.printStackTrace();
            }
            break;

        case "shapes":
            ShapesJsonExporter ojex = new ShapesJsonExporter();
            try {
                ojex .doExport( this, null, null, out, 0, 0 );
            } catch (Exception e) {
                // TODO fail better here
                e.printStackTrace();
            }
            break;
        }
        return out .toString();
    }

    public boolean doEdit( String action, Map<String,Object> props )
    {
        if ( this .editorModel .mSelection .isEmpty() && action .equals( "hideball" ) ) {
            action = "ShowHidden";
        }

        Command command = this .kind .getLegacyCommand( action );
        if ( command != null )
        {
            CommandEdit edit = new CommandEdit( (AbstractCommand) command, editorModel );
            this .performAndRecord( edit );
            return true;
        }
        
        String[] tokens = action .split( "/" );
        action = tokens[ 0 ];
        String mode = ( tokens.length == 2 )? tokens[ 1 ] : null;

        UndoableEdit edit = this .editorModel .createEdit( action );

        if ( edit == null )
        {
            logger .warning( "no DocumentModel action for : " + action );
            return false;
        }
        if ( mode != null )
            props .put( "mode", mode );
        edit .configure( props );
        this .performAndRecord( edit );
        return true;
    }

    public boolean doEdit( String action )
    {
        return this .doEdit( action, new HashMap<>() );
    }

    public void doPickEdit( Manifestation pickedManifestation, String action )
    {
        Map<String,Object> props = new HashMap<>();
        if ( pickedManifestation != null )
            props .put( "picked", pickedManifestation );
        doEdit( action, props );
    }

    @Override
    public void performAndRecord( UndoableEdit edit )
    {
        if ( edit == null )
            return;

        try {
            synchronized ( this .mHistory ) {
                edit .perform();
                if ( edit .isNoOp() )
                    return;
                this .mHistory .mergeSelectionChanges();
                this .mHistory .addEdit( edit, DocumentModel.this );
                this .editorModel .notifyListeners();
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

    public RealVector getLocation( Construction target )
    {
        if ( target instanceof Point)
            return this .renderedModel .renderVector( ( (Point) target ).getLocation() );
        else if ( target instanceof Segment )
            return this .renderedModel .renderVector( ( (Segment) target ).getStart() );
        else if ( target instanceof Polygon )
            return this .renderedModel .renderVector( ( (Polygon) target ).getVertex( 0 ) );
        else
            return new RealVector( 0, 0, 0 );
    }

    public RealVector getCentroid( Manifestation target )
    {
        AlgebraicVector v;
        if ( target instanceof Connector )
            v = ( (Connector) target ) .getLocation();
        else if ( target instanceof Strut ) {
            v = ((Strut) target) .getCentroid( );
        }
        else if ( target instanceof Panel )
            v = ((Panel) target) .getCentroid( );
        else
            v = this.getField().origin(3);

        return this .renderedModel .renderVector( v );
    }

    public RealVector getParamLocation( String string )
    {
        if ( "ball" .equals( string ) )
        {
            Point ball = editorModel .getCenterPoint();
            return ball .getLocation() .toRealVector();
        }
        return new RealVector( 0, 0, 0 );
    }
    
    public Color getSelectionColor()
    {
        Manifestation last = null;
        for (Manifestation man : editorModel .getSelection() ) {
            last = man;
        }
        return last == null ? null : last .getColor();
    }

    public void finishLoading( boolean openUndone, boolean asTemplate ) throws Command.Failure
    {
        try {
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
            XmlSymmetryFormat format = XmlSymmetryFormat.getFormat( tns );
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
                    OrbitSource system = symmetrySystems .get( name );
                    return system .getOrbits();
                }

                @Override
                public QuaternionicSymmetry getQuaternionSet( String name )
                {
                    return kind .getQuaternionSymmetry( name );
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
                    lesson .setXml( notesXml, editNum, this .defaultCamera );

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

            // This has to be done before any of the tools are defined, in mHistory .synchronize() below
            Element toolsXml = (Element) mXML .getElementsByTagName( "Tools" ) .item( 0 );
            if ( toolsXml != null )
                this .tools .loadFromXml( toolsXml );

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
        finally {
            if(logger.isLoggable(Level.FINE)) {
                double duration = (System.nanoTime() - startTime) / 1000000000D;
                logger.fine( "Document @ " + System.identityHashCode(this) + 
                        " loaded in " + duration + " seconds" 
                        + (mXML == null ? " (new)" : " from XML") );
            }
        }
    }
    private final long startTime = System.nanoTime();

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

        childElement = getSceneLighting() .getXml( doc );
        vZomeRoot .appendChild( childElement );

        childElement = doc .createElement( "Viewing" );
        Element viewXml = this .defaultCamera .getXML( doc );
        childElement .appendChild( viewXml );
        vZomeRoot .appendChild( childElement );

        childElement = ((SymmetrySystem) this .editorModel .getSymmetrySystem()) .getXml( doc );
        vZomeRoot .appendChild( childElement );

        // We have to store all symmetries, not just the current one, due to sequences like this:
        //   1. drag an icosahedral olive strut
        //   2. switch to octahedral symmetry
        //   3. do "build with this" on the olive strut
        //   4. drag out a strut
        //   5. switch back to icosahedral symmetry
        // The end result is that the command in step 4 records the name of an automatic orbit.
        // That automatic orbit must be captured in the file.
        childElement = doc .createElement( "OtherSymmetries" );
        for ( Iterator<OrbitSource> iterator = this .editorModel .getSymmetrySystems(); iterator.hasNext(); ) {
            OrbitSource symmetry = iterator.next();
            if ( symmetry == this .editorModel .getSymmetrySystem() )
                continue; // already serialized above
            Element symmElement = ((SymmetrySystem) symmetry) .getXml( doc );
            childElement .appendChild( symmElement );
        }
        vZomeRoot .appendChild( childElement );

        childElement = this .tools .getXml( doc );
        vZomeRoot .appendChild( childElement );

        DomSerializer .serialize( doc, out );
    }

    public AlgebraicField getField()
    {
        return this .field;
    }

    public void addSelectionListener( ManifestationChanges listener )
    {
        ((SelectionImpl) this .editorModel .getSelection()) .addListener( listener );
    }

    private AbstractToolFactory bookmarkFactory;

    public void undoToManifestation( Manifestation man )
    {
        mHistory .undoToManifestation( man );
        this .editorModel .notifyListeners();
    }

    public Exporter3d getNaiveExporter( String format, Camera camera, Colors colors, Lights lights, RenderedModel currentSnapshot )
    {
        Exporter3d exporter = null;
        switch ( format ) {

        case "pov":
            exporter = new POVRayExporter( camera, colors, lights, currentSnapshot );
            break;

        default:
            break;
        }

        boolean inArticleMode = (renderedModel != currentSnapshot);
        if ( exporter != null && exporter.needsManifestations() && inArticleMode ) {
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

    public Exporter3d getStructuredExporter( String format, Camera camera, Colors colors, Lights lights, RenderedModel mRenderedModel )
    {
        if ( format.equals( "partgeom" ) )
            return new PartGeometryExporter( camera, colors, lights, mRenderedModel, editorModel .getSelection() );
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

    public void addSnapshotPage( Camera camera )
    {
        int id = numSnapshots;
        this .performAndRecord( new Snapshot( id, this ) );
        lesson .newSnapshotPage( id, camera );
    }

    public RenderedModel getRenderedModel()
    {
        return this .renderedModel;
    }

    public Camera getCamera()
    {
        return this .defaultCamera;
    }

    public Segment getSelectedSegment()
    {
        return (Segment) editorModel .getSelectedConstruction( Segment.class );
    }

    public Segment getSymmetryAxis()
    {
        return editorModel .getSymmetrySegment();
    }

    public ToolsModel getToolsModel()
    {
        return this .tools;
    }

    public OrbitSource getSymmetrySystem()
    {
        return this .editorModel .getSymmetrySystem();
    }

    public OrbitSource getSymmetrySystem( String name )
    {
        if ( name == null )
            return this .getSymmetrySystem();
        else
            return this .symmetrySystems .get( name );
    }

    public void setSymmetrySystem( SymmetrySystem system )
    {
        this .editorModel .setSymmetrySystem( system );
    }

    public FieldApplication getFieldApplication()
    {
        return this .kind;
    }

    public Factory getBookmarkFactory()
    {
        return this .bookmarkFactory;
    }

    public EditHistory getHistoryModel()
    {
        return this .mHistory;
    }

    public EditorModel getEditorModel()
    {
        return this .editorModel;
    }

    public Java2dSnapshot capture2d( RenderedModel model, int height, int width, Camera camera, Lights lights,
            boolean drawLines, boolean doLighting ) throws Exception
    {
        Java2dExporter captureSnapshot = new Java2dExporter();
        Java2dSnapshot snapshot = captureSnapshot .render2d( model, camera, lights, height, width, drawLines, doLighting );

        return snapshot;
    }

    public void export2d( Java2dSnapshot snapshot, String format, File file, boolean doOutlines, boolean monochrome ) throws Exception
    {
        SnapshotExporter exporter = this .app .getSnapshotExporter( format );
        // A try-with-resources block closes the resource even if an exception occurs
        try ( Writer out = new FileWriter( file ) ) {
            exporter .export( snapshot, out, doOutlines, monochrome );
        }
    }

    public Lights getSceneLighting()
    {
        return this.sceneLighting;
    }
}
