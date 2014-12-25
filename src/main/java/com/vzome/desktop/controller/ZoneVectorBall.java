
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.desktop.controller;

import javax.vecmath.Matrix4d;
import javax.vecmath.Quat4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.OrbitSet;

/**
 * Transducer: turns trackball roll events into zone (Axis) change events.
 * 
 * @author Scott Vorthmann
 *
 */
public abstract class ZoneVectorBall
{
    private final ViewPlatformModel mViewPlatform;
    
    private OrbitSet orbits;
    
    private Vector3d zoneVector3d;

    private Axis zone = null;

    public ZoneVectorBall( ViewPlatformModel viewModel )
    {
        mViewPlatform = viewModel;
    }
    
    public Axis setOrbits( OrbitSet orbits )
    {
        this .orbits = orbits;
        return resetToZ();
    }
    
    public Axis getZone()
    {
        return zone;
    }

    public Axis resetToZ()
    {
        Vector3f Z = new Vector3f( 0f, 0f, 1f );
        mViewPlatform .mapViewToWorld( Z );
        zoneVector3d = new Vector3d( Z.x, Z.y, Z.z );
        mapVectorToAxis();
        return zone;
    }

    public void trackballRolled( Quat4d roll )
    {
        mViewPlatform .getWorldRotation( roll );
        Matrix4d rollM = new Matrix4d();
        rollM.set( roll );
        // roll is now a rotation in world coordinates
        rollM.transform( zoneVector3d );
        mapVectorToAxis();
    }
    
    /**
     * This is used when we're doing some non-trackball drag
     * to define a new vector, as for the working plane.
     * @param vector
     */
    public void setVector( Vector3d vector )
    {
    	zoneVector3d = vector;
        mapVectorToAxis();
    }

    private void mapVectorToAxis()
    {
        RealVector vector = new RealVector( zoneVector3d.x, zoneVector3d.y, zoneVector3d.z );
        Axis oldAxis = zone;
        zone = orbits .getAxis( vector );
        if ( zone == null && oldAxis == null )
            return;
        if ( zone != null && zone .equals(  oldAxis ) )
            return;
        zoneChanged( oldAxis, zone );
    }
    
    protected abstract void zoneChanged( Axis oldZone, Axis newZone );
}
