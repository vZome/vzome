package com.vzome.core.exporters;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.ViewModel;

/**
 * Renders out to POV-Ray using #declare statements to reuse geometry.
 * @author vorth
 */
public class PartsListExporter extends Exporter3d
{	
	public PartsListExporter( ViewModel scene, Colors colors, Lights lights, RenderedModel model )
	{
	    super( scene, colors, lights, model );
	}


	public void doExport( File directory, Writer writer, Dimension screenSize ) throws IOException
	{
	    output = new PrintWriter( writer );
	    
        AlgebraicField field = mModel .getField();
        
        int numBalls = 0;
		Map[] orbits = new Map[]{ new HashMap(), new HashMap() };
		for ( Iterator rms = mModel .getRenderedManifestations(); rms .hasNext(); )
		{
		    RenderedManifestation rm = (RenderedManifestation) rms .next();
		    
		    Manifestation m = rm .getManifestation();
		    if ( m instanceof Connector ) {
		        ++ numBalls;
		    }
		    else if ( m instanceof Strut ) {
	            Polyhedron shape = rm .getShape();
	            boolean flip = rm .reverseOrder(); // part is left-handed
		        Direction orbit = shape .getOrbit();
		        Map orbitHistogram = (Map) orbits[ flip?1:0 ] .get( orbit );
		        if ( orbitHistogram == null )
		        {
		            orbitHistogram = new HashMap();
		            orbits[ flip?1:0 ] .put( orbit, orbitHistogram );
		        }
		        int[] /*AlgebraicNumber*/ len = shape .getLength();
		        AlgebraicVector key = new AlgebraicVector( len );
                Integer lengthCount = (Integer) orbitHistogram .get( key );
                if ( lengthCount == null )
                {
                    lengthCount = new Integer( 1 );
                }
                else
                    lengthCount = new Integer( lengthCount .intValue() + 1 );
                orbitHistogram .put( key, lengthCount );
		    }
		}
        output .println( "balls" );
        output .println( "  " + numBalls );
		
		for ( int i = 0; i < orbits.length; i++ ) {
            for ( Iterator iterator = orbits[i].keySet().iterator(); iterator.hasNext(); ) {
                Direction orbit = (Direction) iterator.next();
                output .print( orbit .getName() );
                if ( i == 1 )
                    output .print( " (lefty)" );
                output .println();
                Map histogram = (Map) orbits[i] .get( orbit );
                for ( Iterator iterator2 = histogram .keySet().iterator(); iterator2.hasNext(); ) {
                    AlgebraicVector key = (AlgebraicVector) iterator2.next();
                    output .println( "  " + key .toString( field ) + " : " + (Integer) histogram .get( key ) );
                }
            }
        }

		output .close();
	}

    public String getFileExtension()
    {
        return "txt";
    }

}


