
package com.vzome.online.classic;

import java.awt.Graphics;
import java.awt.Dimension;

import javax.swing.JApplet;
import javax.swing.JPanel;

import com.vzome.desktop.awt.GraphicsController;
import com.vzome.desktop.awt.OrbitSetGraphicsController;
import com.vzome.desktop.controller.DefaultController;

public class OrbitsApplet extends JApplet
{
    private GraphicsController enabledOrbits;

    public void init()
    {
        DefaultController controller = new DefaultController()
        {
            public String[] getCommandList( String listName )
            {
                if ( "allOrbits" .equals( listName ) )
                    return new String[]{ "red", "yellow", "blue" };
                else
                    return super .getCommandList( listName );
            }

            public String getProperty( String name )
            {
                switch ( name ) {

                    case "orbitDot.red":
                        return "0xaf0000/1.0/0.0";
                    case "orbitDot.yellow":
                        return "0xf0a000/0.0/1.0";
                    case "orbitDot.blue":
                        return "0x7695/0.0/0.0";

                    default:
                        return super .getProperty( name );
                }
            }

            protected void doAction( String action ) throws Exception
            {
            }
        };
        this .enabledOrbits = new OrbitSetGraphicsController( controller, false );
        this .enabledOrbits .actionPerformed( this, "refreshDots" );
        Dimension size = this .getSize();
        JPanel orbitsPanel = new JPanel()
        {
            public void paint( Graphics graphics )
            {
                enabledOrbits .repaintGraphics( "orbits", graphics, size );
            }
        };
        this.add( orbitsPanel );

        // shouldn't need to do this
        orbitsPanel .setSize( this .getSize() );
    }
}