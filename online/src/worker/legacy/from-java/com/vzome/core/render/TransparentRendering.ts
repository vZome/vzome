import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Shapes } from "../editor/api/Shapes.js";
import { RenderedManifestation } from "./RenderedManifestation.js";
import { RenderingChanges } from "./RenderingChanges.js";

export class TransparentRendering implements RenderingChanges {
    /*private*/ mRealOne: RenderingChanges;

    public constructor(realOne: RenderingChanges) {
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
     * @param {RenderedManifestation} manifestation
     */
    public manifestationAdded(manifestation: RenderedManifestation) {
        manifestation.setTransparency(0.5);
        manifestation.setPickable(false);
        this.mRealOne.manifestationAdded(manifestation);
    }

    /**
     * 
     * @param {RenderedManifestation} manifestation
     */
    public manifestationRemoved(manifestation: RenderedManifestation) {
        this.mRealOne.manifestationRemoved(manifestation);
    }

    /**
     * 
     * @param {RenderedManifestation} manifestation
     */
    public glowChanged(manifestation: RenderedManifestation) {
        this.mRealOne.glowChanged(manifestation);
    }

    /**
     * 
     * @param {RenderedManifestation} manifestation
     */
    public labelChanged(manifestation: RenderedManifestation) {
        this.mRealOne.labelChanged(manifestation);
    }

    /**
     * 
     * @param {RenderedManifestation} manifestation
     */
    public colorChanged(manifestation: RenderedManifestation) {
        this.mRealOne.colorChanged(manifestation);
    }

    /**
     * 
     * @param {RenderedManifestation} manifestation
     */
    public locationChanged(manifestation: RenderedManifestation) {
        this.mRealOne.locationChanged(manifestation);
    }

    /**
     * 
     * @param {RenderedManifestation} manifestation
     */
    public orientationChanged(manifestation: RenderedManifestation) {
        this.mRealOne.orientationChanged(manifestation);
    }

    /**
     * 
     * @param {RenderedManifestation} manifestation
     */
    public shapeChanged(manifestation: RenderedManifestation) {
        this.mRealOne.shapeChanged(manifestation);
    }

    /**
     * 
     * @param {RenderedManifestation} from
     * @param {RenderedManifestation} to
     */
    public manifestationSwitched(from: RenderedManifestation, to: RenderedManifestation) {
        throw new java.lang.IllegalStateException();
    }

    /**
     * 
     * @param {*} shapes
     * @return {boolean}
     */
    public shapesChanged(shapes: Shapes): boolean {
        return this.mRealOne.shapesChanged(shapes);
    }
}
TransparentRendering["__class"] = "com.vzome.core.render.TransparentRendering";
TransparentRendering["__interfaces"] = ["com.vzome.core.render.RenderingChanges"];
