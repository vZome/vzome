package com.vzome.core.editor.api;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Color;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Symmetry;

public interface Shapes
{
    String getName();

    String getAlias();

	Polyhedron getConnectorShape();

    Polyhedron getStrutShape( Direction dir, AlgebraicNumber length );

    Polyhedron getPanelShape( int vertexCount, AlgebraicNumber quadrea, Axis zone, Iterable<AlgebraicVector> vertices, boolean oneSidedPanels );

    Symmetry getSymmetry();

    String getPackage();
    
    Color getColor( Direction dir );

	boolean hasColors();
}


