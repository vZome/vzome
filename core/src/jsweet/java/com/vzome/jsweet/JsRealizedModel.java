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
    private final Object adapter;

    public JsRealizedModel( AlgebraicField field, Object adapter )
    {
        this.field = field;
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
        vectors = (int[][][]) ( (Function) this.adapter .$get( "findOrAddManifestation" ) ).apply( this.adapter, $array( vectors ) );
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
    public void show( Manifestation mManifestation )
    {
        System.out.println( "show ball at: " + mManifestation .getLocation() .toRealVector() );
    }

    @Override
    public void hide( Manifestation mManifestation )
    {
        System.out.println( "hide ball at: " + mManifestation .getLocation() .toRealVector() );
    }

    
    
    @Override
    public Manifestation removeConstruction(Construction c)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public Manifestation getManifestation(Construction c)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public int size()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void add(Manifestation m)
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public void setColor(Manifestation manifestation, Color color)
    {
        throw new RuntimeException( "unimplemented" );
    }
}
