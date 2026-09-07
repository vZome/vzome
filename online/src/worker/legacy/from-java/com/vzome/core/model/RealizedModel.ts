import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { AlgebraicField } from "../algebra/AlgebraicField.js";
import { Color } from "../construction/Color.js";
import { Construction } from "../construction/Construction.js";
import { Manifestation } from "./Manifestation.js";

export interface RealizedModel extends java.lang.Iterable<Manifestation> {
    getField(): AlgebraicField;

    findConstruction(c: Construction): Manifestation;

    removeConstruction(c: Construction): Manifestation;

    getManifestation(c: Construction): Manifestation;

    size(): number;

    show(mManifestation: Manifestation);

    hide(mManifestation: Manifestation);

    add(m: Manifestation);

    remove(mManifestation: Manifestation);

    setColor(manifestation: Manifestation, color: Color);

    setLabel(m: Manifestation, label: string);

    findPerEditManifestation(signature: string): Manifestation;

    addPerEditManifestation(signature: string, m: Manifestation);

    clearPerEditManifestations();
}
