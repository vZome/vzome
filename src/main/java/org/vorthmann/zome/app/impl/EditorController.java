package org.vorthmann.zome.app.impl;

import java.awt.Component;
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
import java.util.TreeMap;

import javax.media.jai.JAI;
import javax.vecmath.Point3d;
import javax.vecmath.Quat4d;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
import com.vzome.core.editor.UndoableEdit;
import com.vzome.core.exporters.Exporter3d;
import com.vzome.core.math.DomUtils;
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
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.ThumbnailRenderer;
import com.vzome.core.viewing.ViewModel;
import com.vzome.desktop.controller.RenderingViewer;
import com.vzome.desktop.controller.ThumbnailRendererImpl;
import com.vzome.desktop.controller.TrackballViewPlatformModel;
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

    private ViewPlatformModel mViewPlatform, trackballVPM;
    
    private Lights sceneLighting;
    
    private RenderingViewer imageCaptureViewer;
    
    private ThumbnailRenderer thumbnails;

    private RenderedModel mControlBallModel;

    private RenderingChanges mainScene, mControlBallScene;

    private final DefaultApplication mApp;

    private Java2dSnapshot mSnapshot = null;

    private Map<String,SymmetryController> symmetries = new HashMap<>();

    private boolean mRequireShift = false, showFrameLabels = false, useWorkingPlane = false;

    private LessonController lessonController;

    private final boolean noRendering, startReader, noViewing, noJava3d;
    
    private final ManifestationChanges selectionRendering;
    
    private PropertyChangeListener articleChanges, modelChanges;

    private final Properties properties;
        
    private SymmetryController symmetryController;
    
    private Segment workingPlaneAxis = null;
    
    private final ToolsController toolsController;
    
    private PartsController partsController;

    private transient Manifestation mTargetManifestation;
    
    private TreeMap designs;
    
    private String designClipboard;

    private boolean editingModel;

    private ViewModel currentView;

    private MouseTool lessonPageClick, articleModeMainTrackball, modelModeMainTrackball;

    private Component modelCanvas;

    private MouseTool targetManifestationDrag, selectionClick, previewStrutStart, previewStrutRoll, previewStrutPlanarDrag;

    private final Controller polytopesController;

    private int changeCount = 0;
    
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

        noRendering = propertyIsTrue( "no.rendering" );  // we'll probably never do this any more, since we create a RenderedModel in the DocumentModel anyway
            // note that noRendering actually breaks things, since the automatic orbit behavior is only in the SymmetryController, not the SymmetrySystem.

        noViewing = noRendering || propertyIsTrue( "no.viewing" );

        noJava3d = noViewing || propertyIsTrue( "no.java3d" );

        final boolean asTemplate = propertyIsTrue( "as.template" );

        startReader = ! newDocument && ! asTemplate;
        
        editingModel = userHasEntitlement( "model.edit" ) && ! propertyIsTrue( "reader.preview" );
        
        toolsController = new ToolsController();
        toolsController .setNextController( this );
        
        polytopesController = new PolytopesController( document );
        polytopesController .setNextController( this );
        

        if ( noRendering ) {
            mRenderedModel = null;
            selectionRendering = null;
            
            // so that initial ball is realized
//            this .document .render( true, null );  // not necessary now with "finish.load" behavior?
            
        } else {
            mRenderedModel = new RenderedModel( this .document .getField(), true );
            currentSnapshot = mRenderedModel;
            if ( noViewing )
                selectionRendering = null;
            else
            {
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

                    public void manifestationColored( Manifestation m, String colorName )
                    {}
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


                sceneLighting = new Lights( app .getLights() );
                if ( ! newDocument )
                {
                	// TODO move sceneLighting into DocumentModel
                    NodeList nl = this .document .getLoadXml() .getElementsByTagName( "sceneModel" );
                    if ( nl .getLength() != 0 )
                        sceneLighting = new Lights( (Element) nl .item( 0 ) );
                }
                
                if ( ! noJava3d )
                {
                	RenderingViewer.Factory rvFactory = app .getJ3dFactory();
                	mainScene = rvFactory .createRenderingChanges( sceneLighting, true );

                	mControlBallScene = rvFactory .createRenderingChanges( sceneLighting, true );

                	thumbnails = new ThumbnailRendererImpl( rvFactory, sceneLighting );
                }
            }
        }

        mApp = app;

        mRequireShift = "true".equals( app.getProperty( "multiselect.with.shift" ) );
        useGraphicalViews = "true".equals( app.getProperty( "useGraphicalViews" ) );
        showStrutScales = "true" .equals( app.getProperty( "showStrutScales" ) );

        // RenderingViewer.Factory factory = app .getRenderingViewerFactory();

        // mRenderedModel .addListener( colors ); // TODO this is weird

        AlgebraicField field = this .document .getField();
        Symmetry symms[] = field .getSymmetries();
        Map<String,SymmetrySystem> systems = document .getSymmetrySystems();
        for ( int i = 0; i < symms.length; i++ ) {
        	SymmetrySystem system = systems .get( symms[i] .getName() );
            SymmetryController symmControl = new SymmetryController( this, system );
            this.symmetries.put( symms[i].getName(), symmControl );
            if ( i == 0 ) {
                symmetryController = symmControl;
                if ( ! noRendering )
                    setRenderingStyle();
                if ( ! noViewing )
                {
                    mControlBallModel = app .getSymmetryModel( symms[i] );
                    mControlBallModel .addListener( mControlBallScene );
                }
            }
        }

        // can't do this before the setRenderingStyle() call just above
        if ( mRenderedModel != null )
        {
        	this .document .setRenderedModel( mRenderedModel );
        	this .currentSnapshot = mRenderedModel;  // Not too sure if this is necessary
        }

        if ( noViewing )
        {
            previewStrut = null;
            return;
        }

        // this seems backwards, I know... the TrackballViewPlatformModel is the main
        // model, and only forwards two events to trackballVPM
        trackballVPM = new ViewPlatformModel();
        trackballVPM.setMagnification( 1f );

        mViewPlatform = new TrackballViewPlatformModel( trackballVPM );
        mViewPlatform.setNextController( this );
        mViewPlatform.setMagnification( 1f );

        trackballVPM.setSnapper( symmetryController.getSnapper() );
        mViewPlatform.setSnapper( symmetryController.getSnapper() );

        previewStrut = new PreviewStrut( field, mainScene, mViewPlatform );
        
        lessonController = new LessonController( document .getLesson(), mViewPlatform );
        lessonController .setNextController( this );

        Element xml = this .document .getLoadXml();
        if ( xml != null ) {
            NodeList nl = xml .getElementsByTagName( "Viewing" );
            if ( nl .getLength() != 0 )
                // this will now restore just the anonymous view
            	mViewPlatform .setXml( (Element) nl .item( 0 ) );
            
            nl = xml .getElementsByTagName( "SymmetrySystem" );
            if ( nl .getLength() != 0 ) {
            	Element symmXml = (Element) nl .item( 0 );
                String system = symmXml .getAttribute( "name" );
                SymmetryController symmControl = (SymmetryController) symmetries.get( system );
                symmControl.setXml( symmXml );
                setSymmetry( symmControl );
            }
        }
        
        partsController = new PartsController( symmetryController .getOrbitSource() );
        partsController .setNextController( this );
        mRenderedModel .addListener( partsController );

        if ( noJava3d )
        {
            // so that initial ball is realized
//            currentDesign .render( true, null );   // I think this is not necessary now
            // if we do this too early, the RenderedModel has no Shapes
        }

        // mControlBallModel .manifestationAdded( new Connector( document
        // .getField() .origin() ) );
    }

    public Component createJ3dComponent( String name )
    {
        RenderingViewer.Factory rvFactory = mApp .getJ3dFactory();
        Component canvas = rvFactory .createJ3dComponent( name ); // name not relevant there

        if ( name.startsWith( "mainViewer" ) )
        {
            final RenderingViewer viewer = rvFactory.createRenderingViewer( mainScene, canvas );
            if ( name.endsWith( "-leftEye" ) )
                viewer .setEye( RenderingViewer .LEFT_EYE );
            else if ( name.endsWith( "-rightEye" ) )
                viewer .setEye( RenderingViewer .RIGHT_EYE );
            else
            {
                imageCaptureViewer = viewer;
                modelCanvas = canvas;
            }
            
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
                    RenderedManifestation rm = viewer .pickManifestation( e );
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
            
            // all this does is set the target, for other commands or the popup menu
            mouseTool = new ManifestationPicker( viewer )
            {
                protected void dragFinished( Manifestation target, boolean b )
                {
                    mTargetManifestation = target;
                    // I am relying here on the order of attachment.  Since this listener is added
                    // to the canvas first, it should get called first, and so mTargetManifestation
                    // will be set before enableContextualCommands() is called.
                }

                protected void dragStarted( Manifestation target, boolean b )
                {
                    mTargetManifestation = target;
                    // I am relying here on the order of attachment.  Since this listener is added
                    // to the canvas first, it should get called first, and so mTargetManifestation
                    // will be set before enableContextualCommands() is called.
                }

                // apparently only one of these events will be a popup trigger... but it is platform specific?

                public void mousePressed( MouseEvent e )
                {
                    if ( e.isPopupTrigger() )
                        super.mousePressed( e );
                }

                public void mouseReleased( MouseEvent e )
                {
                    if ( e.isPopupTrigger() )
                        super.mouseReleased( e );
                }
            };
            if ( editingModel )
                mouseTool .attach( canvas );
            if ( canvas == modelCanvas )
                targetManifestationDrag = mouseTool;

            // clicks become select or deselect all
            mouseTool = new LeftMouseDragAdapter( new ManifestationPicker( viewer )
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
            mouseTool = new LeftMouseDragAdapter( new ManifestationPicker( viewer )
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
                    UndoableEdit buildStrut = previewStrut .finishPreview( document );
                    if ( buildStrut != null )
                        document .performAndRecord( buildStrut );
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
            
            mViewPlatform .addViewer( viewer );
            // mRenderedModel .setFactory( mViewer .getSceneGraphFactory() );
            // mRenderedModel .setTopGroup( mViewer .getSceneGraphRoot() );

            mViewPlatform .init();
//            currentDesign .render( true, null );   // I think this is not necessary now
        }
        else if ( name.equals( "controlViewer" ) )
        {
            MouseTool trackball = mViewPlatform .getTrackball();
            
            
            // cannot use MouseTool .attach(), because it attaches a useless wheel listener,
            //  and ViewPlatformControlPanel will attach a better one to the parent component 
            canvas .addMouseListener( trackball );
            canvas .addMouseMotionListener( trackball );

            RenderingViewer viewer = rvFactory.createRenderingViewer( mControlBallScene, canvas );
            trackballVPM .addViewer( viewer );

            // mControlBallScene .reset();
            for ( Iterator rms = mControlBallModel.getRenderedManifestations(); rms.hasNext(); )
                mControlBallScene.manifestationAdded( (RenderedManifestation) rms.next() );

            trackballVPM.init(); // TODO this is odd here
        }
        else
        {
            RenderingChanges scene = rvFactory.createRenderingChanges( sceneLighting, true );
            mRenderedModel.addListener( scene );
            RenderingViewer viewer = rvFactory.createRenderingViewer( mainScene, canvas );
            this.addPropertyListener( (PropertyChangeListener) viewer );
        }
        return canvas;
    }

    private void setSymmetry( SymmetryController system )
    {
        symmetryController = system;
        mControlBallModel = mApp.getSymmetryModel( symmetryController.getSymmetry() );
        if ( mControlBallScene != null ) {
            mControlBallScene.reset();
            for ( Iterator rms = mControlBallModel.getRenderedManifestations(); rms.hasNext(); )
                mControlBallScene.manifestationAdded( (RenderedManifestation) rms.next() );
        }
        trackballVPM.setSnapper( symmetryController.getSnapper() );
        mViewPlatform.setSnapper( symmetryController.getSnapper() );
        properties().firePropertyChange( "symmetry", null, system.getSymmetry().getName() ); // notify UI, so cardpanel can flip, or whatever
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
            this .document .loadXml( openUndone, asTemplate );
                        
            // mainScene is not listening to mRenderedModel yet, so batch the rendering changes to it
            this .syncRendering();
            return;
        }

        mErrors .clearError();
        try {
            Construction singleConstruction = null;
            // TODO support group/ungroup: mTargetManifestation may be many
            // Manifestations
            if ( mTargetManifestation != null )
                singleConstruction = (Construction) mTargetManifestation.getConstructions().next();

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
                
                targetManifestationDrag .detach( modelCanvas );
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
                
                targetManifestationDrag .attach( modelCanvas );
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

            else if ( action.equals( "setSymmetryCenter" ) )
            	document .setParameter( singleConstruction, "ball" );
            else if ( action.equals( "setSymmetryAxis" ) )
            	document .setParameter( singleConstruction, "strut" );

            else if ( action.equals( "setWorkingPlaneAxis" ) )
            {
            	this .workingPlaneAxis = (Segment) singleConstruction;
            	this .properties() .firePropertyChange( "workingPlaneDefined", false, true );
            }

            else if ( action.equals( "setWorkingPlane" ) )
            {
            	this .workingPlaneAxis = this .document .getPlaneAxis( (Polygon) singleConstruction );
            	this .properties() .firePropertyChange( "workingPlaneDefined", false, true );
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
                setSymmetry( (SymmetryController) symmetries .get( system ) );
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
            
            else if ( action.equals( "lookAtBall" ) )
            {
                RealVector loc = document .getLocation( singleConstruction );
                mViewPlatform .setLookAtPoint( new Point3d( loc.x, loc.y, loc.z ) );
            }
            else if ( action.equals( "lookAtSymmetryCenter" ) )
            {
            	RealVector loc = document .getParamLocation( "ball" );
            	mViewPlatform .setLookAtPoint( new Point3d( loc.x, loc.y, loc.z ) );
            }

            else if ( action.equals( "listVEFindices" ) )
                listVEFindices();
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
            else if ( action .equals( "setBuildOrbitAndLength" ) )
            {
                Strut strut = (Strut) mTargetManifestation;
                AlgebraicVector offset = strut .getOffset();
                Axis zone = symmetryController .getZone( offset );
                Direction orbit = zone .getOrbit();
                AlgebraicNumber length = zone .getLength( offset );
                symmetryController .availableController .doAction( "enableDirection." + orbit .getName(), null );
                symmetryController .buildController .doAction( "setSingleDirection." + orbit .getName(), null );
                LengthController lmodel = (LengthController) symmetryController .buildController .getSubController( "currentLength" );
                lmodel .setActualLength( length );
            }
            else if ( action .equals( "selectSimilarSize" ) )
            {
                Strut strut = (Strut) mTargetManifestation;
                AlgebraicVector offset = strut .getOffset();
                Axis zone = symmetryController .getZone( offset );
                Direction orbit = zone .getOrbit();
                AlgebraicNumber length = zone .getLength( offset );
                document .selectSimilarStruts( orbit, length ); // does performAndRecord
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
            		 	 
            	UndoableEdit edit = document .createEdit( action );
                if ( edit == null )
                    super .doAction( action, e );
                else
                    document .performAndRecord( edit );
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
        mTargetManifestation = null;
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
            	Document doc = getSaveXml();
            	
                File dir = file .getParentFile();
                if ( ! dir .exists() )
                    dir .mkdirs();
                
                FileOutputStream out = new FileOutputStream( file );
                DomUtils .serialize( doc, out );
                out.close();
                // just did a save, so lets record the document change count again,
                //  so isEdited() will return false until more changes occur.
                // IMPORTANT! TODO if we ever implement "save a copy", this code should NOT reset
                //   the count just because we're writing a copy.  The reset will have to move to the
                //   context of the save.
                this .changeCount  = this .document .getChangeCount();
                return;
            }
            if ( command.startsWith( "capture." ) )
            {
                try {
                    Class.forName( "javax.media.jai.JAI" );
                } catch ( Exception ex ) {
                    mErrors.reportError(
                            "You must install Java Advanced Imaging before running vZome, if you intend to capture images.",
                            new Object[0] );
                    return;
                }
                String maxSizeStr = getProperty( "max.image.size" );
                final int maxSize = ( maxSizeStr == null )? -1 : Integer .parseInt( maxSizeStr );
                final String extension = command .substring( "capture.".length() );
                imageCaptureViewer .captureImage( maxSize, new RenderingViewer.ImageCapture()
                {
					@Override
                    public void captureImage( RenderedImage image )
                    {                        
                        String type = extension.toUpperCase();
                        if ( type.equals( "JPG" ) )
                            type = "JPEG";
                        try {
                            FileOutputStream stream = new FileOutputStream( file );
                            JAI .create( "encode", image, stream, type, null );
                            stream.close();
                            Platform.setFileType( file, extension );
                            Platform.openApplication( file );
                        } catch ( Exception exc ) {
                            mErrors.reportError( UNKNOWN_ERROR_CODE, new Object[] { exc } );
                        }
                    }
                } );
                return;
            }
//            if ( command .equals( "export.zomespace" ) )
//            {
//                new ZomespaceExporter( file ) .exportArticle( document, colors, sceneLighting, getSaveXml(), getProperty( "edition" ), getProperty( "version" ) );
//            } else
            if ( command.startsWith( "export." ) )
            {
                Writer out = new FileWriter( file );
            	try {
                    String format = command .substring( "export." .length() );
                    Exporter3d exporter = document .getNaiveExporter( format, mViewPlatform .getView(), colors, sceneLighting, currentSnapshot );
                    if ( exporter != null ) {
                        exporter.doExport( file, file.getParentFile(), out, 600, 600 ); // TODO fix hardcoded size
                    }
                    else {
                        exporter = this .mApp .getExporter( format );
                        if ( exporter == null ) {
                        	// currently just "partgeom"
                        	exporter = document .getStructuredExporter( format, mViewPlatform .getView(), colors, sceneLighting, mRenderedModel );
                        }
                    	if ( exporter != null )
                    		exporter .doExport( document, file, file.getParentFile(), out, 600, 600 ); // TODO fix hardcoded size
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

    /**
     * This relies on mTargetManifestation being already set!
     */
	@Override
    public boolean[] enableContextualCommands( String[] menu, MouseEvent e )
    {
        boolean[] result = new boolean[menu.length];
        for ( int i = 0; i < menu.length; i++ ) {
            String menuItem = menu[i];
            // TODO restore command disable based on capabilities
//            if ( ! mApp.getCommands().containsKey( menuItem ) ) {
//                result[i] = false;
//                continue;
//            }
            if ( menuItem.equals( "lookAtSymmetryCenter" )
            	    || menuItem .equals( "setBackgroundColor" )
                    // || menuItem .equals( "setZoneColor" )
                    || menuItem.equals( "rZomeOrbits" ) || menuItem.equals( "predefinedOrbits" )
                    || menuItem.equals( "setAllDirections" )
                    || menuItem .equals( "usedOrbits" ) || menuItem .equals( "copyThisView" )
                    || menuItem.equals( "configureDirections" ) || menuItem.equals( "lookAtOrigin" ) )
                result[i] = true;
            
            else if ( menuItem .equals( "useCopiedView" ) )
            	result[ i ] = mViewPlatform .hasCopiedView();

            else if ( mTargetManifestation instanceof Connector )
                result[i] = menuItem.equals( "lookAtBall" ) || menuItem.equals( "setSymmetryCenter" )
                        || menuItem.equals( "showProperties" );
            else if ( mTargetManifestation instanceof Strut )
                result[i] = menuItem.equals( "setSymmetryAxis" ) 
                			|| menuItem.equals( "setWorkingPlaneAxis" ) || menuItem.equals( "showProperties" ) || menuItem.equals( "extractPartModel" )
                            || menuItem.equals( "selectSimilarSize" ) || menuItem.equals( "setBuildOrbitAndLength" );
            else if ( mTargetManifestation instanceof Panel )
                result[i] = menuItem.equals( "reversePanel" ) || menuItem.equals( "setWorkingPlane" ) || menuItem.equals( "showPanelVertices" );
            else
                result[i] = false;
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

        if ( "objectProperties".equals( string ) ) {
            // TODO support group/ungroup: mTargetManifestation may be many
            // Manifestations
            if ( mTargetManifestation != null ) {
            	return this .document .getManifestationProperties( mTargetManifestation, symmetryController .getOrbitSource() );
            }
            mTargetManifestation = null;
            return "no object";
        }

        if ( "objectColor".equals( string ) ) {
            // TODO support group/ungroup: mTargetManifestation may be many
            // Manifestations
            if ( mTargetManifestation != null ) {
                RenderedManifestation rm = (RenderedManifestation) mTargetManifestation.getRenderedObject();
                rm.getColorName();
            }
            mTargetManifestation = null;
            return "no object";
        }
        
        String result = this .properties .getProperty( string );
        if ( result != null )
            return result;

        return super.getProperty( string );
    }

    private void listVEFindices()
    {
        for ( Iterator cs = mTargetManifestation.getConstructions(); cs.hasNext(); ) {
            Construction c = (Construction) cs.next();
            int index = c.getIndex();
            if ( index >= 0 )
                System.out.print( " " + index );
        }
        System.out.println();
        System.out.flush();
    }
    
    // private void setZoneColor()
    // {
    // String colorName = "background";
    // if ( mTargetManifestation instanceof Connector ) {
    // colorName = Colors.CONNECTOR;
    // }
    // else if ( mTargetManifestation instanceof Strut ) {
    // Strut strut = (Strut) mTargetManifestation;
    // int[] /*AlgebraicVector*/ offset = strut.getOffset();
    // Axis axis = mStyleSymmetry .getAxis( offset );
    // Direction dir = axis.getDirection();
    // colorName = Colors.DIRECTION + dir.getName();
    // }
    // else if ( mTargetManifestation instanceof Panel ) {
    // Panel panel = (Panel) mTargetManifestation;
    // int[] /*AlgebraicVector*/ normal = panel .getNormal( mField );
    // Axis axis = mStyleSymmetry .getAxis( normal );
    // Direction dir = axis.getDirection();
    // colorName = Colors.PLANE + dir.getName();
    // }
    // if ( colorName != null )
    // mAppUI .showColorDialog( colorName, null );
    // }

	@Override
    public Controller getSubController( String name )
    {
        if ( name.equals( "viewPlatform" ) )
            return mViewPlatform;
        if ( name.startsWith( "symmetry." ) )
            return (SymmetryController) this.symmetries.get( name.substring( "symmetry.".length() ) );
        if ( name.equals( "symmetry" ) )
            return symmetryController;
        if ( name.equals( "tools" ) )
            return toolsController;
        if ( name.equals( "parts" ) )
            return partsController; 
        if ( name.equals( "polytopes" ) )
            return polytopesController; 
        if ( name.equals( "lesson" ) )
            return lessonController;
        if ( name.equals( "snapshot.2d" ) ) {
            if ( mSnapshot == null ) {
                Java2dExporter exporter = new Java2dExporter( mViewPlatform.getView(), this.mApp.getColors(), this.sceneLighting, this.currentSnapshot );
                mSnapshot = new Java2dSnapshot( exporter );
                mSnapshot.setNextController( this );
            }
            return mSnapshot;
        }
        return null;
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
        
        if ( "designs" .equals( listName ) ) {
            Set<?> dkSet = designs.keySet();
            return (String[]) dkSet.toArray( );
		}
        return super.getCommandList( listName );
    }

	public Document getSaveXml() throws ParserConfigurationException
	{
    	DocumentBuilderFactory factory = DocumentBuilderFactory .newInstance();
    	factory .setNamespaceAware( true );
    	DocumentBuilder builder = factory .newDocumentBuilder();
        Document doc = builder .newDocument();

        Element modelXml = document .getSaveXml( doc );
        doc .appendChild( modelXml );

        Element result = sceneLighting .getXml( doc );
        modelXml .appendChild( result );
        result = mViewPlatform .getXml( doc );
        modelXml .appendChild( result );
        result = symmetryController .getXml( doc );  // TODO load this with the document
        modelXml .appendChild( result );
                
        return doc;
	}
}
