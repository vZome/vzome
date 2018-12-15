
//(c) Copyright 2010, Scott Vorthmann.

package com.vzome.core.model;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.generic.ArrayComparator;
import com.vzome.core.math.Polyhedron;

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

	private final AlgebraicNumber scale;
    
    public VefModelExporter( Writer writer, AlgebraicField field )
    {
    		this( writer, field, null );
    }

    public VefModelExporter( Writer writer, AlgebraicField field, AlgebraicNumber scale )
    {
        this .scale = scale;
		this .output = new PrintWriter( writer );
        this .field = field;

        FORMAT .setMaximumFractionDigits( 16 );

        ArrayComparator<AlgebraicVector> arrayComparator = new ArrayComparator<>();
        strutEnds = new TreeSet<>( arrayComparator.getContentFirstArrayComparator() );
        panelVertices = new TreeSet<>( arrayComparator.getLengthFirstArrayComparator() );
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

    /**
     * @param buffer = Don't assume that buffer starts out empty. Results will be appended.
     * @param vector = Value to be converted to a zero-padded 4D String which will be 
     * prefixed and/or padded with field specific zeroes
     * depending on the number of dimensions in vector as follows:
     * 1D : 0 X 0 0
     * 2D : 0 X Y 0
     * 3D : 0 X Y Z
     * 4D : W X Y Z
     */
    public static void appendVector( StringBuffer buffer, AlgebraicVector vector, AlgebraicNumber scale )
    {
        String zeroString = vector.getField().zero().toString(AlgebraicField.VEF_FORMAT);
        int dims = vector .dimension();
        if(dims < 4) {
            // prefix with zero in the W dimension
            buffer.append(zeroString);
            buffer.append(" ");
        }

        if ( scale != null )
            vector = vector .scale( scale );            

        // now append the vector (all of its dimensions) from the parameter
        // it will contain at least X and usually Y and Z
        vector .getVectorExpression( buffer, AlgebraicField.VEF_FORMAT );
      
        for( int d = dims + 1; d < 4; d++ ) {
            // append zeroString in the y and z dimensions as needed...
            buffer.append(" ");
            buffer.append(zeroString);
        }
    }


    /**
     * @deprecated As of 8/12/2016 Use {@link #appendVector( StringBuffer buffer, AlgebraicVector vector )} instead.
     */
    @Deprecated
    public static void appendVector( StringBuffer buffer, AlgebraicVector vector )
    {
        appendVector( buffer, vector, null );
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
        for(AlgebraicVector vector : sortedVertexList) {
            appendVector( buf, vector, this .scale );
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

	public static String exportPolyhedron( Polyhedron polyhedron )
	{
    	StringWriter out = new StringWriter();
        Exporter exporter = new VefModelExporter( out, polyhedron .getField() );
    	List<AlgebraicVector> vertexList = polyhedron .getVertexList();
    	for (Polyhedron.Face face : polyhedron .getFaceSet()) {
    		List<AlgebraicVector> vertices = new ArrayList<>( face .size() );
    		for ( int i = 0; i < face.size(); i++ ) {
    			int vertexIndex = face .getVertex( i );
    			vertices.add( vertexList .get( vertexIndex ) );
    		}
    		exporter .exportManifestation( new Panel( vertices ) );
    	}
        exporter .finish();
		return out .toString();
	}
}
