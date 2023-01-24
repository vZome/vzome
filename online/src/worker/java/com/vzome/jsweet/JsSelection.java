package com.vzome.jsweet;

import static jsweet.util.Lang.$array;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.vzome.core.editor.api.Selection;
import com.vzome.core.model.Manifestation;

import def.js.Function;
import def.js.IteratorResult;
import def.js.Object;

public class JsSelection implements Selection
{
    private Object adapter;
    private final JsAlgebraicField field;

    public JsSelection( JsAlgebraicField field, Object adapter )
    {
        this.field = field;
        this.adapter = adapter;
    }
    
    void setAdapter( Object adapter )
    {
        this.adapter = adapter;
    }

    @Override
    public Iterator<Manifestation> iterator()
    {
        if ( this.adapter == null ) // can happen during predefined Tool initialization
            return Collections.emptyIterator();

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
        return (int) ( (Function) this.adapter .$get( "selectionSize" ) ).apply( this.adapter );
    }

    @Override
    public void gatherGroup()
    {
        ( (Function) this.adapter .$get( "createGroup" ) ).apply( this.adapter );
    }

    @Override
    public void scatterGroup()
    {
        ( (Function) this.adapter .$get( "disbandGroup" ) ).apply( this.adapter );
    }

    @Override
    public void gatherGroup211()
    {
        ( (Function) this.adapter .$get( "createLegacyGroup" ) ).apply( this.adapter );
    }

    @Override
    public void scatterGroup211()
    {
        ( (Function) this.adapter .$get( "disbandLegacyGroup" ) ).apply( this.adapter );
    }

    @Override
    public void selectWithGrouping( Manifestation man )
    {
        if ( man == null ) // This check is performed in SelectionImpl.java
            return;

        int[][][] vectors = ((JsManifestation) man) .getVectors();
        ( (Function) this.adapter .$get( "selectWithGrouping" ) ).apply( this.adapter, $array( vectors ) );
    }

    @Override
    public void unselectWithGrouping( Manifestation man )
    {
        if ( man == null ) // This check is performed in SelectionImpl.java
            return;

        int[][][] vectors = ((JsManifestation) man) .getVectors();
        ( (Function) this.adapter .$get( "unselectWithGrouping" ) ).apply( this.adapter, $array( vectors ) );
    }

    @Override
    public boolean isSelectionAGroup()
    {
        return (boolean) ( (Function) this.adapter .$get( "selectionIsGroup" ) ).apply( this.adapter );
    }
    
    
    
    

    @Override
    public Manifestation getSingleSelection( Class<? extends Manifestation> class1 )
    {
        throw new RuntimeException( "unimplemented getSingleSelection" );
    }
    
    @Override
    public void copy(List<Manifestation> bookmarkedSelection)
    {
        throw new RuntimeException( "unimplemented" );
    }
}
