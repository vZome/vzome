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
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;
import com.vzome.core.render.RenderedManifestation;


public class PythonBuild123dExporter extends GeometryExporter
{
	private static final NumberFormat FORMAT = NumberFormat .getNumberInstance( Locale .US );
	
	static {
        FORMAT .setMinimumFractionDigits( 6 );
        FORMAT .setMaximumFractionDigits( 6 );
	}

    @Override
	public void doExport( File directory, Writer writer, int height, int width ) throws IOException
	{
        SortedSet<AlgebraicVector> vertices = new TreeSet<>();

        for (RenderedManifestation rm : mModel) {
            Manifestation man = rm .getManifestation();

            if ( man instanceof Strut ) {
                Strut strut = (Strut) man;
                AlgebraicVector loc = strut.getLocation();
                vertices.add( loc );
                loc = strut .getEnd();
                vertices.add( loc );
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
        
        String prelude = this.getBoilerplate( "com/vzome/core/exporters/mesh-prelude.py" );
        output .print( prelude );

        output .println( "vertices = [" );
        for(AlgebraicVector vector : sortedVertexList) {
            double[] dv = mModel .renderVectorDouble( vector );
            output.print  ( "( " );
            output.print  ( FORMAT.format(dv[0]) + ", " );   // x
            output.print  ( FORMAT.format(dv[1]) + ", " );   // y
            output.println( FORMAT.format(dv[2]) + " )," );  // z
        }
        output.println( "]" );

        output .println();
        output .println( "edges = [" );
        
        for (RenderedManifestation rm : mModel) {
            Manifestation man = rm .getManifestation();

            if (man instanceof Strut) {
                Strut strut = (Strut) man;
                output .println( "[ " + sortedVertexList.indexOf( strut.getLocation() ) + ", " +
                                    sortedVertexList.indexOf( strut.getEnd() ) + " ]," );
            }
        }
        output.println( "]" );

        String postlude = this.getBoilerplate( "com/vzome/core/exporters/mesh-postlude.py" );
        output .print( postlude );

		output .flush();
	}

    @Override
    public String getFileExtension()
    {
        return "py";
    }
}
