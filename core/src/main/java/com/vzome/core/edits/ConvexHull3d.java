package com.vzome.core.edits;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.commands.Command.Failure;
import com.vzome.core.commands.XmlSaveFormat;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.PolygonFromVertices;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.math.convexhull.QuickHull3D;

public class ConvexHull3d extends ConvexHull {
    public static final String NAME = "ConvexHull3d";
    
    private String mode = null;
    private boolean generateStruts = true; 
    private boolean generatePanels = true; 

    public ConvexHull3d( EditorModel editorModel )
    {
        super( editorModel );
    }
    
    @Override
    protected String getXmlElementName() {
        return NAME;
    }

    @Override
    public boolean isNoOp()
    {
        return (generateStruts || generatePanels) ? super.isNoOp() : true;
    }
    
    @Override
    public void configure( Map<String,Object> props ) 
    {
        setMode( (String) props .get( "mode" ) );
    }
    
    private void setMode(String newMode) 
    {
        // initialize to legacy default values in case mode is invalid or not specified
        this.mode = "";
        generateStruts = true; 
        generatePanels = true; 

        if ( newMode != null )
            switch ( newMode ) {

            case "":
                this.mode = newMode;
                this.generateStruts = true;
                this.generatePanels = true;
                break;

            case "noPanels":
                this.mode = newMode;
                this.generateStruts = true;
                this.generatePanels = false;
                break;

            case "onlyPanels":
                this.mode = newMode;
                this.generateStruts = false;
                this.generatePanels = true;
                break;

            default:
                if(logger.isLoggable(Level.WARNING) ) {
                    logger.warning(NAME + ": Ignoring unknown mode: \"" + newMode + "\".");
                }
                break;
            }
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
                if(generateStruts && (startIndex < endIndex)) {
                    select(manifestConstruction(new SegmentJoiningPoints(start, end)));
                }
                start = end;
            }
            // panels
            if(generatePanels) {
                select( manifestConstruction( new PolygonFromVertices( points ) ) );
            }
        }

        redo(); // commit the changes
    }
    
    @Override
    protected void getXmlAttributes( Element element )
    {
        if(mode != null) {
            element .setAttribute( "mode", mode );
        }
    }

    @Override
    protected void setXmlAttributes( Element xml, XmlSaveFormat format ) throws Command.Failure
    {
        setMode( xml.getAttribute( "mode" ) );
    }
}
