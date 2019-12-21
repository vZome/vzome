package com.vzome.core.edits;

import java.util.HashMap;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.PolygonFromVertices;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.editor.EditorModel;
import com.vzome.core.math.convexhull.GrahamScan2D;

public class ConvexHull2d extends ConvexHull {
    public static final String NAME = "ConvexHull2d";

    public ConvexHull2d( EditorModel editor )
    {
        super( editor .getSelection(), editor .getRealizedModel() );
    }
    
    @Override
    protected String getXmlElementName() {
        return NAME;
    }

    @Override
    public void perform() throws Failure {
        AlgebraicVector[] hull2d = GrahamScan2D.buildHull( getSelectedVertexSet(true) );

        redo();  // no validation failures, so commit the initial unselect operations
        
        Point[] vertices = new Point[hull2d.length];
        int p = 0;
        Map<AlgebraicVector, Point> pointMap = new HashMap<>(hull2d.length);
        // vertices
        for (AlgebraicVector vertex : hull2d) {
            Point point = new FreePoint(vertex);
            pointMap.put(vertex, point);
            vertices[p++] = point;
            select(manifestConstruction(point));
        }
        // polygon
        select( manifestConstruction( new PolygonFromVertices( vertices) ) );
        // edges
        Point start = pointMap.get(hull2d[0]);
        for (int i = 1; i < hull2d.length; i++) {
            Point end = pointMap.get(hull2d[i]);
            select(manifestConstruction(new SegmentJoiningPoints(start, end)));
            start = end;
        }
        // close the polygon
        Point end = pointMap.get(hull2d[0]);
        select(manifestConstruction(new SegmentJoiningPoints(start, end)));

        redo(); // commit the changes
    }
}
