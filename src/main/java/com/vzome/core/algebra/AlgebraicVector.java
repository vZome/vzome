package com.vzome.core.algebra;

import java.util.Arrays;

import com.vzome.core.math.RealVector;


/**
 * A wrapper object supporting use as a hash key.
 * @author vorth
 *
 */
public final class AlgebraicVector
{
    private final int[] array;
    
    public AlgebraicVector( int[] array )
    {
        this.array = array;
    }

    public final boolean equals( Object other )
    {
        if ( other == null )
            return false;
        if ( other == this )
            return true;
        if ( other instanceof AlgebraicVector )
        {
            AlgebraicVector that = (AlgebraicVector) other;
            return Arrays .equals( this.array, that.array );
        } else
            return false;
    }

    public final int hashCode()
    {
        return RationalVectors .hashCode( array );
    }
    
    public final RealVector toRealVector( AlgebraicField field )
    {
        return field .getRealVector( this .array );
    }
    
    public final String toString( AlgebraicField field )
    {
        StringBuffer buf = new StringBuffer();
        field .getNumberExpression( buf, array, 0, AlgebraicField.EXPRESSION_FORMAT );
        return buf .toString();
    }
}
