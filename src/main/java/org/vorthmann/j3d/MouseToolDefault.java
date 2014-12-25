

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
    public void attach( Component canvas )
    {
        canvas .addMouseListener( this );
        canvas .addMouseMotionListener( this );
        canvas .addMouseWheelListener( this );
    }
    
    public void detach( Component canvas )
    {
        canvas .removeMouseListener( this );
        canvas .removeMouseMotionListener( this );
        canvas .removeMouseWheelListener( this );
    }

    public void mouseWheelMoved( MouseWheelEvent arg0 )
    {}

}
