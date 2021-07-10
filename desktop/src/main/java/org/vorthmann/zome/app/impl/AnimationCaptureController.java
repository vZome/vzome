package org.vorthmann.zome.app.impl;

import java.io.File;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.vorthmann.ui.Controller;
import org.vorthmann.ui.DefaultController;

import com.vzome.desktop.controller.CameraController;

public class AnimationCaptureController extends DefaultController implements Controller
{
    private static final int NUM_ITERATIONS = 80;

    public static final String TYPE = "png";

    private static final String FILENAME_PATTERN = "frame%03d." + TYPE;

    private CameraController cameraController;

    private final Quat4f rotation;

    private final File parentDir;

    private int iteration;

    public AnimationCaptureController( CameraController cameraController, File directory )
    {    	
        this .iteration = NUM_ITERATIONS;
        double rotationRadians = 2 * Math.PI / NUM_ITERATIONS;

        this.cameraController = cameraController;
        Vector3f viewRotAxis = new Vector3f( 0f, 1.618f, 1f );
        this .cameraController .mapViewToWorld( viewRotAxis );
        this .rotation = new Quat4f();
        this .rotation .set( new AxisAngle4f( viewRotAxis, (float) rotationRadians ) );

        this .parentDir = directory .isDirectory()? directory : directory .getParentFile();
    }

    public int getImageSize()
    {
        return 1200;
    }

    public void rotate()
    {
        this .cameraController .addViewpointRotation( this .rotation );
    }

    public File nextFile()
    {
        String fileName = String .format( FILENAME_PATTERN, -- this .iteration );
        return new File( this .parentDir, fileName );
    }

    public boolean finished()
    {
        return this .iteration == 0;
    }

}
