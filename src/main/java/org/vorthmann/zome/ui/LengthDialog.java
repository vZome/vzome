
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import org.vorthmann.ui.Controller;

public class LengthDialog extends EscapeDialog
{
    private final JTextArea[] fields;
    
    public LengthDialog( Frame frame, final Controller controller )
    {
        super( frame, "Custom Unit Strut Length", true );
        Container content = getContentPane();
        content .setLayout( new BorderLayout() );

        {
            JPanel topPanel = new JPanel();
            topPanel .setLayout( new BoxLayout( topPanel, BoxLayout.LINE_AXIS ) );
            topPanel .setBorder( BorderFactory .createEmptyBorder( 4, 4, 4, 4 ) );
            
            // TODO : add text fields
            String[] labels = controller .getCommandList( "labels" );
            String[] values = controller .getCommandList( "values" );
            fields = new JTextArea[ values .length ];

            JLabel label = new JLabel( "(  " );
            Font biggerFont = label .getFont() .deriveFont( 14f );
            Font biggestFont = label .getFont() .deriveFont( 20f );
            Dimension maxSize = new Dimension( 40, 20 );
            
            for ( int i = 0; i < values.length; i++ )
            {
                if ( i == 0 )
                    label = new JLabel( "( " );
                else if ( i == 1 )
                    label = new JLabel( "  +  " );
                else if ( i == values.length - 1 )
                    label = new JLabel( labels[ i-1 ] + " ) / " );
                else
                    label = new JLabel( labels[ i-1 ] + " + " );
                label .setFont( biggestFont );
                topPanel .add( label );

                fields[ i ] = new JTextArea( values[ i ] );
                fields[ i ] .setBorder( BorderFactory .createBevelBorder( BevelBorder .LOWERED ) );
                fields[ i ] .setFont( biggerFont );
                fields[ i ] .setMaximumSize( maxSize );
                fields[ i ] .setPreferredSize( maxSize );
                topPanel .add( fields[ i ] );
            }
            
            content .add( topPanel, BorderLayout .CENTER );
        }
        JPanel bottomPanel = new JPanel();
        bottomPanel .setComponentOrientation( ComponentOrientation.RIGHT_TO_LEFT );
        {
            JButton cancelButton = new JButton( "Cancel" );
            cancelButton .addActionListener( new ActionListener(){

                @Override
                public void actionPerformed( ActionEvent e )
                {
                    LengthDialog.this .setVisible( false );
                }
            } );
            bottomPanel .add( cancelButton );
        }
        {
            final JButton setButton = new JButton( "Set" );
            getRootPane() .setDefaultButton( setButton );
            setButton .addActionListener( new ActionListener(){

                @Override
                public void actionPerformed( ActionEvent e )
                {
                    StringBuffer buf = new StringBuffer();
                    for ( int i = 0; i < fields.length; i++ ) {
                        buf .append( fields[ i ] .getText() );
                        buf .append( " " );
                    }
                    controller .setProperty( "customUnit", buf .toString());
                    LengthDialog .this .setVisible( false );
                }
            } );
            bottomPanel .add( setButton );
        }
        content .add( bottomPanel, BorderLayout .SOUTH );

        JRadioButton radioButton = null;
        String[] styles = controller .getCommandList( "styles" );
        String currStyle = controller .getProperty( "renderingStyle" );
        for ( int i = 0; i < styles.length; i++ ) {
            radioButton  = new JRadioButton( styles[ i ] );
            radioButton .setVisible( true );
            radioButton .setSelected( styles[ i ] .equals( currStyle ) );
            content .add( radioButton );
            radioButton .setActionCommand( "setStyle." + styles[ i ] );
            radioButton .addActionListener( controller );
        }

        setSize( new Dimension( 250, 150 ) );
        setLocationRelativeTo( frame );
    }

    public void resync( Controller controller )
    {
        String[] values = controller .getCommandList( "values" );
        for ( int i = 0; i < values.length; i++ )
            fields[ i ] .setText( values[ i ] );
    }

}
