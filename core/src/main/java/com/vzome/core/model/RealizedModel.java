package com.vzome.core.model;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.construction.Color;
import com.vzome.core.construction.Construction;

public interface RealizedModel extends Iterable<Manifestation>
{
    AlgebraicField getField();

    Manifestation findConstruction( Construction c );

    Manifestation removeConstruction( Construction c );

    // only for SelectManifestation
    Manifestation getManifestation( Construction c );

    int size();

    void show( Manifestation mManifestation );

    void hide( Manifestation mManifestation );

    void add( Manifestation m );

    void remove( Manifestation mManifestation );

    void setColor( Manifestation manifestation, Color color );
    
    // new for better performance in Javascript

    Manifestation findPerEditManifestation( String signature );

    void addPerEditManifestation( String signature, Manifestation m );

    void clearPerEditManifestations();
}
