package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicNumber;
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


    private class OrbitMap extends HashMap<Direction, Map<AlgebraicNumber, Integer> > {}
    
	public void doExport( File directory, Writer writer, int height, int width ) throws IOException
	{
	    output = new PrintWriter( writer );
	    
        int numBalls = 0;
		OrbitMap[] orbits = new OrbitMap[]{ new OrbitMap(), new OrbitMap() };
		for ( Iterator<RenderedManifestation> rms = mModel .iterator(); rms .hasNext(); )
		{
		    RenderedManifestation rm = rms .next();
		    
		    Manifestation m = rm .getManifestation();
		    if ( m instanceof Connector ) {
		        ++ numBalls;
		    }
		    else if ( m instanceof Strut ) {
	            Polyhedron shape = rm .getShape();
	            boolean flip = rm .reverseOrder(); // part is left-handed
		        Direction orbit = shape .getOrbit();
		        Map<AlgebraicNumber, Integer> orbitHistogram = orbits[ flip?1:0 ] .get( orbit );
		        if ( orbitHistogram == null )
		        {
		            orbitHistogram = new HashMap<>();
		            orbits[ flip?1:0 ] .put( orbit, orbitHistogram );
		        }
		        AlgebraicNumber len = shape .getLength();
                Integer lengthCount = orbitHistogram .get( len );
                if ( lengthCount == null )
                {
                    lengthCount = 1;
                }
                else
                    lengthCount = lengthCount + 1;
                orbitHistogram .put( len, lengthCount );
		    }
		}
        output .println( "balls" );
        output .println( "  " + numBalls );
		
		for ( int i = 0; i < orbits.length; i++ ) {
            for ( Iterator<Direction> iterator = orbits[i].keySet().iterator(); iterator.hasNext(); ) {
                Direction orbit = iterator.next();
                output .print( orbit .getName() );
                if ( i == 1 )
                    output .print( " (lefty)" );
                output .println();
                Map<AlgebraicNumber, Integer> histogram = orbits[i] .get( orbit );
                for ( Iterator<AlgebraicNumber> iterator2 = histogram .keySet().iterator(); iterator2.hasNext(); ) {
                    AlgebraicNumber key = iterator2.next();
                    output .println( "  " + key .toString() + " : " + histogram .get( key ) );
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


