
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.edits;

import java.util.List;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.PolygonFromVertices;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.model.HasRenderedObject;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.RenderedObject;

public class RealizeMetaParts extends ChangeManifestations
{
    public static final String NAME = "realizeMetaParts";
        
    @Override
    public void perform() throws Command.Failure
    {
    	AlgebraicNumber scale = null;
        for (Manifestation man : mSelection) {
            unselect( man );
            RenderedObject rm = ((HasRenderedObject) man) .getRenderedObject();
            if (rm != null) {
                Polyhedron shape = rm .getShape();
                if ( scale == null ) {
                	// wait until we know the field
                    AlgebraicField field = shape .getField();
            		scale = field .createPower( 5 ); // .times( field .createRational( 2 ));
                }
                AlgebraicMatrix orientation = rm .getOrientation();
                List<AlgebraicVector> vertexList = shape .getVertexList();
                for (AlgebraicVector vertex : shape .getVertexList()) {
                    Point vertexPt = transformVertex( vertex, man .getLocation(), scale, orientation );
                    select( manifestConstruction( vertexPt ) );
                }
                for (Polyhedron.Face face : shape .getFaceSet()) {
                    Point[] vertices = new Point[ face .size() ];
                    for ( int i = 0; i < vertices.length; i++ ) {
                        int vertexIndex = face .getVertex( i );
                        AlgebraicVector vertex = vertexList .get( vertexIndex );
                        vertices[ i ] = transformVertex( vertex, man .getLocation(), scale, orientation );
                    }
                    Polygon polygon = new PolygonFromVertices( vertices );
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
        return new FreePoint( vertex .scale( scale ) );
    }

    public RealizeMetaParts( Selection selection, RealizedModel realized )
    {
        super( selection, realized );
    }
        
    @Override
    protected String getXmlElementName()
    {
        return NAME;
    }
}
