
package com.vzome.core.viewing;

import com.vzome.core.render.RenderedModel;

public interface ThumbnailRenderer
{
    public interface Listener
    {
        void thumbnailReady( Object thumbnail );
    }

    public abstract void captureSnapshot( RenderedModel snapshot,
            Camera view, int maxSize, Listener callback );

}
