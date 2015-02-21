/*
 * Created on Jun 25, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
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
import java.util.Set;
import java.util.StringTokenizer;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.VefParser;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.parts.DefaultStrutGeometry;
import com.vzome.core.parts.StrutGeometry;

/**
 * @author vorth
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ExportedVEFShapes extends AbstractShapes
{
    public static final String MODEL_PREFIX = "com/vzome/core/parts/";

    private static final String NODE_MODEL = "/connector.vef";
    
    private final File prefsFolder;
    
    private final AbstractShapes fallback;
        
    public ExportedVEFShapes( File prefsFolder, String pkgName, String name, String alias, Symmetry symm )
    {
        super( pkgName, name, alias, symm );
        this .prefsFolder = prefsFolder;
        this .fallback = null;
    }
    
    public ExportedVEFShapes( File prefsFolder, String pkgName, String name, Symmetry symm, AbstractShapes fallback )
    {
        super( pkgName, name, symm );
        this .prefsFolder = prefsFolder;
        this .fallback = fallback;
    }
    
    protected Polyhedron buildConnectorShape( String pkgName )
    {
        String path = pkgName + NODE_MODEL;       
        InputStream bytes = null;
        File shapeFile = new File( this .prefsFolder, "Shapes/" + path );
        if ( shapeFile .exists() )
            try {
                bytes = new FileInputStream( shapeFile );
            	bytes .close();
            } catch ( Exception e1 ) {
                // TODO log this... should never happen?
                e1.printStackTrace();
            }
        else {
            path = MODEL_PREFIX + path;
            bytes = Thread.currentThread() .getContextClassLoader().getResourceAsStream( path );
        }
        if ( bytes == null )
            throw new IllegalStateException( "missing script: " + path );

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int num;
        try {
            while ( ( num = bytes .read( buf, 0, 1024 )) > 0 )
                out .write( buf, 0, num );
        } catch ( IOException e ) {
            throw new IllegalStateException( "IO exception: " + path );
        }
        String vefData = new String( out .toByteArray() );
        
        VefToShape parser = new VefToShape();
        parser .parseVEF( vefData, mSymmetry .getField() );

        return parser .getConnectorPolyhedron();
    }

    protected StrutGeometry createStrutGeometry( Direction dir )
    {
        // else is default, let's try to improve on that

        String script = mPkgName + "/" + dir .getName() + ".vef";
        InputStream bytes = null;
        File shapeFile = new File( this .prefsFolder, "Shapes/" + script );
        if ( shapeFile .exists() )
            try {
                bytes = new FileInputStream( shapeFile );
                bytes .close();
            } catch ( Exception e1 ) {
                // TODO log this... should never happen?
                e1.printStackTrace();
            }
        else {
            script = MODEL_PREFIX + script;
            bytes = Thread.currentThread() .getContextClassLoader().getResourceAsStream( script );
        }
        if ( bytes == null )
            if ( this .fallback == null )
                return new DefaultStrutGeometry( dir );
            else
                return this .fallback .createStrutGeometry( dir );

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int num;
        try {
            while ( ( num = bytes .read( buf, 0, 1024 )) > 0 )
                out .write( buf, 0, num );
        } catch ( IOException e ) {
            return new DefaultStrutGeometry( dir );
        }
        String vefData = new String( out .toByteArray() );
        
        VefToShape parser = new VefToShape();
        parser .parseVEF( vefData, mSymmetry .getField() );

        return parser .getStrutGeometry( dir .getAxis( Symmetry .PLUS, 0 ) .normal() );
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
     */
    
    private class VefToShape extends VefParser
    {                
        private Set stretchVertexIndices = new HashSet();
        
        private List vertices = new ArrayList();
        
        private List faces = new ArrayList();
        
        public StrutGeometry getStrutGeometry( AlgebraicVector prototype )
        {
            return new ExportedVEFStrutGeometry( vertices, faces, prototype, stretchVertexIndices, mSymmetry .getField() );
        }
        
        public Polyhedron getConnectorPolyhedron()
        {
            Polyhedron result = new Polyhedron( mSymmetry .getField() );
            for ( int i = 0; i < vertices .size(); i ++ )
            {
                AlgebraicVector vertex = (AlgebraicVector) vertices .get( i );
                result .addVertex( vertex );
            }
            for ( int j = 0; j < faces .size(); j ++ )
            {
                List prototypeFace = (List) faces .get( j );
                Polyhedron.Face face = result .newFace();
                face .addAll( prototypeFace );
                result .addFace( face );
            }
            return result;
        }

        protected void addFace( int index, int[] verts )
        {
            List face = new ArrayList();
            for ( int i = 0; i < verts.length; i++ ) {
                int j = verts[i];
                face .add( new Integer( j ) );
            }
            faces .add( face );
        }

        protected void addVertex( int index, AlgebraicVector location )
        {
            AlgebraicVector vertex = mSymmetry .getField() .projectTo3d( location, wFirst() );
            vertices .add( vertex );
        }

        protected void addBall( int index, int vertex )
        {
            stretchVertexIndices .add( new Integer( vertex ) );
        }

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
            
            AlgebraicVector tipVertex = (AlgebraicVector) vertices .get( tipIndex );

            // next, get the arbitrary axis that the strut model lies along
            Axis tipAxis = mSymmetry .getAxis( tipVertex );
         //   int sense = tipAxis .getSense();  // TODO make this logic the same regardless of sense
            
            // find the orientation index, and invert it...
            int orientation = mSymmetry .inverse( tipAxis .getOrientation() );
            // ... so we can get the inverse matrix without recomputing it
            AlgebraicMatrix adjustment = mSymmetry .getMatrix( orientation );
            
            // now, adjust the vertex data
            List newVertices = new ArrayList();
            for ( int i = 0; i < vertices .size(); i++ )
            {
                AlgebraicVector originalVertex = (AlgebraicVector) vertices .get( i );
                // first, subtract the tipVertex if appropriate
                if ( stretchVertexIndices .contains( new Integer( i ) ) )
                    originalVertex = originalVertex .minus( tipVertex );
                // then, rotate to align with the 0-index zone for this orbit
                AlgebraicVector adjustedVertex = adjustment .timesColumn( originalVertex );
                newVertices .add( adjustedVertex );
            }
            vertices = newVertices;
        }

        protected void startBalls( int numVertices )
        {}
        
        protected void startEdges( int numEdges )
        {}

        protected void addEdge( int index, int v1, int v2 )
        {}

        protected void startFaces( int numFaces )
        {}

        protected void startVertices( int numVertices )
        {}
    }    
}
