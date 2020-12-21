package com.vzome.jsweet;

import com.vzome.core.editor.api.OrbitSource;
import com.vzome.core.math.symmetry.OrbitSet;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;

public class JsOrbitSetField implements OrbitSet.Field
{
    private final OrbitSource orbitSource;

    // TODO: fix this hard-wiring to a single orbitSource!
    
    public JsOrbitSetField( OrbitSource orbitSource )
    {
        super();
        this.orbitSource = orbitSource;
    }

    @Override
    public OrbitSet getGroup( String name )
    {
        return this.orbitSource.getOrbits();
    }

    @Override
    public QuaternionicSymmetry getQuaternionSet( String name )
     {
        throw new RuntimeException( "unimplemented" );
    }

}
