package com.vzome.core.algebra;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.vzome.core.generic.ArrayComparator;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.VefParser;
//import com.vzome.core.model.Exporter;
//import com.vzome.core.model.Panel;
//import com.vzome.core.model.VefModelExporter;

public class VefVectorExporter {

    protected final PrintWriter output;
    protected final AlgebraicField field;
    protected ArrayList<AlgebraicVector> sortedVertexList = null;
    private SortedSet<AlgebraicVector> vertices = new TreeSet<>();
    private final SortedSet<AlgebraicVector> ballLocations = new TreeSet<>();
    protected final SortedSet<AlgebraicVector[]> strutEnds;
    protected final SortedSet<AlgebraicVector[]> panelVertices;
    protected final AlgebraicNumber scale;
    protected final boolean includeOffset;
    protected AlgebraicVector exportedOffset = null; // note that this is different from the offset used on import
    protected String strTip = "tip";

    public VefVectorExporter( Writer writer, AlgebraicField field ) {
        this( writer, field, null, false );
    }
    
    public VefVectorExporter( Writer writer, AlgebraicField field, AlgebraicNumber scale, boolean withOffset )
    {
        this.includeOffset = withOffset;
        this .scale = scale;
        this .output = new PrintWriter( writer );
        this .field = field;

        ArrayComparator<AlgebraicVector> arrayComparator = new ArrayComparator<>();
        strutEnds = new TreeSet<>( arrayComparator.getContentFirstArrayComparator() );
        panelVertices = new TreeSet<>( arrayComparator.getLengthFirstArrayComparator() );
    }

    public void exportPoint(AlgebraicVector pt) {
        vertices .add( pt );
        ballLocations .add( pt );
        if(this.includeOffset) {
            this.exportedOffset = pt; // last point selected will ultimately end up as the exported offset 
        }
    }

    public void exportSegment(AlgebraicVector start, AlgebraicVector end) {
        AlgebraicVector[] ends = { start, end };
        vertices .add( ends[0] );
        vertices .add( ends[1] );
        strutEnds .add( ends );
    }

    public void exportPolygon(List<AlgebraicVector> corners) {
        vertices .addAll( corners );
        AlgebraicVector[] cornerArray = new AlgebraicVector[ corners.size() ];
        corners .toArray( cornerArray );
        panelVertices .add( cornerArray );
    }

    protected String strMiddle = "middle";

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
    public static void appendVector(StringBuffer buffer, AlgebraicVector vector, AlgebraicNumber scale) {
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

    public static String exportPolyhedron(Polyhedron polyhedron) {
        StringWriter out = new StringWriter();
        VefVectorExporter exporter = new VefVectorExporter( out, polyhedron .getField() );
        List<AlgebraicVector> vertexList = polyhedron .getVertexList();
        for (Polyhedron.Face face : polyhedron .getFaceSet()) {
            List<AlgebraicVector> vertices = new ArrayList<>( face .size() );
            for ( int i = 0; i < face.size(); i++ ) {
                int vertexIndex = face .getVertex( i );
                vertices.add( vertexList .get( vertexIndex ) );
            }
            exporter .exportPolygon( vertices );
        }
        exporter .finishExport();
        return out .toString();
    }

    public void finishExport() {
        // Up to this point, the vertices TreeSet has collected and sorted every unique vertex of every manifestation.
        // From now on we'll need their index, so we copy them into an ArrayList, preserving their sorted order.
        // so we can get their index into that array.
        sortedVertexList = new ArrayList<>(vertices);
        // we no longer need the vertices collection, 
        // so set it to null to free the memory and to ensure we don't use it later by mistake.
        vertices = null;
    
        // format version 6, with explicit "balls" section, not a ball for every vertex
        // format version 10 includes an explicit offset
        final int version = (exportedOffset == null) 
            ? VefParser.VERSION_EXPLICIT_BALLS // 6 
            : VefParser.VERSION_EXPLICIT_OFFSET; // 10
        output .println( "vZome VEF " + version + " field " + field .getName() );
        
        // offset
        if (exportedOffset != null) {
            StringBuffer buf = new StringBuffer();
            buf.append("\noffset ");
            // note that we negate exportedOffset before writing it
            // so that the last selected ball during a copy
            // will be positioned at a single selected ball (if any) 
            // when it's subsequently pasted. 
            appendVector(buf, exportedOffset.negate(), null);
            buf.append("\n");
            output.println(buf.toString());
        }
    
        // vertices
        output .println( "\n" + sortedVertexList .size() );
        {
            StringBuffer buf = new StringBuffer();
            for(AlgebraicVector vector : sortedVertexList) {
                appendVector( buf, vector, this .scale );
                buf.append("\n");
            }
            buf.append("\n");
            output .println( buf.toString());
        }
    
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