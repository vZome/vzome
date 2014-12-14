

package com.vzome.core.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.Quaternion;
import com.vzome.core.algebra.RationalVectors;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.ModelRoot;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.math.DomUtils;
import com.vzome.core.math.Projection;
import com.vzome.core.math.QuaternionProjection;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;

/**
 * @author Scott Vorthmann
 */
public class CommandUniformH4Polytope extends CommandTransform
{
    public void setFixedAttributes( Map attributes, XmlSaveFormat format )
    {
        super.setFixedAttributes( attributes, format );

        this.field = format .getField();
        this.symm = (H4Symmetry) h4Symms .get( field );
        if ( symm == null )
        {
            symm = new H4Symmetry( field );
            h4Symms .put( field, symm );
        }
        this .mRoots = format .getQuaternionicSymmetry( "H_4" ) .getRoots();
    }
    
    private final Map h4Symms = new HashMap();

    public static final String POLYTOPE_INDEX_ATTR_NAME = "polytope.index";
        
    private Quaternion[] mRoots;

    private int mPolytopeIndex = -1;
    
    private static Logger logger = Logger .getLogger( "com.vzome.core.commands.h4polytope" );

    public static class H4Symmetry
    {
        private final /*AlgebraicVector*/ int[][] mPrototypes = new /*AlgebraicVector*/ int[15][];

        private final Quaternion[] mMirrors = new Quaternion[4];
        
        private final int[][] coRoots = new int[4][];

        public H4Symmetry( AlgebraicField field )
        {
            /* from 2.1 code, constructor is (x, y, z, w)
             * 
        mMirrors[ 3 ] = new GoldenNumberVector( A, ONE, B, ZERO ) .div( 2 );
        mMirrors[ 2 ] = new GoldenNumberVector( ZERO, ZERO, TWO.neg(), ZERO ) .div( 2 );
        mMirrors[ 1 ] = new GoldenNumberVector( ONE, ONE.neg(), ONE, ONE.neg() ) .div( 2 );
        mMirrors[ 0 ] = new GoldenNumberVector( ZERO, ZERO, ZERO, TWO ) .div( 2 );

        GoldenVector[] coRoots = new GoldenVector[4];
        coRoots[ 3 ]= new GoldenNumberVector( B2, B2, ZERO, ZERO );
        coRoots[ 2 ] = new GoldenNumberVector( B2 .plus( ONE ), B .plus( TWO ), A, ZERO );
        coRoots[ 1 ] = new GoldenNumberVector( B2, TWO, ZERO, ZERO );
        coRoots[ 0 ] = new GoldenNumberVector( B, ONE, ZERO, A.neg() );
             */

            final /*AlgebraicNumber*/ int[] ONE = field .createRational( new int[]{ 1,1 } );
            final /*AlgebraicNumber*/ int[] TWO = field .createRational( new int[]{ 2,1 } );
            final /*AlgebraicNumber*/ int[] A = field .createAlgebraicNumber( 1, -1, 1, 0 );
            final /*AlgebraicNumber*/ int[] B = field .createAlgebraicNumber( 0, 1, 1, 0 );

            int[] temp = field .origin( 4 );
            field .setVectorComponent( temp, 1, field .divide( A, TWO ) );
            field .setVectorComponent( temp, 2, field .divide( ONE, TWO ) );
            field .setVectorComponent( temp, 3, field .divide( B, TWO ) );
            mMirrors[ 3 ] = new Quaternion( field, temp );

            temp = field .origin( 4 );
            field .setVectorComponent( temp, 3, field .negate( ONE ) );
            mMirrors[ 2 ] = new Quaternion( field, temp );

            temp = field .origin( 4 );
            field .setVectorComponent( temp, 1, field .divide( ONE, TWO ) );
            field .setVectorComponent( temp, 2, field .divide( field .negate( ONE ), TWO ) );
            field .setVectorComponent( temp, 3, field .divide( ONE, TWO ) );
            field .setVectorComponent( temp, 0, field .divide( field .negate( ONE ), TWO ) );
            mMirrors[ 1 ] = new Quaternion( field, temp );

            temp = field .origin( 4 );
            field .setVectorComponent( temp, 0, ONE );
            mMirrors[ 0 ] = new Quaternion( field, temp );

            final /*AlgebraicNumber*/ int[] B2 = field .createAlgebraicNumber( 0, 2, 1, 0 );

            coRoots[ 3 ] = field .origin( 4 );
            field .setVectorComponent( coRoots[ 3 ], 1, B2 );
            field .setVectorComponent( coRoots[ 3 ], 2, B2 );

            coRoots[ 2 ] = field .origin( 4 );
            field .setVectorComponent( coRoots[ 2 ], 1, field .add( B2, ONE ) );
            field .setVectorComponent( coRoots[ 2 ], 2, field .add( B, TWO ) );
            field .setVectorComponent( coRoots[ 2 ], 3, A );

            coRoots[ 1 ] = field .origin( 4 );
            field .setVectorComponent( coRoots[ 1 ], 1, B2 );
            field .setVectorComponent( coRoots[ 1 ], 2, TWO );

            coRoots[ 0 ] = field .origin( 4 );
            field .setVectorComponent( coRoots[ 0 ], 1, B );
            field .setVectorComponent( coRoots[ 0 ], 2, ONE );
            field .setVectorComponent( coRoots[ 0 ], 0, field .negate( A ) );
            
//            coRoots[ 0 ] = field .scaleVector( coRoots[ 0 ], field .createPower( 3 ) );

            if ( logger .isLoggable( Level .FINE ) )
                for ( int i = 0; i < 4; i++ )
                {
                    StringBuffer buf = new StringBuffer();
                    field .getVectorExpression( buf, coRoots[ i ], AlgebraicField .DEFAULT_FORMAT );
                    logger .fine( buf .toString() );
                }

            // Leaving this here, but when I use edgeScales, I'll recompute the prototype each time.
            /*AlgebraicVector*/ int[] origin = field .origin( 4 );
            for ( int index = 1; index <= 15; index++ ){
//                int len = 0;
                /*AlgebraicVector*/ int[] vertex = origin;
                for ( int b = 0; b < 4; b++ ) {
                    int mask = 1 << b;
                    int test = index & mask;
                    if ( test != 0 ) {
//                        ++len;
                        vertex = field .add( vertex, coRoots[ b ] );
                    }
                }
                //          vertex = vertex .div( len );
                mPrototypes[ index-1 ] = vertex;
                //            System .out .println( "\n" + RationalVectors .toString( vertex ) );
            }
        }
        
        public int[] getPrototype( int index )
        {
            return mPrototypes[ index-1 ];
        }

        public int[] reflect( int mirror, int[] prototype )
        {
            return mMirrors[ mirror ] .reflect( prototype );
        }
        
        public int[] getCoRoot( int i )
        {
            return this .coRoots[ i ];
        }
    }
    
    private AlgebraicField field;
    
    private H4Symmetry symm;
    
    public CommandUniformH4Polytope( AlgebraicField field, QuaternionicSymmetry qsymm, int index )
    {
        mPolytopeIndex = index;
        this .field = field;
        this .symm = new H4Symmetry( field );
        this .mRoots = qsymm .getRoots();
    }
        
    public CommandUniformH4Polytope()
    {
        mPolytopeIndex = -1;
        this.field = null;
        this.symm = null;
        this .mRoots = null;
    }

    /*
     * Currently, there is no way to set this via the UI... only reading it from XML
     */
    private int[] quaternionVector = null;

    /**
     * Only called when migrating a 2.0 model file.
     */
    public void setQuaternion( int[] offset )
    {
        quaternionVector = offset;
    }

    /*
     * Adding this to support a 4D quaternion.
     */
    public Map setXml( Element xml, XmlSaveFormat format ) 
    {
        Map attrs = super .setXml( xml, format );
        
        quaternionVector = format .parseRationalVector( xml, "quaternion" );
        
        return attrs;
    }
    
    public void getXml( Element result, Map attributes )
    {
        if ( quaternionVector != null )
        	DomUtils .addAttribute( result, "quaternion", RationalVectors .toString( quaternionVector ) );        
        super .getXml( result, attributes );
    }

    public Object[][] getAttributeSignature()
    {
        return GROUP_ATTR_SIGNATURE;
    }
    
    public boolean attributeIs3D( String attrName )
    {
        if ( "symmetry.axis.segment" .equals( attrName ) )
            return false;
        else
            return true;
    }
    
    public ConstructionList apply( final ConstructionList parameters, Map attributes, final ConstructionChanges effects ) throws Failure
    {
        int[] /*AlgebraicNumber*/ SCALE_DOWN_5 = field .createPower( -5 );
        
        Projection proj = new Projection .Default( field );
        /*AlgebraicVector*/ int[] leftQuat = null, rightQuat = null;
        if ( parameters .size() == 0 )
        {        
            rightQuat = quaternionVector;
            Segment symmAxis = (Segment) attributes .get( CommandTransform.SYMMETRY_AXIS_ATTR_NAME );
            
            if ( rightQuat == null )
                rightQuat = (symmAxis==null)? null : symmAxis .getOffset();
            
            if ( rightQuat != null )
                rightQuat = field .scaleVector( rightQuat, SCALE_DOWN_5 );
        }
        else
        {
            int numSegs = 0;
            for ( Iterator params = parameters .iterator(); params .hasNext(); ) {
                Construction cons = (Construction) params .next();
                if ( cons instanceof Segment ) {
                    Segment seg = (Segment) cons;
                    if ( ++numSegs == 1 )
                        rightQuat = field .scaleVector( seg .getOffset(), SCALE_DOWN_5 );
                    else if ( numSegs == 2 )
                        leftQuat = field .scaleVector( seg .getOffset(), SCALE_DOWN_5 );
                    else
                        throw new Command.Failure( "Too many struts to specify quaternion multiplication." );
                }
            }
        }
//        leftQuat = new GoldenNumberVector( ONE, A, ZERO, B ) .div( 2 );
//        rightQuat = new GoldenNumberVector( A, B, ZERO, ONE.neg() ) .div( 2 );
        if ( rightQuat != null )
            proj = new QuaternionProjection( field, leftQuat, rightQuat );
        
//      proj = new PerspectiveProjection( field, field .add( field .createPower( 3 ), field .createPower( 0 ) ) );
//        proj = new PerspectiveProjection( field, field .subtract( field .createPower( 5 ), field .createPower( 3 ) ) );
//        proj = new PerspectiveProjection( field, field .createPower( 4 ) );
        
        ModelRoot root = (ModelRoot) attributes .get( MODEL_ROOT_ATTR_NAME );
//        final Integer scaleObj = (Integer) attributes .get( SCALE_ATTR_NAME );
//        int scale = 5;
//        if ( scaleObj != null )
//            scale += scaleObj .intValue();
        if ( mPolytopeIndex < 0 ) {
            Integer indexObj = (Integer) attributes .get( POLYTOPE_INDEX_ATTR_NAME );
            mPolytopeIndex = indexObj .intValue();
        }
        else
            // make sure the attr is set, so it get saved with the file
            attributes .put( POLYTOPE_INDEX_ATTR_NAME, new Integer( mPolytopeIndex ) );
        
        generate( proj, mPolytopeIndex, mPolytopeIndex, null, root, effects );
        
        return new ConstructionList();
    }
    
    public void generate( Projection proj, int index, int renderEdges, int[][] edgeScales, ModelRoot root, ConstructionChanges effects )
    {   
        int[] /*AlgebraicNumber*/ SCALE_UP_5 = field .createPower( 5 );

        /*AlgebraicVector*/ int[][] reflections = new /*AlgebraicVector*/ int[4][];
        /*AlgebraicVector*/ int[] prototype = symm .getPrototype( index );

        if ( edgeScales != null )  // ignore the prebuilt prototype
        {
            prototype = field .origin( 4 );
            for ( int b = 0; b < 4; b++ ) {
                int mask = 1 << b;
                int test = index & mask;
                if ( test != 0 ) {
                    int[] contribution = field .scaleVector( symm .getCoRoot( b ), edgeScales[ b ] );
                    prototype = field .add( prototype, contribution );
                }
            }
        }

        for ( int mirror = 0; mirror < 4; mirror++ ) 
            if ( ( renderEdges & ( 1 << mirror ) ) != 0 )
                reflections[ mirror ] = symm .reflect( mirror, prototype );

        Map vertices = new HashMap();
        Set edges = new HashSet();
        StringBuffer vefVertices = new StringBuffer();
        StringBuffer vefEdges = new StringBuffer();
        for ( int i = 0; i < mRoots.length; i++ ) 
            for ( int j = 0; j < mRoots.length; j++ )
            {
                /*AlgebraicVector*/ int[] vertex = mRoots[ i ] .rightMultiply( prototype );
                vertex = mRoots[ j ] .leftMultiply( vertex );
                AlgebraicVector key = new AlgebraicVector( vertex );
                Point p = (Point) vertices .get( key );
                boolean newVertex = p == null;
                if ( newVertex ) {
                    /*AlgebraicVector*/ int[] projected = vertex;
                    
                    logger .finer( "before   : " );
                    printGoldenVector( projected, vefVertices );

                    if ( proj != null )
                        projected = proj .projectImage( projected, true );

                    logger .finer( "projected: " );
                    printGoldenVector( projected, vefVertices );

                    projected = field .scaleVector( projected, SCALE_UP_5 );

                    logger .finer( "scaled   : " );
                    printGoldenVector( projected, vefVertices );

                    p = new FreePoint( projected, root );
                    p .setIndex( vertices .size() );
                    effects .constructionAdded( p );
                    
                    vertices .put( key, p );
                }
                
                for ( int mirror = 0; mirror < 4; mirror++ )
                    if ( reflections[ mirror ] != null )
                    {
                        /*AlgebraicVector*/ int[] other = mRoots[ i ] .rightMultiply( reflections[ mirror ] );
                        other = mRoots[ j ] .leftMultiply( other );
                        key = new AlgebraicVector( other );
                        if ( ! Arrays.equals( other, vertex ) )
                        {
                            Point p2 = (Point) vertices .get( key );
                            if ( p2 == null ) {
                                /*AlgebraicVector*/ int[] projected = other;
                                
                                logger .finer( "before   : " );
                                printGoldenVector( projected, vefVertices );

                                if ( proj != null )
                                    projected = proj .projectImage( projected, true );

                                logger .finer( "projected: " );
                                printGoldenVector( projected, vefVertices );

                                projected = field .scaleVector( projected, SCALE_UP_5 );

                                logger .finer( "scaled   : " );
                                printGoldenVector( projected, vefVertices );

                                p2 = new FreePoint( projected, root );
                                p2 .setIndex( vertices .size() );
                                effects .constructionAdded( p2 );
                                
                                vertices .put( key, p2 );
                            }
                            Edge edge = new Edge( p .getIndex(), p2 .getIndex() );
                            if ( edges .contains( edge ) )
                                continue;
                            edges .add( edge );
                            vefEdges .append( p .getIndex() + "\t" + p2 .getIndex() + "\n" );
                            
                            Segment segment = new SegmentJoiningPoints( p, p2 );
                            effects .constructionAdded( segment );
                        }
                    }
            }

//        try {
//            String wythoff = Integer .toBinaryString( index );
//            wythoff = "0000" .substring( wythoff .length() ) + wythoff;
//            PrintWriter out = new PrintWriter( new FileWriter( new File( "H4_" + wythoff + ".vef" ) ) );
//            out .println( vertices .size() );
//            out .println( vefVertices .toString() );
//            out .println( edges .size() );
//            out .println( vefEdges .toString() );
//            out .close();
//        } catch ( IOException e ) {
//            e.printStackTrace();
//        }
    }

    private void printGoldenVector( /*AlgebraicVector*/ int[] gv, StringBuffer vefVertices )
    {
//        vefVertices .append( gv .getX() .toString( IntegralNumber.VEF_FORMAT ) );
//        vefVertices .append( "\t" );
//        vefVertices .append( gv .getY() .toString( IntegralNumber.VEF_FORMAT ) );
//        vefVertices .append( "\t" );
//        vefVertices .append( gv .getZ() .toString( IntegralNumber.VEF_FORMAT ) );
//        vefVertices .append( "\t" );
//        vefVertices .append( gv .getW() .toString( IntegralNumber.VEF_FORMAT ) );
//        vefVertices .append( "\n" ); 

        if ( logger .isLoggable( Level .FINER ) )
        {
            StringBuffer buf = new StringBuffer();
            field .getVectorExpression( buf, gv, AlgebraicField .DEFAULT_FORMAT );
            logger .finer( buf .toString() );
        }
    }
    
    private static class Edge
    {
        final int p1, p2;
        
        public Edge( int p1, int p2 )
        {
            this.p1 = p1;
            this.p2 = p2;
        }

        public boolean equals( Object obj )
        {
            if ( super.equals( obj ) )
                return true;
            if ( ! ( obj instanceof Edge ) )
                return false;
            Edge that = (Edge) obj;
            if ( this.p1 == that.p1 && this.p2 == that.p2 )
                return true;
            if ( this.p1 == that.p2 && this.p2 == that.p1 )
                return true;
            return false;
        }

        public int hashCode()
        {
            return p1 ^ p2;
        }
    }
}
