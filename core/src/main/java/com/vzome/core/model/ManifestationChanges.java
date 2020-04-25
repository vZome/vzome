
package com.vzome.core.model;

public interface ManifestationChanges
{
    void manifestationAdded( Manifestation m );
    
    void manifestationRemoved( Manifestation m );
    
    void manifestationColored( Manifestation m, Color color );
}
