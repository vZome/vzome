package com.vzome.core.editor.api;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Color;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.OrbitSet;
import com.vzome.core.math.symmetry.Symmetry;

public interface OrbitSource
{
    Symmetry getSymmetry();
	    	
    Axis getAxis( AlgebraicVector vector );
    
    Color getColor( Direction orbit );  // used only in rendering

    Color getVectorColor( AlgebraicVector vector );

	OrbitSet getOrbits();
	
	Shapes getShapes();

    String getName();  // used in serializing edits, and in rendering
}