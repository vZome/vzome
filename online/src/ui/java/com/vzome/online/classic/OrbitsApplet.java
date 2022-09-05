
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
        this .enabledOrbits = new OrbitSetGraphicsController( new DefaultController(), false );
        JPanel orbitsPanel = new JPanel()
        {
            public void paint( Graphics graphics )
            {
                Dimension size = getSize();
                enabledOrbits .repaintGraphics( "orbits", graphics, getSize() );
                graphics .drawString( "HELLO!", 100, 100 );
                graphics .drawString( "HELLO!", 100, 200 );
                graphics .drawString( "HELLO!", 200, 100 );
                graphics .drawString( "HELLO!", 200, 200 );
            }
        };
        this.add( orbitsPanel );
    }
}