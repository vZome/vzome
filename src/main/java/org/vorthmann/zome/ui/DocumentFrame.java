package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;

import org.vorthmann.j3d.J3dComponentFactory;
import org.vorthmann.j3d.Platform;
import org.vorthmann.ui.Controller;
import org.vorthmann.ui.ExclusiveAction;

import com.vzome.desktop.controller.ViewPlatformControlPanel;

public class DocumentFrame extends JFrame implements PropertyChangeListener
{
    private static final class ContextualMenuMouseListener extends MouseAdapter
    {
        private final Controller controller;

        private final PickerPopup pickerPopup;

        private ContextualMenuMouseListener( Controller controller, PickerPopup pickerPopup )
        {
            this.controller = controller;
            this.pickerPopup = pickerPopup;
        }

		@Override
        public void mousePressed( MouseEvent e )
        {
            maybeShowPopup( e );
        }

		@Override
        public void mouseReleased( MouseEvent e )
        {
            maybeShowPopup( e );
        }

        private void maybeShowPopup( MouseEvent e )
        {
            if ( e.isPopupTrigger() ) {
                pickerPopup.enableActions( controller, e );
                pickerPopup.show( e.getComponent(), e.getX(), e.getY() );
            }
        }
    }

    private static final long serialVersionUID = 1L;

    private File mFile; // this means that frames and files are one-to-one!

    private final JFrame mZomicFrame, mPythonFrame;

    protected final Controller mController, toolsController;

    private final JPanel mModelPanel, mMonocularPanel, outerPanel, rightPanel, modeAndStatusPanel, leftCenterPanel, modelAndToolbarPanel;
        
    private final JTabbedPane tabbedPane = new JTabbedPane();
    
    private final CardLayout monoStereoCardLayout;

    private final ExclusiveAction.Excluder mExcluder = new ExclusiveAction.Excluder();

    private final ApplicationUI mAppUI;

    // this will choose a file first
    private final ActionListener saveAction;

    private Snapshot2dFrame snapshot2dFrame = null;

    private final boolean developerExtras, enable4d, metaModels;

    private boolean isEditor;

    private boolean fullPower;

    private CardLayout modelArticleCardLayout;

    private static final Logger logger = Logger.getLogger( "org.vorthmann.zome.ui" );

    private LessonPanel lessonPanel;

    private JButton snapshotButton;

    private JToolBar toolBar;

    private JRadioButton articleButton;

    private JMenuItem setColorMenuItem;

    private JMenuItem showToolsMenuItem;

    private JMenuItem zomicMenuItem, pythonMenuItem;

    private JMenu import3dSubmenu;

    private JRadioButton modelButton;

    private JPanel viewControl;

    private JPanel modelArticleEditPanel;

    private JLabel statusText;

    private MouseListener modelPopupClicks;

    private Component monocularCanvas;

    private Component leftEyeCanvas;

    private Component rightEyeCanvas;

    private Controller viewPlatform;

    private boolean canSave;

    private Controller lessonController;
    
    private JDialog polytopesDialog;

    public ExclusiveAction.Excluder getExcluder()
    {
        return mExcluder;
    }

    void setFile( File file )
    {
        mFile = file;
    }
    
    private void switchToMode( String mode )
    {
        int width = 0;
        if ( "article" .equals( mode ) )
        {
            width = 400;
            if ( snapshotButton != null ) // this gets called just once for the Reader
                snapshotButton .setEnabled( false );
            setColorMenuItem .setEnabled( false );
            showToolsMenuItem .setEnabled( false );
            pythonMenuItem .setEnabled( false );
            zomicMenuItem .setEnabled( false );
            import3dSubmenu .setEnabled( false );
            toolBar .setVisible( false );
            if ( isEditor )
            {
                if ( "true" .equals( viewPlatform .getProperty( "stereo" ) ) )
                {
                    leftEyeCanvas .removeMouseListener( modelPopupClicks );
                    rightEyeCanvas .removeMouseListener( modelPopupClicks );
                }
                else
                {
                    monocularCanvas .removeMouseListener( modelPopupClicks );
                }
            }
//            viewControl .setVisible( false );
            mExcluder .grab();
        }
        else
        {                   
            width = 300;
            snapshotButton .setEnabled( true );
            setColorMenuItem .setEnabled( true );
            showToolsMenuItem .setEnabled( fullPower );
            pythonMenuItem .setEnabled( fullPower );
            zomicMenuItem .setEnabled( fullPower );
            import3dSubmenu .setEnabled( fullPower );
            if ( isEditor && ! "true" .equals( mController .getProperty( "no.toolbar" ) ) )
                toolBar .setVisible( true );
            if ( isEditor )
            {
                if ( "true" .equals( viewPlatform .getProperty( "stereo" ) ) )
                {
                    leftEyeCanvas .addMouseListener( modelPopupClicks );
                    rightEyeCanvas .addMouseListener( modelPopupClicks );
                }
                else
                {
                    monocularCanvas .addMouseListener( modelPopupClicks );
                }
            }
//            viewControl .setVisible( true );
            mExcluder .release();
        }
        modelArticleCardLayout .show( modelArticleEditPanel, mode );
        modelArticleEditPanel .setMinimumSize( new Dimension( width, 500 ) );
        modelArticleEditPanel .setPreferredSize( new Dimension( width, 800 ) );
    }

    public DocumentFrame( final ApplicationUI appui, File file, final Controller controller )
    {
        mAppUI = appui;
        mFile = file;

        mController = controller;
        controller .addPropertyListener( this );
        toolsController = mController .getSubController( "tools" );

        String fieldName = controller.getProperty( "field.name" );
        boolean isGolden = "golden".equals( fieldName );
        boolean isSnubDodec = "snubDodec".equals( fieldName );
        boolean isRootThree = "rootThree".equals( fieldName );
        boolean isRootTwo = "rootTwo".equals( fieldName );
        boolean isHeptagon = "heptagon".equals( fieldName );
        
        boolean readerPreview = controller .propertyIsTrue( "reader.preview" );
        
        this.isEditor = controller .userHasEntitlement( "model.edit" ) && ! readerPreview;

        this.canSave = controller .userHasEntitlement( "save.files" );

        this.fullPower = isEditor && controller .userHasEntitlement( "all.tools" );

        developerExtras = fullPower && controller .userHasEntitlement( "developer.extras" );

        enable4d = developerExtras || ( fullPower && controller .userHasEntitlement( "4d.symmetries" ) );

        metaModels = developerExtras || ( fullPower && controller .userHasEntitlement( "meta.models" ) );

        mZomicFrame = new JFrame( "Zomic Scripting" );
        mZomicFrame.setContentPane( new ZomicEditorPanel( mZomicFrame, controller ) );
        mZomicFrame.pack();
        
        mPythonFrame = new JFrame( "Python Scripting" );
        mPythonFrame .setContentPane( new PythonConsolePanel( mPythonFrame, controller ) );
        mPythonFrame .pack();
        
        polytopesDialog = new PolytopesDialog( this, controller .getSubController( "polytopes" ) );

        final Controller.ErrorChannel errors = new Controller.ErrorChannel()
        {
			@Override
            public void reportError( String errorCode, Object[] arguments )
            {
                if ( Controller.USER_ERROR_CODE.equals( errorCode ) ) {
                    errorCode = ( (Exception) arguments[0] ).getMessage();
                    // don't want a stack trace for a user error
                    logger.log( Level.WARNING, errorCode );
                } else if ( Controller.UNKNOWN_ERROR_CODE.equals( errorCode ) ) {
                    errorCode = ( (Exception) arguments[0] ).getMessage();
                    logger.log( Level.WARNING, "internal error: " + errorCode, ( (Exception) arguments[0] ) );
                    errorCode = "internal error, see vZomeLog0.log in your home directory";
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
        controller.setErrorChannel( errors );

        // -------------------------------------- create panels and tools

        final PickerPopup modelPopupMenu = new PickerPopup();
        modelPopupMenu.setLightWeightPopupEnabled( false );

        PickerPopup directionPopupMenu = new PickerPopup();
        directionPopupMenu.setLightWeightPopupEnabled( false );

        viewPlatform = controller .getSubController( "viewPlatform" );
        lessonController = controller .getSubController( "lesson" );
        lessonController .addPropertyListener( this );

        // Now the component containment hierarchy
        
        outerPanel = new JPanel( new BorderLayout() );
        setContentPane( outerPanel );
        {
            leftCenterPanel = new JPanel( new BorderLayout() );
            {
                if ( this .isEditor )
                {
                    modeAndStatusPanel = new JPanel( new BorderLayout() );
                    JPanel articleButtonsPanel = new JPanel();
                    modeAndStatusPanel .add( articleButtonsPanel, BorderLayout.LINE_START );

                    ButtonGroup group = new ButtonGroup();
                    modelButton  = new JRadioButton( "Model" );
                    modelButton .setSelected( true );
                    articleButtonsPanel .add( modelButton );
                    group .add( modelButton );
                    modelButton .setActionCommand( "switchToModel" );
                    modelButton .addActionListener( mController );
                    snapshotButton = new JButton( "-> capture ->" );
                    //                String imgLocation = "/icons/snapshot.png";
                    //                URL imageURL = getClass().getResource( imgLocation );
                    //                if ( imageURL != null ) {
                    //                    Icon icon = new ImageIcon( imageURL, "capture model snapshot" );
                    //                    snapshotButton .setIcon( icon );
                    //                    Dimension dim = new Dimension( 50, 36 );
                    //                    snapshotButton .setPreferredSize( dim );
                    //                    snapshotButton .setMaximumSize( dim );
                    //                }
                    articleButtonsPanel .add( snapshotButton );
                    snapshotButton .setActionCommand( "takeSnapshot" );
                    snapshotButton .addActionListener( new ActionListener()
                    {
						@Override
                        public void actionPerformed( ActionEvent e )
                        {
                            mController .actionPerformed( e ); // sends thumbnailChanged propertyChange, but no listener...
                            articleButton .doClick();  // switch to article mode, so now there's a listener
                            // now trigger the propertyChange again
                            lessonController .actionPerformed( new ActionEvent( DocumentFrame.this, ActionEvent.ACTION_PERFORMED, "setView" ) );
                        }
                    } );
                    articleButton  = new JRadioButton( "Article" );
                    articleButton .setEnabled( lessonController .propertyIsTrue( "has.pages" ) );
                    articleButton .setSelected( false );
                    articleButtonsPanel .add( articleButton );
                    group .add( articleButton );
                    articleButton .setActionCommand( "switchToArticle" );
                    articleButton .addActionListener( mController );

                    JPanel statusPanel = new JPanel( new BorderLayout() );
                    statusPanel .setBorder( BorderFactory .createEmptyBorder( 4, 4, 4, 4 ) );
                    statusText = new JLabel( "", JLabel.CENTER );
                    statusText .setBorder( BorderFactory .createLineBorder( Color.LIGHT_GRAY ) );
                    statusText .setForeground( Color .RED );
                    statusPanel .add( statusText );
                    modeAndStatusPanel .add( statusPanel, BorderLayout.CENTER );

                    leftCenterPanel .add(  modeAndStatusPanel, BorderLayout.PAGE_START );
                }
                else
                    modeAndStatusPanel = null;

                modelAndToolbarPanel = new JPanel( new BorderLayout() );
                {
                    toolBar = new JToolBar( "vZome Toolbar" );
                    toolBar .setOrientation( JToolBar.VERTICAL );
                    String toolbarLoc = controller .getProperty( "toolbar.position" );
                    if ( toolbarLoc == null )
                    	toolbarLoc = BorderLayout .LINE_END;
                    if ( this .isEditor && ! "true" .equals( controller.getProperty( "no.toolbar" ) ) )
                        modelAndToolbarPanel .add( toolBar, toolbarLoc );

                    mModelPanel = new JPanel();
                    {
                        monoStereoCardLayout = new CardLayout();
                        mModelPanel .setLayout( monoStereoCardLayout );
                        boolean showStereo =  "true" .equals( viewPlatform .getProperty( "stereo" ) );

                        modelPopupClicks = new ContextualMenuMouseListener( controller, modelPopupMenu );

                        mMonocularPanel = new JPanel( new BorderLayout() );
                        {
                            mMonocularPanel .setPreferredSize( new Dimension( 2000, 2000 ) );
                            monocularCanvas = ( (J3dComponentFactory) controller ).createJ3dComponent( "mainViewer-monocular" );
                            mMonocularPanel .add( monocularCanvas, BorderLayout.CENTER );
                            if ( this .isEditor )
                                monocularCanvas .addMouseListener( modelPopupClicks );
                        }
                        mModelPanel .add( mMonocularPanel, "mono" );
                        JPanel stereoPanel = new JPanel();
                        {
                            GridLayout grid = new GridLayout( 1, 2 );
                            stereoPanel .setLayout( grid );
                            leftEyeCanvas = ( (J3dComponentFactory) controller ).createJ3dComponent( "mainViewer-leftEye" );
                            stereoPanel .add( leftEyeCanvas );
                            rightEyeCanvas = ( (J3dComponentFactory) controller ).createJ3dComponent( "mainViewer-rightEye" );
                            stereoPanel .add( rightEyeCanvas );

                            if ( this .isEditor )
                            {
                                leftEyeCanvas .addMouseListener( modelPopupClicks );
                                rightEyeCanvas .addMouseListener( modelPopupClicks );
                            }
                        }
                        mModelPanel .add( stereoPanel, "stereo" );
                        if ( showStereo )
                            monoStereoCardLayout .show( mModelPanel, "stereo" );
                        else
                            monoStereoCardLayout .show( mModelPanel, "mono" );
                        viewPlatform .addPropertyListener( new PropertyChangeListener(){
							@Override
                            public void propertyChange( PropertyChangeEvent chg )
                            {
                                if ( "stereo" .equals( chg .getPropertyName() ) )
                                    if ( ((Boolean) chg .getNewValue()) .booleanValue() )
                                        monoStereoCardLayout .show( mModelPanel, "stereo" );
                                    else
                                        monoStereoCardLayout .show( mModelPanel, "mono" );
                            }} );
                    }
                    modelAndToolbarPanel .add( mModelPanel, BorderLayout.CENTER );
                }
                leftCenterPanel .add( modelAndToolbarPanel, BorderLayout.CENTER );
            }
            outerPanel.add( leftCenterPanel, BorderLayout.CENTER );

            // String mag = props .getProperty( "default.magnification" );
            // if ( mag != null ) {
            // float magnification = Float .parseFloat( mag );
            // // TODO this seems to work, but ought not to!
            // viewControl .zoomAdjusted( (int) magnification );
            // }

            rightPanel = new JPanel( new BorderLayout() );
            {
                Component trackballCanvas = ( (J3dComponentFactory) controller ) .createJ3dComponent( "controlViewer" );
                viewControl = new ViewPlatformControlPanel( trackballCanvas, viewPlatform );
                // this is probably moot for reader mode
                rightPanel .add( viewControl, BorderLayout.PAGE_START );
                
                modelArticleEditPanel = new JPanel();
                modelArticleCardLayout = new CardLayout();
                modelArticleEditPanel .setLayout( modelArticleCardLayout );
                if ( this .isEditor )
                {
                    String[] symmNames = controller.getCommandList( "symmetries." + fieldName );
                    MouseListener orbitPopup = new ContextualMenuMouseListener( controller, directionPopupMenu );
                    JPanel buildPanel = new StrutBuilderPanel( DocumentFrame.this, symmNames, controller, orbitPopup );
                    if ( this .fullPower )
                    {
                        tabbedPane .addTab( "build", buildPanel );
                        JPanel toolsPanel = new ToolsPanel( DocumentFrame.this, toolsController );
                        tabbedPane .addTab( "tools", toolsPanel );
                        
                        JPanel bomPanel = new PartsPanel( controller .getSubController( "parts" ) );
                        tabbedPane .addTab( "parts", bomPanel );
                        
                        modelArticleEditPanel .add( tabbedPane, "model" );
                    }
                    else
                        modelArticleEditPanel .add( buildPanel, "model" );
                }
                {
                    lessonPanel = new LessonPanel( lessonController );
                    
                    modelArticleEditPanel .add( lessonPanel, "article" );
                }

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

        // create the contextual menus
        // -----------------------------------------------

        JMenuItem menuItem;

        menuItem = createMenuItem( "Copy This View", "copyThisView", mController );
        modelPopupMenu.add( menuItem );

        menuItem = createMenuItem( "Use Copied View", "useCopiedView", mController );
        modelPopupMenu.add( menuItem );

        modelPopupMenu.addSeparator();

        menuItem = createMenuItem( "Look At This", "lookAtBall", mController );
        modelPopupMenu.add( menuItem );

        menuItem = createMenuItem( "Look At Origin", "lookAtOrigin", mController );
        modelPopupMenu.add( menuItem );

        menuItem = createMenuItem( "Look At Symmetry Center", "lookAtSymmetryCenter", mController );
        modelPopupMenu.add( menuItem );

        modelPopupMenu.addSeparator();

        menuItem = createMenuItem( "Set Symmetry Center", "setSymmetryCenter", mController );
        modelPopupMenu.add( menuItem );

        menuItem = createMenuItem( "Set Symmetry Axis", "setSymmetryAxis", mController );
        modelPopupMenu.add( menuItem );

        modelPopupMenu.addSeparator();

        menuItem = createMenuItem( "Set Working Plane", "setWorkingPlane", mController );
        modelPopupMenu.add( menuItem );

        menuItem = createMenuItem( "Set Working Plane Axis", "setWorkingPlaneAxis", mController );
        modelPopupMenu.add( menuItem );

        modelPopupMenu.addSeparator();

        menuItem = createMenuItem( "Select All Similar", "selectSimilarSize", mController );
        modelPopupMenu.add( menuItem );

        modelPopupMenu.addSeparator();

        menuItem = createMenuItem( "Set Background Color...", "setBackgroundColor", new ActionListener()
        {
			@Override
            public void actionPerformed( ActionEvent e )
            {
                Color color = JColorChooser.showDialog( DocumentFrame.this, "Choose Background Color", null );
                if ( color != null )
                    mController.setProperty( "backgroundColor", Integer.toHexString( color.getRGB() & 0xffffff ) );
            }
        } );
        modelPopupMenu .add( menuItem );

        modelPopupMenu .addSeparator();

        menuItem = doCreateMenuItem( "Build With This", "setBuildOrbitAndLength", fullPower, mController, KeyEvent.CHAR_UNDEFINED, 0 );
        modelPopupMenu.add( menuItem );

        menuItem = doCreateMenuItem( "Show Properties", "showProperties", fullPower, new ActionListener()
        {
			@Override
            public void actionPerformed( ActionEvent e )
            {
                JOptionPane.showMessageDialog( null, mController.getProperty( "objectProperties" ), "Object Properties",
                        JOptionPane.PLAIN_MESSAGE );
            }
        }, KeyEvent.CHAR_UNDEFINED, 0 );
        modelPopupMenu.add( menuItem );

        // -------------------------------------------- create the menubar

        JPopupMenu.setDefaultLightWeightPopupEnabled( false );
        ToolTipManager ttm = ToolTipManager.sharedInstance();
        ttm.setLightWeightPopupEnabled( false );

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar( menuBar );

        // ----------------------------------------- File menu

        final ActionListener saveAsAction = new ControllerFileAction( new FileDialog( this ), false, "save", "vZome", mController )
        {
            // this happens at the very end, after choose, save, set type
			@Override
            protected void openApplication( File file )
            {
                mFile = file;
                String newTitle = file.getAbsolutePath();
                appui.recordRecentDoc( newTitle );
                appui.documentFrameRenamed( getTitle(), newTitle, DocumentFrame.this );
            }
        };
        this.saveAction = new ActionListener()
        {
			@Override
            public void actionPerformed( ActionEvent e )
            {
                if ( mFile == null )
                    saveAsAction.actionPerformed( e );
                else {
                    mController .doFileAction( "save", mFile );
                }
            }
        };
        final ActionListener saveDefault = new ActionListener()
        {
			@Override
            public void actionPerformed( ActionEvent e )
            {
                // this is basically "save a copy...", with no choosing
                String fieldName = mController.getProperty( "field.name" );
                File prototype = new File( Platform.getPreferencesFolder(), "Prototypes/" + fieldName + ".vZome" );
                mController.doFileAction( "save", prototype );
            }
        };

        JMenu menu = new JMenu( "File" );

        if ( this .fullPower )
        {
            JMenu submenu = new JMenu( "New Model..." );
            submenu.add( doCreateMenuItem( "Zome (Golden Field)", "new", true, mController, KeyEvent.VK_N, 0 ) );
            submenu.add( createMenuItem( "\u221A2 Field", "new-rootTwo", mController ) );
            submenu.add( createMenuItem( "\u221A3 Field", "new-rootThree", mController ) );
            if ( "true" .equals( controller .getProperty( "enable.heptagon.field" ) ) )
                submenu.add( createMenuItem( "Heptagon Field", "new-heptagon", mController ) );
            if ( "true" .equals( controller .getProperty( "enable.snub.dodec.field" ) ) )
                submenu.add( createMenuItem( "Snub Dodec Field", "new-snubDodec", mController ) );
            menu.add( submenu );
        }
        else
        {
            menu.add( doCreateMenuItem( "New Model...", "new", isEditor || readerPreview, mController, KeyEvent.VK_N, 0 ) );
        }
        menu.add( createMenuItem( "Open...", new ControllerFileAction( new FileDialog( this ), true, "open", "vZome", mController ),
                KeyEvent.VK_O ) );
        menu.add( createMenuItem( "Open URL...", new ActionListener()
        {
			@Override
            public void actionPerformed( ActionEvent e )
            {
                String url = JOptionPane.showInputDialog( null, "Enter the URL for an online .vZome file.", "Open URL",
                        JOptionPane.PLAIN_MESSAGE );
                try {
                    mController .doAction( "openURL", new ActionEvent( DocumentFrame.this, ActionEvent.ACTION_PERFORMED, url ) );
                } catch ( Exception ex )
                {
                    ex .printStackTrace();
                    // TODO better error report
                    errors .reportError( Controller.USER_ERROR_CODE, new Object[]{ ex } );
                }
            }
        } ) );
        menu.add( createEditorMenuItem( "Open As New Model...", new ControllerFileAction( new FileDialog( this ), true, "newFromTemplate",
                "vZome", mController ) ) );
        if ( developerExtras )
            menu.add( createEditorMenuItem( "Open Deferred...", new ControllerFileAction( new FileDialog( this ), true, "openDeferringRedo",
                    "vZome", mController ) ) );
        menu.addSeparator();
        menu.add( createMenuItem( "Close", new ActionListener()
        {
			@Override
            public void actionPerformed( ActionEvent arg0 )
            {
                closeWindow();
            }
        }, KeyEvent.VK_W ) );
        menuItem = createMenuItem( "Save...", this.saveAction, KeyEvent.VK_S );
        menu .add( menuItem );
        menuItem .setEnabled( canSave );
        menuItem = createMenuItem( "Save As...", saveAsAction );
        menu .add( menuItem );
        menuItem .setEnabled( canSave );
        menuItem = createMenuItem( "Save Default", saveDefault );
        menu .add( menuItem );
        menuItem .setEnabled( canSave );

        menu.addSeparator();

        JMenu submenu = new JMenu( "Import 3D..." );
        import3dSubmenu = submenu;
//        import3dSubmenu .add( createMenuItem( "Zomod",
//                        new ControllerFileAction( new FileDialog( this ), true, "import.zomod", "zomod", mController ) ) );
        import3dSubmenu.add( createMenuItem( "4D VEF projection", new ControllerFileAction( new FileDialog( this ), true, "import.vef", "vef",
                mController ) ) );
        menu.add( import3dSubmenu );
        import3dSubmenu .setEnabled( fullPower );

        submenu = new JMenu( "Export Faithful 3D..." );
        submenu .add( createMenuItem( "POV-Ray", new ControllerFileAction( new FileDialog( this ), false, "export.pov", "pov", mController ) ) );
        submenu .add( createMenuItem( "WebGL JSON", new ControllerFileAction( new FileDialog( this ), false, "export.json", "json", mController ) ) );
        submenu .add( createMenuItem( "VRML", new ControllerFileAction( new FileDialog( this ), false, "export.vrml", "wrl", mController ) ) );
        if ( developerExtras )
        {
            submenu .addSeparator();
            submenu.add( createMenuItem( "OpenGL", new ControllerFileAction( new FileDialog( this ), false, "export.opengl", "m", mController ) ) );
            submenu.add( createMenuItem( "LiveGraphics3D", new ControllerFileAction( new FileDialog( this ), false, "export.LiveGraphics", "m", mController ) ) );
            submenu.add( createMenuItem( "vZome history detail", new ControllerFileAction( new FileDialog( this ), false, "export.history", "history", mController ) ) );
        }
        menu.add( submenu );
        submenu .setEnabled( fullPower && canSave );

        submenu = new JMenu( "Export Abstract 3D..." );
        submenu .add( createMenuItem( "STEP", new ControllerFileAction( new FileDialog( this ), false, "export.step", "step", mController ) ) );
        submenu .add( createMenuItem( "VEF", new ControllerFileAction( new FileDialog( this ), false, "export.vef", "vef", mController ) ) );
        submenu .add( createMenuItem( "OFF", new ControllerFileAction( new FileDialog( this ), false, "export.off", "off", mController ) ) );
        submenu .add( createMenuItem( "StL", new ControllerFileAction( new FileDialog( this ), false, "export.stl", "StL", mController ) ) );
        submenu .add( createMenuItem( "AutoCAD DXF", new ControllerFileAction( new FileDialog( this ), false, "export.dxf", "dxf", mController ) ) );
        if ( controller .userHasEntitlement( "export.pdb" ) )
        {
            submenu .add( createMenuItem( "PDB", new ControllerFileAction( new FileDialog( this ), false, "export.pdb", "pdb", mController ) ) );
        }
        if ( controller .userHasEntitlement( "export.seg" ) )
        {
            submenu .add( createMenuItem( "Mark Stock .seg", new ControllerFileAction( new FileDialog( this ), false, "export.seg", "seg", mController ) ) );
        }
        if ( controller .userHasEntitlement( "export.partslist" ) )
        {
            submenu.add( createMenuItem( "bill of materials", new ControllerFileAction( new FileDialog( this ), false, "export.partslist", "txt", mController ) ) );
        }
        if ( developerExtras )
        {
            submenu .addSeparator();
            submenu.add( createMenuItem( "COLLADA digital asset exchange", new ControllerFileAction( new FileDialog( this ), false, "export.dae", "dae", mController ) ) );
            submenu.add( createMenuItem( "Second Life", new ControllerFileAction( new FileDialog( this ), false, "export.2life", "2life", mController ) ) );
            submenu.add( createMenuItem( "Maximum XYZ", new ControllerFileAction( new FileDialog( this ), false, "export.size", "txt", mController ) ) );
            submenu.add( createMenuItem( "vZome part geometry", new ControllerFileAction( new FileDialog( this ), false, "export.partgeom", "vef", mController ) ) );
        }
        menu.add( submenu );
        submenu .setEnabled( fullPower && canSave );

        menu.addSeparator();

        if ( controller .userHasEntitlement( "export.zomespace" ) )
        {
            submenu = new JMenu( "Export Article..." );
            submenu .add( createMenuItem( "Zomespace", new ControllerFileAction( new FileDialog( this ), false, "export.zomespace", "zip", mController ) ) );
            menu.add( submenu );
            submenu .setEnabled( fullPower && canSave );
        }

        submenu = new JMenu( "Capture Image..." );
        submenu .add( createMenuItem( "BMP", new ControllerFileAction( new FileDialog( this ), false, "capture.bmp", "bmp", mController ) ) );
        submenu .add( createMenuItem( "JPEG", new ControllerFileAction( new FileDialog( this ), false, "capture.jpg", "jpg", mController ) ) );
        submenu .add( createMenuItem( "PNG", new ControllerFileAction( new FileDialog( this ), false, "capture.png", "png", mController ) ) );
        submenu .add( createMenuItem( "TIFF", new ControllerFileAction( new FileDialog( this ), false, "capture.tiff", "tiff", mController ) ) );
        menu.add( submenu );

        // menu .add( createMenuItem( "Capture Image Sequence...",
        // new CaptureImageSequenceAction( mFileChooser, mVPM, mMainPanel ) ) );

        menu.add( createEditorMenuItem( "Capture PDF or SVG...", new ActionListener()
        {
			@Override
            public void actionPerformed( ActionEvent e )
            {
                if ( snapshot2dFrame == null ) {
                    snapshot2dFrame = new Snapshot2dFrame( mController.getSubController( "snapshot.2d" ), new FileDialog( DocumentFrame.this ) );
                }
                snapshot2dFrame.setPanelSize( mMonocularPanel .getSize() );
                snapshot2dFrame.pack();
                if ( ! snapshot2dFrame .isVisible() )
                    snapshot2dFrame.repaint();
                snapshot2dFrame.setVisible( true );
            }
        } ) );

        menu.addSeparator();
        menu.add( doCreateMenuItem( "Quit", "quit", true, mController, KeyEvent.VK_Q, 0 ) );

        menuBar.add( menu );

        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Edit menu

        menu = new JMenu( "Edit" );
        menu.add( createEditorMenuItem( "Undo", getExclusiveAction( "undo" ), KeyEvent.VK_Z ) );
        menu.add( createEditorMenuItem( "Redo", getExclusiveAction( "redo" ), KeyEvent.VK_Y ) );
        menu.add( createEditorMenuItem( "Undo All", getExclusiveAction( "undoAll" ), KeyEvent.VK_Z, InputEvent.ALT_MASK ) );
        menu.add( createEditorMenuItem( "Redo All", getExclusiveAction( "redoAll" ), KeyEvent.VK_Y, InputEvent.ALT_MASK ) );
        if ( developerExtras )
        {
            menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
            menu.add( createPowerMenuItem( "Undo To Breakpoint", getExclusiveAction( "undoToBreakpoint" ), KeyEvent.VK_B, InputEvent.SHIFT_MASK ) );
            menu.add( createPowerMenuItem( "Redo To Breakpoint", getExclusiveAction( "redoToBreakpoint" ), KeyEvent.VK_B, InputEvent.ALT_MASK ) );
            menu.add( createEditorMenuItem( "Set Breakpoint", getExclusiveAction( "setBreakpoint" ), KeyEvent.VK_B ) );
            menuItem = createPowerMenuItem( "Redo to Edit Number...", new ActionListener()
            {
				@Override
                public void actionPerformed( ActionEvent e )
                {
                    String number = JOptionPane.showInputDialog( null, "Enter the edit number.", "Set Edit Number",
                            JOptionPane.PLAIN_MESSAGE );
                    try {
                        mController .doAction( "redoUntilEdit." + number, new ActionEvent( DocumentFrame.this, ActionEvent.ACTION_PERFORMED, "redoUntilEdit." + number ) );
                    } catch ( Exception e1 ) {
                        errors .reportError( Controller.USER_ERROR_CODE, new Object[]{ e1 } );
                    }
                }
            } );
            menu .add(  menuItem );
        }
        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        menu .add( createEditorMenuItem( "Copy", getExclusiveAction( "copy" ), KeyEvent.VK_C ) );
        menu .add( createEditorMenuItem( "Paste", getExclusiveAction( "paste" ), KeyEvent.VK_V ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        menu .add( createEditorMenuItem( "Select All", getExclusiveAction( "selectAll" ), KeyEvent.VK_A ) );
        menu .add( createEditorMenuItem( "Select Neighbors", getExclusiveAction( "selectNeighbors" ), KeyEvent.VK_A, InputEvent.ALT_MASK ) );
        menu.add( createEditorMenuItem( "Invert Selection", getExclusiveAction( "invertSelection" ) ) );
        menu.add( createEditorMenuItem( "Deselect Balls", getExclusiveAction( "unselectBalls" ) ) );
        menu.add( createEditorMenuItem( "Deselect Struts", getExclusiveAction( "unselectStruts" ) ) );
        // menu .add( createMenuItem( "Select First Octant", getExclusiveAction( "test.pick.cube" ) ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        menu .add( createPowerMenuItem( "Group", getExclusiveAction( "group" ), KeyEvent.VK_G, 0 ) );
        menu .add( createPowerMenuItem( "Ungroup", getExclusiveAction( "ungroup" ), KeyEvent.VK_G, InputEvent.ALT_MASK ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        menuItem = createEditorMenuItem( "Hide", getExclusiveAction( "hideball" ) );
        menuItem .setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_H, InputEvent.CTRL_MASK ) );
        menu .add( menuItem );
        menuItem = createEditorMenuItem( "Show All Hidden", getExclusiveAction( "showHidden" ) );
        menuItem .setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_H, InputEvent.CTRL_MASK | InputEvent.ALT_MASK ) );
        menu .add( menuItem );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        setColorMenuItem = createEditorMenuItem( "Set Color...", new ActionListener()
        {
			@Override
            public void actionPerformed( ActionEvent e )
            {
                Color color = JColorChooser.showDialog( DocumentFrame.this, "Choose Object Color", null );
                if ( color == null )
                    return;
                String command = "setItemColor/" + Integer.toHexString( color.getRGB() & 0xffffff );
                mController .actionPerformed( new ActionEvent( e .getSource(), e.getID(), command ) );
            }
        } );
        menu .add( setColorMenuItem );

        menuBar .add( menu );

        // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Construct menu
        menu = new JMenu( "Construct" );

        menu.add( createEditorMenuItem( "Loop Balls", getExclusiveAction( "joinballs" ), KeyEvent.VK_J ) );
        menu.add( createEditorMenuItem( "Chain Balls", getExclusiveAction( "chainBalls" ), KeyEvent.VK_J, InputEvent.ALT_MASK ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        menu .add( createEditorMenuItem( "Panel", getExclusiveAction( "panel" ), KeyEvent.VK_P ) );
        menu .add( createEditorMenuItem( "Panel/Strut Vertices", getExclusiveAction( "showVertices" ) ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        menu.add( createPowerMenuItem( "Centroid", getExclusiveAction( "centroid" ) ) );
        menu.add( createEditorMenuItem( "Strut Midpoint", getExclusiveAction( "midpoint" ) ) );
        menu.add( createPowerMenuItem( "Line-Line Intersection", getExclusiveAction( "lineLineIntersect" ) ) );
        menu.add( createPowerMenuItem( "Line-Plane Intersection", getExclusiveAction( "linePlaneIntersect" ) ) );
        menu.add( createPowerMenuItem( "Cross Product", getExclusiveAction( "crossProduct" ) ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        menu.add( createEditorMenuItem( "Ball At Origin", getExclusiveAction( "ballAtOrigin" ) ) );
        menu.add( createEditorMenuItem( "Ball At Symmetry Center", getExclusiveAction( "ballAtSymmCenter" ) ) );

        menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
//        menu.add( createEditorMenuItem( "Affine Transform All", getExclusiveAction( "affineTransformAll" ) ) );
//        menuItem = createPowerMenuItem( "Conjugate", getExclusiveAction( "conjugate" ) );
        if ( metaModels ) {
        	menu .add(  createPowerMenuItem( "Meta-model", getExclusiveAction( "realizeMetaParts" ) ) );
        }
        if ( isGolden ) {
            menu.add( createEditorMenuItem( "\u03C4 Divide", getExclusiveAction( "tauDivide" ) ) );
            menu.add( createPowerMenuItem( "Affine Pentagon", getExclusiveAction( "affinePentagon" ) ) );
        } else if ( isHeptagon )
            menu.add( createPowerMenuItem( "1/\u03C3/\u03C1 Subdivisions", getExclusiveAction( "heptagonDivide" ) ) );

        if ( developerExtras ) {
            menu .addSeparator(); // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

            menu.add( createEditorMenuItem( "Assert Selection", getExclusiveAction( "assertSelection" ) ) );

//            menu.add( createEditorMenuItem( "6-Lattice", getExclusiveAction( "sixLattice" ) ) );
        }

        // TODO restore this somehow
//        try {
//            Properties softMenu = Preferences .getUserPropertiesFile(
//            "vZomeEditMenu.properties" );
//            if ( softMenu != null ) {
//                menu.addSeparator();
//                for ( Iterator keys = softMenu .keySet() .iterator(); keys
//                .hasNext(); ) {
//                    String actionName = (String) keys .next();
//                    String menuString = softMenu .getProperty( actionName );
//                    menu.add( createMenuItem( menuString, getExclusiveAction( actionName
//                    ) ) );
//                }
//            }
//        } catch ( Throwable t ) {
//            // !!! don't want to advertise this ability yet
//        }

        menuBar.add( menu );

        // ---- catch-all ActionListener for locally-handled actions

        final String initSystem = controller.getProperty( "symmetry" );
        ActionListener localActions = new ActionListener()
        {
            private String system = initSystem;

            private final Map<String, JDialog> shapesDialogs = new HashMap<>();

            private final Map<String, JDialog> directionsDialogs = new HashMap<>();

			@Override
            public void actionPerformed( ActionEvent e )
            {
                String cmd = e.getActionCommand();

                if ( cmd.startsWith( "setSymmetry." ) )
                {
                    system = cmd.substring( "setSymmetry.".length() );
                    controller.actionPerformed( e ); // TODO exclusive
                }
                else if ( cmd.equals( "usedOrbits" ) )
                {
                    controller .actionPerformed( e ); // TO DO exclusive
                }
                else if ( cmd.equals( "configureShapes" ) )
                {
                    JDialog shapesDialog = (JDialog) shapesDialogs.get( system );
                    if ( shapesDialog == null ) {
                        Controller subController = controller.getSubController( "symmetry." + system );
                        shapesDialog = new ShapesDialog( DocumentFrame.this, subController );
                        shapesDialogs.put( system, shapesDialog );
                    }
                    shapesDialog.setVisible( true );
                }
                else if ( cmd.equals( "configureDirections" ) )
                {
                    JDialog symmetryDialog = (JDialog) directionsDialogs.get( system );
                    if ( symmetryDialog == null ) {
                        Controller subController = controller.getSubController( "symmetry." + system );
                        symmetryDialog = new SymmetryDialog( DocumentFrame.this, subController );
                        directionsDialogs.put( system, symmetryDialog );
                    }
                    symmetryDialog.setVisible( true );
                }
                else if ( cmd.equals( "rZomeOrbits" ) || cmd.equals( "predefinedOrbits" )
                        || cmd.equals( "setAllDirections" ) ) {
                    Controller subController = controller.getSubController( "symmetry." + system );
                    subController.actionPerformed( e );
                }
                else if ( "showPolytopesDialog" .equals( cmd ) )
                {
                    polytopesDialog .setVisible( true );
                }
            }
        };

        // ----------------------------------------- Symmetry menu

        menu = new JMenu( "Tools" );
        
        menu.add( createEditorMenuItem( "Set Center", getExclusiveAction( "setSymmetryCenter" ) ) ); 
        menu.add( createEditorMenuItem( "Set Axis", getExclusiveAction( "setSymmetryAxis" ) ) ); 
        menu.addSeparator(); 
        
        showToolsMenuItem = createEditorMenuItem( "Show Tools Panel", new ActionListener(){

			@Override
            public void actionPerformed( ActionEvent e )
            {
                tabbedPane .setSelectedIndex( 1 );  // should be "tools" tab
            }
        } );
        showToolsMenuItem .setEnabled( fullPower );
        menu .add( showToolsMenuItem );
        menu .addSeparator();

        if ( isGolden ) {
            menu.add( createEditorMenuItem( "Icosahedral Symmetry", getExclusiveAction( "icosasymm-golden" ), KeyEvent.VK_I ) );
        } else if ( isSnubDodec )
            menu.add( createEditorMenuItem( "Icosahedral Symmetry", getExclusiveAction( "icosasymm-snubDodec" ), KeyEvent.VK_I ) );

        if ( developerExtras && isRootThree ) {
            menu.add( createEditorMenuItem( "Dodecagonal Symmetry", getExclusiveAction( "dodecagonsymm" ), KeyEvent.VK_D ) );
        }
        menu.add( createEditorMenuItem( "Cubic / Octahedral Symmetry", getExclusiveAction( "octasymm" ), KeyEvent.VK_C,
                InputEvent.ALT_MASK ) );
        menu .add( createEditorMenuItem( "Tetrahedral Symmetry", getExclusiveAction( "tetrasymm" ), KeyEvent.VK_T,
                InputEvent.ALT_MASK ) );
        menu.add( createEditorMenuItem( "Axial Symmetry", getExclusiveAction( "axialsymm" ), KeyEvent.VK_R ) );
        menu.add( createEditorMenuItem( "Point Reflection", getExclusiveAction( "pointsymm" ) ) );
        menu.add( createEditorMenuItem( "Mirror Reflection", getExclusiveAction( "mirrorsymm" ), KeyEvent.VK_M ) );
        menu.add( createEditorMenuItem( "Translate", getExclusiveAction( "translate" ), KeyEvent.VK_T ) );
        
        menu .addSeparator();
        menu .add( doCreateMenuItem( "Generate Polytope...", "showPolytopesDialog", fullPower, localActions, KeyEvent.VK_P, InputEvent.ALT_MASK ) );
        if ( enable4d ) {
            menu.add( createEditorMenuItem( "H_4 Symmetry", getExclusiveAction( "h4symmetry" ) ) );
            menu.add( createEditorMenuItem( "H_4 Rotations", getExclusiveAction( "h4rotations" ) ) );
            menu.add( createEditorMenuItem( "I,T Symmetry", getExclusiveAction( "IxTsymmetry" ) ) );
            menu.add( createEditorMenuItem( "T,T Symmetry", getExclusiveAction( "TxTsymmetry" ) ) );
        }

        menuBar.add( menu );

        // ----------------------------------------- Polytopes menu
        menu = new JMenu( "Polytopes" );
        menu .setEnabled( fullPower );
//        menuBar .add( menu );

        String zeros = "0000";

        if ( isGolden ) {
            submenu = new JMenu( "H_4 Uniform" );
            for ( int p = 0x1; p <= 0xF; p++ ) {
                String dynkin = Integer.toString( p, 2 );
                dynkin = zeros.substring( dynkin.length() ) + dynkin;
                submenu.add( createEditorMenuItem( dynkin + "   " + Integer.toString( p, 16 ), getExclusiveAction( "polytope_H4"
                        + dynkin ) ) );
            }
            menu.add( submenu );
            submenu .setEnabled( fullPower );

            submenu = new JMenu( "A_4 Uniform" );
            for ( int p = 0x1; p <= 0xF; p++ ) {
                String dynkin = Integer.toString( p, 2 );
                dynkin = zeros.substring( dynkin.length() ) + dynkin;
                submenu.add( createEditorMenuItem( dynkin + "   " + Integer.toString( p, 16 ), getExclusiveAction( "polytope_A4"
                        + dynkin ) ) );
            }
            menu.add( submenu );
            submenu .setEnabled( fullPower );
        }

        submenu = new JMenu( isGolden ? "B_4 Symmetric" : "B_4 Uniform" );
        for ( int p = 0x1; p <= 0xF; p++ ) {
            String dynkin = Integer.toString( p, 2 );
            dynkin = zeros.substring( dynkin.length() ) + dynkin;
            submenu
                    .add( createEditorMenuItem( dynkin + "   " + Integer.toString( p, 16 ), getExclusiveAction( "polytope_B4" + dynkin ) ) );
        }
        menu.add( submenu );
        submenu .setEnabled( fullPower );

        submenu = new JMenu( isGolden ? "F_4 Symmetric" : "F_4 Uniform" );
        for ( int p = 0x1; p <= 0xF; p++ ) {
            String dynkin = Integer.toString( p, 2 );
            dynkin = zeros.substring( dynkin.length() ) + dynkin;
            submenu
                    .add( createEditorMenuItem( dynkin + "   " + Integer.toString( p, 16 ), getExclusiveAction( "polytope_F4" + dynkin ) ) );
        }
        menu.add( submenu );
        submenu .setEnabled( fullPower );

        if ( developerExtras && isRootThree )
            menu.add( createEditorMenuItem( "Ghost Symmetric 24-cell", getExclusiveAction( "ghostsymm24cell" ) ) );
        if ( developerExtras && isGolden )
            menu.add( createEditorMenuItem( "van Oss 600-cell", getExclusiveAction( "vanOss600cell" ) ) );

        // ----------------------------------------- System menu

        menuItem = createMenuItem( "real Zome", "rZomeOrbits", localActions );
        directionPopupMenu.add( menuItem );

        menuItem = doCreateMenuItem( "predefined", "predefinedOrbits", fullPower, localActions, KeyEvent.CHAR_UNDEFINED, 0 );
        directionPopupMenu.add( menuItem );

        menuItem = doCreateMenuItem( "used in model", "usedOrbits", fullPower, localActions, KeyEvent.CHAR_UNDEFINED, 0 );
        directionPopupMenu.add( menuItem );

        menuItem = doCreateMenuItem( "all", "setAllDirections", fullPower, localActions, KeyEvent.CHAR_UNDEFINED, 0 );
        directionPopupMenu.add( menuItem );

        menuItem = createMenuItem( "configure...", "configureDirections", localActions );
        directionPopupMenu.add( menuItem );

        menu = new JMenu( "System" );
        ButtonGroup group = new ButtonGroup();
        JMenuItem rbMenuItem;
        if ( isGolden || isSnubDodec ) {
            rbMenuItem = (JMenuItem) setAction( new JRadioButtonMenuItem(), "Icosahedral System", "setSymmetry.icosahedral",
                    localActions );
            rbMenuItem .setSelected( "icosahedral".equals( initSystem ) );
            rbMenuItem .setEnabled( fullPower );
            group.add( rbMenuItem );
            menu.add( rbMenuItem );
        }

        rbMenuItem = (JMenuItem) setAction( new JRadioButtonMenuItem(), "Octahedral System", "setSymmetry.octahedral",
                localActions );
        rbMenuItem .setSelected( "octahedral".equals( initSystem ) );
        rbMenuItem .setEnabled( fullPower );
        group.add( rbMenuItem );
        menu.add( rbMenuItem );

        if ( isRootThree )
        {
            rbMenuItem = (JMenuItem) setAction( new JRadioButtonMenuItem(), "Dodecagon System", "setSymmetry.dodecagonal",
                    localActions );
            rbMenuItem .setSelected( "dodecagonal".equals( initSystem ) );
            rbMenuItem .setEnabled( fullPower );
            group.add( rbMenuItem );
            menu.add( rbMenuItem );
        }
        else if ( isRootTwo )
        {
            rbMenuItem = (JMenuItem) setAction( new JRadioButtonMenuItem(), "Synestructics System", "setSymmetry.synestructics",
                    localActions );
            rbMenuItem .setSelected( "synestructics".equals( initSystem ) );
            rbMenuItem .setEnabled( fullPower );
            group.add( rbMenuItem );
            menu.add( rbMenuItem );
        }

        menu.addSeparator();
        
        if ( developerExtras )
        {
            JMenuItem wfMenuItem = (JMenuItem) setAction( new JCheckBoxMenuItem(), "Wireframe", "toggleWireframe",
                    controller );
            boolean isWireframe = "true".equals( controller.getProperty( "wireframe" ) );
            wfMenuItem .setSelected( isWireframe );
            menu.add( wfMenuItem );
        }

        menu.add( createMenuItem( "Shapes...", "configureShapes", localActions ) );
        menu.add( createMenuItem( "Directions...", "configureDirections", localActions ) );

        JMenuItem cbMenuItem = (JMenuItem) setAction( new JCheckBoxMenuItem(), "Show Directions Graphically", "toggleOrbitViews",
                controller );
        boolean setting = "true".equals( controller.getProperty( "useGraphicalViews" ) );
        cbMenuItem .setSelected( setting );
        menu.add( cbMenuItem );
        cbMenuItem .setEnabled( fullPower );

        final JMenuItem showStrutScalesItem = (JMenuItem) setAction( new JCheckBoxMenuItem(), "Show Strut Scales", "toggleStrutScales",
                controller );
        setting = "true" .equals( controller.getProperty( "showStrutScales" ) );
        showStrutScalesItem .setSelected( setting );
        showStrutScalesItem .setEnabled( fullPower );
        controller .addPropertyListener( new PropertyChangeListener(){
			@Override
            public void propertyChange( PropertyChangeEvent chg )
            {
                if ( "showStrutScales" .equals( chg .getPropertyName() ) )
                    showStrutScalesItem .setSelected(((Boolean) chg .getNewValue()));
                if ( developerExtras && "current.edit.xml" .equals( chg .getPropertyName() ) )
                	DocumentFrame.this .statusText .setText( (String) chg .getNewValue() );
            }} );
        menu .add( showStrutScalesItem );

//        cbMenuItem = (JMenuItem) setAction( new JCheckBoxMenuItem(), "Show Panels One-sided", "toggleOneSidedPanels",
//                controller );
//        setting = "true".equals( controller.getProperty( "oneSidedPanels" ) );
//        cbMenuItem.setSelected( setting );
//        menu.add( cbMenuItem );

        cbMenuItem = (JMenuItem) setAction( new JCheckBoxMenuItem(), "Show Frame Labels", "toggleFrameLabels",
                controller );
        setting = "true".equals( controller.getProperty( "showFrameLabels" ) );
        cbMenuItem .setSelected( setting );
        menu.add( cbMenuItem );

        menuBar.add( menu );

        // ----------------------------------------- Scripting menu

        menu = new JMenu( "Scripting" );
        menu .setEnabled( fullPower );
        pythonMenuItem = createMenuItem( "Python...", new ActionListener()
        {
			@Override
            public void actionPerformed( ActionEvent e )
            {
                mPythonFrame.setVisible( true );
            }
        } );
        pythonMenuItem .setEnabled( fullPower );
        if ( developerExtras )
            menu .add( pythonMenuItem );
        zomicMenuItem = createMenuItem( "Zomic...", new ActionListener()
        {
			@Override
            public void actionPerformed( ActionEvent e )
            {
                mZomicFrame.setVisible( true );
            }
        } );
        zomicMenuItem .setEnabled( fullPower );
        menu .add( zomicMenuItem );
        menuBar .add( menu );

        menu = new JMenu( "Help" );
        if ( "G4G10" .equals( mController .getProperty( "licensed.user" ) ) )
            menu .add( createMenuItem( "Welcome G4G10 Participant...", "openResource-org/vorthmann/zome/content/welcomeG4G10.vZome", mController ) );            
        menu .add( createMenuItem( "Quick Start...", "openResource-org/vorthmann/zome/content/welcomeDodec.vZome", mController ) );
        menu .addSeparator(); 
        menu .add( createMenuItem( "About vZome...", "showAbout", mController ) );
        menuBar.add( menu );

        // --------------------------------------- Create the toolbar.

        AbstractButton button = null;

        button = makeEditButton( "joinballs", "Join", "Join two or more selected balls" );
        toolBar.add( button );
        button.setRolloverEnabled( true );

        if ( isGolden ) {
            button = makeEditButton( "icosasymm-golden", "Icosahedral Symm.", "Repeat selection with chiral icosahedral symmetry" );
            toolBar.add( button );
            button.setRolloverEnabled( true );
        } else if ( isSnubDodec ) {
            button = makeEditButton( "icosasymm-snubDodec", "Icosahedral Symm.", "Repeat selection with chiral icosahedral symmetry" );
            toolBar.add( button );
            button.setRolloverEnabled( true );
        } else {
            button = makeEditButton( "octasymm", "Octahedral Symm.", "Repeat selection with chiral octahedral symmetry" );
            toolBar.add( button );
            button.setRolloverEnabled( true );
        }

        button = makeEditButton( "tetrasymm", "Tetrahedral Symm.", "Repeat selection with chiral tetrahedral symmetry" );
        toolBar.add( button );
        button.setRolloverEnabled( true );

        button = makeEditButton( "axialsymm", "Axial Symmetry", "Repeat selection with symmetry around an axis" );
        toolBar.add( button );
        button.setRolloverEnabled( true );

        button = makeEditButton( "pointsymm", "Point Reflect", "Reflect selection through origin" );
        toolBar.add( button );
        button.setRolloverEnabled( true );

        button = makeEditButton( "mirrorsymm", "Mirror Symmetry", "Reflect selection through mirror" );
        toolBar.add( button );
        button.setRolloverEnabled( true );

        button = makeEditButton( "translate", "Translate", "Repeat selection translated along symmetry axis" );
        toolBar.add( button );
        button.setRolloverEnabled( true );

        if ( fullPower )
        {
            button = makeEditButton( "centroid", "Centroid", "Construct centroid of points" );
            toolBar.add( button );
            button.setRolloverEnabled( true );
        }

        button = makeEditButton( "hideball", "Hide", "Hide selected objects" );
        toolBar.add( button );
        button.setRolloverEnabled( true );

        button = makeEditButton( "panel", "Panel", "Make a panel polygon" );
        toolBar.add( button );
        button.setRolloverEnabled( true );

        this.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
        this.addWindowListener( new WindowAdapter()
        {
            public void windowClosing( WindowEvent e )
            {
                closeWindow();
            }
        } );

        this.pack();
        this.setVisible( true );
        this.setFocusable( true );
    }

    // TODO do this better... shouldn't need ref to mAppUI
    boolean closeWindow()
    {
        if ( "true".equals( mController.getProperty( "edited" ) )
        && ( isEditor && canSave ) ) {
            // TODO replace showConfirmDialog() with use of EscapeDialog, or something similar...
            //   see  http://java.sun.com/docs/books/tutorial/uiswing/components/dialog.html
            int response = JOptionPane.showConfirmDialog( DocumentFrame.this, "Do you want to save your changes?",
                    "file is changed", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE );
            
            if ( response == JOptionPane.CANCEL_OPTION )
                return false;
            if ( response == JOptionPane.YES_OPTION )
                try {
                    saveAction.actionPerformed( new ActionEvent( DocumentFrame.this, ActionEvent.ACTION_PERFORMED, "save" ) );
                    return false;
                } catch ( RuntimeException e ) {
                    logger.log( Level.WARNING, "did not save due to error", e );
                    return false;
                }
        }
        mAppUI.documentFrameClosed( DocumentFrame.this );
        dispose();
        return true;
    }

    private ExclusiveAction getExclusiveAction( final String action )
    {
        return new ExclusiveAction( getExcluder() )
        {
			@Override
            protected void doAction( ActionEvent e ) throws Exception
            {
                mController.doAction( action, e );
            }

			@Override
            protected void showError( Exception e )
            {
                JOptionPane.showMessageDialog( DocumentFrame.this, e.getMessage(), "Command failure", JOptionPane.ERROR_MESSAGE );
            }
        };
    }

    private JButton makeEditButton( String cmdName, final String altText, String toolTipText )
    {
        String imageName = cmdName;
        if ( imageName.endsWith( "-roottwo" ) )
            imageName = cmdName.substring( 0, cmdName.length() - 8 );
        else if ( imageName.endsWith( "-golden" ) )
            imageName = cmdName.substring( 0, cmdName.length() - 7 );
        return makeCommandButton( imageName, altText, toolTipText, getExclusiveAction( cmdName ) );
    }

    private AbstractButton makeMouseToolButton(
        String imageName, final String altText, String toolTipText, ItemListener listener )
    {
        // Look for the image.
        String imgLocation = "/icons/" + imageName + "_off.png";
        URL imageURL = getClass().getResource( imgLocation );

        // Create and initialize the button.
        final JToggleButton button = new JToggleButton();
        button.setText( altText );
        button.setVerticalTextPosition( SwingConstants.TOP );
        button.setHorizontalTextPosition( SwingConstants.CENTER );
        button.setToolTipText( toolTipText );
        button.addItemListener( listener );

        if ( imageURL != null ) {
            Icon icon = new ImageIcon( imageURL, altText );
            button.setIcon( icon );
            // the rest will only work if setRolloverEnabled(true) is called
            // after adding to the toolbar!
            // imageURL = getClass() .getResource( "/icons/" + imageName +
            // "_r.png" );
            // icon = new ImageIcon( imageURL, altText );
            // button .setRolloverIcon( icon );
            imageURL = getClass().getResource( "/icons/" + imageName + "_on_sel.png" );
            icon = new ImageIcon( imageURL, altText );
            button.setRolloverSelectedIcon( icon );
            imageURL = getClass().getResource( "/icons/" + imageName + "_off_sel.png" );
            icon = new ImageIcon( imageURL, altText );
            button.setSelectedIcon( icon );
        } else { // no image found
            button.setText( altText );
            System.err.println( "Resource not found: " + imgLocation );
        }

        return button;
    }

    private JButton makeCommandButton( String imageName, final String altText, String toolTipText, ActionListener actions )
    {
        boolean skipImages = mController.getProperty( "noButtonImages" ) != null;

        // Look for the image.
        String imgLocation = "/icons/" + imageName + "_off.png";
        URL imageURL = getClass().getResource( imgLocation );

        // Create and initialize the button.
        JButton button = new JButton();
        if ( skipImages )
            button.setText( altText );
        else if ( imageURL != null ) {
            Icon icon = new ImageIcon( imageURL, altText );
            button.setIcon( icon );
            // the rest will only work if setRolloverEnabled(true) is called
            // after adding to the toolbar!
            imageURL = getClass().getResource( "/icons/" + imageName + "_on.png" );
            icon = new ImageIcon( imageURL, altText );
            button.setRolloverIcon( icon );
            Dimension dim = new Dimension( 100, 63 );
            button .setPreferredSize( dim );
            button .setMaximumSize( dim );
        } else
            System.err.println( "Resource not found: " + imgLocation );
        button.setVerticalTextPosition( SwingConstants.TOP );
        button.setHorizontalTextPosition( SwingConstants.CENTER );
        button.addActionListener( actions );
        button.setToolTipText( toolTipText );

        getExcluder().addExcludable( button );

        return button;
    }

    private JMenuItem createPowerMenuItem( String buttonText, ExclusiveAction actionListener, int key, int modifiers )
    {
        return doCreateMenuItem( buttonText, null, fullPower, actionListener, key, modifiers );
    }

    private JMenuItem createEditorMenuItem( String buttonText, ExclusiveAction actionListener, int key )
    {
        return doCreateMenuItem( buttonText, null, isEditor, actionListener, key, 0 );
    }

    private JMenuItem createEditorMenuItem( String buttonText, ExclusiveAction actionListener, int key, int modifiers )
    {
        return doCreateMenuItem( buttonText, null, isEditor, actionListener, key, modifiers );
    }

    private JMenuItem createPowerMenuItem( String buttonText, ActionListener actionListener )
    {
        return doCreateMenuItem( buttonText, null, fullPower, actionListener, KeyEvent .CHAR_UNDEFINED, 0 );
    }

    private JMenuItem createEditorMenuItem( String buttonText, ActionListener actionListener )
    {
        return doCreateMenuItem( buttonText, null, isEditor, actionListener, KeyEvent .CHAR_UNDEFINED, 0 );
    }

    protected JMenuItem createMenuItem( String buttonText, ActionListener actionListener, int key )
    {
        return doCreateMenuItem( buttonText, null, true, actionListener, key, 0 );
    }

    protected JMenuItem createMenuItem( String buttonText, ActionListener actionListener )
    {
        return doCreateMenuItem( buttonText, null, true, actionListener, KeyEvent .CHAR_UNDEFINED, 0 );
    }

    protected JMenuItem createMenuItem( String buttonText, String actionCommand, ActionListener actionListener )
    {
        return doCreateMenuItem( buttonText, actionCommand, true, actionListener, KeyEvent .CHAR_UNDEFINED, 0 );
    }

    protected JMenuItem doCreateMenuItem
    (
        String buttonText,
        String actionCommand,
        boolean enable,
        ActionListener actionListener,
        int key,
        int modifiers
    )
    {
        JMenuItem menuItem = new JMenuItem( buttonText );
        menuItem .setEnabled( enable );
        if ( enable )
        {
            menuItem .addActionListener( actionListener );
            menuItem .setActionCommand( actionCommand );
            if ( actionListener instanceof ExclusiveAction )
                mExcluder.addExcludable( menuItem );
        }
        if ( key != KeyEvent .CHAR_UNDEFINED )
            menuItem.setAccelerator( KeyStroke.getKeyStroke( key, Platform.getKeyModifierMask() | modifiers ) );
        return menuItem;
    }

    protected AbstractButton setAction( AbstractButton control, String buttonText, String actionCommand, ActionListener listener )
    {
        control.setText( buttonText );
        control.addActionListener( listener );
        control.setActionCommand( actionCommand );

        if ( listener instanceof ExclusiveAction )
            mExcluder.addExcludable( control );

        return control;
    }

	@Override
    public void propertyChange( PropertyChangeEvent e )
    {
        if ( "command.status" .equals( e .getPropertyName() ) )
        {
            statusText .setText( (String) e .getNewValue() );
        }
        else if ( "editor.mode" .equals( e .getPropertyName() ) )
        {
            switchToMode( (String) e .getNewValue() );
        }
        else if ( "has.pages" .equals( e .getPropertyName() ) )
        {
            if ( articleButton != null )
            {
                boolean enable = e .getNewValue() .toString() .equals( "true" );
                articleButton .setEnabled( enable );
                if ( ! enable )
                    modelButton .doClick();
            }
        }
    }
}
