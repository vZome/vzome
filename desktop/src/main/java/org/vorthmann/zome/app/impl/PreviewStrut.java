
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package org.vorthmann.zome.app.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Point;
import com.vzome.core.editor.DocumentModel;
import com.vzome.core.edits.StrutCreation;
import com.vzome.core.math.Line;
import com.vzome.core.math.Projection;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.OrbitSet;
import com.vzome.core.math.symmetry.PlaneOrbitSet;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.render.TransparentRendering;
import com.vzome.desktop.controller.CameraController;
import com.vzome.desktop.controller.ZoneVectorBall;

public class PreviewStrut implements PropertyChangeListener
{
    private final RealizedModel model;

    private final RenderedModel rendering;

    private final ZoneVectorBall zoneBall;

    private Point point;

    private Axis zone;

    private SymmetryController symmetryController;

    private LengthController length;

    private StrutCreation strut;

    private double[] workingPlaneDual = null; // a GA homogeneous 4-vector for the dual of the working plane

    private static Logger logger = Logger .getLogger( "org.vorthmann.zome.app.impl.PreviewStrut" );

    public PreviewStrut( AlgebraicField field, RenderingChanges mainScene, CameraController cameraController )
    {
        rendering = new RenderedModel( field, true );
        TransparentRendering transp = new TransparentRendering( mainScene );
        rendering .addListener( transp );
        model = new RealizedModel( field, new Projection.Default( field ) );
        model .addListener( rendering );

        zoneBall = new ZoneVectorBall( cameraController )
        {
            @Override
            protected void zoneChanged( Axis oldZone, Axis newZone )
            {
                if ( length != null )
                    length .removePropertyListener( PreviewStrut .this );
                zone = newZone;
                if ( newZone == null )
                    return;
                length = symmetryController.orbitLengths.get( newZone .getDirection() );
                adjustStrut();
                length .addPropertyListener( PreviewStrut .this );
            }
        };
    }

    public void startRendering( SymmetryController symmetryController, Point point, AlgebraicVector workingPlaneNormal )
    {
        this .point = point;

        setSymmetryController( symmetryController );
        OrbitSet orbits = symmetryController .getBuildOrbits();
        if ( workingPlaneNormal != null )
        {
            orbits = new PlaneOrbitSet( orbits, workingPlaneNormal );

            RealVector normal = workingPlaneNormal .toRealVector();
            RealVector other = new RealVector( 1d, 0d, 0d );
            RealVector v1 = normal .cross( other );
            double len = v1 .length();
            if ( len < 0.0001d )
            {
                other = new RealVector( 0d, 1d, 0d );
                v1 = normal .cross( other );
            }
            RealVector v2 = normal .cross( v1 );

            // This line-plane intersection comes right out of Vince, GA4CG, p. 196,
            // but I had to derive the general forms for the products.
            // This part is just computing the plane trivector, and then its dual vector.

            RealVector p = this.point.getLocation() .toRealVector();

            RealVector q = p .plus( v1 );
            RealVector r = p .plus( v2 );

            // p ^ q
            double e12 = p.x * q.y - q.x * p.y;
            double e23 = p.y * q.z - q.y * p.z;
            double e31 = p.z * q.x - q.z * p.x;
            double e10 = p.x - q.x; // homogeneous 3-vectors have 4th coord == 1
            double e20 = p.y - q.y;
            double e30 = p.z - q.z;

            // ( p ^ q ) ^ r = P
            double P_e123 = e12 * r.z + e23 * r.x + e31 * r.y;
            double P_e310 = e10 * r.z + e31 * 1d  - e30 * r.x;
            double P_e320 = e20 * r.z - e30 * r.y - e23 * 1d;
            double P_e120 = e12 * 1d  + e20 * r.x - e10 * r.y;

            // dual of P = e0123 * P
            this .workingPlaneDual = new double[4];
            this .workingPlaneDual[ 0 ] = - P_e123;
            this .workingPlaneDual[ 1 ] = - P_e320;
            this .workingPlaneDual[ 2 ] = P_e310;
            this .workingPlaneDual[ 3 ] = P_e120;
        }

        this .zone = zoneBall .setOrbits( orbits );
        if ( zone == null )
        {
            length = null;
            return;
        }
        this .length = symmetryController.orbitLengths.get( zone.getDirection() );
        adjustStrut();
        length .addPropertyListener( this );
    }

    public void finishPreview( DocumentModel document )
    {
        if ( length == null )
            return;
        length .removePropertyListener( this );
        strut .undo();
        strut = null;
        if ( logger .isLoggable( Level.FINE ) )
            logger .fine( "preview finished at  " + zone );
        Map<String, Object> params = new HashMap<>();
        params .put( "anchor", point );
        params .put( "zone", zone );
        params .put( "length", length .getValue() );
        document .doEdit( "StrutCreation", params );
        point = null;
        zone = null;
        length = null;
        this .workingPlaneDual = null;
    }

    public LengthController getLengthModel()
    {
        return length;
    }

    private void adjustStrut()
    {
        if ( strut != null )
            strut .undo();
        if ( length == null )
            return;
        if ( logger .isLoggable( Level.FINER ) )
            logger .finer( "preview now " + zone );
        strut = new StrutCreation( point, zone, length .getValue(), model );
        strut .perform();
    }

    @Override
    public void propertyChange( PropertyChangeEvent evt )
    {
        // mousewheel ticked over enough to trigger a scale change in the LengthModel
        if ( "length" .equals( evt .getPropertyName() ) )
            adjustStrut();
    }

    public void setSymmetryController( SymmetryController symmetryController )
    {
        this .symmetryController = symmetryController;
        rendering .setOrbitSource( symmetryController .getOrbitSource() );
    }
    
    private final boolean usingWorkingPlane()
    {
        return this .workingPlaneDual != null;
    }

    public void trackballRolled( Quat4f roll )
    {
        if ( point != null && ! usingWorkingPlane() )
            zoneBall .trackballRolled( roll );  // some of these events will trigger the zone change
    }

    public void workingPlaneDrag( Line ray )
    {
        if ( usingWorkingPlane() )
        {
            if ( this .point == null && logger .isLoggable( Level.SEVERE ) ) {
                logger .severe( "No point during workingPlaneDrag!" );
                return;
            }
            RealVector planeIntersection = this .intersectWorkingPlane( ray );
            RealVector vectorInPlane = planeIntersection .minus( this .point .getLocation() .toRealVector() );

            Vector3f almostPlanarVector = new Vector3f();
            almostPlanarVector .set( vectorInPlane.x, vectorInPlane.y, vectorInPlane.z );
            zoneBall .setVector( almostPlanarVector );
        }
    }
    
    private RealVector intersectWorkingPlane( Line ray )
    {
        RealVector s = ray .getOrigin();
        RealVector t = s .plus( ray .getDirection() );  // we need two points on the line

        // This line-plane intersection comes right out of Vince, GA4CG, p. 196,
        // but I had to derive the general forms for the products.
        
        // The line l is encoded in the outer product s ^ t, a bivector.
        double e12 = s.x * t.y - t.x * s.y;
        double e23 = s.y * t.z - t.y * s.z;
        double e31 = s.z * t.x - t.z * s.x;
        double e10 = s.x - t.x; // homogeneous 3-vectors have 4th coord == 1, what would be t.w and s.w
        double e20 = s.y - t.y;
        double e30 = s.z - t.z;

        // dualP . l = x (the result, in homogeneous coordinates)
        double x_e1 = - workingPlaneDual[ 2 ] * e12 - workingPlaneDual[ 0 ] * e10 + workingPlaneDual[ 3 ] * e31;
        double x_e2 = - workingPlaneDual[ 3 ] * e23 - workingPlaneDual[ 0 ] * e20 + workingPlaneDual[ 1 ] * e12;
        double x_e3 = - workingPlaneDual[ 1 ] * e31 - workingPlaneDual[ 0 ] * e30 + workingPlaneDual[ 2 ] * e23;
        double x_e0 = workingPlaneDual[ 1 ] * e10 + workingPlaneDual[ 2 ] * e20 + workingPlaneDual[ 3 ] * e30;
        
        // Convert from homogeneous to normal 3D coordinates
        return new RealVector( x_e1 / x_e0, x_e2 / x_e0, x_e3 / x_e0 );
    }
}
