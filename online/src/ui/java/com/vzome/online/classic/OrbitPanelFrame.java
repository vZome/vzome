
package vzome;

import java.awt.Dimension;
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.vzome.desktop.api.Controller;
import com.vzome.desktop.controller.DefaultController;
import org.vorthmann.zome.ui.OrbitPanel;

/**
 * This was my initial attempt at using legacy Swing-based vZome UI classes in the web.
 * I abandoned it because j4ts-awt-swing is simply not ready.  In this case, it fails
 * somewhere in GridLayout, one of the *six* layout managers used by vZome, and I'm not
 * willing to debug and possibly implement them.
 *
 * This code is currently not getting transpiled by JSweet... see `build.gradle`.
 */
public class OrbitPanelFrame extends JFrame
{
    public OrbitPanelFrame()
    {
        super( "Icosahedral Orbits" );

        Controller c1 = new DefaultController();
        Controller c2 = new DefaultController();
        JPanel panel = new OrbitPanel( c1, c2, null ); // This will fail, because of some invariant with GridLayout
        getContentPane() .add( panel, BorderLayout.CENTER );
    }

    /*
     * We are already running on the "event dispatch thread", since the
     * browser is single-threaded, so there's no need for the usual
     * SwingUtilities.invokeLater() pattern.
     */
    public static void main( String[] args )
    {
        JFrame frame = new OrbitPanelFrame() ;
        frame .setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        //Display the window.
        frame .pack();
        frame .setVisible( true );
    }
}