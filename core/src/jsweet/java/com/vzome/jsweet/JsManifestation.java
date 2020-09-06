package com.vzome.jsweet;

import java.util.Arrays;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;
import com.vzome.core.model.Manifestation;

public abstract class JsManifestation implements Manifestation
{
    protected int[][][] vectors;
    protected AlgebraicField field;

    public JsManifestation( AlgebraicField field, int[][][] vectors )
    {
        this.field = field;
        this.vectors = vectors;
    }
    
    public int[][][] getVectors()
    {
        return this.vectors;
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

    public static Manifestation manifest( int[][][] vectors, AlgebraicField field )
    {
        switch ( vectors.length ) {

        case 1:
            return new JsBall( field, vectors );

        case 2:
            // TODO JsStrut
            return null;

        default:
            // TODO JsPanel
            return null;
        }
    }
}
