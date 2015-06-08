
package com.vzome.core.render;


public class TransparentRendering implements RenderingChanges
{
    private final RenderingChanges mRealOne;
    
    public TransparentRendering( RenderingChanges realOne )
    {
        mRealOne = realOne;
    }
    
    public void reset()
    {
        mRealOne .reset();
    }

    public void manifestationAdded( RenderedManifestation manifestation )
    {
        manifestation .setTransparency( 0.99f );
        manifestation .setPickable( false );
        mRealOne .manifestationAdded( manifestation );
    }

    public void manifestationRemoved( RenderedManifestation manifestation )
    {
        mRealOne .manifestationRemoved( manifestation );
    }

    public void glowChanged( RenderedManifestation manifestation )
    {
        mRealOne .glowChanged( manifestation );
    }

    public void colorChanged( RenderedManifestation manifestation )
    {
        mRealOne .colorChanged( manifestation );
    }

    public void locationChanged( RenderedManifestation manifestation )
    {
        mRealOne .locationChanged( manifestation );
    }

    public void orientationChanged( RenderedManifestation manifestation )
    {
        mRealOne .orientationChanged( manifestation );
    }

    public void shapeChanged( RenderedManifestation manifestation )
    {
        mRealOne .shapeChanged( manifestation );
    }

    public void manifestationSwitched( RenderedManifestation from,
            RenderedManifestation to )
    {
        throw new IllegalStateException();
    }

    public void enableFrameLabels() {}

    public void disableFrameLabels() {}
    
}
