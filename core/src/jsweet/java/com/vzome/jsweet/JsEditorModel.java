package com.vzome.jsweet;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.editor.api.OrbitSource;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.editor.api.UndoableEdit;
import com.vzome.core.math.symmetry.Symmetries4D;
import com.vzome.core.model.RealizedModel;

public class JsEditorModel implements EditorModel
{
    public JsEditorModel() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public RealizedModel getRealizedModel() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Point getCenterPoint() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setCenterPoint(Construction point) {
        // TODO Auto-generated method stub

    }

    @Override
    public Segment getSymmetrySegment() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSymmetrySegment(Segment segment) {
        // TODO Auto-generated method stub

    }

    @Override
    public UndoableEdit createEdit(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Construction getSelectedConstruction(Class<? extends Construction> kind) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addFailedConstruction(Construction cons) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean hasFailedConstruction(Construction cons) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Selection getSelection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OrbitSource getSymmetrySystem() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OrbitSource getSymmetrySystem(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Symmetries4D get4dSymmetries() {
        // TODO Auto-generated method stub
        return null;
    }

}
