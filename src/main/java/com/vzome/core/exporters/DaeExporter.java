package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Locale;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.RealVector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.ViewModel;


public class DaeExporter extends Exporter3d
{
	private static final NumberFormat FORMAT = NumberFormat .getNumberInstance( Locale .US );	
	
	public DaeExporter( ViewModel scene, Colors colors, Lights lights, RenderedModel model )
	{
	    super( scene, colors, lights, model );
	}


	public void doExport( File directory, Writer writer, int height, int width ) throws IOException
	{
	    AlgebraicField field = this .mModel .getField();

	    if (FORMAT instanceof DecimalFormat) {
            ((DecimalFormat) FORMAT) .applyPattern( "0.0000" );
        }

        output = new PrintWriter( writer );
        
        int vertexCount = 0;
        int normalCount = 0;
        int triangleCount = 0;
        int lineCount = 0;
        StringBuffer vertices = new StringBuffer();
        StringBuffer normals = new StringBuffer();
        StringBuffer triangles = new StringBuffer();
        StringBuffer lines = new StringBuffer();
        
        for ( Iterator<RenderedManifestation> rms = mModel .iterator(); rms .hasNext(); )
        {
            Manifestation man = rms .next() .getManifestation();
            if ( man instanceof Panel )
            {
                Panel panel = (Panel) man;
                RealVector norm = panel .getNormal( field ) .toRealVector() .normalize();
                int v0 = -1, v1 = -1, n0 = -1, n1 = -1;
                for ( Iterator<AlgebraicVector> verts = panel .iterator(); verts .hasNext(); )
                {
                    RealVector vertex = verts .next() .toRealVector();
                    // This scale factor corresponds to a vZome model that uses a long blue as the radius of a ball.
                    //  norm squared of diameter in vZome: 1967.87  => diameter == 44.36
                    //  nominal ball diameter in rZome: .700 in
                    //  plastic shrinkage in rZome production: .994
                    //    so actual ball diameter = .6958
                    //  .6958 / 44.36 = 0.01568
//                    vertex = vertex .scale( 0.01568d );
                    if ( v0 == -1 )
                    {
                        v0 = vertexCount;
                        n0 = normalCount;
                    }
                    else if ( v1 == -1 )
                    {
                        v1 = vertexCount;
                        n1 = normalCount;
                    }
                    else
                    {
                        if ( triangleCount % 40 == 0 )
                            triangles .append( "\n" );
                        triangles .append( v0 + " " );
                        triangles .append( n0 + " " );
                        triangles .append( v1 + " " );
                        triangles .append( n1 + " " );
                        triangles .append( vertexCount + " " );
                        triangles .append( normalCount + " " );
                        v1 = vertexCount;
                        ++ triangleCount;
                    }
                    if ( vertexCount % 20 == 0 )
                        vertices .append( "\n" );
                    vertices .append( FORMAT .format( vertex.x ) + " " );
                    vertices .append( FORMAT .format( vertex.y ) + " " );
                    vertices .append( FORMAT .format( vertex.z ) + " " );
                    ++ vertexCount;
                    if ( normalCount % 20 == 0 )
                        normals .append( "\n" );
                    normals .append( FORMAT .format( norm.x ) + " " );
                    normals .append( FORMAT .format( norm.y ) + " " );
                    normals .append( FORMAT .format( norm.z ) + " " );
                    ++ normalCount;
                }
            }
            else
                if ( man instanceof Strut )
                {
                    Strut strut = (Strut) man;
                    int v0 = vertexCount++;
                    if ( vertexCount % 20 == 0 )
                        vertices .append( "\n" );
                    RealVector vertex = strut .getLocation() .toRealVector();
                    vertices .append( FORMAT .format( vertex.x ) + " " );
                    vertices .append( FORMAT .format( vertex.y ) + " " );
                    vertices .append( FORMAT .format( vertex.z ) + " " );

                    int v1 = vertexCount++;
                    if ( vertexCount % 20 == 0 )
                        vertices .append( "\n" );
                    vertex = strut .getEnd() .toRealVector();
                    vertices .append( FORMAT .format( vertex.x ) + " " );
                    vertices .append( FORMAT .format( vertex.y ) + " " );
                    vertices .append( FORMAT .format( vertex.z ) + " " );
                    if ( lineCount % 40 == 0 )
                        lines .append( "\n" );
                    lines .append( v0 + " " );
                    lines .append( v1 + " " );
                    ++ lineCount;
                }
        }
        output .println( vertexCount + " vertices " + vertices );
        output .println( normalCount + " normals " + normals );
        output .println( lineCount + " lines " + lines );
        output .println( triangleCount + " triangles " + triangles );
        
		output .flush();
	}
		
    public String getFileExtension()
    {
        return "dae";
    }

    public String getContentType()
    {
        return "model/vnd.collada+xml";
    }
}


