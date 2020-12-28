package com.vzome.jsweet;

import com.vzome.core.construction.Construction;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.editor.api.EditorModel;
import com.vzome.core.editor.api.LegacyEditorModel;
import com.vzome.core.editor.api.OrbitSource;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.editor.api.SymmetryAware;
import com.vzome.core.math.symmetry.Symmetries4D;
import com.vzome.core.model.RealizedModel;

import def.js.Object;

public class JsEditorModel implements EditorModel, LegacyEditorModel, SymmetryAware
{
    private final RealizedModel realizedModel;
    private final Selection selection;
    private final Symmetries4D kind;
    private final Point symmetryCenter;
    private final OrbitSource symmetries;

    public JsEditorModel( RealizedModel realizedModel, Selection selection, Symmetries4D kind, OrbitSource symmetries )
    {
        this.realizedModel = realizedModel;
        this.selection = selection;
        this.kind = kind;
        this.symmetries = symmetries;
        this.symmetryCenter = new FreePoint( realizedModel .getField() .origin( 3 ) );
    }
    
    public void setAdapter( Object adapter )
    {
        ((JsRealizedModel) this.realizedModel) .setAdapter( adapter );
        ((JsSelection) this.selection) .setAdapter( adapter );
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
    public boolean hasFailedConstruction( Construction cons )
    {
        return false;
    }

    @Override
    public OrbitSource getSymmetrySystem()
    {
        return this .symmetries;
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
    public OrbitSource getSymmetrySystem( String name )
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void addFailedConstruction( Construction cons )
    {
        throw new RuntimeException( "unimplemented" );
    }
}
