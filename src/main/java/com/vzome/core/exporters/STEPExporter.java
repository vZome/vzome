package com.vzome.core.exporters;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;
import com.vzome.core.viewing.ViewModel;

/**
 * Renders out to POV-Ray using #declare statements to reuse geometry.
 * @author vorth
 */
public class STEPExporter extends Exporter3d{
	
	private static final String PREAMBLE_FILE = "com/vzome/core/exporters/step/preamble.step";
    
    private static final String POSTLUDE = "ENDSEC;\nEND-ISO-10303-21;";
    
    private static final int START_INDEX = 300;

    private static final PentagonField FIELD = new PentagonField();

    private static final int[] MODEL_BALL_RADIUS = FIELD .createAlgebraicNumber( 1, 0, 1, 0 );
    /**
     * This scale factor is appropriate for making true-to-scale STEP renderings of strut models built
     *   in vZome with a model ball radius (blue) of one long blue strut.  In other terms, the edges of the
     *   ball model are short and medium blue struts.
     */
    private static final double SCALE = 0.350d / FIELD .evaluateNumber( MODEL_BALL_RADIUS );

    public STEPExporter( ViewModel scene, Colors colors, Lights lights, RenderedModel model )
    {
        super( scene, colors, lights, model );
    }
    

    public void doExport( File directory, Writer writer, int height, int width ) throws Exception
    {
        int numShapes = 0;
        Map[] shapes = new Map[]{ new HashMap(), new HashMap() };
        for ( Iterator rms = mModel .getRenderedManifestations(); rms .hasNext(); )
        {
            RenderedManifestation rm = (RenderedManifestation) rms .next();
            Polyhedron shape = rm .getShape();
            boolean flip = rm .reverseOrder(); // need to reverse face vertex order
            String shapeName = (String) shapes[ flip?1:0 ] .get( shape );
            if ( shapeName == null )
            {
                shapeName = shape .getName();
                if ( shapeName == null )
                    shapeName = "shape" + numShapes++;
                shapes[ flip?1:0 ] .put( shape, shapeName );
                

                writer = new FileWriter( new File( directory, shapeName + ".step" ) );
                output = new PrintWriter( writer );

                
                exportShape( shapeName, shape, flip );

                output .close();
                writer .close();
            }
            // not doing instances yet, for STEP
        }

    }


    private void exportShape( String shapeName, Polyhedron poly, boolean reverseFaces )
    {
        InputStream input = getClass() .getClassLoader()
                                    .getResourceAsStream( PREAMBLE_FILE );
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int num;
        try {
            while ( ( num = input .read( buf, 0, 1024 )) > 0 )
                    out .write( buf, 0, num );
        } catch (IOException e) {
            e.printStackTrace();
        }
        String preamble = new String( out .toByteArray() );
        preamble = preamble .replaceAll( "_____FILENAME_____", shapeName );
        output .println( preamble );
        output .println();
        
        NumberFormat FORMAT = NumberFormat .getNumberInstance( Locale .US );
        FORMAT .setMaximumFractionDigits( 19 );
        
        int index = START_INDEX-1;

        // first, produce all the vertices
        ArrayList realVectors = new ArrayList();
        AlgebraicField field = poly .getField();;
        for ( Iterator vertices = poly .getVertexList() .iterator(); vertices .hasNext(); ) {
            int[] /*AlgebraicVector*/ gv = (int[] /*AlgebraicVector*/) vertices .next();
            if ( reverseFaces )
                gv = field .negate( gv  );
            RealVector v = field .getRealVector( gv ) .scale( SCALE );
            realVectors .add( v );
            
            int cpIndex = ++index;
            output .println( "#" + cpIndex + " = CARTESIAN_POINT ( 'NONE',  ( " + v .toString( FORMAT ) + " ) ) ;" );
            output .println( "#" + (++index) + " = VERTEX_POINT ( 'NONE', #" + cpIndex + " ) ;" );
            output .println();
        }
        output .println();

        ArrayList faceIndices = new ArrayList();
        for ( Iterator faces = poly .getFaceSet() .iterator(); faces .hasNext(); )
        {
            Polyhedron.Face face = (Polyhedron.Face) faces .next();
            int arity = face .size();

            ArrayList edgeIndices = new ArrayList();
            int point1 = 0;
            RealVector dir1 = null, dir2 = null;
            for ( int j = 0; j < arity; j++ )
            {
                Integer vindex = (Integer) face .get( /*reverseFaces? arity-j-1 :*/ j );
                int rv1index = vindex .intValue();
                RealVector rv1 = (RealVector) realVectors .get( rv1index );
                point1 = rv1index * 2 + START_INDEX;
                int vertex1 = point1 + 1;
                vindex = (Integer) face .get( /*reverseFaces? arity-j-1 :*/ (j+1)%arity );
                int rv2index = vindex .intValue();
                RealVector rv2 = (RealVector) realVectors .get( rv2index );
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
                edgeIndices .add( new Integer( orientedEdge ) );
                output .println( "#" + orientedEdge + " = ORIENTED_EDGE ( 'NONE', *, *, #"+ edgeCurve + ", ." + ((j==0)?"T":"T") + ". ) ;" );
            }

            int edgeLoop = ++index;
            output .print( "#" + edgeLoop + " = EDGE_LOOP ( 'NONE', ( " );
            for ( Iterator it = edgeIndices .iterator(); it .hasNext(); )
            {
                output .print( "#" + it .next() );
                if ( it .hasNext() )
                    output .print( ", " );
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
            faceIndices .add( new Integer( advancedFace ) );
        }

        output .print( "#299 = CLOSED_SHELL ( 'NONE', ( " );
        for ( Iterator it = faceIndices .iterator(); it .hasNext(); )
        {
            output .print( "#" + it .next() );
            if ( it .hasNext() )
                output .print( ", " );
        }
        output .println( " ) ) ;" );

        
        
        output .println( POSTLUDE );
        output .flush();
    }


    public String getFileExtension()
    {
        return "step";
    }
	
}


