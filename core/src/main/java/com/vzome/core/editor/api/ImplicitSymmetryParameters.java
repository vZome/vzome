package com.vzome.core.editor.api;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;

public interface ImplicitSymmetryParameters extends EditorModel
{
    Point getCenterPoint();

    void setCenterPoint(Construction point);

    Segment getSymmetrySegment();

    void setSymmetrySegment(Segment segment);

    Construction getSelectedConstruction(Class<? extends Construction> kind);
}