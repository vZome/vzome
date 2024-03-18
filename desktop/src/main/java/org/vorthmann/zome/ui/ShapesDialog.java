
package org.vorthmann.zome.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import com.vzome.desktop.api.Controller;

@SuppressWarnings("serial")
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
            radioButton .addActionListener( new ControllerActionListener(controller) );
        }

        // adjust height to fit all styles plus the title bar plus a bit extra at the bottom for good measure
        setSize( new Dimension( 250, 66 + (styles.length * 24)) );
        setLocationRelativeTo( frame );
    }

}
