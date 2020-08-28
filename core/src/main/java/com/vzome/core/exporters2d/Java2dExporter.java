package com.vzome.core.exporters2d;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.Polyhedron.Face;
import com.vzome.core.math.RealVector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Camera;
import com.vzome.core.viewing.Lights;


/**
 * Builds a Java2dSnapshot, for use in rendering to a Snapshot2dPanel
 * or exporting via a SnapshotExporter.
 * @author vorth
 */
public class Java2dExporter
{
	private transient Matrix4f viewTransform, eyeTrans;
        
    public Java2dSnapshot render2d( RenderedModel model, Camera view, Lights lights, int height, int width, boolean drawLines, boolean doLighting ) throws Exception
    {
        Vector3f[] lightDirs = new Vector3f[ lights .size() ];
        Color[] lightColors = new Color[ lights .size() ];
        Color ambientLight;
        Color background;
        Java2dSnapshot snapshot = new Java2dSnapshot();

        this .viewTransform = new Matrix4f();
        view .getViewTransform( this .viewTransform );
        
        if ( ! view .isPerspective() ) {
            float edge = view .getWidth() / 2;
            this .eyeTrans = ortho( -edge, edge, -edge, edge, view .getNearClipDistance(), view .getFarClipDistance() );
        }
        else
            this .eyeTrans = perspective( view .getFieldOfView(), 1.0f, view .getNearClipDistance(), view .getFarClipDistance() );
            // TODO - make aspect ratio track the screen window shape

        for ( int i = 0; i < lightDirs.length; i++ ) {
            lightDirs[ i ] = new Vector3f();
            // the next line fills in the direction, as well as returning the color... bad style!
            lightColors[ i ] = new Color( lights .getDirectionalLight( i, lightDirs[ i ] ) .getRGB() );
            // the lights stay fixed relative to the viewpoint, so we must not apply the view transform
            lightDirs[ i ] .normalize();
            lightDirs[ i ] .negate();
        }
        ambientLight = new Color( lights .getAmbientColor() .getRGB() );

        float[] rgb = new float[3];
        lights .getBackgroundColor() .getRGBColorComponents( rgb );
        background = new Color( rgb[0], rgb[1], rgb[2] );

        snapshot .setStrokeWidth( 0.5f );
        snapshot .setRect( new Rectangle2D.Float( 0f, 0f, width, height ) );
        
        snapshot .setBackgroundColor( background );

        List<Vector3f> mappedVertices = new ArrayList<>( 60 );
        for (RenderedManifestation rm : model) {
            Polyhedron shape = rm .getShape();
            com.vzome.core.model.Color c = rm .getColor();
            Color color = (c == null)? Color.WHITE : new Color( c .getRGB() );
            
            if ( drawLines ) {
                Manifestation m = rm .getManifestation();
                if ( m instanceof Strut ) {
                    AlgebraicVector start = ((Strut) m) .getLocation();
                    AlgebraicVector end = ((Strut) m) .getEnd();
                    Vector3f v0 = mapCoordinates( model .renderVector( start ), height, width );
                    Vector3f v1 = mapCoordinates( model .renderVector( end ), height, width );
                    snapshot .addLineSegment( color, v0, v1 );
                }
                continue;
            }
            
            List<AlgebraicVector> vertices = shape .getVertexList();
            AlgebraicMatrix partOrientation = rm .getOrientation();
            RealVector location = rm .getLocation();  // should *2?
            
            if ( location == null )
                // avoid NPE reported by Antonio Montero
                continue;
            
            mappedVertices .clear();
            for ( int i = 0; i < vertices .size(); i++ )
            {
                AlgebraicVector gv = vertices .get( i );
                gv = partOrientation .timesColumn( gv );
                RealVector rv = location .plus( model .renderVector( gv ) );
                Vector3f v = mapCoordinates( rv, height, width );
                mappedVertices .add( v );
            }
            
            for (Face face : shape .getFaceSet()) {
                int arity = face .size();
                Java2dSnapshot.Polygon path = new Java2dSnapshot.Polygon( color );
                boolean backFacing = false;
                Vector3f v1 = null, v2 = null;
                for ( int j = 0; j < arity; j++ ){
                    Integer index = face .get( j );
                    Vector3f v = mappedVertices .get( index );
                    path .addVertex( v );
                    switch ( path .size() ) {
                        case 1 :
                            v1 = (Vector3f) v.clone();
                            break;

                        case 2:
                            v2 = (Vector3f) v.clone();
                            break;
                            
                        case 3:
                            Vector3f v3 = (Vector3f) v.clone();
                            v3 .sub( v2 );
                            v2 .sub( v1 );
                            Vector3f normal = new Vector3f();
                            normal .cross( v2, v3 );
                            backFacing = normal .z > 0;
                            break;
                            
                        default :
                            break;
                    }
                }
                path .close();
                if ( ! backFacing )
                {
                    if ( doLighting ) {
                        AlgebraicVector faceNormal = partOrientation .timesColumn( face .getNormal() );
                        RealVector normal = model .renderVector( faceNormal ) .normalize();
                        Vector3f normalV = new Vector3f( normal.x, normal.y, normal.z );
                        this .viewTransform .transform( normalV );
                        
                        path .applyLighting( normalV, lightDirs, lightColors, ambientLight );
                    }
                    snapshot .addPolygon( path );
                }
            }        
        }

        snapshot .depthSort();  // TODO could do more here than just "painter's algorithm"
        return snapshot;
    }
    

    /**
     * Creates a perspective projection transform that mimics a standard,
     * camera-based,
     * view-model.  
     * (From javax.media.j3d.Transform3d.java)
     * This transform maps coordinates from Eye Coordinates (EC)
     * to Clipping Coordinates (CC).  Note that unlike the similar function
     * in OpenGL, the clipping coordinates generated by the resulting
     * transform are in a right-handed coordinate system
     * (as are all other coordinate systems in Java 3D). Also note that the
     * field of view is specified in radians.
     * @param fovx specifies the field of view in the x direction, in radians
     * @param aspect specifies the aspect ratio and thus the field of
     * view in the x direction. The aspect ratio is the ratio of x to y,
     * or width to height.
     * @param zNear the distance to the frustum's near clipping plane.
     * This value must be positive, (the value -zNear is the location of the
     * near clip plane).
     * @param zFar the distance to the frustum's far clipping plane
     */
    public static Matrix4f perspective( float fovx, float aspect, float zNear, float zFar )
    {
        Matrix4f m = new Matrix4f();
        float sine, cotangent, deltaZ;
        float half_fov = fovx * 0.5f;

        deltaZ = zFar - zNear;
        sine = (float) Math.sin(half_fov);
        cotangent = (float) (Math.cos(half_fov) / sine);

        m.m00 = cotangent;
        m.m11 = cotangent * aspect;
        m.m22 = (zFar + zNear) / deltaZ;
        m.m23 = 2.0f * zNear * zFar / deltaZ;
        m.m32 = -1.0f;
        m.m01 = m.m02 = m.m03 = m.m10 = m.m12 = m.m13 = m.m20 =
                m.m21 = m.m30 = m.m31 = m.m33 = 0;

        return m;
    }


    /**
     * Creates an orthographic projection transform that mimics a standard,
     * camera-based,
     * view-model.  
     * (From javax.media.j3d.Transform3d.java)
     * This transform maps coordinates from Eye Coordinates (EC)
     * to Clipping Coordinates (CC).  Note that unlike the similar function
     * in OpenGL, the clipping coordinates generated by the resulting
     * transform are in a right-handed coordinate system
     * (as are all other coordinate systems in Java 3D).
     * @param left the vertical line on the left edge of the near
     * clipping plane mapped to the left edge of the graphics window
     * @param right the vertical line on the right edge of the near
     * clipping plane mapped to the right edge of the graphics window
     * @param bottom the horizontal line on the bottom edge of the near
     * clipping plane mapped to the bottom edge of the graphics window
     * @param top the horizontal line on the top edge of the near
     * clipping plane mapped to the top edge of the graphics window
     * @param near the distance to the frustum's near clipping plane
     * (the value -near is the location of the near clip plane)
     * @param far the distance to the frustum's far clipping plane
     */
    public static Matrix4f ortho( float left, float right, float bottom,
            float top, float near, float far )
    {
        Matrix4f m = new Matrix4f();
        float deltax = 1/(right - left);
        float deltay = 1/(top - bottom);
        float deltaz = 1/(far - near);

        m.m00 = 2.0f * deltax;
        m.m03 = -(right + left) * deltax;
        m.m11 = 2.0f * deltay;
        m.m13 = -(top + bottom) * deltay;
        m.m22 = 2.0f * deltaz;
        m.m23 = (far + near) * deltaz;
        m.m01 = m.m02 = m.m10 = m.m12 = m.m20 =
            m.m21 = m.m30 = m.m31 = m.m32 = 0;
        m.m33 = 1;
        return m;
    }

    
    private Vector3f mapCoordinates( RealVector rv, int height, int width )
    {
        float xscale = width/2f;
        Point3f vr = new Point3f( rv.x, rv.y, rv.z );
        // vr is still in world coordinates
        this .viewTransform .transform( vr );
        // vr is now in view coordinates
        Vector4f p4 = new Vector4f( vr.x, vr.y, vr.z, 1f );
        this .eyeTrans .transform( p4 );
        // p4 is in screen coordinates
        vr .project( new Point4f( p4 ) );

        // The next two lines map Java3d screen coordinates to Java2d coordinates.
        // The rectangles are:
        //
        //     upper left          lower right
        //
        //     (-1, h/w)           (1, -h/w)      Java3d (origin in center, right-handed)
        //
        //     (0,0)               (w, h)         Java2d (pixels, origin upperleft, left-handed (y grows downward))
        //
        //  X2 = (w/2) ( X3 + 1 )
        vr.x = xscale * ( vr.x + 1f );
        //
        //  Y2 = (1/2) ( h - w * Y3 )
        vr.y = ( height - ( width * vr.y) ) / 2f;
        
        return new Vector3f( vr.x, vr.y, vr.z );
    }
}


