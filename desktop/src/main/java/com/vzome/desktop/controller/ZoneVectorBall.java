
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.desktop.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
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
    private final CameraController mViewPlatform;
    
    private OrbitSet orbits;
    
    private Vector3f zoneVector3d;

    private Axis zone = null;

    private static Logger logger = Logger .getLogger( "com.vzome.desktop.controller.ZoneVectorBall" );

    public ZoneVectorBall( CameraController viewModel )
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
        zoneVector3d = new Vector3f( Z.x, Z.y, Z.z );
        mapVectorToAxis();
        return zone;
    }

    public void trackballRolled( Quat4f roll )
    {
        mViewPlatform .getWorldRotation( roll );
        Matrix4f rollM = new Matrix4f();
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
    public void setVector( Vector3f vector )
    {
        zoneVector3d = vector;
        mapVectorToAxis();
    }

    private void mapVectorToAxis()
    {
        RealVector vector = new RealVector( zoneVector3d.x, zoneVector3d.y, zoneVector3d.z );
        Axis oldAxis = zone;
        zone = orbits .getAxis( vector );
        if ( zone == null && oldAxis == null ) {
            if ( logger .isLoggable( Level.FINER ) )
                logger .finer( "mapVectorToAxis null zone for " + vector );
            return;
        }
        if ( zone != null && zone .equals(  oldAxis ) ) {
            if ( logger .isLoggable( Level.FINER ) )
                logger .finer( "mapVectorToAxis  zone " + zone + " unchanged for " + vector );
            return;
        }
		if ( logger .isLoggable( Level.FINER ) )
			logger .finer( "preview finished at  " + zone + " for " + vector );
        zoneChanged( oldAxis, zone );
    }
    
    protected abstract void zoneChanged( Axis oldZone, Axis newZone );
}
