package com.vzome.core.editor.api;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.construction.Color;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Symmetry;

public interface Shapes
{
    String getName();

    String getAlias();

	Polyhedron getConnectorShape();

    Polyhedron getStrutShape( Direction dir, AlgebraicNumber length );
        
    Symmetry getSymmetry();

    String getPackage();
    
    Color getColor( Direction dir );

	boolean hasColors();
}


