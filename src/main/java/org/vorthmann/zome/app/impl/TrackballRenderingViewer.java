package org.vorthmann.zome.app.impl;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import com.vzome.core.viewing.ViewModel;
import com.vzome.desktop.controller.ViewPlatformModel;
import com.vzome.desktop.controller.ViewPlatformModel.Viewer;

/**
 * A ViewPlatformModel.Viewer that only changes rotations.
 * 
 * It is connected to the ViewModel to rotate the trackball.
 * 
 * @author vorth
 *
 */
public class TrackballRenderingViewer implements ViewPlatformModel.Viewer
{
	private final ViewPlatformModel.Viewer delegate;
	
	private final Vector3d translation;

	public TrackballRenderingViewer( ViewPlatformModel.Viewer delegate )
	{
		this .delegate = delegate;
		
		this .translation = new Vector3d();
		Matrix4d matrix = new Matrix4d();
		ViewModel defaultCamera = new ViewModel();
		defaultCamera .setMagnification( 1.0f );
		defaultCamera .getViewTransform( matrix, 0d );
		matrix .get( translation ); // save the default translation to apply on every update below

		// set the perspective view just once
		double near = defaultCamera .getNearClipDistance();
        double far = defaultCamera .getFarClipDistance();
        double fov = defaultCamera .getFieldOfView();
		this .delegate .setPerspective( fov, 1.0d, near, far );
	}

	@Override
	public void setViewTransformation( Matrix4d trans, int eye )
	{
		if ( eye == Viewer .MONOCULAR ) {
			Matrix3d justRotation3d = new Matrix3d();
			trans .get( justRotation3d );
			justRotation3d .invert(); // to match the invert() in the caller
			Matrix4d finalTransform = new Matrix4d();
			finalTransform .set( this .translation );
			finalTransform .setRotation( justRotation3d );
			finalTransform .invert(); // to match the invert() in the caller
			this .delegate .setViewTransformation( finalTransform, Viewer .MONOCULAR );
		}
	}

	@Override
	public void setEye(int eye) {}

	@Override
	public void setPerspective( double fov, double aspectRatio, double near, double far ) {}

	@Override
	public void setOrthographic( double halfEdge, double near, double far ) {}
}
