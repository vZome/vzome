package com.vzome.desktop.awt;

import java.awt.event.MouseWheelEvent;

import org.vorthmann.j3d.MouseToolDefault;

import com.vzome.desktop.controller.CameraController;

public class CameraZoomWheel extends MouseToolDefault
{
    private CameraController controller;

    public CameraZoomWheel( CameraController controller )
    {
        super();
        this.controller = controller;
    }

    @Override
    public void mouseWheelMoved( MouseWheelEvent e )
    {
        int amt = e .getWheelRotation();
        // TODO: try to eliminate this class, since it should be anonymous.
        //   See the usage in StrutBuilderController, which seems suspect
        this .controller .adjustZoom( amt );
    }
}
