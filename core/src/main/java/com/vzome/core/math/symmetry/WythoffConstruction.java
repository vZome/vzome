
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.math.symmetry;

import java.io.PrintWriter;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;

public class WythoffConstruction
{
    public interface Listener
    {
        Object addVertex( AlgebraicVector v );
        
        Object addEdge( Object p1, Object p2 );
        
        Object addFace( Object[] vertices );
    }
    
    public static void constructPolytope( CoxeterGroup group, int index, int edgesToRender, AlgebraicNumber[] edgeScales, CoxeterGroup renderingGroup, Listener listener )
    {
        AlgebraicVector[] neighbors = new AlgebraicVector[4];
        
        // TODO remove; TEMPORARY hack to see chiral stuff
        boolean chiral = false;
        if ( chiral )
        {
            index = edgesToRender = 0xF;
        }

        AlgebraicVector origin = group .getOrigin();
        AlgebraicVector model = origin;
        int bits = index;
        // We always use all the weights indicated by the bits of index, so we create vertices based on that.
        for ( int i = 0; i < 4; i++ ) {
            if ( bits % 2 == 1 )
                model = model .plus( group .getWeight( i ) .scale( edgeScales[ i ] ) );
            bits >>= 1;
        }
        bits = index;
        // We only create edges based on edgesToRender, so we can select the surtopes we want to render.
        for ( int i = 0; i < 4; i++ ) {
            if ( ( bits % 2 == 1 ) && ( edgesToRender % 2 == 1 ) )
                neighbors[ i ] = model .minus( group .getSimpleRoot( i ) .scale( edgeScales[ i ] ) );
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
                AlgebraicVector vector = renderingGroup .chiralSubgroupAction( model, i );
                if ( vector == null )
                    continue;  // must have been an odd number of sign changes
                for ( int e = 0; e < 4; e++ )
                    for ( int f = e+1; f< 4; f++ )
                    {
                        AlgebraicVector v1 = renderingGroup .chiralSubgroupAction( neighbors[ e ], i );
                        Object p1 = listener .addVertex( v1 );
                        AlgebraicVector v2 = renderingGroup .chiralSubgroupAction( neighbors[ f ], i );
                        Object p2 = listener .addVertex( v2 );
                        listener .addEdge( p1, p2 );
                    }
            }
        }
        else
            for ( int i = 0; i < order; i++ )
            {
                AlgebraicVector vector = renderingGroup .groupAction( model, i );
                Object p = listener .addVertex( vector );
                for ( int e = 0; e < 4; e++ )
                {
                    if ( neighbors[ e ] .equals( origin ) )
                        continue;
                    AlgebraicVector other = renderingGroup .groupAction( neighbors[ e ], i );
                    if ( ! other .equals( vector ) ) {
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

        @Override
        public Object addEdge( Object p1, Object p2 )
        {
            vefEdges .append( ((Integer) p1) + "\t" + ((Integer) p2) + "\n" );
            ++numEdges;
            return null;
        }

        @Override
        public Object addFace( Object[] vertices )
        {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Object addVertex( AlgebraicVector gv )
        {
            gv .getVectorExpression( vefVertices, AlgebraicField .VEF_FORMAT );  // TODO not finished replacing the lines below
            vefVertices .append( "\n" );
            
            return numVertices++;
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
}
