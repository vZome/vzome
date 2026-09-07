import { Manifestation } from "./Manifestation.js";

export interface Exporter {
    exportManifestation(man: Manifestation);

    finish();
}
