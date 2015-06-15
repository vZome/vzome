
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.editor;

import java.util.Iterator;
import java.util.List;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.ModelRoot;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.PolygonFromVertices;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.render.RenderedManifestation;

public class RealizeMetaParts extends ChangeConstructions
{
    public static final String NAME = "realizeMetaParts";
    
    private final ModelRoot root;
    
    public void perform() throws Command.Failure
    {
        AlgebraicField field = this.root .getField();
		AlgebraicNumber scale = field .createPower( 5 ) .times( field .createRational( 2 ));
        for ( Iterator mans = mSelection .iterator(); mans .hasNext(); ) {
            Manifestation man = (Manifestation) mans .next();
            unselect( man );
            RenderedManifestation rm = (RenderedManifestation) man .getRenderedObject();
            if ( rm != null ) {
                Polyhedron shape = rm .getShape();
                AlgebraicMatrix orientation = rm .getOrientation();
                List<AlgebraicVector> vertexList = shape .getVertexList();
                for ( Iterator iterator = shape .getVertexList() .iterator(); iterator.hasNext(); ) {
                    AlgebraicVector vertex = (AlgebraicVector) iterator.next();
                    Point vertexPt = transformVertex( vertex, man .getLocation(), scale, orientation );
                    addConstruction( vertexPt );
                    select( manifestConstruction( vertexPt ) );
                }
                for ( Iterator iterator = shape .getFaceSet() .iterator(); iterator .hasNext(); ) {
                    Polyhedron.Face face = (Polyhedron.Face) iterator.next();
                    Point[] vertices = new Point[ face .size() ];
                    for ( int i = 0; i < vertices.length; i++ ) {
                        int vertexIndex = face .getVertex( i );
                        AlgebraicVector vertex = vertexList .get( vertexIndex );
                        vertices[ i ] = transformVertex( vertex, man .getLocation(), scale, orientation );
                    }
                    Polygon polygon = new PolygonFromVertices( vertices );
                    addConstruction( polygon );
                    select( manifestConstruction( polygon ) );
                }
            }
        }
        
        redo();
    }
    
    private Point transformVertex( AlgebraicVector vertex, AlgebraicVector offset, AlgebraicNumber scale, AlgebraicMatrix orientation )
    {
        if ( orientation != null )
            vertex = orientation .timesColumn( vertex );
        if ( offset != null )
            vertex = vertex .plus( offset );
        return new FreePoint( vertex .scale( scale ), this .root );
    }

    public RealizeMetaParts( Selection selection, RealizedModel realized, ModelRoot root )
    {
        super( selection, realized, false );
        this.root = root;
    }
        
    protected String getXmlElementName()
    {
        return NAME;
    }
}
