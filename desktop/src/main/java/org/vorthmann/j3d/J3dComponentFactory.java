

package org.vorthmann.j3d;

import com.vzome.core.render.Scene;
import com.vzome.desktop.awt.RenderingViewer;

public interface J3dComponentFactory
{
    RenderingViewer createRenderingViewer( Scene scene, boolean lightweight );
}
