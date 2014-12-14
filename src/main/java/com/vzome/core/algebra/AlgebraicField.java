//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package com.vzome.core.algebra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.QuaternionicSymmetry;
import com.vzome.core.math.symmetry.Symmetry;

public abstract class AlgebraicField
{
    public abstract double evaluateNumber( int[] number );

    public abstract int[] createAlgebraicNumber( int ones, int irrat, int denominator, int power );

    public abstract int[] getDefaultStrutScaling();

    public abstract void createRepresentation( int[] number, int i, int[][] representation, int j, int k );

    public abstract int getOrder();
    
    public int getNumIrrationals()
    {
        return 1;
    }
    
    public abstract String getIrrational( int which );

    private final String name;

    private final ArrayList symmetries = new ArrayList();
    
    private final Map quaternionSymmetries = new HashMap();

    private final boolean isRational;

    private AlgebraicField subfield = null;

    public AlgebraicField( String name, boolean rational )
    {
        this.name = name;
        isRational = rational;
    }

    public AlgebraicField( String name )
    {
        this( name, true );
    }

    public AlgebraicField( String name, boolean rational, AlgebraicField subfield )
    {
        this.name = name;
        isRational = rational;
        this .subfield  = subfield;
    }

    public boolean isRational()
    {
        return isRational;
    }

    public String getName()
    {
        return this.name;
    }

    public AlgebraicField getSubfield()
    {
        return subfield;
    }

    public final int[] multiply( int[] v1, int[] v2 )
    {
        int doubling = isRational ? 2 : 1;
        int[][] repr = new int[v1.length / doubling][v1.length];
        createRepresentation( v1, 0, repr, 0, 0 );
        return this.transform( repr, v2 );
    }

    public int[] divide( int[] v1, int[] v2 )
    {
        if ( ! isRational )
            throw new IllegalStateException( "method only supported for rationals" );

        if ( isZero( v2 ) ) {
            if ( 99 == 1 / ( 77 - 77 ) )
                ; // force a divide-by-zero
        }
        int[][] repr = new int[v1.length / 2][v1.length];
        createRepresentation( v2, 0, repr, 0, 0 );
        repr = RationalMatrices.invert( repr );
        return RationalMatrices.transform( repr, v1 );
    }
    
    public BigRational[] makeBigElement( int[] e )
    {
    	BigRational[] result = new BigRational[ e.length / 2 ];
    	for (int i = 0; i < e.length / 2; i++) {
			result[ i ] = new BigRational( e[ i * 2 ], e[ i * 2 + 1 ] );
		}
    	return result;
    }

    public int[] makeIntElement( BigRational[] e )
    {
    	int[] result = new int[ e.length * 2 ];
    	for ( int i = 0; i < e.length; i++ ) {
			result[ i ] = e[ i ] .intNumerator();
			result[ i+1 ] = e[ i ] .intDenominator();
		}
    	return result;
    }

	public BigRational[] negate( BigRational[] e )
	{
    	BigRational[] result = new BigRational[ e.length ];
    	for (int i = 0; i < e.length; i++) {
			result[ i ] = e[ i ] .negate();
		}
    	return result;
	}

	public boolean isZero( BigRational[] e )
	{
    	for ( int i = 0; i < e.length; i++ )
    		if ( ! e[ i ] .isZero() )
    			return false;
		return true;
	}

    public BigRational[] add( BigRational[] v1, BigRational[] v2 )
    {
    	if ( v1.length != v2.length )
    		throw new IllegalArgumentException( "arguments don't match" );
    	BigRational[] result = new BigRational[ v1.length ];
    	for (int i = 0; i < result.length; i++) {
			result[ i ] = v1[ i ] .plus( v2[ i ] );
		}
    	return result;
    }

    public BigRational[] subtract( BigRational[] v1, BigRational[] v2 )
    {
    	if ( v1.length != v2.length )
    		throw new IllegalArgumentException( "arguments don't match" );
    	BigRational[] result = new BigRational[ v1.length ];
    	for (int i = 0; i < result.length; i++) {
			result[ i ] = v1[ i ] .minus( v2[ i ] );
		}
    	return result;
    }

    public BigRational[] divide( BigRational[] v1, BigRational[] v2 )
    {
        throw new IllegalStateException( "BigRational division not supported in this field" );
    }

    public BigRational[] multiply( BigRational[] v1, BigRational[] v2 )
    {
        throw new IllegalStateException( "BigRational multiplication not supported in this field" );
    }

    public void addSymmetry( Symmetry symmetry )
    {
        this.symmetries.add( symmetry );
    }
    
    public Symmetry getSymmetry( String name )
    {
        for ( Iterator iterator = symmetries.iterator(); iterator.hasNext(); ) {
            Symmetry symm = (Symmetry) iterator.next();
            if ( symm .getName() .equals( name ) )
                return symm;
        }
        return null;
    }

    public Symmetry[] getSymmetries()
    {
        return (Symmetry[]) symmetries.toArray( new Symmetry[0] );
    }
    
    public void addQuaternionSymmetry( QuaternionicSymmetry symm )
    {
    	quaternionSymmetries .put( symm .getName(), symm );
    }

    public QuaternionicSymmetry getQuaternionSymmetry( String name )
    {
        return (QuaternionicSymmetry) quaternionSymmetries .get( name );
    }

    /**
     * Create a matrix from column vectors.
     * 
     * @param basis
     *            a vector of column vectors
     * @return
     */
    public final int[][] createMatrix( int[][] basis )
    {
        int doubling = isRational ? 2 : 1;
        int order = getOrder();
        int[][] result = new int[basis[0].length / doubling][basis.length * order * doubling];
        for ( int i = 0; i < basis.length; i++ )
            for ( int j = 0; j < result.length; j += order )
                createRepresentation( basis[i], j, result, j, order * i );
        return result;
    }

    public final int getVectorComponentOffset( int i )
    {
        return i * getOrder() * ( isRational ? 2 : 1 );
    }

    public final int[] getVectorComponent( int[] vector, int i )
    {
        if ( ! isRational )
            throw new IllegalStateException( "method only supported for rationals" );

        int order = getOrder();
        int[] result = new int[2 * order];

        for ( int j = 0; j < order; j++ )
            RationalNumbers.copy( vector, i * order + j, result, j );
        return result;
    }

    public final void setVectorComponent( int[] vector, int i, int[] num )
    {
        if ( ! isRational )
            throw new IllegalStateException( "method only supported for rationals" );

        int order = getOrder();
        for ( int j = 0; j < order; j++ )
            RationalNumbers.copy( num, j, vector, i * order + j );
    }

    /**
     * Drop one coordinate from the 4D vector. If wFirst (the usual), then drop
     * the first coordinate, taking the "imaginary part" of the vector. If
     * !wFirst (for old VEF import, etc.), drop the last coordinate.
     * 
     * @param source
     * @param wFirst
     * @return
     */
    public final int[] projectTo3d( int[] source, boolean wFirst )
    {
        if ( ! isRational )
            throw new IllegalStateException( "method only supported for rationals" );

        int order = getOrder();
        if ( source.length == order * 6 )
            return source;
        else {
            int[] result = new int[order * 6];
            int offset = wFirst ? order : 0;
            for ( int i = 0; i < result.length / 2; i++ )
                RationalNumbers.copy( source, i + offset, result, i );
            return result;
        }
    }

    public final int[] inflateTo4d( int[] source )
    {
        if ( ! isRational )
            throw new IllegalStateException( "method only supported for rationals" );

        int order = getOrder();
        int[] result = new int[order * 8];
        boolean isShort = source.length < order * 8;
        if ( isShort )
            for ( int i = 0; i < order; i++ )
                RationalNumbers.copy( RationalNumbers.ZERO, 0, result, i );
        for ( int i = 0; i < source.length / 2; i++ )
            RationalNumbers.copy( source, i, result, i + ( isShort ? order : 0 ) ); // skip W if < 4D
        return result; // always return a copy
    }

    public final int[] createPower( int power )
    {
        return createAlgebraicNumber( 1, 0, 1, power );
    }

    public final int[] createRational( int[] value )
    {
        return createAlgebraicNumber( value[0], 0, value[1], 0 );
    }
    
    /**
     * Create from an array of ints, including an optional divisor int.
     * The divisor should be the last in the array.
     * @param inputs
     * @return
     */
    public final int[] fromIntegersWithDivisor( int[] inputs )
    {
        int order = getOrder();
        int[] result = new int[ order * 2 ];
        int denominator = 1;
        int[] divisor = null;
        if ( inputs .length > order )
        {
            denominator = inputs[ order ];
            divisor = new int[]{ denominator, 1 };
        }
        for ( int i = 0; i < result.length / 2; i++ ) {
            result[ i * 2 ] = inputs[ i ];
            result[ i * 2 + 1 ] = 1;
            if ( denominator != 1 )
                RationalNumbers.divide( result, i, divisor, 0, result, i );
        }
        return result;
    }
    
    /**
     * Map a field value (array of rationals) to an array of integers
     * with an integer divisor.
     * @param value
     * @return
     */
    public final int[] toIntegersWithDivisor( int[] value )
    {
        int order = getOrder();
        int[] result = new int[ order + 1 ];
        int divisor = 1;
        
        for ( int i = 0; i < order; i++ ) {
            int denominator = value[ i*2 + 1 ];
            if ( denominator != 1 )
            {
                int[] factor = this .createRational( new int[]{ denominator, 1 } );
                value = this .multiply( value, factor );
                divisor = divisor * denominator;
            }
        }
        for ( int i = 0; i < order; i++ )
            result[ i ] = value[ i*2 ];
        result[ order ] = divisor;
        return result;
    }

    public final int[] origin( int dims )
    {
        if ( ! isRational )
            throw new IllegalStateException( "method only supported for rationals" );

        int order = getOrder();
        int[] result = new int[order * dims * 2];
        for ( int i = 0; i < result.length / 2; i++ )
            RationalNumbers.copy( RationalNumbers.ZERO, 0, result, i );
        return result;
    }

    public final int[] basisVector( int dims, int axis )
    {
        if ( ! isRational )
            throw new IllegalStateException( "method only supported for rationals" );

        int order = getOrder();
        int[] result = origin( dims );
        RationalNumbers.copy( RationalNumbers.ONE, 0, result, order * axis );
        return result;
    }

    public final int[][] /* AlgebraicVector */createScalingMatrix( int[] /* AlgebraicNumber */scale, int dimensions )
    {
        int order = getOrder();
        int dim = order * dimensions;
        int[][] matrix = new int[dim][dim * ( isRational ? 2 : 1 )];
        for ( int i = 0; i < dim; i++ )
            matrix[i] = origin( dimensions );
        for ( int i = 0; i < dimensions; i++ )
            createRepresentation( scale, 0, matrix, i * order, i * order );
        return matrix;
    }

    // ======================================================================================
    // number operations
    // ======================================================================================

    public final int[] negate( int[] v )
    {
        if ( ! isRational ) {
            int[] result = new int[v.length];
            for ( int j = 0; j < v.length; j++ )
                result[j] = - v[j];
            return result;
        }

        return RationalVectors.negate( v );
    }

    public final int[] conjugate( int[] v )
    {
        if ( ! isRational )
            throw new IllegalArgumentException( "method only implemented for rationals" );
        int order = getOrder();
        if ( order != 2 )
            throw new IllegalArgumentException( "method only supported for order-2 fields" );

        int[] result = new int[v.length];
        for ( int i = 0; i < v.length / 2; i += 2 ) {
            RationalNumbers.negate( v, i + 1, result, i + 0 );
            RationalNumbers.add( v, i + 0, v, i + 1, result, i + 1 );
        }
        return result;
    }

    public final int[] add( int[] v1, int[] v2 )
    {
        if ( isRational )
            return RationalVectors.addition( v1, v2, true );
        else {
            int[] result = new int[v1.length];
            for ( int i = 0; i < v1.length; i++ )
                result[i] = v1[i] + v2[i];
            return result;
        }
    }

    public final int[] subtract( int[] v1, int[] v2 )
    {
        if ( ! isRational )
            throw new IllegalStateException( "method only supported for rationals" );

        return RationalVectors.addition( v1, v2, false );
    }

    public final boolean isZero( int[] comp )
    {
        return isZero( comp, 0 );
    }

    // ======================================================================================
    // vector operations
    // ======================================================================================

    public final boolean isOrigin( int[] v )
    {
        if ( ! isRational )
            throw new IllegalStateException( "method only supported for rationals" );

        for ( int i = 0; i < v.length / 2; i++ )
            if ( ! RationalNumbers.isZero( v, i ) )
                return false;
        return true;
    }

    public final int[] dot( int[] v1, int[] v2 )
    {
        if ( ! isRational )
            throw new IllegalStateException( "method only supported for rationals" );

        int order = getOrder();
        int[][] dotV1 = new int[order][3 * 2 * order];
        for ( int i = 0; i < 3; i++ )
            createRepresentation( v1, i * order, dotV1, 0, i * order );
        return RationalMatrices.transform( dotV1, v2 );
    }

    public final int[] cross( int[] v1, int[] v2 )
    {
        if ( ! isRational )
            throw new IllegalStateException( "method only supported for rationals" );

        int order = v1.length / 6;
        int[] result = new int[v1.length];
        int[][] componentV1 = new int[3][order * 2];
        int[][][] timesV2 = new int[3][order][order * 2];
        for ( int i = 0; i < 3; i++ ) {
            componentV1[i] = this.getVectorComponent( v1, i );
            int[] componentV2 = this.getVectorComponent( v2, i );
            this.createRepresentation( componentV2, 0, timesV2[i], 0, 0 );
        }
        for ( int i = 0; i < 3; i++ ) {
            // compute the x_j * y_k and x_k * y_j terms of a cross-product, see
            // if they are equal
            int j = ( i + 1 ) % 3;
            int k = ( i + 2 ) % 3;
            int[] p1 = RationalMatrices.transform( timesV2[j], componentV1[k] );
            int[] p2 = RationalMatrices.transform( timesV2[k], componentV1[j] );
            int[] diff = this.subtract( p1, p2 );
            this.setVectorComponent( result, i, diff );
        }
        return result;
    }

    // GA outer product
    public final int[] outer( int[] v1, int[] v2 )
    {
        if ( ! isRational )
            throw new IllegalStateException( "method only supported for rationals" );

        int order = v1.length / 6;
        int[] result = new int[v1.length];
        int[][] componentV1 = new int[3][order * 2];
        int[][][] timesV2 = new int[3][order][order * 2];
        for ( int i = 0; i < 3; i++ ) {
            componentV1[i] = this.getVectorComponent( v1, i );
            int[] componentV2 = this.getVectorComponent( v2, i );
            this.createRepresentation( componentV2, 0, timesV2[i], 0, 0 );
        }
        for ( int i = 0; i < 3; i++ ) {
            // compute the x_j * y_k and x_k * y_j terms of a cross-product, see
            // if they are equal
            int j = ( i + 1 ) % 3;
            int k = ( i + 2 ) % 3;
            int[] p1 = RationalMatrices.transform( timesV2[j], componentV1[k] );
            int[] p2 = RationalMatrices.transform( timesV2[k], componentV1[j] );
            int[] diff = this.subtract( p1, p2 );
            this.setVectorComponent( result, i, diff );
        }
        return result;
    }

    public final RealVector getRealVector( int[] v )
    {
        double x = this.evaluateNumber( this.getVectorComponent( v, 0 ) );
        double y = this.evaluateNumber( this.getVectorComponent( v, 1 ) );
        double z = this.evaluateNumber( this.getVectorComponent( v, 2 ) );
        return new RealVector( x, y, z );
    }

    /**
     * 
     * @param field
     * @param v
     * @param out
     *            non-null array of three doubles
     * @return the array passed in as 'out', with double values assigned
     */
    public final double[] getRealVector( int[] v, double[] out )
    {
        if ( ! isRational )
            throw new IllegalStateException( "method only supported for rationals" );

        int n = this.getOrder();
        for ( int i = 0; i < 3; i++ )
            out[i] = RationalNumbers.getReal( v, n * 2 * i );
        return out;
    }

    public final int[] /* AlgebraicVector */ scaleVector( int[] /* AlgebraicVector */vector, int[] /* AlgebraicNumber */scale )
    {
        if ( ! isRational )
            throw new IllegalStateException( "method only supported for rationals" );

        int order = scale.length / 2;
        int dim = vector.length / 2;
        return this.transform( this.createScalingMatrix( scale, dim / order ), vector );
    }

    // ======================================================================================
    // matrix operations
    // ======================================================================================

    public final int[] transform( int[] v, int[][] m )
    {
        if ( isRational )
            return RationalMatrices.transform( v, m );
        else {
            if ( m.length != v.length )
                throw new IllegalArgumentException( "multiplying " + m.length + "-row matrix by " + v.length + "-column vector." );

            int[] result = new int[v.length];
            for ( int j = 0; j < m[0].length; j++ ) {
                int sum = 0;
                for ( int k = 0; k < m.length; k++ )
                    sum += v[k] * m[k][j];
                result[j] = sum;
            }
            return result;
        }
    }

    public final int[] transform( int[][] m, int[] v )
    {
        if ( isRational )
            return RationalMatrices.transform( m, v );
        else {
            if ( m[0].length != v.length )
                throw new IllegalArgumentException( "multiplying " + m.length + "-row matrix by " + v.length + "-column vector." );

            int[] result = new int[m.length];
            for ( int i = 0; i < m.length; i++ ) {
                int sum = 0;
                for ( int k = 0; k < m[0].length; k++ )
                    sum += m[i][k] * v[k];
                result[i] = sum;
            }
            return result;
        }
    }

    public final int[][] invert( int[][] matrix )
    {
        if ( isRational )
            return RationalMatrices.invert( matrix );
        else
            throw new IllegalStateException( "method only supported for rationals" );
    }

    public int[] divideVectors( int[] offset, int[] is )
    {
        throw new IllegalStateException( "method not implemented" );
    }

    public void defineMultiplier( StringBuffer buf, int i )
    {
    }
    
    public static int DEFAULT_FORMAT = 0; // 4 + 3 \u03C4

    public static int EXPRESSION_FORMAT = 1; // 4+\u03C4*3

    public static int ZOMIC_FORMAT = 2; // 4 3

    public static int VEF_FORMAT = 3; // (3,4)

    public String getVectorExpression( int[] vector, int format )
    {
        StringBuffer buf = new StringBuffer();
        getVectorExpression( buf, vector, format );
        return buf.toString();
    }

    public void getNumberExpression( StringBuffer buf, int[] vector, int coord, int format )
    {
        int order = getOrder();
        buf.append( RationalNumbers.toString( vector, coord * order ) );
    }

    public void getVectorExpression( StringBuffer buf, int[] loc, int format )
    {
        if ( format == ZOMIC_FORMAT )
        {
            buf .append( RationalNumbers.toString( loc ) );
            return;
        }
        
        int dim = loc.length / ( 2 * getOrder() ); // rationals only
        for ( int i = 0; i < dim; i++ ) {
            if ( i > 0 )
                if ( format == VEF_FORMAT )
                    buf.append( " " );
                else
                    buf.append( ", " );
            getNumberExpression( buf, loc, i, format );
        }
    }

    public void shiftXtoW( int[] source )
    {
        if ( ! isRational )
            throw new IllegalStateException( "method only supported for rationals" );

        int order = getOrder();
        int[] wValue = getVectorComponent( source, RationalMatrices.W4 );
        for ( int i = 0; i < source.length / 2 - order; i++ ) {
            RationalNumbers.copy( source, i + order, source, i );
        }
        setVectorComponent( source, RationalMatrices.Z4, wValue );
    }

    public final boolean isZero( int[] storage, int offset )
    {
        if ( ! isRational )
            throw new IllegalStateException( "method only supported for rationals" );

        int order = getOrder();
        for ( int i = 0; i < order; i++ )
            if ( ! RationalNumbers.isZero( storage, offset + i ) )
                return false;
        return true;
    }

    public void multiply(
        int[] leftStorage, int leftOffset, int[] rightStorage, int rightOffset, int[] resultStorage, int resultOffset )
    {
        // int doubling = isRational ? 2 : 1;
        // int[][] repr = new int[v1.length / doubling][v1.length];
        // createRepresentation( v1, 0, repr, 0, 0 );
        // int[] result = this.transform( repr, v2 );
        // for ( int i = 0; i < source.length / 2 - order; i++ ) {
        // RationalNumbers.copy( source, i + order, source, i );
        // }
    }

    public void negate( int[] storage, int offset )
    {
        if ( ! isRational )
            throw new IllegalStateException( "method only supported for rationals" );

        for ( int j = 0; j < getOrder(); j++ )
            RationalNumbers.negate( storage, j + offset, storage, j + offset );
    }

    public void add( int[] lStorage, int lOffset, int[] rStorage, int rOffset, int[] sumStorage, int sumOffset )
    {
        if ( ! isRational )
            throw new IllegalStateException( "method only supported for rationals" );

        for ( int j = 0; j < getOrder(); j++ )
            RationalNumbers.add( lStorage, lOffset + j, rStorage, rOffset + j, sumStorage, sumOffset + j );
    }

    /*
     * array-based implementation uses divNormal; subclass may use normal and division instead.    
     */
    public int[] getLength( int[] vector, int[] normal, int[][][] divNormal )
    {
        for ( int i = 0; i < 3; i++ ) {
            int[] comp = this .getVectorComponent( vector, i );
            if ( this .isZero( comp ) )
                continue;
            return this .transform( divNormal[ i ], comp );
        }
        throw new IllegalStateException( "vector is the origin!" );
    }

    /*
     * array-based implementation uses timesNormal; subclass may use normal and multiply instead.    
     */
    public boolean isParallel( int[] vector, int[] normal, int[][][] timesNormal )
    {
        int[][] component = new int[3][];
        for ( int i = 0; i < 3; i++ )
            component[ i ] = this .getVectorComponent( vector, i );
        for ( int i = 0; i < 3; i++ ) {
            // compute the x_j * y_k and x_k * y_j terms of a cross-product, see if they are equal
            int j = (i+1) % 3;
            int k = (i+2) % 3;
            int[] p1 = this .transform( timesNormal[ j ], component[ k ] );
            int[] p2 = this .transform( timesNormal[ k ], component[ j ] );
            if ( ! Arrays .equals( p1, p2 ) )
                return false;
        }
        return true;
    }

    /*
     * array-based implementation uses timesNormal; subclass may use normal and multiply instead.    
     */
    public int[] scaleNormal( int[] length, int[] normal, int[][][] timesNormal )
    {
        int[] result = new int[ normal.length ];
        for ( int i = 0; i < 3; i++ ) {
            int[] sn_i = this .transform( timesNormal[ i ], length );
            this .setVectorComponent( result, i, sn_i );
        }
        return result;
    }
}
