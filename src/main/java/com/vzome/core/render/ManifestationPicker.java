
package com.vzome.core.render;

import java.awt.event.MouseEvent;
import java.util.Collection;

public interface ManifestationPicker
{
    public RenderedManifestation pickManifestation( MouseEvent e );
    
    public Collection<RenderedManifestation> pickCube();
}
