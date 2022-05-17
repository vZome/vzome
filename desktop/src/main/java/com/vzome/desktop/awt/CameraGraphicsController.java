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
    public CameraGraphicsController( Camera init, Lights sceneLighting, int maxOrientations )
    {
        super( init, sceneLighting, maxOrientations );
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
    public void attachViewer( RenderingViewer viewer, Component canvas )
    {
        MouseTool trackball = new CameraTrackball( this, 0.04d );
        
        // cannot use MouseTool .attach(), because it attaches a useless wheel listener,
        //  and CameraControlPanel will attach a better one to the parent component 
        canvas .addMouseListener( trackball );
        canvas .addMouseMotionListener( trackball );

        this .addViewer( new TrackballRenderingViewer( viewer ) );
    }
}
