
package com.vzome.online.classic;

import java.awt.Graphics;
import java.awt.Dimension;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JApplet;
import javax.swing.JPanel;

import com.vzome.desktop.awt.GraphicsController;
import com.vzome.desktop.awt.OrbitSetGraphicsController;
import com.vzome.desktop.api.Controller;

/**
 * Having failed with OrbitPanelFrame.java, this is my attempt to replicate
 * just enough of OrbitPanel to render the triangle and dots.  This clearly
 * represents too much custom development in Java (JSweet), and is not an
 * attractive option for implementing either a classic or a fresh editor UI.
 * It is more interesting as an exercise in using the WorkerController
 * that is injected into it.
 */
public class OrbitsApplet extends JApplet implements PropertyChangeListener
{
    private GraphicsController enabledOrbits;
    private JPanel orbitsPanel;

    public void setController( Controller controller )
    {
        this .enabledOrbits = new OrbitSetGraphicsController( controller, false );
        this .enabledOrbits .addPropertyListener( this );
        this .enabledOrbits .actionPerformed( this, "refreshDots" );
        Dimension size = this .getSize();
        this .orbitsPanel = new JPanel()
        {
            public void paint( Graphics graphics )
            {
                enabledOrbits .repaintGraphics( "orbits", graphics, size );
            }
        };
        this.add( this .orbitsPanel );

        // shouldn't need to do this
        orbitsPanel .setSize( this .getSize() );
        this .enabledOrbits .getProperty( "oneAtATime" ); // trigger async fetch
    }

    public void propertyChange( PropertyChangeEvent event )
	{
        if ( "orbits" .equals( event .getPropertyName() ) ) {
            this .enabledOrbits .actionPerformed( this, "refreshDots" );
            this .enabledOrbits .getProperty( "oneAtATime" ); // trigger async fetch
        }
        if ( "oneAtATime" .equals( event .getPropertyName() ) ) {
            this .orbitsPanel .repaint();
        }
    }
}