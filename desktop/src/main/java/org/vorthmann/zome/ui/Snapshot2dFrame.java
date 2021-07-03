
package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.vorthmann.ui.Controller;

public class Snapshot2dFrame extends JFrame
{
    private final Controller controller;
    
    private final Snapshot2dPanel snapshotPanel;
    
    private final String SAVE_AS_PDF = "as PDF";
    private final String SAVE_AS_POSTSCRIPT = "as Postscript";
    private final String SAVE_AS_SVG = "as SVG";
    
    private final String[] FORMATS = new String[]{ SAVE_AS_PDF, SAVE_AS_POSTSCRIPT, SAVE_AS_SVG };
        
    public Snapshot2dFrame( final Controller controller, final FileDialog mFileChooser )
    {
        super( "vZome 2D snapshot" );
        
        this.controller = controller;
        
        final ActionListener actions = new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent ae )
            {
                controller .actionPerformed( ae .getSource(), ae .getActionCommand() );
                snapshotPanel .repaint();
            }
        };

        snapshotPanel = new Snapshot2dPanel();
        Container contentPane = getContentPane();
        contentPane .setLayout( new BorderLayout() );

        JPanel buttonPanel = new JPanel();
        
        JButton button = new JButton( "Refresh" );
        button .setActionCommand( "refresh.2d" );
        button .addActionListener( actions );
        button .setToolTipText( "Refresh the snapshot." );
        buttonPanel .add( button );

        String[] drawStyles = controller .getCommandList( "draw.styles" );
        JComboBox<String> drawStylesCombo = new JComboBox<>( drawStyles );
        drawStylesCombo .setSelectedItem( controller .getProperty( "drawStyle" ) );
        drawStylesCombo .addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                JComboBox<?> cb = (JComboBox<?>) e .getSource();
                String drawStyle = cb .getSelectedItem().toString();
                actions .actionPerformed( new ActionEvent( cb, 0, "setDrawStyle." + drawStyle ) );
            }
        } );
        buttonPanel .add( drawStylesCombo );

        boolean setting = Boolean .valueOf( controller .getProperty( "showBackground" ) ) .booleanValue();
        JCheckBox checkbox = new JCheckBox( "show background", setting );
        checkbox .setActionCommand( "toggleBackground" );
        checkbox .addActionListener( actions );        
        buttonPanel .add( checkbox );

        final JComboBox<String> formatsCombo = new JComboBox<>( FORMATS );
        final JButton saveButton = new JButton( "Save..." );
        saveButton .addActionListener( new ActionListener()
        {            
            @Override
            public void actionPerformed( ActionEvent e )
            {
                String tail = "pdf";
                switch ( formatsCombo .getSelectedIndex() ) {

                case 1:
                    tail = "ps";
                    break;

                case 2:
                    tail = "svg";
                    break;

                default:
                    break;
                }
                ActionListener delegate = new ControllerFileAction( mFileChooser, false, "export2d."+tail, tail, controller );
                delegate .actionPerformed( e );
            }
        });
        saveButton .setToolTipText( "Save a PDF file." );
        buttonPanel .add( saveButton );

        formatsCombo .setSelectedItem( SAVE_AS_PDF );
        formatsCombo .addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                String format = "a PDF";
                switch ( formatsCombo .getSelectedIndex() ) {

                case 1:
                    format = "a Postscript";
                    break;

                case 2:
                    format = "an SVG";
                    break;

                default:
                    break;
                }
                saveButton .setToolTipText( "Save " + format + " file" );
            }
        } );
        buttonPanel .add( formatsCombo );

        contentPane .add( buttonPanel, BorderLayout.NORTH );
        contentPane .add( snapshotPanel, BorderLayout.CENTER );
    }
    
    public void setPanelSize( Dimension dims )
    {
        snapshotPanel .setPreferredSize( dims );
        controller .actionPerformed( this, "refresh.2d" );
    }

    @Override
    public void repaint()
    {
        this .snapshotPanel .repaint();
    }
    
    private class Snapshot2dPanel extends JPanel
    {
        @Override
        protected void paintComponent( Graphics g )
        {
            controller .repaintGraphics( "snapshot.2d", g, getSize() );
        }
    }
}
