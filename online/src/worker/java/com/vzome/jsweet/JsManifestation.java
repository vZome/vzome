package com.vzome.jsweet;

import static jsweet.util.Lang.$array;

import java.util.Arrays;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Color;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Polygon;
import com.vzome.core.construction.Segment;
import com.vzome.core.model.Group;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.RenderedObject;

import def.js.Function;
import def.js.Object;

public abstract class JsManifestation implements Manifestation
{
    protected final int[][][] vectors;
    protected final AlgebraicField field;
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
        boolean colorful = (boolean) ( (Function) this.adapter .$get( "manifestationHasColor" ) ).apply( this.adapter, $array( vectors ) );
        if ( ! colorful )
            return null;
        int rgb = (int) ( (Function) this.adapter .$get( "manifestationColor" ) ).apply( this.adapter, $array( vectors ) );
        return new Color( rgb );
    }

    @Override
    public void setColor( Color color )
    {
        if ( color != null )
            ( (Function) this.adapter .$get( "setManifestationColor" ) ).apply( this.adapter, $array( vectors, color.getRGB() ) );
    }

    @Override
    public boolean isRendered()
    {
        return (boolean) ( (Function) this.adapter .$get( "manifestationRendered" ) ).apply( this.adapter, $array( vectors ) );
    }

    @Override
    public void addConstruction(Construction mConstruction)
    {}

    @Override
    public void removeConstruction(Construction mConstruction)
    {}

    @Override
    public boolean isUnnecessary()
    {
        return true;
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
            JsStrut strut = new JsStrut( field, adapter, vectors );
            if ( strut .getOffset() .isOrigin() )
                return null; // consistent with Java implementation
            else
                return strut;

        default:
            // TODO JsPanel
            return new JsPanel( field, adapter, vectors );
        }
    }

    @Override
    public void setHidden( boolean b ) {}  // State will be in the Adapter
    
    @Override
    public boolean isHidden()
    {
        return (boolean) ( (Function) this.adapter .$get( "manifestationHidden" ) ).apply( this.adapter, $array( vectors ) );
    }

    @Override
    public void setContainer( Group container )
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public Group getContainer()
    {
        int[][][][] members = (int[][][][]) ( (Function) this.adapter .$get( "getLargestGroup" ) ).apply( this.adapter, $array( vectors ) );
        if ( members == null )
            return null;
        Group group = new Group();
        for ( int i = 0; i < members.length; i++ ) {
            group.add( manifest( members[ i ], field, adapter ) );
        }
        return group;
    }
    
    @Override
    public void setRenderedObject( RenderedObject renderedObject ) {}

    
    
    
    
    
    @Override
    public Iterator<Construction> getConstructions()
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public Element getXml( Document doc )
    {
        throw new RuntimeException( "unimplemented" );
    }

    @Override
    public AlgebraicVector getCentroid()
    {
        throw new RuntimeException( "unimplemented" );
    }
}
