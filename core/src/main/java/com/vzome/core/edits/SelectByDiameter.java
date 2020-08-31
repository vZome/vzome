package com.vzome.core.edits;

import java.util.ArrayList;
import java.util.List;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;

public class SelectByDiameter extends SelectByBoundary {
    public static final String NAME = "SelectByDiameter";

    @Override
    protected String getXmlElementName() {
        return NAME;
    }

    protected AlgebraicVector center;
    protected AlgebraicNumber maxRadiusSquared;
    
    public SelectByDiameter( EditorModel editor )
    {
        super( editor .getSelection(), editor .getRealizedModel() );
    }

    @Override
    public String usage() {
        return  "This command requires two connectors which define the\n" +
                "diameter of a sphere centered at their midpoint.\n\n" +
                "All parts that are completely within the sphere will be selected.\n";
    }

    protected String adjustBoundary(List<AlgebraicVector> vectors) {
        switch(vectors.size()) {
        case 1:
            // anticipate subsequent call with 2 vectors
            return null;
        case 2:
            center = AlgebraicVectors.calculateCentroid(vectors);
            AlgebraicVector v1 = vectors.get(0).minus(center);
            maxRadiusSquared = v1.dot(v1);
            return null;
        }
        return "Too many connectors are selected.";
    }
    
    @Override
    protected String setBoundary() {
        center = null;
        maxRadiusSquared = null;

        List<AlgebraicVector> vectors = new ArrayList<>();
        for (Manifestation man : mSelection) {
            if (man instanceof Connector) {
                vectors.add(man.getLocation());
                // derived classes can adjust the boundary or evaluate additional points
                String errMsg = adjustBoundary(vectors);
                if(errMsg != null) {
                    return errMsg;
                }
            }
        }

        if (vectors.isEmpty()) {
            return "No connectors are selected.";
        }
        if (center == null || maxRadiusSquared == null) {
            int n = vectors.size();
            return n == 1
                ? "Only one connector is selected."
                : "Only " + n + " connectors are selected.";
        }
        
        return null;
    }
    
    @Override
    protected boolean boundaryContains(AlgebraicVector v) {
        v = v.minus( center );
        AlgebraicNumber vSq = v.dot(v);
        return vSq.compareTo(maxRadiusSquared) <= 0;
    }
}
