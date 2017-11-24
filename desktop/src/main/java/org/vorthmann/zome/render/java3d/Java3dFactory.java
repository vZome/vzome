
package org.vorthmann.zome.render.java3d;

import java.awt.Color;
import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import org.vorthmann.ui.Controller;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.Polyhedron.Face;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Embedding;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderingChanges;
import com.vzome.core.viewing.Lights;
import com.vzome.desktop.controller.RenderingViewer;

public class Java3dFactory implements RenderingViewer.Factory
{
    private static class GraphicsConfigurationFactory {
        private static final Logger logger = Logger.getLogger(new Throwable().getStackTrace()[0].getClassName());

        private static GraphicsConfiguration GC;

        private static GraphicsConfiguration getGraphicsConfiguration() {
            if(GC == null) {
                long start = System.currentTimeMillis();
                GraphicsConfigTemplate3D gct = new GraphicsConfigTemplate3D();
                gct .setSceneAntialiasing( GraphicsConfigTemplate3D .REQUIRED );
                gct .setDepthSize( 32 );
                GraphicsDevice defaultScreenDevice = GraphicsEnvironment .getLocalGraphicsEnvironment() .getDefaultScreenDevice();
                // Each time that getBestConfiguration() is called on my Windows PC,
                // a new JVM icon is shown on the taskbar for a total of 5 of them.
                // They are all replaced with yet another one as soon as the main window is displayed.
                // The multiple JVM icon symptom doesn't show up on Scott's Mac.
                //
                // Using this static class not only improves the startup time;
                // it also eliminates all but one of the extraneous taskbar icons at startup.
                // TODO: It would be nice if we could figure out how to avoid those taskbar icons all together.
                GraphicsConfiguration gc = defaultScreenDevice.getBestConfiguration( gct );
                if ( gc == null ) {
                    gct .setDepthSize( 24 );
                    gc = defaultScreenDevice.getBestConfiguration( gct );
                }
                if ( gc == null ) {
                    gc = SimpleUniverse .getPreferredConfiguration();
                }
                GC = gc;
                logger .log(Level.INFO, "GraphicsConfiguration initialization in milliseconds: {0}", ( System.currentTimeMillis() - start ));
            }
            return GC;
        }
    }

    protected final Appearances mAppearances;
    
    protected final Appearance outlines;
    
    protected boolean mHasEmissiveColor;
    
    protected final Map<Polyhedron, Map<AlgebraicMatrix, Geometry> > solidGeometries = new HashMap<>();

    protected final Map<Polyhedron, Map<AlgebraicMatrix, Geometry> > outlineGeometries = new HashMap<>();

    private static Logger logger = Logger .getLogger( "org.vorthmann.zome.render.java3d.Java3dFactory" );

	private final Lights lights;

    public Java3dFactory( Lights lights, Colors colors, Boolean useEmissiveColor )
    {
        this .lights = lights;
		mHasEmissiveColor = useEmissiveColor .booleanValue();
        mAppearances = new Appearances( colors, mHasEmissiveColor );
        outlines = new Appearance();
        PolygonAttributes wirePa = new PolygonAttributes( PolygonAttributes.POLYGON_LINE, PolygonAttributes.CULL_BACK, -10f );
        outlines .setPolygonAttributes( wirePa );
        LineAttributes lineAtts = new LineAttributes( 1, LineAttributes .PATTERN_SOLID, true );
        outlines .setLineAttributes( lineAtts );
        outlines .setColoringAttributes( new ColoringAttributes( new Color3f( Color.BLACK ), ColoringAttributes .SHADE_FLAT ) );
    }
    
    @Override
    public RenderingViewer createRenderingViewer( RenderingChanges scene, Component canvas )
    {
        if ( canvas == null ) // this viewer is for offscreen rendering
        {
            canvas = new CapturingCanvas3D( GraphicsConfigurationFactory.getGraphicsConfiguration(), true );
        }
        return new Java3dRenderingViewer( (Java3dSceneGraph) scene, (CapturingCanvas3D) canvas );
    }

    @Override
	public RenderingChanges createRenderingChanges( boolean isSticky, Controller controller )
	{
		return new Java3dSceneGraph( this, this .lights, isSticky, controller );
	}

    Colors getColors()
    {
        return mAppearances .getColors();
    }

    Appearance getAppearance( com.vzome.core.render.Color color, boolean glowing, boolean transparent )
    {
        return mAppearances .getAppearance( color, glowing, transparent );
    }
    
    Appearance getOutlineAppearance()
    {
    	return this .outlines;
    }
    
    // The resulting geometry does not support the polygon offset
    //  required to avoid "stitching" when the line geometry is rendered at the same Z-depth
    //  as the solid geometry.
    Geometry makeOutlineGeometry( Map<AlgebraicMatrix, Geometry> map, Polyhedron poly, AlgebraicMatrix matrix, Embedding embedding )
    {
        Geometry geom = ( map == null )? null : map .get( matrix );

        if ( geom == null ) {

            List<AlgebraicVector> polyVertices = poly .getVertexList();
            Set<Face> faces = poly .getFaceSet();
            int[] counts = new int [ faces .size() ];
            int numIndices = 0;
            int i = 0;
            for (Face face : faces) {
                int arity = face .size();
                if(face.get(0) != face.get(arity-1)) {
                    // make room for one more vertex at the end to close the outline
                    arity++;
                }
                counts[i++] = arity;
                numIndices += arity;
            }
            IndexedLineStripArray strips = new IndexedLineStripArray( polyVertices .size(), GeometryArray.COORDINATES, numIndices, counts );

            i = 0;
            for (AlgebraicVector gv : polyVertices) {
                if ( matrix != null )
                    gv = matrix .timesColumn( gv );
                RealVector v = embedding .embedInR3( gv );
                strips .setCoordinate( i++, new Point3d( v.x, v.y, v.z ) );
            }
            i = 0;
            for (Face face : faces) {
                int arity = face .size();
                for ( int j = 0; j < arity; j++ ){
                    Integer index = face .get( j );
                    strips .setCoordinateIndex(i++, index);
                }
                if(face.get(0) != face.get(arity-1)) {
                    // repeat the first vertex at the end to complete the outline.
                    Integer index = face .get( 0 );
                    strips .setCoordinateIndex(i++, index);
                }
            }

            geom = strips;

            // map is null when shape is a panel
            if ( map != null )
            	map .put( matrix, geom );
        }

        return geom;
    }
    
    Geometry makeOutlineGeometry( RenderedManifestation rm )
    {
    	Polyhedron poly = rm .getShape();
        Map<AlgebraicMatrix, Geometry> map = null;
    	if ( ! poly .isPanel() )
    	{
    		// Panels are all unique shapes, so there is no point in caching the geometries,
    		//   and it causes trouble for embeddings, since panel shapes don't change, but
    		//   their embedded rendering must.
    		
            map = outlineGeometries .get( poly );
            if ( map == null ){
                map = new HashMap<>();
                outlineGeometries .put( poly, map );
            }
    	}
        return makeOutlineGeometry( map, poly, rm .getOrientation(), rm .getEmbedding() );
    }

    Geometry makeSolidGeometry( RenderedManifestation rm )
    {
    	Polyhedron poly = rm .getShape();
    	Map<AlgebraicMatrix, Geometry> map = null;
    	if ( ! poly .isPanel() )
    	{
    		// Panels are all unique shapes, so there is no point in caching the geometries,
    		//   and it causes trouble for embeddings, since panel shapes don't change, but
    		//   their embedded rendering must.
    		
            map = solidGeometries .get( poly );
            if ( map == null ){
                map = new HashMap<>();
                solidGeometries .put( poly, map );
            }
    	}
        return makeGeometry( map, poly, rm .getOrientation(), true, rm .getEmbedding() );
    }

    Geometry makeGeometry( Map<AlgebraicMatrix, Geometry> map, Polyhedron poly, AlgebraicMatrix matrix, boolean makeNormals, Embedding embedding )
    {
    	// map is null when poly is a panel... see above
        Geometry geom = ( map == null )? null : map .get( matrix );

        if ( geom == null ) {

    		if ( logger .isLoggable( Level.FINE ) )
    			logger .fine( "creating geometry for " + poly + " and " + matrix );

            List<AlgebraicVector> vertices = poly .getVertexList();
            Point3d[] coords = new Point3d [ vertices .size() ];
            int i = 0;      
            for (AlgebraicVector gv : vertices) {
                Point3d pt = new Point3d();
                if ( matrix != null )
                    gv = matrix .timesColumn( gv );
                RealVector v = embedding .embedInR3( gv );
                pt.x = v.x; pt.y = v.y; pt.z = v.z;
                coords[i++] = pt;
            }

            Set<Face> faces = poly .getFaceSet();
            int[] stripCounts = new int [ faces .size() ];
            int[] contourCounts = new int [ faces .size() ];
            int numIndices = 0;
            i = 0;
            for (Face face : faces) {
                int arity = face .size();
                contourCounts[i] = 1;
                stripCounts[i++] = arity;
                numIndices += arity;
            }
            int[] indices = new int [ numIndices ];
            i = 0;
            for (Face face : faces) {
                int arity = face .size();
                for ( int j = 0; j < arity; j++ ){
                    Integer index = face .get( j );
                    indices[i++] = index;
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

            // map is null when shape is a panel
            if ( map != null )
            	map .put( matrix, geom );
        }

        return geom;
    }

    boolean hasEmissiveColor()
    {
        return mHasEmissiveColor;
    }

    @Override
    public Component createJ3dComponent( String name )
    {
        return new CapturingCanvas3D( GraphicsConfigurationFactory.getGraphicsConfiguration() );
    }
}
