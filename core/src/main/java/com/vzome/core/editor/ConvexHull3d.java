package com.vzome.core.editor;

import java.util.HashMap;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.PolygonFromVertices;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.math.convexhull.QuickHull3D;

public class ConvexHull3d extends ConvexHull {
    public static final String NAME = "ConvexHull3d";

    public ConvexHull3d( EditorModel editor )
    {
        super( editor .getSelection(), editor .getRealizedModel() );
    }
    
    @Override
    protected String getXmlElementName() {
        return NAME;
    }

    @Override
    public void perform() throws Failure {
        QuickHull3D hull3d = new QuickHull3D();
        hull3d.build( getSelectedVertexSet(true) );
        
        redo();  // no validation failures, so commit the initial unselect operations

        AlgebraicVector[] vertices = hull3d.getVertices();
        Map<AlgebraicVector, Point> pointMap = new HashMap<>(vertices.length);
        
        for (AlgebraicVector vertex : vertices) {
            Point point = new FreePoint(vertex);
            pointMap.put(vertex, point);
            select(manifestConstruction(point));
        }

        int[][] faces = hull3d.getFaces();
        for (int[] face : faces) {
            Point[] points = new Point[face.length];
            int startIndex = face[face.length-1]; // start with the last vertex index
            Point start = pointMap.get(vertices[startIndex]);
            for (int i = 0; i < face.length; i++) {
                int endIndex = startIndex;
                startIndex = face[i];
                Point end = points[i] = pointMap.get(vertices[startIndex]);
                // each half-edge is shared by two faces
                // so we only need a new strut for half of them.
                if(startIndex < endIndex) {
                    select(manifestConstruction(new SegmentJoiningPoints(start, end)));
                }
                start = end;
            }
            // panels
            select( manifestConstruction( new PolygonFromVertices( points ) ) );
        }

        redo(); // commit the changes
    }
}
