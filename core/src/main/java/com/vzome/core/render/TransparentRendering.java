
package com.vzome.core.render;

import com.vzome.core.editor.api.Shapes;

public class TransparentRendering implements RenderingChanges
{
    private final RenderingChanges mRealOne;
    
    public TransparentRendering( RenderingChanges realOne )
    {
        mRealOne = realOne;
    }
    
    @Override
    public void reset()
    {
        mRealOne .reset();
    }

    @Override
    public void manifestationAdded( RenderedManifestation manifestation )
    {
        manifestation .setTransparency( 0.50f );
        manifestation .setPickable( false );
        mRealOne .manifestationAdded( manifestation );
    }

    @Override
    public void manifestationRemoved( RenderedManifestation manifestation )
    {
        mRealOne .manifestationRemoved( manifestation );
    }

    @Override
    public void glowChanged( RenderedManifestation manifestation )
    {
        mRealOne .glowChanged( manifestation );
    }

    @Override
    public void labelChanged( RenderedManifestation manifestation )
    {
        mRealOne .labelChanged( manifestation );
    }

    @Override
    public void colorChanged( RenderedManifestation manifestation )
    {
        mRealOne .colorChanged( manifestation );
    }

    @Override
    public void locationChanged( RenderedManifestation manifestation )
    {
        mRealOne .locationChanged( manifestation );
    }

    @Override
    public void orientationChanged( RenderedManifestation manifestation )
    {
        mRealOne .orientationChanged( manifestation );
    }

    @Override
    public void shapeChanged( RenderedManifestation manifestation )
    {
        mRealOne .shapeChanged( manifestation );
    }

    @Override
    public void manifestationSwitched( RenderedManifestation from,
            RenderedManifestation to )
    {
        throw new IllegalStateException();
    }

    @Override
    public boolean shapesChanged( Shapes shapes )
    {
        return this .mRealOne .shapesChanged( shapes );
    }

}
