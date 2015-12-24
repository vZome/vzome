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
    private MouseListener modelPopupClicks;
    private Component monocularCanvas, leftEyeCanvas, rightEyeCanvas;
    private final JToolBar toolBar;
    private final boolean isEditor;
	private final Controller controller, view;
	private JPanel mMonocularPanel;

	public ModelPanel( Controller controller, Controller view, ControlActions enabler, boolean isEditor, boolean fullPower )
	{
		super( new BorderLayout() );
		this .controller = controller;
        this .view = view;
        this .isEditor = isEditor;

        controller .addPropertyListener( this );

        JPanel monoStereoPanel = new JPanel();
        this .add( monoStereoPanel, BorderLayout.CENTER );
		CardLayout monoStereoCardLayout = new CardLayout();
		monoStereoPanel .setLayout( monoStereoCardLayout );
        boolean showStereo =  "true" .equals( view .getProperty( "stereo" ) );

        final ContextualMenu modelPopupMenu = new ContextualMenu();
        modelPopupMenu .setLightWeightPopupEnabled( false );
        modelPopupClicks = new ContextualMenuMouseListener( controller , modelPopupMenu );

        mMonocularPanel = new JPanel( new BorderLayout() );
        {
            mMonocularPanel .setPreferredSize( new Dimension( 2000, 2000 ) );
            monocularCanvas = ( (J3dComponentFactory) controller ).createJ3dComponent( "mainViewer-monocular" );
            mMonocularPanel .add( monocularCanvas, BorderLayout.CENTER );
            if ( isEditor )
                monocularCanvas .addMouseListener( modelPopupClicks );
        }
        monoStereoPanel .add( mMonocularPanel, "mono" );
        JPanel stereoPanel = new JPanel();
        {
            GridLayout grid = new GridLayout( 1, 2 );
            stereoPanel .setLayout( grid );
            leftEyeCanvas = ( (J3dComponentFactory) controller ).createJ3dComponent( "mainViewer-leftEye" );
            stereoPanel .add( leftEyeCanvas );
            rightEyeCanvas = ( (J3dComponentFactory) controller ).createJ3dComponent( "mainViewer-rightEye" );
            stereoPanel .add( rightEyeCanvas );

            if ( isEditor )
            {
                leftEyeCanvas .addMouseListener( modelPopupClicks );
                rightEyeCanvas .addMouseListener( modelPopupClicks );
            }
        }
        monoStereoPanel .add( stereoPanel, "stereo" );
        if ( showStereo )
            monoStereoCardLayout .show( monoStereoPanel, "stereo" );
        else
            monoStereoCardLayout .show( monoStereoPanel, "mono" );
        view .addPropertyListener( new PropertyChangeListener(){
        	@Override
        	public void propertyChange( PropertyChangeEvent chg )
        	{
        		if ( "stereo" .equals( chg .getPropertyName() ) )
        			if ( ((Boolean) chg .getNewValue()) .booleanValue() )
        				monoStereoCardLayout .show( monoStereoPanel, "stereo" );
        			else
        				monoStereoCardLayout .show( monoStereoPanel, "mono" );
        	}
        } );

        // create the contextual menus
        // -----------------------------------------------

        modelPopupMenu.add( enabler .setMenuAction( "copyThisView", new JMenuItem( "Copy This View" ) ) );
        modelPopupMenu.add( enabler .setMenuAction( "useCopiedView", new JMenuItem( "Use Copied View" ) ) );

        modelPopupMenu.addSeparator();

        modelPopupMenu.add( enabler .setMenuAction( "lookAtBall", new JMenuItem( "Look At This" ) ) );
        modelPopupMenu.add( enabler .setMenuAction( "lookAtOrigin", new JMenuItem( "Look At Origin" ) ) );
        modelPopupMenu.add( enabler .setMenuAction( "lookAtSymmetryCenter", new JMenuItem( "Look At Symmetry Center" ) ) );

        modelPopupMenu.addSeparator();

        modelPopupMenu.add( enabler .setMenuAction( "setSymmetryCenter", new JMenuItem( "Set Symmetry Center" ) ) );
        modelPopupMenu.add( enabler .setMenuAction( "setSymmetryAxis", new JMenuItem( "Set Symmetry Axis" ) ) );

        modelPopupMenu.addSeparator();

        modelPopupMenu.add( enabler .setMenuAction( "setWorkingPlane", new JMenuItem( "Set Working Plane" ) ) );
        modelPopupMenu.add( enabler .setMenuAction( "setWorkingPlaneAxis", new JMenuItem( "Set Working Plane Axis" ) ) );

        modelPopupMenu.addSeparator();

        modelPopupMenu.add( enabler .setMenuAction( "selectSimilarSize", new JMenuItem( "Select All Similar" ) ) );

        modelPopupMenu.add( enabler .setMenuAction( "undoToManifestation", new JMenuItem( "Undo Including This" ) ) );

        modelPopupMenu.addSeparator();

        modelPopupMenu .add( enabler .setMenuAction( "setBackgroundColor", new JMenuItem( "Set Background Color..." ) ) );

        modelPopupMenu .addSeparator();

        modelPopupMenu.add( enabler .setMenuAction( "setBuildOrbitAndLength", new JMenuItem( "Build With This" ) ) );
        modelPopupMenu.add( enabler .setMenuAction( "showProperties", new JMenuItem( "Show Properties" ) ) );

        // --------------------------------------- Create the toolbar.

        this .toolBar = new JToolBar( "vZome Toolbar" );
        this .toolBar .setOrientation( JToolBar.VERTICAL );
        String toolbarLoc = controller .getProperty( "toolbar.position" );
        if ( toolbarLoc == null )
        	toolbarLoc = BorderLayout .LINE_END;
        if ( this .isEditor && ! "true" .equals( controller.getProperty( "no.toolbar" ) ) )
        	this .add( toolBar, toolbarLoc );

        AbstractButton button = null;

        button = makeEditButton( enabler, "joinballs", "Join", "Join two or more selected balls" );
        toolBar.add( button );
        button.setRolloverEnabled( true );

        String fieldName = controller.getProperty( "field.name" );
        if ( "golden" .equals( fieldName ) ) {
            button = makeEditButton( enabler, "icosasymm-golden", "Icosahedral Symm.", "Repeat selection with chiral icosahedral symmetry" );
            toolBar.add( button );
            button.setRolloverEnabled( true );
        } else if ( "snubDodec" .equals( fieldName ) ) {
            button = makeEditButton( enabler, "icosasymm-snubDodec", "Icosahedral Symm.", "Repeat selection with chiral icosahedral symmetry" );
            toolBar.add( button );
            button.setRolloverEnabled( true );
        } else {
            button = makeEditButton( enabler, "octasymm", "Octahedral Symm.", "Repeat selection with chiral octahedral symmetry" );
            toolBar.add( button );
            button.setRolloverEnabled( true );
        }

   		button = makeEditButton( enabler, "tetrasymm", "Tetrahedral Symm.", "Repeat selection with chiral tetrahedral symmetry" );
        toolBar.add( button );
        button.setRolloverEnabled( true );

        button = makeEditButton( enabler, "axialsymm", "Axial Symmetry", "Repeat selection with symmetry around an axis" );
        toolBar.add( button );
        button.setRolloverEnabled( true );

        button = makeEditButton( enabler, "pointsymm", "Point Reflect", "Reflect selection through origin" );
        toolBar.add( button );
        button.setRolloverEnabled( true );

        button = makeEditButton( enabler, "mirrorsymm", "Mirror Symmetry", "Reflect selection through mirror" );
        toolBar.add( button );
        button.setRolloverEnabled( true );

        button = makeEditButton( enabler, "translate", "Translate", "Repeat selection translated along symmetry axis" );
        toolBar.add( button );
        button.setRolloverEnabled( true );

        if ( fullPower )
        {
            button = makeEditButton( enabler, "centroid", "Centroid", "Construct centroid of points" );
            toolBar.add( button );
            button.setRolloverEnabled( true );
        }

        button = makeEditButton( enabler, "hideball", "Hide", "Hide selected objects" );
        toolBar.add( button );
        button.setRolloverEnabled( true );

        button = makeEditButton( enabler, "panel", "Panel", "Make a panel polygon" );
        toolBar.add( button );
        button.setRolloverEnabled( true );
	}
	
	private AbstractButton makeEditButton( ControlActions enabler, String command, String label, String tooltip )
	{

        String imageName = command;
        if ( imageName.endsWith( "-roottwo" ) )
            imageName = command.substring( 0, command.length() - 8 );
        else if ( imageName.endsWith( "-golden" ) )
            imageName = command.substring( 0, command.length() - 7 );

        boolean skipImages = controller .getProperty( "noButtonImages" ) != null;

        // Look for the image.
        String imgLocation = "/icons/" + imageName + "_off.png";
        URL imageURL = getClass().getResource( imgLocation );

        // Create and initialize the button.
        AbstractButton button = new JButton();
        if ( skipImages )
            button.setText( label );
        else if ( imageURL != null ) {
            Icon icon = new ImageIcon( imageURL, label );
            button.setIcon( icon );
            // the rest will only work if setRolloverEnabled(true) is called
            // after adding to the toolbar!
            imageURL = getClass().getResource( "/icons/" + imageName + "_on.png" );
            icon = new ImageIcon( imageURL, label );
            button.setRolloverIcon( icon );
            Dimension dim = new Dimension( 100, 63 );
            button .setPreferredSize( dim );
            button .setMaximumSize( dim );
        } else
            System.err.println( "Resource not found: " + imgLocation );
        button.setVerticalTextPosition( SwingConstants.TOP );
        button.setHorizontalTextPosition( SwingConstants.CENTER );
        button.setToolTipText( tooltip );
            
        button = enabler .setButtonAction( command, button );
		return button;
	}

	@Override
	public void propertyChange( PropertyChangeEvent e )
	{
        if ( "editor.mode" .equals( e .getPropertyName() ) )
        {
            String mode = (String) e .getNewValue();
            String stereo = this .view .getProperty( "stereo" );
            if ( "article" .equals( mode ) )
            {
                this .toolBar .setVisible( false );
                if ( isEditor )
                {
                    if ( "true" .equals( stereo ) )
                    {
                        leftEyeCanvas .removeMouseListener( modelPopupClicks );
                        rightEyeCanvas .removeMouseListener( modelPopupClicks );
                    }
                    else
                    {
                        monocularCanvas .removeMouseListener( modelPopupClicks );
                    }
                }
            }
            else
            {                   
                if ( isEditor )
                {
                    if ( ! "true" .equals( this .controller .getProperty( "no.toolbar" ) ) )
                        this .toolBar .setVisible( true );
                    if ( "true" .equals( stereo ) )
                    {
                        leftEyeCanvas .addMouseListener( modelPopupClicks );
                        rightEyeCanvas .addMouseListener( modelPopupClicks );
                    }
                    else
                    {
                        monocularCanvas .addMouseListener( modelPopupClicks );
                    }
                }
            }
        }
	}

	public Dimension getRenderedSize()
	{
		return this .mMonocularPanel .getSize();
	}
}
