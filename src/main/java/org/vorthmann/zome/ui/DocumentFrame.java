package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
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
import javax.swing.ToolTipManager;

import org.vorthmann.j3d.J3dComponentFactory;
import org.vorthmann.j3d.Platform;
import org.vorthmann.ui.Controller;
import org.vorthmann.ui.ExclusiveAction;

import com.vzome.desktop.controller.ViewPlatformControlPanel;

public class DocumentFrame extends JFrame implements PropertyChangeListener, ControlActions
{
    private static final long serialVersionUID = 1L;

    protected final Controller mController, toolsController;

    private final ModelPanel modelPanel;
            
    private final JTabbedPane tabbedPane = new JTabbedPane();
    
    private final ExclusiveAction.Excluder mExcluder = new ExclusiveAction.Excluder( this );

    private boolean isEditor, fullPower, readerPreview, canSave;

    private CardLayout modelArticleCardLayout;

    private static final Logger logger = Logger.getLogger( "org.vorthmann.zome.ui" );

    private LessonPanel lessonPanel;

    private JButton snapshotButton;

    private JRadioButton articleButton, modelButton;

    private JPanel viewControl, modelArticleEditPanel;

    private JLabel statusText;

    private Controller viewPlatform;

    private Controller lessonController;
    
    private JDialog polytopesDialog;

	private final boolean developerExtras;
	
	private final ActionListener localActions;
	
	private File mFile = null;
	
	private final Controller.ErrorChannel errors;

    private Snapshot2dFrame snapshot2dFrame;

	private ControllerFileAction saveAsAction;
    
    public ExclusiveAction.Excluder getExcluder()
    {
        return mExcluder;
    }
        
    public DocumentFrame( File file, final Controller controller, PropertyChangeListener app )
    {
        mController = controller;
        controller .addPropertyListener( this );
        toolsController = mController .getSubController( "tools" );

        // TODO: compute these booleans once here, and don't recompute in DocumentMenuBar

        this.readerPreview = controller .propertyIsTrue( "reader.preview" );

        this.isEditor = controller .userHasEntitlement( "model.edit" ) && ! readerPreview;

        this.canSave = controller .userHasEntitlement( "save.files" );

        this.fullPower = isEditor ;//&& controller .userHasEntitlement( "all.tools" );
        
        this.developerExtras = fullPower && mController .userHasEntitlement( "developer.extras" );
        
        JFrame mZomicFrame = new JFrame( "Zomic Scripting" );
        mZomicFrame.setContentPane( new ZomicEditorPanel( mZomicFrame, controller ) );
        mZomicFrame.pack();
        
        JFrame mPythonFrame = new JFrame( "Python Scripting" );
        mPythonFrame .setContentPane( new PythonConsolePanel( mPythonFrame, controller ) );
        mPythonFrame .pack();
        
        polytopesDialog = new PolytopesDialog( this, controller .getSubController( "polytopes" ) );

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

		saveAsAction = new ControllerFileAction( new FileDialog( this ), false, "save", "vZome", mController )
        {
            // this happens at the very end, after choose, save, set type
    		@Override
            protected void openApplication( File file )
            {
                mFile = file;
                String newTitle = file.getAbsolutePath();
                app .propertyChange( new PropertyChangeEvent( DocumentFrame.this, "window.title", getTitle(), newTitle ) );
            }
        };

        // ---- catch-all ActionListener for locally-handled actions

        final String initSystem = controller .getProperty( "symmetry" );
        localActions = new ActionListener()
        {
            private String system = initSystem;

            private final Map<String, JDialog> shapesDialogs = new HashMap<>();

            private final Map<String, JDialog> directionsDialogs = new HashMap<>();

			@Override
            public void actionPerformed( ActionEvent e )
            {
				Controller delegate = controller;
                String cmd = e.getActionCommand();
                switch ( cmd ) {
                
            	case "close":
            		closeWindow();
            		break;

            	case "openURL":
                    String url = JOptionPane .showInputDialog( DocumentFrame.this, "Enter the URL for an online .vZome file.", "Open URL",
                            JOptionPane.PLAIN_MESSAGE );
                    try {
                        mController .doAction( cmd, new ActionEvent( DocumentFrame.this, ActionEvent.ACTION_PERFORMED, url ) );
                    } catch ( Exception ex )
                    {
                        ex .printStackTrace();
                        // TODO better error report
                        errors .reportError( Controller.USER_ERROR_CODE, new Object[]{ ex } );
                    }
                    break;
                    
            	case "save":
                    if ( mFile == null )
                        saveAsAction .actionPerformed( e );
                    else {
                        mController .doFileAction( "save", mFile );
                    }
                    break;

            	case "saveDefault":
                    // this is basically "save a copy...", with no choosing
                    String fieldName = mController.getProperty( "field.name" );
                    File prototype = new File( Platform.getPreferencesFolder(), "Prototypes/" + fieldName + ".vZome" );
                    mController .doFileAction( "save", prototype );
                    break;
                    
            	case "snapshot.2d":
                    if ( snapshot2dFrame == null ) {
                        snapshot2dFrame = new Snapshot2dFrame( mController.getSubController( "snapshot.2d" ), new FileDialog( DocumentFrame.this ) );
                    }
                    snapshot2dFrame.setPanelSize( modelPanel .getRenderedSize() );
                    snapshot2dFrame.pack();
                    if ( ! snapshot2dFrame .isVisible() )
                        snapshot2dFrame.repaint();
                    snapshot2dFrame.setVisible( true );
                    break;

            	case "redoUntilEdit":
                    String number = JOptionPane.showInputDialog( null, "Enter the edit number.", "Set Edit Number",
                            JOptionPane.PLAIN_MESSAGE );
                    try {
                        mController .doAction( "redoUntilEdit." + number, new ActionEvent( DocumentFrame.this, ActionEvent.ACTION_PERFORMED, "redoUntilEdit." + number ) );
                    } catch ( Exception e1 ) {
                        errors .reportError( Controller.USER_ERROR_CODE, new Object[]{ e1 } );
                    }
                    break;

            	case "showToolsPanel":
                    tabbedPane .setSelectedIndex( 1 );  // should be "tools" tab
                    break;

            	case "showPolytopesDialog":
                    polytopesDialog .setVisible( true );
                	break;
                
                case "showPythonWindow":
                	mPythonFrame .setVisible( true );
                	break;
                
                case "showZomicWindow":
                	mZomicFrame .setVisible( true );
                	break;
                
            	case "setItemColor":
                    Color color = JColorChooser.showDialog( DocumentFrame.this, "Choose Object Color", null );
                    if ( color == null )
                        return;
                    int rgb = color .getRGB() & 0xffffff;
                    int alpha = color .getAlpha() & 0xff;
                    String command = "setItemColor/" + Integer.toHexString( ( rgb << 8 ) | alpha );
                    mController .actionPerformed( new ActionEvent( e .getSource(), e.getID(), command ) );
                    break;
                    
                case "setBackgroundColor":
                    color = JColorChooser.showDialog( DocumentFrame.this, "Choose Background Color", null );
                    if ( color != null )
                    	mController .setProperty( "backgroundColor", Integer.toHexString( color.getRGB() & 0xffffff ) );
                	break;
                
                case "usedOrbits":
                	mController .actionPerformed( e ); // TO DO exclusive
                	break;
                
                case "rZomeOrbits":
                case "predefinedOrbits":
                case "setAllDirections":
                	delegate = mController .getSubController( "symmetry." + system );
                	delegate .actionPerformed( e );
                	break;
                
                case "configureShapes":
                    JDialog shapesDialog = (JDialog) shapesDialogs.get( system );
                    if ( shapesDialog == null ) {
                    	delegate = mController .getSubController( "symmetry." + system );
                        shapesDialog = new ShapesDialog( DocumentFrame.this, delegate );
                        shapesDialogs .put( system, shapesDialog );
                    }
                    shapesDialog .setVisible( true );
                	break;
                
                case "configureDirections":
                    JDialog symmetryDialog = (JDialog) directionsDialogs.get( system );
                    if ( symmetryDialog == null ) {
                    	delegate = mController .getSubController( "symmetry." + system );
                        symmetryDialog = new SymmetryDialog( DocumentFrame.this, delegate );
                        directionsDialogs .put( system, symmetryDialog );
                    }
                    symmetryDialog .setVisible( true );

                default:
                    if ( cmd .startsWith( "setSymmetry." ) )
                    {
                        system = cmd .substring( "setSymmetry.".length() );
                        mController .actionPerformed( e ); // TODO exclusive
                    }
                    else if ( cmd .startsWith( "showProperties-" ) )
                    {
            			String key = cmd .substring( "showProperties-" .length() );
                    	Controller subc = mController .getSubController( key + "Picking" );
                        JOptionPane .showMessageDialog( DocumentFrame.this, subc .getProperty( "objectProperties" ), "Object Properties",
                                JOptionPane.PLAIN_MESSAGE );
                    }
                    break;
                }
            }
        };

        // -------------------------------------- create panels and tools

        viewPlatform = controller .getSubController( "viewPlatform" );
        lessonController = controller .getSubController( "lesson" );
        lessonController .addPropertyListener( this );

        // Now the component containment hierarchy
        
        JPanel outerPanel = new JPanel( new BorderLayout() );
        setContentPane( outerPanel );
        {
            JPanel leftCenterPanel = new JPanel( new BorderLayout() );
            {
                if ( this .isEditor )
                {
                    JPanel modeAndStatusPanel = new JPanel( new BorderLayout() );
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

                modelPanel = new ModelPanel( controller, this, this .isEditor, fullPower );
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
                Component trackballCanvas = ( (J3dComponentFactory) controller ) .createJ3dComponent( "controlViewer" );
                viewControl = new ViewPlatformControlPanel( trackballCanvas, viewPlatform );
                // this is probably moot for reader mode
                rightPanel .add( viewControl, BorderLayout.PAGE_START );
                
                modelArticleEditPanel = new JPanel();
                modelArticleCardLayout = new CardLayout();
                modelArticleEditPanel .setLayout( modelArticleCardLayout );
                if ( this .isEditor )
                {
                    JPanel buildPanel = new StrutBuilderPanel( DocumentFrame.this, controller .getProperty( "symmetry" ), controller, this );
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

        JPopupMenu.setDefaultLightWeightPopupEnabled( false );
        ToolTipManager ttm = ToolTipManager.sharedInstance();
        ttm .setLightWeightPopupEnabled( false );

        this .setJMenuBar( new DocumentMenuBar( controller, this ) );

        this.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
        this.addWindowListener( new WindowAdapter()
        {
            public void windowClosing( WindowEvent we )
            {
                closeWindow();
                mController .actionPerformed( new ActionEvent( DocumentFrame.this, ActionEvent.ACTION_PERFORMED, "documentClosed" ));
            }
        } );

        this.pack();
        this.setVisible( true );
        this.setFocusable( true );
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
    
    public AbstractButton setButtonAction( String command, AbstractButton control )
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

    	default:
        	if ( command .startsWith( "export." ) ) {
        		enable = this .canSave;
        	}
    	}
    	control .setEnabled( enable );
        if ( control .isEnabled() )
        {
        	ActionListener actionListener = this .mController;
        	switch ( command ) {
            
    		// these can fall through to the ApplicationUI
        	case "quit":
        	case "new":
        	case "new-rootTwo":
        	case "new-rootThree":
        	case "new-heptagon":
        	case "new-snubDodec":
        	case "showAbout":

        	case "toggleWireframe":
        	case "toggleOrbitViews":
        	case "toggleStrutScales":
        	case "toggleFrameLabels":
        	case "toggleOutlines":
        		actionListener = this .mController;
        		break;

			case "open":
        	case "newFromTemplate":
        	case "openDeferringRedo":
        		actionListener = new ControllerFileAction( new FileDialog( this ), true, command, "vZome", mController );
        		break;
                
        	case "import.vef":
        		actionListener = new ControllerFileAction( new FileDialog( this ), true, command, "vef", mController );
        		break;
                
        	case "saveAs":
        		actionListener = saveAsAction;
        		break;

        	case "save":
        	case "saveDefault":
        	case "openURL":
        	case "close":
        	case "snapshot.2d":
        	case "showToolsPanel":
        	case "setItemColor":
        	case "setBackgroundColor":
        	case "showPolytopesDialog":
        	case "showZomicWindow":
        	case "showPythonWindow":
        	case "rZomeOrbits":
        	case "predefinedOrbits":
        	case "usedOrbits":
        	case "setAllDirections":
        	case "configureShapes":
        	case "configureDirections":
        	case "redoUntilEdit":
        		actionListener = this .localActions;
                break;
                                                
        	case "capture-animation":
        		actionListener = new ControllerFileAction( new FileDialog( this ), false, command, "png", mController );
                break;
                                                
        	default:
        		if ( command .startsWith( "openResource-" ) ) {
        			actionListener = this .mController;
        		}
        		else if ( command .startsWith( "setSymmetry." ) ) {
        			actionListener = this .localActions;
        		}
        		else if ( command .startsWith( "showProperties-" ) ) {
        			actionListener = this .localActions;
        		}
        		else if ( command .startsWith( "capture." ) ) {
        			String ext = command .substring( "capture." .length() );
            		actionListener = new ControllerFileAction( new FileDialog( this ), false, command, ext, mController );
        		}
        		else if ( command .startsWith( "export." ) ) {
        			String ext = command .substring( "export." .length() );
        			switch ( ext ) {
        			case "vrml": ext = "wrl"; break;
        			case "size": ext = "txt"; break;
        			case "partslist": ext = "txt"; break;
        			case "partgeom": ext = "vef"; break;
					default:
						break;
					}
            		actionListener = new ControllerFileAction( new FileDialog( this ), false, command, ext, mController );
        		}
        		else {
            		actionListener = getExclusiveAction( command );
                    this .mExcluder .addExcludable( control );
        		}
                break;
        	}
        	control .addActionListener( actionListener );
        }
    	return control;
    }
    
    public JMenuItem setMenuAction( String command, JMenuItem menuItem )
    {
    	return (JMenuItem) this .setButtonAction( command, menuItem );
    }

	@Override
    public void propertyChange( PropertyChangeEvent e )
    {
		switch ( e .getPropertyName() ) {

		case "command.status":
			statusText .setText( (String) e .getNewValue() );
			break;

		case "current.edit.xml":
			if ( this .developerExtras )
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
            if ( articleButton != null )
            {
                boolean enable = e .getNewValue() .toString() .equals( "true" );
                articleButton .setEnabled( enable );
                if ( ! enable )
                    modelButton .doClick();
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

    		if ( response == JOptionPane.CANCEL_OPTION )
    			return false;
    		if ( response == JOptionPane.YES_OPTION )
    			try {
    				localActions .actionPerformed( new ActionEvent( DocumentFrame.this, ActionEvent.ACTION_PERFORMED, "save" ) );
    				return false;
    			} catch ( RuntimeException re ) {
    				logger.log( Level.WARNING, "did not save due to error", re );
    				return false;
    			}
    	}
    	dispose();
    	mController .actionPerformed( new ActionEvent( DocumentFrame.this, ActionEvent.ACTION_PERFORMED, "documentClosed" ) );
    	return true;
    }

	public void makeUnnamed()
	{
		this .mFile = null;
	}
}
