

package com.vzome.core.construction;

import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.Symmetry;

public class Scaling extends Transformation
{
    private Segment s1, s2;
    private Point center;
    private Symmetry symmetry;

    public Scaling( Segment s1, Segment s2, Point center, Symmetry symmetry )
    {
        super( s1 .field );
        mOffset = field .projectTo3d( center .getLocation(), true );
        this.s1 = s1;
        this.s2 = s2;    
        this.center = center;
        this.symmetry = symmetry;
        mapParamsToState();
    }

    public void attach()
    {
        s1 .addDerivative( this );
        s2 .addDerivative( this );
        center .addDerivative( this );
    }
    
    public void detach()
    {
        s1 .removeDerivative( this );
        s2 .removeDerivative( this );
        center .removeDerivative( this );
    }

    protected boolean mapParamsToState()
    {
        Axis zone1 = symmetry .getAxis( s1 .getOffset() );
        Axis zone2 = symmetry .getAxis( s2 .getOffset() );
        Direction orbit = zone1 .getDirection();
        if ( orbit != zone2 .getDirection() )
            return setStateVariables( null, null, true );
        
        AlgebraicNumber len1 = zone1 .getLength( s1 .getOffset() );
        AlgebraicNumber len2 = zone2 .getLength( s2 .getOffset() );
        AlgebraicNumber scale = len2 .dividedBy( len1 );
//        scale = field .multiply( scale, field .createPower( -5 ) );
        AlgebraicMatrix transform = new AlgebraicMatrix(
                field .basisVector( 3, AlgebraicVector.X ) .scale( scale ),
                field .basisVector( 3, AlgebraicVector.Y ) .scale( scale ),
                field .basisVector( 3, AlgebraicVector.Z ) .scale( scale ) );
        return setStateVariables( transform, center .getLocation(), false );
    }

    public void accept( Visitor v )
    {
//        v .visitTranslation( this );
    }
}
