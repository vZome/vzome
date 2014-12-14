
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.math.symmetry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.PentagonField;


public class WythoffConstruction
{
    public interface Listener
    {
        Object addVertex( int[] /**/ v );
        
        Object addEdge( Object p1, Object p2 );
        
        Object addFace( Object[] vertices );
    }
    
    public static void constructPolytope( CoxeterGroup group, int index, int edgesToRender, int[][] edgeScales, CoxeterGroup renderingGroup, Listener listener )
    {
        int[] /*AlgebraicVector*/[] neighbors = new int[4] /*AlgebraicVector*/[];
        
        // TODO remove; TEMPORARY hack to see chiral stuff
        boolean chiral = false;
        if ( chiral )
        {
            index = edgesToRender = 0xF;
        }

        int[] /*AlgebraicVector*/ origin = group .getOrigin();
        int[] /*AlgebraicVector*/ model = origin;
        AlgebraicField field = group .getField();
        int bits = index;
        // We always use all the weights indicated by the bits of index, so we create vertices based on that.
        for ( int i = 0; i < 4; i++ ) {
            if ( bits % 2 == 1 )
                model = field .add( model, field .scaleVector( group .getWeight( i ), edgeScales[ i ] ) );
            bits >>= 1;
        }
        bits = index;
        // We only create edges based on edgesToRender, so we can select the surtopes we want to render.
        for ( int i = 0; i < 4; i++ ) {
            if ( ( bits % 2 == 1 ) && ( edgesToRender % 2 == 1 ) )
                neighbors[ i ] = field .subtract( model, field .scaleVector( group .getSimpleRoot( i ), edgeScales[ i ] ) );
            else
                neighbors[ i ] = origin;
            bits >>= 1;
            edgesToRender >>= 1;
        }

        // TODO validate that renderingGroup is a subgroup of group
        int order = renderingGroup .getOrder();
        
        if ( chiral )
        {
            for ( int i = 0; i < order; i++ )
            {
                int[] /*AlgebraicVector*/ vector = renderingGroup .chiralSubgroupAction( model, i );
                if ( vector == null )
                    continue;  // must have been an odd number of sign changes
                for ( int e = 0; e < 4; e++ )
                    for ( int f = e+1; f< 4; f++ )
                    {
                        int[] /*AlgebraicVector*/ v1 = renderingGroup .chiralSubgroupAction( neighbors[ e ], i );
                        Object p1 = listener .addVertex( v1 );
                        int[] /*AlgebraicVector*/ v2 = renderingGroup .chiralSubgroupAction( neighbors[ f ], i );
                        Object p2 = listener .addVertex( v2 );
                        listener .addEdge( p1, p2 );
                    }
            }
        }
        else
            for ( int i = 0; i < order; i++ )
            {
                int[] /*AlgebraicVector*/ vector = renderingGroup .groupAction( model, i );
                Object p = listener .addVertex( vector );
                for ( int e = 0; e < 4; e++ )
                {
                    if ( Arrays .equals( neighbors[ e ], origin ) )
                        continue;
                    int[] /*AlgebraicVector*/ other = renderingGroup .groupAction( neighbors[ e ], i );
                    if ( ! Arrays .equals( other, vector ) ) {
                        Object p2 = listener .addVertex( other );
                        listener .addEdge( p, p2 );
                    }
                }
            }
    }

    public static class VefPrinter implements Listener
    {
        private StringBuffer vefVertices = new StringBuffer();
        private StringBuffer vefEdges = new StringBuffer();
        int numEdges = 0, numVertices = 0;
        AlgebraicField field;

        public VefPrinter( AlgebraicField field2 )
        {
            this .field = field2;
        }

        public Object addEdge( Object p1, Object p2 )
        {
            vefEdges .append( ((Integer) p1) .intValue() + "\t" + ((Integer) p2) .intValue() + "\n" );
            ++numEdges;
            return null;
        }

        public Object addFace( Object[] vertices )
        {
            // TODO Auto-generated method stub
            return null;
        }

        public Object addVertex( int[] /*AlgebraicVector*/ gv )
        {
            field .getVectorExpression( vefVertices, gv, AlgebraicField .VEF_FORMAT );  // TODO not finished replacing the lines below
            vefVertices .append( "\n" );
            
            return new Integer( numVertices++ );
        }
        
        public void print( PrintWriter out )
        {
            out .println( "vZome VEF 5" );
            out .println( numVertices );
            out .println( vefVertices .toString() );
            out .println( numEdges );
            out .println( vefEdges .toString() );
            out .close();
        }
    }
    
    public static void main( String[] args )
    {
        String groupName = args[ 0 ];
        AlgebraicField field = new PentagonField();
        CoxeterGroup group = new B4Group( field );
        if ( "B4" .equals( groupName ) )
            group = new B4Group( field );
        else if ( "A4" .equals( groupName ) )
            group = new A4Group( field );
        else if ( "F4" .equals( groupName ) )
            group = new F4Group( field );
        int index = Integer .parseInt( args[ 1 ], 2 );
        VefPrinter listener = new VefPrinter( field );
        int[][] scales = new int[4][];
        for (int i = 0; i < scales.length; i++)
        {
            scales[ i ] = field .createPower( i );
        }
        constructPolytope( group, index, index, scales, group, listener );

        String wythoff = Integer .toBinaryString( index );
        try {
            PrintWriter out = new PrintWriter( new FileWriter( new File( groupName + "_" + wythoff + ".vef" ) ) );
            listener .print( out );
            out .close();
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
