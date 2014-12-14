
//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.math.symmetry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.Quaternion;
import com.vzome.core.math.VefParser;

/**
 * @author Scott Vorthmann
 *
 */
public class QuaternionicSymmetry
{    
//    private static final IntegralNumber ONE = GoldenNumber.ONE, SIGMA = GoldenNumber.TAU_INV.neg(),
//                            TAU = GoldenNumber.TAU, ZERO = GoldenNumber.ZERO, HALF = GoldenNumber.HALF;
    
//    public static final int[] /*AlgebraicVector*/ I_GENERATOR = new GoldenNumberVector( ONE, SIGMA, TAU, ZERO, HALF );
//    
//    public static final int[] /*AlgebraicVector*/ T_GENERATOR = new GoldenNumberVector( ONE, ZERO, ZERO, ZERO );
    
//    public static final int[] /*AlgebraicVector*/ OMEGA = new GoldenNumberVector( ONE, ONE, ONE, ONE.neg(), HALF );
    
//    public static final QuaternionicSymmetry IxT = new QuaternionicSymmetry( 
//                                                         new int[] /*AlgebraicVector*/[]{ I_GENERATOR, OMEGA }, 
//                                                         new int[] /*AlgebraicVector*/[]{ T_GENERATOR, OMEGA } );
    
    private Quaternion[] mRoots;
    
    private final String mName;
    
    public QuaternionicSymmetry( String name, String rootsResource, AlgebraicField field )
    {
        mName = name;
        try {
            InputStream input =
                getClass() .getClassLoader() .getResourceAsStream( rootsResource );
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int num;
            while ( ( num = input .read( buf, 0, 1024 )) > 0 )
                out .write( buf, 0, num );
            String vefData = new String( out .toByteArray() );
            RootParser parser = new RootParser( field );
            parser .parseVEF( vefData, field );
            mRoots = parser .getQuaternions();
        }
        catch (IOException exc) {
            exc.printStackTrace();
        }
    }

//    private QuaternionicSymmetry( int[] /*AlgebraicVector*/[] leftGens, int[] /*AlgebraicVector*/[] rightGens )
//    {
//        // TODO Auto-generated constructor stub
//        mName = "whatever";
//    }

    public Quaternion[] getRoots()
    {
        return mRoots;
    }
    
    private final static class RootParser extends VefParser
    {
        private Quaternion[] mRoots;

        private final AlgebraicField field;
        
        RootParser( AlgebraicField field )
        {
            this.field = field;
            HALF = field .createRational( new int[]{1,2} );
        }

        protected void startVertices( int numVertices )
        {
            mRoots = new Quaternion[ numVertices ];
        }

        public Quaternion[] getQuaternions()
        {
            return mRoots;
        }

        private final int[] HALF;
        
        protected void addVertex( int index, int[] /*AlgebraicVector*/ location )
        {
            mRoots[ index ] = new Quaternion( field, field .scaleVector( location, HALF ) );
        }

        protected void startEdges( int numEdges )
        {}

        protected void addEdge( int index, int v1, int v2 )
        {}

        protected void startFaces( int numFaces )
        {}

        protected void addFace( int index, int[] verts )
        {}

        protected void addBall( int index, int vertex )
        {}

        protected void startBalls( int numVertices )
        {}

        protected void endFile( StringTokenizer tokens )
        {}
        
    }

    public String getName()
    {
        return mName;
    }



    
}
