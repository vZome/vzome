/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.render {
    export interface RenderingChanges {
        reset();

        manifestationAdded(manifestation: com.vzome.core.render.RenderedManifestation);

        manifestationRemoved(manifestation: com.vzome.core.render.RenderedManifestation);

        /**
         * Given two RMs that both render the same underlying Manifestation,
         * switch the associated graphics object's userData.
         * @param {com.vzome.core.render.RenderedManifestation} from
         * @param {com.vzome.core.render.RenderedManifestation} to
         */
        manifestationSwitched(from: com.vzome.core.render.RenderedManifestation, to: com.vzome.core.render.RenderedManifestation);

        glowChanged(manifestation: com.vzome.core.render.RenderedManifestation);

        colorChanged(manifestation: com.vzome.core.render.RenderedManifestation);

        locationChanged(manifestation: com.vzome.core.render.RenderedManifestation);

        orientationChanged(manifestation: com.vzome.core.render.RenderedManifestation);

        shapeChanged(manifestation: com.vzome.core.render.RenderedManifestation);

        labelChanged(manifestation: com.vzome.core.render.RenderedManifestation);

        /**
         * Change shapes all at once, if supported.
         * @param {*} shapes
         * @return {boolean} true if the rendering mechanism can support this
         */
        shapesChanged(shapes: com.vzome.core.editor.api.Shapes): boolean;
    }
}

