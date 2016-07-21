/*
 * Created on Jun 30, 2003
 */
package com.vzome.core.render;


public interface RenderingChanges {
    
    void reset();

	void manifestationAdded( RenderedManifestation manifestation );

    void manifestationRemoved( RenderedManifestation manifestation );

    void manifestationSwitched( RenderedManifestation from, RenderedManifestation to );

    void glowChanged( RenderedManifestation manifestation );

    void colorChanged( RenderedManifestation manifestation );

    void locationChanged( RenderedManifestation manifestation );

    void orientationChanged( RenderedManifestation manifestation );

    void shapeChanged( RenderedManifestation manifestation );

    /**
    * @deprecated As of 7/20/2016: Use controller property "showFrameLabels" instead.
    */
    @Deprecated
    default void enableFrameLabels(){
        throw new IllegalStateException( "enableFrameLabels is deprecated." );
    }

    /**
    * @deprecated As of 7/20/2016: Use controller property "showFrameLabels" instead.
    */
    @Deprecated
    default void disableFrameLabels(){
        throw new IllegalStateException( "disableFrameLabels is deprecated." );
    }
}
