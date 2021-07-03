
package com.vzome.core.parts;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.math.Polyhedron;

/**
 * @author Scott Vorthmann
 *
 */
public interface StrutGeometry
{
    public abstract Polyhedron getStrutPolyhedron( AlgebraicNumber length );
}
