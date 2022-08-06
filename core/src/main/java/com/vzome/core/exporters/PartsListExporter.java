package com.vzome.core.exporters;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;
import com.vzome.core.render.RenderedManifestation;

/**
 * Renders out to POV-Ray using #declare statements to reuse geometry.
 * @author vorth
 */
public class PartsListExporter extends Exporter3d
{	
    @Override
	public void doExport( File directory, Writer writer, int height, int width ) throws IOException
	{
	    output = new PrintWriter( writer );
	    
        int numBalls = 0;
        HashMap<Direction, Map<AlgebraicNumber, Integer> > orbits = new HashMap<>();
        for (RenderedManifestation rm : mModel) {
            Manifestation m = rm .getManifestation();
            if ( m instanceof Connector ) {
                ++ numBalls;
            }
            else if ( m instanceof Strut ) {
                Polyhedron shape = rm .getShape();
                Direction orbit = shape .getOrbit();
                Map<AlgebraicNumber, Integer> orbitHistogram = orbits .get( orbit );
                if ( orbitHistogram == null )
                {
                    orbitHistogram = new HashMap<>();
                    orbits .put( orbit, orbitHistogram );
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
		
        for (Direction orbit : orbits.keySet()) {
        	output .print( orbit .getName() );
        	output .println();
        	Map<AlgebraicNumber, Integer> histogram = orbits .get( orbit );
        	for (AlgebraicNumber key : histogram .keySet()) {
        		output .println( "  " + key .toString() + " : " + histogram .get( key ) );
        	}
        }

		output .close();
	}

    @Override
    public String getFileExtension()
    {
        return "txt";
    }

}
