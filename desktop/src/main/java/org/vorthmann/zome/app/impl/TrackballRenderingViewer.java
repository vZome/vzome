package org.vorthmann.zome.app.impl;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.vzome.core.viewing.Camera;
import com.vzome.desktop.controller.CameraController;

/**
 * A CameraController.Viewer that only changes rotations.
 * 
 * It is connected to the Camera to rotate the trackball.
 * 
 * @author vorth
 *
 */
public class TrackballRenderingViewer implements CameraController.Viewer
{
	private final CameraController.Viewer delegate;
	
	private final Vector3f translation;

	public TrackballRenderingViewer( CameraController.Viewer delegate )
	{
		this .delegate = delegate;
		
		this .translation = new Vector3f();
		Matrix4f matrix = new Matrix4f();
		Camera defaultCamera = new Camera();
		defaultCamera .setMagnification( 1.0f );
		defaultCamera .getViewTransform( matrix );
		matrix .get( translation ); // save the default translation to apply on every update below

		// set the perspective view just once
		double near = defaultCamera .getNearClipDistance();
        double far = defaultCamera .getFarClipDistance();
        double fov = defaultCamera .getFieldOfView();
		this .delegate .setPerspective( fov, 1.0d, near, far );
	}

	@Override
	public void setViewTransformation( Matrix4f trans )
	{
	    Matrix3f justRotation3d = new Matrix3f();
	    trans .get( justRotation3d );
	    justRotation3d .invert(); // to match the invert() in the caller
	    Matrix4f finalTransform = new Matrix4f();
	    finalTransform .set( this .translation );
	    finalTransform .setRotation( justRotation3d );
	    finalTransform .invert(); // to match the invert() in the caller
	    this .delegate .setViewTransformation( finalTransform );
	}

	@Override
	public void setPerspective( double fov, double aspectRatio, double near, double far ) {}

	@Override
	public void setOrthographic( double halfEdge, double near, double far ) {}
}
