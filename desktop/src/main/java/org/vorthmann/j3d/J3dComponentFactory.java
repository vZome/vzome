

package org.vorthmann.j3d;

import com.vzome.core.render.Scene;
import com.vzome.desktop.awt.RenderingViewer;

public interface J3dComponentFactory
{
    /**
     * @param scene
     * @param lightweight true for correct layout but worse frame rate
     * @return
     */
    RenderingViewer createRenderingViewer( Scene scene, boolean lightweight );
}
