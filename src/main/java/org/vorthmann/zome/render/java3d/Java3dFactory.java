
package org.vorthmann.zome.render.java3d;

import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.vecmath.Point3d;

import org.vorthmann.j3d.J3dComponentFactory;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.RealVector;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.viewing.Lights;
import com.vzome.desktop.controller.RenderingViewer;

public class Java3dFactory implements RenderingViewer.Factory, J3dComponentFactory
{
    protected final Appearances mAppearances;
    
    protected boolean mHasEmissiveColor;
    
    protected final Map mGeomInfoCache = new HashMap();

    public Java3dFactory( Colors colors, Boolean useEmissiveColor )
    {
        mHasEmissiveColor = useEmissiveColor .booleanValue();
        mAppearances = new Appearances( colors, mHasEmissiveColor );
    }
    
    public RenderingViewer createRenderingViewer( RenderingChanges scene, Component canvas )
    {
        if ( canvas == null ) // this viewer is for offscreen rendering
        {
            GraphicsConfigTemplate3D gct = new GraphicsConfigTemplate3D();
            gct .setSceneAntialiasing( GraphicsConfigTemplate3D .REQUIRED );
            gct .setDepthSize( 32 );
            GraphicsConfiguration  gc = GraphicsEnvironment .getLocalGraphicsEnvironment() .getDefaultScreenDevice() .getBestConfiguration( gct );
            if ( gc == null )
            {
                gct .setDepthSize( 24 );
                gc = GraphicsEnvironment .getLocalGraphicsEnvironment() .getDefaultScreenDevice() .getBestConfiguration( gct );
            }
            if ( gc == null )
            {
                gc = SimpleUniverse .getPreferredConfiguration();
            }
            canvas = new CapturingCanvas3D( gc, true );
        }
        return new Java3dRenderingViewer( scene, canvas );
    }


	public RenderingChanges createRenderingChanges( Lights lights, boolean isSticky )
	{
		return new Java3dSceneGraph( this, lights, isSticky );
	}

    Colors getColors()
    {
        return mAppearances .getColors();
    }

    Appearance getAppearance( String colorName, boolean glowing, boolean transparent )
    {
        return mAppearances .getAppearance( colorName, glowing, transparent );
    }

    Geometry makeGeometry( RenderedManifestation rm )
    {
        return makeGeometry( rm .getShape(), rm .getOrientation(), rm .reverseOrder() );
    }

    Geometry makeGeometry( Polyhedron poly, int[][] matrix, boolean reverseFaces )
    {
        Map map = (Map) mGeomInfoCache .get( poly );
        if ( map == null ){
            map = new HashMap();
            mGeomInfoCache .put( poly, map );
        }
        
        GeometryInfo gi = (GeometryInfo) map .get( matrix );

        if ( gi == null ) {

            List vertices = poly .getVertexList();
            AlgebraicField field = poly .getField();
            Point3d[] coords = new Point3d [ vertices .size() ];
            int i = 0;      
            for ( Iterator it = vertices .iterator(); it .hasNext(); ) {
                int[] /*AlgebraicVector*/ gv = (int[]) it .next();
                Point3d pt = new Point3d();
                if ( matrix != null )
                    gv = field .transform( matrix, gv );
                RealVector v = field .getRealVector( gv );
                pt.x = v.x; pt.y = v.y; pt.z = v.z;
                coords[i++] = pt;
            }

            // avoid all this work most of the time... only the actual
            //  vertex coordinates need to be adjusted each time
            Set faces = poly .getFaceSet();
            int[] counts = new int [ faces .size() ];
            i = 0;
            int numIndices = 0;
            i = 0;
            for ( Iterator it = faces .iterator(); it .hasNext(); ){
                int arity = ((List) it .next()) .size();
                counts[i++] = arity;
                numIndices += arity;
            }
            int[] indices = new int [ numIndices ];
            i = 0;
            for ( Iterator it = faces .iterator(); it .hasNext(); ){
                List face = (List) it .next();
                int arity = face .size();
                for ( int j = 0; j < arity; j++ ){
                    Integer index = (Integer) face .get( reverseFaces? arity-j-1 : j );
                    indices[i++] = index .intValue();
                }
            }
        
            gi = new GeometryInfo( GeometryInfo .POLYGON_ARRAY );
            gi .setCoordinateIndices( indices );
            gi .setStripCounts( counts );
            gi .setCoordinates( coords );
            
//          gi .convertToIndexedTriangles();
            
            NormalGenerator ng = new NormalGenerator();
            // zero crease angle means always make creases, no matter how close the normals are
            ng .setCreaseAngle( (float) Math .toRadians( 0 ) );
            ng .generateNormals( gi );
            
             // stripify
             Stripifier st = new Stripifier();
             st.stripify( gi );
             
            map .put( matrix, gi );
        }

        return gi .getIndexedGeometryArray( false );
    }

    boolean hasEmissiveColor()
    {
        return mHasEmissiveColor;
    }

    public Component createJ3dComponent( String name )
    {
        GraphicsConfigTemplate3D gct = new GraphicsConfigTemplate3D();
        gct .setSceneAntialiasing( GraphicsConfigTemplate3D .REQUIRED );
        gct .setDepthSize( 32 );
        GraphicsConfiguration  gc = GraphicsEnvironment .getLocalGraphicsEnvironment() .getDefaultScreenDevice() .getBestConfiguration( gct );
        if ( gc == null )
        {
            gct .setDepthSize( 24 );
            gc = GraphicsEnvironment .getLocalGraphicsEnvironment() .getDefaultScreenDevice() .getBestConfiguration( gct );
        }
        if ( gc == null )
        {
            gc = SimpleUniverse .getPreferredConfiguration();
        }
        return new CapturingCanvas3D( gc );
    }
}
