package com.vzome.jsweet;

import static jsweet.util.Lang.$array;

import java.util.Iterator;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.construction.Color;
import com.vzome.core.construction.Construction;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RealizedModel;

import def.js.Function;
import def.js.IteratorResult;
import def.js.Object;

public class JsRealizedModel implements RealizedModel {

    private final AlgebraicField field;
    private Object adapter;

    public JsRealizedModel( AlgebraicField field, Object adapter )
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
        Function f = (Function) this.adapter .$get( "allIterator" );
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
    public AlgebraicField getField()
    {
        return this.field;
    }

    @Override
    public Manifestation findConstruction( Construction c )
    {
        if ( c == null )
            return null;
        int[][][] vectors = JsManifestation.canonicalizeConstruction( c );
        if ( vectors == null )
            return null;
        vectors = (int[][][]) ( (Function) this.adapter .$get( "findOrCreateManifestation" ) ).apply( this.adapter, $array( vectors ) );
        if ( vectors == null )
            return null;
        return JsManifestation .manifest( vectors, this.field, this.adapter );
    }

    @Override
    public void remove( Manifestation man )
    {
        int[][][] vectors = ((JsManifestation) man) .getVectors();
        ( (Function) this.adapter .$get( "delete" ) ).apply( this.adapter, $array( vectors ) );
    }

    @Override
    public Manifestation getManifestation( Construction c )
    {
        return findConstruction( c );
    }

    @Override
    public void show( Manifestation man )
    {
        int[][][] vectors = ((JsManifestation) man) .getVectors();
        ( (Function) this.adapter .$get( "showManifestation" ) ).apply( this.adapter, $array( vectors ) );
    }

    @Override
    public void hide( Manifestation man )
    {
        int[][][] vectors = ((JsManifestation) man) .getVectors();
        ( (Function) this.adapter .$get( "hideManifestation" ) ).apply( this.adapter, $array( vectors ) );
    }

    @Override
    public void setColor( Manifestation man, Color color )
    {
        ((JsManifestation) man) .setColor( color );
    }

    @Override
    public void add( Manifestation man )
    {
        int[][][] vectors = ((JsManifestation) man) .getVectors();
        ( (Function) this.adapter .$get( "showManifestation" ) ).apply( this.adapter, $array( vectors ) );
    }

    // Stubbing these three out eliminates a HUGE performance hit, since JS does not like HashMaps with object keys.
    //  In JS we manage uniqueness in a simpler way, anyway.
    
    @Override
    public Manifestation findPerEditManifestation( String signature ) { return null; }

    @Override
    public void addPerEditManifestation( String signature, Manifestation m ) {}

    @Override
    public void clearPerEditManifestations() {}

    
    
    
    
    
    @Override
    public Manifestation removeConstruction(Construction c)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public int size()
    {
        throw new RuntimeException( "unimplemented" );
    }
}
