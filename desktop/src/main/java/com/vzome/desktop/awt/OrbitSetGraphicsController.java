
package com.vzome.desktop.awt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.util.HashMap;
import java.util.Map;

import org.vorthmann.j3d.MouseTool;
import org.vorthmann.j3d.MouseToolDefault;
import org.vorthmann.ui.LeftMouseDragAdapter;

import com.vzome.desktop.api.Controller;

public class OrbitSetGraphicsController extends DefaultGraphicsController
{
    private boolean showLastOrbit = false;

    private final Map<String, OrbitState> orbitDots = new HashMap<>();

    private final MouseTool mouseTool = new LeftMouseDragAdapter( new MouseToolDefault()
    {
        @Override
        public void mouseClicked( MouseEvent click )
        {
            String pickedDir = pickDirection( click );
            if ( pickedDir != null ) {
                actionPerformed( click.getSource(), "toggleDirection." + pickedDir );
            }
        }
    }, /* half-second forgiveness */ 500 );

    private static class OrbitState
    {
        double dotX, dotY;
        int dotXint, dotYint;
        Color color;
    }

    public OrbitSetGraphicsController( Controller parent, boolean showLastOrbit )
    {
        super();
        parent .addSubController( "graphics", this );
        this.showLastOrbit = showLastOrbit;
    }


    @Override
    public void doAction( String action ) throws Exception
    {
        if ( action .equals( "refreshDots" ) )
        {
            super .doAction( action ); // recompute the dots in the real controller
            // then fetch them
            orbitDots .clear();
            for ( String orbitName : super .getCommandList( "allOrbits" ) ) {
                String details = super .getProperty( "orbitDot." + orbitName );
                if ( details != null && details .contains( "/" ) ) {
                    String[] tokens = details .split( "/" );
                    OrbitState dot = new OrbitState();
                    dot .color = Color.decode( tokens[0] );
                    dot .dotX = Double.parseDouble( tokens[1] );
                    dot .dotY = Double.parseDouble( tokens[2] );
                    this .orbitDots .put( orbitName, dot );
                }
            }
            return;
        }
        super .doAction( action );
    }
    
    private static final int RADIUS = 12;
    private static final int INNER_RADIUS = 5;
    private static final int OUTER_RADIUS = 19;
    private static final int DIAM = 2 * RADIUS;
    private static int TOP = 30;
    private static int LEFT = TOP;

    @Override
    public void repaintGraphics( String panelName, Graphics graphics, Dimension size )
    {
        if ( panelName .startsWith( "oneOrbit." ) )
        {
            OrbitState orbit = orbitDots .get( panelName .substring( "oneOrbit." .length() ) );
            Graphics2D g2d = (Graphics2D) graphics;
            g2d .clearRect( 0, 0, (int) size .getWidth(), (int) size .getHeight() );
            g2d .setPaint( orbit.color );
            g2d .fill( g2d .getClipBounds() );
        }
        else if ( "selectedOrbit" .equals( panelName ) )
        {
            Graphics2D g2d = (Graphics2D) graphics;
            g2d .clearRect( 0, 0, (int) size .getWidth(), (int) size .getHeight() );
            String lastOrbit = super .getProperty( "lastOrbit" );
            OrbitState orbit = orbitDots .get( lastOrbit );
            g2d .setPaint( orbit.color );
            g2d .fill( g2d .getClipBounds() );
        }
        else if ( "orbits" .equals( panelName ) )
        {
            int fullwidth = (int) size .getWidth();
            int fullheight = (int) size .getHeight();

            Graphics2D g2d = (Graphics2D) graphics;
            g2d .clearRect( 0, 0, fullwidth, fullheight );
            g2d .setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

            if ( fullheight > 180 )
                fullheight = 180;
            int width = fullwidth - 2*TOP;
            int height = fullheight - 2*LEFT;

            int right = LEFT + width;
            int bottom = TOP + height ;
            int corner = LEFT;
            if ( super .propertyIsTrue( "reverseOrbitTriangle" ) )
                corner = right;

            g2d .setStroke( new BasicStroke( 1.5f , BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
            g2d .setPaint( Color.black );

            GeneralPath path = new GeneralPath();
            path .moveTo( corner, TOP );
            path .lineTo( LEFT, bottom );
            path .lineTo( right, bottom );
            path .lineTo( corner, TOP );
            path .closePath();
            g2d .draw( path );

            for (String dir : orbitDots .keySet()) {
                OrbitState orbit = orbitDots .get( dir );
                int x = LEFT + (int) Math .round( orbit.dotX * width );
                // flip right/left if necessary
                if ( corner == right )
                    x = corner - (int) Math .round( orbit.dotX * width );
                int y = bottom - (int) Math .round( orbit.dotY * height );
                // now store the int coords for later picking
                orbit.dotXint = x;
                orbit.dotYint = y;
                g2d .setPaint( orbit.color );
                g2d .fillOval( orbit.dotXint-RADIUS, orbit.dotYint-RADIUS, DIAM, DIAM );
                g2d .setPaint( Color.black );
                g2d .drawOval( orbit.dotXint-RADIUS, orbit.dotYint-RADIUS, DIAM, DIAM );
                if ( super .propertyIsTrue( "orbitEnabled." + dir ) ) {
                    g2d .setPaint( Color.black );
                    g2d .fillOval( orbit.dotXint-INNER_RADIUS, orbit.dotYint-INNER_RADIUS, INNER_RADIUS*2, INNER_RADIUS*2 );
                }
                if ( showLastOrbit && dir .equals( super .getProperty( "selectedOrbit" ) ) )
                {
                    g2d .setPaint( Color.black );
                    g2d .drawOval( orbit.dotXint-OUTER_RADIUS, orbit.dotYint-OUTER_RADIUS, OUTER_RADIUS*2, OUTER_RADIUS*2 );
                }
            }

            g2d.dispose(); //clean up
        }
    }

    String pickDirection( MouseEvent click )
    {
        double minDist = 999d;
        String pickedDir = null;
        for (String dir : orbitDots .keySet()) {
            OrbitState orbit = orbitDots .get( dir );
            double dist = Math.sqrt( Math.pow( click.getX()-orbit.dotXint, 2 ) + Math.pow( click.getY()-orbit.dotYint, 2 ) );
            if ( dist < (double) RADIUS*4 )
            {
                if ( dist < minDist ) {
                    minDist = dist;
                    pickedDir = dir;
                }
            }
        }
        return pickedDir;
    }


    @Override
    public MouseTool getMouseTool()
    {
        return this .mouseTool;
    }

    @Override
    public boolean[] enableContextualCommands( String[] menu, MouseEvent e )
    {
        boolean[] result = new boolean[menu.length];
        for ( int i = 0; i < menu.length; i++ ) {
            String menuItem = menu[i];
            switch ( menuItem ) {

            case "rZomeOrbits":
            case "predefinedOrbits":
            case "setAllDirections":
            case "usedOrbits":
            case "configureDirections":
                result[i] = true;
                break;

            default:
                result[i] = false;
            }
        }
        return result;
    }
}
