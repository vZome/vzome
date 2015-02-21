package com.vzome.core.algebra;

import java.util.Arrays;

import com.vzome.core.math.RealVector;


/**
 * @author vorth
 *
 */
public final class AlgebraicVector
{
    public static final int X = 0, Y = 1, Z = 2;

    public static final int W4 = 0, X4 = 1, Y4 = 2, Z4 = 3;

    private final AlgebraicNumber[] coordinates;
    private final AlgebraicField field;
    
    public AlgebraicVector( AlgebraicNumber... n )
    {
        coordinates = new AlgebraicNumber[ n.length ];
        for ( int i = 0; i < n.length; i++ ) {
            coordinates[ i ] = n[ i ];
        }
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

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode( coordinates );
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
        if ( !Arrays.equals( coordinates, other.coordinates ) )
            return false;
        return true;
    }
    
    public final RealVector toRealVector()
    {
        // TODO assert this is 3d
        return new RealVector( this .coordinates[ 0 ] .evaluate(), this .coordinates[ 1 ] .evaluate(), this .coordinates[ 2 ] .evaluate() );
    }
    
    public final String toString()
    {
        return this .getVectorExpression( AlgebraicField .DEFAULT_FORMAT );
    }

    public AlgebraicNumber getComponent( int i )
    {
        return this .coordinates[ i ];
    }

    public void setComponent( int component, AlgebraicNumber coord )
    {
        this .coordinates[ component ] = coord;
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

    public boolean isOrigin()
    {
        for ( int i = 0; i < this .coordinates .length; i++ ) {
            if ( ! this .coordinates[ i ] .isZero() )
                return false;
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

    public AlgebraicVector inflateTo4d( boolean wFirst )
    {
        if ( this .coordinates .length == 4 ) {
            if ( wFirst )
                return this;
            else
                return new AlgebraicVector( this .coordinates[ 1 ], this .coordinates[ 2 ], this .coordinates[ 3 ], this .coordinates[ 0 ] );
        }
        if ( wFirst )
            return new AlgebraicVector( this .field .zero(), this .coordinates[ 0 ], this .coordinates[ 1 ], this .coordinates[ 2 ] );
        else // the older usage
            return new AlgebraicVector( this .coordinates[ 0 ], this .coordinates[ 1 ], this .coordinates[ 2 ], this .field .zero() );
    }

    public AlgebraicVector projectTo3d( boolean wFirst )
    {
        if ( wFirst )
            return new AlgebraicVector( this .coordinates[ 1 ], this .coordinates[ 2 ], this .coordinates[ 3 ] );
        else
            return new AlgebraicVector( this .coordinates[ 0 ], this .coordinates[ 1 ], this .coordinates[ 2 ] );
    }

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
}
