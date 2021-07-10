package com.vzome.core.editor.api;

import com.vzome.core.math.symmetry.Symmetries4D;

public interface SymmetryAware
{
    OrbitSource getSymmetrySystem();

    OrbitSource getSymmetrySystem( String name );
    
    Symmetries4D get4dSymmetries();
}
