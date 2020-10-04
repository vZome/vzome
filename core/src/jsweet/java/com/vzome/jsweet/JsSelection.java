package com.vzome.jsweet;

import static jsweet.util.Lang.$array;

import java.util.Iterator;
import java.util.List;

import com.vzome.core.editor.api.Selection;
import com.vzome.core.model.Manifestation;

import def.js.Function;
import def.js.IteratorResult;
import def.js.Object;

public class JsSelection implements Selection
{
    private final Object adapter;
    private final JsAlgebraicField field;

    public JsSelection( JsAlgebraicField field, Object adapter )
    {
        this.field = field;
        this.adapter = adapter;
    }

    @Override
    public Iterator<Manifestation> iterator()
    {
        Function f = (Function) this.adapter .$get( "selectedIterator" );
        final def.js.Iterator<int[][][]> jSiterator = (def.js.Iterator<int[][][]>) f.apply( this.adapter );
        return new Iterator<Manifestation>()
        {
            IteratorResult<int[][][]> peek = jSiterator .next();
            
            @Override
            public boolean hasNext()
            {
                return ! this .peek .done;
            }

            @Override
            public Manifestation next()
            {
                Manifestation result = JsManifestation .manifest( peek .value, field, adapter );
                this .peek = jSiterator .next();
                return result;
            }
        };
    }

    @Override
    public void clear()
    {
        ( (Function) this.adapter .$get( "clearSelection" ) ).apply( this.adapter );
    }

    @Override
    public boolean manifestationSelected( Manifestation man )
    {
        int[][][] vectors = ((JsManifestation) man) .getVectors();
        return (boolean) ( (Function) this.adapter .$get( "manifestationSelected" ) ).apply( this.adapter, $array( vectors ) );
    }

    @Override
    public void select( Manifestation man )
    {
        int[][][] vectors = ((JsManifestation) man) .getVectors();
        ( (Function) this.adapter .$get( "select" ) ).apply( this.adapter, $array( vectors ) );
    }

    @Override
    public void unselect( Manifestation man )
    {
        int[][][] vectors = ((JsManifestation) man) .getVectors();
        ( (Function) this.adapter .$get( "unselect" ) ).apply( this.adapter, $array( vectors ) );
    }

    @Override
    public int size()
    {
        throw new RuntimeException( "unimplemented" );
    }

    
    
    @Override
    public void selectWithGrouping(Manifestation mMan)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void unselectWithGrouping(Manifestation mMan)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void gatherGroup()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void gatherGroup211()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void scatterGroup()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void scatterGroup211()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public boolean isSelectionAGroup()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void copy(List<Manifestation> bookmarkedSelection)
    {
        throw new RuntimeException( "unimplemented" );
    }
}
