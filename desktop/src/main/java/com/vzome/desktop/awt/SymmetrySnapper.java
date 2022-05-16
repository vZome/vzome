
package com.vzome.desktop.awt;

import javax.vecmath.Vector3f;

import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Embedding;
import com.vzome.core.math.symmetry.OrbitSet;

public class SymmetrySnapper implements CameraController.Snapper
{
    private final OrbitSet orbitSet;
    
    public SymmetrySnapper( OrbitSet orbitSet )
    {
        this.orbitSet = orbitSet;
    }

    @Override
    public void snapDirections( Vector3f lookDir, Vector3f upDir )
    {
        RealVector vector = new RealVector( lookDir .x, lookDir .y, lookDir .z );
        Axis axis = orbitSet .getAxis( vector );
        if ( axis == null )
            return;
        Embedding embedding = orbitSet .getSymmetry();
        RealVector rv = embedding .embedInR3( axis .normal() );
        lookDir .set( rv.x, rv.y, rv.z );
        vector = new RealVector( upDir .x, upDir .y, upDir .z );
        axis = orbitSet .getAxis( vector );
        rv = embedding .embedInR3( axis .normal() );
        upDir .set( rv.x, rv.y, rv.z );
        
        Vector3f cross = new Vector3f();
        cross .cross( lookDir, upDir );
        upDir .cross( cross, lookDir );
    }

}
