package org.vorthmann.zome.app.impl;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.RenderedImage;
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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.vecmath.Point3d;

import org.vorthmann.j3d.MouseTool;
import org.vorthmann.j3d.MouseToolDefault;
import org.vorthmann.j3d.MouseToolFilter;
import org.vorthmann.j3d.Trackball;
import org.vorthmann.ui.Controller;
import org.vorthmann.ui.DefaultController;
import org.vorthmann.ui.LeftMouseDragAdapter;
import org.vorthmann.zome.app.impl.PartsController.PartInfo;
import org.vorthmann.zome.ui.PartsPanel.PartsPanelActionEvent;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;
import com.vzome.core.editor.DocumentModel;
import com.vzome.core.editor.FieldApplication.SymmetryPerspective;
import com.vzome.core.editor.SymmetrySystem;
import com.vzome.core.exporters.Exporter3d;
import com.vzome.core.exporters2d.Java2dSnapshot;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.ManifestationChanges;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;
import com.vzome.core.render.Color;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.render.RenderedModel.OrbitSource;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.viewing.Camera;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.ThumbnailRenderer;
import com.vzome.desktop.controller.CameraController;
import com.vzome.desktop.controller.Controller3d;
import com.vzome.desktop.controller.RenderingViewer;
import com.vzome.desktop.controller.ThumbnailRendererImpl;

/**
 * @author Scott Vorthmann 2003
 */
public class DocumentController extends DefaultController implements Controller3d
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
    private RenderingChanges mainScene;
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
    private Map<String,SymmetryController> symmetries = new HashMap<>();
    private final PartsController partsController;

    // to SelectionController (subclass of RenderedModelController?)
    //
    private MouseTool selectionClick;
    private boolean mRequireShift = false;
    private final ManifestationChanges selectionRendering;
    
    private final StrutBuilderController strutBuilder;

    // to LessonController
    //
    private ThumbnailRenderer thumbnails;
    private MouseTool lessonPageClick, articleModeMainTrackball, articleModeZoom;
    private final LessonController lessonController;
    private PropertyChangeListener articleChanges;
    private RenderedModel currentSnapshot;
    
    private PropertyChangeListener modelChanges;
        
    private final ClipboardController systemClipboard;
    private String designClipboard;
    
    private final NumberController importScaleController;
        
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
        
        this .addSubController( "polytopes", new PolytopesController( this .documentModel ) );
        
        this .addSubController( "undoRedo", new UndoRedoController( this .documentModel .getHistoryModel() ) );
                
        importScaleController = new NumberController( this .documentModel .getField() );
        this .addSubController( "importScale", importScaleController );
        
        mRenderedModel = new RenderedModel( this .documentModel .getField(), true );
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
        };
        this .documentModel .addSelectionListener( selectionRendering );
        
        this .addSubController( "measure", new MeasureController( this .documentModel .getEditorModel(), this .documentModel .getRenderedModel() ) );

        this .articleChanges = new PropertyChangeListener()
        {   
            @Override
            public void propertyChange( PropertyChangeEvent change )
            {
                if ( "currentSnapshot" .equals( change .getPropertyName() ) )
                {
                    // contents of old "renderSnapshot" action
                    RenderedModel newSnapshot = (RenderedModel) change .getNewValue();
                    if ( newSnapshot != currentSnapshot )
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

        sceneLighting = new Lights( app .getLights() );  // TODO: restore the ability for the document to override

        cameraController = new CameraController( document .getCamera() );
        this .addSubController( "camera", cameraController );
        this .articleModeZoom = this .cameraController .getZoomScroller();

        strutBuilder = new StrutBuilderController( this, cameraController )
                .withGraphicalViews( app .propertyIsTrue( "useGraphicalViews" ) )
                .withShowStrutScales( app .propertyIsTrue( "showStrutScales" ) );
        this .addSubController( "strutBuilder", strutBuilder );
        
        for ( SymmetryPerspective symper : document .getFieldApplication() .getSymmetryPerspectives() )
        {
            String name = symper .getName();
            SymmetryController symmController = new SymmetryController( strutBuilder, this .documentModel .getSymmetrySystem( name ) );
            strutBuilder .addSubController( "symmetry." + name, symmController );
            this .symmetries .put( name, symmController );
        }

        mRequireShift = "true".equals( app.getProperty( "multiselect.with.shift" ) );
        showFrameLabels = "true" .equals( app.getProperty( "showFrameLabels" ) );

        thumbnails = new ThumbnailRendererImpl( app .getJ3dFactory() );

        mApp = app;
        
        lessonController = new LessonController( this .documentModel .getLesson(), cameraController );
        this .addSubController( "lesson", lessonController );

        setSymmetrySystem( this .documentModel .getSymmetrySystem() );

        // can't do this before the setSymmetrySystem() call just above
        if ( mRenderedModel != null )
        {
            this .documentModel .setRenderedModel( mRenderedModel );
            this .currentSnapshot = mRenderedModel;  // Not too sure if this is necessary
        }

        partsController = new PartsController( symmetryController .getOrbitSource() );
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
                actionPerformed( new ActionEvent( e .getSource(), ActionEvent.ACTION_PERFORMED, "nextPage" ) );
                e .consume();
            }
        };

        articleModeMainTrackball = cameraController .getTrackball();
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
    public void attachViewer( RenderingViewer viewer, RenderingChanges scene, Component canvas )
    {
    		// This is called on a UI thread!
        this .modelCanvas = canvas;
        this .mainScene = scene;
        this .imageCaptureViewer = viewer;

//      leftEyeCanvas = rvFactory .createJ3dComponent( "" );
//      RenderingViewer viewer = rvFactory .createRenderingViewer( mainScene, leftEyeCanvas );
//      mViewPlatform .addViewer( viewer );
//      viewer .setEye( RenderingViewer .LEFT_EYE );
//      leftController = new PickingController( viewer, this );
//
//      rightEyeCanvas = rvFactory .createJ3dComponent( "" );
//      viewer = rvFactory .createRenderingViewer( mainScene, rightEyeCanvas );
//      mViewPlatform .addViewer( viewer );
//      viewer .setEye( RenderingViewer .RIGHT_EYE );
//      rightController = new PickingController( viewer, this );

        if ( this .mainScene instanceof PropertyChangeListener )
            this .addPropertyListener( (PropertyChangeListener) this .mainScene );

        
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

        this .strutBuilder .attach( viewer, scene );
        
        if ( this .modelCanvas != null )
            if ( editingModel ) {
                this .selectionClick .attach( modelCanvas );
                this .modelModeMainTrackball .attach( modelCanvas );
                this .strutBuilder .attach( modelCanvas );
            } else {
                this .articleModeMainTrackball .attach( modelCanvas );
                this .articleModeZoom .attach( modelCanvas );
                this .lessonPageClick .attach( modelCanvas );
            }
    }

    private SymmetryController getSymmetryController( String name )
    {
        SymmetryController result = this .symmetries .get( name );
        return result;
    }


    private void setSymmetrySystem( SymmetrySystem symmetrySystem )
    {
        String name =  symmetrySystem .getName();
        symmetryController = getSymmetryController( name );
        if(symmetryController == null) {
            String msg = "Unsupported symmetry: " + name;
            mErrors.reportError(msg, new Object[] {} );
            throw new IllegalStateException( msg );
        }

        String modelResourcePath = this .symmetryController .getProperty( "modelResourcePath" );
        RenderedModel model = this .mApp .getSymmetryModel( modelResourcePath, symmetrySystem .getSymmetry() );
        cameraController .setSymmetry( model, symmetryController .getSnapper() );

        firePropertyChange( "symmetry", null, name ); // notify UI, so cardpanel can flip, or whatever
        setRenderingStyle();
    }

    private void setRenderingStyle()
    {
        if ( mRenderedModel != null ) {
            if ( partsController != null )
                partsController .startSwitch( symmetryController .getOrbitSource() );
            mRenderedModel .setOrbitSource( symmetryController .getOrbitSource() );
            if ( partsController != null )
                partsController .endSwitch();
        }
        strutBuilder .setSymmetryController( symmetryController );
    }


    @Override
    public void doAction( String action, ActionEvent e ) throws Failure
    {
        if ( "finish.load".equals( action ) ) {

            boolean openUndone = propertyIsTrue( "open.undone" );
            boolean asTemplate = propertyIsTrue( "as.template" );

            // used to finish loading a model history on a non-UI thread
            this .documentModel .finishLoading( openUndone, asTemplate );
                        
            // mainScene is not listening to mRenderedModel yet, so batch the rendering changes to it
            if ( mainScene != null )
            {
                if ( editingModel )
                {
                    RenderedModel .renderChange( new RenderedModel( null, null ), mRenderedModel, mainScene );
                    mRenderedModel .addListener( mainScene );
                    // get the thumbnails updating in the background
                    if ( lessonController != null )
                        lessonController .renderThumbnails( documentModel, thumbnails );
                }
                else
                    try {
                        currentSnapshot = new RenderedModel( null, null ); // force render of first snapshot, see "renderSnapshot." below
                        lessonController .doAction( "restoreSnapshot", new ActionEvent( this, 0, "restoreSnapshot" ) );
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
            switch (action) {

            case "switchToArticle":
                {
                    this .currentView = this .cameraController .getView();
                    
                    this .selectionClick .detach( this .modelCanvas );
                    this .strutBuilder .detach( this .modelCanvas );
                    this .modelModeMainTrackball .detach( this .modelCanvas );
                    
                    this .lessonPageClick .attach( this .modelCanvas );
                    this .articleModeMainTrackball .attach( this .modelCanvas );
                    this .articleModeZoom .attach( this .modelCanvas );
    
                    this .documentModel .addPropertyChangeListener( this .articleChanges );
                    this .documentModel .removePropertyChangeListener( this .modelChanges );
                    this .lessonController .doAction( "restoreSnapshot", e );
    
                    this .editingModel = false;
                    firePropertyChange( "editor.mode", "model", "article" );
                }
                break;

            case "takeSnapshot":
                documentModel .addSnapshotPage( cameraController .getView() );
                break;

            case "test.pick.cube":
//              // just a test of Bounds picking
//          {
//              Collection rms = imageCaptureViewer.pickCube();
//              for ( Iterator it = rms.iterator(); it.hasNext(); ) {
//                  RenderedManifestation rm = (RenderedManifestation) it.next();
//                  Manifestation targetManifestation = null;
//                  if ( rm != null && rm.isPickable() )
//                      targetManifestation = rm.getManifestation();
//                  else
//                      continue;
//                  document .selectManifestation( targetManifestation, true ); // NOT UNDOABLE!
//              }
//          }
                break;

            case "refresh.2d":
                this .java2dController .setScene( cameraController.getView(), this.sceneLighting, this.currentSnapshot, this.drawOutlines );
                break;

            case "nextPage":
                lessonController .doAction( action, e );
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
                    this .modelModeMainTrackball .attach( this .modelCanvas );
                    this .strutBuilder .attach( this .modelCanvas );
    
                    this .editingModel = true;
                    firePropertyChange( "editor.mode", "article", "model" );
                }
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
                cameraController.setLookAtPoint( new Point3d( 0, 0, 0 ) );
                break;
    
            case "lookAtSymmetryCenter":
                {
                    RealVector loc = documentModel .getParamLocation( "ball" );
                    cameraController .setLookAtPoint( new Point3d( loc.x, loc.y, loc.z ) );
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
                    symmetryController .availableController .doAction( "setNoDirections", null );
                    for ( Direction orbit : usedOrbits ) {
                        symmetryController .availableController .doAction( "enableDirection." + orbit .getName(), null );
                    }
                }
                break;
                
            case "delete":
            case "Delete":
                documentModel .doEdit( "Delete" );
                break;
    
            case "cut":
                {
                    setProperty( "clipboard", documentModel .copySelectionVEF() );
                    documentModel .doEdit( "Delete" );
                }
                break;
    
            case "copy":
                setProperty( "clipboard", documentModel .copySelectionVEF() );
                break;
    
            case "copy.observable":
                setProperty( "clipboard", documentModel .copyRenderedModel( "observable" ) );
                break;
    
            case "copy.vson":
                setProperty( "clipboard", documentModel .copyRenderedModel( "vson" ) );
                break;
    
            case "paste":
                {
                    String vefContent = getProperty( "clipboard" );
                    Map<String, Object> params = new HashMap<>();
                    params .put( "vef", vefContent );
                    documentModel .doEdit( "LoadVEF/clipboard", params );
                }
                break;
    
            default:
                if ( action.startsWith( "setStyle." ) )
                    setRenderingStyle();
    
                else if ( action.startsWith( "setSymmetry." ) ) {
                    String system = action.substring( "setSymmetry.".length() );
                    this .documentModel .setSymmetrySystem( system );
                    setSymmetrySystem( this .documentModel .getSymmetrySystem() );
                }
    
                else if ( e != null && PartsPanelActionEvent.class.isAssignableFrom(e.getClass()) )
                {
                    // this is a select or deselect command from the PartsPanel context menu
                    PartsPanelActionEvent ppae = PartsPanelActionEvent.class.cast(e);
                    PartInfo partInfo = ppae.row.partInfo;
                    String cmd = action.toLowerCase();
                    switch(ppae.row.partClassGroupingOrder) {
                        case BALLS_TOTAL:
                            documentModel .doEdit( "AdjustSelectionByClass/" + cmd + "Balls" );
                            break;
    
                        case STRUTS_TOTAL:
                            documentModel .doEdit( "AdjustSelectionByClass/" + cmd + "Struts" );
                            break;
    
                        case PANELS_TOTAL:
                            documentModel .doEdit( "AdjustSelectionByClass/" + cmd + "Panels" );
                            break;
    
                        case STRUTS:
                        {
                            Direction orbit = symmetryController.getOrbits().getDirection(partInfo.orbitStr);
                            AlgebraicNumber unitScalar = orbit.getLengthInUnits(symmetryController.getSymmetry().getField().one());
                            AlgebraicNumber rawLength = partInfo.strutLength.dividedBy(unitScalar);
                            Map<String,Object> props = new HashMap<>();
                            props .put( "orbit", orbit );
                            if ( rawLength != null )
                                props .put( "length", rawLength );
                            documentModel .doEdit( "AdjustSelectionByOrbitLength/" + cmd + "SimilarStruts", props );
                        }
                        break;
    
                        case PANELS:
                        {
                            Direction orbit = symmetryController.getOrbits().getDirection(partInfo.orbitStr);
                            Map<String,Object> props = new HashMap<>();
                            props .put( "orbit", orbit );
                            documentModel .doEdit( "AdjustSelectionByOrbitLength/" + cmd + "SimilarPanels", props );
                        }
                        break;
    
                        default:
                            break;
                    }
                }
                else
                {
                    boolean handled = documentModel .doEdit( action );
                    if ( ! handled )
                        super .doAction( action, e );
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
        if ( command.equals( "RunZomicScript" ) || command.equals( "RunPythonScript" ) )
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
        try {
            final Colors colors = mApp.getColors();

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
                    for ( String captureOrExport : exports .split( " " ) ) {
                        // captureOrExport should be "capture.png" or "export.dae" or similar
                        String extension = "";
                        String[] cmd = captureOrExport .split("\\.");
                        if(cmd.length == 2 ) {
                            switch (cmd[0]) {
                            case "capture":
                            case "export2d":
                            case "export":
                                extension = cmd[1];
                                break;
                            }
                        }
                        if(extension == "") {
                            mErrors.reportError( UNKNOWN_PROPERTY + " save.exports=" + captureOrExport, null );
                        } else {
                            File exportFile = new File( dir, file .getName() + "." + extension );
                            doFileAction( captureOrExport, exportFile );
                        }
                    }
                }

                String script = this .getProperty( "save.script" );
                if ( script != null )
                {
                    try {
                        Runtime .getRuntime() .exec( script + " " + file .getAbsolutePath(),
                                null, file .getParentFile() );
                    } catch ( IOException e ) {
                        System .err .println( "Runtime.exec() failed on " + file .getAbsolutePath() );
                        e .printStackTrace();
                    }
                }
                return;
            }
            if ( "capture-animation" .equals( command ) )
            {
                File dir = file .isDirectory()? file : file .getParentFile();
                Dimension size = this .modelCanvas .getSize();              
                String html = readResource( "org/vorthmann/zome/app/animation.html" );
                html = html .replaceFirst( "%%WIDTH%%", Integer .toString( size .width ) );
                html = html .replaceFirst( "%%HEIGHT%%", Integer .toString( size .height ) );
                File htmlFile = new File( dir, "index.html" );
                writeFile( html, htmlFile );
                String js = readResource( "org/vorthmann/zome/app/j360-loop.js" );
                writeFile( js, new File( dir, "j360-loop.js" ) );

                AnimationCaptureController animation = new AnimationCaptureController( this .cameraController, dir );
                captureImageFile( null, AnimationCaptureController.TYPE, animation );
                this .openApplication( htmlFile );
                return;
            }
            if ( command.startsWith( "capture." ) )
            {
                final String extension = command .substring( "capture.".length() );
                captureImageFile( file, extension, null );
                return;
            }
            //            if ( command .equals( "export.zomespace" ) )
            //            {
            //                new ZomespaceExporter( file ) .exportArticle( document, colors, sceneLighting, getSaveXml(), getProperty( "edition" ), getProperty( "version" ) );
            //            } else
            if ( command.startsWith( "export2d." ) )
            {
                Dimension size = this .modelCanvas .getSize();              
                String format = command .substring( "export2d." .length() ) .toLowerCase();
                Java2dSnapshot snapshot = documentModel .capture2d( currentSnapshot, size.height, size.width, cameraController .getView(), sceneLighting, false, true );
                documentModel .export2d( snapshot, format, file, this .drawOutlines, false );
                this .openApplication( file );
                return;
            }
            if ( command.startsWith( "export." ) )
            {
                Writer out = new FileWriter( file );
                Dimension size = this .modelCanvas .getSize();              
                try {
                    String format = command .substring( "export." .length() ) .toLowerCase();
                    Exporter3d exporter = documentModel .getNaiveExporter( format, cameraController .getView(), colors, sceneLighting, currentSnapshot );
                    if ( exporter != null ) {
                        exporter.doExport( file, file.getParentFile(), out, size.height, size.width );
                    }
                    else {
                        exporter = this .mApp .getExporter( format );
                        if ( exporter == null ) {
                            // currently just "partgeom"
                            exporter = documentModel .getStructuredExporter( format, cameraController .getView(), colors, sceneLighting, mRenderedModel );
                        }
                        if ( exporter != null )
                            exporter .doExport( documentModel, file, file.getParentFile(), out, size.height, size.width );
                    }
                } finally {
                    out.close();
                }
                this .openApplication( file );
                return;
            }
            if ( command.startsWith( "LoadVEF/" ) ) {
                String vefData = readFile( file );
                Map<String, Object> params = new HashMap<>();
                params .put( "vef", vefData );
                params .put( "scale", this .importScaleController .getValue() );
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

    private void captureImageFile( final File file, final String extension, final AnimationCaptureController animation )
    {
        String maxSizeStr = getProperty( "max.image.size" );
        final int maxSize = ( maxSizeStr != null )? Integer .parseInt( maxSizeStr ) :
                                ( animation != null )? animation .getImageSize() : -1; // Animation images can't be too big
        if ( animation != null ) {
            animation .rotate();
        }
        imageCaptureViewer .captureImage( maxSize, new RenderingViewer.ImageCapture()
        {
            private void setImageCompression(String format, ImageWriteParam iwParam)
            {
                if (iwParam.canWriteCompressed()) {
                    // Ensure that the compressionType we want to use is supported
                    String[] preferedTypes = null; // Listed in preferred order
                    switch (format) {
                        case "BMP":
                            preferedTypes = new String[]{
                                "BI_RGB",       // BI_RGB seems to result in a smaller file than BI_BITFIELDS in Windows 10
                                "BI_BITFIELDS", // OK
                                // "BI_PNG",    // File is created OK, but can't be opened by default viewer "Photos" in Windows 10
                                // "BI_JPEG",   // File is created OK, but can't be opened by default viewer "Photos" in Windows 10
                                // "BI_RLE8",   // IOException: Image can not be encoded with compression type BI_RLE8
                                // "BI_RLE4",   // IOException: Image can not be encoded with compression type BI_RLE4
                            };
                            break;

                        case "GIF":
                            preferedTypes = new String[]{
                                "lzw",
                                "LZW",
                            };
                            break;

                        case "JPEG":
                            preferedTypes = new String[]{
                                "JPEG",
                            };
                            break;
                    }
                    if (preferedTypes != null) {
                        String[] compressionTypes = iwParam.getCompressionTypes();
                        String chosenType = null;
                        for (String preferredType : preferedTypes) {
                            for (String compressionType : compressionTypes) {
                                if (compressionType.equals(preferredType)) {
                                    chosenType = preferredType;
                                    break;
                                }
                            }
                            if (chosenType != null) {
                                break;
                            }
                        }
                        if (chosenType != null) {
                            System.out.println(format + " compression set to " + chosenType);
                            iwParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                            iwParam.setCompressionType(chosenType); // this default is better for BMP, to avoid non-compression
                            iwParam.setCompressionQuality(.95f);
                        }
                    }
                }
            }

            @Override
            public void captureImage( final RenderedImage image )
            {
                String format = extension.toUpperCase();
                if ( format.equals( "JPG" ) )
                    format = "JPEG";
                try {
                    ImageWriter writer = ImageIO.getImageWritersByFormatName( format ) .next();
                    ImageWriteParam iwParam = writer .getDefaultWriteParam();
                    this.setImageCompression(format, iwParam);
                    File thisFile = ( animation != null ) ? animation .nextFile() : file;
                    
                    // A try-with-resources block closes the resource even if an exception occurs
                    try (ImageOutputStream ios = ImageIO.createImageOutputStream( thisFile )) {
                        writer .setOutput( ios );
                        writer .write( null, new IIOImage( image, null, null), iwParam );
                        writer .dispose(); // disposing of the writer doesn't close ios
                        // ios is closed automatically by exiting the try-with-resources block
                        // either normally or due to an exception
                        // If this code is ever changed to not use the try-with-resources block
                        // then uncomment the following line so that ios will be explicitly closed
                        // ios.close();
                    }

                    if ( animation == null )
                        openApplication( file );
                    else if ( ! animation .finished() ) {
                        // queue up the next capture in the sequence
                        EventQueue .invokeLater( new Runnable(){

                            @Override
                            public void run() {
                                captureImageFile( null, extension, animation );
                            }});
                    }
                } catch (Exception e) {
                    mErrors.reportError( UNKNOWN_ERROR_CODE, new Object[] { e } );
                }
            }
        } );
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
    
    
    private static String readResource( String resourcePath )
    {
        InputStream stream = null;
        try {
            stream = DocumentController.class .getClassLoader() .getResourceAsStream( resourcePath );
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int num;
            while ( ( num = stream .read( buf, 0, 1024 )) > 0 )
                out .write( buf, 0, num );
            return new String( out .toByteArray() );
        } catch (Exception e) {
            return null;
        } finally {
            if ( stream != null )
                try {
                    stream .close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
    }


    private static void writeFile( String content, File file ) throws Exception
    {
        // A try-with-resources block closes the resource even if an exception occurs
        try (FileWriter writer = new FileWriter( file )) {
            writer .write( content );
        } catch (Exception ex) {
            throw ex;
        }
    }


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
            return symmetryController.getSymmetry().getName();

        case "field.name":
            return this .documentModel .getField() .getName();

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
                java2dController = new Java2dSnapshotController( this .documentModel, cameraController.getView(), this.sceneLighting,
                						this.currentSnapshot, this.drawOutlines );
                this .addSubController( name, java2dController );
            }
            return java2dController;
        }

        default:
            if ( name.startsWith( "symmetry." ) )
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
            // App controller is listening, will change its map
            firePropertyChange( "name", null, value );
        } else if ( "backgroundColor".equals( cmd ) ) {
            sceneLighting .setProperty( cmd, value );
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

        super .setModelProperty( cmd, value );
    }


    @Override
    public String[] getCommandList( String listName )
    {
        switch ( listName ) {

        case "symmetryPerspectives":
            return this .symmetries .keySet() .toArray( new String[]{} );

        case "field.irrationals":
        {
            AlgebraicField field = this .documentModel .getField();
            int len = field .getNumIrrationals();
            String[] result = new String[ len ];
            for ( int i = 0; i < result.length; i++ ) {
                result[ i ] = field .getIrrational( i+1 );
            }
            return result;
        }

        case "field.multipliers":
        {
            AlgebraicField field = this .documentModel .getField();
            int len = field. getNumMultipliers();
            String[] result = new String[ len ];
            for ( int i = 0; i < result.length; i++ ) {
                result[ i ] = field .getIrrational( i+1 );
            }
            return result;
        }

        default:
            return super.getCommandList( listName );
        }
    }
    

    public void doManifestationAction( Manifestation pickedManifestation, String action )
    {
        Construction singleConstruction = null;
        if ( pickedManifestation != null )
            singleConstruction = pickedManifestation .toConstruction();

        try {
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
                cameraController .setLookAtPoint( new Point3d( loc.x, loc.y, loc.z ) );
                break;

            case "setBuildOrbitAndLength": {
                AlgebraicVector offset = ((Strut) pickedManifestation) .getOffset();
                Axis zone = symmetryController .getZone( offset );
                Direction orbit = zone .getOrbit();
                AlgebraicNumber length = zone .getLength( offset );
                symmetryController .availableController .doAction( "enableDirection." + orbit .getName(), null );
                symmetryController .buildController .doAction( "setSingleDirection." + orbit .getName(), null );
                LengthController lmodel = (LengthController) symmetryController .buildController .getSubController( "currentLength" );
                lmodel .setActualLength( length );
                break;
            }

            default:
                documentModel .doPickEdit( pickedManifestation, action );
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

    public String getManifestationProperty( Manifestation pickedManifestation, String propName )
    {
        boolean devExtras = userHasEntitlement( "developer.extras" );
        switch ( propName ) {

        case "objectProperties":
            StringBuffer buf = new StringBuffer();
            if ( pickedManifestation != null ) {
                final NumberFormat FORMAT = NumberFormat .getNumberInstance( Locale .US );
                OrbitSource symmetry  = symmetryController .getOrbitSource();
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
                    buf.append("\n\ndirection: ");
                    if (direction.isAutomatic()) {
                        buf.append("Automatic ");
                    }
                    buf.append(direction.getName());

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
                    buf.append("\n\ndirection: ");
                    if (direction.isAutomatic()) {
                        buf.append("Automatic ");
                    }
                    buf.append(direction.getName());
                } else {
                    // should never get here
                    return man.getClass().getSimpleName();
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
               
                return buf.toString();
            }

        case "objectColor":
            if ( pickedManifestation != null ) {
                RenderedManifestation rm = pickedManifestation .getRenderedObject();
                String colorStr = rm .getColor() .toString();
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
}
