package com.vzome.jsweet;

import static jsweet.util.Lang.$array;

import java.util.Arrays;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Color;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;
import com.vzome.core.model.Manifestation;

import def.js.Function;
import def.js.Object;

public abstract class JsManifestation implements Manifestation
{
    protected final int[][][] vectors;
    protected final AlgebraicField field;
    private Color color;
    protected final Object adapter;

    public JsManifestation( AlgebraicField field, Object adapter, int[][][] vectors )
    {
        this.field = field;
        this.adapter = adapter;
        this.vectors = vectors;
    }
    
    public int[][][] getVectors()
    {
        return this.vectors;
    }

    @Override
    public Color getColor()
    {
        return this.color;
    }

    @Override
    public void setColor( Color color )
    {
        this.color = color;
    }

    @Override
    public boolean isRendered()
    {
        return (boolean) ( (Function) this.adapter .$get( "manifestationRendered" ) ).apply( this.adapter, $array( vectors ) );
    }

    @Override
    public Construction getFirstConstruction()
    {
        return toConstruction();
    }

    
    
    public static int[][] canonicalizeNumbers( AlgebraicNumber... ns )
    {
        return (int[][]) Arrays.stream( ns ) .map( n -> n .toTrailingDivisor() ) .toArray();
    }
    
    /**
     * Note: this does NOT order the vectors canonically in the outermost array
     * @param vs
     * @return
     */
    public static int[][][] canonicalizeVectors( AlgebraicVector... vs )
    {
        return (int[][][]) Arrays.stream( vs ) .map( v -> canonicalizeNumbers( v .getComponents() ) ) .toArray();
    }
    
    public static int[][][] canonicalizeConstruction( Construction c )
    {
        if ( c instanceof Point )
        {
            Point p = (Point) c;
            return canonicalizeVectors( p .getLocation() );
        }
        else if ( c instanceof Segment )
        {
            Segment s = (Segment) c;
            return canonicalizeVectors( s .getStart(), s .getEnd() );
        }
        else if ( c instanceof Polygon )
        {
            return canonicalizeVectors( ((Polygon) c) .getVertices() );
        }
        return null;
    }

    public static Manifestation manifest( int[][][] vectors, AlgebraicField field, Object adapter )
    {
        switch ( vectors.length ) {

        case 1:
            return new JsBall( field, adapter, vectors );

        case 2:
            return new JsStrut( field, adapter, vectors );

        default:
            // TODO JsPanel
            return null;
        }
    }
}
