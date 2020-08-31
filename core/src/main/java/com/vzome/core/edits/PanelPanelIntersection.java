package com.vzome.core.edits;

import java.util.ArrayList;
import java.util.List;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;
import com.vzome.core.commands.Command;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.PolygonFromVertices;
import com.vzome.core.construction.PolygonPolygonProjectionToSegment;
import com.vzome.core.construction.Segment;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.RealizedModel;

/**
 * @author David Hall
 */
public class PanelPanelIntersection extends ChangeManifestations {

    public PanelPanelIntersection(Selection selection, RealizedModel realized) {
        super(selection, realized);
    }

    @Override
    public void perform() throws Command.Failure
    {
        Panel panel0 = null;
        Panel panel1 = null;
        int nPanels = 0;
        for (Manifestation man : mSelection) {
            unselect(man);
            if (man instanceof Panel) {
                switch(nPanels++) {
                case 0:
                    panel0 = (Panel) man;
                    break;
                case 1:
                    panel1 = (Panel) man;
                    break;
                default:
                    break;
                }
            }
        }
        if(nPanels != 2) {
            String msg;
            switch(nPanels) {
            case 0:
                msg = "No panels are selected.";
                break;
            case 1:
                msg = "One panel is selected.";
                break;
            default:
                msg = nPanels + " panels are selected.";
                break;
            }
            fail(msg + " Two are required.");
        }
        
        if(AlgebraicVectors.areParallel(panel0.getNormal(), panel1.getNormal())) {
            List<AlgebraicVector> vertices = new ArrayList<>();
            for(Panel panel : new Panel[] {panel0, panel1}) {
                for(AlgebraicVector v : panel) {
                    vertices.add(v);
                }
            }
            fail("Panels are " + (AlgebraicVectors.areCoplanar(vertices) ? "coplanar" : "parallel") + ".");
        }
        
        redo(); // commit the unselects
                
        Segment segment = new PolygonPolygonProjectionToSegment(polygonFromPanel(panel0), polygonFromPanel(panel1));
        Point start = new FreePoint(segment.getStart()); 
        Point end = new FreePoint(segment.getEnd());
        
        select(manifestConstruction(segment));
        select(manifestConstruction(start));
        select(manifestConstruction(end));
        
        redo(); // commit the new constructions
    }

    private static Polygon polygonFromPanel(Panel panel) {
        List<Point> vertices = new ArrayList<>(panel.getVertexCount());
        for(AlgebraicVector vector : panel) {
            vertices.add(new FreePoint(vector));
        }
        return new PolygonFromVertices(vertices);
    }
    
    @Override
    protected String getXmlElementName() {
        return getClass().getSimpleName();
    }

}
