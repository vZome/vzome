

package com.vzome.core.math.symmetry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.RealVector;

/**
 * @author Scott Vorthmann
 */
public class Direction implements Comparable
{
    private String mName;
    
    private final Axis[][] mAxes;
    
    private final Map mVectors = new HashMap();
    
    private final Symmetry mSymmetryGroup;
    
    private final int[] mPrototype;
    
    private boolean mStandard, mAutomatic, hasHalfSizes;
    
    private final String[] scaleNames = new String[]{ "shorter", "short", "medium", "long" };
    
    private int[] unitLength;
    
    public void setAutomatic( boolean auto )
    {
        mAutomatic = auto;
    }
    
    public boolean isAutomatic()
    {
        return mAutomatic;
    }
    
    public boolean isStandard()
    {
        return mStandard;
    }
    
    private static int globalIndex = 0;
    
    private final int index;
    
    public Direction( String name, Symmetry group, int prototype, int rotatedPrototype, int[] vector, boolean isStd )
    {
        this.index = globalIndex++; // we want to just retain the order used to create these
        mStandard = isStd;
        mName = name;
        mSymmetryGroup = group;
        mPrototype = vector;
        mAxes = new Axis[ 2 ][ group .getChiralOrder() ];
        AlgebraicField field = group .getField();
        for ( int i = 0; i < mAxes[ Symmetry.PLUS ] .length; i++ ) {
            int[][] transform = group .getMatrix( i );
            if ( transform == null )
                return; // only happens for the "base" direction, like "blue" for icosa
            Permutation perm = group .getPermutation( i );
            int j = perm .mapIndex( prototype );
            int rotated = perm .mapIndex( rotatedPrototype );
            int[] normal = field .transform( transform, vector );

//            if ( ! Arrays.equals( field .dot( normal, normal ), field .dot( vector, vector ) ) )
//                throw new IllegalStateException( "rotated normal has bad length" );

            int rot = group .getMapping( j, rotated );
            createAxis( j, rot, normal );
        }
    }
    
    public String toString()
    {
        return mSymmetryGroup .getName() + " " + this .mName;
    }

    /**
     * Create a new automatic direction.
     * @param name
     * @param group
     * @param orientation
     * @param rotation
     * @param vector
     */
    public Direction( String name, Symmetry group, int orientation, int rotation, int[] vector )
    {
        this( name, group, orientation, rotation, vector, false );
        this .setAutomatic( true );
    }
    
    public int[] getPrototype()
    {
        return mPrototype;
    }
    
    public Iterator getAxes()
    {
        return mVectors .values() .iterator();
    }
    
    
    public Symmetry getSymmetry()
    {
        return mSymmetryGroup;
    }
    
    
    /**
     * @return
     */
    public String getName()
    {
        return mName;
    }
    
    
    /**
     * @param vector
     * @return
     */
    public Axis getAxis( int[] vector )
    {
        AlgebraicField field = mSymmetryGroup .getField();
        for ( Iterator lines = mVectors .values() .iterator(); lines .hasNext(); ) {
            Axis axis = (Axis) lines .next();
            if ( axis .isParallel( vector ) ) {
                int[] dotProd = axis .dotNormal( vector );
                if ( field .evaluateNumber( dotProd ) > 0 )
                    return axis;
                else {
                	int opp = ( axis .getSense() + 1 ) % 2;
                	return getAxis( opp, axis .getOrientation() );
                }
            }
        }
        return null;
    }
    
    public Axis getAxis( RealVector vector )
    {
        if ( RealVector .ORIGIN .equals( vector ) ) {
            return null;
        }
        // largest cosine means smallest angle
        //  and cosine is (a . b ) / |a| |b|
        double maxCosine = - 1d;
        AlgebraicField field = mSymmetryGroup .getField();
        Axis closest = null;
        for ( Iterator axes = this .getAxes(); axes .hasNext(); ) {
            Axis axis = (Axis) axes .next();
            RealVector axisV = field .getRealVector( axis .normal() );
            double cosine = vector .dot( axisV ) / (vector .length() * axisV .length());
            if ( cosine > maxCosine ) {
                maxCosine = cosine;
                closest = axis;
            }
        }
        return closest;
    }


    
    public Axis getAxis( int sense, int index )
    {
        return mAxes[ sense ][ index ];
    }

    /**
     * @param orientation the index of this axis (zone) in its Direction (orbit)
     * @param rotation the index of the permutation that is a rotation around this axis, or NO_ROTATION
     */
    public void createAxis( int orientation, int rotation, int[] norm )
    {
        AlgebraicVector key = new AlgebraicVector( norm );
        Axis axis = (Axis) mVectors .get( key );
        Permutation perm = mSymmetryGroup .getPermutation( rotation );
        if ( axis == null ) {
            axis = new Axis( this, orientation, Symmetry.PLUS, rotation, perm, norm );
            mVectors .put( key, axis );
        } else if ( axis .getSense() == Symmetry .MINUS )
            axis .rename( Symmetry.PLUS, orientation );
        mAxes[ Symmetry.PLUS ][ orientation ] = axis;
        norm = mSymmetryGroup .getField() .negate( norm );
        if ( perm != null )
            perm = perm .inverse();
        key = new AlgebraicVector( norm );
        axis = (Axis) mVectors .get( key );
        if ( axis == null ) {
            axis = new Axis( this, orientation, Symmetry.MINUS, rotation, perm, norm );
            mVectors .put( key, axis );
        }
        mAxes[ Symmetry.MINUS ][ orientation ] = axis;
    }

    public int compareTo( Object arg0 )
    {
        return this.index - ((Direction) arg0).index;
    }
    
    public void setHalfSizes( boolean value )
    {
        hasHalfSizes = value;
    }

    public boolean hasHalfSizes()
    {
        return hasHalfSizes;
    }
    
    public void setScaleNames( String[] names )
    {
        for (int i = 0; i < names.length; i++)
        {
            scaleNames[ i ] = names[ i ];
        }
    }
    
    public String getScaleName( int scale )
    {
        if ( scale <= 3 && scale >= 0 )
            return scaleNames[ scale ];
        else
            return "scale " + (scale-1);
    }
    
    public void setUnitLength( int[] unitLength )
    {
        this .unitLength = unitLength;
    }

    public int[] getUnitLength()
    {
        if ( unitLength == null)
            return mSymmetryGroup .getField() .createPower( 0 );
        else
            return unitLength;
    }
    
    public static final int USER_SCALE = 3;
    
    public final int[][] scales = new int[4][];

    public int[] getLengthInUnits( int[] rawLength )
    {
        // reproduce the calculation in LengthModel .setActualLength()
        // TODO rationalize that
        AlgebraicField field = mSymmetryGroup .getField();
        int[] scaledLength = field .multiply( rawLength, field .createPower( - USER_SCALE ) );
        if ( unitLength == null)
            return scaledLength;
        else
            return field .divide( scaledLength, unitLength );
    }

    public void getLengthExpression( StringBuffer buf, int[] length )
    {
        AlgebraicField field = mSymmetryGroup .getField();
        if ( scales[ 0 ] == null )
        {
            for ( int i = 0; i < scales .length; i++ )
                scales[ i ] = field .createPower( i - 1 );
        }
        for ( int i = 0; i < scales .length; i++ )
        {
            if ( Arrays .equals( scales[ i ], length ) )
            {
                buf .append( scaleNames[ i ] );
                buf .append( ": " );
                return;
            }
        }
        buf .append( scaleNames[ 1 ] );
        buf .append( ":" );
        field .getNumberExpression( buf, length, 0, AlgebraicField .EXPRESSION_FORMAT );
    }

    // no provision here yet for misalignment between vZome and rZome unit lengths
    public String getRZomeLength( int[] length )
    {
        AlgebraicField field = mSymmetryGroup .getField();
        if ( scales[ 0 ] == null )
        {
            for ( int i = 0; i < scales .length; i++ )
                scales[ i ] = field .createPower( i - 1 );
        }
        for ( int i = 0; i < scales .length; i++ )
        {
            if ( Arrays .equals( scales[ i ], length ) )
                return Integer .toString( i );
        }
        return null;
    }
}
