package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.vorthmann.ui.CardPanel;
import org.vorthmann.ui.Controller;

public class OrbitPanel extends JPanel
{
	public OrbitPanel( final Controller controller, final Controller orbitController, ControlActions enabler )
	{
        final CardPanel cardPanel = new CardPanel();
        final JPanel orbitCheckboxes = new JPanel();
        final JPanel orbitTriangle = new JPanel()
        {
            public void paintComponent( Graphics graphics )
            {
                controller .repaintGraphics( "orbits", graphics, getSize() );
            }
        };

        // here's the containment hierarchy
        this .setLayout( new BorderLayout() );
        {
            JPanel controlStrip = new JPanel();
            controlStrip .setLayout( new BorderLayout() );
            {
                JPanel row1 = new JPanel();
                row1 .setLayout( new GridLayout( 0, 3 ) );
                row1 .add( createButton( "None", "setNoDirections", controller ) );
                final JCheckBox checkbox = createCheckbox( "single", "oneAtATime", controller );
                if ( "true" .equals( controller .getProperty( "oneAtATime" ) ) )
                    checkbox .setSelected( true );
                row1 .add( createButton( "All", "setAllDirections", new ActionListener()
                {
                    public void actionPerformed( ActionEvent evt )
                    {
                        controller .actionPerformed( evt );
                        checkbox .setSelected( false );
                    }
                } ) );
                row1 .add( checkbox, BorderLayout.WEST );
                controlStrip .add( row1, BorderLayout.NORTH );
            }
            this .add( controlStrip, BorderLayout.NORTH );
        }
        {
            cardPanel .add( "graphical", orbitTriangle );
            {
                orbitCheckboxes .setLayout( new BoxLayout( orbitCheckboxes, 1 ) );
                JScrollPane scrollPanel = new JScrollPane( orbitCheckboxes );
                cardPanel .add( "textual", scrollPanel );
            }
            if ( "true" .equals( controller .getProperty( "useGraphicalViews" ) ) )
                cardPanel .showCard( "graphical" );
            else
                cardPanel .showCard( "textual" );
            this .add( cardPanel, BorderLayout.CENTER );
        }
        orbitsChanged( controller, orbitController, orbitCheckboxes, orbitTriangle );

        controller .addPropertyListener( new PropertyChangeListener()
        {
            public void propertyChange( PropertyChangeEvent event )
            {
                if ( "orbits" .equals( event .getPropertyName() ) )
                    enabledChanged( controller, orbitCheckboxes, orbitTriangle );
                else if ( "useGraphicalViews" .equals( event .getPropertyName() ) )
                    if ( Boolean.TRUE .equals( event .getNewValue() ) )
                        cardPanel .showCard( "graphical" );
                    else
                        cardPanel .showCard( "textual" );
            }
        } );
        
        orbitController .addPropertyListener( new PropertyChangeListener()
        {
            public void propertyChange( PropertyChangeEvent event )
            {
                if ( "orbits" .equals( event .getPropertyName() ) )
                    orbitsChanged( controller, orbitController, orbitCheckboxes, orbitTriangle );
            }
        } );
        
        controller .getMouseTool() .attach( orbitTriangle );

        if ( enabler != null )
        {
            PickerPopup directionPopupMenu = new PickerPopup();
            directionPopupMenu.setLightWeightPopupEnabled( false );

            directionPopupMenu.add( enabler .setMenuAction( "rZomeOrbits",         new JMenuItem( "real Zome" ) ) );
            directionPopupMenu.add( enabler .setMenuAction( "predefinedOrbits",    new JMenuItem( "predefined" ) ) );
            directionPopupMenu.add( enabler .setMenuAction( "usedOrbits",          new JMenuItem( "used in model" ) ) );
            directionPopupMenu.add( enabler .setMenuAction( "setAllDirections",    new JMenuItem( "all" ) ) );
            directionPopupMenu.add( enabler .setMenuAction( "configureDirections", new JMenuItem( "configure..." ) ) );

            MouseListener orbitPopup = new ContextualMenuMouseListener( controller, directionPopupMenu );
            
            orbitTriangle .addMouseListener( orbitPopup );
        }
	}
    
    private void orbitsChanged( final Controller controller, final Controller orbitController, JPanel orbitCheckboxes, JPanel orbitTriangle )
    {
        controller .actionPerformed( new ActionEvent( orbitTriangle, 0, "refreshDots" ) );
        
        orbitCheckboxes .removeAll();
        String[] dirNames = controller .getCommandList( "allOrbits" );
        for ( int j = 0; j < dirNames.length; j++ )
        {
            final String orbitName = dirNames[ j ];
            JPanel panel = new JPanel();
            panel .setMaximumSize( new Dimension( 600, 20 ) );
            panel .setLayout( new BorderLayout() );
            final JPanel colorSwatch = new JPanel()
            {
                public void paintComponent( Graphics graphics )
                {
                    controller .repaintGraphics( "oneOrbit." + orbitName, graphics, getSize() );
                }
            };
            colorSwatch .setMaximumSize( new Dimension( 60, 20 ) );
            colorSwatch .setPreferredSize( new Dimension( 60, 20 ) );
            colorSwatch .setMinimumSize( new Dimension( 60, 20 ) );
            panel .add( colorSwatch, BorderLayout.WEST );
            {
                JCheckBox checkbox  = new JCheckBox();
                checkbox .setText( orbitName );
                checkbox .setVisible( true );
                checkbox .setSelected( false );
                checkbox .setActionCommand( "toggleDirection." + orbitName );
                checkbox .addActionListener( controller );
                panel .add( checkbox, BorderLayout.CENTER );
            }
            orbitCheckboxes .add( panel );
        }
        enabledChanged( controller, orbitCheckboxes, orbitTriangle );
    }
    
    private void enabledChanged( Controller controller, JPanel orbitCheckboxes, JPanel orbitTriangle )
    {
        String[] dirNames = controller .getCommandList( "orbits" );
        for ( int i = 0; i < orbitCheckboxes .getComponentCount(); i++ ) {
            JPanel row  = (JPanel) orbitCheckboxes .getComponent( i );
            JCheckBox checkbox  = (JCheckBox) row .getComponent( 1 );
            checkbox .setSelected( false );
        }
        for ( int j = 0; j < dirNames.length; j++ )
        {
            for ( int i = 0; i < orbitCheckboxes .getComponentCount(); i++ ) {
                JPanel row  = (JPanel) orbitCheckboxes .getComponent( i );
                JCheckBox checkbox  = (JCheckBox) row .getComponent( 1 );
                if ( dirNames[ j ] .equals( checkbox .getText() ) ) {
                    checkbox .setSelected( true );
                    break;
                }
            }
        }
        orbitTriangle .repaint();
        orbitCheckboxes .repaint(); 
        // The repaint leaves some instances blank!  don't know why.
        //  This seems to address the problem.
        orbitCheckboxes .setVisible( false );
        orbitCheckboxes .setVisible( true );
    }

    protected static JButton createButton( String buttonText, String actionCommand, ActionListener listener )
    {
        JButton button = new JButton( buttonText );
        button .addActionListener( listener );
        button .setActionCommand( actionCommand );        
        return button;
    }

    protected static JCheckBox createCheckbox( String buttonText, String actionCommand, ActionListener listener )
    {
        JCheckBox button = new JCheckBox( buttonText, false );
        button .addActionListener( listener );
        button .setActionCommand( actionCommand );        
        return button;
    }
}
