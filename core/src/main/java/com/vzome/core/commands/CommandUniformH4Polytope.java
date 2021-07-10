

package com.vzome.core.commands;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.Quaternion;
import com.vzome.core.construction.Construction;
import com.vzome.core.construction.ConstructionChanges;
import com.vzome.core.construction.ConstructionList;
import com.vzome.core.construction.FreePoint;
import com.vzome.core.construction.Point;
import com.vzome.core.construction.Segment;
import com.vzome.core.construction.SegmentJoiningPoints;
import com.vzome.core.math.Projection;
import com.vzome.core.math.QuaternionProjection;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;
import com.vzome.core.math.symmetry.WythoffConstruction;
import com.vzome.xml.DomUtils;

/**
 * @author Scott Vorthmann
 */
public class CommandUniformH4Polytope extends CommandTransform
{
    @Override
    public void setFixedAttributes( AttributeMap attributes, XmlSaveFormat format )
    {
        super.setFixedAttributes( attributes, format );

        this.field = format .getField();
        this.symm = h4Symms .get( field );
        if ( symm == null )
        {
            symm = new H4Symmetry( field );
            h4Symms .put( field, symm );
        }
        this .mRoots = ((XmlSymmetryFormat) format) .getQuaternionicSymmetry( "H_4" ) .getRoots();
    }
    
    private final Map<AlgebraicField, H4Symmetry> h4Symms = new HashMap<>();

    public static final String POLYTOPE_INDEX_ATTR_NAME = "polytope.index";
        
    private Quaternion[] mRoots;

    private int mPolytopeIndex = -1;
    
    private static final Logger logger = Logger .getLogger( "com.vzome.core.commands.h4polytope" );

    public static class H4Symmetry
    {
        private final AlgebraicVector[] mPrototypes = new AlgebraicVector[15];

        private final Quaternion[] mMirrors = new Quaternion[4];
        
        private final AlgebraicVector[] coRoots = new AlgebraicVector[4];

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

            final AlgebraicNumber ONE = field .createRational( 1 );
            final AlgebraicNumber NEG_ONE = field .createRational( -1 );
            final AlgebraicNumber TWO = field .createRational( 2 );
            final AlgebraicNumber A = field .createAlgebraicNumber( 1, -1, 1, 0 );
            final AlgebraicNumber B = field .createAlgebraicNumber( 0, 1, 1, 0 );

            AlgebraicVector temp = field .origin( 4 );
            temp .setComponent( 1, A .dividedBy( TWO ) );
            temp .setComponent( 2, ONE .dividedBy( TWO ) );
            temp .setComponent( 3, B .dividedBy( TWO ) );
            mMirrors[ 3 ] = new Quaternion( field, temp );

            temp = field .origin( 4 );
            temp .setComponent( 3, NEG_ONE );
            mMirrors[ 2 ] = new Quaternion( field, temp );

            temp = field .origin( 4 );
            temp .setComponent( 1, ONE .dividedBy( TWO ) );
            temp .setComponent( 2, NEG_ONE .dividedBy( TWO ) );
            temp .setComponent( 3, ONE .dividedBy( TWO ) );
            temp .setComponent( 0, NEG_ONE .dividedBy( TWO ) );
            mMirrors[ 1 ] = new Quaternion( field, temp );

            temp = field .origin( 4 );
            temp .setComponent( 0, ONE );
            mMirrors[ 0 ] = new Quaternion( field, temp );

            final AlgebraicNumber B2 = field .createAlgebraicNumber( 0, 2, 1, 0 );

            coRoots[ 3 ] = field .origin( 4 );
            coRoots[ 3 ] .setComponent( 1, B2 );
            coRoots[ 3 ] .setComponent( 2, B2 );

            coRoots[ 2 ] = field .origin( 4 );
            coRoots[ 2 ] .setComponent( 1, B2 .plus( ONE ) );
            coRoots[ 2 ] .setComponent( 2, B .plus( TWO ) );
            coRoots[ 2 ] .setComponent( 3, A );

            coRoots[ 1 ] = field .origin( 4 );
            coRoots[ 1 ] .setComponent( 1, B2 );
            coRoots[ 1 ] .setComponent( 2, TWO );

            coRoots[ 0 ] = field .origin( 4 );
            coRoots[ 0 ] .setComponent( 1, B );
            coRoots[ 0 ] .setComponent( 2, ONE );
            coRoots[ 0 ] .setComponent( 0, A .negate() );
            
//            coRoots[ 0 ] = field .scaleVector( coRoots[ 0 ], field .createPower( 3 ) );

            if ( logger .isLoggable( Level .FINE ) )
                for ( int i = 0; i < 4; i++ )
                {
                    StringBuffer buf = new StringBuffer();
                    coRoots[ i ] .getVectorExpression( buf, AlgebraicField .DEFAULT_FORMAT );
                    logger .fine( buf .toString() );
                }

            // Leaving this here, but when I use edgeScales, I'll recompute the prototype each time.
            AlgebraicVector origin = field .origin( 4 );
            for ( int index = 1; index <= 15; index++ ){
//                int len = 0;
                AlgebraicVector vertex = origin;
                for ( int b = 0; b < 4; b++ ) {
                    int mask = 1 << b;
                    int test = index & mask;
                    if ( test != 0 ) {
//                        ++len;
                        vertex = vertex .plus( coRoots[ b ] );
                    }
                }
                //          vertex = vertex .div( len );
                mPrototypes[ index-1 ] = vertex;
                //            System .out .println( "\n" + RationalVectors .toString( vertex ) );
            }
        }
        
        public AlgebraicVector getPrototype( int index )
        {
            return mPrototypes[ index-1 ];
        }

        public AlgebraicVector reflect( int mirror, AlgebraicVector prototype )
        {
            return mMirrors[ mirror ] .reflect( prototype );
        }
        
        public AlgebraicVector getCoRoot( int i )
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
    
    public CommandUniformH4Polytope() {} // nullary constructor required for deserializing legacy files

    /*
     * Currently, there is no way to set this via the UI... only reading it from XML
     */
    private AlgebraicVector quaternionVector = null;

    /**
     * Only called when migrating a 2.0 model file.
     */
    @Override
    public void setQuaternion( AlgebraicVector offset )
    {
        quaternionVector = offset;
    }

    /*
     * Adding this to support a 4D quaternion.
     */
    @Override
    public AttributeMap setXml( Element xml, XmlSaveFormat format ) 
    {
        AttributeMap attrs = super .setXml( xml, format );
        
        quaternionVector = format .parseRationalVector( xml, "quaternion" );
        
        return attrs;
    }
    
    @Override
    public void getXml( Element result, AttributeMap attributes )
    {
        if ( quaternionVector != null )
            DomUtils .addAttribute( result, "quaternion", quaternionVector .toParsableString() );        
        super .getXml( result, attributes );
    }

    @Override
    public Object[][] getAttributeSignature()
    {
        return GROUP_ATTR_SIGNATURE;
    }
    
    @Override
    public boolean attributeIs3D( String attrName )
    {
        if ( "symmetry.axis.segment" .equals( attrName ) )
            return false;
        else
            return true;
    }
    
    @Override
    public ConstructionList apply( final ConstructionList parameters, AttributeMap attributes, final ConstructionChanges effects ) throws Failure
    {
        AlgebraicNumber SCALE_DOWN_5 = field .createPower( -5 );
        
        Projection proj = new Projection .Default( field );
        AlgebraicVector leftQuat = null, rightQuat = null;
        if ( parameters .size() == 0 )
        {        
            rightQuat = quaternionVector;
            Segment symmAxis = (Segment) attributes .get( CommandTransform.SYMMETRY_AXIS_ATTR_NAME );
            
            if ( rightQuat == null )
                rightQuat = (symmAxis==null)? null : symmAxis .getOffset();
            
            if ( rightQuat != null )
                rightQuat = rightQuat .scale( SCALE_DOWN_5 );
        }
        else
        {
            int numSegs = 0;
            for (Construction cons : parameters) {
                if ( cons instanceof Segment ) {
                    Segment seg = (Segment) cons;
                    if ( ++numSegs == 1 )
                        rightQuat = seg .getOffset() .scale( SCALE_DOWN_5 );
                    else if ( numSegs == 2 )
                        leftQuat = seg .getOffset() .scale( SCALE_DOWN_5 );
                    else
                        throw new Command.Failure( "Too many struts to specify quaternion multiplication." );
                }
            }
        }
//        leftQuat = new GoldenNumberVector( ONE, A, ZERO, B ) .div( 2 );
//        rightQuat = new GoldenNumberVector( A, B, ZERO, ONE.neg() ) .div( 2 );
        if ( rightQuat != null )
            proj = new QuaternionProjection( field, leftQuat, rightQuat );
        
//      proj = new PerspectiveProjection( field, field .add( field .createPower( 3 ), field .one() ) );
//        proj = new PerspectiveProjection( field, field .subtract( field .createPower( 5 ), field .createPower( 3 ) ) );
//        proj = new PerspectiveProjection( field, field .createPower( 4 ) );
        
//        final Integer scaleObj = (Integer) attributes .get( SCALE_ATTR_NAME );
//        int scale = 5;
//        if ( scaleObj != null )
//            scale += scaleObj;
        if ( mPolytopeIndex < 0 ) {
            Integer indexObj = (Integer) attributes .get( POLYTOPE_INDEX_ATTR_NAME );
            mPolytopeIndex = indexObj;
        }
        else
            // make sure the attr is set, so it get saved with the file
            attributes .put(POLYTOPE_INDEX_ATTR_NAME, mPolytopeIndex);

        generate( mPolytopeIndex, mPolytopeIndex, null, new ConstructionChangesAdapter( effects, proj, field .createPower( 5 ) ) );
        
        return new ConstructionList();
    }
    
    private static class ConstructionChangesAdapter implements WythoffConstruction.Listener
    {
        private final Map<AlgebraicVector, Point> vertices = new HashMap<>();
        private final ConstructionChanges effects;
		private final Projection proj;
		private final AlgebraicNumber scale;
        private final Set<Edge> edges = new HashSet<>();

        
        ConstructionChangesAdapter( ConstructionChanges effects, Projection proj, AlgebraicNumber scale )
        {
			this.effects = effects;
			this.proj = proj;
			this.scale = scale;
        }

        @Override
        public Object addEdge( Object v1, Object v2 )
        {
            Point p1 = (Point) v1;
            Point p2 = (Point) v2;
            Edge edge = new Edge( p1 .getIndex(), p2 .getIndex() );
            if ( edges .contains( edge ) )
                return null;
            edges .add( edge );                            
            this .effects .constructionAdded( new SegmentJoiningPoints( p1, p2 ) );
            return edge;
        }

        @Override
        public Object addFace( Object[] vertices )
        {
            return null;
        }

        @Override
        public Object addVertex( AlgebraicVector vertex )
        {
            Point p = vertices .get( vertex );
            if ( p == null ) {
                AlgebraicVector projected = vertex;
                
                logger .finer( "before   : " );
                printGoldenVector( projected );

                if ( proj != null )
                    projected = proj .projectImage( projected, true );

                logger .finer( "projected: " );
                printGoldenVector( projected );

                projected = projected .scale( scale );

                logger .finer( "scaled   : " );
                printGoldenVector( projected );

                p = new FreePoint( projected );
                p .setIndex( vertices .size() );
                this .effects .constructionAdded( p );
                
                vertices .put( vertex, p );
            }
            return p;
        }

        private void printGoldenVector( AlgebraicVector gv )
        {
//            vefVertices .append( gv .getX() .toString( IntegralNumber.VEF_FORMAT ) );
//            vefVertices .append( "\t" );
//            vefVertices .append( gv .getY() .toString( IntegralNumber.VEF_FORMAT ) );
//            vefVertices .append( "\t" );
//            vefVertices .append( gv .getZ() .toString( IntegralNumber.VEF_FORMAT ) );
//            vefVertices .append( "\t" );
//            vefVertices .append( gv .getW() .toString( IntegralNumber.VEF_FORMAT ) );
//            vefVertices .append( "\n" ); 

            if ( logger .isLoggable( Level .FINER ) )
            {
                StringBuffer buf = new StringBuffer();
                gv .getVectorExpression( buf, AlgebraicField .DEFAULT_FORMAT );
                logger .finer( buf .toString() );
            }
        }
    }

    public void generate( int index, int renderEdges, AlgebraicNumber[] edgeScales, WythoffConstruction.Listener listener )
    {   
        AlgebraicVector[] reflections = new AlgebraicVector[4];
        AlgebraicVector prototype = symm .getPrototype( index );

        if ( edgeScales != null )  // ignore the prebuilt prototype
        {
            prototype = field .origin( 4 );
            for ( int b = 0; b < 4; b++ ) {
                int mask = 1 << b;
                int test = index & mask;
                if ( test != 0 ) {
                    AlgebraicVector contribution = symm .getCoRoot( b ) .scale( edgeScales[ b ] );
                    prototype = prototype .plus( contribution );
                }
            }
        }

        for ( int mirror = 0; mirror < 4; mirror++ ) 
            if ( ( renderEdges & ( 1 << mirror ) ) != 0 )
                reflections[ mirror ] = symm .reflect( mirror, prototype );

        for (Quaternion outerRoot : mRoots) { 
            for (Quaternion innerRoot : mRoots) {
                AlgebraicVector vertex = outerRoot.rightMultiply(prototype);
                vertex = innerRoot.leftMultiply(vertex);

                Object p1 = listener .addVertex( vertex );

                for (int mirror = 0; mirror < 4; mirror++) {
                    if (reflections[ mirror ] != null) {
                        AlgebraicVector other = outerRoot.rightMultiply(reflections[ mirror ]);
                        other = innerRoot.leftMultiply( other );
                        if ( ! other .equals( vertex ) )
                        {
                            Object p2 = listener .addVertex( other );

                            listener .addEdge( p1, p2 );
                        }
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
            
    }
    
    private static class Edge
    {
        final int p1, p2;
        
        public Edge( int p1, int p2 )
        {
            this.p1 = p1;
            this.p2 = p2;
        }

        @Override
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

        @Override
        public int hashCode()
        {
            return p1 ^ p2;
        }
    }
}
