package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.generic.ArrayComparator;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Camera;
import com.vzome.core.viewing.Lights;


public class OffExporter extends Exporter3d
{
	private static final NumberFormat FORMAT = NumberFormat .getNumberInstance( Locale .US );
	
	public OffExporter( Camera scene, Colors colors, Lights lights, RenderedModel model )
	{
	    super( scene, colors, lights, model );
		FORMAT .setMinimumFractionDigits( 16 ); // https://github.com/vZome/vzome/issues/313
	}

    @Override
	public void doExport( File directory, Writer writer, int height, int width ) throws IOException
	{
        SortedSet<AlgebraicVector> vertices = new TreeSet<>();
		int numStruts = 0;
        ArrayComparator<AlgebraicVector> arrayComparator = new ArrayComparator<>();
        final SortedSet<AlgebraicVector[]> panelVertices = new TreeSet<>( arrayComparator.getLengthFirstArrayComparator() );

        for (RenderedManifestation rm : mModel) {
            Manifestation man = rm .getManifestation();

            if (man instanceof Connector) {
                AlgebraicVector loc = man.getLocation();
                vertices.add(loc);
            } else if (man instanceof Strut) {
                ++ numStruts;
            } else if (man instanceof Panel) {
                Panel panel = (Panel) man;
                // Unsorted list retains the panel vertex order
                ArrayList<AlgebraicVector> corners = new ArrayList<>(panel.getVertexCount());
                for (AlgebraicVector vertex : panel) {
                    corners.add(vertex);
                }
                vertices.addAll(corners);
                AlgebraicVector[] cornerArray = new AlgebraicVector[corners.size()];
                corners.toArray(cornerArray);
                panelVertices.add(cornerArray);
            }
        }

        // Now that we have all of the unique vertices sorted in vertices,
        // we'll need their index, so we copy them into an ArrayList, preserving their sorted order.
        // so we can get their index into that array.
        ArrayList<AlgebraicVector> sortedVertexList = new ArrayList<>(vertices);
        // we no longer need the vertices collection,
        // so set it to null to free the memory and to ensure we don't use it later by mistake.
        vertices = null;

        output = new PrintWriter( writer );
        output .println( "OFF" );
        output .println( "# numVertices numFaces numEdges (numEdges is ignored)" );
        output .println( sortedVertexList.size() + " " + panelVertices.size() + " " + numStruts + "\n");

        output .println( "# Vertices.  Each line is the XYZ coordinates of one vertex." );
        for(AlgebraicVector vector : sortedVertexList) {
            double[] dv = mModel .renderVectorDouble( vector );
            output.print(FORMAT.format(dv[0]) + " ");   // x
            output.print(FORMAT.format(dv[1]) + " ");   // y
            output.print(FORMAT.format(dv[2]) + "\n");  // z
        }

        output .println();
        output .println( "# Faces.  numCorners vertexIndex[0] ... vertexIndex[numCorners-1]" );
        for(AlgebraicVector[] corners : panelVertices) {
            output .print( corners.length );
            for(AlgebraicVector corner : corners) {
                output .print( " " + sortedVertexList.indexOf(corner));
            }
            output .println();
        }
        
		output .flush();
	}

    @Override
    public String getFileExtension()
    {
        return "off";
    }
}
