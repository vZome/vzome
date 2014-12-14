
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.construction;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.math.Projection;
import com.vzome.core.math.QuaternionProjection;
import com.vzome.core.math.VefParser;

public class VefToModel extends VefParser
{
    protected final int[] mQuaternion;
    
    protected final int[] scale, offset;

    protected final AlgebraicField field;
    
    protected Projection mProjection;

    protected Point[] mVertices;
    
    protected final ModelRoot mRoot;
    
    protected final ConstructionChanges mEffects;
    
    protected boolean noBallsSection = true;

    static Logger logger = Logger .getLogger( "com.vzome.core.construction.VefToModel" );

    
    public VefToModel( int[] quaternion, ModelRoot root, ConstructionChanges effects, int[] scale, int[] offset )
    {
        mQuaternion = quaternion;
        mRoot = root;
        mEffects = effects;
        field = root .getField();
        this.scale = scale;
        this.offset = offset;

        if ( quaternion != null && logger .isLoggable( Level .FINEST ) )
            logger .finest( "quaternion = " + field .getVectorExpression( quaternion, AlgebraicField .VEF_FORMAT ) );
    }

    protected void startVertices( int numVertices )
    {
        mVertices = new Point[ numVertices ];
        if ( mQuaternion == null )
            mProjection = new Projection.Default( field );
        else
        {
            int[] quat = field .inflateTo4d( mQuaternion );
            if ( !wFirst() )
                field .shiftXtoW( quat );
                // this is to match the projection, which will discard Z rather than W
            mProjection = new QuaternionProjection( field, null, quat );
        }
    }

    protected void addVertex( int index, int[] location )
    {
        logger .finest( "addVertex location = " + field .getVectorExpression( location, AlgebraicField .VEF_FORMAT ) );
        if ( scale != null )
        {
            location = field .scaleVector( location, scale );
            logger .finest( "scaled = " + field .getVectorExpression( location, AlgebraicField .VEF_FORMAT ) );
        }
        location = mProjection .projectImage( location, wFirst() );
        logger .finest( "projected = " + field .getVectorExpression( location, AlgebraicField .VEF_FORMAT ) );
        if ( offset != null )
        {
            location = field .add( location, offset );
            logger .finest( "translated = " + field .getVectorExpression( location, AlgebraicField .VEF_FORMAT ) );
        }

        mVertices[ index ] = new FreePoint( location, mRoot );
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
        if ( noBallsSection )
            for ( int i = 0; i < mVertices.length; i++ )
                mEffects .constructionAdded( mVertices[ i ] );
    }
    
}