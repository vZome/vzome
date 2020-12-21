package com.vzome.jsweet;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.editor.api.ImplicitSymmetryParameters;
import com.vzome.core.editor.api.OrbitSource;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.editor.api.SymmetryAware;
import com.vzome.core.math.symmetry.Symmetries4D;
import com.vzome.core.model.RealizedModel;

public class JsEditorModel implements EditorModel, ImplicitSymmetryParameters, SymmetryAware
{
    private final RealizedModel realizedModel;
    private final Selection selection;
    private final Symmetries4D kind;
    private final Point symmetryCenter;

    public JsEditorModel( RealizedModel realizedModel, Selection selection, Symmetries4D kind )
    {
        this.realizedModel = realizedModel;
        this.selection = selection;
        this.kind = kind;
        this.symmetryCenter = new FreePoint( realizedModel .getField() .origin( 3 ) );
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
    public Symmetries4D get4dSymmetries()
    {
        return this.kind;
    }

    @Override
    public Segment getSymmetrySegment()
    {
        return null;
    }

    @Override
    public Point getCenterPoint()
    {
        return this.symmetryCenter;
    }
    
    
    

    
    @Override
    public void setCenterPoint(Construction point)
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

    @Override
    public OrbitSource getSymmetrySystem()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public OrbitSource getSymmetrySystem( String name )
    {
        throw new RuntimeException( "unimplemented" );
    }
}
