package com.vzome.core.editor.api;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.editor.FieldApplication;
import com.vzome.core.editor.SelectionSummary;
import com.vzome.core.editor.SelectionSummary.Listener;
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

    void setSymmetrySystem( OrbitSource system );

    OrbitSource getSymmetrySystem( String name );

    FieldApplication getKind();

    void addSelectionSummaryListener( SelectionSummary.Listener listener );
}
