

package org.vorthmann.j3d;

import java.awt.Component;
import java.awt.event.MouseWheelEvent;

import javax.swing.event.MouseInputAdapter;

/**
 * @author Scott Vorthmann
 *
 */
public class MouseToolDefault extends MouseInputAdapter implements MouseTool
{
    @Override
    public void attach( Component canvas )
    {
        canvas .addMouseListener( this );
        canvas .addMouseMotionListener( this );
        canvas .addMouseWheelListener( this );
    }
    
    @Override
    public void detach( Component canvas )
    {
        canvas .removeMouseListener( this );
        canvas .removeMouseMotionListener( this );
        canvas .removeMouseWheelListener( this );
    }

    @Override
    public void mouseWheelMoved( MouseWheelEvent arg0 )
    {}

}
