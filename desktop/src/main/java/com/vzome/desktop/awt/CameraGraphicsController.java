package com.vzome.desktop.awt;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import org.vorthmann.j3d.MouseTool;

import com.vzome.core.viewing.Camera;
import com.vzome.core.viewing.Lights;
import com.vzome.desktop.controller.CameraController;

public class CameraGraphicsController extends CameraController implements GraphicsController
{
    private final double speed;

    public CameraGraphicsController( Camera init, Lights sceneLighting, int maxOrientations, double speed )
    {
        super( init, sceneLighting, maxOrientations );
        this.speed = speed;
    }

    @Override
    public void repaintGraphics( String panelName, Graphics graphics, Dimension size ) {}

    @Override
    public boolean[] enableContextualCommands( String[] menu, MouseEvent e )
    {
        return null;
    }

    @Override
    public MouseTool getMouseTool()
    {
        return null;
    }

    @Override
    public void attachViewer( GraphicsViewer viewer, Component canvas )
    {
        MouseTool trackball = new CameraTrackball( this, this.speed );
        
        // cannot use MouseTool .attach(), because it attaches a useless wheel listener,
        //  and CameraControlPanel will attach a better one to the parent component 
        canvas .addMouseListener( trackball );
        canvas .addMouseMotionListener( trackball );

        this .addViewer( new TrackballRenderingViewer( (RenderingViewer) viewer ) );
    }
}
