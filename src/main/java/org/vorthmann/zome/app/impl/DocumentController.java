package org.vorthmann.zome.app.impl;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;

import org.vorthmann.j3d.J3dComponentFactory;
import org.vorthmann.j3d.MouseTool;
import org.vorthmann.j3d.MouseToolDefault;
import org.vorthmann.j3d.MouseToolFilter;
import org.vorthmann.j3d.Platform;
import org.vorthmann.j3d.Trackball;
import org.vorthmann.ui.Controller;
import org.vorthmann.ui.DefaultController;
import org.vorthmann.ui.LeftMouseDragAdapter;
import org.vorthmann.zome.export.java2d.Java2dExporter;
import org.vorthmann.zome.export.java2d.Java2dSnapshot;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.HeptagonField;
import com.vzome.core.algebra.RootThreeField;
import com.vzome.core.algebra.RootTwoField;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;
import com.vzome.core.editor.DocumentModel;
import com.vzome.core.editor.SymmetrySystem;
import com.vzome.core.exporters.Exporter3d;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.ManifestationChanges;
import com.vzome.core.model.Strut;
import com.vzome.core.render.Color;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.viewing.Camera;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.ThumbnailRenderer;
import com.vzome.desktop.controller.CameraController;
import com.vzome.desktop.controller.RenderingViewer;
import com.vzome.desktop.controller.ThumbnailRendererImpl;

/**
 * Description here.
 * 
 * @author Scott Vorthmann 2003
 */
public class DocumentController extends DefaultController implements J3dComponentFactory
{
    private DocumentModel documentModel;
    
    private final PreviewStrut previewStrut;

    private final RenderedModel mRenderedModel;

    private RenderedModel currentSnapshot;

    private CameraController mViewPlatform;
    
    private Lights sceneLighting;
    
    private RenderingViewer imageCaptureViewer;
    
    private ThumbnailRenderer thumbnails;

    private RenderedModel mControlBallModel;

    private RenderingChanges mainScene;
    private final RenderingChanges mControlBallScene;

    private final ApplicationController mApp;

    private Java2dSnapshot mSnapshot = null;

    private boolean useGraphicalViews = false;
    private boolean showStrutScales = false;
    private boolean mRequireShift = false;
    private boolean drawOutlines = false;
    private boolean showFrameLabels = false;
    private boolean useWorkingPlane = false;

    private final LessonController lessonController;

    private final boolean startReader;
    
    private final ManifestationChanges selectionRendering;
    
    private PropertyChangeListener articleChanges, modelChanges;

    private final Properties properties;
        
    private SymmetryController symmetryController;
    
    private Segment workingPlaneAxis = null;
    
    private final ToolsController toolsController;
    
    private final PartsController partsController;

    private Map<String,SymmetryController> symmetries = new HashMap<>();

    private final ClipboardController systemClipboard;
    private String designClipboard;

    private boolean editingModel;

    private Camera currentView;

    private MouseTool lessonPageClick, articleModeMainTrackball, modelModeMainTrackball;

    private final Component modelCanvas, leftEyeCanvas, rightEyeCanvas;

    private MouseTool selectionClick, previewStrutStart, previewStrutRoll, previewStrutPlanarDrag;

    private final Controller polytopesController;

    private int changeCount = 0;

	private final PickingController monoController, leftController, rightController;
	    
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
        setNextController( app );

        this .properties = props;
        this .documentModel = document;
        
        if ( this .documentModel .isMigrated() )
            this .changeCount = -1; // this will force isEdited() to return true

        final boolean asTemplate = propertyIsTrue( "as.template" );

        final boolean newDocument = propertyIsTrue( "new.document" );
        
        drawOutlines = propertyIsTrue( "outline.geometry" );

        startReader = ! newDocument && ! asTemplate;
        
        editingModel = super.userHasEntitlement( "model.edit" ) ;//&& ! propertyIsTrue( "reader.preview" );
        
        systemClipboard = propertyIsTrue( "enable.system.clipboard" )
                ? new ClipboardController()
                : null;
        
        toolsController = new ToolsController( document );
        toolsController .setNextController( this );
        this .addPropertyListener( toolsController );
        
        polytopesController = new PolytopesController( this .documentModel );
        polytopesController .setNextController( this );
        
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
        			if ( ! newView .equals( mViewPlatform .getView() ) )
        				mViewPlatform .restoreView( newView );
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
            	case "tool.separator":
            	case "tool.instances":
        			properties() .firePropertyChange( change ); // forward to the UI for display
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

        // this seems backwards, I know... the TrackballViewPlatformModel is the main
        // model, and only forwards two events to trackballVPM
        mViewPlatform = new CameraController( document .getViewModel() );
        mViewPlatform .setNextController( this );

        mRequireShift = "true".equals( app.getProperty( "multiselect.with.shift" ) );
        useGraphicalViews = "true".equals( app.getProperty( "useGraphicalViews" ) );
        showStrutScales = "true" .equals( app.getProperty( "showStrutScales" ) );
        showFrameLabels = "true" .equals( app.getProperty( "showFrameLabels" ) );

        RenderingViewer.Factory rvFactory = app .getJ3dFactory();
        mainScene = rvFactory .createRenderingChanges( sceneLighting, true, this );
        this .addPropertyListener( (PropertyChangeListener) mainScene );

        modelCanvas = rvFactory .createJ3dComponent( "" ); // name not relevant there
        imageCaptureViewer = rvFactory.createRenderingViewer( mainScene, modelCanvas );
        mViewPlatform .addViewer( imageCaptureViewer );
        monoController = new PickingController( imageCaptureViewer, this );
        
        leftEyeCanvas = rvFactory .createJ3dComponent( "" );
        RenderingViewer viewer = rvFactory .createRenderingViewer( mainScene, leftEyeCanvas );
        mViewPlatform .addViewer( viewer );
        viewer .setEye( RenderingViewer .LEFT_EYE );
        leftController = new PickingController( viewer, this );

        rightEyeCanvas = rvFactory .createJ3dComponent( "" );
        viewer = rvFactory .createRenderingViewer( mainScene, rightEyeCanvas );
        mViewPlatform .addViewer( viewer );
        viewer .setEye( RenderingViewer .RIGHT_EYE );
        rightController = new PickingController( viewer, this );

        	// TODO define a standalone controller class for contextual menus, etc.
        Controller controlBallProps = new DefaultController()
        {
        	@Override
        	public String getProperty( String name )
        	{
        		switch ( name ) {

        		case "drawOutlines":
        			return "false";

        		case "showIcosahedralLabels":
        			if ( super .userHasEntitlement( "developer.extras" )
        					&& documentModel.getSymmetrySystem().getSymmetry() instanceof IcosahedralSymmetry ) {
        				return super.getProperty( "trackball.showIcosahedralLabels" );
        			} else
        				return "false";

        		default:
        			return super.getProperty( name );
        		}
        	}
        };
        controlBallProps .setNextController( this );
        mControlBallScene = rvFactory .createRenderingChanges( sceneLighting, true, controlBallProps );

        thumbnails = new ThumbnailRendererImpl( rvFactory, sceneLighting );

        mApp = app;

        AlgebraicField field = this .documentModel .getField();
        previewStrut = new PreviewStrut( field, mainScene, mViewPlatform );
        
        lessonController = new LessonController( this .documentModel .getLesson(), mViewPlatform );
        lessonController .setNextController( this );

        setSymmetrySystem( this .documentModel .getSymmetrySystem() );

        // can't do this before the setSymmetrySystem() call just above
        if ( mRenderedModel != null )
        {
        	this .documentModel .setRenderedModel( mRenderedModel );
        	this .currentSnapshot = mRenderedModel;  // Not too sure if this is necessary
        }

        partsController = new PartsController( symmetryController .getOrbitSource() );
        partsController .setNextController( this );
        mRenderedModel .addListener( partsController );

        copyThisView(); // initialize the "copied" view at startup.
    }

    @Override
    public Component createJ3dComponent( String name )
    {
        if ( name.startsWith( "mainViewer" ) )
        {
        	Component canvas = null;
        	if ( name .endsWith( "monocular" ) )
        		canvas = modelCanvas;
        	else if ( name .endsWith( "leftEye" ) )
        		canvas = leftEyeCanvas;
        	else
        		canvas = rightEyeCanvas;
        	
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
            MouseTool mouseTool = new MouseToolDefault()
            {
                @Override
                public void mouseClicked( MouseEvent e )
                {
                    actionPerformed( new ActionEvent( e .getSource(), ActionEvent.ACTION_PERFORMED, "nextPage" ) );
                    e .consume();
                }
            };
            if ( canvas == modelCanvas )
                lessonPageClick = mouseTool; // will not be attached, initially; gets attached on switchToArticle

            mouseTool = new MouseToolFilter( mViewPlatform .getZoomScroller() )
            {
                @Override
                public void mouseWheelMoved( MouseWheelEvent e )
                {
                    LengthController length = previewStrut .getLengthModel();
                    if ( length != null )
                    {
                        // scroll to scale the preview strut (when it is rendered)
                        length .getMouseTool() .mouseWheelMoved( e );
                        // don't adjustPreviewStrut() here, let the prop change trigger it,
                        // so we don't flicker for every tick of the mousewheel
                    }
                    else
                    {
                        // no strut build in progress, so zoom the view
                        super .mouseWheelMoved( e );
                    }
                }
            };
            mouseTool .attach( canvas );

            mouseTool = mViewPlatform .getTrackball();
            if ( propertyIsTrue( "presenter.mode" ) )
                ((Trackball) mouseTool) .setModal( false );
//            if ( ! editingModel )
//            {
//                // cannot use MouseTool .attach(), because it attaches a useless wheel listener,
//                //  and ViewPlatformControlPanel will attach a better one to the parent component 
//                canvas .addMouseListener( mouseTool );
//                canvas .addMouseMotionListener( mouseTool );
//            }
            if ( canvas == modelCanvas )
                articleModeMainTrackball = mouseTool; // will not be attached, initially; gets attached on switchToArticle

            // this wrapper for mainCanvasTrackball is disabled when the press is initiated over a ball
            mouseTool = new LeftMouseDragAdapter( new MouseToolFilter( articleModeMainTrackball )
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
            if ( canvas == modelCanvas )
                modelModeMainTrackball = mouseTool;
            if ( editingModel )
            	modelModeMainTrackball .attach( canvas );
            else
            	articleModeMainTrackball .attach( canvas );
            
            // clicks become select or deselect all
            mouseTool = new LeftMouseDragAdapter( new ManifestationPicker( imageCaptureViewer )
            {
                @Override
                protected void manifestationPicked( Manifestation target, boolean shiftKey )
                {
                    mErrors .clearError();
                    boolean shift = true;
                    if ( mRequireShift )
                        shift = shiftKey;
                    if ( target == null )
                        try {
                            documentModel .performAndRecord( documentModel .deselectAll() );
                        } catch ( Exception e ) {
                            mErrors .reportError( UNKNOWN_ERROR_CODE, new Object[] { e } );
                        }
                    else
                        documentModel .performAndRecord( documentModel .selectManifestation( target, ! shift ) );
                }
            } );
            if ( editingModel )
                mouseTool .attach( canvas );
            if ( canvas == modelCanvas )
                selectionClick = mouseTool;

            // drag events to render or realize the preview strut;
            //   only works when drag starts over a ball
            mouseTool = new LeftMouseDragAdapter( new ManifestationPicker( imageCaptureViewer )
            {                
                @Override
                protected void dragStarted( Manifestation target, boolean b )
                {
                    if ( target instanceof Connector )
                    {
                        mErrors .clearError();
                        Point point = (Point) target .getConstructions() .next();
                        AlgebraicVector workingPlaneNormal = null;
                        if ( useWorkingPlane && (workingPlaneAxis != null ) )
                        	workingPlaneNormal = workingPlaneAxis .getOffset();
                        previewStrut .startRendering( symmetryController, point, workingPlaneNormal );
                    }
                }

                @Override
				protected void dragFinished( Manifestation target, boolean b )
                {
                    previewStrut .finishPreview( documentModel );
                }
            } );
            if ( editingModel )
                mouseTool .attach( canvas );
            if ( canvas == modelCanvas )
                previewStrutStart = mouseTool;

            // trackball to adjust the preview strut (when it is rendered)
            mouseTool = new LeftMouseDragAdapter( new Trackball()
            {
                @Override
                protected void trackballRolled( Quat4d roll )
                {
                    previewStrut .trackballRolled( roll );
                }
            } );
            if ( editingModel )
                mouseTool .attach( canvas );
            if ( canvas == modelCanvas )
                previewStrutRoll = mouseTool;
            
            // working plane drag events to adjust the preview strut (when it is rendered)
            mouseTool = new LeftMouseDragAdapter( new MouseToolDefault()
            {
				@Override
				public void mouseDragged( MouseEvent e )
				{
					Point3d imagePt = new Point3d();
					Point3d eyePt = new Point3d();
					imageCaptureViewer .pickPoint( e, imagePt, eyePt );
					previewStrut .workingPlaneDrag( imagePt, eyePt );
				}
            } );
            if ( editingModel )
                mouseTool .attach( canvas );
            if ( canvas == modelCanvas )
                previewStrutPlanarDrag = mouseTool;
            
            // mRenderedModel .setFactory( mViewer .getSceneGraphFactory() );
            // mRenderedModel .setTopGroup( mViewer .getSceneGraphRoot() );

            mViewPlatform .updateViewers();
//            currentDesign .render( true, null );   // I think this is not necessary now
            return canvas;
        }
        else if ( name.equals( "controlViewer" ) )
        {
            MouseTool trackball = mViewPlatform .getTrackball();
            RenderingViewer.Factory rvFactory = mApp .getJ3dFactory();
            Component canvas = rvFactory .createJ3dComponent( name ); // name not relevant there
   
            // cannot use MouseTool .attach(), because it attaches a useless wheel listener,
            //  and ViewPlatformControlPanel will attach a better one to the parent component 
            canvas .addMouseListener( trackball );
            canvas .addMouseMotionListener( trackball );

            RenderingViewer viewer = rvFactory .createRenderingViewer( mControlBallScene, canvas );
            mViewPlatform .addViewer( new TrackballRenderingViewer( viewer ) );

            // mControlBallScene .reset();
        	for ( RenderedManifestation rm : mControlBallModel )
                mControlBallScene.manifestationAdded( rm );

            mViewPlatform .updateViewers();
            return canvas;
        }
        else
        {
            RenderingViewer.Factory rvFactory = mApp .getJ3dFactory();
            Component canvas = rvFactory .createJ3dComponent( name ); // name not relevant there

            drawOutlines = true;
            RenderingChanges scene = rvFactory.createRenderingChanges( sceneLighting, true, this );
            mRenderedModel.addListener( scene );
            RenderingViewer viewer = rvFactory.createRenderingViewer( mainScene, canvas );
            this.addPropertyListener( (PropertyChangeListener) viewer );
            return canvas;
        }
    }
    
    private SymmetryController getSymmetryController( String name )
    {
    	SymmetryController result = this .symmetries .get( name );
        if ( result == null ) {
        	result = new SymmetryController( this, this .documentModel .getSymmetrySystem( name ) );
        	this .symmetries .put( name, result );
        }
        return result;
    }

    private void setSymmetrySystem( SymmetrySystem symmetrySystem )
    {
    	String name =  symmetrySystem .getName();
        symmetryController = getSymmetryController( name );

        mControlBallModel = mApp.getSymmetryModel( symmetryController.getSymmetry() );
        if ( mControlBallScene != null ) {
            mControlBallScene.reset();
        	for ( RenderedManifestation rm : mControlBallModel )
                mControlBallScene.manifestationAdded( rm );
        }
        mViewPlatform .setSnapper( symmetryController.getSnapper() );
        properties().firePropertyChange( "symmetry", null, name ); // notify UI, so cardpanel can flip, or whatever
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
        if ( previewStrut != null )
            previewStrut .setSymmetryController( symmetryController );
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
            this .syncRendering();
            
            this .documentModel .addSelectionListener( new ManifestationChanges()
            {
    			@Override
    			public void manifestationRemoved( Manifestation man )
    			{
    				properties() .firePropertyChange( "tools.enabled", null, null );
    			}
    			
    			@Override
    			public void manifestationColored(Manifestation arg0, Color arg1) {}
    			
    			@Override
    			public void manifestationAdded( Manifestation man )
    			{
    				properties() .firePropertyChange( "tools.enabled", null, null );
    			}
    		});
            return;
        }
        
        mErrors .clearError();
        try {
            if ( action.equals( "undo" ) )
            	this .documentModel .undo( ! this .userHasEntitlement( "developer.extras" ) );
            else if ( action.equals( "redo" ) )
            	this .documentModel .redo( ! this .userHasEntitlement( "developer.extras" ) );
            else if ( action.equals( "undoToBreakpoint" ) ) {
            	this .documentModel .undoToBreakpoint();
            } else if ( action.equals( "redoToBreakpoint" ) ) {
            	this .documentModel .redoToBreakpoint();
            } 
            else if ( action.equals( "setBreakpoint" ) )
            	this .documentModel .setBreakpoint();
            else if ( action.equals( "undoAll" ) ) {
            	this .documentModel .undoAll();
            } else if ( action.equals( "redoAll" ) ) {
            	this .documentModel .redoAll( - 1 );
            } else if ( action.startsWith( "redoUntilEdit." ) ) {
                String editNum = action .substring( "redoUntilEdit.".length() );
                this .documentModel .redoAll( Integer.parseInt( editNum ) );
            }
            else if ( action .equals( "switchToArticle" ) )
            {
                currentView = mViewPlatform .getView();
                
                selectionClick .detach( modelCanvas );
                previewStrutStart .detach( modelCanvas );
                previewStrutRoll .detach( modelCanvas );
                previewStrutPlanarDrag .detach( modelCanvas );
                modelModeMainTrackball .detach( modelCanvas );
                
                lessonPageClick .attach( modelCanvas );
                articleModeMainTrackball .attach( modelCanvas );

                documentModel .addPropertyChangeListener( this .articleChanges );
                documentModel .removePropertyChangeListener( this .modelChanges );
                lessonController .doAction( "restoreSnapshot", e );

                this .editingModel = false;
                properties() .firePropertyChange( "editor.mode", "model", "article" );
            }
            else if ( action .equals( "switchToModel" ) )
            {
                documentModel .removePropertyChangeListener( this .articleChanges );
                documentModel .addPropertyChangeListener( this .modelChanges );
                mViewPlatform .restoreView( currentView );

                RenderedModel .renderChange( currentSnapshot, mRenderedModel, mainScene );
                currentSnapshot = mRenderedModel;

                lessonPageClick .detach( modelCanvas );
                articleModeMainTrackball .detach( modelCanvas );
                
                selectionClick .attach( modelCanvas );
                previewStrutStart .attach( modelCanvas );
                previewStrutRoll .attach( modelCanvas );
                previewStrutPlanarDrag .attach( modelCanvas );
                modelModeMainTrackball .attach( modelCanvas );

                this .editingModel = true;
                properties() .firePropertyChange( "editor.mode", "article", "model" );
            }
            else if ( action .equals( "takeSnapshot" ) )
            {
            	documentModel .addSnapshotPage( mViewPlatform .getView() );
            }

            else if ( "nextPage" .equals( action ) )
                lessonController .doAction( action, e );

//            else if ( action .equals( "test.pick.cube" ) ) // just a test of
//            // Bounds picking
//            {
//                Collection rms = imageCaptureViewer.pickCube();
//                for ( Iterator it = rms.iterator(); it.hasNext(); ) {
//                    RenderedManifestation rm = (RenderedManifestation) it.next();
//                    Manifestation targetManifestation = null;
//                    if ( rm != null && rm.isPickable() )
//                        targetManifestation = rm.getManifestation();
//                    else
//                        continue;
//                    document .selectManifestation( targetManifestation, true ); // NOT UNDOABLE!
//                }
//            }

            else if ( action.equals( "refresh.2d" ) )
            {
                Java2dExporter exporter = new Java2dExporter( mViewPlatform.getView(), this.mApp.getColors(), this.sceneLighting, this.currentSnapshot );
                this .mSnapshot .setExporter( exporter );
            }

            else if ( action.startsWith( "setStyle." ) )
                setRenderingStyle();

            else if ( action.equals( "toggleFrameLabels" ) )
            {
                showFrameLabels = ! showFrameLabels;
                properties() .firePropertyChange( "showFrameLabels", !showFrameLabels, showFrameLabels );
            }

            else if ( action.equals( "toggleOutlines" ) )
            {
                drawOutlines = ! drawOutlines;
                properties() .firePropertyChange( "drawOutlines", !drawOutlines, drawOutlines );
            }

            else if ( action.equals( "toggleWorkingPlane" ) )
            {
                useWorkingPlane = ! useWorkingPlane;
//                if ( useWorkingPlane )
//                    mainScene .enableWorkingPlane( workingPlaneOrbits );
//                else
//                    mainScene .disableWorkingPlane( workingPlaneOrbits );
            }

            else if ( action.equals( "toggleOrbitViews" ) ) {
                boolean old = useGraphicalViews;
                useGraphicalViews = ! old;
                properties().firePropertyChange( "useGraphicalViews", old, this.useGraphicalViews );
            }

            else if ( action.equals( "toggleStrutScales" ) ) {
                boolean old = showStrutScales;
                showStrutScales = ! old;
                properties().firePropertyChange( "showStrutScales", old, this.showStrutScales );
            }

            else if ( action.startsWith( "setSymmetry." ) ) {
                String system = action.substring( "setSymmetry.".length() );
                this .documentModel .setSymmetrySystem( system );
                setSymmetrySystem( this .documentModel .getSymmetrySystem() );
            }

            else if ( action.equals( "copyThisView" ) )
            {
                copyThisView();
            }
            else if ( action.equals( "useCopiedView" ) )
            {
                mViewPlatform .useCopiedView();
            }
            else if ( action.equals( "lookAtOrigin" ) )
                mViewPlatform.setLookAtPoint( new Point3d( 0, 0, 0 ) );
            
            else if ( action.equals( "lookAtSymmetryCenter" ) )
            {
            	RealVector loc = documentModel .getParamLocation( "ball" );
            	mViewPlatform .setLookAtPoint( new Point3d( loc.x, loc.y, loc.z ) );
            }

            else if ( action .equals( "usedOrbits" ) )
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
            else if ( action.startsWith( "newTool/" ) )
            {
                String name = action .substring( "newTool/" .length() );
                int nextDot = name .indexOf( "." );
                String group = name .substring( 0, nextDot );
                
                Symmetry symmetry = symmetryController.getSymmetry();
                if ( "icosahedral" .equals( group ) )
                	// no longer supporting no-axis octahedral when in icosahedral mode
                    symmetry = getSymmetryController( group ) .getSymmetry();
                
                documentModel .createTool( name, group, documentModel, symmetry );
            }
            
// This was an experiment, to see if the applyQuaternionSymmetry() approach was workable.
//  It seems it is too restrictive to insist upon all W=0 inputs.
//            else if ( action.equals( "h4symmetry" ) )
//            {
//                QuaternionicSymmetry qsymm = document .getField() .getQuaternionSymmetry( "H_4" ); 
//                document .applyQuaternionSymmetry( qsymm, qsymm );
//            }
//            
            else if ( action .equals( "delete" ) )
            {
                documentModel .doEdit( action );
            }
            else if ( action .equals( "cut" ) )
            {
                setProperty( "clipboard", documentModel .copySelectionVEF() );
                documentModel .doEdit( "delete" );
            }
            else if ( action .equals( "copy" ) )
                setProperty( "clipboard", documentModel .copySelectionVEF() );
            else if ( action.equals( "paste" ) )
            {
                String vefContent = getProperty( "clipboard" );
                documentModel .pasteVEF( vefContent );
            }
            else
            {
            	switch ( action ) {
                
                case "setSymmetryCenter":
                    this .documentModel .setParameter( null, "ball" );
        			break;

                case "setSymmetryAxis":
                    this .documentModel .setParameter( null, "strut" );
        			break;

				default:
	        		Symmetry symm = symmetryController .getSymmetry();
	        		String symmName = symm .getName();
	        		AlgebraicField field = symm .getField();
	            	if ( action.equals( "axialsymm" ) )
	            	{
	            		if ( field instanceof RootTwoField ) 
	            			action += "-roottwo"; 
	            		else if ( field instanceof RootThreeField ) 
	            			action += "-rootthree"; 
	            		else if ( field instanceof HeptagonField ) 
	            			action += "-heptagon"; 
	            		else if ( "octahedral" .equals( symmName ) ) 
	            			action += "-octa"; 
	            		else if ( "icosahedral" .equals( symmName ) ) 
	            			action += "-icosa"; 
	            	} else if ( action.equals( "tetrasymm" ) || action.equals( "octasymm" ) ) 
	            		if ( field instanceof RootTwoField ) 
	            			action += "-roottwo"; 
	            		else if ( field instanceof RootThreeField ) 
	            			action += "-rootthree"; 
	            		else if ( field instanceof HeptagonField ) 
	            			action += "-heptagon"; 
	            		else 
	            			action += "-golden"; 
	            		 	 
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
        mViewPlatform.copyView(mViewPlatform.getView());
    }

    public void syncRendering()
    {
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
    }

	@Override
    public void doScriptAction( String command, String script )
    {
        if ( command.equals( "runZomicScript" ) 
				|| command.equals( "runPythonScript" )
        		|| command.equals( "import.vef" ) 
				//|| command.equals( "import.zomod" ) 
				)
        	documentModel .doScriptAction( command, script );
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
                
                FileOutputStream out = new FileOutputStream( file );
                documentModel .serialize( out, this .properties );
                out.close();
                // just did a save, so lets record the document change count again,
                //  so isEdited() will return false until more changes occur.
                // IMPORTANT! TODO if we ever implement "save a copy", this code should NOT reset
                //   the count just because we're writing a copy.  The reset will have to move to the
                //   context of the save.
                this .changeCount  = this .documentModel .getChangeCount();
                return;
            }
            if ( "capture-animation" .equals( command ) )
            {
        		File dir = file .isDirectory()? file : file .getParentFile();
        		Dimension size = this .modelCanvas .getSize();        		
        		String html = readResource( "org/vorthmann/zome/app/animation.html" );
        		html = html .replaceFirst( "%%WIDTH%%", Integer .toString( size .width ) );
        		html = html .replaceFirst( "%%HEIGHT%%", Integer .toString( size .height ) );
        		writeFile( html, new File( dir, "index.html" ) );
        		String js = readResource( "org/vorthmann/zome/app/j360-loop.js" );
        		writeFile( js, new File( dir, "j360-loop.js" ) );

            	AnimationCaptureController animation = new AnimationCaptureController( this .mViewPlatform, dir );
            	captureImageFile( null, AnimationCaptureController.TYPE, animation );
                return;
            }
            if ( command.startsWith( "capture." ) )
            {
                final String extension = command .substring( "capture.".length() );
                captureImageFile( file, extension, null );
	            Platform .setFileType( file, extension );
	            Platform .openApplication( file );
                return;
            }
//            if ( command .equals( "export.zomespace" ) )
//            {
//                new ZomespaceExporter( file ) .exportArticle( document, colors, sceneLighting, getSaveXml(), getProperty( "edition" ), getProperty( "version" ) );
//            } else
            if ( command.startsWith( "export." ) )
            {
                Writer out = new FileWriter( file );
        		Dimension size = this .modelCanvas .getSize();        		
            	try {
                    String format = command .substring( "export." .length() ) .toLowerCase();
                    Exporter3d exporter = documentModel .getNaiveExporter( format, mViewPlatform .getView(), colors, sceneLighting, currentSnapshot );
                    if ( exporter != null ) {
                        exporter.doExport( file, file.getParentFile(), out, size.height, size.width );
                    }
                    else {
                        exporter = this .mApp .getExporter( format );
                        if ( exporter == null ) {
                        	// currently just "partgeom"
                        	exporter = documentModel .getStructuredExporter( format, mViewPlatform .getView(), colors, sceneLighting, mRenderedModel );
                        }
                    	if ( exporter != null )
                    		exporter .doExport( documentModel, file, file.getParentFile(), out, size.height, size.width );
                    }
                } finally {
                    out.close();
                }
                return;
            }
            if ( command.equals( "import.vef" ) 
					// || command.equals( "import.zomod" )
					) {
                String vefData = readFile( file );
                documentModel .doScriptAction( command, vefData );
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
			@Override
		    public void captureImage( final RenderedImage image )
		    {
		        String type = extension.toUpperCase();
		        if ( type.equals( "JPG" ) )
		            type = "JPEG";
		        try {
		        	File thisFile = ( animation != null )? animation .nextFile() : file;
		            ImageOutputStream  ios =  ImageIO.createImageOutputStream( thisFile );
		            Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName( type );
		            ImageWriter writer = iter .next();
		            writer .setOutput( ios );
		            ImageWriteParam iwParam = writer .getDefaultWriteParam();
		            if ( iwParam .canWriteCompressed() ) {
		            	String[] types = iwParam .getCompressionTypes();
		                iwParam .setCompressionMode( ImageWriteParam.MODE_EXPLICIT );
		                iwParam .setCompressionType( types[ types.length - 1 ] ); // this default is better for BMP, to avoid non-compression
		                iwParam .setCompressionQuality( .95f );
		            }
		            writer .write( null, new IIOImage( image, null, null), iwParam );
		            writer .dispose();
		            if ( animation != null && ! animation .finished() ) {
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
        InputStream bytes = new FileInputStream( file );
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int num;
        while ( ( num = bytes.read( buf, 0, 1024 ) ) > 0 )
            out.write( buf, 0, num );
        bytes .close();
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
    	FileWriter writer = null;
        try {
        	writer = new FileWriter( file );
        	writer .write( content );
        } catch (Exception ex) {
            throw ex;
        } finally {
            if(writer != null)
                writer .close();
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
            	result[ i ] = mViewPlatform .hasCopiedView();
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
        if ( mViewPlatform == null )
            return;
        mViewPlatform.setErrorChannel( errors );
        lessonController.setErrorChannel( errors );
        toolsController .setErrorChannel( errors );
    }

	@Override
    public String getProperty( String string )
    {
        if ( "useGraphicalViews".equals( string ) ) {
            return Boolean.toString( this.useGraphicalViews );
        }
        if ( "showStrutScales".equals( string ) ) {
            return Boolean.toString( this.showStrutScales );
        }
        if ( "startReader".equals( string ) )
            return Boolean.toString( startReader );

        if ( "migrated".equals( string ) )
            return Boolean.toString( this .documentModel .isMigrated() );

        if ( "edited".equals( string ) )
            return Boolean.toString( this .isEdited() );

        if ( "symmetry".equals( string ) )
            return symmetryController.getSymmetry().getName();

        if ( "field.name".equals( string ) )
            return this .documentModel .getField() .getName();
        
        if ( "field.label".equals( string ) ) {
        	
            String name = this .documentModel .getField() .getName();
            return super.getProperty( "field.label." + name ); // defer to app controller
            // TODO implement AlgebraicField.getLabel()
        }
        
        if ( string .startsWith( "supports.symmetry." ) )
        {
            String group = string .substring( "supports.symmetry." .length() );
            Symmetry symm = this .documentModel .getField() .getSymmetry( group );
            return Boolean .toString(symm != null);
        }
        
        if ( string .startsWith( "tool.enabled." ) )
        {
            String group = string .substring( "tool.enabled." .length() );
            
            Symmetry symmetry = symmetryController .getSymmetry();
            if ( "icosahedral" .equals( group ) )
                symmetry = getSymmetryController( group ) .getSymmetry();
            
            return Boolean .toString( this .documentModel .isToolEnabled( group, symmetry ) );
        }
        
        if ( "clipboard" .equals( string ) )
            return systemClipboard != null
                    ? systemClipboard.getClipboardContents()
                    : designClipboard;
        
        if ( "showFrameLabels" .equals( string ) )
            return Boolean .toString( showFrameLabels );
                
        if ( "drawOutlines" .equals( string ) )
            return Boolean .toString( drawOutlines );
                
        if ( "useWorkingPlane" .equals( string ) )
            return Boolean .toString( useWorkingPlane );
                
        if ( "workingPlaneDefined" .equals( string ) )
            return Boolean .toString( workingPlaneAxis != null );
                
        if ( string .startsWith( "tool.description." ) )
        {
            string = string .substring( "tool.description." .length() ); // strip "tool.description."
            if ( "bookmark" .equals( string ) )
                return "set the selection to be whatever it is now";
            else if ( "module" .equals( string ) )
                return "duplicate a module at every point";
            else if ( "point reflection" .equals( string ) )
                return "apply a central inversion through a point";
            else if ( "mirror" .equals( string ) )
                return "reflect objects in a mirror plane";
            else if ( "translation" .equals( string ) )
                return "translate objects by the given offset";
            else if ( "linear map" .equals( string ) )
                return "apply a linear transformation";
            else if ( "rotation" .equals( string ) )
                return "rotate objects around an axis, by a fixed angle";
            else if ( "scaling" .equals( string ) )
                return "scale objects linearly, relative to a fixed point";
            else if ( "plane" .equals( string ) )
                return "select objects based on incidence on a plane or half-space";
            else
                return "replicate objects to create " + string + " symmetry";
        }
        
        if ( string .startsWith( "file-dialog-title." ) ) {
        	switch ( string .substring( "file-dialog-title." .length() ) ) {
			case "capture-animation":
				return "Choose a target folder for animation frames";

			default:
				break; // fall through to properties or super
			}
        }

        String result = this .properties .getProperty( string );
        if ( result != null )
            return result;

        return super.getProperty( string );
    }
    
	@Override
    public Controller getSubController( String name )
    {
		switch ( name ) {

		case "monocularPicking":
			return monoController;

		case "leftEyePicking":
			return leftController;

		case "rightEyePicking":
			return rightController;

	    case "viewPlatform":
            return mViewPlatform;

	    case "symmetry":
            return symmetryController;

	    case "tools":
            return toolsController;
        
	    case "parts":
            return partsController; 
        
	    case "polytopes":
            return polytopesController; 
        
	    case "lesson":
            return lessonController;
        
	    case "snapshot.2d": {
            if ( mSnapshot == null ) {
                Java2dExporter exporter = new Java2dExporter( mViewPlatform.getView(), this.mApp.getColors(), this.sceneLighting, this.currentSnapshot );
                mSnapshot = new Java2dSnapshot( exporter );
                mSnapshot .setNextController( this );
            }
            return mSnapshot;
        }

		default:
	        if ( name.startsWith( "symmetry." ) )
	            return this.symmetries.get( name.substring( "symmetry.".length() ) );
	        else
	        	return null;
		}
    }

	@Override
    public void setProperty( String cmd, Object value )
    {
        if ( "useGraphicalViews".equals( cmd ) ) {
            this.useGraphicalViews = "true".equals( value );
            properties().firePropertyChange( cmd, false, this.useGraphicalViews );
            return;
        } else if ( "visible".equals( cmd ) ) {
        	// Window is listening, will bring itself to the front, or close itself
        	// App controller will set topDocument, or remove the document.
        	properties() .firePropertyChange( "visible", null, value );
        } else if ( "name".equals( cmd ) ) {
        	// App controller is listening, will change its map
            properties() .firePropertyChange( "name", null, (String) value );
        } else if ( "backgroundColor".equals( cmd ) ) {
            sceneLighting .setProperty( cmd, value );
        } else if ( "terminating".equals( cmd ) ) {
            properties().firePropertyChange( cmd, null, value );
        } else if ( "showStrutScales".equals( cmd ) ) {
            boolean old = showStrutScales;
            this.showStrutScales = "true" .equals( value );
            properties().firePropertyChange( "showStrutScales", old, this.showStrutScales );
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
            properties() .firePropertyChange( "showFrameLabels", old, showFrameLabels );
        }

        super.setProperty( cmd, value );
    }

	@Override
    public String[] getCommandList( String listName )
    {
        if ( "tool.templates" .equals( listName ) )
        {
            List<String> all = new ArrayList<>();
            List<String> genericTools = Arrays .asList( new String[]{ "translation", "scaling", "point reflection", "linear map", "module", "bookmark", "plane" } );
            all .addAll( genericTools );
            List<String> symmTools = Arrays .asList( symmetryController .getCommandList( listName ) );
            all .addAll( symmTools );
            return all .toArray( new String[all.size()] );
        }
        return super.getCommandList( listName );
    }
	
	void doManifestationAction( Manifestation pickedManifestation, String action )
	{
        Construction singleConstruction = null;
        if ( pickedManifestation != null )
            singleConstruction = pickedManifestation .getConstructions().next();

        try {
            switch ( action ) {

            case "undoToManifestation":
                this .documentModel .undoToManifestation( pickedManifestation );
    			break;

//            case "symmTool-icosahedral":
//                Symmetry symmetry = ((SymmetryController) getSymmetryController( "icosahedral" )) .getSymmetry();
//
//                this .document .createTool( "icosahedral.99/", "icosahedral", toolsController, symmetry );
//                this .document .createAndApplyTool( pickedManifestation, "icosahedral", toolsController, symmetry );
//    			break;

            case "setSymmetryCenter":
                this .documentModel .setParameter( singleConstruction, "ball" );
    			break;

            case "setSymmetryAxis":
                this .documentModel .setParameter( singleConstruction, "strut" );
    			break;

            case "setWorkingPlaneAxis":
            	this .workingPlaneAxis = (Segment) singleConstruction;
            	this .properties() .firePropertyChange( "workingPlaneDefined", false, true );
    			break;

            case "setWorkingPlane":
            	this .workingPlaneAxis = this .documentModel .getPlaneAxis( (Polygon) singleConstruction );
            	this .properties() .firePropertyChange( "workingPlaneDefined", false, true );
    			break;

            case "lookAtBall":
            	RealVector loc = documentModel .getLocation( singleConstruction );
            	mViewPlatform .setLookAtPoint( new Point3d( loc.x, loc.y, loc.z ) );
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
            	}
            	break;
                
            case "selectCollinear": 
                documentModel .selectCollinear( (Strut) pickedManifestation );
            	break;

            case "selectParallelStruts":
                documentModel.selectParallelStruts( (Strut) pickedManifestation );
            	break;
				
            case "selectSimilarSize": {
                Strut strut = (Strut) pickedManifestation;
                AlgebraicVector offset = strut .getOffset();
                Axis zone = symmetryController .getZone( offset );
                Direction orbit = zone .getOrbit();
                AlgebraicNumber length = zone .getLength( offset );
                documentModel .selectSimilarStruts( orbit, length ); // does performAndRecord
            	}
            	break;
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

	String getManifestationProperty( Manifestation pickedManifestation, String propName )
	{
		switch ( propName ) {

		case "objectProperties":
			if ( pickedManifestation != null ) {
				String objectProps = documentModel .getManifestationProperties( pickedManifestation, symmetryController .getOrbitSource() );
				if ( pickedManifestation instanceof Strut && this .userHasEntitlement( "developer.extras" ) ) {
		            AlgebraicVector offset = ( (Strut) pickedManifestation ).getOffset();
		            Axis zone = symmetryController .getOrbitSource() .getAxis( offset );
		            objectProps += "\n\nzone: " + zone .toString();
		            objectProps += "\n\nrotation: " + zone .getCorrectRotation();
		            objectProps += "\n\norientation: " + zone .getOrientation();
		            objectProps += "\n\nsense: " + zone .getSense();
				}
				pickedManifestation = null;
				return objectProps;
			}
			return null;

		case "objectColor":
			if ( pickedManifestation != null ) {
                RenderedManifestation rm = pickedManifestation .getRenderedObject();
                String colorStr = rm .getColor() .toString();
				pickedManifestation = null;
				return colorStr;
			}
			return null;

		default:
	 		return this .getProperty( propName );
		}
	}
}
