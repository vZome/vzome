

package org.vorthmann.j3d;

import java.awt.Component;
import java.awt.event.MouseWheelListener;

import javax.swing.event.MouseInputListener;

/**
 * A combination of AWT listeners.
 */
public interface MouseTool extends MouseInputListener, MouseWheelListener
{
    void attach( Component canvas );
    
    void detach( Component canvas );
}
