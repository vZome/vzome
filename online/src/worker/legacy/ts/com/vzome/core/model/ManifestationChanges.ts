/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.model {
    export interface ManifestationChanges {
        manifestationAdded(m: com.vzome.core.model.Manifestation);

        manifestationRemoved(m: com.vzome.core.model.Manifestation);

        manifestationColored(m: com.vzome.core.model.Manifestation, color: com.vzome.core.construction.Color);

        manifestationLabeled(m: com.vzome.core.model.Manifestation, label: string);
    }
}

