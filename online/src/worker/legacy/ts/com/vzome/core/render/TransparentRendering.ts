/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.render {
    export class TransparentRendering implements com.vzome.core.render.RenderingChanges {
        /*private*/ mRealOne: com.vzome.core.render.RenderingChanges;

        public constructor(realOne: com.vzome.core.render.RenderingChanges) {
            if (this.mRealOne === undefined) { this.mRealOne = null; }
            this.mRealOne = realOne;
        }

        /**
         * 
         */
        public reset() {
            this.mRealOne.reset();
        }

        /**
         * 
         * @param {com.vzome.core.render.RenderedManifestation} manifestation
         */
        public manifestationAdded(manifestation: com.vzome.core.render.RenderedManifestation) {
            manifestation.setTransparency(0.5);
            manifestation.setPickable(false);
            this.mRealOne.manifestationAdded(manifestation);
        }

        /**
         * 
         * @param {com.vzome.core.render.RenderedManifestation} manifestation
         */
        public manifestationRemoved(manifestation: com.vzome.core.render.RenderedManifestation) {
            this.mRealOne.manifestationRemoved(manifestation);
        }

        /**
         * 
         * @param {com.vzome.core.render.RenderedManifestation} manifestation
         */
        public glowChanged(manifestation: com.vzome.core.render.RenderedManifestation) {
            this.mRealOne.glowChanged(manifestation);
        }

        /**
         * 
         * @param {com.vzome.core.render.RenderedManifestation} manifestation
         */
        public labelChanged(manifestation: com.vzome.core.render.RenderedManifestation) {
            this.mRealOne.labelChanged(manifestation);
        }

        /**
         * 
         * @param {com.vzome.core.render.RenderedManifestation} manifestation
         */
        public colorChanged(manifestation: com.vzome.core.render.RenderedManifestation) {
            this.mRealOne.colorChanged(manifestation);
        }

        /**
         * 
         * @param {com.vzome.core.render.RenderedManifestation} manifestation
         */
        public locationChanged(manifestation: com.vzome.core.render.RenderedManifestation) {
            this.mRealOne.locationChanged(manifestation);
        }

        /**
         * 
         * @param {com.vzome.core.render.RenderedManifestation} manifestation
         */
        public orientationChanged(manifestation: com.vzome.core.render.RenderedManifestation) {
            this.mRealOne.orientationChanged(manifestation);
        }

        /**
         * 
         * @param {com.vzome.core.render.RenderedManifestation} manifestation
         */
        public shapeChanged(manifestation: com.vzome.core.render.RenderedManifestation) {
            this.mRealOne.shapeChanged(manifestation);
        }

        /**
         * 
         * @param {com.vzome.core.render.RenderedManifestation} from
         * @param {com.vzome.core.render.RenderedManifestation} to
         */
        public manifestationSwitched(from: com.vzome.core.render.RenderedManifestation, to: com.vzome.core.render.RenderedManifestation) {
            throw new java.lang.IllegalStateException();
        }

        /**
         * 
         * @param {*} shapes
         * @return {boolean}
         */
        public shapesChanged(shapes: com.vzome.core.editor.api.Shapes): boolean {
            return this.mRealOne.shapesChanged(shapes);
        }
    }
    TransparentRendering["__class"] = "com.vzome.core.render.TransparentRendering";
    TransparentRendering["__interfaces"] = ["com.vzome.core.render.RenderingChanges"];


}

