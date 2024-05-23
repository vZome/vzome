package org.vorthmann.zome.render.jogl;

import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.math.Ray;
import com.jogamp.opengl.math.Vec3f;
import com.vzome.core.math.Line;
import com.vzome.core.render.RenderedManifestation;
import com.vzome.core.render.ShapeAndInstances;

class NearestPicker implements ShapeAndInstances.Intersector
{        
    private final Ray ray;
    private final Vec3f rayPoint, rayDirection;

    private RenderedManifestation nearest = null;
    private float nearestZ = Float .MAX_VALUE;
    private final float[] modelViewProjection = new float[16];

    NearestPicker( Line rayLine, float[] modelView, float[] projection )
    {
        super();
        FloatUtil .multMatrix( projection, modelView, this .modelViewProjection );
        this .ray = new Ray();
        float[] array = new float[3];
        rayLine .getOrigin() .toArray( array );
        this .ray .orig .set( array );
        rayLine .getDirection() .toArray( array );
        this .ray .dir .set( array );
        this .rayPoint = new Vec3f( ray.orig );
        this .rayDirection = new Vec3f( ray.dir );
        this .rayDirection .normalize();
    }
    
    // temporary storage, reset and reused during intersections
    private final float[] verticesIn = new float[16];
    private final float[] verticesOut = new float[16];
    private final float[] vertices3x3 = new float[9];

    @Override
    public void intersectTriangle( float[] verticesArray, int offset, RenderedManifestation rm, float scale, float[] embedding, float[] orientation, float[] location )
    {        
        // Incoming verticesArray is an array larger than 9, and the three vertices are stored in order.

        if ( orientation == null ) {
            for ( int i = 0; i < 3; i++ ) {
                for ( int j = 0; j < 3; j++ ) {
                    this .vertices3x3[ 3*i + j ] = verticesArray[ offset + 3*i + j ] * scale + location[ j ];
                }
            }
        }
        else {
            // If we have an orientation, we have to put them into a 4x4 matrix in order to orient them all at once.
            // Then we add the location and put them back into a 9-element array, vertices3x3.

            for ( int i = 0; i < 3; i++ ) { // ith column
                for ( int j = 0; j < 3; j++ ) { // jth row
                    this .verticesIn[ 4*i + j ] = verticesArray[ offset + 3*i + j ];
                }
            }
            FloatUtil .multMatrix( orientation, this .verticesIn, this .verticesOut );
            for ( int i = 0; i < 3; i++ ) {
                for ( int j = 0; j < 3; j++ ) {
                    this .vertices3x3[ 3*i + j ] = this .verticesOut[ 4*i + j ] * scale + location[ j ];
                }
            }
        }
        intersectTriangle( this .vertices3x3, 0, rm, 1f, embedding );
    }

    // temporary storage, reset and reused during intersections
    private final Vec3f v0 = new Vec3f();
    private final Vec3f v1 = new Vec3f();
    private final Vec3f v2 = new Vec3f();
    private final Vec3f tuv = new Vec3f();
    private final RayTriangleIntersection rayTriangleIntersection = new RayTriangleIntersection();

    @Override
    public void intersectTriangle( float[] verticesArray, int offset, RenderedManifestation rm, float scale, float[] embedding )
    {
        // The triangle is represented by 9 floats from the array, starting at i

        // To apply the embedding, we have to put the vertices into a 4x4 matrix in order to orient them all at once.
        // Then we add the location and put them back into a 9-element array, vertices3x3.

        for ( int i = 0; i < 3; i++ ) { // ith column
            for ( int j = 0; j < 3; j++ ) { // jth row
                this .verticesIn[ 4*i + j ] = verticesArray[ offset + 3*i + j ];
            }
        }
        FloatUtil .multMatrix( embedding, this .verticesIn, this .verticesOut );
        // verticesOut is 4x4, column-major order

        this .v0 .set( verticesOut[ 0 ], verticesOut[ 1 ], verticesOut[ 2 ] );
        this .v1 .set( verticesOut[ 4 ], verticesOut[ 5 ], verticesOut[ 6 ] );
        this .v2 .set( verticesOut[ 8 ], verticesOut[ 9 ], verticesOut[ 10 ] );

        // TODO figure out why we need this scale factor!!
        this .v0 .scale( scale );
        this .v1 .scale( scale );
        this .v2 .scale( scale );
        
        boolean hits = this .rayTriangleIntersection .intersectTriangle( rayPoint, rayDirection, v0, v1, v2, tuv );
        if ( hits ) {
            float u = tuv.y();
            float v = tuv.z();
            v1 .scale( u );
            v1 .plus( v1, v2.scale( v ) );
            v1 .plus( v1, v0.scale( 1f - u - v) );
            setNearest( v1.x(), v1.y(), v1.z(), rm );
        }
    }
    
    private void setNearest( float x, float y, float z, RenderedManifestation rm )
    {
        float[] intInWorldCoords = new float[] { x, y, z, 1 };
        float[] intInDeviceCoords = new float[4];
        FloatUtil .multMatrixVec( modelViewProjection, intInWorldCoords, intInDeviceCoords );
        if ( intInDeviceCoords[2] < nearestZ ) {
            nearest = rm;
            nearestZ = intInDeviceCoords[2];
        }
    }
    
    RenderedManifestation getNearest()
    {
        return this .nearest;
    }
}