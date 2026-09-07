import { Color } from "../construction/Color.js";
import { Manifestation } from "./Manifestation.js";

export interface ManifestationChanges {
    manifestationAdded(m: Manifestation);

    manifestationRemoved(m: Manifestation);

    manifestationColored(m: Manifestation, color: Color);

    manifestationLabeled(m: Manifestation, label: string);
}
