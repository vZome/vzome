
//(c) Copyright 2011, Scott Vorthmann.

package org.vorthmann.j3d;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class MouseToolFilter extends MouseToolDefault
{
    private MouseTool delegate;
    
    public MouseToolFilter( MouseTool delegate )
    {
        this .delegate = delegate;
    }

    public void mouseWheelMoved( MouseWheelEvent arg0 )
    {
        delegate .mouseWheelMoved( arg0 );
    }

    public void mouseClicked( MouseEvent e )
    {
        delegate .mouseClicked( e );
    }

    public void mouseDragged( MouseEvent e )
    {
        delegate .mouseDragged( e );
    }

    public void mouseEntered( MouseEvent e )
    {
        delegate .mouseEntered( e );
    }

    public void mouseExited( MouseEvent e )
    {
        delegate .mouseExited( e );
    }

    public void mouseMoved( MouseEvent e )
    {
        delegate .mouseMoved( e );
    }

    public void mousePressed( MouseEvent e )
    {
        delegate .mousePressed( e );
    }

    public void mouseReleased( MouseEvent e )
    {
        delegate .mouseReleased( e );
    }

}
