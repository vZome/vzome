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
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.vorthmann.zome.render.java3d.Java3dSceneGraph;

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
import com.vzome.core.editor.Tool;
import com.vzome.core.exporters.Exporter3d;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
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
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.ThumbnailRenderer;
import com.vzome.core.viewing.ViewModel;
import com.vzome.desktop.controller.RenderingViewer;
import com.vzome.desktop.controller.ThumbnailRendererImpl;
import com.vzome.desktop.controller.ViewPlatformModel;

/**
 * Description here.
 * 
 * @author Scott Vorthmann 2003
 */
public class EditorController extends DefaultController implements J3dComponentFactory
{
    private DocumentModel document;
    
    public boolean useGraphicalViews = false, showStrutScales = false;

    private final PreviewStrut previewStrut;

    private final RenderedModel mRenderedModel;

    private RenderedModel currentSnapshot;

    private ViewPlatformModel mViewPlatform;
    
    private Lights sceneLighting;
    
    private RenderingViewer imageCaptureViewer;
    
    private ThumbnailRenderer thumbnails;

    private RenderedModel mControlBallModel;

    private RenderingChanges mainScene, mControlBallScene;

    private final DefaultApplication mApp;

    private Java2dSnapshot mSnapshot = null;

    private boolean mRequireShift = false, showFrameLabels = false, useWorkingPlane = false;

    private LessonController lessonController;

    private final boolean startReader;
    
    private final ManifestationChanges selectionRendering;
    
    private PropertyChangeListener articleChanges, modelChanges;

    private final Properties properties;
        
    private SymmetryController symmetryController;
    
    private Segment workingPlaneAxis = null;
    
    private final ToolsController toolsController;
    
    private PartsController partsController;

    private Map<String,SymmetryController> symmetries = new HashMap<>();

    private String designClipboard;

    private boolean editingModel;

    private ViewModel currentView;

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

    public EditorController( DocumentModel document, DefaultApplication app, Properties props, boolean newDocument )
    {
        setNextController( app );

        this .properties = props;
        this .document = document;
        
        if ( document .isMigrated() )
            this .changeCount = -1; // this will force isEdited() to return true

        final boolean asTemplate = propertyIsTrue( "as.template" );

        startReader = ! newDocument && ! asTemplate;
        
        editingModel = userHasEntitlement( "model.edit" ) && ! propertyIsTrue( "reader.preview" );
        
        toolsController = new ToolsController();
        toolsController .setNextController( this );
        
        polytopesController = new PolytopesController( document );
        polytopesController .setNextController( this );
        
        mRenderedModel = new RenderedModel( this .document .getField(), true );
        currentSnapshot = mRenderedModel;

        selectionRendering = new ManifestationChanges()
        {
        	public void manifestationAdded( Manifestation m )
        	{
        		mRenderedModel .setManifestationGlow( m, true );
        	}

        	public void manifestationRemoved( Manifestation m )
        	{
        		mRenderedModel .setManifestationGlow( m, false );
        	}

        	@Override
        	public void manifestationColored( Manifestation m, Color c ) {}
        };
        this .document .addSelectionListener( selectionRendering );

        this .articleChanges = new PropertyChangeListener()
        {   
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
        			ViewModel newView = (ViewModel) change .getNewValue();
        			if ( ! newView .equals( mViewPlatform .getView() ) )
        				mViewPlatform .restoreView( newView );
        		}
        		else if ( "thumbnailChanged" .equals( change .getPropertyName() ) )
        		{
        			int pageNum = ((Integer) change .getNewValue()) .intValue();
        			EditorController .this .document .getLesson() .updateThumbnail( pageNum, EditorController .this .document, thumbnails );
        		}
        	}
        };
        this .modelChanges = new PropertyChangeListener()
        {   
        	public void propertyChange( PropertyChangeEvent change )
        	{
        		if ( "current.edit.xml" .equals( change .getPropertyName() ) )
        		{
        			properties() .firePropertyChange( change ); // forward to the UI for display in the statusText
        		}
        	}
        };
        if ( editingModel )
        	document .addPropertyChangeListener( this .modelChanges );
        else
        	document .addPropertyChangeListener( this .articleChanges );

        sceneLighting = new Lights( app .getLights() );  // TODO: restore the ability for the document to override

        // this seems backwards, I know... the TrackballViewPlatformModel is the main
        // model, and only forwards two events to trackballVPM
        mViewPlatform = new ViewPlatformModel( document .getViewModel() );
        mViewPlatform .setNextController( this );

        RenderingViewer.Factory rvFactory = app .getJ3dFactory();
        mainScene = rvFactory .createRenderingChanges( sceneLighting, true, true );

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
        
        mControlBallScene = rvFactory .createRenderingChanges( sceneLighting, true, false );

        thumbnails = new ThumbnailRendererImpl( rvFactory, sceneLighting );

        mApp = app;

        mRequireShift = "true".equals( app.getProperty( "multiselect.with.shift" ) );
        useGraphicalViews = "true".equals( app.getProperty( "useGraphicalViews" ) );
        showStrutScales = "true" .equals( app.getProperty( "showStrutScales" ) );

        AlgebraicField field = this .document .getField();
        previewStrut = new PreviewStrut( field, mainScene, mViewPlatform );
        
        lessonController = new LessonController( document .getLesson(), mViewPlatform );
        lessonController .setNextController( this );

        setSymmetrySystem( this .document .getSymmetrySystem() );

        // can't do this before the setSymmetrySystem() call just above
        if ( mRenderedModel != null )
        {
        	this .document .setRenderedModel( mRenderedModel );
        	this .currentSnapshot = mRenderedModel;  // Not too sure if this is necessary
        }

        partsController = new PartsController( symmetryController .getOrbitSource() );
        partsController .setNextController( this );
        mRenderedModel .addListener( partsController );
    }

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

                public void mousePressed( MouseEvent e )
                {
                    RenderedManifestation rm = imageCaptureViewer .pickManifestation( e );
                    if ( rm == null || !( rm .getManifestation() instanceof Connector ) )
                    {
                        this .live = true;
                        super .mousePressed( e );
                    }
                }

                public void mouseDragged( MouseEvent e )
                {
                    if ( live )
                        super .mouseDragged( e );
                }

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
                protected void manifestationPicked( Manifestation target, boolean shiftKey )
                {
                    mErrors .clearError();
                    boolean shift = true;
                    if ( mRequireShift )
                        shift = shiftKey;
                    if ( target == null )
                        try {
                            document .performAndRecord( document .deselectAll() );
                        } catch ( Exception e ) {
                            mErrors .reportError( UNKNOWN_ERROR_CODE, new Object[] { e } );
                        }
                    else
                        document .performAndRecord( document .selectManifestation( target, ! shift ) );
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
                protected void dragStarted( Manifestation target, boolean b )
                {
                    if ( target instanceof Connector )
                    {
                        mErrors .clearError();
                        Point point = (Point) ((Connector) target) .getConstructions() .next();
                        AlgebraicVector workingPlaneNormal = null;
                        if ( useWorkingPlane && (workingPlaneAxis != null ) )
                        	workingPlaneNormal = workingPlaneAxis .getOffset();
                        previewStrut .startRendering( symmetryController, point, workingPlaneNormal );
                    }
                }

				protected void dragFinished( Manifestation target, boolean b )
                {
                    previewStrut .finishPreview( document );
                }
            } );
            if ( editingModel )
                mouseTool .attach( canvas );
            if ( canvas == modelCanvas )
                previewStrutStart = mouseTool;

            // trackball to adjust the preview strut (when it is rendered)
            mouseTool = new LeftMouseDragAdapter( new Trackball()
            {
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
            for ( Iterator rms = mControlBallModel.getRenderedManifestations(); rms.hasNext(); )
                mControlBallScene.manifestationAdded( (RenderedManifestation) rms.next() );

            mViewPlatform .updateViewers();
            return canvas;
        }
        else
        {
            RenderingViewer.Factory rvFactory = mApp .getJ3dFactory();
            Component canvas = rvFactory .createJ3dComponent( name ); // name not relevant there

            RenderingChanges scene = rvFactory.createRenderingChanges( sceneLighting, true, true );
            mRenderedModel.addListener( scene );
            RenderingViewer viewer = rvFactory.createRenderingViewer( mainScene, canvas );
            this.addPropertyListener( (PropertyChangeListener) viewer );
            return canvas;
        }
    }

    private void setSymmetrySystem( SymmetrySystem symmetrySystem )
    {
    	String name =  symmetrySystem .getName();
        symmetryController = this .symmetries .get( name );
        if ( symmetryController == null ) {
        	symmetryController = new SymmetryController( this, symmetrySystem );
        	this .symmetries .put( name, symmetryController );
        }

        mControlBallModel = mApp.getSymmetryModel( symmetryController.getSymmetry() );
        if ( mControlBallScene != null ) {
            mControlBallScene.reset();
            for ( Iterator rms = mControlBallModel.getRenderedManifestations(); rms.hasNext(); )
                mControlBallScene.manifestationAdded( (RenderedManifestation) rms.next() );
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

    public void doAction( String action, ActionEvent e ) throws Failure
    {
        if ( "finish.load".equals( action ) ) {

            boolean openUndone = propertyIsTrue( "open.undone" );
            boolean asTemplate = propertyIsTrue( "as.template" );

            // used to finish loading a model history on a non-UI thread
            this .document .finishLoading( openUndone, asTemplate );
                        
            // mainScene is not listening to mRenderedModel yet, so batch the rendering changes to it
            this .syncRendering();
            return;
        }

        mErrors .clearError();
        try {
            if ( action.equals( "undo" ) )
                this .document .undo();
            else if ( action.equals( "redo" ) )
            	this .document .redo();
            else if ( action.equals( "undoToBreakpoint" ) ) {
            	this .document .undoToBreakpoint();
            } else if ( action.equals( "redoToBreakpoint" ) ) {
            	this .document .redoToBreakpoint();
            } 
            else if ( action.equals( "setBreakpoint" ) )
            	this .document .setBreakpoint();
            else if ( action.equals( "undoAll" ) ) {
            	this .document .undoAll();
            } else if ( action.equals( "redoAll" ) ) {
            	this .document .redoAll( - 1 );
            } else if ( action.startsWith( "redoUntilEdit." ) ) {
                String editNum = action .substring( "redoUntilEdit.".length() );
                this .document .redoAll( Integer.parseInt( editNum ) );
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

                document .addPropertyChangeListener( this .articleChanges );
                document .removePropertyChangeListener( this .modelChanges );
                lessonController .doAction( "restoreSnapshot", e );

                this .editingModel = false;
                properties() .firePropertyChange( "editor.mode", "model", "article" );
            }
            else if ( action .equals( "switchToModel" ) )
            {
                document .removePropertyChangeListener( this .articleChanges );
                document .addPropertyChangeListener( this .modelChanges );
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
            	document .addSnapshotPage( mViewPlatform .getView() );
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
                if ( showFrameLabels )
                    mainScene .enableFrameLabels();
                else
                    mainScene .disableFrameLabels();
            }

            else if ( action.equals( "toggleOutlines" ) )
            {
                ((Java3dSceneGraph) mainScene ) .togglePolygonOutlinesMode();
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
                this .document .setSymmetrySystem( system );
                setSymmetrySystem( this .document .getSymmetrySystem() );
            }

            else if ( action.equals( "copyThisView" ) )
            {
                mViewPlatform .copyView( mViewPlatform .getView() );
            }
            else if ( action.equals( "useCopiedView" ) )
            {
                mViewPlatform .useCopiedView();
            }
            else if ( action.equals( "lookAtOrigin" ) )
                mViewPlatform.setLookAtPoint( new Point3d( 0, 0, 0 ) );
            
            else if ( action.equals( "lookAtSymmetryCenter" ) )
            {
            	RealVector loc = document .getParamLocation( "ball" );
            	mViewPlatform .setLookAtPoint( new Point3d( loc.x, loc.y, loc.z ) );
            }

            else if ( action .equals( "usedOrbits" ) )
            {
            	Set<Direction> usedOrbits = new HashSet<>();
            	for ( Iterator iterator = mRenderedModel .getRenderedManifestations(); iterator.hasNext(); )
            	{
            		RenderedManifestation rm = (RenderedManifestation) iterator.next();
            		Polyhedron shape = rm .getShape();
            		Direction orbit = shape .getOrbit();
            		if ( orbit != null )
            			usedOrbits .add( orbit );
            	}
            	symmetryController .availableController .doAction( "setNoDirections", null );
            	for ( Iterator iterator = usedOrbits.iterator(); iterator.hasNext(); ) {
            		Direction orbit = (Direction) iterator.next();
            		symmetryController .availableController .doAction( "enableDirection." + orbit .getName(), null );
            	}
            }
            else if ( action.startsWith( "newTool/" ) )
            {
                String name = action .substring( "newTool/" .length() );
                int nextDot = name .indexOf( "." );
                String group = name .substring( 0, nextDot );
                
                Symmetry symmetry = symmetryController.getSymmetry();
                if ( "icosahedral" .equals( group ) || "octahedral" .equals( group ) )
                    symmetry = ((SymmetryController) symmetries .get( group )) .getSymmetry();
                
                document .createTool( name, group, toolsController, symmetry );
            }
            else if ( action.equals( "applyTool" ) )
            {
    	        ToolEvent event = (ToolEvent) e;
    	        document .applyTool( event .getTool(), toolsController, event .getModes() );
            }
            
// This was an experiment, to see if the applyQuaternionSymmetry() approach was workable.
//  It seems it is too restrictive to insist upon all W=0 inputs.
//            else if ( action.equals( "h4symmetry" ) )
//            {
//                QuaternionicSymmetry qsymm = document .getField() .getQuaternionSymmetry( "H_4" ); 
//                document .applyQuaternionSymmetry( qsymm, qsymm );
//            }
//            
            else if ( action .equals( "copy" ) )
                setProperty( "clipboard", document .copySelectionVEF() );
            else if ( action.equals( "paste" ) )
            {
                String vefContent = getProperty( "clipboard" );
                document .pasteVEF( vefContent );
            }
            else
            {
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
            		 	 
            	boolean handled = document .doEdit( action );
                if ( ! handled )
                    super .doAction( action, e );
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
                    lessonController .renderThumbnails( document, thumbnails );
                if ( this .toolsController != null )
                    for ( Tool tool : this .document .getTools() )
                        this .toolsController .addTool( tool );
            }
            else
                try {
                    currentSnapshot = new RenderedModel( null, null ); // force render of first snapshot, see "renderSnapshot." below
                    lessonController .doAction( "restoreSnapshot", new ActionEvent( this, 0, "restoreSnapshot" ) );
                    // order these to avoid issues with the thumbnails (unexplained)
                    lessonController .renderThumbnails( document, thumbnails );
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
        	document .doScriptAction( command, script );
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
                document .serialize( out );
                out.close();
                // just did a save, so lets record the document change count again,
                //  so isEdited() will return false until more changes occur.
                // IMPORTANT! TODO if we ever implement "save a copy", this code should NOT reset
                //   the count just because we're writing a copy.  The reset will have to move to the
                //   context of the save.
                this .changeCount  = this .document .getChangeCount();
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
                    String format = command .substring( "export." .length() );
                    Exporter3d exporter = document .getNaiveExporter( format, mViewPlatform .getView(), colors, sceneLighting, currentSnapshot );
                    if ( exporter != null ) {
                        exporter.doExport( file, file.getParentFile(), out, size.height, size.width );
                    }
                    else {
                        exporter = this .mApp .getExporter( format );
                        if ( exporter == null ) {
                        	// currently just "partgeom"
                        	exporter = document .getStructuredExporter( format, mViewPlatform .getView(), colors, sceneLighting, mRenderedModel );
                        }
                    	if ( exporter != null )
                    		exporter .doExport( document, file, file.getParentFile(), out, size.height, size.width );
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
                document .doScriptAction( command, vefData );
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
        	stream = EditorController.class .getClassLoader() .getResourceAsStream( resourcePath );
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
        int currentChangeCount = this .document .getChangeCount();
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
            return Boolean.toString( this .document .isMigrated() );

        if ( "edited".equals( string ) )
            return Boolean.toString( this .isEdited() );

        if ( "symmetry".equals( string ) )
            return symmetryController.getSymmetry().getName();

        if ( "field.name".equals( string ) )
            return this .document .getField() .getName();
        
        if ( "clipboard" .equals( string ) )
            return designClipboard;
                
        if ( "showFrameLabels" .equals( string ) )
            return Boolean .toString( showFrameLabels );
                
        if ( "drawOutlines" .equals( string ) )
            return Boolean .toString( ((Java3dSceneGraph) mainScene) .getPolygonOutlinesMode() );
                
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
	            return (SymmetryController) this.symmetries.get( name.substring( "symmetry.".length() ) );
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
        } else if ( "backgroundColor".equals( cmd ) ) {
            sceneLighting .setProperty( cmd, value );
        } else if ( "terminating".equals( cmd ) ) {
            properties().firePropertyChange( cmd, null, value );
        } else if ( "showStrutScales".equals( cmd ) ) {
            boolean old = showStrutScales;
            this.showStrutScales = "true" .equals( value );
            properties().firePropertyChange( "showStrutScales", old, this.showStrutScales );
        }
        else if ( "clipboard" .equals( cmd ) )
            this .designClipboard = (String) value;
        
        else if ( "showFrameLabels" .equals( cmd ) )
        {
            showFrameLabels = "true" .equals( value );
            if ( showFrameLabels )
                mainScene .enableFrameLabels();
            else
                mainScene .disableFrameLabels();
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
            return (String[]) all .toArray( new String[0] );
        }
        return super.getCommandList( listName );
    }
	
	void doManifestationAction( Manifestation pickedManifestation, String action )
	{
        Construction singleConstruction = null;
        if ( pickedManifestation != null )
            singleConstruction = (Construction) pickedManifestation .getConstructions().next();

        try {
            switch ( action ) {

            case "undoToManifestation":
                this .document .undoToManifestation( pickedManifestation );
    			break;

//            case "symmTool-icosahedral":
//                Symmetry symmetry = ((SymmetryController) symmetries .get( "icosahedral" )) .getSymmetry();
//
//                this .document .createTool( "icosahedral.99/", "icosahedral", toolsController, symmetry );
//                this .document .createAndApplyTool( pickedManifestation, "icosahedral", toolsController, symmetry );
//    			break;

            case "setSymmetryCenter":
                this .document .setParameter( singleConstruction, "ball" );
    			break;

            case "setSymmetryAxis":
                this .document .setParameter( singleConstruction, "strut" );
    			break;

            case "setWorkingPlaneAxis":
            	this .workingPlaneAxis = (Segment) singleConstruction;
            	this .properties() .firePropertyChange( "workingPlaneDefined", false, true );
    			break;

            case "setWorkingPlane":
            	this .workingPlaneAxis = this .document .getPlaneAxis( (Polygon) singleConstruction );
            	this .properties() .firePropertyChange( "workingPlaneDefined", false, true );
    			break;

            case "lookAtBall":
            	RealVector loc = document .getLocation( singleConstruction );
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
                
            case "selectSimilarSize": {
                Strut strut = (Strut) pickedManifestation;
                AlgebraicVector offset = strut .getOffset();
                Axis zone = symmetryController .getZone( offset );
                Direction orbit = zone .getOrbit();
                AlgebraicNumber length = zone .getLength( offset );
                document .selectSimilarStruts( orbit, length ); // does performAndRecord
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
				String objectProps = document .getManifestationProperties( pickedManifestation, symmetryController .getOrbitSource() );
				pickedManifestation = null;
				return objectProps;
			}
			return null;

		case "objectColor":
			if ( pickedManifestation != null ) {
                RenderedManifestation rm = (RenderedManifestation) pickedManifestation .getRenderedObject();
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
