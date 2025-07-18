
package com.vzome.desktop.awt;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.SwingWorker;
import javax.vecmath.Point3f;

import org.vorthmann.j3d.MouseTool;
import org.vorthmann.j3d.MouseToolDefault;
import org.vorthmann.j3d.MouseToolFilter;
import org.vorthmann.j3d.Trackball;
import org.vorthmann.ui.LeftMouseDragAdapter;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.Color;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;
import com.vzome.core.editor.DocumentModel;
import com.vzome.core.editor.SymmetryPerspective;
import com.vzome.core.editor.SymmetrySystem;
import com.vzome.core.editor.api.ImplicitSymmetryParameters;
import com.vzome.core.editor.api.OrbitSource;
import com.vzome.core.edits.AffinePolygon;
import com.vzome.core.exporters.DocumentExporterIntf;
import com.vzome.core.exporters.GeometryExporter;
import com.vzome.core.exporters2d.Java2dSnapshot;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.QuaternionProjection;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.ManifestationChanges;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.render.Scene;
import com.vzome.core.viewing.Camera;
import com.vzome.core.viewing.Lights;
import com.vzome.desktop.api.Controller;
import com.vzome.desktop.controller.AnimationController;
import com.vzome.desktop.controller.CameraController;
import com.vzome.desktop.controller.LengthController;
import com.vzome.desktop.controller.MeasureController;
import com.vzome.desktop.controller.NumberController;
import com.vzome.desktop.controller.PartsController;
import com.vzome.desktop.controller.PolytopesController;
import com.vzome.desktop.controller.ShareController;
import com.vzome.desktop.controller.StrutBuilderController;
import com.vzome.desktop.controller.SymmetryController;
import com.vzome.desktop.controller.ToolFactoryController;
import com.vzome.desktop.controller.ToolsController;
import com.vzome.desktop.controller.UndoRedoController;
import com.vzome.desktop.controller.VectorController;

import shine.htetaung.giffer.Giffer;

/**
 * @author Scott Vorthmann 2003
 */
public class DocumentController extends DefaultGraphicsController implements Scene.Provider
{
    // local state
    //
    private int changeCount = 0;
    private final boolean startReader;
    private DocumentModel documentModel;
    private boolean editingModel;
    private Camera currentView; // ?? TBD if this is in the right group
    private final Properties properties;
    
    // to RenderedModelController
    //
    private RenderingViewer imageCaptureViewer;
    private final RenderedModel mRenderedModel;
    private Scene mainScene;
    private Lights sceneLighting;
    private MouseTool modelModeMainTrackball;
    private Component modelCanvas;
    private boolean drawNormals = false;
    private boolean drawOutlines = false;
    private boolean showFrameLabels = false;
    private Java2dSnapshotController java2dController = null;
    private CameraController cameraController;
    private final ApplicationController mApp;

    // to SymmetryRenderingController?
    //
    private SymmetryController symmetryController;
    private Map<String,SymmetryController> symmetries = new HashMap<>(); // keyed by labels, like SymmetryPerspectives, not symmetry names
    private final PartsController partsController;

    // to SelectionController (subclass of RenderedModelController?)
    //
    private MouseTool selectionClick;
    private boolean mRequireShift = false;
    private final ManifestationChanges selectionRendering;
    
    private final StrutBuilderController strutBuilder;
    private final StrutBuilderGraphicsController strutBuilderGraphics;

    // to LessonController
    //
    private ThumbnailRendererImpl thumbnails;
    private MouseTool lessonPageClick, articleModeMainTrackball, articleModeZoom;
    private final LessonController lessonController;
    private PropertyChangeListener articleChanges;
    private RenderedModel currentSnapshot;
    
    private PropertyChangeListener modelChanges;
        
    private final ClipboardController systemClipboard;
    private String designClipboard;
    
    private final NumberController importScaleController;
    private final VectorController quaternionController;
    private String lastObjectColor = "#ffffffff";
    
    private boolean mainViewRotation = true;

   /*
     * See the javadoc to control the logging:
     * 
     * http://java.sun.com/j2se/1.4.2/docs/api/java/util/logging/LogManager.html
     * 
     * The easiest approach is to edit the lines at the end of the global config
     * file:
     * 
     * edit /Library/Java/Home/lib/logging.properties
     */
    private static final Logger logger = Logger .getLogger( "org.vorthmann.zome.app.impl" );

    public DocumentController( DocumentModel document, ApplicationController app, Properties props )
    {
        // initialize this.properties before calling setNextController()
        // so it can safely call getProperty()
        this .properties = props;
        this .documentModel = document;
        
        app .addSubController( "someDoc", this ); // TODO fix this
   
        if ( this .documentModel .isMigrated() )
            this .changeCount = -1; // this will force isEdited() to return true

        final boolean asTemplate = propertyIsTrue( "as.template" );

        final boolean newDocument = propertyIsTrue( "new.document" );
        
        drawOutlines = propertyIsTrue( "outline.geometry" );

        startReader = ! newDocument && ! asTemplate;
        
        editingModel = super.userHasEntitlement( "model.edit" ) && ! propertyIsTrue( "reader.preview" );
        
        systemClipboard = propertyIsTrue( "enable.system.clipboard" )
                ? new ClipboardController()
                : null;
        
        ToolsController toolsController = new ToolsController( document .getToolsModel() );
        this .addSubController( "tools", toolsController );
        this .addPropertyListener( toolsController );
        
        toolsController .addTool( document .getToolsModel() .get( "bookmark.builtin/ball at origin" ) );

        this .addSubController( "bookmark", new ToolFactoryController( this .documentModel .getBookmarkFactory() ) );
        
        this .addSubController( "polytopes", new PolytopesController( (ImplicitSymmetryParameters) this .documentModel .getEditorModel(), document ) );
        
        this .addSubController( "undoRedo", new UndoRedoController( this .documentModel .getHistoryModel() ) );
 
        this .addSubController( "github", new ShareController() );

        importScaleController = new NumberController( this .documentModel .getField() );
        this .addSubController( "importScale", importScaleController );
        
        quaternionController = new VectorController( this .documentModel .getField() .basisVector( 4, AlgebraicVector.W4 ) );
        this .addSubController( "quaternion", quaternionController );

        mRenderedModel = new RenderedModel( this .documentModel .getField(), null );
        currentSnapshot = mRenderedModel;

        selectionRendering = new ManifestationChanges()
        {
            @Override
            public void manifestationAdded( Manifestation m )
            {
                mRenderedModel .setManifestationGlow( m, true );
            }

            @Override
            public void manifestationRemoved( Manifestation m )
            {
                mRenderedModel .setManifestationGlow( m, false );
            }

            @Override
            public void manifestationColored( Manifestation m, Color c ) {}

            @Override
            public void manifestationLabeled( Manifestation m, String label ) {}
        };
        this .documentModel .addSelectionListener( selectionRendering );
        
        this .addSubController( "measure", new MeasureController( this .documentModel .getEditorModel(), this .mRenderedModel ) );

        this .articleChanges = new PropertyChangeListener()
        {   
            @Override
            public void propertyChange( PropertyChangeEvent change )
            {
                if ( "currentSnapshot" .equals( change .getPropertyName() ) )
                {
                    // contents of old "renderSnapshot" action
                    RenderedModel newSnapshot = (RenderedModel) change .getNewValue();
                    if ( newSnapshot != null && newSnapshot != currentSnapshot )
                    {
                        synchronized ( newSnapshot ) {
                            RenderedModel .renderChange( currentSnapshot, newSnapshot, mainScene );
                        }
                        currentSnapshot = newSnapshot;
                    }
                }
                else if ( "currentView" .equals( change .getPropertyName() ) )
                {
                    Camera newView = (Camera) change .getNewValue();
                    if ( ! newView .equals( cameraController .getView() ) )
                        cameraController .restoreView( newView );
                }
                else if ( "thumbnailChanged" .equals( change .getPropertyName() ) )
                {
                    int pageNum = (Integer) change .getNewValue();
                    DocumentController .this .documentModel .getLesson() .updateThumbnail( pageNum, DocumentController .this .documentModel, thumbnails );
                }
            }
        };
        this .modelChanges = new PropertyChangeListener()
        {   
            @Override
            public void propertyChange( PropertyChangeEvent change )
            {
                switch ( change .getPropertyName() ) {

                case "current.edit.xml":
                    firePropertyChange( change ); // forward to the UI for display
                    break;

                default:
                    break;
                }
            }
        };
        if ( editingModel )
            this .documentModel .addPropertyChangeListener( this .modelChanges );
        else
            this .documentModel .addPropertyChangeListener( this .articleChanges );

        int maxOrientations = 0;
        for ( SymmetryPerspective perspective : this .documentModel .getFieldApplication() .getSymmetryPerspectives() ) {
            int order = perspective .getSymmetry() .getChiralOrder();
            if ( order > maxOrientations )
                maxOrientations = order;
        }

        sceneLighting = this .documentModel .getSceneLighting();
        
        mainViewRotation = ! app .propertyIsTrue( "no.main.view.trackball" );

        cameraController = new CameraGraphicsController( document .getCamera(), sceneLighting, maxOrientations, mainViewRotation? 0.15d : 0.7d );
        this .addSubController( "camera", cameraController );
        this .articleModeZoom = new CameraZoomWheel( this .cameraController );

        strutBuilder = new StrutBuilderController( this .documentModel, this .documentModel .getField() )
                .withGraphicalViews( app .propertyIsTrue( "useGraphicalViews" ) )
                .withShowStrutScales( app .propertyIsTrue( "showStrutScales" ) );
        this .addSubController( "strutBuilder", strutBuilder );
        this .strutBuilderGraphics = new StrutBuilderGraphicsController( strutBuilder, cameraController );
        
        SymmetrySystem docSymmetrySystem = (SymmetrySystem) this .documentModel .getEditorModel() .getSymmetrySystem();
        
        // In case we fall through without matching (due to a legacy system in the doc, no longer UI-exposed)
        String docLabel = document .getFieldApplication() .getDefaultSymmetryPerspective() .getLabel();

        for ( SymmetryPerspective symper : document .getFieldApplication() .getSymmetryPerspectives() ) {
            String label = symper .getLabel(); // this can be different from name
            if ( label != null ) // null label means this symmetry is not to expose in the UI
            {
                String name = symper .getSymmetry() .getName();
                SymmetrySystem system = (SymmetrySystem) this .documentModel .getEditorModel() .getSymmetrySystem( name );
                if ( system.getName() .equals( docSymmetrySystem.getName() ) ) {
                    docLabel = label; // for setSymmetryController(), below
                }
                SymmetryController symmController = new SymmetryController( label, strutBuilder, system, mRenderedModel );
                strutBuilder .addSubController( "symmetry." + label, symmController );
                this .symmetries .put( label, symmController );
            }
        }

        mRequireShift = "true".equals( app.getProperty( "multiselect.with.shift" ) );
        showFrameLabels = "true" .equals( app.getProperty( "showFrameLabels" ) );

        thumbnails = new ThumbnailRendererImpl( sceneLighting, maxOrientations );
        this .addSubController( "thumbnails", thumbnails );
        thumbnails .setFactory( app .getJ3dFactory() );

        mApp = app;
        
        lessonController = new LessonController( this .documentModel .getLesson(), cameraController );
        this .addSubController( "lesson", lessonController );

        this .mainScene = new Scene( this .sceneLighting, this .drawOutlines, maxOrientations );
        if ( this .mainScene instanceof PropertyChangeListener )
            this .addPropertyListener( this .mainScene );
        this .strutBuilder .setMainScene( this .mainScene );

        setSymmetryController( docLabel );

        // can't do this before the setSymmetrySystem() call just above
        if ( mRenderedModel != null )
        {
            this .documentModel .setRenderedModel( mRenderedModel );
            this .currentSnapshot = mRenderedModel;  // Not too sure if this is necessary
        }
        
        partsController = new PartsController( this .documentModel .getEditorModel() .getSymmetrySystem() );
        this .addSubController( "parts", partsController );
        mRenderedModel .addListener( partsController );

        copyThisView(); // initialize the "copied" view at startup.

        /*
         * Mouse tools here follow some general principles:
         * 
         * 1. Don't try to dispatch events by pipelining tools ("first one eats").  Instead, all tools get the event,
         *     and let mutually-exclusive conditions make sure that only the desired processing
         *     occurs.  MouseToolFilter is a good way to filter events.
         *     
         * 2. Use LeftMouseDragAdapter whenever you need drag / click hysteresis.  Really, the
         *     only "drag" tool here that does not need that is the targetManifestationDrag,
         *     since that is not a true drag.
         *     
         * 3. Use mode switch (article/model) to detach and attach sets of tools.
         * 
         * 4. A Trackball can be subclassed to determine what the transform operates on.
         * 
         */

        // these are for the model viewer (article mode)
        // will not be attached, initially; gets attached on switchToArticle
        lessonPageClick = new MouseToolDefault()
        {
            @Override
            public void mouseClicked( MouseEvent e )
            {
                actionPerformed( e .getSource(), "nextPage" );
                e .consume();
            }
        };

        articleModeMainTrackball = new CameraTrackball( cameraController, 0.7d );
        // will not be attached, initially; gets attached on switchToArticle
        if ( propertyIsTrue( "presenter.mode" ) )
            ((Trackball) articleModeMainTrackball) .setModal( false );
//        if ( ! editingModel )
//        {
//            // cannot use MouseTool .attach(), because it attaches a useless wheel listener,
//            //  and ViewPlatformControlPanel will attach a better one to the parent component 
//            canvas .addMouseListener( mouseTool );
//            canvas .addMouseMotionListener( mouseTool );
//        }

        // this wrapper for mainCanvasTrackball is disabled when the press is initiated over a ball
        modelModeMainTrackball = new LeftMouseDragAdapter( new MouseToolFilter( articleModeMainTrackball )
        {
            boolean live = false;

            @Override
            public void mousePressed( MouseEvent e )
            {
                RenderedManifestation rm = imageCaptureViewer .pickManifestation( e );
                if ( rm == null || !( rm .getManifestation() instanceof Connector ) )
                {
                    this .live = true;
                    super .mousePressed( e );
                }
            }

            @Override
            public void mouseDragged( MouseEvent e )
            {
                if ( live )
                    super .mouseDragged( e );
            }

            @Override
            public void mouseReleased( MouseEvent e )
            {
                this .live = false;
                super .mouseReleased( e );
            }
        } );
    }
    
    @Override
    public void attachViewer( GraphicsViewer viewer, Component canvas )
    {
            // This is called on a UI thread!
        this .modelCanvas = canvas;
        this .imageCaptureViewer = (RenderingViewer) viewer;
        
        // clicks become select or deselect all
        selectionClick = new LeftMouseDragAdapter( new ManifestationPicker( imageCaptureViewer )
        {
            @Override
            protected void manifestationPicked( Manifestation target, boolean shiftKey )
            {
                mErrors .clearError();
                boolean shift = true;
                if ( mRequireShift )
                    shift = shiftKey;
                if ( target == null )
                    documentModel .doEdit( "DeselectAll" );
                else
                    documentModel .doPickEdit( target, "SelectManifestation" + ( shift? "" : "/replace" ) );
            }
        } );
        this .cameraController .addViewer( this .imageCaptureViewer );
        this .addSubController( "monocularPicking", new PickingController( this .imageCaptureViewer, this ) );

        this .strutBuilderGraphics .attach( (RenderingViewer) viewer, this .mainScene );
        
        if ( this .modelCanvas != null )
            if ( editingModel ) {
                this .selectionClick .attach( modelCanvas );
                if ( mainViewRotation )
                    this .modelModeMainTrackball .attach( modelCanvas );
                this .strutBuilderGraphics .attach( modelCanvas );
            } else {
                this .articleModeMainTrackball .attach( modelCanvas );
                this .articleModeZoom .attach( modelCanvas );
                this .lessonPageClick .attach( modelCanvas );
            }
    }
    
    private void setSymmetryController( String label )
    {
        this.symmetryController = this .symmetries .get( label );
        if (this.symmetryController == null) {
            String msg = "Unsupported symmetry: " + label;
            mErrors.reportError(msg, new Object[] {} );
            throw new IllegalStateException( msg );
        }
        
        SymmetrySystem symmetrySystem = (SymmetrySystem) this.symmetryController .getOrbitSource();
        this .documentModel .setSymmetrySystem( symmetrySystem ); // redundant within initSymmetryController() calls,
        // except when we are replacing a legacy system with a currently supported one


        // The current code is running on the EDT, and we want to load the symmetry model on a background thread,
        //  and then update the UI on the EDT.
        SwingWorker<RenderedModel,Object> worker = new SwingWorker<RenderedModel,Object>()
        {
            @Override
            protected RenderedModel doInBackground() throws Exception
            {
                String modelResourcePath = symmetryController .getProperty( "modelResourcePath" );
                RenderedModel model = mApp .getSymmetryModel( modelResourcePath, symmetryController .getSymmetry() );
                return model;
            }

            @Override
            protected void done()
            {
                // This runs on the EDT again.  We can be sure it won't run until the current
                //   thread (calling execute()) has completed, AND doInBackground() has completed.
                try {
                    cameraController .setSymmetry( get(), symmetryController .getSnapper() );
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        worker .execute();  //Start the background thread

        firePropertyChange( "symmetry", null, label  ); // notify UI, so cardpanel can flip, or whatever

        if ( mRenderedModel != null ) {
            if ( partsController != null )
                partsController .startSwitch( symmetrySystem );
            mRenderedModel .setOrbitSource( symmetrySystem );
            if ( partsController != null )
                partsController .endSwitch();
        }
        strutBuilder .setSymmetryController( this.symmetryController );
    }


    @Override
    public void doAction( String action ) throws Failure
    {
        if ( "finish.load".equals( action ) ) {

            boolean openUndone = propertyIsTrue( "open.undone" );
            boolean asTemplate = propertyIsTrue( "as.template" );
            boolean headless = propertyIsTrue( "headless.open" );

            // used to finish loading a model history on a non-UI thread
            this .documentModel .finishLoading( openUndone, asTemplate );
                        
            // mainScene is not listening to mRenderedModel yet, so batch the rendering changes to it
            if ( !headless && mainScene != null )
            {
                if ( editingModel )
                {
                    RenderedModel .renderChange( new RenderedModel( null, null ), mRenderedModel, mainScene );
                    mRenderedModel .addListener( mainScene );
                    // get the thumbnails updating in the background thread (we are already on)
                    if ( lessonController != null )
                        lessonController .renderThumbnails( documentModel, thumbnails );
                }
                else
                    try {
                        currentSnapshot = new RenderedModel( null, null ); // force render of first snapshot, see "renderSnapshot." below
                        lessonController .doAction( "restoreSnapshot" );
                        // order these to avoid issues with the thumbnails (unexplained)
                        lessonController .renderThumbnails( documentModel, thumbnails );
                    } catch ( Exception e1 ) {
                        Throwable cause = e1.getCause();
                        if ( cause instanceof Command.Failure )
                            mErrors.reportError( USER_ERROR_CODE, new Object[] { cause } );
                        else
                            mErrors.reportError( UNKNOWN_ERROR_CODE, new Object[] { e1 } );
                    }
            }
            return;
        }
        
        mErrors .clearError();
        try {
            final boolean vefExportOffset = propertyIsTrue( "copy.vef.include.offset" );
            switch (action) {

            case "switchToArticle":
                {
                    this .currentView = this .cameraController .getView();
                    
                    this .selectionClick .detach( this .modelCanvas );
                    this .strutBuilderGraphics .detach( this .modelCanvas );
                    if ( mainViewRotation )
                        this .modelModeMainTrackball .detach( this .modelCanvas );
                    
                    this .lessonPageClick .attach( this .modelCanvas );
                    this .articleModeMainTrackball .attach( this .modelCanvas );
                    this .articleModeZoom .attach( this .modelCanvas );
    
                    this .documentModel .addPropertyChangeListener( this .articleChanges );
                    this .documentModel .removePropertyChangeListener( this .modelChanges );
                    this .lessonController .doAction( "restoreSnapshot" );
    
                    this .editingModel = false;
                    firePropertyChange( "editor.mode", "model", "article" );
                }
                break;

            case "takeSnapshot":
                documentModel .addSnapshotPage( cameraController .getView() );
                break;

            case "refresh.2d":
                this .java2dController .setScene( cameraController.getView(), this.sceneLighting, this.currentSnapshot, this.drawOutlines );
                break;

            case "nextPage":
                lessonController .doAction( action );
                break;

            case "switchToModel":
                {
                    this .documentModel .removePropertyChangeListener( this .articleChanges );
                    this .documentModel .addPropertyChangeListener( this .modelChanges );
                    this .cameraController .restoreView( this .currentView );
    
                    RenderedModel .renderChange( this .currentSnapshot, this .mRenderedModel, this .mainScene );
                    this .currentSnapshot = this .mRenderedModel;
    
                    this .lessonPageClick .detach( this .modelCanvas );
                    this .articleModeMainTrackball .detach( this .modelCanvas );
                    this .articleModeZoom .detach( this .modelCanvas );
                    
                    this .selectionClick .attach( this .modelCanvas );
                    if ( mainViewRotation )
                        this .modelModeMainTrackball .attach( this .modelCanvas );
                    this .strutBuilderGraphics .attach( this .modelCanvas );
    
                    this .editingModel = true;
                    firePropertyChange( "editor.mode", "article", "model" );
                }
                break;


            case "ReplaceWithShape":
                this .getSymmetryController() .doAction( action );
                break;

            case "toggleFrameLabels":
                {
                    showFrameLabels = ! showFrameLabels;
                    firePropertyChange( "showFrameLabels", !showFrameLabels, showFrameLabels );
                }
                break;
    
            case "toggleNormals":
                {
                    drawNormals = ! drawNormals;
                    firePropertyChange( "drawNormals", !drawNormals, drawNormals );
                }
                break;
    
            case "toggleOutlines":
                {
                    drawOutlines = ! drawOutlines;
                    firePropertyChange( "drawOutlines", !drawOutlines, drawOutlines );
                }
                break;
    
            case "copyThisView":
                copyThisView();
                break;
    
            case "useCopiedView":
                cameraController .useCopiedView();
                break;
    
            case "lookAtOrigin":
                cameraController.setLookAtPoint( new Point3f( 0, 0, 0 ) );
                break;
    
            case "lookAtSymmetryCenter":
                {
                    RealVector loc = documentModel .getParamLocation( "ball" );
                    cameraController .setLookAtPoint( new Point3f( loc.x, loc.y, loc.z ) );
                }
                break;
    
            case "usedOrbits":
                {
                    Set<Direction> usedOrbits = new HashSet<>();
                    for ( RenderedManifestation rm : mRenderedModel ) {
                        Polyhedron shape = rm .getShape();
                        Direction orbit = shape .getOrbit();
                        if ( orbit != null )
                            usedOrbits .add( orbit );
                    }
                    symmetryController .availableController .doAction( "setNoDirections" );
                    for ( Direction orbit : usedOrbits ) {
                        symmetryController .availableController .doAction( "enableDirection." + orbit .getName() );
                    }
                }
                break;
                
            case "delete":
            case "Delete":
                documentModel .doEdit( "Delete" );
                break;
    
            case "cut":
                setProperty( "clipboard", documentModel .copyRenderedModel( "cmesh" ) );
                documentModel .doEdit( "Delete" );
                break;
    
            case "copy.mesh":
                setProperty( "clipboard", documentModel .copyRenderedModel( "mesh" ) );
                break;
        
            case "copy":
            case "copy.cmesh":
                setProperty( "clipboard", documentModel .copyRenderedModel( "cmesh" ) );
                break;
                
            case "copy.vef":
                setProperty( "clipboard", documentModel .copySelectionVEF( vefExportOffset ) );
                break;
    
            case "copy.shapes":
                setProperty( "clipboard", documentModel .copyRenderedModel( "shapes" ) );
                break;

            case "copy.camera":
                setProperty( "clipboard", cameraController .getProperty( "json" ) );
                break;

            case "paste":
                {
                    String vefContent = getProperty( "clipboard" );
                    Map<String, Object> params = new HashMap<>();
                    params .put( "vef", vefContent );
                    documentModel .doEdit( "ImportColoredMeshJson/clipboard", params );
                }
                break;
    
            case "openTrackballModel": // invoked from custom menu
                String modelResourcePath = this .symmetryController .getProperty( "modelResourcePath" );
                if ( modelResourcePath != null ) {
                    String newAction = "newFromResource-" + modelResourcePath;
                    String symmName = symmetryController.getProperty( "name" ); // the name, not the label
                    if(symmName.startsWith("antiprism")) {
                        // use a delimiter character that shouldn't ever be in a path
                        // and doesn't need to be escaped in a regular expression
                        newAction = newAction + ":" + symmName;
                    }
                    super.doAction(newAction);
                }
                break;

            case "showOrbitTriangle": // invoked from custom menu
            {
                AlgebraicVector[] orbitTriangle = 
                getSymmetryController().getSymmetry().getOrbitTriangle();
                StringBuffer buf = new StringBuffer();
                // We don't need the VEF header here, so skip it.
                // Specify that the vectors are all 3D
                // so we don't need v.inflateTo4d() in the loop below
                buf.append("dimension 3\n\n3");// always 3 vertices too
                for(AlgebraicVector v : orbitTriangle) {
                    buf.append("\n");
                    v.getVectorExpression(buf, AlgebraicField.VEF_FORMAT);
                }
                // 0 edges, 1 triangular panel and 0 balls
                buf.append("\n\n0\n\n1\n3  0 1 2\n\n0\n");
                Map<String, Object> params = new HashMap<>();
                params .put( "vef", buf.toString() );
                documentModel .doEdit( "LoadVEF", params );
            }
            break;
            
            case "show.vZome-xml":   // invoked from custom menu
                System.out.println(getProperty("vZome-xml"));
                break;
                
            default:
                if ( action.startsWith( "setSymmetry." ) ) {
                    String label = action.substring( "setSymmetry.".length() );
                    this .setSymmetryController( label );
                }
    
                else if ( action.startsWith( "AdjustSelectionByOrbitLength/" ) )
                {
                    String tail = action.substring( "AdjustSelectionByOrbitLength/" .length() );
                    Map<String,Object> props = new HashMap<>();
                    symmetryController .parseOrbitLength( tail, props );
                    documentModel .doEdit( "AdjustSelectionByOrbitLength/" + props.get( "mode"), props );
                }
                else
                {
                    boolean handled = documentModel .doEdit( action );
                    if ( ! handled )
                        super .doAction( action );
                }
            }

 
        } catch ( Command.Failure failure ) {
            // signal an error to the user
            mErrors.reportError( USER_ERROR_CODE, new Object[] { failure } );
        } catch ( Exception re ) {
            Throwable cause = re.getCause();
            if ( cause instanceof Command.Failure )
                mErrors.reportError( USER_ERROR_CODE, new Object[] { cause } );
            else
                mErrors.reportError( UNKNOWN_ERROR_CODE, new Object[] { re } );
        } 
    }

    private void copyThisView()
    {
        cameraController.copyView(cameraController.getView());
    }


    @Override
    public void doScriptAction( String command, String script )
    {
        if ( command.equals( "RunZomicScript" ) || command.equals( "RunZomodScript" ) || command.equals( "RunPythonScript" ) )
        {
            Map<String,Object> props = new HashMap<>();
            props .put( "script", script );
            documentModel .doEdit( command, props );
        }
        else
            super .doScriptAction( command, script );
    }


    /* (non-Javadoc)
     * @see org.vorthmann.ui.DefaultController#doFileAction(java.lang.String, java.io.File)
     */
    @Override
    public void doFileAction( String command, final File file )
    {
        // TODO set output file types
        if ( logger .isLoggable( Level.FINE ) ) logger .fine( String.format( "doFileAction: %s %s", command, file .getAbsolutePath() ) );
        try {
            if ( "save".equals( command ) )
            {               
                File dir = file .getParentFile();
                if ( ! dir .exists() )
                    dir .mkdirs();

                // A try-with-resources block closes the resource even if an exception occurs
                try (FileOutputStream out = new FileOutputStream( file )) {
                    documentModel .serialize( out, this .properties );
                }
                // just did a save, so lets record the document change count again,
                //  so isEdited() will return false until more changes occur.
                // IMPORTANT! TODO if we ever implement "save a copy", this code should NOT reset
                //   the count just because we're writing a copy.  The reset will have to move to the
                //   context of the save.
                this .changeCount  = this .documentModel .getChangeCount();

                // Sample prefs file entry: save.exports=export.dae capture.png capture.jpg export.vef
                String exports = this .getProperty( "save.exports" );
                if ( exports != null ) {
                    Path filePath = file .toPath();
                    String fileName = filePath .getFileName() .toString();
                    int index = fileName .lastIndexOf( "." );
                    if ( index > 0 )
                    {
                        fileName = fileName .substring( 0, index );
                    }
                    for ( String captureOrExport : exports .split( " " ) ) {
                        // captureOrExport should be "capture.png" or "export.dae" or similar
                        String extension = "";
                        String[] cmd = captureOrExport .split("\\.");
                        if(cmd.length == 2 ) {
                            switch (cmd[0]) {
                            case "capture":
                            case "export2d":
                                extension = cmd[1];
                                break;
                            case "export":
                                extension = this .getProperty( "exportExtension." + cmd[1] );
                                break;
                            }
                        }
                        if ( extension == "" ) {
                            mErrors.reportError( UNKNOWN_PROPERTY + " save.exports=" + captureOrExport, null );
                        } else {
                            File exportFile = new File( dir, fileName + "." + extension );
                            doFileAction( captureOrExport, exportFile );
                        }
                    }
                }

                String script = this .getProperty( "save.script" );
                if ( script != null )
                    this .runScript( script, file );
                return;
            }

            if ( "capture-animation" .equals( command ) )
            {
                EventQueue .invokeLater( new FrameGrabber( file, new RedSpinAnimation( this .cameraController ) ) );
                return;
            }

            if ( "capture-wiggle-gif" .equals( command ) )
            {
                EventQueue .invokeLater( new FrameGrabber( file, new WiggleAnimation( this .cameraController ) ) );
                return;
            }

            if ( command.startsWith( "capture." ) )
            {
                final String extension = command .substring( "capture.".length() );
                captureImageFile( file, extension );
                return;
            }
            if ( command.startsWith( "export2d." ) )
            {
                Dimension size = this .modelCanvas .getSize();
                String format = command .substring( "export2d." .length() ) .toLowerCase();
                Java2dSnapshot snapshot = Java2dSnapshotController .capture2d( currentSnapshot, size.height, size.width, cameraController .getView(), sceneLighting, false, true );
                Java2dSnapshotController controller = new Java2dSnapshotController( cameraController .getView(), sceneLighting, currentSnapshot, this .drawOutlines, this .mApp :: get2dExporter ); 
                controller .export2d( snapshot, format, file, this .drawOutlines, false, true );
                this .openApplication( file );
                return;
            }
            if ( command.startsWith( "export." ) )
            {
                Dimension size = this .modelCanvas .getSize();
                Writer out = null;
                try {
                    out = new FileWriter( file );
                    String format = command .substring( "export." .length() ) .toLowerCase();
                    GeometryExporter exporter = this .mApp .getExporter( format ); //, cameraController .getView(), colors, sceneLighting, currentSnapshot );
                    if ( exporter != null ) {
                        if ( exporter instanceof DocumentExporterIntf )
                            ((DocumentExporterIntf) exporter) .exportDocument( documentModel, file, out, size.height, size.width );
                        else
                            exporter .exportGeometry( documentModel .getRenderedModel(), file, out, size.height, size.width );
                    }
                }
                catch (Command.Failure f) {
                    logger .severe( "failed exporting " + command );
                    mErrors.reportError( USER_ERROR_CODE, new Object[] { f } );
                }
                catch (Exception e) {
                    logger .severe( "failed exporting " + command );
                    e .printStackTrace();
                }
                finally {
                    if ( logger .isLoggable( Level.INFO ) ) logger .info( String.format( "exported: %s %s", command, file .getAbsolutePath() ) );
                    out .flush();
                    out .close();
                }
                this .openApplication( file );
                return;
            }
            if ( command.startsWith( "LoadVEF/" )
              || command.startsWith( "ImportSimpleMeshJson/" )
              || command.startsWith( "ImportColoredMeshJson/" ) ) {
                String vefData = readFile( file );
                Map<String, Object> params = new HashMap<>();
                params .put( "vef", vefData );
                params .put( "scale", this .importScaleController .getValue() );
                params .put( "projection", new QuaternionProjection( documentModel .getField(), null,
                                                this .quaternionController .getVector() ) );
                documentModel .doEdit( command, params );
                return;
            }
            if ( command.equals( "import.zomecad.binary" ) ) {
                //                InputStream bytes = new FileInputStream( file );
                //                new ZomeCADImporter( bytes, events, (PentagonField) mField ) .parseStream();
            }
            if ( command.equals( "save.pdf" ) ) {
            }
            super.doFileAction( command, file );
        } catch ( Exception e ) {
            mErrors.reportError( UNKNOWN_ERROR_CODE, new Object[] { e } );
        }
    }

    
    /**
     * 
     * @param dest a File or OutputStream
     * @param extension
     * @param animation
     */
    void captureImageFile( final File file, final String extension )
    {
        String maxSizeStr = getProperty( "max.image.size" );
        final int maxSize = ( maxSizeStr != null )? Integer .parseInt( maxSizeStr ) : -1;
        String format = extension.toUpperCase();
        // According to https://stackoverflow.com/a/3432532/4568099, regarding the "pinkish orange tint", 
        // Java saves the JPEG as ARGB (still with transparency information). 
        // Most viewers, when opening, assume the four channels must correspond to a CMYK (not ARGB) and thus the red tint.
        // In Windows 10, this results in a pinkish orange tint. On Scott's Mac, it's solid black.
        // the solution is to exclude the alpha data when exporting JPEG.
        boolean withAlpha = ! (format.equals( "BMP" ) || format.equals( "JPG" ));

        BufferedImage image = imageCaptureViewer .captureImage( maxSize, withAlpha );
        try {
            FileImageCapture .captureImage( image, file, extension );
            this .openApplication( file );
        } catch (Exception e) {
            this.mErrors .reportError( DocumentController.UNKNOWN_ERROR_CODE, new Object[] { e } );
        }
    }
    
    
    private class FrameGrabber implements Runnable
    {
        private final ArrayList<BufferedImage> imageList;
        private final AnimationController animation;
        private final File file;
        private final int maxSize;

        public FrameGrabber( final File file, final AnimationController animation )
        {
            this .file = file;
            this .animation = animation;
            this .imageList = new ArrayList<>();
            this .maxSize = animation .getImageSize();
        }
        
        @Override
        public void run()
        {
            if ( this .animation .finished() ) {
                Thread bkgdTask = new Thread() // don't tie up the event queue with this!
                {
                    @Override
                    public void run()
                    {
                        try {
                            BufferedImage[] images = imageList .toArray( new BufferedImage[ imageList.size() ] );
                            Giffer .generateFromBI( images, file .getAbsolutePath(), 0, true );
                            openApplication( file );
                        } catch (IOException e) {
                            mErrors .reportError( DocumentController.UNKNOWN_ERROR_CODE, new Object[] { e } );
                        }
                    }
                };
                bkgdTask .start();
            }
            else {
                animation .rotate();
                imageList .add( imageCaptureViewer .captureImage( maxSize, true ) );
                // queue up the next capture in the sequence
                EventQueue .invokeLater( this );
            }
        }
    }
        
    
    private static String readFile( File file ) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // A try-with-resources block closes the resource even if an exception occurs
        try (InputStream bytes = new FileInputStream( file )) {
            byte[] buf = new byte[1024];
            int num;
            while ( ( num = bytes.read( buf, 0, 1024 ) ) > 0 ) {
                out.write( buf, 0, num );
            }
        }
        return new String( out.toByteArray() );
    }
    
    
//    private static String readResource( String resourcePath )
//    {
//        InputStream stream = null;
//        try {
//            stream = DocumentController.class .getClassLoader() .getResourceAsStream( resourcePath );
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            byte[] buf = new byte[1024];
//            int num;
//            while ( ( num = stream .read( buf, 0, 1024 )) > 0 )
//                out .write( buf, 0, num );
//            return new String( out .toByteArray() );
//        } catch (Exception e) {
//            return null;
//        } finally {
//            if ( stream != null )
//                try {
//                    stream .close();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//        }
//    }


//    private static void writeFile( String content, File file ) throws Exception
//    {
//        // A try-with-resources block closes the resource even if an exception occurs
//        try (FileWriter writer = new FileWriter( file )) {
//            writer .write( content );
//        } catch (Exception ex) {
//            throw ex;
//        }
//    }


    @Override
    public boolean[] enableContextualCommands( String[] menu, MouseEvent e )
    {
        boolean[] result = new boolean[menu.length];
        for ( int i = 0; i < menu.length; i++ ) {
            String menuItem = menu[i];
            switch ( menuItem ) {

            case "lookAtOrigin":
            case "lookAtSymmetryCenter":
            case "setBackgroundColor":
            case "copyThisView":
                result[i] = true;
                break;

            case "useCopiedView":
                result[ i ] = cameraController .hasCopiedView();
                break;

            default:
                result[i] = false;
            }
        }
        return result;
    }


    public boolean isEdited()
    {
        int currentChangeCount = this .documentModel .getChangeCount();
        return currentChangeCount > this .changeCount;
    }


    @Override
    public void setErrorChannel( ErrorChannel errors )
    {
        mErrors = errors;
        if ( cameraController == null )
            return;
        cameraController.setErrorChannel( errors );
        lessonController.setErrorChannel( errors );
    }


    @Override
    public String getProperty( String propName )
    {
        switch ( propName ) {

        case "hasScenes":
            return this .getSubController( "lesson" ) .getProperty( "has.pages" );
            
        case "isIcosahedralSymmetry":
            return Boolean .toString( symmetryController.getSymmetry().getName() .equals( "icosahedral" ) );

        case "showIcosahedralLabels":
            // TODO refactor to a controller for ModelPanel
            return "false";

        case "trackball.showIcosahedralLabels":
            if ( super .userHasEntitlement( "developer.extras" ) ) {
                return super.getProperty( "trackball.showIcosahedralLabels" );
            } else
                return "false";
            
        case "clipboard":
            return systemClipboard != null ? systemClipboard.getClipboardContents() : designClipboard;

        case "backgroundColor":
            return this .sceneLighting .getBackgroundColor() .toWebString();
            
        case "lastObjectColor":
            return this .lastObjectColor;

        case "showFrameLabels":
            return Boolean .toString( showFrameLabels );

        case "drawNormals":
            return Boolean .toString( drawNormals );

        case "drawOutlines":
            return Boolean .toString( drawOutlines );

        case "startReader":
            return Boolean.toString( startReader );

        case "migrated":
            return Boolean.toString( this .documentModel .isMigrated() );

        case "edited":
            return Boolean.toString( this .isEdited() );

        case "symmetry":
            return symmetryController .getProperty( "label" );

        case "field.name":
            return this .documentModel .getField() .getName();

        case "shapes-json":
            return this .documentModel .copyRenderedModel( "shapes" );
                    
        case "vZome-xml":
            try ( ByteArrayOutputStream out = new ByteArrayOutputStream() ) {
                documentModel .serialize( out, properties );
                return out .toString();
            } catch ( Exception e ) {
                return null;
            }
            
        case "png-base64": {  // This is used when sharing to GitHub
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final int maxSize = 1600;
            boolean withAlpha = true;
            BufferedImage image = imageCaptureViewer .captureImage( maxSize, withAlpha );
            try {
                if ( propertyIsTrue( "share.3d.watermark" ) ) {
                    InputStream is = DocumentController.class .getClassLoader() .getResourceAsStream( "org/vorthmann/zome/app/3D-watermark.png" );
                    BufferedImage watermarkImage = ImageIO .read( is );
                    WatermarkImage .watermarkImage( image, watermarkImage );
                }
                FileImageCapture .captureImage( image, byteArrayOutputStream, "png" );
            } catch (Exception e) {
                this.mErrors .reportError( DocumentController.UNKNOWN_ERROR_CODE, new Object[] { e } );
            }
            String encoded = Base64.getEncoder() .encodeToString( byteArrayOutputStream .toByteArray() );
            try {
                byteArrayOutputStream .close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return encoded;
        }

        case "field.label": {
            String name = this .documentModel .getField() .getName();
            return super.getProperty( "field.label." + name ); // defer to app controller
            // TODO implement AlgebraicField.getLabel()
        }

        default:
            if ( propName .startsWith( "supports.symmetry." ) )
            {
                String group = propName .substring( "supports.symmetry." .length() );
                Symmetry symm = this .documentModel .getFieldApplication() .getSymmetryPerspective( group ) .getSymmetry();
                return Boolean .toString(symm != null);
            }   
            else if ( propName .startsWith( "tool.description." ) )
            {
                propName = propName .substring( "tool.description." .length() ); // strip "tool.description."
                if ( "bookmark" .equals( propName ) )
                    return "set the selection to be whatever it is now";
                else if ( "module" .equals( propName ) )
                    return "duplicate a module at every point";
                else if ( "point reflection" .equals( propName ) )
                    return "apply a central inversion through a point";
                else if ( "mirror" .equals( propName ) )
                    return "reflect objects in a mirror plane";
                else if ( "translation" .equals( propName ) )
                    return "translate objects by the given offset";
                else if ( "linear map" .equals( propName ) )
                    return "apply a linear transformation";
                else if ( "rotation" .equals( propName ) )
                    return "rotate objects around an axis, by a fixed angle";
                else if ( "scaling" .equals( propName ) )
                    return "scale objects linearly, relative to a fixed point";
                else if ( "plane" .equals( propName ) )
                    return "select objects based on incidence on a plane or half-space";
                else
                    return "replicate objects to create " + propName + " symmetry";
            }
            else if ( propName .startsWith( "file-dialog-title." ) ) {
                switch ( propName .substring( "file-dialog-title." .length() ) ) {
                case "capture-animation":
                    return "Choose a target folder for animation frames";

                default:
                    break; // fall through to properties or super
                }
            }
            // TODO: move this to ApplicationController!
            else if ( propName .startsWith( "exportExtension." ) ) {
                String format = propName .substring( "exportExtension." .length() );
                // handle null exporter so that typo in custom menu doesn't throw NPE 
                GeometryExporter exporter = this .mApp .getExporter( format .toLowerCase() );
                return exporter == null ? "" : exporter .getFileExtension();
            }

            String result = this .properties .getProperty( propName );
            if ( result != null )
                return result;

            return super.getProperty( propName );
        }
    }
    

    @Override
    public Controller getSubController( String name )
    {
        switch ( name ) {

        case "symmetry":
            // This value is transient, so we don't want to use getSubController()
            return symmetryController;
        
        case "snapshot.2d": {
            if ( java2dController == null ) {
                java2dController = new Java2dSnapshotController( cameraController.getView(), this.sceneLighting,
                                        this.currentSnapshot, this.drawOutlines, this .mApp :: get2dExporter );
                this .addSubController( name, java2dController );
            }
            return java2dController;
        }

        default:
            if ( name.startsWith( "symmetry." ) )
                // the substring is a label (for SymmetryController or SymmetryPerspective), not a symmetry name
                return this.symmetries.get( name.substring( "symmetry.".length() ) );
            else
                return super .getSubController( name );
        }
    }

    @Override
    public void setModelProperty( String cmd, Object value )
    {
        if ( "visible".equals( cmd ) ) {
            // Window is listening, will bring itself to the front, or close itself
            // App controller will set topDocument, or remove the document.
            firePropertyChange( "visible", null, value );
        } else if ( "name".equals( cmd ) ) {
            String oldValue = this .properties .getProperty( "window.file" );
            if ( value == null )
                this .properties .remove( "window.file" );
            else {
                this .properties .setProperty( "window.file", (String) value );
                this .properties .setProperty( "original.path", (String) value );
            }
            // App controller is listening, will change its map
            firePropertyChange( "name", oldValue, value );
            firePropertyChange( "window.file", oldValue, value );
            firePropertyChange( "original.path", oldValue, value );
        } else if ( "backgroundColor".equals( cmd ) ) {
            sceneLighting .setProperty( cmd, value );
        } else if ( "lastObjectColor".equals( cmd ) ) {
            this .lastObjectColor = (String) value;
        } else if ( "terminating".equals( cmd ) ) {
            firePropertyChange( cmd, null, value );
        }
        else if ( "clipboard" .equals( cmd ) ) {
            if( systemClipboard != null ) {
                systemClipboard.setClipboardContents((String) value);
            }
            else {
                designClipboard = (String) value;
            }
        }
        
        else if ( "showFrameLabels" .equals( cmd ) )
        {
            boolean old = showFrameLabels;
            showFrameLabels = "true" .equals( value );
            firePropertyChange( "showFrameLabels", old, showFrameLabels );
        }
        else
            super .setModelProperty( cmd, value );
    }


    @Override
    public String[] getCommandList( String listName )
    {
        switch ( listName ) {

        case "symmetryPerspectives":
            return this .symmetries .keySet() .toArray( new String[]{} );

        case "field.irrationals":
            return AlgebraicField .getIrrationals( this.documentModel .getField() );

        case "field.multipliers":
            return AlgebraicField .getMultipliers( this.documentModel .getField() );

        case "affinePolygon.labels":
            return AffinePolygon.getPolygonModes(this.documentModel.getField()).sequencedValues()
                    .toArray(new String[0]);

        case "affinePolygon.modes": {
            ArrayList<String> modes = new ArrayList<>();
            for (Integer mode : AffinePolygon.getPolygonModes(this.documentModel.getField()).sequencedKeySet()) {
                modes.add(mode.toString());
            }
            return modes.toArray(new String[modes.size()]);
        }

        default:
            return super.getCommandList( listName );
        }
    }
    

    public void doManifestationAction( RenderedManifestation picked, String action )
    {

        try {
            switch ( action ) {

            case "setBuildOrbitAndLength": {
                AlgebraicNumber length = picked .getStrutLength();
                Direction orbit = picked .getStrutOrbit();
                symmetryController .availableController .doAction( "enableDirection." + orbit .getName() );
                symmetryController .buildController .doAction( "setSingleDirection." + orbit .getName() );
                LengthController lmodel = (LengthController) symmetryController .buildController .getSubController( "currentLength" );
                lmodel .setActualLength( length );
                break;
            }
            
            case "CreateStrutPrototype": {
                // This command was primarily designed to help 
                // while adding new directions to FieldApplications.
                // CreateStrutAxisPlus0 and CreateStrutPrototype 
                // are identical except for the way the zone is chosen.
                // Since during this stage, the prototype vector and unit length may differ
                // from their eventual values, I don't want to add a command in core to do this
                // because a file with that command in it would potentially fail if the prototype is changed.
                // For that reason, I want the command history to look the same 
                // as if the strut were generated with the usual mouse drag paradigm. 
                Map<String,Object> props = new HashMap<>();
                props .put( "anchor", new FreePoint(symmetryController.getSymmetry().getField().origin(3)) );
                props .put( "zone", symmetryController .getZone( picked.getStrutOrbit().getPrototype() ) );
                props .put( "length", picked.getStrutOrbit().getUnitLength() );
                documentModel .doEdit( "StrutCreation", props );
                break;
            }

            case "CreateStrutAxisPlus0": {
                // This command was primarily designed to help
                // with adding new directions to FieldApplications.
                // Specifically it can be helpful in determining 
                // an appropriate unitLength for createZoneOrbit().
                // CreateStrutAxisPlus0 and CreateStrutPrototype 
                // are identical except for the way the zone is chosen.
                // For most directions the two are equivalent
                // but for a few legacy directions, they differ.
                // See FieldApplicationTest.testDirectionAxisVsPrototype()
                // for a list of those that differ.
                Map<String,Object> props = new HashMap<>();
                props .put( "anchor", new FreePoint(symmetryController.getSymmetry().getField().origin(3)) );
                props .put( "zone", symmetryController.getZone(picked.getStrutOrbit().getAxis(Symmetry.PLUS, 0).normal()));
                props .put( "length", picked.getStrutOrbit().getUnitLength() );
                documentModel .doEdit( "StrutCreation", props );
                break;
            }

            // This is only for manual testing of the FreeMove edit, needed for VR grabbing.
            case "testMoveAndRotate": {
                Direction orbit = picked .getStrutOrbit();
                AlgebraicNumber length = picked .getStrutLength();
                
                Strut strut = (Strut) picked .getManifestation();

                Map<String,Object> props = new HashMap<>();
                props .put( "oldStrut", strut );
                props .put( "anchor", new FreePoint( strut .getEnd() ) ); // move the strut to its own end location
                props .put( "zone", orbit .getAxis( 0, 0 ) ); // always clone as the +0 zone, for this test
                props .put( "length", length );
                documentModel .doEdit( "StrutMove", props );
                break;
            }

            default:
                doManifestationAction( picked .getManifestation(), action );
            }
        } catch ( Command.Failure failure ) {
            // signal an error to the user
            mErrors.reportError( USER_ERROR_CODE, new Object[] { failure } );
        } catch ( Exception re ) {
            Throwable cause = re.getCause();
            if ( cause instanceof Command.Failure )
                mErrors.reportError( USER_ERROR_CODE, new Object[] { cause } );
            else
                mErrors.reportError( UNKNOWN_ERROR_CODE, new Object[] { re } );
        } 
    }
    

    public void doManifestationAction( Manifestation pickedManifestation, String action )
    {
        Construction singleConstruction = null;
        if ( pickedManifestation != null )
            singleConstruction = pickedManifestation .toConstruction();

        switch ( action ) {

        case "undoToManifestation":
            this .documentModel .undoToManifestation( pickedManifestation );
            break;

        case "setWorkingPlaneAxis":
            this .strutBuilder .setWorkingPlaneAxis( ((Segment) singleConstruction).getOffset() );
            break;

        case "setWorkingPlane":
            this .strutBuilder .setWorkingPlaneAxis( ((Polygon) singleConstruction ).getNormal() );
            break;

        case "lookAtThis":
            RealVector loc = documentModel .getCentroid( pickedManifestation );
            cameraController .setLookAtPoint( new Point3f( loc.x, loc.y, loc.z ) );
            break;

        default:
            documentModel .doPickEdit( pickedManifestation, action );
        }
    }

    public String getManifestationProperty( Manifestation pickedManifestation, String propName )
    {
        boolean devExtras = userHasEntitlement( "developer.extras" );
        switch ( propName ) {

        case "objectProperties":
            StringBuffer buf = new StringBuffer();
            if ( pickedManifestation != null ) {
                final NumberFormat FORMAT = NumberFormat .getNumberInstance( Locale .US );
                OrbitSource symmetry  = this .documentModel .getEditorModel() .getSymmetrySystem();
                Manifestation man = pickedManifestation;
                Axis zone = null;
                if (man instanceof Connector) {
                    AlgebraicVector loc = man.getLocation();
                    if(devExtras) {
                        System.out.println(loc.getVectorExpression(AlgebraicField.EXPRESSION_FORMAT));
                        System.out.println(loc.getVectorExpression(AlgebraicField.ZOMIC_FORMAT));
                        System.out.println(loc.getVectorExpression(AlgebraicField.VEF_FORMAT));
                    }
                    buf.append("location: ");
                    loc.getVectorExpression(buf, AlgebraicField.DEFAULT_FORMAT);
                    
                    if( devExtras && ! loc.isOrigin()) {
                        AlgebraicNumber normSquared = loc.dot(loc);
                        double norm2d = normSquared.evaluate();
                        buf.append("\n\nquadrance: ");
                        normSquared.getNumberExpression(buf, AlgebraicField.DEFAULT_FORMAT);
                        buf.append(" = ");
                        buf.append(FORMAT.format(norm2d));

                        buf.append("\n\nradius: ");
                        buf.append(FORMAT.format(Math.sqrt(norm2d)));
                    }
                } else if (man instanceof Strut) {
                    buf.append("start: ");
                    Strut strut = Strut.class.cast(man);
                    strut.getLocation().getVectorExpression(buf, AlgebraicField.DEFAULT_FORMAT);
                    
                    buf.append("\n\nend: ");
                    strut.getEnd().getVectorExpression(buf, AlgebraicField.DEFAULT_FORMAT);
                    
                    buf.append("\n\noffset: ");
                    AlgebraicVector offset = strut.getOffset();
                    if (offset.isOrigin()) {
                        return "zero length!";
                    }
                    if (devExtras) {
                        System.out.println(offset.getVectorExpression(AlgebraicField.EXPRESSION_FORMAT));
                        System.out.println(offset.getVectorExpression(AlgebraicField.ZOMIC_FORMAT));
                        System.out.println(offset.getVectorExpression(AlgebraicField.VEF_FORMAT));
                    }
                    offset.getVectorExpression(buf, AlgebraicField.DEFAULT_FORMAT);
                    buf.append("\n\nnorm squared: ");
                    AlgebraicNumber normSquared = offset.dot(offset);
                    double norm2d = normSquared.evaluate();
                    normSquared.getNumberExpression(buf, AlgebraicField.DEFAULT_FORMAT);
                    buf.append(" = ");
                    buf.append(FORMAT.format(norm2d));

                    zone = symmetry.getAxis(offset);
                    Direction direction = zone.getDirection();
                    buf.append("\n\norbit: ");
                    buf.append( direction.getCanonicalName() );
                    buf.append(" = ");
                    if (direction.isAutomatic()) {
                        buf.append("Automatic ");
                    }
                    buf.append( direction.getName() );

                    AlgebraicNumber len = zone.getLength(offset);
                    len = zone.getOrbit().getLengthInUnits(len);

                    buf.append("\n\nlength in orbit units: ");
                    len.getNumberExpression(buf, AlgebraicField.DEFAULT_FORMAT);

                    if (this .documentModel.getField() instanceof PentagonField) {
                        buf.append("\n\nlength in Zome b1 struts: ");
                        if (FORMAT instanceof DecimalFormat) {
                            ((DecimalFormat) FORMAT).applyPattern("0.0000");
                        }
                        buf.append(FORMAT.format(Math.sqrt(norm2d) / PentagonField.B1_LENGTH));
                    }
                } else if (man instanceof Panel) {
                    Panel panel = Panel.class.cast(man);

                    buf.append("vertices: ");
                    buf.append(panel.getVertexCount());

                    String delim = "";
                    for (AlgebraicVector vertex : panel) {
                        buf.append(delim);
                        buf.append("\n  ");
                        vertex.getVectorExpression(buf, AlgebraicField.DEFAULT_FORMAT);
                        delim = ",";
                    }

                    AlgebraicVector normal = panel.getNormal();
                    buf.append("\n\nnormal: ");
                    normal.getVectorExpression(buf, AlgebraicField.DEFAULT_FORMAT);
                    if (devExtras) {
                        System.out.println(normal.getVectorExpression(AlgebraicField.EXPRESSION_FORMAT));
                        System.out.println(normal.getVectorExpression(AlgebraicField.ZOMIC_FORMAT));
                        System.out.println(normal.getVectorExpression(AlgebraicField.VEF_FORMAT));
                    }
                    buf.append("\n\nnorm squared: ");
                    AlgebraicNumber normSquared = normal.dot(normal);
                    double norm2d = normSquared.evaluate();
                    normSquared.getNumberExpression(buf, AlgebraicField.DEFAULT_FORMAT);
                    buf.append(" = ");
                    buf.append(FORMAT.format(norm2d));

                    zone = symmetry.getAxis(normal);
                    Direction direction = zone.getDirection();
                    buf.append("\n\norbit: ");
                    buf.append( direction.getCanonicalName() );
                    buf.append(" = ");
                    if (direction.isAutomatic()) {
                        buf.append("Automatic ");
                    }
                    buf.append( direction.getName() );
                } else {
                    // should never get here
                    return man.getClass().getSimpleName();
                }
                buf.append("\n\ncolor (RGB): ");
                buf.append(man.getColor().toString());
                
                String label = man.getLabel();
                if(label != null) {
                    buf.append("\n\nlabel: ");
                    buf.append(label);
                }
                
                if( devExtras) {
                    if( zone != null) {
                        buf.append( "\n\nzone: " + zone .toString() );
                        buf.append( "\n\nrotation: " + zone .getCorrectRotation() );
                        buf.append( "\n\norientation: " + zone .getOrientation() );
                        buf.append( "\n\nsense: " + zone .getSense() );
                        buf.append( "\n\nprototype: " + zone.getDirection().getPrototype() );
                        buf.append( "\n\ncentroid: " + man .getCentroid() );
                    }
                    System .out .println(buf.toString().replace("\n\n", "\n"));
                    System .out .println();
                }
                pickedManifestation = null;
                return buf.toString();
            } else {
                buf.append( "field: " );
                buf.append( this.getProperty("field.label" ));

                buf.append( "\n\nsymmetry: " );
                buf.append( cameraController.getProperty( "symmetry" ) );

                buf.append("\n\nbackground color (RGB): ");
                buf.append(this .sceneLighting .getBackgroundColor() .toString());

                if( propertyIsTrue("show.camera.properties") ) {
                    buf.append( "\n\nlook at point: " );
                    buf.append( cameraController.getProperty( "lookAtPoint" ) );

                    buf.append( "\n\nlook direction: " );
                    buf.append( cameraController.getProperty( "lookDir" ) );
                    buf.append( "\n  up direction: " );
                    buf.append( cameraController.getProperty( "upDir" ) );
                    
                    buf.append( "\n\nview distance: " );
                    buf.append( cameraController.getProperty( "viewDistance" ) );
                    
                    buf.append( "\n\nmagnification: " );
                    buf.append( cameraController.getProperty( "magnification" ) );
                }
               
                System .out .println(buf.toString().replace("\n\n", "\n"));
                System .out .println();
                return buf.toString();
            }

        case "objectColor":
            if ( pickedManifestation != null ) {
                String colorStr = pickedManifestation .getColor() .toString();
                pickedManifestation = null;
                return colorStr;
            }
            // TODO: We could return the background color here
            return null;

        default:
            return this .getProperty( propName );
        }
    }

    public DocumentModel getModel()
    {
        return this .documentModel;
    }

    public SymmetryController getSymmetryController()
    {
        return this .symmetryController;
    }

    @Override
    public Scene getScene()
    {
        return this .mainScene;
    }
}
