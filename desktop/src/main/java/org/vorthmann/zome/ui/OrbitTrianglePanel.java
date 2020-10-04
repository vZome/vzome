package org.vorthmann.zome.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.vzome.core.construction.Color;
import com.vzome.core.editor.Application;
import com.vzome.core.editor.FieldApplication;
import com.vzome.core.editor.api.OrbitSource;
import com.vzome.core.editor.SymmetrySystem;
import com.vzome.core.editor.FieldApplication.SymmetryPerspective;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.render.Colors;

public class OrbitTrianglePanel extends JPanel
{
    private static final int RADIUS = 12;
    private static final int DIAM = 2 * RADIUS;
    private static int TOP = 30;
    private static int LEFT = TOP;
    
    private static final Colors COLORS = new Colors( Application.loadDefaults() );

    private final Map<Direction, OrbitState> orbitDots = new HashMap<>();
    private OrbitSource colorSource;

    public OrbitTrianglePanel( SymmetryPerspective symmPer )
    {
        super();

        colorSource = new SymmetrySystem( null, symmPer, null, COLORS, true );
        Symmetry symm = symmPer .getSymmetry();
        for ( Direction orbit : symm ) {
            OrbitState dot = new OrbitState();
            dot .dotX = orbit .getDotX();
            dot .dotY = orbit .getDotY();
            orbitDots .put( orbit, dot );
        }
    }
    
    private static class OrbitState
    {
        double dotX, dotY;
        int dotXint, dotYint;
    }

    @Override
    public void paintComponent( Graphics graphics )
    {
        int fullwidth = (int) this .getSize() .getWidth();
        int fullheight = (int) this .getSize() .getHeight();
        
        Graphics2D g2d = (Graphics2D) graphics;
        g2d .clearRect( 0, 0, fullwidth, fullheight );
        g2d .setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        int width = fullwidth - 2*TOP;
        int height = fullheight - 2*LEFT;

        int right = LEFT + width;
        int bottom = TOP + height ;
        int corner = LEFT;

        g2d .setStroke( new BasicStroke( 1.5f , BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
        g2d .setPaint( java.awt.Color.black );

        GeneralPath path = new GeneralPath();
        path .moveTo( corner, TOP );
        path .lineTo( LEFT, bottom );
        path .lineTo( right, bottom );
        path .lineTo( corner, TOP );
        path .closePath();
        g2d .draw( path );

        for (Direction dir : orbitDots .keySet()) {
            OrbitState orbit = orbitDots .get( dir );
            Color color = colorSource .getColor( dir );
            int x = LEFT + (int) Math .round( orbit.dotX * width );
            // now store the int coords for later picking
            orbit.dotXint = x;
            orbit.dotYint = bottom -  (int) Math .round( orbit.dotY * height );
            g2d .setPaint( color == null? java.awt.Color.WHITE : new java.awt.Color( color .getRGB() ) );
            g2d .fillOval( orbit.dotXint-RADIUS, orbit.dotYint-RADIUS, DIAM, DIAM );
            g2d .setPaint( java.awt.Color.black );
            g2d .drawOval( orbit.dotXint-RADIUS, orbit.dotYint-RADIUS, DIAM, DIAM );
        }

        g2d.dispose(); //clean up
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI()
    {
        int x = 0, y = 0;
        Application app = new Application( false, null, new Properties() );
        for (String fieldName : app .getFieldNames()) {
            x += 60;
            y += 40;
            FieldApplication kind = app .getDocumentKind( fieldName );
            for (SymmetryPerspective symmPer : kind .getSymmetryPerspectives()) {
                String symmName = symmPer .getName();
                
                //Create and set up the window.
                JFrame frame = new JFrame( fieldName + " : " + symmName );
                x += 20;
                y += 20;
                frame .setLocation( x, y );
         
                JPanel trianglePanel = new OrbitTrianglePanel( symmPer );
                trianglePanel .setPreferredSize( new Dimension( 500, 300 ) );
         
                frame.getContentPane().add( trianglePanel, BorderLayout.CENTER );
         
                //Display the window.
                frame.pack();
                frame.setVisible(true);
            }
            
        }
    }
 
    public static void main( String[] args )
    {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}