package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.vorthmann.j3d.J3dComponentFactory;
import org.vorthmann.ui.Controller;

public class ModelPanel extends JPanel implements PropertyChangeListener
{
    private final Component monocularCanvas, leftEyeCanvas, rightEyeCanvas;
    private MouseListener monocularClicks, leftEyeClicks, rightEyeClicks;
    private final JToolBar oldToolBar, firstToolbar, secondToolbar, bookmarkBar;
    private final boolean isEditor;
	private final Controller controller, view;
	private final JPanel mMonocularPanel;
	private final ToolConfigDialog toolConfigDialog;
	private Map<String, AbstractButton> toolCreationButtons = new HashMap<String, AbstractButton>();

	public ModelPanel( Controller controller, ControlActions enabler, boolean isEditor, boolean fullPower )
	{
		super( new BorderLayout() );
		this .controller = controller;
        this .view = controller .getSubController( "viewPlatform" );
        this .isEditor = isEditor;
        
        Controller monoController = controller .getSubController( "monocularPicking" );
        Controller leftController = controller .getSubController( "leftEyePicking" );
        Controller rightController = controller .getSubController( "rightEyePicking" );
        
        controller .addPropertyListener( this );

        JPanel monoStereoPlusToolbar = new JPanel();
        monoStereoPlusToolbar .setLayout( new BorderLayout() );
        this .add( monoStereoPlusToolbar, BorderLayout.CENTER );

        JPanel monoStereoPanel = new JPanel();
        monoStereoPlusToolbar .add( monoStereoPanel, BorderLayout.CENTER );
		CardLayout monoStereoCardLayout = new CardLayout();
		monoStereoPanel .setLayout( monoStereoCardLayout );
        boolean showStereo =  "true" .equals( view .getProperty( "stereo" ) );

        mMonocularPanel = new JPanel( new BorderLayout() );
        {
            mMonocularPanel .setPreferredSize( new Dimension( 2000, 2000 ) );
            monocularCanvas = ( (J3dComponentFactory) controller ) .createJ3dComponent( "mainViewer-monocular" );
            mMonocularPanel .add( monocularCanvas, BorderLayout.CENTER );
        }
        monoStereoPanel .add( mMonocularPanel, "mono" );
        JPanel stereoPanel = new JPanel();
        {
            GridLayout grid = new GridLayout( 1, 2 );
            stereoPanel .setLayout( grid );
            leftEyeCanvas = ( (J3dComponentFactory) controller ) .createJ3dComponent( "mainViewer-leftEye" );
            stereoPanel .add( leftEyeCanvas );
            rightEyeCanvas = ( (J3dComponentFactory) controller ) .createJ3dComponent( "mainViewer-rightEye" );
            stereoPanel .add( rightEyeCanvas );
        }
        monoStereoPanel .add( stereoPanel, "stereo" );
        if ( showStereo )
            monoStereoCardLayout .show( monoStereoPanel, "stereo" );
        else
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

        this .toolConfigDialog = new ToolConfigDialog( (JFrame) this.getParent() );

        if ( isEditor )
        {
            if ( ! controller .propertyIsTrue( "no.toolbar" ) )
            {
            	// -------------------- Create the dynamic toolbar
            	
                boolean hasOldToolBar = controller .propertyIsTrue( "original.tools" );

                this .firstToolbar = new JToolBar();
                this .firstToolbar .setFloatable( false );
                this .firstToolbar .setOrientation( JToolBar.HORIZONTAL );
//                this .firstToolbar .setToolTipText( "Click on objects to select them, and enable creation of new tools accordingly." );
                this .add( firstToolbar, BorderLayout .NORTH );

                this .bookmarkBar = new JToolBar();
                this .bookmarkBar .setFloatable( false );
                this .bookmarkBar .setOrientation( JToolBar.VERTICAL );
                this .bookmarkBar .setToolTipText( "Selection bookmarks" );
                monoStereoPlusToolbar .add( bookmarkBar, BorderLayout .LINE_START );
                
                AbstractButton button;

                if ( controller .propertyIsTrue( "supports.symmetry.icosahedral" ) ) {
                    button = newToolButton( enabler, "icosahedral", "Create an icosahedral symmetry tool",
                    		"<p>" +
                            		"Each tool produces up to 59 copies of the<br>" +
                            		"input selection, using the rotation<br>" +
                            		"symmetries of an icosahedron.  To create a tool,<br>" +
                            		"select a single ball that defines the center<br>" +
                            		"of symmetry.<br><br>" +
                            		"Combine with a point reflection tool to achieve<br>" +
                            		"all 120 symmetries of the icosahedron,<br>" +
                            		"including reflections.<br>" +
                    		"</p>" );
                    firstToolbar .add( button );
                	button = newToolButton( enabler, "octahedral", "Create an octahedral symmetry tool",
                    		"<p>" +
                            		"Each tool is defined by a single ball,<br>" +
                            		"defining the center of symmetry, and<br>" +
                            		"a single blue or green strut, defining<br>" +
                            		"one of five possible orientations for<br>" +
                            		"the symmetry." +
                    		"</p>" );
                    firstToolbar .add( button );
                	button = newToolButton( enabler, "tetrahedral", "Create a tetrahedral symmetry tool",
                    		"<p>" +
                            		"Each tool is defined by a single ball,<br>" +
                            		"defining the center of symmetry, and<br>" +
                            		"a single blue or green strut, defining<br>" +
                            		"one of five possible orientations for<br>" +
                            		"the symmetry." +
                    		"</p>" );
                    firstToolbar .add( button );
                } else {
                	button = newToolButton( enabler, "octahedral", "Create an octahedral symmetry tool",
                    		"<p>" +
                            		"Each tool is defined by a single<br>" +
                            		"ball, defining the center of symmetry.<br>" +
                    		"</p>" );
                    firstToolbar .add( button );
                	button = newToolButton( enabler, "tetrahedral", "Create a tetrahedral symmetry tool",
                    		"<p>" +
                            		"Each tool is defined by a single<br>" +
                            		"ball, defining the center of symmetry.<br>" +
                    		"</p>" );
                    firstToolbar .add( button );
                }
                button = newToolButton( enabler, "point reflection", "Create a point reflection tool",
                		"<p>" +
                        		"Each tool is defined by a single<br>" +
                        		"ball, defining the center of symmetry.<br>" +
                		"</p>" );
                firstToolbar .add( button );
                button = newToolButton( enabler, "mirror", "Create a mirror reflection tool", "" );
                firstToolbar .add( button );
                button = newToolButton( enabler, "scaling", "Create a scaling tool", "" );
                firstToolbar .add( button );
                button = newToolButton( enabler, "rotation", "Create a rotation tool", "" );
                firstToolbar .add( button );
                button = newToolButton( enabler, "axial symmetry", "Create a rotational symmetry tool", "" );
                firstToolbar .add( button );
                button = newToolButton( enabler, "translation", "Create a translation tool", "" );
                firstToolbar .add( button );
                button = newToolButton( enabler, "linear map", "Create a linear map tool", "" );
                firstToolbar .add( button );
                button = newToolButton( enabler, "axialstretch", "Create a yellow stretch tool" );
                firstToolbar .add( button );
                button = newToolButton( enabler, "axialsquish", "Create a yellow squash tool" );
                firstToolbar .add( button );
                button = newToolButton( enabler, "redstretch", "Create a red stretch tool" );
                firstToolbar .add( button );
                button = newToolButton( enabler, "redsquash", "Create a red squash tool" );
                firstToolbar .add( button );

                this .secondToolbar = new JToolBar();
                this .secondToolbar .setFloatable( false );
                this .secondToolbar .setOrientation( JToolBar.HORIZONTAL );
//                this .secondToolbar .setToolTipText( "All commands and tools apply to the currently selected objects." );
                monoStereoPlusToolbar .add( secondToolbar, BorderLayout .NORTH );

                button = makeEditButton2( "Create a selection bookmark", "/icons/tools/newTool/bookmark.png" );
        		button = enabler .setButtonAction( "addBookmark", button );
        		this .toolCreationButtons .put( "bookmark", button );
        		boolean on = this .controller .propertyIsTrue( "tool.enabled.bookmark" );
        		button .setEnabled( on );
                bookmarkBar .add( button );
                bookmarkBar .addSeparator();

                button = makeEditButton( enabler, "delete", "Delete selected objects" );
                secondToolbar .add( button );
                button = makeEditButton( enabler, "hideball", "Hide selected objects" );
                secondToolbar .add( button );
                
                secondToolbar .addSeparator();

                button = makeEditButton( enabler, "joinballs", "Connect balls in a loop" );
                secondToolbar .add( button );
                button = makeEditButton( enabler, "chainBalls", "Connect balls in a chain" );
                secondToolbar .add( button );
                button = makeEditButton( enabler, "joinBallsAllToLast", "Connect all balls to last selected" );
                secondToolbar .add( button );
                button = makeEditButton( enabler, "joinBallsAllPossible", "Connect balls in all possible ways" );
                secondToolbar .add( button );
                button = makeEditButton( enabler, "panel", "Make a panel polygon" );
                secondToolbar .add( button );
                button = makeEditButton( enabler, "centroid", "Construct centroid of points" );
                secondToolbar .add( button );
                
                secondToolbar .addSeparator();

                final Controller toolsController = controller .getSubController( "tools" );
            	
            	toolsController .addPropertyListener( new PropertyChangeListener()
            	{
					@Override
					public void propertyChange( PropertyChangeEvent evt )
					{
						switch ( evt .getPropertyName() ) {

						case "tool.separator":
		            		secondToolbar .addSeparator();
							break;

						case "tool.added":
				            if ( evt .getOldValue() == null )
				            {
				            	Controller controller = (Controller) evt .getNewValue();
				                String kind = controller .getProperty( "kind" );
				                String name = controller .getProperty( "label" );
				                String iconPath = "/icons/tools/small/" + kind + ".png";
				                String tooltip = "<html><b>" + name + "</b><br><br><p>Right-click to configure this tool.</p></html>";
				                JButton button = makeEditButton2( tooltip, iconPath );
				        		button .setActionCommand( "apply" );
				        		button .addActionListener( controller );
				        		if ( kind .equals( "bookmark" ) )
				        			bookmarkBar .add( button );
				        		else {
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
				        			            toolConfigDialog .showTool( button, controller );
				        			        }
				        			    }
									} );
				        			secondToolbar .add( button );
				        		}
				            }
							break;

						default:
							break;
						}
				    }
				});
                
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
            	this .secondToolbar = null;
            	this .firstToolbar = null;
            	this .bookmarkBar = null;
            }

            monocularClicks = new ContextualMenuMouseListener( monoController , new PickerContextualMenu( monoController, enabler, "monocular" ) );
            leftEyeClicks = new ContextualMenuMouseListener( leftController , new PickerContextualMenu( leftController, enabler, "leftEye" ) );
            rightEyeClicks = new ContextualMenuMouseListener( rightController , new PickerContextualMenu( rightController, enabler, "rightEye" ) );
            monocularCanvas .addMouseListener( monocularClicks );
            leftEyeCanvas .addMouseListener( leftEyeClicks );
            rightEyeCanvas .addMouseListener( rightEyeClicks );
        }
        else {
        	this .oldToolBar = null;
        	this .secondToolbar = null;
        	this .firstToolbar = null;
        	this .bookmarkBar = null;
        }
	}
	
	private AbstractButton newToolButton( ControlActions enabler, String group, String title, String helpHtml )
	{
		String iconPath = "/icons/tools/newTool/" + group + ".png";
		String html = "<html><img src=\"" + ModelPanel.class.getResource( iconPath ) + "\">&nbsp;&nbsp;<b>" + title
					+ "</b><br><br>" + helpHtml + "</html>";
		final JButton button = makeEditButton2( html, iconPath );
		enabler .setButtonAction( "addTool-" + group, button );
		this .toolCreationButtons .put( group, button );
		boolean on = this .controller .propertyIsTrue( "tool.enabled." + group );
		button .setEnabled( on );
		return button;
	}
	
	private AbstractButton makeEditButton( ControlActions enabler, String command, String tooltip )
	{
		AbstractButton button = makeEditButton2( tooltip, "/icons/tools/small/" + command + ".png" );
		button = enabler .setButtonAction( command, button );
		return button;
	}
	
	private AbstractButton makeLegacyEditButton( ControlActions enabler, String command, String tooltip )
	{
		String imageName = command;
		if ( imageName .endsWith( "-roottwo" ) )
			imageName = command .substring( 0, command.length() - 8 );
		else if ( imageName .endsWith( "-golden" ) )
			imageName = command .substring( 0, command.length() - 7 );
		AbstractButton button = makeEditButton2( tooltip, "/icons/" + imageName + "_on.png" );
		button = enabler .setButtonAction( command, button );
		Dimension dim = new Dimension( 100, 63 );
		button .setPreferredSize( dim );
		button .setMaximumSize( dim );
		return button;
	}
	
	private JButton makeEditButton2( String tooltip, String imgLocation )
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
        } else
        	System.err.println( "Resource not found: " + imgLocation );

        button .setVerticalTextPosition( SwingConstants.TOP );
        button .setHorizontalTextPosition( SwingConstants.CENTER );
        button .setToolTipText( tooltip );
		return button;
	}

	@Override
	public void propertyChange( PropertyChangeEvent e )
	{
		switch ( e .getPropertyName() ) {
		
		case "tools.enabled":
			// selection changed, update button states
			for ( Entry<String, AbstractButton> entry : this .toolCreationButtons .entrySet() ) {
				boolean on = this .controller .propertyIsTrue( "tool.enabled." + entry .getKey() );
				entry .getValue() .setEnabled( on );
			}
			break;

		case "editor.mode":
	        if ( isEditor )
	        {
	            if ( "article" .equals( e .getNewValue() ) ) {
	            	if ( this .oldToolBar != null )
	            		this .oldToolBar .setVisible( false );
	                monocularCanvas .removeMouseListener( monocularClicks );
	                leftEyeCanvas .removeMouseListener( leftEyeClicks );
	                rightEyeCanvas .removeMouseListener( rightEyeClicks );
	            }
	            else if ( ! "true" .equals( this .controller .getProperty( "no.toolbar" ) ) ) {
	            	if ( this .oldToolBar != null )
	            		this .oldToolBar .setVisible( true );
	                monocularCanvas .addMouseListener( monocularClicks );
	                leftEyeCanvas .addMouseListener( leftEyeClicks );
	                rightEyeCanvas .addMouseListener( rightEyeClicks );
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

			this .add( enabler .setMenuAction( "setBackgroundColor", new JMenuItem( "Set Background Color..." ) ) );

			this .addSeparator();

			this .add( setMenuAction( "setBuildOrbitAndLength", new JMenuItem( "Build With This" ) ) );
			this .add( enabler .setMenuAction( "showProperties-"+key, new JMenuItem( "Show Properties" ) ) );
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
