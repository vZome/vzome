
//(c) Copyright 2010, Scott Vorthmann.

package com.vzome.core.model;

import java.io.PrintWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.generic.ArrayComparator;
import com.vzome.core.math.Polyhedron;
import java.util.SortedSet;
import java.util.TreeSet;

public class VefModelExporter implements Exporter
{
    private final PrintWriter output;

    protected final AlgebraicField field;

    private static final NumberFormat FORMAT = NumberFormat .getNumberInstance( Locale .US );

    private ArrayList<AlgebraicVector> sortedVertexList = null;
    private SortedSet<AlgebraicVector> vertices = new TreeSet<>();
    private final SortedSet<AlgebraicVector> ballLocations = new TreeSet<>();
    private final SortedSet<AlgebraicVector[]> strutEnds;
    private final SortedSet<AlgebraicVector[]> panelVertices;
    
    public VefModelExporter( Writer writer, AlgebraicField field )
    {
        this .output = new PrintWriter( writer );
        this .field = field;

        FORMAT .setMaximumFractionDigits( 16 );

        ArrayComparator<AlgebraicVector> arrayComparator = new ArrayComparator<>();
        strutEnds = new TreeSet<>( arrayComparator.getContentFirstArrayComparator() );
        panelVertices = new TreeSet<>( arrayComparator.getLengthFirstArrayComparator() );
    }

    /**
     * @deprecated On 8/10/2016
     * Use {@link #VefModelExporter( Writer writer, AlgebraicField field )}
     * since the scale parameter is no longer used.
     */
    @Deprecated
    public VefModelExporter( Writer writer, AlgebraicField field, AlgebraicNumber scale )
    {
        this(writer, field); // scale is no longer used
    }

    /**
    * @deprecated On 8/10/2016 I realized that exportStrutPolyhedron() is not being used
    * by the current vzome-core or vzome-desktop, and it's not compatible with the new code for sorting vertices
    * so I have deprecated it and commented out all of the code.
    * I'll leave it here for now so we don't need to bump the version yet.
    */
    @Deprecated
    public void exportStrutPolyhedron( Polyhedron poly, AlgebraicMatrix rotation, boolean reverseFaces, AlgebraicVector endpoint )
    {
    }

	@Override
    public void exportManifestation( Manifestation man )
    {
        if ( man instanceof Connector )
        {
            AlgebraicVector loc = man .getLocation();
            vertices.add(loc);
            ballLocations.add(loc);
        }
        else if ( man instanceof Strut )
        {
            Strut strut = (Strut) man;
            AlgebraicVector[] ends = {
                // we must preserve the strut direction since it may be significant later
                // such as when it's used for a translation tool's direction parameter
                // so don't use getCanonicalLesserEnd() and getCanonicalGreaterEnd() here
                strut .getLocation(),
                strut .getEnd()
            };
            vertices.add(ends[0]);
            vertices.add(ends[1]);
            strutEnds.add(ends);
        }
        else if ( man instanceof Panel )
        {
            Panel panel = (Panel) man;
            // Unsorted list retains the panel vertex order
            ArrayList<AlgebraicVector> corners = new ArrayList<>(panel.getVertexCount());
            for (AlgebraicVector vertex : panel) {
                corners .add( vertex );
            }
            vertices.addAll(corners);
            AlgebraicVector[] cornerArray = new AlgebraicVector[corners.size()];
            corners.toArray(cornerArray);
            panelVertices.add( cornerArray );
        }
    }

    private String strTip = "tip";
    private String strMiddle = "middle";

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
			output .println();
			output .flush();
		}
		else if ( man instanceof Connector )
        {
        	if ( strTip != null ) {
        		output .print( strTip );
        		strTip = null; // indicates that it's already been written
        	}
            AlgebraicVector loc = man .getLocation();
            output .println( " " + sortedVertexList.indexOf(loc));
        }
        else if ( man instanceof Panel ) {
        	if ( strMiddle != null ) {
        		output .println( strMiddle );
        		strMiddle = null; // indicates that it's already been written
        	}
            Panel panel = (Panel) man;
            for (AlgebraicVector corner : panel) {
                output .print( sortedVertexList.indexOf( corner ) + " " );
            }
            output.println();
        }
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
        // Up to this point, the vertices TreeSet has collected and sorted every unique vertex of every manifestation.
        // From now on we'll need their index, so we copy them into an ArrayList, preserving their sorted order.
        // so we can get their index into that array.
        sortedVertexList = new ArrayList<>(vertices);
        // we no longer need the vertices collection, 
        // so set it to null to free the memory and to ensure we don't use it later by mistake.
        vertices = null;

        // format version 6, with explicit "balls" section, not a ball for every vertex
        output .println( "vZome VEF 6 field " + field .getName() );


        // vertices
        output .println( "\n" + sortedVertexList .size() );
        StringBuffer buf = new StringBuffer();
        int order = field.getOrder();
        for(AlgebraicVector vector : sortedVertexList) {
            appendVector(buf, order, vector);
            buf.append("\n");
        }
        output .println( buf.toString() + "\n" );

        // strut ends as vertex index pairs
        output .println( "\n" + strutEnds.size() );
        for(AlgebraicVector[] ends : strutEnds) {
            output .print( sortedVertexList.indexOf(ends[0]) + " " );
            output .println( sortedVertexList.indexOf(ends[1]) );
        }
        output .println( "\n" );


        // panel corners as vertex index sets
        output .println( "\n" + panelVertices.size() );
        for(AlgebraicVector[] corners : panelVertices) {
            output .print( corners.length + "  " );
            for(AlgebraicVector corner : corners) {
                output .print( sortedVertexList.indexOf(corner) + " " );
            }
            output .println();
        }
        output .println( "\n" );


        // balls as vertex indices
        output .println( "\n" + ballLocations.size() );
        int i = 0;
        for(AlgebraicVector ball : ballLocations) {
            output .print( sortedVertexList.indexOf(ball) + " " );
            if(++i % 10 == 0) {
                output. println(); // wrap lines for readability
            }
        }
        output .println( "\n" );

        output .flush();
    }
}
