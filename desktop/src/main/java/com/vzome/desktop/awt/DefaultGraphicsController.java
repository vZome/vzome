package com.vzome.desktop.awt;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import org.vorthmann.j3d.MouseTool;

import com.vzome.desktop.controller.DefaultController;

public class DefaultGraphicsController extends DefaultController implements GraphicsController
{
    @Override
    public boolean[] enableContextualCommands( String[] menu, MouseEvent e )
    {
        if ( mNextController != null )
            return ((DefaultGraphicsController) mNextController) .enableContextualCommands( menu, e );
        else
            return new boolean[0];
    }

    @Override
    public void repaintGraphics( String panelName, Graphics graphics, Dimension size )
    {
        if ( mNextController != null )
            ((DefaultGraphicsController) mNextController) .repaintGraphics( panelName, graphics, size );
    }

    @Override
    public MouseTool getMouseTool()
    {
        return null;
    }
}
