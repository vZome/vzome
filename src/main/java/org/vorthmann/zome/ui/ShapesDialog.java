
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package org.vorthmann.zome.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import org.vorthmann.ui.Controller;

public class ShapesDialog extends EscapeDialog
{
    public ShapesDialog( Frame frame, Controller controller )
    {
        super( frame, "Rendering Shapes", true );
        Container content = getContentPane();
        content .setLayout( new BoxLayout( content, BoxLayout.PAGE_AXIS ) );

        ButtonGroup group = new ButtonGroup();
        JRadioButton radioButton = null;
        String[] styles = controller .getCommandList( "styles" );
        String currStyle = controller .getProperty( "renderingStyle" );
        for ( int i = 0; i < styles.length; i++ ) {
            radioButton  = new JRadioButton( styles[ i ] );
            radioButton .setVisible( true );
            radioButton .setSelected( styles[ i ] .equals( currStyle ) );
            content .add( radioButton );
            group .add( radioButton );
            radioButton .setActionCommand( "setStyle." + styles[ i ] );
            radioButton .addActionListener( controller );
        }

        setSize( new Dimension( 250, 180 ) );
        setLocationRelativeTo( frame );
    }

}
