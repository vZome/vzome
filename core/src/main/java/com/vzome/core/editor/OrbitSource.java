package com.vzome.core.editor;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Color;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.OrbitSet;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.render.Shapes;

public interface OrbitSource
{
    Symmetry getSymmetry();
	    	
    Axis getAxis( AlgebraicVector vector );
    
    Color getColor( Direction orbit );

    Color getVectorColor( AlgebraicVector vector );

	OrbitSet getOrbits();
	
	Shapes getShapes();

    String getName();
}