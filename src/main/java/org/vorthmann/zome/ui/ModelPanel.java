package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
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
    private final JToolBar oldToolBar, staticToolBar, dynamicToolBar;
    private final boolean isEditor;
	private final Controller controller, view;
	private final JPanel mMonocularPanel;
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

        JPanel monoStereoPanel = new JPanel();
        this .add( monoStereoPanel, BorderLayout.CENTER );
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


        if ( isEditor )
        {
            if ( ! controller .propertyIsTrue( "no.toolbar" ) )
            {
            	// -------------------- Create the dynamic toolbar
            	
                String toolbarLoc = controller .getProperty( "toolbar.position" );
                boolean hasOldToolBar = controller .propertyIsTrue( "original.tools" );
                if ( toolbarLoc == null )
                	toolbarLoc = BorderLayout .NORTH;
                else if ( toolbarLoc == BorderLayout .NORTH )
                	toolbarLoc = BorderLayout .LINE_END;
                else
                	toolbarLoc = BorderLayout .NORTH;

                this .dynamicToolBar = new JToolBar();
                this .dynamicToolBar .setOrientation( JToolBar.HORIZONTAL );
                this .add( dynamicToolBar, toolbarLoc );

                staticToolBar = new JToolBar();
                staticToolBar .setOrientation( JToolBar.VERTICAL );
                AbstractButton button = newToolButton( enabler, "icosahedral", "CreateTool", "Create icosahedral symmetry tool" );
                staticToolBar .add( button );
                button = newToolButton( enabler, "octahedral", "CreateTool", "Create octahedral symmetry tool" );
                staticToolBar .add( button );
                button = newToolButton( enabler, "tetrahedral", "CreateTool", "Create tetrahedral symmetry tool" );
                staticToolBar .add( button );
                button = newToolButton( enabler, "point reflection", "CreateTool", "Create point reflection tool" );
                staticToolBar .add( button );
                button = newToolButton( enabler, "mirror", "CreateTool", "Create mirror symmetry tool" );
                staticToolBar .add( button );
                button = newToolButton( enabler, "scaling", "CreateTool", "Create scaling tool" );
                staticToolBar .add( button );
                button = newToolButton( enabler, "rotation", "CreateTool", "Create rotation tool" );
                staticToolBar .add( button );
                staticToolBar .addSeparator();
                this .add( staticToolBar, BorderLayout .LINE_START );
                
                final Controller toolsController = controller .getSubController( "tools" );
            	
            	toolsController .addPropertyListener( new PropertyChangeListener()
            	{
					@Override
					public void propertyChange( PropertyChangeEvent evt )
					{
				        if ( evt .getPropertyName() .equals( "tool.instances" ) )
				        {
				            if ( evt .getOldValue() == null )
				            {
				                String idAndName = (String) evt .getNewValue(); // will be "group.N/label"
				                int delim = idAndName .indexOf( "." );
				                String group = idAndName .substring( 0, delim );
				                delim = idAndName .indexOf( "/" );
				                String name = idAndName .substring( delim + 1 );
				                String iconPath = "/icons/tools/small/" + group + ".png";
				                AbstractButton button = makeEditButton2( enabler, name, name, iconPath );
				        		button .setActionCommand( idAndName );
				        		button .addActionListener( toolsController );
				                dynamicToolBar .add( button );
				                //scroller .revalidate();
				            }
				        }
				    }
				});
                
                // --------------------------------------- Create the fixed toolbar.

                this .oldToolBar = new JToolBar( "vZome Toolbar" );
                this .oldToolBar .setOrientation( JToolBar.VERTICAL );
                toolbarLoc = controller .getProperty( "toolbar.position" );
                if ( toolbarLoc == null )
                	toolbarLoc = BorderLayout .LINE_END;

                button = makeEditButton( enabler, "joinballs", "Join", "Join two or more selected balls" );
                oldToolBar.add( button );
                button.setRolloverEnabled( true );

                String fieldName = controller.getProperty( "field.name" );
                if ( "golden" .equals( fieldName ) ) {
                    button = makeEditButton( enabler, "icosasymm-golden", "Icosahedral Symm.", "Repeat selection with chiral icosahedral symmetry" );
                    oldToolBar.add( button );
                    button.setRolloverEnabled( true );
                } else if ( "snubDodec" .equals( fieldName ) ) {
                    button = makeEditButton( enabler, "icosasymm-snubDodec", "Icosahedral Symm.", "Repeat selection with chiral icosahedral symmetry" );
                    oldToolBar.add( button );
                    button.setRolloverEnabled( true );
                } else {
                    button = makeEditButton( enabler, "octasymm", "Octahedral Symm.", "Repeat selection with chiral octahedral symmetry" );
                    oldToolBar.add( button );
                    button.setRolloverEnabled( true );
                }

           		button = makeEditButton( enabler, "tetrasymm", "Tetrahedral Symm.", "Repeat selection with chiral tetrahedral symmetry" );
                oldToolBar.add( button );
                button.setRolloverEnabled( true );

                button = makeEditButton( enabler, "axialsymm", "Axial Symmetry", "Repeat selection with symmetry around an axis" );
                oldToolBar.add( button );
                button.setRolloverEnabled( true );

                button = makeEditButton( enabler, "pointsymm", "Point Reflect", "Reflect selection through origin" );
                oldToolBar.add( button );
                button.setRolloverEnabled( true );

                button = makeEditButton( enabler, "mirrorsymm", "Mirror Symmetry", "Reflect selection through mirror" );
                oldToolBar.add( button );
                button.setRolloverEnabled( true );

                button = makeEditButton( enabler, "translate", "Translate", "Repeat selection translated along symmetry axis" );
                oldToolBar.add( button );
                button.setRolloverEnabled( true );

                if ( fullPower )
                {
                    button = makeEditButton( enabler, "centroid", "Centroid", "Construct centroid of points" );
                    oldToolBar.add( button );
                    button.setRolloverEnabled( true );
                }

                button = makeEditButton( enabler, "hideball", "Hide", "Hide selected objects" );
                oldToolBar.add( button );
                button.setRolloverEnabled( true );

                button = makeEditButton( enabler, "panel", "Panel", "Make a panel polygon" );
                oldToolBar.add( button );
                button.setRolloverEnabled( true );
            	
            	this .add( oldToolBar, toolbarLoc );
            }
            else {
            	this .oldToolBar = null;
            	this .dynamicToolBar = null;
            	this .staticToolBar = null;
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
        	this .dynamicToolBar = null;
        	this .staticToolBar = null;
        }
	}
	
	private AbstractButton newToolButton( ControlActions enabler, String group, String label, String tooltip )
	{
		AbstractButton button = makeEditButton2( enabler, "addTool-" + group, tooltip, "/icons/tools/newTool/" + group + ".png" );
		button = enabler .setButtonAction( "addTool-" + group, button );
		this .toolCreationButtons .put( group, button );
		return button;
	}
	
	private AbstractButton makeEditButton( ControlActions enabler, String command, String label, String tooltip )
	{
		String imageName = command;
		if ( imageName .endsWith( "-roottwo" ) )
			imageName = command .substring( 0, command.length() - 8 );
		else if ( imageName .endsWith( "-golden" ) )
			imageName = command .substring( 0, command.length() - 7 );
		AbstractButton button = makeEditButton2( enabler, label, tooltip, "/icons/" + imageName + "_on.png" );
		button = enabler .setButtonAction( command, button );
		Dimension dim = new Dimension( 100, 63 );
		button .setPreferredSize( dim );
		button .setMaximumSize( dim );
		return button;
	}
	
	private AbstractButton makeEditButton2( ControlActions enabler, String label, String tooltip, String imgLocation )
	{
        boolean skipImages = controller .getProperty( "noButtonImages" ) != null;

        // Create and initialize the button.
        AbstractButton button = new JButton();
        if ( skipImages )
            button .setText( label );
        else
        {
        	URL imageURL = getClass() .getResource( imgLocation );
        	if ( imageURL != null ) {
        		Icon icon = new ImageIcon( imageURL, label );
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
	                this .oldToolBar .setVisible( false );
	                monocularCanvas .removeMouseListener( monocularClicks );
	                leftEyeCanvas .removeMouseListener( leftEyeClicks );
	                rightEyeCanvas .removeMouseListener( rightEyeClicks );
	            }
	            else if ( ! "true" .equals( this .controller .getProperty( "no.toolbar" ) ) ) {
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
            this .setLightWeightPopupEnabled( false );

			this .add( setMenuAction( "copyThisView", new JMenuItem( "Copy This View" ) ) );
			this .add( setMenuAction( "useCopiedView", new JMenuItem( "Use Copied View" ) ) );

			this .addSeparator();

			this .add( setMenuAction( "lookAtBall", new JMenuItem( "Look At This" ) ) );
			this .add( setMenuAction( "lookAtOrigin", new JMenuItem( "Look At Origin" ) ) );
			this .add( setMenuAction( "lookAtSymmetryCenter", new JMenuItem( "Look At Symmetry Center" ) ) );

			this .addSeparator();

			this .add( setMenuAction( "symmTool-icosahedral", new JMenuItem( "Apply Icosahedral Symmetry" ) ) );
			this .add( setMenuAction( "setSymmetryCenter", new JMenuItem( "Set Symmetry Center" ) ) );
			this .add( setMenuAction( "setSymmetryAxis", new JMenuItem( "Set Symmetry Axis" ) ) );

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
