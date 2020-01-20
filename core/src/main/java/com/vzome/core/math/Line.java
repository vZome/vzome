package com.vzome.core.math;

public class Line
{
    private final RealVector origin, direction;

    public Line( RealVector origin, RealVector direction )
    {
        this .direction = direction;
        this .origin = origin;
    }

    public RealVector getOrigin()
    {
        return this .origin;
    }

    public RealVector getDirection()
    {
        return this .direction;
    }
}
