
package org.vorthmann.zome.render.java3d;

import java.awt.Color;
import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.IndexedLineStripArray;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.PolygonAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import org.vorthmann.j3d.J3dComponentFactory;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;
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
    
    protected final Appearance outlines;
    
    protected boolean mHasEmissiveColor;
    
    protected final Map solidGeometries = new HashMap();

    protected final Map outlineGeometries = new HashMap();

    public Java3dFactory( Colors colors, Boolean useEmissiveColor )
    {
        mHasEmissiveColor = useEmissiveColor .booleanValue();
        mAppearances = new Appearances( colors, mHasEmissiveColor );
        outlines = new Appearance();
        PolygonAttributes wirePa = new PolygonAttributes( PolygonAttributes.POLYGON_LINE, PolygonAttributes.CULL_BACK, -10f );
        outlines .setPolygonAttributes( wirePa );
        LineAttributes lineAtts = new LineAttributes( 1, LineAttributes .PATTERN_SOLID, true );
        outlines .setLineAttributes( lineAtts );
        outlines .setColoringAttributes( new ColoringAttributes( new Color3f( Color.BLACK ), ColoringAttributes .SHADE_FLAT ) );
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


	public RenderingChanges createRenderingChanges( Lights lights, boolean isSticky, boolean outlineMode )
	{
		return new Java3dSceneGraph( this, lights, isSticky, outlineMode );
	}

    Colors getColors()
    {
        return mAppearances .getColors();
    }

    Appearance getAppearance( String colorName, boolean glowing, boolean transparent )
    {
        return mAppearances .getAppearance( colorName, glowing, transparent );
    }
    
    Appearance getOutlineAppearance()
    {
    	return this .outlines;
    }
    
    // The resulting geometry does not support the polygon offset
    //  required to avoid "stitching" when the line geometry is rendered at the same Z-depth
    //  as the solid geometry.
    Geometry makeOutlineGeometry( Map map, Polyhedron poly, AlgebraicMatrix matrix )
    {
        Geometry geom = (Geometry) map .get( matrix );

        if ( geom == null ) {

            List polyVertices = poly .getVertexList();
            Set faces = poly .getFaceSet();
            int[] counts = new int [ faces .size() ];
            int numIndices = 0;
            int i = 0;
            for ( Iterator it = faces .iterator(); it .hasNext(); ){
                int arity = ((List) it .next()) .size();
                counts[i++] = arity;
                numIndices += arity;
            }
            IndexedLineStripArray strips = new IndexedLineStripArray( polyVertices .size(), GeometryArray.COORDINATES, numIndices, counts );

            i = 0;
            for ( Iterator it = polyVertices .iterator(); it .hasNext(); ) {
                AlgebraicVector gv = (AlgebraicVector) it .next();
                if ( matrix != null )
                    gv = matrix .timesColumn( gv );
                RealVector v = gv .toRealVector();
            	strips .setCoordinate( i++, new Point3d( v.x, v.y, v.z ) );
            }
            i = 0;
            for ( Iterator it = faces .iterator(); it .hasNext(); ){
                List face = (List) it .next();
                int arity = face .size();
                for ( int j = 0; j < arity; j++ ){
                    Integer index = (Integer) face .get( j );
                    strips .setCoordinateIndex( i++, index .intValue() );
                }
            }

            geom = strips;
            map .put( matrix, geom );
        }

        return geom;
    }
    
    Geometry makeOutlineGeometry( RenderedManifestation rm )
    {
    	Polyhedron poly = rm .getShape();
        Map map = (Map) outlineGeometries .get( poly );
        if ( map == null ){
            map = new HashMap();
            outlineGeometries .put( poly, map );
        }
        return makeOutlineGeometry( map, poly, rm .getOrientation() );
    }

    Geometry makeSolidGeometry( RenderedManifestation rm )
    {
    	Polyhedron poly = rm .getShape();
        Map map = (Map) solidGeometries .get( poly );
        if ( map == null ){
            map = new HashMap();
            solidGeometries .put( poly, map );
        }
        return makeGeometry( map, poly, rm .getOrientation(), rm .reverseOrder(), true );
    }

    Geometry makeGeometry( Map map, Polyhedron poly, AlgebraicMatrix matrix, boolean reverseFaces, boolean makeNormals )
    {
        Geometry geom = (Geometry) map .get( matrix );
        if ( geom == null ) {

            List vertices = poly .getVertexList();
            Point3d[] coords = new Point3d [ vertices .size() ];
            int i = 0;      
            for ( Iterator it = vertices .iterator(); it .hasNext(); ) {
                AlgebraicVector gv = (AlgebraicVector) it .next();
                Point3d pt = new Point3d();
                if ( matrix != null )
                    gv = matrix .timesColumn( gv );
                RealVector v = gv .toRealVector();
                pt.x = v.x; pt.y = v.y; pt.z = v.z;
                coords[i++] = pt;
            }

            Set faces = poly .getFaceSet();
            int[] stripCounts = new int [ faces .size() ];
            int[] contourCounts = new int [ faces .size() ];
            i = 0;
            int numIndices = 0;
            i = 0;
            for ( Iterator it = faces .iterator(); it .hasNext(); ){
                int arity = ((List) it .next()) .size();
                contourCounts[i] = 1;
                stripCounts[i++] = arity;
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
        
            GeometryInfo gi = new GeometryInfo( GeometryInfo .POLYGON_ARRAY );
            gi .setCoordinates( coords );
            gi .setCoordinateIndices( indices );
            gi .setStripCounts( stripCounts );
            gi .setContourCounts( contourCounts );
            
//          gi .convertToIndexedTriangles();
            
            if ( makeNormals ) {
            	NormalGenerator ng = new NormalGenerator();
            	// zero crease angle means always make creases, no matter how close the normals are
            	ng .setCreaseAngle( (float) Math .toRadians( 0 ) );
            	ng .generateNormals( gi );
                
                // stripify
                Stripifier st = new Stripifier();
                st.stripify( gi );
            }
            
            geom = gi .getGeometryArray();
            if ( makeNormals )
                geom .setCapability( Geometry.ALLOW_INTERSECT );
            map .put( matrix, geom );
        }

        return geom;
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
