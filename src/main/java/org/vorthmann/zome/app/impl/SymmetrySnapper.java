
package org.vorthmann.zome.app.impl;

import javax.vecmath.Vector3f;

import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.OrbitSet;
import com.vzome.desktop.controller.CameraController;

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
        RealVector rv = axis .normal() .toRealVector();
        lookDir .set( (float) rv.x, (float) rv.y, (float) rv.z );
        vector = new RealVector( upDir .x, upDir .y, upDir .z );
        axis = orbitSet .getAxis( vector );
        rv = axis .normal() .toRealVector();
        upDir .set( (float) rv.x, (float) rv.y, (float) rv.z );
        
        Vector3f cross = new Vector3f();
        cross .cross( lookDir, upDir );
        upDir .cross( cross, lookDir );
    }

}
