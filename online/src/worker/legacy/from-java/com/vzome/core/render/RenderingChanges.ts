import { Shapes } from "../editor/api/Shapes.js";
import { RenderedManifestation } from "./RenderedManifestation.js";

export interface RenderingChanges {
    reset();

    manifestationAdded(manifestation: RenderedManifestation);

    manifestationRemoved(manifestation: RenderedManifestation);

    /**
     * Given two RMs that both render the same underlying Manifestation,
     * switch the associated graphics object's userData.
     * @param {RenderedManifestation} from
     * @param {RenderedManifestation} to
     */
    manifestationSwitched(from: RenderedManifestation, to: RenderedManifestation);

    glowChanged(manifestation: RenderedManifestation);

    colorChanged(manifestation: RenderedManifestation);

    locationChanged(manifestation: RenderedManifestation);

    orientationChanged(manifestation: RenderedManifestation);

    shapeChanged(manifestation: RenderedManifestation);

    labelChanged(manifestation: RenderedManifestation);

    /**
     * Change shapes all at once, if supported.
     * @param {*} shapes
     * @return {boolean} true if the rendering mechanism can support this
     */
    shapesChanged(shapes: Shapes): boolean;
}
