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
    public JsEditorModel()
    {
        // TODO Auto-generated constructor stub
    }

    @Override
    public RealizedModel getRealizedModel()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public Point getCenterPoint()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void setCenterPoint(Construction point)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public Segment getSymmetrySegment()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void setSymmetrySegment(Segment segment)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public UndoableEdit createEdit(String name)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public Construction getSelectedConstruction(Class<? extends Construction> kind)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void addFailedConstruction(Construction cons)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public boolean hasFailedConstruction(Construction cons)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public Selection getSelection()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public OrbitSource getSymmetrySystem()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public OrbitSource getSymmetrySystem(String name)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public Symmetries4D get4dSymmetries()
    {
        throw new RuntimeException( "unimplemented" );
    }

}
