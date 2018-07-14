
package org.vorthmann.j3d;

import java.awt.Component;

public interface CanvasTool
{
    void attach( Component canvas );
    
    void detach( Component canvas );
}
