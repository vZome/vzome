package com.vzome.core.exporters;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.editor.api.Manifestations;
import com.vzome.core.math.RealVector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.xml.ResourceLoader;

/**
 * This only exports shapes, no instances yet.
 * 
 * @author vorth
 */
public class STEPExporter extends GeometryExporter
{	
	private static final String PREAMBLE_FILE = "com/vzome/core/exporters/step/preamble.step";
    
    private static final String POSTLUDE = "ENDSEC;\nEND-ISO-10303-21;";
    
    private static final int START_INDEX = 300;
    
    @Override
    public void doExport( File file, Writer writer, int height, int width ) throws Exception
    {
        output = new PrintWriter( writer );

        String preamble = ResourceLoader.loadStringResource( PREAMBLE_FILE );

        // preamble = preamble .replaceAll( "_____FILENAME_____", file .getName() );
        output .println( preamble );
        output .println();

        NumberFormat FORMAT = NumberFormat .getNumberInstance( Locale .US );
        FORMAT .setMaximumFractionDigits( 19 );

        // first, produce all the vertices
        SortedSet<AlgebraicVector> vertices = new TreeSet<>();
        Manifestations.sortVertices( mModel .getManifestations(), vertices );
        // Up to this point, the vertices TreeSet has collected and sorted every unique vertex of every manifestation.
        // From now on we'll need their index, so we copy them into an ArrayList, preserving their sorted order.
        // so we can get their index into that array.
        ArrayList<AlgebraicVector> sortedVertexList = new ArrayList<>(vertices);
        // we no longer need the vertices collection, 
        // so set it to null to free the memory and to ensure we don't use it later by mistake.
        vertices = null;

        double inchScaling = mModel .getCmScaling() / 2.54d;
        int index = START_INDEX-1;

        ArrayList<RealVector> realVectors = new ArrayList<>();
        for (AlgebraicVector gv : sortedVertexList) {
            RealVector v = mModel .renderVector( gv ) .scale( inchScaling );
            realVectors .add( v );
            
            int cpIndex = ++index;
            output .println( "#" + cpIndex + " = CARTESIAN_POINT ( 'NONE',  ( " + v .toString( FORMAT ) + " ) ) ;" );
            output .println( "#" + (++index) + " = VERTEX_POINT ( 'NONE', #" + cpIndex + " ) ;" );
            output .println();
        }
        output .println();

        ArrayList<Integer> faceIndices = new ArrayList<>();
        for ( Manifestation man : mModel .getManifestations() ) {
            if ( man instanceof Panel ) {
                Panel panel = (Panel) man;
                int arity = panel .getVertexCount();

                int[] vIndices = new int[ arity ];
                int k = 0;
                for ( AlgebraicVector av : panel ) {
                  int vIndex = sortedVertexList .indexOf( av );
                  vIndices[ k++ ] = vIndex;
                }

                ArrayList<Integer> edgeIndices = new ArrayList<>();
                int point1 = 0;
                RealVector dir1 = null, dir2 = null;
                
                for ( int j=0; j < arity; j++ )
                {
                    Integer vindex = vIndices[ j ];
                    int rv1index = vindex;
                    RealVector rv1 = realVectors .get( rv1index );
                    point1 = rv1index * 2 + START_INDEX;
                    int vertex1 = point1 + 1;
                    vindex = vIndices[ (j+1)%arity ];
                    int rv2index = vindex;
                    RealVector rv2 = realVectors .get( rv2index );
                    int vertex2 = rv2index * 2 + START_INDEX + 1;
    
                    int direction1 = ++index;   // TODO compute direction1
                    rv1 = rv2 .minus( rv1 );
                    rv2 = rv1 .normalize();
                    if ( dir2 == null )
                    {
                        if ( dir1 == null )
                            dir1 = rv2;
                        else
                        {
                            dir2 = dir1 .cross( rv2 ) .normalize();
                            dir1 = dir2 .cross( dir1 );
                        }
                    }
                    output .println( "#" + direction1 + " = DIRECTION ( 'NONE',  ( " + rv2 .toString( FORMAT ) + " ) ) ;" );
                    int vector = ++index;
                    output .println( "#" + vector + " = VECTOR ( 'NONE', #"+ direction1 + ", 39.37 ) ;" );
                    int line = ++index;
                    output .println( "#" + line + " = LINE ( 'NONE', #"+ point1 + ", #"+ vector + " ) ;" );
                    int edgeCurve = ++index;
                    output .println( "#" + edgeCurve + " = EDGE_CURVE ( 'NONE', #"+ vertex1 + ", #"+ vertex2 + ", #"+ line + ", .T. ) ;" );
                    int orientedEdge = ++index;
                    edgeIndices .add(orientedEdge);
                    output .println( "#" + orientedEdge + " = ORIENTED_EDGE ( 'NONE', *, *, #"+ edgeCurve + ", ." + ((j==0)?"T":"T") + ". ) ;" );
                }
                int edgeLoop = ++index;
                output .print( "#" + edgeLoop + " = EDGE_LOOP ( 'NONE', ( " );
                String delim = "";
                for ( Integer i : edgeIndices) {
                    output .print( delim + "#" + i );
                    delim = ", ";
                }
                output .println( " ) ) ;" );
                int direction1 = ++index;   // TODO compute direction1
                output .println( "#" + direction1 + " = DIRECTION ( 'NONE',  ( " + dir2 .toString( FORMAT ) + " ) ) ;" );
                int direction2 = ++index;   // TODO compute direction2
                output .println( "#" + direction2 + " = DIRECTION ( 'NONE',  ( " + dir1 .toString( FORMAT ) + " ) ) ;" );
                int axisPlacement3d = ++index;
                output .println( "#" + axisPlacement3d + " = AXIS2_PLACEMENT_3D ( 'NONE', #"+ point1 + ", #"+ direction1 + ", #"+ direction2 + " ) ;" );
                int plane = ++index;
                output .println( "#" + plane + " = PLANE ( 'NONE', #"+ axisPlacement3d + " ) ;" );
                int faceOuterBound = ++index;
                output .println( "#" + faceOuterBound + " = FACE_OUTER_BOUND ( 'NONE', #"+ edgeLoop + ", .T. ) ;" );
                int advancedFace = ++index;
                output .println( "#" + advancedFace + " = ADVANCED_FACE ( 'NONE', ( #"+ faceOuterBound + " ), #"+ plane + ", .T. ) ;" );
                output .println();
                faceIndices .add(advancedFace);
            }
    
        }
        output .print( "#299 = CLOSED_SHELL ( 'NONE', ( " );
        String delim = "";
        for ( Integer i : faceIndices ) {
            output .print( delim + "#" + i );
            delim = ", " ;
        }
        output .println( " ) ) ;" );
        
        output .println( POSTLUDE );
        output .flush();
        output .close();
    }

    @Override
    public String getFileExtension()
    {
        return "step";
    }
	
}
