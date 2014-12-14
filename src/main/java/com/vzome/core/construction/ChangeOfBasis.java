

package com.vzome.core.construction;

import java.util.Arrays;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.RationalMatrices;



/**
 * @author Scott Vorthmann
 */
public class ChangeOfBasis extends Transformation
{
    // parameters
    private final Segment[] mOld, mNew;
    private final Point mKernel;
    private final int[] scale;

    /**
     * This assumes that the starting basis is the usual X,Y,Z basis
     * @param originalScaling 
     * @param prototype
     */
    public ChangeOfBasis( Segment newX, Segment newY, Segment newZ, Point kernel, boolean originalScaling )
    {
        super( newX .field );
        mNew = new Segment[]{ newX, newY, newZ };
        mOld = null;
        mKernel = kernel;
        if ( originalScaling )
            this .scale = field .createPower( -5 );
        else
            this .scale = field .multiply( field .createRational( new int[]{ 1,2 } ), field .createPower( -3 ) );
        mapParamsToState();
    }

    public ChangeOfBasis( Segment[] oldBasis, Segment[] newBasis, Point kernel )
    {
        super( oldBasis[ 0 ] .field );
        mOld = oldBasis;
        mNew = newBasis;
        mKernel = kernel;
        this .scale = field .multiply( field .createRational( new int[]{ 2,1 } ), field .createPower( -7 ) );
        mapParamsToState();
    }

    public void attach()
    {
        mNew[0] .addDerivative( this );
        mNew[1] .addDerivative( this );
        mNew[2] .addDerivative( this );
        if ( mOld != null )
        {
            mOld[0] .addDerivative( this );
            mOld[1] .addDerivative( this );
            mOld[2] .addDerivative( this );
        }
        mKernel .addDerivative( this );
    }
    
    public void detach()
    {
        mNew[0] .removeDerivative( this );
        mNew[1] .removeDerivative( this );
        mNew[2] .removeDerivative( this );
        if ( mOld != null )
        {
            mOld[0] .removeDerivative( this );
            mOld[1] .removeDerivative( this );
            mOld[2] .removeDerivative( this );
        }
        mKernel .addDerivative( this );
    }
    
    protected boolean mapParamsToState()
    {
        if ( 3d == Math.PI /* TODO test orthogonality */ )
            setStateVariables( null, null, true );

        int[] /*AlgebraicVector*/ loc = mKernel .getLocation();
        
        if ( mOld != null ) // new, six-segment form
        {
            // first, find the common vertex of each basis
            int[] oldCommon = findCommonVertex( mOld );
            // now, orient each offset
            int[][] offsets = new int[3][];
            for ( int i = 0; i < offsets.length; i++ ) {
                offsets[ i ] = field .scaleVector( mOld[ i ] .getOffset(), scale );
                if ( Arrays .equals( oldCommon, mOld[ i ] .getEnd() ) )
                    offsets[ i ] = field .negate( offsets[ i ] );
            }
            int[][] oldMatrix = field .createMatrix( offsets );

            int[] newCommon = findCommonVertex( mNew );
            for ( int i = 0; i < offsets.length; i++ ) {
                offsets[ i ] = field .scaleVector( mNew[ i ] .getOffset(), scale );
                if ( Arrays .equals( newCommon, mNew[ i ] .getEnd() ) )
                    offsets[ i ] = field .negate( offsets[ i ] );
            }
            int[][] transform = field .createMatrix( offsets );

            RationalMatrices .gaussJordanReduction( oldMatrix, transform );
            // now transform has the transition matrix, and oldMatrix is the identity
            for ( int i = 0; i < oldMatrix.length; i++ ) {
                StringBuffer buf = new StringBuffer();
                field .getVectorExpression( buf, oldMatrix[ i ], AlgebraicField.DEFAULT_FORMAT );
                System .out .println( buf );
            }
            for ( int i = 0; i < transform.length; i++ ) {
                StringBuffer buf = new StringBuffer();
                field .getVectorExpression( buf, transform[ i ], AlgebraicField.DEFAULT_FORMAT );
                System .out .println( buf );
            }
            return setStateVariables( transform, loc, false );
        }
        else
        {
            int[][] transform = field .createMatrix( new int[][]{
                    field .scaleVector( mNew[0] .getOffset(), scale ),
                    field .scaleVector( mNew[1] .getOffset(), scale ),
                    field .scaleVector( mNew[2] .getOffset(), scale ) } );
            return setStateVariables( transform, loc, false );
        }
    }
    
    private static int[] findCommonVertex( Segment[] basis )
    {
        int[] common = basis[0] .getStart();
        if ( Arrays.equals( common, basis[1] .getStart() )
        ||   Arrays.equals( common, basis[1] .getEnd() ) )
            return common;
        else
        {
            common = basis[0] .getEnd();
            if ( Arrays.equals( common, basis[1] .getStart() )
            ||   Arrays.equals( common, basis[1] .getEnd() ) )
                return common;
            else
                return null;
        }
    }
    

    public void accept( Visitor v )
    {
        v .visitChangeOfBasis( this );
    }

}
