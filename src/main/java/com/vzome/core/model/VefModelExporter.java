
//(c) Copyright 2010, Scott Vorthmann.

package com.vzome.core.model;

import java.io.PrintWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.Polyhedron;

public class VefModelExporter implements Exporter
{
    private static final NumberFormat FORMAT = NumberFormat .getNumberInstance( Locale .US );

    private final Map<AlgebraicVector, Integer> vertexData = new LinkedHashMap<>();

    protected final AlgebraicField field;

    private final int order;

    private final AlgebraicNumber scale;

    private final StringBuffer vertices = new StringBuffer();

    private int numBalls;

    private int numStruts;

    private final StringBuffer balls;

    private final StringBuffer panels;

    private final StringBuffer struts;

    private int numPanels;

    private final PrintWriter output;
    
    private boolean writingTip;
    
    private final Set<Integer> middleVertices = new HashSet<>();

    public VefModelExporter( Writer writer, AlgebraicField field, AlgebraicNumber scale )
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
    
    public void exportStrutPolyhedron( Polyhedron poly, AlgebraicMatrix rotation, boolean reverseFaces, AlgebraicVector endpoint )
    {
        // I have a challenge here, since there are apparently some strut models that contain duplicate
        // vertices: same location, several indices.  Since this.vertices wants to have unique locations,
        // (a Map, not a List), I need to make a mapping from polyhedron vertex index to VEF vertex index.
        //
        int[] vertexMapping = new int[ poly .getVertexList() .size() ];
        int vertexNum = 0;
        for ( Iterator verts = poly .getVertexList() .iterator(); verts .hasNext(); ) {
            AlgebraicVector vertexVector = (AlgebraicVector) verts .next();
            if ( reverseFaces )
                vertexVector = vertexVector .negate();
            vertexVector = rotation .timesColumn( vertexVector );
            // have to inline getVertexIndex here, so I can create balls.
            Integer vefIndex = vertexData .get( vertexVector );
            if ( vefIndex == null )
            {
                AlgebraicVector key = vertexVector;
                int index = vertexData .size();
                vefIndex = index;
                vertexData .put( key, vefIndex );
                if ( scale != null )
                    vertexVector = vertexVector .scale( scale );            
                appendVector( this.vertices, order, vertexVector );            
                this.vertices .append( "\n" );
                ++ numBalls;
                balls .append( " " );
                balls .append( vefIndex );
            }
            vertexMapping[ vertexNum++ ] = vefIndex;
        }
        ++ numBalls;
        balls .append( " " );
        balls .append( getVertexIndex( endpoint ) );

        for ( Iterator<Polyhedron.Face> faces = poly .getFaceSet() .iterator(); faces .hasNext(); ){
            Polyhedron.Face face = faces .next();
            ++ numPanels;
            List<Integer> vs = new ArrayList<>();
            int arity = face .size();
            for ( int j = 0; j < arity; j++ ){
                Integer index = face .get( /*reverseFaces? arity-j-1 :*/ j );
                vs .add( vertexMapping[ index ] );
            }
            panels .append( vs .size() );
			for (Integer v : vs) {
				panels .append( " " );
				panels .append(v);
			}
            panels .append( "\n" );
        }
    }

	@Override
    public void exportManifestation( Manifestation man )
    {
        if ( man instanceof Connector )
        {
            ++ numBalls;
            AlgebraicVector loc = ((Connector) man) .getLocation();
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
            List<Integer> vs = new ArrayList<>();
            for ( Iterator verts = ((Panel) man) .getVertices(); verts .hasNext(); )
                vs .add( getVertexIndex( (AlgebraicVector) verts .next() ));
            panels .append( vs .size() );
			for (Integer v : vs) {
				panels .append( " " );
				panels .append(v);
			}
            panels .append( "\n" );
        }
    }
    
    /**
     * This is used only for vZome part geometry export    
     * @param man
     */
    public void exportSelectedManifestation( Manifestation man )
    {
		if(man == null) 
		{
			// This is called to append a newline after all of the panels are exported
			// to provide readability and separation of whatever new field may eventually be appended.
			output .println( "");
		}
		else if ( man instanceof Connector )
        {
        	if ( ! this .writingTip ) {
        		output .print( "tip" );
        		this .writingTip = true;
        	}
            AlgebraicVector loc = ((Connector) man) .getLocation();
            output .println( " " + getVertexIndex( loc ) );
        }
        else if ( man instanceof Panel ) {
        	if ( this .middleVertices .isEmpty() )
        		output .print( "middle" );
            for ( Iterator verts = ((Panel) man) .getVertices(); verts .hasNext(); ) {
                Integer index = getVertexIndex( (AlgebraicVector) verts .next() );
                if ( ! this .middleVertices .contains( index ) ) {
                    this .middleVertices .add( index );
                	output .print( " " + index );
                }
            }
        }
    }
    
    protected Integer getVertexIndex( AlgebraicVector vertexVector )
    {
        Integer obj = vertexData .get(vertexVector);
        if ( obj == null )
        {
            AlgebraicVector key = vertexVector;
            int index = vertexData .size();
            obj = index;
            vertexData .put( key, obj );
            if ( scale != null )
                vertexVector = vertexVector .scale( scale );            
            appendVector( vertices, order, vertexVector );            
            vertices .append( "\n" );
        }
        return obj;
    }

    public static void appendVector( StringBuffer buffer, int order, AlgebraicVector vector )
    {
        int dims = vector .dimension();
        if ( dims < 4 )
        {
            buffer .append( "(" );
            for ( int k = 0; k < order-1; k++ )
                buffer .append( "0," );
            buffer .append( "0) " );
        }
        vector .getVectorExpression( buffer, AlgebraicField.VEF_FORMAT );
    }
        
	@Override
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
