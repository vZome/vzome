
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.vorthmann.ui.Controller;

public class ToolsPanel extends JPanel implements PropertyChangeListener
{
    private final Controller controller;
    
    private int toolNum = 0;
    
    private JPanel instances;
    
    private JScrollPane scroller;
    
    private List<String> toolIDs;
    
    private static final Logger logger = Logger .getLogger( "org.vorthmann.zome.ui" );

    public ToolsPanel( final JFrame frame, final Controller controller )
    {
        this .controller = controller;
        controller .addPropertyListener( this );
                
        setLayout( new BorderLayout() );
        
        JPanel types = new JPanel();
        {
            JButton newButton = new JButton( "+" );
            newButton .addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent arg0 )
                {
                    final NewToolDialog newToolDialog = new NewToolDialog( frame, controller );
                    newToolDialog .setToolNum( ++toolNum );
                    newToolDialog .setVisible( true );
                }
                
            } );
            newButton .setPreferredSize( new Dimension( 40, 20 ) );
            types .add( newButton );
        }
        
        add( types, BorderLayout .NORTH );
        
        instances = new JPanel();
        instances .setMaximumSize( new Dimension( 350, 0 ) );
//        instances .setLayout( new BoxLayout( instances, BoxLayout .Y_AXIS ) );
        instances .setLayout( new GridLayout( 0,4 ) );
        {
            toolIDs = new ArrayList<>();
            Collections .addAll( toolIDs, this .controller .getCommandList( "tool.instances" ) );
        }
        JPanel instancesBorder = new JPanel( new BorderLayout() );
        instancesBorder .add( instances, BorderLayout.PAGE_START );

        scroller = new JScrollPane( instancesBorder, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        add( scroller, BorderLayout .CENTER );
    }


    protected static Component createButton( String action, ActionListener listener )
    {
        int delim = action .indexOf( "." );
        String group = action .substring( 0, delim );
        delim = action .indexOf( "/" );
        String name = action .substring( delim + 1 );
        String iconPath = "/icons/tools/" + group + ".png";
        JButton button = new JButton();
        java.net.URL imgURL = LessonPanel.class .getResource( iconPath );
        if ( imgURL != null )
            button .setIcon( new ImageIcon( imgURL ) );
        else {
            button .setText( name );
            logger .warning( "Couldn't find resource: " + iconPath );
        }
        button .addActionListener( listener );
        button .setActionCommand( action );
        Dimension dim = new Dimension( 55, 52 );
        button .setMinimumSize( dim );
        button .setPreferredSize( dim );
        button .setMaximumSize( dim );
        button .setToolTipText( name );
        
//        JPanel row = new JPanel( new BorderLayout() );
//        row .setBorder( BorderFactory .createEtchedBorder( EtchedBorder.LOWERED ) );
//        row .add( button, BorderLayout.WEST );
//        JLabel label = new JLabel( " " + name );
//        dim = new Dimension( 95, 25 );
//        label .setMinimumSize( dim );
//        row .add( label, BorderLayout.CENTER );
//        row .setMaximumSize( new Dimension( 600, 60 ) );
//        row .getLayout() .layoutContainer( row );
        return button;
    }

    public void propertyChange( PropertyChangeEvent evt )
    {
        if ( evt .getPropertyName() .equals( "tool.instances" ) )
        {
            if ( evt .getNewValue() == null )
            {
                String idAndName = (String) evt .getOldValue(); // will be "group.N/label"
                int index = toolIDs .indexOf( idAndName );
                toolIDs .remove( index );
                instances .remove( index );
                scroller .revalidate();
                scroller .repaint();
            }
            else if ( evt .getOldValue() == null )
            {
                String idAndName = (String) evt .getNewValue(); // will be "group.N/label"
                toolIDs .add( idAndName );
                Component button = createButton( idAndName, controller );
                instances .add( button );
                scroller .revalidate();
            }
        }
    }
}
