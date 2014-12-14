//(c) Copyright 2011, Scott Vorthmann.

package com.vzome.core.viewing;

import com.vzome.core.render.RenderedModel;

public interface ThumbnailRenderer
{
    public interface Listener
    {
        void thumbnailReady( Object thumbnail );
    }

    public abstract void captureSnapshot( RenderedModel snapshot,
            ViewModel view, int maxSize, Listener callback );

}