/*
 * Created on Jun 25, 2003
 */
package com.vzome.core.viewing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.editor.Application;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.VefParser;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.IcosahedralSymmetry;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.parts.StrutGeometry;
import com.vzome.core.render.Color;
import com.vzome.core.render.Colors;

/**
 * @author vorth
 */
public class ExportedVEFShapes extends AbstractShapes
{
    public static final String MODEL_PREFIX = "com/vzome/core/parts/";

    private static final String NODE_MODEL = "connector";

    private final File prefsFolder;

    private final AbstractShapes fallback;

    private final Properties colors = new Properties();

    private static final Logger logger = Logger.getLogger( "com.vzome.core.viewing.shapes" );

    public ExportedVEFShapes( File prefsFolder, String pkgName, String name, String alias, Symmetry symm )
    {
        this( prefsFolder, pkgName, name, alias, symm, ( symm instanceof IcosahedralSymmetry )? new OctahedralShapes( pkgName, name, (IcosahedralSymmetry) symm ) : null );
    }

    public ExportedVEFShapes( File prefsFolder, String pkgName, String name, Symmetry symm )
    {
        this( prefsFolder, pkgName, name, null, symm );
    }

    public ExportedVEFShapes( File prefsFolder, String pkgName, String name, Symmetry symm, AbstractShapes fallback )
    {
        this( prefsFolder, pkgName, name, null, symm, fallback );
    }

    public ExportedVEFShapes( File prefsFolder, String pkgName, String name, String alias, Symmetry symm, AbstractShapes fallback )
    {
        super( pkgName, name, alias, symm );
        this .prefsFolder = prefsFolder;
        this .fallback = fallback;

        String colorProps = MODEL_PREFIX + pkgName + "/colors.properties";
        try {
            ClassLoader cl = Application.class .getClassLoader();
            InputStream in = cl .getResourceAsStream( colorProps );
            if ( in != null )
                this .colors .load( in );
        } catch ( IOException ioe ) {
            if ( logger .isLoggable( Level.FINE ) )
                logger .fine( "problem with shape color properties: " + colorProps );
        }
    }

    @Override
    protected Polyhedron buildConnectorShape( String pkgName )
    {
        String vefData = loadVefData( NODE_MODEL );
        if ( vefData != null ) {
            VefToShape parser = new VefToShape();
            parser .parseVEF( vefData, mSymmetry .getField() );
            return parser .getConnectorPolyhedron();
        }
        if ( this .fallback != null )
            return this .fallback .buildConnectorShape( pkgName );
        else
            throw new IllegalStateException( "missing connector shape: " + pkgName );
    }

    @Override
    protected StrutGeometry createStrutGeometry( Direction dir )
    {
        String vefData = loadVefData( dir .getName() );
        if ( vefData != null ) {
            VefToShape parser = new VefToShape();
            parser .parseVEF( vefData, mSymmetry .getField() );
            return parser .getStrutGeometry( dir .getAxis( Symmetry .PLUS, 0 ) .normal() );
        }
        else  if ( this .fallback != null )
            return this .fallback .createStrutGeometry( dir );
        else
            return super .createStrutGeometry( dir );
    }

    protected String loadVefData( String name )
    {
        String script = mPkgName + "/" + name + ".vef";
        File shapeFile = new File( this .prefsFolder, "Shapes/" + script );
        InputStream stream = null;
        try {
            if ( shapeFile .exists() )
                stream = new FileInputStream( shapeFile );
            else {
                script = MODEL_PREFIX + script;
                stream = Thread.currentThread() .getContextClassLoader().getResourceAsStream( script );
                if ( stream == null )
                    return null; // avoid the NPE!
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int num;
            while ( ( num = stream .read( buf, 0, 1024 )) > 0 )
                out .write( buf, 0, num );
            return new String( out .toByteArray() );

        } catch (Exception e) {
            logger .fine( "Failure loading VEF data from " + shapeFile );
        } finally {
            if ( stream != null )
                try {
                    stream .close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
        return null;
    }

    @Override
    public boolean hasColors()
    {
        return ! this .colors .isEmpty();
    }

    @Override
    public Color getColor( Direction dir )
    {
        if ( this .colors .isEmpty() )
            return null;
        String dirName = ( dir == null )? NODE_MODEL : dir .getName();
        String colorString = this .colors .getProperty( dirName );
        if ( colorString == null )
            return null;
        return Colors .parseColor( colorString );
    }

    /*
     * The VEF file format parsed here is an "extended profile" of the usual format.
     * First, it is a profile because the only "balls" (not vertices) present in the file
     * should be those that correspond to panel vertices that must track the location of the end
     * of the strut.  Second, it is an extension because the "tip" index at the end indicates the
     * vertex corresponding to the end ball position of the strut as modeled.
     * 
     * The tip vector is subtracted from all the strut-end-tracking ("ball") vectors, in the prototype
     * polyhedron.  Then, when a new length polyhedron is required, the scaled prototype vector is added
     * to those ball vectors again, for the final polyhedron.
     * 
     * As of August 2015, this now supports another extension; after the "tip" vertex, an arbitrary number
     * of "middle" vertices may be specified.  Whereas the tip vertex specifies the strut tip relative to
     * strut end vertices, the middle vertices will track the midpoint of the strut.  This is so that we can
     * use this mechanism to model classic Zometool red and yellow struts, and any others that use a central structure.
     */

    private class VefToShape extends VefParser
    {                
        private Set<Integer> tipVertexIndices = new HashSet<>();

        private Set<Integer> midpointVertexIndices = new HashSet<>();

        private AlgebraicVector tipVertex;

        private List<AlgebraicVector> vertices = new ArrayList<>();

        private List< List<Integer> > faces = new ArrayList<>();

        public StrutGeometry getStrutGeometry( AlgebraicVector prototype )
        {
            // next, get the arbitrary axis that the strut model lies along
            Axis tipAxis = mSymmetry .getAxis( tipVertex );
            //   int sense = tipAxis .getSense();  // TODO make this logic the same regardless of sense

            AlgebraicVector midpoint = tipVertex .scale( mSymmetry .getField() .createRational( 1, 2 ) );

            // find the orientation index, and invert it...
            int orientation = mSymmetry .inverse( tipAxis .getOrientation() );
            // ... so we can get the inverse matrix without recomputing it
            AlgebraicMatrix adjustment = mSymmetry .getMatrix( orientation );

            // now, adjust the vertex data
            List<AlgebraicVector> newVertices = new ArrayList<>();
            for ( int i = 0; i < vertices .size(); i++ )
            {
                AlgebraicVector originalVertex = vertices .get( i );
                // first, subtract the tipVertex if appropriate
                if ( tipVertexIndices .contains(i) )
                    originalVertex = originalVertex .minus( tipVertex );
                else if ( midpointVertexIndices .contains(i) )
                    originalVertex = originalVertex .minus( midpoint );
                // then, rotate to align with the 0-index zone for this orbit
                AlgebraicVector adjustedVertex = adjustment .timesColumn( originalVertex );
                newVertices .add( adjustedVertex );
            }

            return new ExportedVEFStrutGeometry( newVertices, faces, prototype, tipVertexIndices, midpointVertexIndices, mSymmetry .getField() );
        }

        public Polyhedron getConnectorPolyhedron()
        {
            Polyhedron result = new Polyhedron( mSymmetry .getField() );
            for (AlgebraicVector vertex : vertices) {
                result .addVertex( vertex );
            }
            for (List<Integer> prototypeFace : faces) {
                Polyhedron.Face face = result .newFace();
                face .addAll( prototypeFace );
                result .addFace( face );
            }
            return result;
        }

        @Override
        protected void addFace( int index, int[] verts )
        {
            List<Integer> face = new ArrayList<>();
            for ( int i = 0; i < verts.length; i++ ) {
                int j = verts[i];
                face .add(j);
            }
            faces .add( face );
        }

        @Override
        protected void addVertex( int index, AlgebraicVector location )
        {
            AlgebraicVector vertex = mSymmetry .getField() .projectTo3d( location, wFirst() );
            vertices .add( vertex );
        }

        @Override
        protected void addBall( int index, int vertex )
        {
            tipVertexIndices .add(vertex);
        }

        @Override
        protected void endFile( StringTokenizer tokens )
        {
            if ( ! tokens .hasMoreTokens() )
                return;

            String token = tokens .nextToken();
            if ( ! "tip" .equals( token ) )
                throw new IllegalStateException( "VEF format error: token after face list (\"" + token + "\" should be \"tip\"" );
            try {
                token = tokens .nextToken();
            } catch ( NoSuchElementException e1 ) {
                throw new IllegalStateException( "VEF format error: no tokens after \"tip\"" );
            }
            int tipIndex;
            try {
                tipIndex = Integer .parseInt( token );
            } catch ( NumberFormatException e ) {
                throw new RuntimeException( "VEF format error: strut tip vertex index (\"" + token + "\") must be an integer", e );
            }

            this .tipVertex = vertices .get( tipIndex );

            if ( ! tokens .hasMoreTokens() )
                return;

            token = tokens .nextToken();
            if ( ! "middle" .equals( token ) )
                throw new IllegalStateException( "VEF format error: token after tip vertex (\"" + token + "\" should be \"middle\"" );
            while ( tokens .hasMoreTokens() ) {
                token = tokens .nextToken();
                int vertexIndex;
                try {
                    vertexIndex = Integer .parseInt( token );
                } catch ( NumberFormatException e ) {
                    throw new RuntimeException( "VEF format error: middle vertex index (\"" + token + "\") must be an integer", e );
                }                
                midpointVertexIndices .add(vertexIndex);
            }
        }

        @Override
        protected void startBalls( int numVertices )
        {}

        @Override
        protected void startEdges( int numEdges )
        {}

        @Override
        protected void addEdge( int index, int v1, int v2 )
        {}

        @Override
        protected void startFaces( int numFaces )
        {}

        @Override
        protected void startVertices( int numVertices )
        {}
    }    
}
