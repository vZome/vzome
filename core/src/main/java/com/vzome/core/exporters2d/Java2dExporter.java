package com.vzome.core.exporters2d;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.Polyhedron;
import com.vzome.core.math.Polyhedron.Face;
import com.vzome.core.math.RealMatrix4;
import com.vzome.core.math.RealVector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Strut;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.RenderedModel;
import com.vzome.core.viewing.Lights;


/**
 * Builds a Java2dSnapshot, for use in rendering to a Snapshot2dPanel
 * or exporting via a SnapshotExporter.
 * @author vorth
 */
public class Java2dExporter
{
	private transient RealMatrix4 viewTransform, eyeTrans;
        
    public Java2dSnapshot render2d( RenderedModel model, RealMatrix4 viewTransform, RealMatrix4 eyeTransform, Lights lights, int height, int width, boolean drawLines, boolean doLighting ) throws Exception
    {
        this.viewTransform = viewTransform;
        this.eyeTrans = eyeTransform;
        
        RealVector[] lightDirs = new RealVector[ lights .size() ];
        Color[] lightColors = new Color[ lights .size() ];
        Color ambientLight;
        Color background;
        Java2dSnapshot snapshot = new Java2dSnapshot();


        for ( int i = 0; i < lightDirs.length; i++ ) {
            lightDirs[ i ] = lights .getDirectionalLightVector( i ) .normalize() .negate();
            lightColors[ i ] = new Color( lights .getDirectionalLightColor( i ) .getRGB() );
            // the lights stay fixed relative to the viewpoint, so we must not apply the view transform
        }
        ambientLight = new Color( lights .getAmbientColor() .getRGB() );

        background = new Color( lights .getBackgroundColor() .getRGB() );

        snapshot .setStrokeWidth( 0.5f );
        snapshot .setRect( new Rectangle2D.Float( 0f, 0f, width, height ) );
        
        snapshot .setBackgroundColor( background );

        List<RealVector> mappedVertices = new ArrayList<>( 60 );
        for (RenderedManifestation rm : model) {
            Polyhedron shape = rm .getShape();
            com.vzome.core.construction.Color c = rm .getColor();
            Color color = (c == null)? Color.WHITE : new Color( c .getRGB() );
            
            if ( drawLines ) {
                Manifestation m = rm .getManifestation();
                if ( m instanceof Strut ) {
                    AlgebraicVector start = ((Strut) m) .getLocation();
                    AlgebraicVector end = ((Strut) m) .getEnd();
                    RealVector v0 = mapCoordinates( model .renderVector( start ), height, width );
                    RealVector v1 = mapCoordinates( model .renderVector( end ), height, width );
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
                RealVector v = mapCoordinates( rv, height, width );
                mappedVertices .add( v );
            }
            
            for (Face face : shape .getFaceSet()) {
                int arity = face .size();
                Java2dSnapshot.Polygon path = new Java2dSnapshot.Polygon( color );
                boolean backFacing = false;
                RealVector v1 = null, v2 = null;
                for ( int j = 0; j < arity; j++ ){
                    Integer index = face .get( j );
                    RealVector v = mappedVertices .get( index );
                    path .addVertex( v );
                    switch ( path .size() ) {
                        case 1 :
                            v1 = (RealVector) v.clone();
                            break;

                        case 2:
                            v2 = (RealVector) v.clone();
                            break;
                            
                        case 3:
                            RealVector v3 = (RealVector) v.clone();
                            v3 = v3 .minus( v2 );
                            v2 = v2 .minus( v1 );
                            RealVector normal = v2 .cross( v3 );
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
                        AlgebraicVector faceNormal = partOrientation .timesColumn( face .getNormal( vertices ) );
                        RealVector normal = model .renderVector( faceNormal ) .normalize();
                        RealVector normalV = new RealVector( normal.x, normal.y, normal.z );
                        normalV = this .viewTransform .transform3dVec( normalV );
                        
                        path .applyLighting( normalV, lightDirs, lightColors, ambientLight );
                    }
                    snapshot .addPolygon( path );
                }
            }        
        }

        snapshot .depthSort();  // TODO could do more here than just "painter's algorithm"
        return snapshot;
    }
    
    private RealVector mapCoordinates( RealVector rv, int height, int width )
    {
        float xscale = width/2f;
        // vr is still in world coordinates
        rv = this .viewTransform .transform3dPt( rv );
        // vr is now in view coordinates
        float[] p4 = new float[] { rv.x, rv.y, rv.z, 1f };
        p4 = this .eyeTrans .transform4d( p4 );
        // p4 is in screen coordinates
        float x = p4[0] / p4[3];
        float y = p4[1] / p4[3];
        float z = p4[2] / p4[3];


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
        x = xscale * ( x + 1f );
        //
        //  Y2 = (1/2) ( h - w * Y3 )
        y = ( height - ( width * y) ) / 2f;
        
        return new RealVector( x, y, z );
    }
}


