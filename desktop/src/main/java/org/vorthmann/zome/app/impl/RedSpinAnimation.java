package org.vorthmann.zome.app.impl;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.vzome.desktop.awt.CameraController;

public class RedSpinAnimation implements AnimationController
{
    private static final int NUM_ITERATIONS = 80;

    private CameraController cameraController;

    private final Quat4f rotation;

    private int iteration;

    public RedSpinAnimation( CameraController cameraController )
    {    	
        this .iteration = NUM_ITERATIONS;
        double rotationRadians = 2 * Math.PI / NUM_ITERATIONS;

        this.cameraController = cameraController;
        Vector3f viewRotAxis = new Vector3f( 0f, 1.618f, 1f );
        this .cameraController .mapViewToWorld( viewRotAxis );
        this .rotation = new Quat4f();
        this .rotation .set( new AxisAngle4f( viewRotAxis, (float) rotationRadians ) );
    }

    @Override
    public int getImageSize()
    {
        return 1200;
    }
    
    @Override
    public boolean finished()
    {
        return this .iteration == 0;
    }

    @Override
    public void rotate()
    {
        this .cameraController .addViewpointRotation( this .rotation );
        
        -- this .iteration;
    }
}
