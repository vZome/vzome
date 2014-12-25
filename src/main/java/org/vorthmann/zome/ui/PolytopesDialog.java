
//(c) Copyright 2013, Scott Vorthmann.

package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.vorthmann.ui.Controller;

public class PolytopesDialog extends EscapeDialog
{
    private final JCheckBox[] renderCheckboxes = new JCheckBox[4];
    
    public PolytopesDialog( Frame frame, final Controller controller )
    {
        super( frame, "Generate a Polytope", true );
        setSize( new Dimension( 250, 250 ) );
        setLocationRelativeTo( frame );

        Container content = getContentPane();
        content .setLayout( new BorderLayout() );
        
        String[] groupNames = controller .getCommandList( "groups" );
        String defaultGroup = controller .getProperty( "group" );
        JComboBox groups = new JComboBox( groupNames );
        groups .setSelectedItem( defaultGroup );
        groups .setMaximumRowCount( 6 );
        groups .addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                JComboBox cb = (JComboBox) e.getSource();
                String command = "setGroup." + (String) cb .getSelectedItem();
                controller .actionPerformed( new ActionEvent( e .getSource(), e.getID(), command ) );
            }
        } );
        JPanel groupsPanel = new JPanel();
        groupsPanel .add( groups );
        content .add( groupsPanel, BorderLayout .NORTH );

        JPanel listPanel = new JPanel();
        
        JPanel column = new JPanel();
        column .setLayout( new BoxLayout( column, BoxLayout.PAGE_AXIS ) );
        JLabel label = new JLabel( "edge" );
        label .setMinimumSize( new Dimension( 90, 1 ) );
        column .add( label );
        for ( int i = 3; i >= 0; i-- ) {
            final int edge = i;
            JCheckBox checkbox = new JCheckBox();
            boolean enabled = "true" .equals( controller .getProperty( "edge." + i ) );
            checkbox .setSelected( enabled );
            checkbox .setActionCommand( "edge." + i );
            checkbox .addActionListener( new ActionListener()
            {
                public void actionPerformed( ActionEvent e )
                {
                    controller .actionPerformed( e );
                    boolean enabled = "true" .equals( controller .getProperty( "edge." + edge ) );
                    renderCheckboxes[ edge ] .setEnabled( enabled );
                    if ( enabled )
                    {
                        boolean render = "true" .equals( controller .getProperty( "render." + edge ) );
                        renderCheckboxes[ edge ] .setSelected( render );
                    }
                    else
                    {
                        renderCheckboxes[ edge ] .setSelected( false );
                    }
                }
            } );
            column .add( checkbox );
        }
        listPanel .add( column );
        
        column = new JPanel();
        column .setLayout( new BoxLayout( column, BoxLayout.PAGE_AXIS ) );
        column .setMinimumSize( new Dimension( 80, 1 ) );
        label = new JLabel( "render" );
        label .setMinimumSize( new Dimension( 90, 1 ) );
        column .add( label );
        for ( int i = 3; i >= 0; i-- ) {
            renderCheckboxes[ i ] = new JCheckBox();
            boolean enabled = "true" .equals( controller .getProperty( "edge." + i ) );
            boolean render = "true" .equals( controller .getProperty( "render." + i ) );
            renderCheckboxes[ i ] .setEnabled( enabled );
            renderCheckboxes[ i ] .setSelected( enabled && render );
            renderCheckboxes[ i ] .setActionCommand( "render." + i );
            renderCheckboxes[ i ] .addActionListener( controller );
            column .add( renderCheckboxes[ i ] );
        }
        listPanel .add( column );
        
        content .add( listPanel, BorderLayout .CENTER );
        
        JPanel buttons = new JPanel();
        content .add( buttons, BorderLayout .SOUTH );
        
        JButton cancel = new JButton( "Cancel" );
        cancel .addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                PolytopesDialog.this .setVisible( false );
            }
        } );
        buttons .add( cancel );
        
        JButton build = new JButton( "Generate" );
        build .setActionCommand( "generate" );
        build .addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                PolytopesDialog.this .setVisible( false );
                controller .actionPerformed( e );
            }
        } );
        buttons .add( build );
    }
}
