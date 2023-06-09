
package com.vzome.desktop.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

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
    private OrbitSet orbits;
    
    private RealVector zoneVector3d;

    private Axis zone = null;

    private static Logger logger = Logger .getLogger( "com.vzome.desktop.controller.ZoneVectorBall" );
    
    public Axis initializeZone( OrbitSet orbits, RealVector worldEye )
    {
        this .orbits = orbits;
        zoneVector3d = new RealVector( worldEye.x, worldEye.y, worldEye.z );
        mapVectorToAxis( false ); // Just initialize the zone
        return zone;
    }

    public void trackballRolled( RealVector[] rotation )
    {
        // roll is now a rotation in world coordinates
        double x = rotation[ 0 ] .dot( zoneVector3d );
        double y = rotation[ 1 ] .dot( zoneVector3d );
        double z = rotation[ 2 ] .dot( zoneVector3d );
        zoneVector3d = new RealVector( x, y, z );
        mapVectorToAxis( true );
    }
    
    /**
     * This is used when we're doing some non-trackball drag
     * to define a new vector, as for the working plane.
     * @param vector
     */
    public void setVector( RealVector vector )
    {
        zoneVector3d = vector;
        mapVectorToAxis( true );
    }

    private void mapVectorToAxis( boolean notify )
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

		if ( notify )
		    zoneChanged( oldAxis, zone );
    }
    
    protected abstract void zoneChanged( Axis oldZone, Axis newZone );
}
