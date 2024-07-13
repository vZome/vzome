package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.FileDialog;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;

import org.vorthmann.j3d.J3dComponentFactory;
import org.vorthmann.j3d.Platform;
import org.vorthmann.ui.ExclusiveAction;

import com.vzome.core.render.Scene;
import com.vzome.desktop.api.Controller;
import com.vzome.desktop.awt.GraphicsController;
import com.vzome.desktop.awt.RenderingViewer;
import com.vzome.desktop.controller.DefaultController;

public class DocumentFrame extends JFrame implements PropertyChangeListener, ControlActions
{
    private static final long serialVersionUID = 1L;

    protected final GraphicsController mController;
    
    protected final Controller toolsController;

    private final ModelPanel modelPanel;
            
    private final JTabbedPane tabbedPane = new JTabbedPane();
    
    private final ExclusiveAction.Excluder mExcluder = new ExclusiveAction.Excluder( this );

    private boolean isEditor, fullPower, readerPreview, canSave;

    private CardLayout modelArticleCardLayout;

    private static final Logger logger = Logger.getLogger( "org.vorthmann.zome.ui" );

    private LessonPanel lessonPanel;
    
    private JFrame zomicFrame, zomodFrame; //, pythonFrame;

    private JButton snapshotButton;

    private JRadioButton scenesButton, designButton;

    private JPanel viewControl, modelArticleEditPanel;

    private JLabel statusText;

    private GraphicsController lessonController, cameraController;
    
    private JDialog polytopesDialog, importScaleDialog;
    
    private ShareDialog shareDialog;

	private final boolean developerExtras;
	
	private final ActionListener localActions;
	    
    private final FileDialog fileDialog = new FileDialog( this );
    
	private File mFile = null;
	
	private final Controller.ErrorChannel errors;

    private Snapshot2dFrame snapshot2dFrame;

	private ControllerFileAction saveAsAction;

	private PropertyChangeListener appUI;
    
    public ExclusiveAction.Excluder getExcluder()
    {
        return mExcluder;
    }
    
    private static final JColorChooser colorChooser = new JColorChooser();
    
    void setAppUI( PropertyChangeListener appUI )
    {
    	this .appUI = appUI;
    }
        
    public DocumentFrame( final GraphicsController controller, final J3dComponentFactory factory3d )
    {
        mController = controller;
        mController .addPropertyListener( this );
        toolsController = mController .getSubController( "tools" );
        
        // Keep the tool tip showing
        ToolTipManager.sharedInstance().setDismissDelay( 20000 );

        String path = mController .getProperty( "window.file" );
        if ( path != null )
            this .mFile = new File( path ); // this enables "save" in localActions

        // TODO: compute these booleans once here, and don't recompute in DocumentMenuBar

        this.readerPreview = mController .propertyIsTrue( "reader.preview" );

        this.isEditor = mController .userHasEntitlement( "model.edit" ) && ! readerPreview;

        this.canSave = mController .userHasEntitlement( "save.files" );

        this.fullPower = isEditor ;//&& controller .userHasEntitlement( "all.tools" );
        
        this.developerExtras = fullPower && mController .userHasEntitlement( "developer.extras" );
        
        errors = new Controller.ErrorChannel()
        {
			@Override
            public void reportError( String errorCode, Object[] arguments )
            {
                if ( Controller.USER_ERROR_CODE.equals( errorCode ) ) {
                    errorCode = ( (Exception) arguments[0] ).getMessage();
                    // don't want a stack trace for a user error
                    logger.log( Level.WARNING, errorCode );
                } else if ( Controller.UNKNOWN_ERROR_CODE.equals( errorCode ) ) {
                	Exception e  = (Exception) arguments[0];
                	e.printStackTrace();
                    logger.log( Level.WARNING, "internal error: " + e.getMessage(), e );
                    errorCode = "internal error has been logged";
                } else {
                    logger.log( Level.WARNING, "reporting error: " + errorCode, arguments );
                    // TODO use resources
                }

                if ( statusText != null )
                    statusText .setText( errorCode );
                else
                    JOptionPane .showMessageDialog( DocumentFrame.this, errorCode, "Command Failure", JOptionPane .ERROR_MESSAGE );
            }

			@Override
            public void clearError()
            {
                if ( statusText != null )
                    statusText .setText( "" );
            }
        };
        mController .setErrorChannel( errors );

        // ---- catch-all ActionListener for locally-handled actions

        final String initSystem = mController .getProperty( "symmetry" );
        localActions = new ActionListener()
        {
            private String system = initSystem;

            private final Map<String, JDialog> shapesDialogs = new HashMap<>();

            private final Map<String, JDialog> directionsDialogs = new HashMap<>();

			@Override
            public void actionPerformed( ActionEvent e )
            {
				Controller delegate = mController;
                String cmd = e.getActionCommand();
                switch ( cmd ) {
                
                case "close":
                    closeWindow();
                    break;

//            	case "openURL":
//                    String url = JOptionPane .showInputDialog( DocumentFrame.this, "Enter the URL for an online .vZome file.", "Open URL",
//                            JOptionPane.PLAIN_MESSAGE );
//                    try {
//                        mController .doAction( cmd, new ActionEvent( DocumentFrame.this, ActionEvent.ACTION_PERFORMED, url ) );
//                    } catch ( Exception ex )
//                    {
//                        ex .printStackTrace();
//                        // TODO better error report
//                        errors .reportError( Controller.USER_ERROR_CODE, new Object[]{ ex } );
//                    }
//                    break;
//                    
                case "save":
                    if ( mFile == null ) {
                        saveAsAction .actionPerformed( e );
                    }
                    else {
                        mController .doFileAction( "save", mFile );
                    }
                    break;

                case "Share":
                    String filePathStr = mController .getProperty( "original.path" );
                    if ( filePathStr == null ) {
                        JOptionPane .showMessageDialog( DocumentFrame.this, "You must save your model before you can share it.",
                                "Command Failure", JOptionPane .ERROR_MESSAGE );
                        return;
                    }
                    Controller githubController = mController .getSubController( "github" );
                    if ( shareDialog == null )
                        shareDialog = new ShareDialog( DocumentFrame.this, githubController );                    
                    githubController .actionPerformed( this, "startShare" );
                    break;

                case "saveDefault":
                    // this is basically "save a copy...", with a hard-coded file path.
                    String fieldName = mController.getProperty( "field.name" );
                    String fieldLabel = mController.getProperty( "field.label" );
                    File prototype = new File( Platform.getPreferencesFolder(), "Prototypes/" + fieldName + ".vZome" );
                    try {
                        String path = prototype.getCanonicalPath();
                        int response = JOptionPane.showConfirmDialog(
                                DocumentFrame.this,
                                "Save as the template for new models in the " + fieldLabel + " field?"
                                + "\n\nTemplate file: " + path,
                                "Save Template?",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE );
                        
                        if ( response == JOptionPane.YES_OPTION ) {
                            logger.config("Saving default template to " + path);
                            mController .doFileAction( "save", prototype );
                        } 
                        else if ( prototype.exists() ) {
                            response = JOptionPane.showConfirmDialog(
                                DocumentFrame.this,
                                "Delete the existing template for new models in the " + fieldLabel + " field?"
                                + "\n\nTemplate file: " + path,
                                "Delete Template?",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE );

                            if ( response == JOptionPane.YES_OPTION ) {
                                logger.config("Deleting default template at " + path);
                                prototype.delete();
                            }
                        }
                    } catch (IOException ex) {
                        errors .reportError( Controller.USER_ERROR_CODE, new Object[]{ ex } );
                    }
                    break;
                    
                case "snapshot.2d":
                    if ( snapshot2dFrame == null ) {
                        snapshot2dFrame = new Snapshot2dFrame( (GraphicsController) mController.getSubController( "snapshot.2d" ), fileDialog );
                    }
                    snapshot2dFrame.setPanelSize( modelPanel .getRenderedSize() );
                    snapshot2dFrame.pack();
                    if ( ! snapshot2dFrame .isVisible() )
                        snapshot2dFrame.repaint();
                    snapshot2dFrame.setVisible( true );
                    break;

                case "showToolsPanel":
                    tabbedPane .setSelectedIndex( 1 );  // should be "tools" tab
                    break;

                case "export4dPolytope":
                    {
                        Controller polytopesController = mController .getSubController( "polytopes" );
                        ActionListener actionListener = new ControllerFileAction( fileDialog, false, cmd, "vef", polytopesController );
                        actionListener .actionPerformed( e );
                    }
                    break;

                case "showPolytopesDialog":
                    Controller polytopesController = mController .getSubController( "polytopes" );
                    if ( polytopesDialog == null )
                        polytopesDialog = new PolytopesDialog( DocumentFrame.this, polytopesController );
                    try {
                    	    polytopesController .actionPerformed( DocumentFrame.this, "setQuaternion" );
                    } catch ( Exception e1 ) {
                        errors .reportError( Controller.USER_ERROR_CODE, new Object[]{ e1 } );
                    }
                    polytopesDialog .setVisible( true );
                    break;
                
//                case "showPythonWindow":
//                    if ( pythonFrame == null ) {
//                        pythonFrame = new JFrame( "Python Scripting" );
//                        pythonFrame .setContentPane( new PythonConsolePanel( pythonFrame, mController ) );
//                    }
//                    pythonFrame .pack();
//                    pythonFrame .setVisible( true );
//                    break;
                    
                case "showZomicWindow":
                    if ( zomicFrame == null ) {
                        zomicFrame = new JFrame( "Zomic Scripting" );
                        zomicFrame .setContentPane( new ZomicEditorPanel( zomicFrame, mController, false ) );
                    }
                    zomicFrame .pack();
                    zomicFrame .setVisible( true );
                    break;

                case "showZomodWindow":
                    if ( zomodFrame == null ) {
                        zomodFrame = new JFrame( "Zomod Scripting" );
                        zomodFrame .setContentPane( new ZomicEditorPanel( zomicFrame, mController, true ) );
                    }
                    zomodFrame .pack();
                    zomodFrame .setVisible( true );
                    break;
                
                case "setItemColor": {
                    Long intval = Long.decode( mController .getProperty( "lastObjectColor" ) );
                    int i = intval.intValue();
                    Color color = new Color((i >> 24) & 0xFF, (i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF);
                    colorChooser .setColor( color );
                    final JDialog dialog = JColorChooser.createDialog( DocumentFrame.this, "Choose Object Color", true, colorChooser,
                        new ActionListener() {
                            
                            @Override
                            public void actionPerformed( ActionEvent e )
                            {
                                Color color = colorChooser .getColor();
                                if ( color == null )
                                    return;
                                int rgb = color .getRGB() & 0xffffff;
                                int alpha = color .getAlpha() & 0xff;
                                String colorString = Integer.toHexString( ( rgb << 8 ) | alpha );
                                mController .setProperty( "lastObjectColor", "#"+colorString );
                                String command = "ColorManifestations/" + colorString;
                                mController .actionPerformed( e .getSource(), command );
                            }
                        }, null );
                    dialog .setVisible( true );
                }
                    break;
                    
                case "setBackgroundColor": {
                    Color color = Color.decode( mController .getProperty( "backgroundColor" ) );
                    colorChooser .setColor( color );
                    final JDialog dialog = JColorChooser.createDialog( DocumentFrame.this, "Choose Background Color", true, colorChooser,
                        new ActionListener() {
                            
                            @Override
                            public void actionPerformed( ActionEvent e )
                            {
                                Color color = colorChooser .getColor();
                                if ( color != null )
                                    mController .setProperty( "backgroundColor", Integer.toHexString( color.getRGB() & 0xffffff ) );
                            }
                        }, null );
                    dialog .setVisible( true );
                }
                    break;
                
                case "usedOrbits":
                    mController .actionPerformed( e .getSource(), e .getActionCommand() ); // TO DO exclusive
                    break;
                
                case "rZomeOrbits":
                case "predefinedOrbits":
                case "setAllDirections":
                    delegate = mController .getSubController( "symmetry." + system );
                    delegate .actionPerformed( e .getSource(), e .getActionCommand() );
                    break;
                
                case "configureShapes":
                    JDialog shapesDialog = shapesDialogs.get( system );
                    if ( shapesDialog == null ) {
                    	delegate = mController .getSubController( "symmetry." + system );
                        shapesDialog = new ShapesDialog( DocumentFrame.this, delegate );
                        shapesDialogs .put( system, shapesDialog );
                    }
                    shapesDialog .setVisible( true );
                    break;
                
                case "configureDirections":
                    JDialog symmetryDialog = directionsDialogs.get( system );
                    if ( symmetryDialog == null ) {
                    	delegate = mController .getSubController( "symmetry." + system );
                        symmetryDialog = new SymmetryDialog( DocumentFrame.this, delegate );
                        directionsDialogs .put( system, symmetryDialog );
                    }
                    symmetryDialog .setVisible( true );
                    break;
                    
                case "addBookmark":
                    String numStr = toolsController .getProperty( "next.tool.number" );
                    int bookmarkNum = Integer .parseInt( numStr );
                    String bookmarkId = "bookmark." + bookmarkNum;
                    String bookmarkName = (String) JOptionPane .showInputDialog( (Component) e .getSource(),
                            "Name the new bookmark:", "New Selection Bookmark",
                            JOptionPane.PLAIN_MESSAGE, null, null, bookmarkId );
                    if ( ( bookmarkName == null ) || ( bookmarkName .length() == 0 ) ) {
                        return;
                    }
                    mController .actionPerformed( e .getSource(), "newTool/" + bookmarkId + "/" + bookmarkName );
                    break;

                default:
                    if ( cmd .startsWith( "LoadVEF/" )
                      || cmd .startsWith( "ImportSimpleMeshJson/" )
                      || cmd .startsWith( "ImportColoredMeshJson/" ) )
                    {
                        Controller importScaleController = mController .getSubController( "importScale" );
                        if ( importScaleDialog == null || importScaleDialog.getTitle() != cmd) {
                            importScaleDialog = new VefImportDialog( DocumentFrame.this, importScaleController, "Set Scale and Rotation",
                                new ControllerFileAction( fileDialog, true, cmd, "vef", controller ) );
                        }
                        try {
                            // The polytopesController knows how to pre-load the quaternion based on a selected strut.
                            // TODO: It's probably better to teach the importScaleController how to do it too
                            // or else make one of them a subcontroller of the other 
                            // so we don't need to invoke them seperately.
                            polytopesController = mController .getSubController( "polytopes" );
                            polytopesController.actionPerformed(DocumentFrame.this, "setQuaternion");
                        } catch (Exception e1) {
                            errors.reportError(Controller.USER_ERROR_CODE, new Object[] { e1 });
                        }
                        importScaleDialog .setVisible( true );
                    }
                    else if ( cmd .startsWith( "setSymmetry." ) )
                    {
                        system = cmd .substring( "setSymmetry.".length() );
                        ExclusiveAction exclusiveAction = getExclusiveAction( cmd, mController );
                        exclusiveAction .actionPerformed( e );
//                        mController .actionPerformed( e .getSource(), e .getActionCommand() ); // TODO getExclusiveAction
                    }
                    else if ( cmd .startsWith( "execCommandLine/" ) )
                    {
                        if ( mFile == null ) {
                            JOptionPane .showMessageDialog( DocumentFrame.this, "You must save your model before you can run a shell command.",
                                    "Command Failure", JOptionPane .ERROR_MESSAGE );
                            return;
                        }
                        String cmdLine = cmd .substring( "execCommandLine/" .length() );
                        cmdLine = cmdLine .replace( "{}", mFile .getName() );
                        logger.log( Level.INFO, "executing command line: " + cmdLine );
                        try {
                            Runtime .getRuntime() .exec( cmdLine, null, mFile .getParentFile() );
                        } catch ( IOException ioe ) {
                            System .err .println( "Runtime.exec() failed on " + cmdLine );
                            ioe .printStackTrace();
                        }
                    }
                    else if ( cmd .startsWith( "showProperties-" ) )
                    {
                        String key = cmd .substring( "showProperties-" .length() );
                        Controller subc = mController .getSubController( key + "Picking" );
                        JOptionPane .showMessageDialog( DocumentFrame.this, subc .getProperty( "objectProperties" ), "Object Properties",
                                JOptionPane.PLAIN_MESSAGE );
                    }
                    else
                        mController .actionPerformed( e .getSource(), e .getActionCommand() );
                    break;
                }
            }
        };

        // -------------------------------------- create panels and tools

        cameraController = (GraphicsController) mController .getSubController( "camera" );
        lessonController = (GraphicsController) mController .getSubController( "lesson" );
        lessonController .addPropertyListener( this );

        ControllerActionListener actionListener = new ControllerActionListener( this .mController );
        
        // Now the component containment hierarchy
        
        JPanel outerPanel = new JPanel( new BorderLayout() );
        setContentPane( outerPanel );
        {
            JPanel leftCenterPanel = new JPanel( new BorderLayout() );
            {
                if ( this .isEditor )
                {
                    JPanel modeAndStatusPanel = new JPanel( new BorderLayout() );
                    JPanel designScenesToggle = new JPanel();
                    modeAndStatusPanel .add( designScenesToggle, BorderLayout.LINE_START );
                    
                    designScenesToggle .add( new JLabel( "Show:" ) );

                    ButtonGroup group = new ButtonGroup();
                    
                    designButton  = new JRadioButton( "Design" );
                    designButton .setSelected( true );
                    designScenesToggle .add( designButton );
                    group .add( designButton );
                    designButton .setActionCommand( "switchToModel" );
                    designButton .addActionListener( actionListener );
                    
                    scenesButton  = new JRadioButton( "Scenes" );
                    scenesButton .setEnabled( false ); // don't check the model, which may be loading concurrently.  PCE will come in due course
                    scenesButton .setSelected( false );
                    designScenesToggle .add( scenesButton );
                    group .add( scenesButton );
                    scenesButton .setActionCommand( "switchToArticle" );
                    scenesButton .addActionListener( actionListener );

                    JPanel spacer = new JPanel();
                    spacer .setMinimumSize( new Dimension( 13, 0 ) );
                    designScenesToggle .add( spacer );

                    snapshotButton = new JButton( "Capture Scene" );
                    //                String imgLocation = "/icons/snapshot.png";
                    //                URL imageURL = getClass().getResource( imgLocation );
                    //                if ( imageURL != null ) {
                    //                    Icon icon = new ImageIcon( imageURL, "capture model snapshot" );
                    //                    snapshotButton .setIcon( icon );
                    //                    Dimension dim = new Dimension( 50, 36 );
                    //                    snapshotButton .setPreferredSize( dim );
                    //                    snapshotButton .setMaximumSize( dim );
                    //                }
                    if ( controller .propertyIsTrue( "enable.article.creation" ) )
                        designScenesToggle .add( snapshotButton );
                    snapshotButton .setActionCommand( "takeSnapshot" );
                    snapshotButton .addActionListener( new ActionListener()
                    {
                        @Override
                        public void actionPerformed( ActionEvent e )
                        {
                            mController .actionPerformed( e .getSource(), e .getActionCommand() ); // sends thumbnailChanged propertyChange, but no listener...
                            scenesButton .doClick();  // switch to article mode, so now there's a listener
                            // now trigger the propertyChange again
                            lessonController .actionPerformed( DocumentFrame.this, "setView" );
                        }
                    } );

                    JPanel statusPanel = new JPanel( new BorderLayout() );
                    statusPanel .setBorder( BorderFactory .createEmptyBorder( 4, 4, 4, 4 ) );
                    statusText = new JLabel( "", JLabel.CENTER );
                    statusText .setBorder( BorderFactory .createLineBorder( Color.LIGHT_GRAY ) );
                    statusText .setForeground( Color .RED );
                    statusPanel .add( statusText );
                    modeAndStatusPanel .add( statusPanel, BorderLayout.CENTER );

                    leftCenterPanel .add(  modeAndStatusPanel, BorderLayout.PAGE_START );
                }

                Scene scene = ((Scene.Provider) mController) .getScene();
                
                // heavyweight (lightweight==false) renders with a much better frame rate,
                //   and this canvas always redraws correctly during window resize.
                // HOWEVER, on Mac OS Sonoma, the heavyweight canvas causes Frame.pack() to spin
                //  in certain circumstances, causing vZome to hang.
                // BUT... the performance is atrocious, so I want to let folks (and me!) force
                //  use of the heavyweight canvas, since crashes/spins seem to vary from system to system.
                boolean lightweight = ! mController .propertyIsTrue( "vzome.jogl.heavyweight.canvas" );
                logger.log( Level.INFO, "vzome.jogl.heavyweight.canvas: " + !lightweight );
                RenderingViewer viewer = factory3d .createRenderingViewer( scene, lightweight );

                modelPanel = new ModelPanel( mController, viewer, this, this .isEditor, fullPower );
                leftCenterPanel .add( modelPanel, BorderLayout.CENTER );
            }
            outerPanel.add( leftCenterPanel, BorderLayout.CENTER );

            // String mag = props .getProperty( "default.magnification" );
            // if ( mag != null ) {
            // float magnification = Float .parseFloat( mag );
            // // TODO this seems to work, but ought not to!
            // viewControl .zoomAdjusted( (int) magnification );
            // }

            JPanel rightPanel = new JPanel( new BorderLayout() );
            {
                Scene scene = ((Scene.Provider) cameraController) .getScene();
                
                // lightweight==true lays out correctly during window resize, though
                //   it delivers a lower frame rate.  See JoglFactory comments.
                RenderingViewer viewer = factory3d .createRenderingViewer( scene, true );
                
                viewControl = new CameraControlPanel( viewer, cameraController );
                // this is probably moot for reader mode
                rightPanel .add( viewControl, BorderLayout.PAGE_START );
                
                modelArticleEditPanel = new JPanel();
                modelArticleCardLayout = new CardLayout();
                modelArticleEditPanel .setLayout( modelArticleCardLayout );
                if ( this .isEditor )
                {
                    Controller sbController = controller .getSubController( "strutBuilder" );
                    JPanel buildPanel = new StrutBuilderPanel( DocumentFrame.this, mController .getProperty( "symmetry" ), sbController, this );
                    if ( this .fullPower )
                    {
                        tabbedPane .addTab( "build", buildPanel );
                        if ( mController .propertyIsTrue( "original.tools" ) )
                        {
                            ToolsPanel toolsPanel = new ToolsPanel( DocumentFrame.this, toolsController );
                            tabbedPane .addTab( "tools", toolsPanel );
                        }
                        
                        JPanel bomPanel = new PartsPanel( mController .getSubController( "parts" ) );
                        tabbedPane .addTab( "parts", bomPanel );
                        
                        JPanel measurePanel = new MeasurePanel( mController .getSubController( "measure" ) );
                        tabbedPane .addTab( "measure", measurePanel );
                        
                        modelArticleEditPanel .add( tabbedPane, "model" );
                    }
                    else
                        modelArticleEditPanel .add( buildPanel, "model" );
                }

                // We always create this, whether we'll need it or not, to simplify the MVC
                //  interactions and concurrency.
                lessonPanel = new LessonPanel( lessonController );
                modelArticleEditPanel .add( lessonPanel, "article" );
                if ( this .isEditor )
                {
                    modelArticleCardLayout .show( modelArticleEditPanel, "model" );
                    modelArticleEditPanel .setMinimumSize( new Dimension( 300, 500 ) );
                    modelArticleEditPanel .setPreferredSize( new Dimension( 300, 800 ) );
                }
                else
                {
                    modelArticleCardLayout .show( modelArticleEditPanel, "article" );
                    modelArticleEditPanel .setMinimumSize( new Dimension( 400, 500 ) );
                    modelArticleEditPanel .setPreferredSize( new Dimension( 400, 800 ) );
                }
                rightPanel .add( modelArticleEditPanel, BorderLayout.CENTER );
            }
            outerPanel.add( rightPanel, BorderLayout.LINE_END );
        }

        JPopupMenu.setDefaultLightWeightPopupEnabled( false );
        ToolTipManager ttm = ToolTipManager.sharedInstance();
        ttm .setLightWeightPopupEnabled( false );

        Controller saveAsController = new DefaultController()
        {
            @Override
            public void doFileAction( String command, final File file )
            {
                mController .doFileAction( command, file );
                mFile = file;
                String newTitle = file .getAbsolutePath();
                mController .setProperty( "name", newTitle );
                setTitle( newTitle );
            }
        };
        mController .addSubController( "saveAs", saveAsController ); // need this so property flow works
		this .saveAsAction = new ControllerFileAction( fileDialog, false, "save", "vZome", saveAsController );

        this .setJMenuBar( new DocumentMenuBar( mController, this ) );

        this.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
        this.addWindowListener( new WindowAdapter()
        {
            @Override
            public void windowClosing( WindowEvent we )
            {
                closeWindow();
            }
        } );

		// Find the screen with the largest area if this is a multi-monitor system.
		// Set the frame size to just a bit smaller than the screen
		//	so the frame will fit on the screen if the user un-maximizes it.
		// Default to opening the window as maximized on the selected (or default) monitor.
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		double bestWidth = 0;
		double bestHeight = 0;
		if (gs.length > 0) {
			GraphicsDevice bestDevice = null;
			String preferredMonitor = mController.getProperty("preferred.monitor");
			if (preferredMonitor != null) {
				try {
					int monitorIndex = Integer.parseInt(preferredMonitor);
					if (monitorIndex < 0) {
						logger.fine("preferred.monitor = " + preferredMonitor + ". Negative values disable the feature.");
					} else if (monitorIndex < gs.length) {
						bestDevice = gs[monitorIndex];
						DisplayMode dm = bestDevice.getDisplayMode();
						AffineTransform t = bestDevice.getDefaultConfiguration().getDefaultTransform();
						bestWidth = dm.getWidth() / t.getScaleX();
						bestHeight = dm.getHeight() / t.getScaleY();
						logger.config("Using preferred.monitor = " + preferredMonitor 
								+ " @ resolution: " + bestWidth + "x" + bestHeight);
					} else {
						logger.warning("Ignoring preferred.monitor = " + preferredMonitor + ".\n" 
								+ "Only " + gs.length + " monitors are available.");
					}
				} catch (NumberFormatException ex) {
					logger.warning("Invalid preferred.monitor = " + preferredMonitor + " in prefs");
				}
			}
			if (bestDevice == null) { // if not specified in prefs file or if value is out of range
				int bestMonitor = 0;
				double bestArea = 0;
				for (int i = 0; i < gs.length; i++) {
					GraphicsDevice testDevice = gs[i];
					AffineTransform t = testDevice.getDefaultConfiguration().getDefaultTransform();
					DisplayMode testMode = testDevice.getDisplayMode();
					int testWidth = testMode.getWidth();
					int testHeight = testMode.getHeight();
					double testArea = testHeight * testWidth;
					if ( "true" .equals( System.getProperty( "vzome.glcanvas.rescaling" ) ) ) {
					    testArea = testArea / ( t.getScaleX() * t.getScaleY() );
					}
					if (bestArea < testArea) {
						bestArea = testArea;
						bestDevice = testDevice;
						bestWidth = testWidth;
						bestHeight = testHeight;
						bestMonitor = i;
					}
				}
				logger.config("Using monitor[" + bestMonitor + "] @ resolution: " + bestWidth + "x" + bestHeight);
			}
			Rectangle bounds = bestDevice.getDefaultConfiguration().getBounds();
			this.setLocation(bounds.x, bounds.y);
		}
		
		String aspectRatioString = mController .getProperty( "aspect.ratio" );
		if ( aspectRatioString != null ) {
		    // aspect.ratio should be width/height
		    try {
	            float aspectRatio = Float.parseFloat( aspectRatioString );
	            // apply some reasonable limits as validation
	            if(aspectRatio >= 0.6 && aspectRatio <= 3) {
		            int arWidth = (int) (aspectRatio * bestHeight);
		            if ( arWidth <= bestWidth ) {
		            	// reduce the width
		                bestWidth = arWidth;
			    	} else {
			    		int arHeight = (int) (bestWidth / aspectRatio);
			            if ( arHeight <= bestHeight ) {
			            	// reduce the height
			                bestHeight = arHeight;
			            }
		            }
	                logger.fine( "aspect.ratio: " + aspectRatioString 
	                		+ " = width: " + bestWidth + " / height: " + bestHeight);
	            } else {
	                logger.warning( "ignoring aspect.ratio: " + aspectRatioString + " out of range.");
	            	aspectRatioString = null;
	            }
            } catch (NumberFormatException e) {
                logger.warning( "ignoring invalid aspect.ratio: " + aspectRatioString );
            }
		}

		try {
			// This is where the GraphicsConfiguration failed on David's Windows 10 using Java 17
			// before including the "--add-exports" JVM args to the build 
            logger.log( Level.INFO, "about to pack the frame " + ((mFile == null)? "untitled" :mFile .getName()) );
			this.pack();
            logger.log( Level.INFO, "packed the frame" );
	        this.setVisible( true );
            logger.log( Level.INFO, "frame setVisible" );
	        // Java 17 seems to successfully set the frame size and extended state only after the frame is visible.
	        if(bestWidth > 0 && bestHeight > 0) {
	        	double downSize = 1.0;
	        	if(aspectRatioString == null) {
	        		double n = 15, d = n + 1; // set NORMAL size to 15/16 of scaled full screen size before maximizing it
	        		downSize = n/d;
	        	}
				int scaledWidth = (int)(bestWidth * downSize);
				int scaledHeight = (int)(bestHeight * downSize);
				this.setSize(scaledWidth, scaledHeight);
	        }
	        if ( aspectRatioString == null ) // this maxes the frame size, so we don't want it when controlling aspect ratio
	            this.setExtendedState(MAXIMIZED_BOTH);
	        this.setFocusable( true );
		} catch (Exception ex) {
			ex.printStackTrace();
			final String msg = "Failed to initialize GraphicsConfiguration.\nExiting the application.";
			errors.reportError(msg, new Object[] { ex } );
			// go ahead and exit so the process is killed
			// and log files have their associated .lck files removed correctly.
			JOptionPane.showMessageDialog( null, msg, "vZome Fatal Error", JOptionPane.ERROR_MESSAGE );
			System.exit(-1);
		}

        SwingWorker<Exception, Object> finisher = new SwingWorker<Exception, Object>()
        {
            @Override
            protected Exception doInBackground() throws Exception
            {
                try {
                    mController .actionPerformed( this, "finish.load" );
                    return null;
                } catch ( Exception e ) {
                    logger .log( Level.INFO, e .getMessage(), e );
                    return e;
                }
            }

            @Override
            protected void done()
            {
                try {
                    Exception error = get();
                    if ( error != null ) {
                        JOptionPane .showMessageDialog( DocumentFrame.this,
                                error .getLocalizedMessage(),
                                "Error Loading Document", JOptionPane.ERROR_MESSAGE );
                        // setting "visible" to FALSE will remove this document from the application controller's 
                        // document collection so its document count is correct and it cleans up correctly 
                        mController .setProperty( "visible", Boolean.FALSE );
                        DocumentFrame.this .dispose();
                    }
                    else {
                        String title = mController .getProperty( "window.title" );
                        boolean migrated = mController .propertyIsTrue( "migrated" );
                        
                        boolean asTemplate = mController .propertyIsTrue( "as.template" );

//                        URL url = null; // TODO
                        if ( ! mController .userHasEntitlement( "model.edit" ) )
                        {
                            mController .actionPerformed( DocumentFrame.this, "switchToArticle" );
//                            if ( url != null )
//                                title = url .toExternalForm();
                            migrated = false;
                        }

                        String macro = mController .getProperty( "macro" );
                        String imgFormat = mController .getProperty( "imageFormat" );
                        if ( macro != null ) {
                            ActionListener performer = getMacroPerformer( macro + ",quit", mController );
                            performer .actionPerformed( new ActionEvent( DocumentFrame.this, ActionEvent.ACTION_PERFORMED, "doMacroStep" ) );
                        }
                        else if ( imgFormat != null ) {
                            ActionListener performer = getMacroPerformer( "capture." + imgFormat + ",quit", mController );
                            performer .actionPerformed( new ActionEvent( DocumentFrame.this, ActionEvent.ACTION_PERFORMED, "doMacroStep" ) );
                        }
                        else if ( ! asTemplate && migrated ) { // a migration
                            final String NL = System .getProperty( "line.separator" );
                            if ( mController .propertyIsTrue( "autoFormatConversion" ) )
                            {
                                if ( mController .propertyIsTrue( "formatIsSupported" ) )
                                    JOptionPane .showMessageDialog( DocumentFrame.this,
                                            "This document was created by an older version." + NL + 
                                            "If you save it now, it will be converted automatically" + NL +
                                            "to the current format.  It will no longer open using" + NL +
                                            "the older version.",
                                            "Automatic Conversion", JOptionPane.INFORMATION_MESSAGE );
                                else
                                {
                                    title = null;
                                    DocumentFrame.this .makeUnnamed();
                                    JOptionPane .showMessageDialog( DocumentFrame.this,
                                            "You have \"autoFormatConversion\" turned on," + NL + 
                                            "but the behavior is disabled until this version of vZome" + NL +
                                            "is stable.  This converted document is being opened as" + NL +
                                            "a new document.",
                                            "Automatic Conversion Disabled", JOptionPane.INFORMATION_MESSAGE );
                                }
                            }
                            else
                            {
                                title = null;
                                DocumentFrame.this .makeUnnamed();
                                JOptionPane .showMessageDialog( DocumentFrame.this,
                                        "This document was created by an older version." + NL + 
                                        "It is being opened as a new document, so you can" + NL +
                                        "still open the original using the older version.",
                                        "Outdated Format", JOptionPane.INFORMATION_MESSAGE );
                            }
                        }

                        if ( title == null )
                            title = mController .getProperty( "untitled.title" );
                        
                        DocumentFrame.this .setTitle( title );
                    }
                    super .done();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        finisher .execute();
    }

    private ExclusiveAction getExclusiveAction( final String action, final Controller controller )
    {
        return new ExclusiveAction( getExcluder() )
        {
			@Override
            protected void doAction( ActionEvent e ) throws Exception
            {
				controller .actionPerformed( e .getSource(), action );
            }

			@Override
            protected void showError( Exception e )
            {
                JOptionPane.showMessageDialog( DocumentFrame.this, e.getMessage(), "Command failure", JOptionPane.ERROR_MESSAGE );
            }
        };
    }
    
    private ActionListener getActionListener( String command, Controller controller )
    {
        final ActionListener actionListener;
        switch ( command ) {

        // these can fall through to the ApplicationController
        case "quit":
        case "new":
        case "new-rootTwo":
        case "new-rootThree":
        case "new-heptagon":
        case "new-snubDodec":
        case "openURL":
        case "showAbout":

            // these will be handled by the DocumentController
        case "toggleWireframe":
        case "toggleOrbitViews":
        case "toggleStrutScales":
        case "toggleFrameLabels":
        case "toggleNormals":
        case "toggleOutlines":
            actionListener = new ControllerActionListener(controller);
            break;

        case "open":
        case "newFromTemplate":
        case "openDeferringRedo":
            actionListener = new ControllerFileAction( fileDialog, true, command, "vZome", controller );
            break;

        case "saveAs":
            actionListener = saveAsAction;
            break;

        case "save":
        case "saveDefault":
        case "close":
        case "snapshot.2d":
        case "showToolsPanel":
        case "setItemColor":
        case "setBackgroundColor":
        case "showPolytopesDialog":
        case "showZomicWindow":
        case "showZomodWindow":
        case "showPythonWindow":
        case "rZomeOrbits":
        case "predefinedOrbits":
        case "usedOrbits":
        case "setAllDirections":
        case "configureShapes":
        case "configureDirections":
        case "addBookmark":
        case "export4dPolytope":
            actionListener = this .localActions;
            break;

        case "capture-wiggle-gif":
        case "capture-animation":
            actionListener = new ControllerFileAction( fileDialog, false, command, "gif", controller );
            break;

        default:
            if ( command .startsWith( "openResource-" ) ) {
                actionListener = new ControllerActionListener(controller);
            }
            else if ( command .startsWith( "LoadVEF/" ) ) {
                actionListener = this .localActions;
            }
            else if ( command .startsWith( "ImportSimpleMeshJson/" ) ) {
                actionListener = this .localActions;
            }
            else if ( command .startsWith( "ImportColoredMeshJson/" ) ) {
                actionListener = this .localActions;
            }
            else if ( command .startsWith( "setSymmetry." ) ) {
                actionListener = this .localActions;
            }
            else if ( command .startsWith( "execCommandLine/" ) ) {
                actionListener = this .localActions;
            }
            else if ( command .startsWith( "showProperties-" ) ) {
                actionListener = this .localActions;
            }
            else if ( command .startsWith( "capture." ) ) {
                String ext = command .substring( "capture." .length() );
                actionListener = new ControllerFileAction( fileDialog, false, command, ext, controller );
            }
            else if ( command .startsWith( "export2d." ) ) {
                String ext = command .substring( "export2d." .length() );
                actionListener = new ControllerFileAction( fileDialog, false, command, ext, controller );
            }
            else if ( command .startsWith( "export." ) ) {
                String ext = command .substring( "export." .length() );
                ext = controller .getProperty( "exportExtension." + ext );
                actionListener = new ControllerFileAction( fileDialog, false, command, ext, controller );
            }
            else if ( command .startsWith( "MACRO/" ) ) {
                String rest = command .substring( "MACRO/" .length() );
                actionListener = getMacroPerformer( rest, controller );
            }
            else {
                actionListener = getExclusiveAction( command, controller );
            }
        }
        return actionListener;
    }
    
    @Override
    public AbstractButton setButtonAction( String command, Controller controller, AbstractButton control )
    {
        control .setActionCommand( command );
        boolean enable = true;
        switch ( command ) {

        // TODO: find a better way to disable these... they are found in OrbitPanel.java
        case "predefinedOrbits":
        case "usedOrbits":
        case "setAllDirections":
            enable = fullPower;
            break;

        case "save":
        case "saveAs":
        case "saveDefault":
            enable = this .canSave;
            break;

        case "Share":
            enable = controller .getProperty( "original.path" ) != null;
            // We're doing this one immediately, because we need the listener attached if
            //  it is later enabled.  It is surprising that this has not been an issue for
            //  anything else!
            control .addActionListener( this .localActions );
            control .setEnabled( enable );
            return control;

        default:
            if ( command .startsWith( "export." ) ) {
                enable = this .canSave;
            }
        }
        control .setEnabled( enable );
        if ( control .isEnabled() )
        {
            if ( command .startsWith( "MACRO/" ) ) {
                // We don't want recursive macros, so this case is separated out
                String rest = command .substring( "MACRO/" .length() );
                ActionListener actionListener = getMacroPerformer( rest, controller );
                control .addActionListener( actionListener );
            }
            else {
                ActionListener actionListener = getActionListener( command, controller );
                if ( actionListener instanceof ExclusiveAction )
                    this .mExcluder .addExcludable( control );
                control .addActionListener( actionListener );
            }
        }
        return control;
    }
    
    private ActionListener getMacroPerformer( String macro, Controller controller )
    {
        String[] commands = macro .split( "," );
        ActionListener[] actions = Arrays.stream( commands )
                .map( ( cmd ) -> new ActionListener() {
                    @Override
                    public void actionPerformed( ActionEvent arg0 )
                    {
                        ActionListener listener = getActionListener( cmd, controller );
                        if ( listener instanceof ControllerFileAction )
                            ((ControllerFileAction) listener) .setHeadless( true );
                        listener .actionPerformed( new ActionEvent( arg0.getSource(), arg0.getID(), cmd ));
                    }
                } )
                .toArray( ActionListener[]::new );
        return new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                for ( ActionListener actionListener : actions ) {
                    actionListener .actionPerformed( e );
                }
            }
        };
    }
    
    @Override
    public JMenuItem setMenuAction( String command, Controller controller, JMenuItem menuItem )
    {
        return (JMenuItem) this .setButtonAction( command, controller, menuItem );
    }

	@Override
    public void propertyChange( PropertyChangeEvent e )
    {
		switch ( e .getPropertyName() ) {

		case "command.status":
            if ( statusText != null && ! this .developerExtras )
                statusText .setText( (String) e .getNewValue() );
			break;

		case "current.edit.xml":
			if ( statusText != null && this .developerExtras )
				statusText .setText( (String) e .getNewValue() );
			break;

		case "editor.mode":
            String mode = (String) e .getNewValue();
            int width = 0;
            if ( "article" .equals( mode ) )
            {
                width = 400;
                if ( snapshotButton != null ) // this gets called just once for the Reader
                    snapshotButton .setEnabled( false );
//                viewControl .setVisible( false );
                mExcluder .grab();
            }
            else
            {                   
                width = 300;
                snapshotButton .setEnabled( true );
//                viewControl .setVisible( true );
                mExcluder .release();
            }
            modelArticleCardLayout .show( modelArticleEditPanel, mode );
            modelArticleEditPanel .setMinimumSize( new Dimension( width, 500 ) );
            modelArticleEditPanel .setPreferredSize( new Dimension( width, 800 ) );
			break;

		case "has.pages":
            if ( scenesButton != null )
            {
                boolean enable = e .getNewValue() .toString() .equals( "true" );
                scenesButton .setEnabled( enable );
                if ( ! enable )
                    designButton .doClick();
            }
			break;
			
		case "window.title":
            this .setTitle( e .getNewValue() .toString() );
            break;
			
		case "visible":
			if ( Boolean.TRUE .equals( e .getNewValue() ) ) {
				this .appUI .propertyChange( e ); // remove this window from the UI's collection
				this .setVisible( true );
			}
            break;
		}
    }

	// called from ApplicationUI on quit
	//
	boolean closeWindow()
	{
	    if ( "true".equals( mController.getProperty( "edited" ) ) && ( isEditor && canSave ) )
	    {
	        // TODO replace showConfirmDialog() with use of EscapeDialog, or something similar...
	        //   see  http://java.sun.com/docs/books/tutorial/uiswing/components/dialog.html
	        int response = JOptionPane.showConfirmDialog( DocumentFrame.this, "Do you want to save your changes?",
	                "file is changed", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE );

	        switch(response) {
	        case JOptionPane.YES_OPTION: 
	            try {
	                localActions .actionPerformed( new ActionEvent( DocumentFrame.this, ActionEvent.ACTION_PERFORMED, "save" ) );
	            } catch ( RuntimeException re ) {
	                logger.log( Level.WARNING, "Did not save due to error", re );
	            }
	            return false; // Queued up the file-save action instead of closing the window
	        case JOptionPane.NO_OPTION:
	        	break; // Don't want to save, so go ahead close the main window
	        case JOptionPane.CANCEL_OPTION:
	        	return false; // Don't close window
	        case JOptionPane.CLOSED_OPTION:
	        	return false; // Don't close window
	        default:
	        	logger.warning("Ignoring undocumented response: " + response );
	        	return false; // Don't close window
	        }
	    }
	    dispose();
	    mController .setProperty( "visible", Boolean.FALSE );
	    return true;
	}

	public void makeUnnamed()
	{
		this .mFile = null;
		this .mController .setProperty( "name", null );
	}
}
