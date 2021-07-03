
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

    @Override
    public void mouseWheelMoved( MouseWheelEvent arg0 )
    {
        delegate .mouseWheelMoved( arg0 );
    }

    @Override
    public void mouseClicked( MouseEvent e )
    {
        delegate .mouseClicked( e );
    }

    @Override
    public void mouseDragged( MouseEvent e )
    {
        delegate .mouseDragged( e );
    }

    @Override
    public void mouseEntered( MouseEvent e )
    {
        delegate .mouseEntered( e );
    }

    @Override
    public void mouseExited( MouseEvent e )
    {
        delegate .mouseExited( e );
    }

    @Override
    public void mouseMoved( MouseEvent e )
    {
        delegate .mouseMoved( e );
    }

    @Override
    public void mousePressed( MouseEvent e )
    {
        delegate .mousePressed( e );
    }

    @Override
    public void mouseReleased( MouseEvent e )
    {
        delegate .mouseReleased( e );
    }

}
