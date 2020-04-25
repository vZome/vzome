package com.vzome.core.render;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.mesh.Color;

public interface Shapes{
    
//    void setSceneGraphFactory( Factory factory );
    
    String getName();

    String getAlias();

	Polyhedron getConnectorShape();

    Polyhedron getStrutShape( Direction dir, AlgebraicNumber length );
        
	void addListener( Changes change );
	
	void removeListener( Changes changes );
	
	public interface Changes
	{
	    void strutShapeChanged( Direction dir );
	    
	    void connectorShapeChanged( Polyhedron shape );
	}

    Symmetry getSymmetry();

    String getPackage();
    
    Color getColor( Direction dir );

	boolean hasColors();
}


