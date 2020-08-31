package com.vzome.core.editor.api;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.math.symmetry.Symmetries4D;
import com.vzome.core.model.RealizedModel;

public interface EditorModel
{
    RealizedModel getRealizedModel();

    Point getCenterPoint();

    void setCenterPoint( Construction point );

    Segment getSymmetrySegment();

    void setSymmetrySegment( Segment segment );
            
    UndoableEdit createEdit( String name );

    Construction getSelectedConstruction( Class<? extends Construction > kind );

    void addFailedConstruction( Construction cons );

    boolean hasFailedConstruction( Construction cons );

    Selection getSelection();

    OrbitSource getSymmetrySystem();

    OrbitSource getSymmetrySystem( String name );
    
    Symmetries4D get4dSymmetries();
}
