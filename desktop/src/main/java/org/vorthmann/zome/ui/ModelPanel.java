package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.vorthmann.ui.CardPanel;
import org.vorthmann.ui.Controller;

import com.vzome.core.render.RenderingChanges;
import com.vzome.desktop.controller.Controller3d;
import com.vzome.desktop.controller.RenderingViewer;

public class ModelPanel extends JPanel implements PropertyChangeListener, SymmetryToolbarsPanel.ButtonFactory
{
	private static final String TOOLTIP_PREFIX = "<html><b>";
	private static final String TOOLTIP_SUFFIX = "</b><br><br><p>Right-click to configure this tool.</p></html>";

	private final Component monocularCanvas; //, leftEyeCanvas, rightEyeCanvas;
    private MouseListener monocularClicks; //, leftEyeClicks, rightEyeClicks;
    private final JToolBar oldToolBar, bookmarkBar;
    private final JScrollPane bookmarkScroller;
    private final boolean isEditor;
	private final Controller controller, view;
	private final JPanel mMonocularPanel;
	private int nextBookmarkIcon = 0;
	private final CardPanel toolbarCards;
	private final Collection<SymmetryToolbarsPanel> toolBarPanels = new ArrayList<SymmetryToolbarsPanel>();
	private final ToolConfigDialog bookmarkConfigDialog;

	public ModelPanel( Controller3d controller, RenderingViewer.Factory factory, ControlActions enabler, boolean isEditor, boolean fullPower )
	{
		super( new BorderLayout() );
		this .controller = controller;
        this .view = controller .getSubController( "viewPlatform" );
        this .isEditor = isEditor;

        this .bookmarkConfigDialog = new ToolConfigDialog( (JFrame) this.getParent(), true );

//        Controller leftController = controller .getSubController( "leftEyePicking" );
//        Controller rightController = controller .getSubController( "rightEyePicking" );
        
        controller .addPropertyListener( this );

        JPanel monoStereoPlusToolbar = new JPanel();
        monoStereoPlusToolbar .setLayout( new BorderLayout() );
        this .add( monoStereoPlusToolbar, BorderLayout.CENTER );

        JPanel monoStereoPanel = new JPanel();
        monoStereoPlusToolbar .add( monoStereoPanel, BorderLayout.CENTER );
		CardLayout monoStereoCardLayout = new CardLayout();
		monoStereoPanel .setLayout( monoStereoCardLayout );
//        boolean showStereo = "true" .equals( view .getProperty( "stereo" ) );

        mMonocularPanel = new JPanel( new BorderLayout() );
        
        mMonocularPanel .setPreferredSize( new Dimension( 2000, 2000 ) );
        monocularCanvas = factory .createJ3dComponent( "mainViewer-monocular" );
        RenderingChanges scene = factory .createRenderingChanges( true, controller );
        RenderingViewer viewer = factory .createRenderingViewer( scene, this .monocularCanvas );
        controller .attachViewer( viewer, scene, this .monocularCanvas, "mainViewer-monocular" );
        // attachViewer must precede this getSubController
        Controller monoController = controller .getSubController( "monocularPicking" );
        mMonocularPanel .add( monocularCanvas, BorderLayout.CENTER );
        
        monoStereoPanel .add( mMonocularPanel, "mono" );
//        JPanel stereoPanel = new JPanel();
//        {
//            GridLayout grid = new GridLayout( 1, 2 );
//            stereoPanel .setLayout( grid );
//            leftEyeCanvas = ( (J3dComponentFactory) controller ) .createJ3dComponent( "mainViewer-leftEye" );
//            stereoPanel .add( leftEyeCanvas );
//            rightEyeCanvas = ( (J3dComponentFactory) controller ) .createJ3dComponent( "mainViewer-rightEye" );
//            stereoPanel .add( rightEyeCanvas );
//        }
//        monoStereoPanel .add( stereoPanel, "stereo" );
//        if ( showStereo )
//            monoStereoCardLayout .show( monoStereoPanel, "stereo" );
//        else
            monoStereoCardLayout .show( monoStereoPanel, "mono" );
        view .addPropertyListener( new PropertyChangeListener()
        {
        	@Override
        	public void propertyChange( PropertyChangeEvent chg )
        	{
        		if ( "stereo" .equals( chg .getPropertyName() ) )
        			if ( ((Boolean) chg .getNewValue()) )
        				monoStereoCardLayout .show( monoStereoPanel, "stereo" );
        			else
        				monoStereoCardLayout .show( monoStereoPanel, "mono" );
        	}
        } );

        if ( isEditor )
        {
            if ( ! controller .propertyIsTrue( "no.toolbar" ) )
            {
                final Controller toolsController = controller .getSubController( "tools" );

                toolbarCards = new CardPanel();
                for ( String symmetrySystem : controller .getCommandList( "symmetryPerspectives" ) ) {
                    Controller symmController = controller .getSubController( "symmetry." + symmetrySystem );
                    SymmetryToolbarsPanel panel = new SymmetryToolbarsPanel( symmController, toolsController, enabler, this );
                    this .toolBarPanels .add( panel );
                    this .toolbarCards .add( symmetrySystem, panel );
				}
                this .toolbarCards .showCard( controller .getProperty( "symmetry" ) );
                monoStereoPlusToolbar .add( this .toolbarCards, BorderLayout .NORTH );

                this .bookmarkBar = new JToolBar();
                this .bookmarkBar .setFloatable( false );
                this .bookmarkBar .setOrientation( JToolBar.VERTICAL );
                this .bookmarkBar .setToolTipText( "Selection bookmarks" );
                this .bookmarkScroller = new JScrollPane( this .bookmarkBar, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
                this .bookmarkScroller .setBorder( null );
                monoStereoPlusToolbar .add( this .bookmarkScroller, BorderLayout .LINE_START );

                AbstractButton button = newBookmarkButton( controller .getSubController( "bookmark" ) );
                bookmarkBar .add( button );
                bookmarkBar .addSeparator();

                addBookmark( toolsController .getSubController( "bookmark.builtin/ball at origin" ) );

            	toolsController .addPropertyListener( new PropertyChangeListener()
            	{
					@Override
					public void propertyChange( PropertyChangeEvent evt )
					{
						switch ( evt .getPropertyName() ) {

						case "tool.added":
				            if ( evt .getOldValue() == null )
				            {
				            	Controller controller = (Controller) evt .getNewValue();
				                String kind = controller .getProperty( "kind" );
				        		if ( kind .equals( "bookmark" ) )
				        			addBookmark( controller );
				        		else {
				        			for (SymmetryToolbarsPanel panel : toolBarPanels ) {
					        			panel .addTool( controller );
									}
				        		}
				            }
							break;

						default:
							break;
						}
				    }
				});
                
                boolean hasOldToolBar = controller .propertyIsTrue( "original.tools" );
            	if ( hasOldToolBar ) {
                    // --------------------------------------- Create the fixed toolbar.

                    this .oldToolBar = new JToolBar( "vZome Toolbar" );
                    this .oldToolBar .setOrientation( JToolBar.VERTICAL );
                	this .add( oldToolBar, BorderLayout .LINE_END );

                    button = makeLegacyEditButton( enabler, "joinballs", "Join two or more selected balls" );
                    oldToolBar.add( button );
                    button.setRolloverEnabled( true );

                    String fieldName = controller.getProperty( "field.name" );
                    if ( "golden" .equals( fieldName ) ) {
                        button = makeLegacyEditButton( enabler, "icosasymm-golden", "Repeat selection with chiral icosahedral symmetry" );
                        oldToolBar.add( button );
                        button.setRolloverEnabled( true );
                    } else if ( "snubDodec" .equals( fieldName ) ) {
                        button = makeLegacyEditButton( enabler, "icosasymm-snubDodec", "Repeat selection with chiral icosahedral symmetry" );
                        oldToolBar.add( button );
                        button.setRolloverEnabled( true );
                    } else {
                        button = makeLegacyEditButton( enabler, "octasymm", "Repeat selection with chiral octahedral symmetry" );
                        oldToolBar.add( button );
                        button.setRolloverEnabled( true );
                    }

               		button = makeLegacyEditButton( enabler, "tetrasymm", "Repeat selection with chiral tetrahedral symmetry" );
                    oldToolBar.add( button );
                    button.setRolloverEnabled( true );

                    button = makeLegacyEditButton( enabler, "axialsymm", "Repeat selection with symmetry around an axis" );
                    oldToolBar.add( button );
                    button.setRolloverEnabled( true );

                    button = makeLegacyEditButton( enabler, "pointsymm", "Reflect selection through origin" );
                    oldToolBar.add( button );
                    button.setRolloverEnabled( true );

                    button = makeLegacyEditButton( enabler, "mirrorsymm", "Reflect selection through mirror" );
                    oldToolBar.add( button );
                    button.setRolloverEnabled( true );

                    button = makeLegacyEditButton( enabler, "translate", "Repeat selection translated along symmetry axis" );
                    oldToolBar.add( button );
                    button.setRolloverEnabled( true );

                    if ( fullPower )
                    {
                        button = makeLegacyEditButton( enabler, "centroid", "Construct centroid of points" );
                        oldToolBar.add( button );
                        button.setRolloverEnabled( true );
                    }

                    button = makeLegacyEditButton( enabler, "hideball", "Hide selected objects" );
                    oldToolBar.add( button );
                    button.setRolloverEnabled( true );

                    button = makeLegacyEditButton( enabler, "panel", "Make a panel polygon" );
                    oldToolBar.add( button );
                    button.setRolloverEnabled( true );
            	}
            	else
                	this .oldToolBar = null;
            }
            else {
            	this .oldToolBar = null;
            	this .bookmarkBar = null;
            	this .bookmarkScroller = null;
            	this .toolbarCards = null;
            }

            monocularClicks = new ContextualMenuMouseListener( monoController , new PickerContextualMenu( monoController, enabler, "monocular" ) );
            monocularCanvas .addMouseListener( monocularClicks );
//            leftEyeClicks = new ContextualMenuMouseListener( leftController , new PickerContextualMenu( leftController, enabler, "leftEye" ) );
//            rightEyeClicks = new ContextualMenuMouseListener( rightController , new PickerContextualMenu( rightController, enabler, "rightEye" ) );
//            leftEyeCanvas .addMouseListener( leftEyeClicks );
//            rightEyeCanvas .addMouseListener( rightEyeClicks );
        }
        else {
        	this .oldToolBar = null;
        	this .bookmarkBar = null;
        	this .bookmarkScroller = null;
        	this .toolbarCards = null;
        }
	}
		
	private void addBookmark( Controller controller )
	{
		String name = controller .getProperty( "label" );
		int icon = this .nextBookmarkIcon;
		this .nextBookmarkIcon = ( icon + 1 ) % 4;
		String iconPath = "/icons/tools/small/bookmark_" + icon + ".png";
		String tooltip = TOOLTIP_PREFIX + name + TOOLTIP_SUFFIX;
		JButton button = makeIconButton( tooltip, iconPath );
		button .setActionCommand( "apply" );
		button .addActionListener( controller );
        button .addMouseListener( new MouseAdapter()
        {
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
                    bookmarkConfigDialog .showTool( button, controller );
                }
            }
        } );
		tooltip = "<html><b>" + name + "</b></html>";
		button .setToolTipText( tooltip );
        controller .addPropertyListener( new PropertyChangeListener()
        {
            @Override
            public void propertyChange( PropertyChangeEvent evt )
            {
                if ( "label" .equals( evt .getPropertyName() ) )
                {
                    String label = (String) evt .getNewValue();
                    String tooltip = TOOLTIP_PREFIX + label + TOOLTIP_SUFFIX;
                    button .setToolTipText( tooltip );
                }
            }
        });
		bookmarkBar .add( button );
	}
	
	private AbstractButton newBookmarkButton( Controller buttonController )
	{
		String iconPath = "/icons/tools/newTool/bookmark.png";
		String html = "<html><img src=\"" + ModelPanel.class.getResource( iconPath ) + "\">&nbsp;&nbsp;<b>" + "Create a selection bookmark"
					+ "</b><br><br>A selection bookmark lets you re-create<br>any selection at a later time.</html>";
		final JButton button = makeIconButton( html, iconPath );
		button .setActionCommand( "createTool" );
		button .addActionListener( buttonController );
		button .setEnabled( buttonController != null && buttonController .propertyIsTrue( "enabled" ) );
		if ( buttonController != null )
			buttonController .addPropertyListener( new PropertyChangeListener()
			{
				@Override
				public void propertyChange( PropertyChangeEvent evt )
				{
					switch ( evt .getPropertyName() ) {

					case "enabled":
						button .setEnabled( (Boolean) evt .getNewValue() );
						break;
					}
				}
			});
		else
			System .out .println( "no controller for tool Factory: bookmark" );
		return button;
	}
	
	private AbstractButton makeLegacyEditButton( ControlActions enabler, String command, String tooltip )
	{
		String imageName = command;
		if ( imageName .endsWith( "-roottwo" ) )
			imageName = command .substring( 0, command.length() - 8 );
		else if ( imageName .endsWith( "-golden" ) )
			imageName = command .substring( 0, command.length() - 7 );
		AbstractButton button = makeIconButton( tooltip, "/icons/" + imageName + "_on.png" );
		button = enabler .setButtonAction( command, this .controller, button );
		Dimension dim = new Dimension( 100, 63 );
		button .setPreferredSize( dim );
		button .setMaximumSize( dim );
		return button;
	}
	
	public JButton makeIconButton( String tooltip, String imgLocation )
	{
        // Create and initialize the button.
		JButton button = new JButton();
        URL imageURL = getClass() .getResource( imgLocation );
        if ( imageURL != null ) {
        	Icon icon = new ImageIcon( imageURL, tooltip );
        	button .setIcon( icon );

        	Dimension dim = new Dimension( icon .getIconWidth()+1, icon .getIconHeight()+1 );
        	button .setPreferredSize( dim );
        	button .setMaximumSize( dim );

        	// the rest will only work if setRolloverEnabled(true) is called
        	// after adding to the toolbar!
        	//        		imageURL = getClass() .getResource( "/icons/" + imageName + "_on.png" );
        	//        		icon = new ImageIcon( imageURL, label );
        	//        		button .setRolloverIcon( icon );
        } else {
        	Dimension dim = new Dimension( 37, 31 );
        	button .setPreferredSize( dim );
        	button .setMaximumSize( dim );
        	System.err.println( "Resource not found: " + imgLocation );
        }

        button .setVerticalTextPosition( SwingConstants.TOP );
        button .setHorizontalTextPosition( SwingConstants.CENTER );
        button .setToolTipText( tooltip );
		return button;
	}

	@Override
	public void propertyChange( PropertyChangeEvent e )
	{
		switch ( e .getPropertyName() ) {
		
		case "symmetry":
			String symmName = (String) e .getNewValue();
            this .toolbarCards .showCard( symmName );
			break;
			
		case "editor.mode":
	        if ( isEditor )
	        {
	            if ( "article" .equals( e .getNewValue() ) ) {
	            	if ( this .oldToolBar != null )
	            		this .oldToolBar .setVisible( false );
	            	this .toolbarCards .setVisible( false );
	            	this .bookmarkScroller .setVisible( false );
	                monocularCanvas .removeMouseListener( monocularClicks );
//	                leftEyeCanvas .removeMouseListener( leftEyeClicks );
//	                rightEyeCanvas .removeMouseListener( rightEyeClicks );
	            }
	            else if ( ! "true" .equals( this .controller .getProperty( "no.toolbar" ) ) ) {
	            	if ( this .oldToolBar != null )
	            		this .oldToolBar .setVisible( true );
	            	this .toolbarCards .setVisible( true );
	            	this .bookmarkScroller .setVisible( true );
	                monocularCanvas .addMouseListener( monocularClicks );
//	                leftEyeCanvas .addMouseListener( leftEyeClicks );
//	                rightEyeCanvas .addMouseListener( rightEyeClicks );
	            }
	        }
			break;

		default:
			break;
		}
	}

	public Dimension getRenderedSize()
	{
		return this .mMonocularPanel .getSize();
	}
	
	private static class PickerContextualMenu extends ContextualMenu
	{
		private final Controller controller;
		
		public PickerContextualMenu( Controller controller, ControlActions enabler, String key )
		{
			super();
			this .controller = controller;
			boolean oldTools = controller .propertyIsTrue( "original.tools" );
            this .setLightWeightPopupEnabled( false );

			this .add( setMenuAction( "copyThisView", new JMenuItem( "Copy This View" ) ) );
			this .add( setMenuAction( "useCopiedView", new JMenuItem( "Use Copied View" ) ) );

			this .addSeparator();

			this .add( setMenuAction( "lookAtBall", new JMenuItem( "Look At This" ) ) );
			this .add( setMenuAction( "lookAtOrigin", new JMenuItem( "Look At Origin" ) ) );

			if ( oldTools ) {
				this .add( setMenuAction( "lookAtSymmetryCenter", new JMenuItem( "Look At Symmetry Center" ) ) );
				this .addSeparator();
				this .add( setMenuAction( "setSymmetryCenter", new JMenuItem( "Set Symmetry Center" ) ) );
				this .add( setMenuAction( "setSymmetryAxis", new JMenuItem( "Set Symmetry Axis" ) ) );
			}

			this .addSeparator();

			this .add( setMenuAction( "setWorkingPlane", new JMenuItem( "Set Working Plane" ) ) );
			this .add( setMenuAction( "setWorkingPlaneAxis", new JMenuItem( "Set Working Plane Axis" ) ) );

			this .addSeparator();

			this .add( setMenuAction( "selectCollinear", new JMenuItem( "Select Collinear" ) ) );
			this .add( setMenuAction( "selectParallelStruts", new JMenuItem( "Select Parallel Struts" ) ) );
			this .add( setMenuAction( "selectSimilarSize", new JMenuItem( "Select Similar Struts" ) ) );

			this .add( setMenuAction( "undoToManifestation", new JMenuItem( "Undo Including This" ) ) );

			this .addSeparator();

			this .add( enabler .setMenuAction( "setBackgroundColor", this .controller, new JMenuItem( "Set Background Color..." ) ) );

			this .addSeparator();

			this .add( setMenuAction( "setBuildOrbitAndLength", new JMenuItem( "Build With This" ) ) );
			this .add( enabler .setMenuAction( "showProperties-"+key, this .controller, new JMenuItem( "Show Properties" ) ) );
		}
		
		private JMenuItem setMenuAction( String action, JMenuItem control )
		{
			control .setEnabled( true );
			control .setActionCommand( action );
        	control .addActionListener( this .controller );
			return control;
		}
	}
}
