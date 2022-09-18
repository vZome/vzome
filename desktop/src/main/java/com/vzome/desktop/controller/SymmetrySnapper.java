
package com.vzome.desktop.controller;

import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Embedding;
import com.vzome.core.math.symmetry.OrbitSet;

public class SymmetrySnapper implements OrbitSnapper
{
    private final OrbitSet orbitSet;
    private final Embedding embedding;
    
    public SymmetrySnapper( OrbitSet orbitSet )
    {
        this.orbitSet = orbitSet;
        this.embedding = orbitSet .getSymmetry();;
    }

    @Override
    public RealVector snapZ( RealVector zIn )
    {
        Axis lookZone = orbitSet .getAxis( zIn );
        if ( lookZone == null )
            return zIn;
        return embedding .embedInR3( lookZone .normal() );
    }

    @Override
    public RealVector snapY( RealVector zOut, RealVector yIn )
    {
        Axis upZone = orbitSet .getAxis( yIn );
        if ( upZone == null )
            return yIn;
        yIn = embedding .embedInR3( upZone .normal() );
        
        RealVector left = zOut .cross( yIn );
        return left .cross( zOut );
    }

}
