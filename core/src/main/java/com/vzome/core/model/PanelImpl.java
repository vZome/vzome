//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.AlgebraicVectors;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.PolygonFromVertices;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Embedding;

public class PanelImpl extends ManifestationImpl implements Iterable<AlgebraicVector>
{
    private final List<AlgebraicVector> mVertices;
    private AlgebraicVector zoneVector;

    /**
     * Create a panel from a list of AlgebraicVectors
     * 
     * @param vertices
     */
    public PanelImpl( List<AlgebraicVector> vertices )
    {
        // copy the list in case the caller modifies it later.
        mVertices = new ArrayList<>(vertices);
    }

    public AlgebraicVector getZoneVector()
    {
        if ( this .zoneVector != null )
            return this .zoneVector;
        else
            return this .getNormal();
    }

    public void setZoneVector( AlgebraicVector vector )
    {
        this .zoneVector = vector;
    }

    @Override
    public AlgebraicVector getLocation()
    {
        return null;
    }

    public AlgebraicVector getFirstVertex()
    {
        return this .mVertices .get( 0 );
    }

    @Override
    public AlgebraicVector getCentroid()
    {
        return AlgebraicVectors.calculateCentroid(mVertices);
    }

    @Override
    public Construction toConstruction()
    {
        Construction first = this .getFirstConstruction();
        if ( first .is3d() )
            return first;

        AlgebraicField field = mVertices.get( 0 ) .getField();
        List<Point> projected = this .mVertices .stream()
                .map( pt -> new FreePoint( field .projectTo3d( pt, true ) ) )
                .collect( Collectors.toList() ); 
        return new PolygonFromVertices( projected );
    }

    /**
     * @deprecated Consider using a JDK-5 for-loop if possible. Otherwise use {@link #iterator()} instead.
     */
    @Deprecated
    public Iterator<AlgebraicVector> getVertices()
    {
        return this.iterator();
    }

    @Override
    public Iterator<AlgebraicVector> iterator()
    {
        return mVertices.iterator();
    }

    public int getVertexCount()
    {
        return mVertices.size();
    }

    @Override
    public int hashCode()
    {
        int len = mVertices.size();
        if ( len == 0 )
            return 0;
        int val = ( mVertices.get( 0 ) ) .hashCode();
        for ( int i = 1; i < len; i++ )
            val ^= ( mVertices.get( i ) ) .hashCode();
        return val;
    }

    @Override
    public boolean equals( Object other )
    {
        if ( other == null )
            return false;
        if ( other == this )
            return true;
        if ( ! ( other instanceof PanelImpl ) )
            return false;
        PanelImpl panel = (PanelImpl) other;
        int size = mVertices.size();
        if ( size != panel.mVertices.size() )
            return false;

        /*
         * I am switching panel equality back to the original approach, which regards both orientations
         * as identical.  I switched to the other approach to allow creation of a "fixed" panel when a
         * badly oriented panel was discovered.  However, this introduces problems when different
         * symmetries (reversing and non-reversing) are used, as in Brendo's tiling models.
         * 
         * Therefore I'm switching it back, and I will provide a way to render panels in an oriented
         * fashion, plus a command to reorient a panel.
         */

        // equal only if the same cycle of vertices, modulo any offset, but no reordering or reversal
        //        int offset = -1;
        //        for ( int i = 0; i < size; i++ )
        //            if ( Arrays.equals( (int[]) mVertices.get( 0 ), (int[]) panel.mVertices.get( i ) ) ) {
        //                offset = i;
        //                break;
        //            }            
        //        if ( offset == -1 )
        //            return false;
        //        for ( int j = 1; j < size; j++ )
        //            if ( ! Arrays.equals( (int[]) mVertices.get( j ), (int[]) panel.mVertices.get( (j+offset) % size ) ) )
        //                return false;
        //        return true;

        // the old way... equal whenever the vertex sets are the same, regardless of order
        boolean[] found = new boolean[ size ];
        for ( int i = 0; i < size; i++ ) {
            boolean found_i = false;
            for ( int j = 0; j < size; j++ ) {
                if ( found[j] )
                    continue;
                if ( mVertices.get( j ) .equals( panel.mVertices.get( i ) ) ) {
                    found[j] = true;
                    found_i = true;
                    break;
                }
            }
            if ( ! found_i )
                return false;
        }
        for ( int j = 0; j < size; j++ )
            if ( ! found[j] )
                return false;
        return true;
    }

    public AlgebraicVector getNormal( )
    {
        AlgebraicVector v0 = mVertices.get( 0 );
        AlgebraicVector v1 = mVertices.get( 1 );
        AlgebraicVector v2 = mVertices.get( 2 );
        return AlgebraicVectors.getNormal(v0, v1, v2);
    }

    public RealVector getNormal( Embedding embedding )
    {
        AlgebraicVector v0 = mVertices.get( 0 );
        AlgebraicVector v1 = mVertices.get( 1 );
        AlgebraicVector v2 = mVertices.get( 2 );
        RealVector rv1 = embedding .embedInR3( v1 .minus( v0 ) );
        RealVector rv2 = embedding .embedInR3( v2 .minus( v0 ) );
        return rv1 .cross( rv2 );
    }

    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder( "panel: " );
        String delim = "";
        for ( AlgebraicVector vertex: mVertices ) {
            buf.append( delim ).append( vertex .toString() );
            delim = ", ";
        }
        return buf.toString();
    }

}
