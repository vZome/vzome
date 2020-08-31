package com.vzome.core.edits;

import java.util.HashSet;
import java.util.Set;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.editor.api.ChangeManifestations;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.RealizedModel;
import com.vzome.core.model.Strut;

public abstract class ConvexHull extends ChangeManifestations {
    public ConvexHull(Selection selection, RealizedModel realized) {
        super(selection, realized);
    }
    
    // common code for 2d and 3d convex hulls
    protected Set<AlgebraicVector> getSelectedVertexSet(boolean unselectAll) {
        Set<AlgebraicVector> vertexSet = new HashSet<>();
       
        for (Manifestation man : mSelection) {
            if (man instanceof Connector) {
                vertexSet.add( man.getLocation() );
            } else if (man instanceof Strut) {
                vertexSet.add( man.getLocation() );
                vertexSet.add( ((Strut) man).getEnd() );
            } else if (man instanceof Panel) {
                for(AlgebraicVector vertex : ((Panel) man)) {
                    vertexSet.add( vertex );
                }
            }
            if(unselectAll) {
                unselect(man);
            }
        }
        return vertexSet;
    }
}
