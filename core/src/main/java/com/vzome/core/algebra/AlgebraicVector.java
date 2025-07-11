package com.vzome.core.algebra;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.vzome.core.math.RealVector;

/**
 * @author vorth
 *
 */
public final class AlgebraicVector implements Comparable<AlgebraicVector>
{
    public static final int X = 0, Y = 1, Z = 2;

    public static final int W4 = 0, X4 = 1, Y4 = 2, Z4 = 3;

    private final AlgebraicNumber[] coordinates;
    private final AlgebraicField field;

    public AlgebraicVector( AlgebraicNumber[] n )
    {
        coordinates = new AlgebraicNumber[ n.length ];
        System.arraycopy(n, 0, coordinates, 0, n.length);
        this .field = n[ 0 ] .getField();
    }

    public AlgebraicVector( AlgebraicField field, int dims )
    {
        coordinates = new AlgebraicNumber[ dims ];
        for ( int i = 0; i < dims; i++ ) {
            coordinates[ i ] = field .zero();
        }
        this .field = field;
    }
    
    // NOTE: JSweet says it handles varargs, but I got runtime errors until I remove the varargs constructor here
    
    public AlgebraicVector( AlgebraicNumber n1 )
    {
        this( n1 .getField(), 1 );
        this .coordinates[ 0 ] = n1;
    }
    
    public AlgebraicVector( AlgebraicNumber n1, AlgebraicNumber n2 )
    {
        this( n1 .getField(), 2 );
        this .coordinates[ 0 ] = n1;
        this .coordinates[ 1 ] = n2;
    }
    
    public AlgebraicVector( AlgebraicNumber n1, AlgebraicNumber n2, AlgebraicNumber n3 )
    {
        this( n1 .getField(), 3 );
        this .coordinates[ 0 ] = n1;
        this .coordinates[ 1 ] = n2;
        this .coordinates[ 2 ] = n3;
    }
    
    public AlgebraicVector( AlgebraicNumber n1, AlgebraicNumber n2, AlgebraicNumber n3, AlgebraicNumber n4 )
    {
        this( n1 .getField(), 4 );
        this .coordinates[ 0 ] = n1;
        this .coordinates[ 1 ] = n2;
        this .coordinates[ 2 ] = n3;
        this .coordinates[ 3 ] = n4;
    }
    
    public AlgebraicVector( AlgebraicNumber n1, AlgebraicNumber n2, AlgebraicNumber n3, AlgebraicNumber n4, AlgebraicNumber n5 )
    {
        this( n1 .getField(), 5 );
        this .coordinates[ 0 ] = n1;
        this .coordinates[ 1 ] = n2;
        this .coordinates[ 2 ] = n3;
        this .coordinates[ 3 ] = n4;
        this .coordinates[ 4 ] = n5;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result 
                + Arrays.hashCode( coordinates );
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        AlgebraicVector other = (AlgebraicVector) obj;
        if(!field.equals( other.field )) {
            String reason  = "Invalid comparison of " 
                    + getClass().getSimpleName() + "s"
                    + "with different fields: "
                    + field.getName()
                    + " and "
                    + other.field.getName();
            throw new IllegalStateException(reason);
        }
        return Arrays.equals( coordinates, other.coordinates );
    }

    @Override
    public int compareTo(AlgebraicVector other) {
        if ( this == other ) {
            return 0;
        }
        if (other.equals(this)) {
            // intentionally throws a NullPointerException if other is null
            // or an IllegalStateException if fields are different
            return 0;
        }
        int comparison = Integer.compare(coordinates.length, other.coordinates.length);
        if(comparison != 0) {
            return comparison;
        }
        for(int i=0; i < coordinates.length; i++) {
            AlgebraicNumber n1 = this. coordinates[i];
            AlgebraicNumber n2 = other.coordinates[i];
            comparison = n1.compareTo(n2);
            if (comparison != 0) {
                return comparison;
            }
        }
        return comparison;
    }

    public final RealVector toRealVector()
    {
        return new RealVector( this .coordinates[ 0 ] .evaluate(), this .coordinates[ 1 ] .evaluate(), this .coordinates[ 2 ] .evaluate() );
    }

    // An array of 3 doubles is used when high precision (double) vector values are needed
    // without the need to manipulate them, thus no operators as in the RealVector class 
    public final double[] to3dDoubleVector()
    {
        return new double[] { this .coordinates[ 0 ] .evaluate(), this .coordinates[ 1 ] .evaluate(), this .coordinates[ 2 ] .evaluate() };
    }

    /**
     * @return A String with no extended characters so it's suitable for writing
     * to an 8 bit stream such as System.out or an ASCII text log file in Windows.
     * Contrast this with {@link toString()} which contains extended characters (e.g. \u03C6 (phi))
     */
    public final String toASCIIString()
    {
        return this .getVectorExpression( AlgebraicField .EXPRESSION_FORMAT );
    }

    /**
     * @return A String representation that can be persisted to XML and parsed by XmlSaveFormat.parseRationalVector().
     */
    public final String toParsableString()
    {
        return this .getVectorExpression( AlgebraicField .ZOMIC_FORMAT );
    }

    @Override
    public final String toString()
    {
        return toString( AlgebraicField .DEFAULT_FORMAT );
    }

    /**
     * Formats an AlgebraicVector as a String using the specified format.
     *  
     * @param format may be any of the following:
     * {@code AlgebraicField.DEFAULT_FORMAT = 0;    // 4 + 3φ}
     * {@code AlgebraicField.EXPRESSION_FORMAT = 1; // 4 +3*phi}
     * {@code AlgebraicField.ZOMIC_FORMAT = 2;      // 4 3}
     * {@code AlgebraicField.VEF_FORMAT = 3;        // (3,4)}
     * {@code AlgebraicField.MATHML_FORMAT = 4;     // Use getMathML()}
     * {@code AlgebraicField.MATH_FORMAT = 5;       // Originally used in JavaScript parts panel, not in Java}
     * 
     * @return this vector formatted as specified
     */
    public final String toString(int format)
    {
        return this .getVectorExpression( format );
    }

    public AlgebraicNumber getComponent( int i )
    {
        return this .coordinates[ i ];
    }

    @JsonValue
    public AlgebraicNumber[] getComponents()
    {
        return this .coordinates;
    }

    public AlgebraicVector setComponent( int component, AlgebraicNumber coord )
    {
        this .coordinates[ component ] = coord;
        return this;
    }

    public AlgebraicVector negate()
    {
        AlgebraicNumber[] result = new AlgebraicNumber[ this .coordinates .length ];
        for ( int i = 0; i < result .length; i++ ) {
            result[ i ] = this .coordinates[ i ] .negate();
        }
        return new AlgebraicVector( result );
    }

    public AlgebraicVector scale( AlgebraicNumber scale )
    {
        AlgebraicNumber[] result = new AlgebraicNumber[ this .coordinates .length ];
        for ( int i = 0; i < result .length; i++ ) {
            result[ i ] = this .coordinates[ i ] .times( scale );
        }
        return new AlgebraicVector( result );
    }

    @JsonIgnore
    public boolean isOrigin()
    {
        for (AlgebraicNumber coordinate : this .coordinates) {
            if (!coordinate.isZero()) {
                return false;
            }
        }
        return true;
    }

    public AlgebraicVector plus( AlgebraicVector that )
    {
        AlgebraicNumber[] result = new AlgebraicNumber[ this .coordinates .length ];
        for ( int i = 0; i < result .length; i++ ) {
            result[ i ] = this .coordinates[ i ] .plus( that .coordinates[ i ] );
        }
        return new AlgebraicVector( result );
    }

    public AlgebraicVector minus( AlgebraicVector that )
    {
        AlgebraicNumber[] result = new AlgebraicNumber[ this .coordinates .length ];
        for ( int i = 0; i < result .length; i++ ) {
            result[ i ] = this .coordinates[ i ] .minus( that .coordinates[ i ] );
        }
        return new AlgebraicVector( result );
    }

    public int dimension()
    {
        return this .coordinates .length;
    }

    public AlgebraicVector cross( AlgebraicVector that )
    {
        AlgebraicNumber[] result = new AlgebraicNumber[ this .coordinates .length ];

        for ( int i = 0; i < result.length; i++ ) {
            int j = ( i + 1 ) % 3;
            int k = ( i + 2 ) % 3;
            result[ i ] = this .coordinates[ j ] .times( that .coordinates[ k ] )
                    .minus( this .coordinates[ k ] .times( that .coordinates[ j ] ) );
        }
        return new AlgebraicVector( result );
    }

    public AlgebraicVector inflateTo4d()
    {
        return this .inflateTo4d( true );
    }

    public AlgebraicVector inflateTo4d( boolean wFirst )
    {
        if ( this .coordinates .length == 4 ) {
            if ( wFirst )
                return this;
            else
                return new AlgebraicVector( new AlgebraicNumber[] { this .coordinates[ 1 ], this .coordinates[ 2 ], this .coordinates[ 3 ], this .coordinates[ 0 ] } );
        }
        if ( wFirst )
            return new AlgebraicVector( new AlgebraicNumber[] { this .field .zero(), this .coordinates[ 0 ], this .coordinates[ 1 ], this .coordinates[ 2 ] } );
        else // the older usage
            return new AlgebraicVector( new AlgebraicNumber[] { this .coordinates[ 0 ], this .coordinates[ 1 ], this .coordinates[ 2 ], this .field .zero() } );
    }

    public AlgebraicVector projectTo3d( boolean wFirst )
    {
        if ( dimension() == 3 )
            return this;
        if ( wFirst )
            return new AlgebraicVector( new AlgebraicNumber[] { this .coordinates[ 1 ], this .coordinates[ 2 ], this .coordinates[ 3 ] } );
        else
            return new AlgebraicVector( new AlgebraicNumber[] { this .coordinates[ 0 ], this .coordinates[ 1 ], this .coordinates[ 2 ] } );
    }

    /**
     * 
     * @param buf a StringBuffer to which the formatted vector will be appended.
     * @param format may be any of the following:
     * {@code AlgebraicField.DEFAULT_FORMAT = 0;    // 4 + 3φ}
     * {@code AlgebraicField.EXPRESSION_FORMAT = 1; // 4 +3*phi}
     * {@code AlgebraicField.ZOMIC_FORMAT = 2;      // 4 3}
     * {@code AlgebraicField.VEF_FORMAT = 3;        // (3,4)}
     * {@code AlgebraicField.MATHML_FORMAT = 4;     // Use getMathML()}
     * {@code AlgebraicField.MATH_FORMAT = 5;       // Originally used in JavaScript parts panel, not in Java}
     */
    public void getVectorExpression( StringBuffer buf, int format )
    {
        if ( format == AlgebraicField.DEFAULT_FORMAT )
            buf .append( "(" );
        for ( int i = 0; i < this.coordinates.length; i++ ) {
            if ( i > 0 )
                if ( format == AlgebraicField.VEF_FORMAT || format == AlgebraicField.ZOMIC_FORMAT )
                    buf.append( " " );
                else
                    buf.append( ", " );
            this .coordinates[ i ] .getNumberExpression( buf, format );
        }
        if ( format == AlgebraicField.DEFAULT_FORMAT )
            buf .append( ")" );
    }

    /**
     * Formats an AlgebraicVector as a String using the specified format.
     * 
     * Note that {@code AlgebraicVector.toString(int format)} is just a wrapper for this method.
     * TODO: Avoid the redundancy by deprecating this method, then replacing it with toString(format)
     *  
     * @param format may be any of the following:
     * {@code AlgebraicField.DEFAULT_FORMAT = 0;    // 4 + 3φ}
     * {@code AlgebraicField.EXPRESSION_FORMAT = 1; // 4 +3*phi}
     * {@code AlgebraicField.ZOMIC_FORMAT = 2;      // 4 3}
     * {@code AlgebraicField.VEF_FORMAT = 3;        // (3,4)}
     * {@code AlgebraicField.MATHML_FORMAT = 4;     // Use getMathML()}
     * {@code AlgebraicField.MATH_FORMAT = 5;       // Originally used in JavaScript parts panel, not in Java}
     * 
     * @return this vector formatted as specified
     */
    public String getVectorExpression( int format )
    {
        StringBuffer buf = new StringBuffer();
        this .getVectorExpression( buf, format );
        return buf.toString();
    }

    public AlgebraicNumber dot( AlgebraicVector that )
    {
        AlgebraicNumber result = this .field .zero();
        for ( int i = 0; i < that.dimension(); i++ ) {
            result = result .plus( this .coordinates[ i ] .times( that .coordinates[ i ] ) );
        }
        return result;
    }

    public AlgebraicNumber getLength( AlgebraicVector unit )
    {
        for ( int i = 0; i < this.coordinates.length; i++ ) {
            if ( this .coordinates[ i ] .isZero() )
                continue;
            return this .coordinates[ i ] .dividedBy( unit .coordinates[ i ] );
        }
        throw new IllegalStateException( "vector is the origin!" );
    }

    @JsonIgnore
    public AlgebraicField getField()
    {
        return this .field;
    }

}
