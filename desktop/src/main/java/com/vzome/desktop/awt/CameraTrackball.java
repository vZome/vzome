package com.vzome.desktop.awt;

import java.awt.event.MouseEvent;

import javax.vecmath.Quat4f;

import org.vorthmann.j3d.Trackball;

import com.vzome.desktop.controller.CameraController;

public class CameraTrackball extends Trackball
{
    private final CameraController controller;

    public CameraTrackball( CameraController controller, double speed )
    {
        super( speed, true );
        this.controller = controller;
    }

//  @Override
//  protected double getSpeed()
//  {
//      double baseSpeed = super.getSpeed();
//      // make speed depend on model.getMagnification()
//      float mag = model .getMagnification();
//      float power = (-1f/3f) * ( mag + 2f );
//      return baseSpeed * Math .pow( 10d, power );
//  }

  @Override
  public void mousePressed( MouseEvent e )
  {
      this .controller .saveBaselineView(); // might have been zooming
      super .mousePressed( e );
  }

  @Override
  public void trackballRolled( Quat4f roll )
  {
      Quat4f copy = new Quat4f( roll );

      this .controller .getWorldRotation( copy );

      this .controller .addViewpointRotation( copy );

      // TODO give will-snap feedback when drag paused
  }

  @Override
  public void mouseReleased( MouseEvent e )
  {
      if ( this .controller .isSnapping() )
          this .controller .snapView();
      this .controller .saveBaselineView();
  }
}
