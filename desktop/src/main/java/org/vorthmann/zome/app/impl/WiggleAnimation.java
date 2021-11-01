package org.vorthmann.zome.app.impl;

import javax.vecmath.Vector3f;

import com.vzome.core.viewing.Camera;
import com.vzome.desktop.controller.CameraController;

public class WiggleAnimation implements AnimationController
{
    private static final int NUM_ITERATIONS = 30;

    private static final float WIGGLE_PERCENT = 0.08f;

    private final CameraController cameraController;
    
    private final Camera initialCamera;
    
    private final Vector3f lookOffset;

    private int iteration;

    public WiggleAnimation( CameraController cameraController )
    {    	
        this .iteration = NUM_ITERATIONS;
        this .cameraController = cameraController;
        this .initialCamera = cameraController .getView();
        this .lookOffset = new Vector3f();
        this .lookOffset .cross( this .initialCamera .getLookDirection(), this .initialCamera .getUpDirection() );
    }

    /* (non-Javadoc)
     * @see org.vorthmann.zome.app.impl.AnimationController#getImageSize()
     */
    @Override
    public int getImageSize()
    {
        return 1500;
    }
    
    @Override
    public boolean finished()
    {
        if ( this .iteration == 0 ) {
            this .cameraController .restoreView( this .initialCamera );
            return true;
        }
        else
            return false;
    }

    /* (non-Javadoc)
     * @see org.vorthmann.zome.app.impl.AnimationController#rotate()
     */
    @Override
    public void rotate()
    {
        Camera newCamera = new Camera( this .initialCamera );
        double paramRadians = 2 * Math.PI * this.iteration / NUM_ITERATIONS;
        Vector3f xWiggle = new Vector3f( this .lookOffset );
        xWiggle .scale( WIGGLE_PERCENT );
        xWiggle .scale( (float) Math.cos( paramRadians ) );
        Vector3f yWiggle = new Vector3f( this .initialCamera .getUpDirection() );
        yWiggle .scale( WIGGLE_PERCENT / 2 );
        yWiggle .scale( (float) Math.sin( paramRadians ) );
        Vector3f wiggle = new Vector3f( this .initialCamera .getLookDirection() );
        wiggle .add( xWiggle );
        wiggle .add( yWiggle );
        newCamera .setViewDirection( wiggle );
        this .cameraController .restoreView( newCamera );
        
        -- this .iteration;
    }
}
