
//(c) Copyright 2006, Scott Vorthmann

package com.vzome.core.commands;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.algebra.Quaternion;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.math.QuaternionProjection;
import com.vzome.core.math.VefParser;

public class CommandVanOss600Cell extends CommandImportVEFData
{

    public ConstructionList apply( ConstructionList parameters, Map attributes,
            ConstructionChanges effects ) throws Failure
    {
        try {
            InputStream input =
                getClass() .getClassLoader() .getResourceAsStream( "com/vzome/core/commands/600cell.vef" );
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int num;
            while ( ( num = input .read( buf, 0, 1024 )) > 0 )
                out .write( buf, 0, num );
            String vefData = new String( out .toByteArray() );

            ConstructionList result = new ConstructionList();
            AlgebraicField field = new PentagonField();
            
            new VefToModel( null, effects ) .parseVEF( vefData, field );
            
            return result;
        }
        catch (IOException exc) {
            throw new Command.Failure(exc);
        }
    }

    private class VefToModel extends VefParser
    {
        protected final Quaternion mQuaternion;
        
        protected QuaternionProjection mProjection;

        protected Point[] mVertices;
        
        protected final ConstructionChanges mEffects;
        
        protected AlgebraicVector[] mLocations;
        
        public VefToModel( Quaternion quaternion, ConstructionChanges effects )
        {
            mQuaternion = null;
            mEffects = effects;
        }

        protected void startVertices( int numVertices )
        {
            mVertices = new Point[ numVertices ];
            mLocations = new AlgebraicVector[ numVertices ];
            mProjection = null;
        }

        protected void addVertex( int index, AlgebraicVector location )
        {
            mLocations[ index ] = location;
        }

        protected void endVertices()
        {
            AlgebraicField field = getField();

            AlgebraicNumber half = field .createRational( new int[]{ 1,2 } );
            AlgebraicNumber quarter = field .createRational( new int[]{ 1,4 } );

            AlgebraicVector centroid = mLocations[0] .plus( mLocations[48] ) .plus( mLocations[50] ) .plus( mLocations[64] ) .scale( quarter );
            AlgebraicVector edgeCenter = mLocations[0] .plus( mLocations[48] ) .scale( half );  // center of one edge
            AlgebraicVector vertex = mLocations[50];

            AlgebraicVector edgeToVertex = vertex .minus( edgeCenter );
            AlgebraicVector edgeToCenter = centroid .minus( edgeCenter );

            AlgebraicVector symmCenter1 = edgeCenter .plus( edgeToCenter .scale( field .createAlgebraicNumber( 0,3,5,0 ) ) );
            AlgebraicVector symmCenter2 = edgeCenter .plus( edgeToVertex .scale( field .createAlgebraicNumber( 0,2,5,0 ) ) );

            AlgebraicVector direction = symmCenter2 .minus( symmCenter1 );
            
            AlgebraicVector target = symmCenter1 .plus( direction .scale( field .createAlgebraicNumber( 0,1,1,0 ) ) );
            
//            // c0 is now the centroid of a tetrahedron
//
//            GoldenNumber ZERO = new GoldenNumber(0,0,1,0);
//            GoldenNumber ONE = new GoldenNumber(0,1,1,0);
//            GoldenNumber HALF = new GoldenNumber(0,1,2,0);
//            GoldenNumber TAU = new GoldenNumber(1,0,1,0);
//            // (x,y,z,w) form
//            // p has order 3: w = -1/2 = cos(120)
//            GoldenVector p = new GoldenNumberVector( ONE, ONE.neg(), ONE, ONE.neg(), HALF );
//            // q has order 10: w = tau/2 = cos(36)
//            GoldenVector q = new GoldenNumberVector( ZERO, ONE, ONE.minus(TAU), TAU, HALF );
//            
//            GoldenVector c1 = Quaternion .multiply( p, c0, q );
////            c1 = Quaternion .multiply( p, c1, q ); // apply pq twice, so we know we are in a plane
//            
//            // now cobble together a vector that lies in a plane orthogonal to both c1 and c0
//            //   (arbitrarily choose x=0, and z=1, since it should not matter)
//            IntegralNumber y = c0.getW() .times( c1.getZ() ) .minus( c1.getW() .times( c0.getZ() ) );
//            y = y .div( c1.getW() .times( c0.getY() ) .minus( c0.getW() .times( c1.getY() ) ) );
//            IntegralNumber w = y .times( c0.getY() ) .plus( c0.getZ() ) .div( c0.getW() ) .neg();
//
//            GoldenVector projQ = new GoldenNumberVector( ZERO, y, ONE, w );
                        
            mProjection = new QuaternionProjection( field, null, target );
            AlgebraicNumber power5 = field .createPower( 5 );

            for ( int i = 0; i < mLocations.length; i++ ) {
                AlgebraicVector location = mLocations[i] .scale( power5 );
                location = mProjection .projectImage( location, wFirst() );
                mVertices[ i ] = new FreePoint( location );
                mEffects .constructionAdded( mVertices[ i ] );
            }
        }

        protected void addEdge( int index, int v1, int v2 )
        {
            if ( 2 == 1 ) return;
            Point p1 = mVertices[ v1 ], p2 = mVertices[ v2 ];
            if ( p1 == null || p2 == null ) {
                System .out .println( "skipping " + v1 + " " + v2 );
                return;
            }
            Segment seg = new SegmentJoiningPoints( p1, p2 );
            mEffects .constructionAdded( seg );
        }

        protected void startEdges( int numEdges )
        {}

        protected void startFaces( int numFaces )
        {}

        protected void addFace( int index, int[] verts )
        {}

        protected void addBall( int index, int vertex )
        {}

        protected void startBalls( int numVertices )
        {}
    }
}
