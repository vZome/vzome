/*
 * Created on Jun 30, 2003
 */
package com.vzome.core.render;


public interface RenderingChanges {
    
    void reset();

	void manifestationAdded( RenderedManifestation manifestation );

    void manifestationRemoved( RenderedManifestation manifestation );

    /**
     * Given two RMs that both render the same underlying Manifestation,
     * switch the associated graphics object's userData.
     * @param from
     * @param to
     */
    void manifestationSwitched( RenderedManifestation from, RenderedManifestation to );

    void glowChanged( RenderedManifestation manifestation );

    void colorChanged( RenderedManifestation manifestation );

    void locationChanged( RenderedManifestation manifestation );

    void orientationChanged( RenderedManifestation manifestation );

    void shapeChanged( RenderedManifestation manifestation );
}
