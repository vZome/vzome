
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.construction;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.Projection;
import com.vzome.core.math.QuaternionProjection;
import com.vzome.core.math.VefParser;

public class VefToModel extends VefParser
{
    protected final AlgebraicVector mQuaternion, offset;
    
    protected final AlgebraicNumber scale;

    protected final AlgebraicField field;
    
    protected Projection mProjection;

    protected Point[] mVertices;
        
    protected final ConstructionChanges mEffects;
    
    protected boolean noBallsSection = true;

    private static final Logger logger = Logger .getLogger( "com.vzome.core.construction.VefToModel" );

    
    public VefToModel( AlgebraicVector quaternion, ConstructionChanges effects, AlgebraicNumber scale, AlgebraicVector offset )
    {
        mQuaternion = quaternion;
        mEffects = effects;
        field = scale .getField();
        this.scale = scale;
        this.offset = offset;

        if ( quaternion != null && logger .isLoggable( Level .FINEST ) )
            logger .finest( "quaternion = " + quaternion .getVectorExpression( AlgebraicField .VEF_FORMAT ) );
    }

    protected void startVertices( int numVertices )
    {
        mVertices = new Point[ numVertices ];
        if ( mQuaternion == null )
            mProjection = new Projection.Default( field );
        else
        {
            AlgebraicVector quat = mQuaternion .inflateTo4d( wFirst() );
                // wFirst() is to match the projection, which will discard Z rather than W
            mProjection = new QuaternionProjection( field, null, quat );
        }
    }

    protected void addVertex( int index, AlgebraicVector location )
    {
        logger .finest( "addVertex location = " + location .getVectorExpression( AlgebraicField .VEF_FORMAT ) );
        if ( scale != null )
        {
            location = location .scale( scale );
            logger .finest( "scaled = " + location .getVectorExpression( AlgebraicField .VEF_FORMAT ) );
        }
        location = mProjection .projectImage( location, wFirst() );
        logger .finest( "projected = " + location .getVectorExpression( AlgebraicField .VEF_FORMAT ) );
        if ( offset != null )
        {
            location = location .plus( offset );
            logger .finest( "translated = " + location .getVectorExpression( AlgebraicField .VEF_FORMAT ) );
        }

        mVertices[ index ] = new FreePoint( location );
        mVertices[ index ] .setIndex( index );
    }

    protected void startEdges( int numEdges )
    {}

    protected void addEdge( int index, int v1, int v2 )
    {
        Point p1 = mVertices[ v1 ], p2 = mVertices[ v2 ];
        if ( p1 == null || p2 == null ) return;
        Segment seg = new SegmentJoiningPoints( p1, p2 );
        seg .setIndex( index );
        mEffects .constructionAdded( seg );
    }

    protected void startFaces( int numFaces )
    {}

    protected void addFace( int index, int[] verts )
    {
        Point[] points = new Point[ verts.length ];
        for ( int i = 0; i < verts.length; i++ )
            points[i] = mVertices[ verts[ i ] ];
        Polygon panel = new PolygonFromVertices( points );
        panel .setIndex( index );
        mEffects .constructionAdded( panel );
    }

    protected void addBall( int index, int vertex )
    {
        mEffects .constructionAdded( mVertices[ vertex ] );
    }

    protected void startBalls( int numVertices )
    {
        noBallsSection = false;
    }

    protected void endFile( StringTokenizer tokens )
    {
        if ( noBallsSection ) {
            for (Point vertex : mVertices) {
                mEffects.constructionAdded(vertex);
            }
        }
    }
    
}
