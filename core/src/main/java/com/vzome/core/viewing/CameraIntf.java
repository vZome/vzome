package com.vzome.core.viewing;

import com.vzome.core.math.RealVector;

public interface CameraIntf
{
    boolean isPerspective();

    float getFieldOfView();

    float getViewDistance();

    float getMagnification();

    RealVector getLookAtPointRV();

    RealVector getLookDirectionRV();

    RealVector getUpDirectionRV();

    RealVector mapViewToWorld( RealVector rv );
}
