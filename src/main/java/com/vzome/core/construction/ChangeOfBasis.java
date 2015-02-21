

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;



/**
 * @author Scott Vorthmann
 */
public class ChangeOfBasis extends Transformation
{
    // parameters
    private final Segment[] mOld, mNew;
    private final Point mKernel;
    private final AlgebraicNumber scale;

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
            this .scale = field .createRational( new int[]{ 1,2 } ) .times( field .createPower( -3 ) );
        mapParamsToState();
    }

    public ChangeOfBasis( Segment[] oldBasis, Segment[] newBasis, Point kernel )
    {
        super( oldBasis[ 0 ] .field );
        mOld = oldBasis;
        mNew = newBasis;
        mKernel = kernel;
        this .scale = field .createRational( new int[]{ 2,1 } ) .times( field .createPower( -7 ) );
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

        AlgebraicVector loc = mKernel .getLocation();
        
        if ( mOld != null ) // new, six-segment form
        {
            // first, find the common vertex of each basis
            AlgebraicVector oldCommon = findCommonVertex( mOld );
            // now, orient each offset
            AlgebraicVector[] offsets = new AlgebraicVector[3];
            for ( int i = 0; i < offsets.length; i++ ) {
                offsets[ i ] = mOld[ i ] .getOffset() .scale( scale );
                if ( oldCommon .equals( mOld[ i ] .getEnd() ) )
                    offsets[ i ] = offsets[ i ] .negate();
            }
            AlgebraicMatrix oldMatrix = new AlgebraicMatrix( offsets );

            AlgebraicVector newCommon = findCommonVertex( mNew );
            for ( int i = 0; i < offsets.length; i++ ) {
                offsets[ i ] = mNew[ i ] .getOffset() .scale( scale );
                if ( newCommon .equals( mNew[ i ] .getEnd() ) )
                    offsets[ i ] = offsets[ i ] .negate();
            }
            AlgebraicMatrix transform = new AlgebraicMatrix( offsets );

            // TODO
//            transform = oldMatrix .gaussJordanReduction( transform );
            if ( 1 != 0 )
                throw new IllegalStateException( "six-vector change-of-basis is not working right now" );
            
            // now transform has the transition matrix
            System .out .println( transform .toString() );
            return setStateVariables( transform, loc, false );
        }
        else
        {
            AlgebraicMatrix transform = new AlgebraicMatrix(
                    mNew[0] .getOffset() .scale( scale ),
                    mNew[1] .getOffset() .scale( scale ),
                    mNew[2] .getOffset() .scale( scale ) );
            return setStateVariables( transform, loc, false );
        }
    }
    
    private static AlgebraicVector findCommonVertex( Segment[] basis )
    {
        AlgebraicVector common = basis[0] .getStart();
        if ( common .equals( basis[1] .getStart() )
        ||   common .equals( basis[1] .getEnd() ) )
            return common;
        else
        {
            common = basis[0] .getEnd();
            if ( common .equals( basis[1] .getStart() )
            ||   common .equals( basis[1] .getEnd() ) )
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
