package com.vzome.jsweet;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.editor.api.ImplicitSymmetryParameters;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.model.RealizedModel;

public class JsEditorModel implements EditorModel, ImplicitSymmetryParameters
{
    private final RealizedModel realizedModel;
    private final Selection selection;

    public JsEditorModel( RealizedModel realizedModel, Selection selection )
    {
        this.realizedModel = realizedModel;
        this.selection = selection;
    }

    @Override
    public RealizedModel getRealizedModel()
    {
        return this.realizedModel;
    }

    @Override
    public Selection getSelection()
    {
       return this.selection;
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
    public Construction getSelectedConstruction( Class<? extends Construction> kind )
    {
        throw new RuntimeException( "unimplemented" );
    }
}
