//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.model;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.RationalVectors;

public class Panel extends Manifestation implements Iterable<int[]>
{
    List mVertices;

    /**
     * Create a panel from an iterator over AlgebraicVectors
     * 
     * @param vertices
     */
    public Panel( List vertices )
    {
        mVertices = vertices;
    }

    public int[] /* AlgebraicVector */ getLocation()
    {
        return null;
    }

    public Iterator getVertices()
    {
        return mVertices.iterator();
    }

    public int hashCode()
    {
        int len = mVertices.size();
        if ( len == 0 )
        	return 0;
        int val = RationalVectors.hashCode( (int[]) mVertices.get( 0 ) );
        for ( int i = 1; i < len; i++ )
            val ^= RationalVectors.hashCode( (int[]) mVertices.get( i ) );
        return val;
    }

    public boolean equals( Object other )
    {
        if ( other == null )
            return false;
        if ( other == this )
            return true;
        if ( ! ( other instanceof Panel ) )
            return false;
        Panel panel = (Panel) other;
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
                if ( Arrays.equals( (int[]) mVertices.get( j ), (int[]) panel.mVertices.get( i ) ) ) {
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

    public int[] /* AlgebraicVector */getNormal( AlgebraicField field )
    {
        int[] /* AlgebraicVector */v0 = (int[] /* AlgebraicVector */) mVertices.get( 0 );
        int[] /* AlgebraicVector */v1 = (int[] /* AlgebraicVector */) mVertices.get( 1 );
        int[] /* AlgebraicVector */v2 = (int[] /* AlgebraicVector */) mVertices.get( 2 );
        v1 = field.subtract( v1, v0 );
        v2 = field.subtract( v2, v0 );
        return field.cross( v1, v2 );
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer( "panel: " );
        for ( Iterator iterator = mVertices.iterator(); iterator.hasNext(); ) {
            int[] vertex = (int[]) iterator.next();
            buf.append( RationalVectors.toString( vertex ) );
            buf.append( ", " );

        }
        return buf.toString();
    }

	@Override
	public Iterator<int[]> iterator()
	{
		return this.getVertices();
	}
}
