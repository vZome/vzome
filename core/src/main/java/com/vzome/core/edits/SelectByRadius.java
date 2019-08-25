package com.vzome.core.edits;

import java.util.List;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.commands.Command;
import com.vzome.core.editor.EditorModel;

public class SelectByRadius extends SelectByDiameter {
    public static final String NAME = "SelectByRadius";
    
    @Override
    protected String getXmlElementName() {
        return NAME;
    }

    protected AlgebraicNumber minRadiusSquared;

    public SelectByRadius( EditorModel editor )
    {
        super( editor );
    }

    @Override
    public String usage() {
        return  "This command requires either two or three selected connectors.\n\n" +
                "The first connector marks the center of a sphere.\n" +
                "The second connector defines its radius.\n" +
                "An optional third connector defines the radius of\n" + 
                " a second sphere with the same center.\n\n" +
                "All parts that are completely within the larger sphere will be selected.\n\n" +
                "If a second sphere is defined then any parts\n" +
                "     within the smaller sphere, even partially, will be excluded.\n";
    }

    @Override
    public void perform() throws Command.Failure
    {
        setOrderedSelection( true );
        super.perform();
    }

    @Override
    protected String adjustBoundary(List<AlgebraicVector> vectors) {
        AlgebraicVector v = vectors.get(vectors.size() - 1);
        switch(vectors.size()) {
        case 1:
            center = v;
            maxRadiusSquared = null;
            minRadiusSquared = null;
            return null;
        case 2:
            AlgebraicVector v2 = v.minus(center);
            maxRadiusSquared = v2.dot(v2);
            return null;
        case 3:
            AlgebraicVector v3 = v.minus(center);
            minRadiusSquared = v3.dot(v3);
            if (maxRadiusSquared.compareTo(minRadiusSquared) < 0) {
                // swap so that minRadiusSquared <= maxRadiusSquared
                AlgebraicNumber temp = maxRadiusSquared;
                maxRadiusSquared = minRadiusSquared;
                minRadiusSquared = temp;
            }
            return null;
        }
        return super.adjustBoundary(vectors);
    }

    @Override
    protected boolean boundaryContains(AlgebraicVector v) {
        if(super.boundaryContains(v)) {
            if(minRadiusSquared != null) {
                AlgebraicVector v1 = v.minus( center );
                return v1.dot(v1).compareTo(minRadiusSquared) >= 0;
            }
            return true;
        }
        return false;
    }
}
