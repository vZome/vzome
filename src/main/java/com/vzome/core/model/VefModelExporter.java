
//(c) Copyright 2010, Scott Vorthmann.

package com.vzome.core.model;

import java.io.PrintWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.RationalNumbers;
import com.vzome.core.math.Polyhedron;

public class VefModelExporter implements Exporter
{
    private static final NumberFormat FORMAT = NumberFormat .getNumberInstance( Locale .US );

    private Map vertexData = new HashMap();

    protected final AlgebraicField field;

    private final int order;

    private final int[] scale;

    private final StringBuffer vertices = new StringBuffer();

    private int numBalls;

    private int numStruts;

    private StringBuffer balls;

    private StringBuffer panels;

    private StringBuffer struts;

    private int numPanels;

    private PrintWriter output;

    public VefModelExporter( Writer writer, AlgebraicField field, int[] scale )
    {
        this .output = new PrintWriter( writer );
        this .field = field;
        this .scale = scale;
        order = field .getOrder();

        FORMAT .setMaximumFractionDigits( 16 );

        numBalls = 0;
        numStruts = 0;
        numPanels = 0;
        balls = new StringBuffer();
        struts = new StringBuffer();
        panels = new StringBuffer();
    }
    
    public void exportStrutPolyhedron( Polyhedron poly, int[][] rotation, boolean reverseFaces, int[] endpoint )
    {
        AlgebraicField field = poly .getField();
        // I have a challenge here, since there are apparently some strut models that contain duplicate
        // vertices: same location, several indices.  Since this.vertices wants to have unique locations,
        // (a Map, not a List), I need to make a mapping from polyhedron vertex index to VEF vertex index.
        //
        int[] vertexMapping = new int[ poly .getVertexList() .size() ];
        int vertexNum = 0;
        for ( Iterator vertices = poly .getVertexList() .iterator(); vertices .hasNext(); ) {
            int[] /*AlgebraicVector*/ vertexVector = (int[] /*AlgebraicVector*/) vertices .next();
            if ( reverseFaces )
                vertexVector = field .negate( vertexVector  );
            vertexVector = field .transform( rotation, vertexVector );
            // have to inline getVertexIndex here, so I can create balls.
            Integer vefIndex = (Integer) vertexData .get( new AlgebraicVector( vertexVector ) );
            if ( vefIndex == null )
            {
                AlgebraicVector key = new AlgebraicVector( vertexVector );
                int index = vertexData .size();
                vefIndex = new Integer( index );
                vertexData .put( key, vefIndex );
                if ( scale != null )
                    vertexVector = field .scaleVector( vertexVector, scale );            
                appendVector( this.vertices, order, vertexVector );            
                this.vertices .append( "\n" );
                ++ numBalls;
                balls .append( " " );
                balls .append( vefIndex );
            }
            vertexMapping[ vertexNum++ ] = vefIndex .intValue();
        }
        ++ numBalls;
        balls .append( " " );
        balls .append( getVertexIndex( endpoint ) );

        for ( Iterator faces = poly .getFaceSet() .iterator(); faces .hasNext(); ){
            ++ numPanels;
            Polyhedron.Face face = (Polyhedron.Face) faces .next();
            List vs = new ArrayList();
            int arity = face .size();
            for ( int j = 0; j < arity; j++ ){
                Integer index = (Integer) face .get( /*reverseFaces? arity-j-1 :*/ j );
                vs .add( vertexMapping[ index ] );
            }
            panels .append( vs .size() );
            for ( int i = 0; i < vs .size(); i++ )
            {
                panels .append( " " );
                panels .append( vs .get( i ) );
            }
            panels .append( "\n" );
        }
    }

    public void exportManifestation( Manifestation man )
    {
        if ( man instanceof Connector )
        {
            ++ numBalls;
            int[] loc = ((Connector) man) .getLocation();
            balls .append( " " );
            balls .append( getVertexIndex( loc ) );
        }
        else if ( man instanceof Strut )
        {
            ++ numStruts;
            Strut strut = (Strut) man;
            struts .append( getVertexIndex( strut .getLocation() ) );
            struts .append( " " );
            struts .append( getVertexIndex( strut .getEnd() ) );
            struts .append( "\n" );
        }
        else if ( man instanceof Panel )
        {
            ++ numPanels;
            List vs = new ArrayList();
            for ( Iterator verts = ((Panel) man) .getVertices(); verts .hasNext(); )
                vs .add( getVertexIndex( (int[]) verts .next() ));
            panels .append( vs .size() );
            for ( int i = 0; i < vs .size(); i++ )
            {
                panels .append( " " );
                panels .append( vs .get( i ) );
            }
            panels .append( "\n" );
        }
    }
    
    /**
     * This is used only for vZome part geometry export    
     * @param man
     * @param first
     */
    public void exportSelectedManifestation( Manifestation man, boolean first )
    {
        if ( man instanceof Connector )
        {
            int[] loc = ((Connector) man) .getLocation();
            if ( first )
                output .println( "tip " + getVertexIndex( loc ) );
            else
                output .print( " " + getVertexIndex( loc ) );
        }
    }
    
    protected Integer getVertexIndex( int[] vertexVector )
    {
        Integer obj = (Integer) vertexData .get( new AlgebraicVector( vertexVector ) );
        if ( obj == null )
        {
            AlgebraicVector key = new AlgebraicVector( vertexVector );
            int index = vertexData .size();
            obj = new Integer( index );
            vertexData .put( key, obj );
            if ( scale != null )
                vertexVector = field .scaleVector( vertexVector, scale );            
            appendVector( vertices, order, vertexVector );            
            vertices .append( "\n" );
        }
        return obj;
    }

    public static void appendVector( StringBuffer buffer, int order, int[] vector )
    {
        int dims = vector .length / (2*order);
        if ( dims < 4 )
        {
            buffer .append( "(" );
            for ( int k = 0; k < order-1; k++ )
                buffer .append( "0," );
            buffer .append( "0) " );
        }
        for ( int i = 0; i < dims; i++ )
            appendNumber( buffer, vector, i, order );
    }
    
    public static void appendNumber( StringBuffer buffer, int[] vector, int startIndex, int order )
    {
        buffer .append( " (" );
        for ( int k = order-1; k > 0; k-- )
        {
            buffer .append( RationalNumbers.toString( vector, startIndex*order + k ) );
            buffer .append( "," );
        }
        buffer .append( RationalNumbers.toString( vector, startIndex*order ) );
        buffer .append( ") " );
    }
    
    public void finish()
    {
        // format version 6, with explicit "balls" section, not a ball for every vertex
        output .println( "vZome VEF 6 field " + field .getName() );
//        if ( outputScale != null )
//        {
//            StringBuffer scaleBuf = new StringBuffer( " scale " );
//            appendNumber( scaleBuf, outputScale, 0, order );
//            output .println( scaleBuf );
//        }
        output .println( "\n" + vertexData .size() + "\n" + vertices + "\n" );
        
        output .println( "\n" + numStruts + "\n" + struts + "\n" );

        output .println( "\n" + numPanels + "\n" + panels + "\n" );

        output .println( "\n" + numBalls + "\n" + balls + "\n" );

        output .flush();
    }
}
