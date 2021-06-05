
package org.vorthmann.zome.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.vorthmann.ui.Controller;

public class SymmetryDialog extends EscapeDialog
{
    public SymmetryDialog( Frame frame, Controller controller )
    {
        super( frame, "Direction Configuration", true );
        {
            Container content = getContentPane();
            content .setLayout( new BorderLayout() );
//            {
//                JCheckBox checkbox = new JCheckBox( "Use graphical views" );
//                checkbox .addActionListener( controller );
//                checkbox .setActionCommand( "toggleOrbitViews" );
//                boolean setting = "true" .equals( controller .getProperty( "useGraphicalViews" ) );
//                checkbox .setSelected( setting );
//                content .add( checkbox, BorderLayout.NORTH );
//            }
            {
                JPanel mainPanel = new JPanel();
                mainPanel .setLayout( new BoxLayout( mainPanel, BoxLayout.LINE_AXIS ) );
                Controller orbitController = controller .getSubController( "availableOrbits" );
                {
                    JPanel panel = new OrbitPanel( orbitController, controller, null );
                    panel .setBorder( BorderFactory .createTitledBorder( "available directions" ) );
                    mainPanel .add( panel );
                }
                {
                    JPanel panel = new OrbitPanel( controller .getSubController( "snapOrbits" ), orbitController, null );
                    panel .setBorder( BorderFactory .createTitledBorder( "snap directions" ) );
                    mainPanel .add( panel );
                }
                content .add( mainPanel, BorderLayout.CENTER );
            }
        }
//        setContentPane( new SymmetryPanel( controller ) );
        
        setSize( new Dimension( 600, 250 ) );
        setLocationRelativeTo( frame );
    }
}
