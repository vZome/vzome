/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.model {
    export interface RealizedModel extends java.lang.Iterable<com.vzome.core.model.Manifestation> {
        getField(): com.vzome.core.algebra.AlgebraicField;

        findConstruction(c: com.vzome.core.construction.Construction): com.vzome.core.model.Manifestation;

        removeConstruction(c: com.vzome.core.construction.Construction): com.vzome.core.model.Manifestation;

        getManifestation(c: com.vzome.core.construction.Construction): com.vzome.core.model.Manifestation;

        size(): number;

        show(mManifestation: com.vzome.core.model.Manifestation);

        hide(mManifestation: com.vzome.core.model.Manifestation);

        add(m: com.vzome.core.model.Manifestation);

        remove(mManifestation: com.vzome.core.model.Manifestation);

        setColor(manifestation: com.vzome.core.model.Manifestation, color: com.vzome.core.construction.Color);

        setLabel(m: com.vzome.core.model.Manifestation, label: string);

        findPerEditManifestation(signature: string): com.vzome.core.model.Manifestation;

        addPerEditManifestation(signature: string, m: com.vzome.core.model.Manifestation);

        clearPerEditManifestations();
    }
}

