//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.parts;

import com.vzome.core.math.Polyhedron;

/**
 * @author Scott Vorthmann
 *
 */
public interface StrutGeometry
{
    public abstract Polyhedron getStrutPolyhedron( int[] length );
}