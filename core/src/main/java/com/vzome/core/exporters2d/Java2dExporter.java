package com.vzome.core.exporters2d;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.exporters.Exporter3d;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.Polyhedron.Face;
import com.vzome.core.math.RealVector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;
import com.vzome.core.render.Colors;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Camera;
import com.vzome.core.viewing.Lights;


/**
 * An Exporter3d that builds a Java2dSnapshot, for use in rendering to a Snapshot2dPanel
 * or exporting via a SnapshotExporter.
 * @author vorth
 */
public class Java2dExporter extends Exporter3d
{
	private final Camera view;
	
	private final Vector3f[] lightDirs = new Vector3f[3];
	private final Color[] lightColors = new Color[3];
	private final Color ambientLight;
	private final Matrix4d viewTransform;
    private final Matrix4d eyeTrans;

    /**
     * @param v[]
     * @param colors
     * @param lights
     * @param model
     */
    public Java2dExporter( Camera view, Colors colors, Lights lights, RenderedModel model )
    {
        super( view, colors, lights, model );
        
        this .view = view;
        Matrix4d viewMatrix = new Matrix4d();
        this .view .getViewTransform( viewMatrix, 0d );
        this .viewTransform = viewMatrix;
        
        if ( ! view .isPerspective() ) {
            double edge = view .getWidth() / 2;
            this .eyeTrans = ortho( -edge, edge, -edge, edge, view .getNearClipDistance(), view .getFarClipDistance() );
        }
        else
            this .eyeTrans = perspective( view .getFieldOfView(), 1.0d, view .getNearClipDistance(), view .getFarClipDistance() );
            // TODO - make aspect ratio track the screen window shape

        for ( int i = 0; i < lightDirs.length; i++ ) {
            lightDirs[ i ] = new Vector3f();
            // the next line fills in the direction, as well as returning the color... bad style!
            lightColors[ i ] = new Color( mLights .getDirectionalLight( i, lightDirs[ i ] ) .getRGB() );
            // the lights stay fixed relative to the viewpoint, so we must not apply the view transform
            lightDirs[ i ] .normalize();
            lightDirs[ i ] .negate();
        }
        ambientLight = new Color( mLights .getAmbientColor() .getRGB() );

    }
    
	private Java2dSnapshot mSnapshot;
    
    public void setSnapshot( Java2dSnapshot snapshot )
    {
        this.mSnapshot = snapshot;
    }

    @Override
    public void doExport( File directory, Writer writer, int height, int width ) throws Exception
    {
        AlgebraicField field = mModel .getField();
        
        mSnapshot .setStrokeWidth( 0.5f );
        mSnapshot .setRect( new Rectangle2D.Float( 0f, 0f, width, height ) );
        
        mSnapshot .setBackgroundColor( this .getBackgroundColor() );

        List<Vector3f> mappedVertices = new ArrayList<>( 60 );
        for (RenderedManifestation rm : mModel) {
            Polyhedron shape = rm .getShape();
            com.vzome.core.render.Color c = rm .getColor();
            Color color = (c == null)? Color.WHITE : new Color( c .getRGB() );
            
            if ( mSnapshot .isLineDrawing() ) {
                Manifestation m = rm .getManifestation();
                if ( m instanceof Strut ) {
                    AlgebraicVector start = ((Strut) m) .getLocation();
                    AlgebraicVector end = ((Strut) m) .getEnd();
                    Vector3f v0 = mapCoordinates( mModel .renderVector( start ), height, width, field, view );
                    Vector3f v1 = mapCoordinates( mModel .renderVector( end ), height, width, field, view );
                    mSnapshot .addLineSegment( color, v0, v1 );
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
                RealVector rv = location .plus( mModel .renderVector( gv ) );
                Vector3f v = mapCoordinates( rv, height, width, field, view );
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
                    if ( mSnapshot .hasLighting() ) {
                        AlgebraicVector faceNormal = partOrientation .timesColumn( face .getNormal() );
                        RealVector normal = mModel .renderVector( faceNormal ) .normalize();
                        Vector3f normalV = new Vector3f( (float) normal.x, (float) normal.y, (float) normal.z );
                        this .viewTransform .transform( normalV );
                        
                        path .applyLighting( normalV, this .lightDirs, this .lightColors, this .ambientLight );
                    }
                    mSnapshot .addPolygon( path );
                }
            }        
        }

        mSnapshot .depthSort();  // TODO could do more here than just "painter's algorithm"
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
    public static Matrix4d perspective( double fovx, double aspect, double zNear, double zFar )
    {
        Matrix4d m = new Matrix4d();
        double sine, cotangent, deltaZ;
        double half_fov = fovx * 0.5;

        deltaZ = zFar - zNear;
        sine = Math.sin(half_fov);
        cotangent = Math.cos(half_fov) / sine;

        m.m00 = cotangent;
        m.m11 = cotangent * aspect;
        m.m22 = (zFar + zNear) / deltaZ;
        m.m23 = 2.0 * zNear * zFar / deltaZ;
        m.m32 = -1.0;
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
    public static Matrix4d ortho( double left, double right, double bottom,
                           double top, double near, double far )
    {
        Matrix4d m = new Matrix4d();
        double deltax = 1/(right - left);
        double deltay = 1/(top - bottom);
        double deltaz = 1/(far - near);

        m.m00 = 2.0 * deltax;
        m.m03 = -(right + left) * deltax;
        m.m11 = 2.0 * deltay;
        m.m13 = -(top + bottom) * deltay;
        m.m22 = 2.0 * deltaz;
        m.m23 = (far + near) * deltaz;
        m.m01 = m.m02 = m.m10 = m.m12 = m.m20 =
            m.m21 = m.m30 = m.m31 = m.m32 = 0;
        m.m33 = 1;
        return m;
    }

    
    private Vector3f mapCoordinates( RealVector rv, int height, int width, AlgebraicField field, Camera view )
    {
        float xscale = width/2f;
        Point3f vr = new Point3f( (float)rv.x, (float)rv.y, (float)rv.z );
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

    @Override
    public String getFileExtension()
    {
        return "java2d";  // this should never get called
    }

    public Color getBackgroundColor()
    {
        float[] rgb = new float[3];
        this .mLights .getBackgroundColor() .getRGBColorComponents( rgb );
        return new Color( rgb[0], rgb[1], rgb[2] );
    }
}


