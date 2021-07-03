
package com.vzome.core.math.symmetry;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;

/*
 ******* from private email from Brian Hall, Notre Dame, May 5, 2007


We start by generating points (x1,x2,x3,x4,x5) in the zero-sum subspace of R^5.
Then we identify that zero-sum subspace with R^4, giving a full 4-dimensional
A4 model in R^4. I have set things up so that the standard projection from R^4
to R^3 will then produce a model with A3 symmetry.

---

We make a collection of "fundamental roots" as:

r1 = (1,-1,0,0,0),
r2 = (0,1,-1,0,0),
r3 = (0,0,1,-1,0),
r4 = (0,0,0,1,-1).

The "fundamental weights" are now the following vectors:

v1 = ( 4/5,-1/5,-1/5,-1/5,-1/5)
v2 = ( 3/5, 3/5,-2/5,-2/5,-2/5)
v3 = ( 2/5, 2/5, 2/5,-3/5,-3/5)
v4 = ( 1/5, 1/5, 1/5, 1/5,-4/5)

Now to build a polyhedron, we take our starting point to be a vector of the form

v = a1(v1) + a2(v2) + a3(v3) + a4(v4),

where each a_i is a non-negative number. Then starting from v we make edges as
follows:

from v to v - a1(r1)
from v to v - a2(r2)
from v to v - a3(r3)
from v to v - a4(r4).

Now we simply take the vertex v and the 4 edges above and hit them with the A4
group, which is just the group of permutations of the 5 entries. This gives a
vertex-regular A4 polytope sitting in the zero-sum subspace of R^5.

If you take each a_i to be either 0 or 1, you will get the standard (uniform) A4
polytopes. If we start working on chiral models, we may try the a_i's as
(1,phi^2,phi^3,?) or (1,phi^3,phi^2,?) and then add some "alternation."

---

Now we identify the zero-sum subspace of R^5 with R^4. This step is NOT a
projection, just an isometric identification of a 4-dimensional subspace of R^5
with R^4. Consider the following roots in R^4:

t1 = ( 2,-2, 0, 0)
t2 = (-2, 0,-2, 0)
t3 = ( 2, 2, 0, 0)
t4 = (-1,-1, 1,sqrt(5))

Then we map from the zero-sum subspace of R^5 to R^4 like this:

(x1,x2,x3,x4,x5) --> x1(t1) + (x1+x2)(t2) + (x1+x2+x3)(t3) + (x1+x2+x3+x4)(t4)

At this point, we should have a full (unprojected) A4 polytope in R^4. Using the
standard projection to R^3 (dropping the last coordinate) should give the
standard green/yellow models. Projecting onto a hyperplane perpendicular to one
of the t_i's should give David's blue/red projection.

 * 
 * @author Scott Vorthmann
 *
 */
public class A4Group implements CoxeterGroup
{
    private final AlgebraicField field;
   
    private final AlgebraicVector [] ROOTS = new AlgebraicVector[4];

    private final AlgebraicVector[] WEIGHTS = new AlgebraicVector[4];

    private final AlgebraicVector[] ROOTS_R4 = new AlgebraicVector[4];

    private static final int[][] S5_PERMS = new int[][]{

        {0,1,2,3,4}, {1,2,3,4,0}, {2,3,4,0,1}, {3,4,0,1,2}, {4,0,1,2,3},   // 5-cycle is even

        {1,2,0,3,4}, {2,0,3,4,1}, {0,3,4,1,2}, {3,4,1,2,0}, {4,1,2,0,3},   // cycle 0,1,2

        {0,2,3,1,4}, {2,3,1,4,0}, {3,1,4,0,2}, {1,4,0,2,3}, {4,0,2,3,1},   // cycle 1,2,3
        
        {0,1,3,4,2}, {1,3,4,2,0}, {3,4,2,0,1}, {4,2,0,1,3}, {2,0,1,3,4},   // cycle 2,3,4
        
        {3,1,2,4,0}, {1,2,4,0,3}, {2,4,0,3,1}, {4,0,3,1,2}, {0,3,1,2,4},   // cycle 3,4,0
        
        {1,4,2,3,0}, {4,2,3,0,1}, {2,3,0,1,4}, {3,0,1,4,2}, {0,1,4,2,3},   // cycle 4,0,1

        {1,3,2,0,4}, {3,2,0,4,1}, {2,0,4,1,3}, {0,4,1,3,2}, {4,1,3,2,0},   // cycle 0,1,3
        
        {0,2,4,3,1}, {2,4,3,1,0}, {4,3,1,0,2}, {3,1,0,2,4}, {1,0,2,4,3},   // cycle 1,2,4
        
        {2,1,3,0,4}, {1,3,0,4,2}, {3,0,4,2,1}, {0,4,2,1,3}, {4,2,1,3,0},   // cycle 2,3,0
        
        {0,3,2,4,1}, {3,2,4,1,0}, {2,4,1,0,3}, {4,1,0,3,2}, {1,0,3,2,4},   // cycle 3,4,1
        
        {2,1,4,3,0}, {1,4,3,0,2}, {4,3,0,2,1}, {3,0,2,1,4}, {0,2,1,4,3},   // cycle 4,0,2
        
        {4,3,2,1,0}, {3,2,1,0,4}, {2,1,0,4,3}, {1,0,4,3,2}, {0,4,3,2,1},   // cycle 0,4 and 1,3

        // the rest are odd permutations
        
        {0,1,3,2,4}, {0,2,1,3,4}, {0,3,2,1,4}, {1,0,2,3,4}, {1,2,3,0,4},

        {1,3,0,2,4}, {2,1,0,3,4}, {2,0,3,1,4}, {2,3,1,0,4}, {3,1,2,0,4},

        {3,2,0,1,4}, {3,0,1,2,4}, {0,1,2,4,3}, {0,2,3,4,1}, {0,3,1,4,2},

        {1,0,3,4,2}, {1,2,0,4,3}, {1,3,2,4,0}, {2,1,3,4,0}, {2,0,1,4,3},

        {2,3,0,4,1}, {3,1,0,4,2}, {3,2,1,4,0}, {3,0,2,4,1}, {0,2,4,1,3},

        {0,3,4,2,1}, {1,0,4,2,3}, {1,2,4,3,0}, {1,3,4,0,2}, {2,1,4,0,3},

        {2,0,4,3,1}, {2,3,4,1,0}, {3,1,4,2,0}, {3,2,4,0,1}, {3,0,4,1,2},

        {0,4,1,2,3}, {0,4,2,3,1}, {0,4,3,1,2}, {1,4,0,3,2}, {1,4,2,0,3},

        {1,4,3,2,0}, {2,4,1,3,0}, {2,4,0,1,3}, {2,4,3,0,1}, {3,4,1,0,2},

        {3,4,2,1,0}, {3,4,0,2,1}, {4,0,1,3,2}, {4,0,2,1,3}, {4,0,3,2,1},

        {4,1,0,2,3}, {4,1,2,3,0}, {4,1,3,0,2}, {4,2,1,0,3}, {4,2,0,3,1},

        {4,2,3,1,0}, {4,3,1,2,0}, {4,3,2,0,1}, {0,1,4,3,2}, {4,3,0,1,2}
    };
    
    private final AlgebraicNumber ONE_FIFTH , TWO_FIFTHS , THREE_FIFTHS, FOUR_FIFTHS;

    public A4Group( AlgebraicField field )
    {
        this.field = field;
        
        ONE_FIFTH    = field .createRational( 1, 5 );
        TWO_FIFTHS   = field .createRational( 2, 5 );
        THREE_FIFTHS = field .createRational( 3, 5 );
        FOUR_FIFTHS  = field .createRational( 4, 5 );

        AlgebraicNumber neg_one = field .createRational( -1 );

        ROOTS[ 0 ] = field .basisVector( 5, 0 ); // ( 1, -1, 0, 0, 0 );
        ROOTS[ 0 ] .setComponent( 1, neg_one );

        ROOTS[ 1 ] = field .basisVector( 5, 1 ); // ( 0, 1, -1, 0, 0 );
        ROOTS[ 1 ] .setComponent( 2, neg_one );

        ROOTS[ 2 ] = field .basisVector( 5, 2 ); // ( 0, 0, 1, -1, 0 );
        ROOTS[ 2 ] .setComponent( 3, neg_one );

        ROOTS[ 3 ] = field .basisVector( 5, 3 ); // ( 0, 0, 0, 1, -1 );
        ROOTS[ 3 ] .setComponent( 4, neg_one );

        WEIGHTS[ 0 ] = field .basisVector( 5, 0 );
        WEIGHTS[ 0 ] .setComponent( 0, FOUR_FIFTHS );
        WEIGHTS[ 0 ] .setComponent( 1, ONE_FIFTH .negate() );
        WEIGHTS[ 0 ] .setComponent( 2, ONE_FIFTH .negate() );
        WEIGHTS[ 0 ] .setComponent( 3, ONE_FIFTH .negate() );
        WEIGHTS[ 0 ] .setComponent( 4, ONE_FIFTH .negate() );

        WEIGHTS[ 1 ] = field .basisVector( 5, 0 );
        WEIGHTS[ 1 ] .setComponent( 0, THREE_FIFTHS );
        WEIGHTS[ 1 ] .setComponent( 1, THREE_FIFTHS );
        WEIGHTS[ 1 ] .setComponent( 2, TWO_FIFTHS .negate() );
        WEIGHTS[ 1 ] .setComponent( 3, TWO_FIFTHS .negate() );
        WEIGHTS[ 1 ] .setComponent( 4, TWO_FIFTHS .negate() );

        WEIGHTS[ 2 ] = field .basisVector( 5, 0 );
        WEIGHTS[ 2 ] .setComponent( 0, TWO_FIFTHS );
        WEIGHTS[ 2 ] .setComponent( 1, TWO_FIFTHS );
        WEIGHTS[ 2 ] .setComponent( 2, TWO_FIFTHS );
        WEIGHTS[ 2 ] .setComponent( 3, THREE_FIFTHS .negate() );
        WEIGHTS[ 2 ] .setComponent( 4, THREE_FIFTHS .negate() );

        WEIGHTS[ 3 ] = field .basisVector( 5, 0 );
        WEIGHTS[ 3 ] .setComponent( 0, ONE_FIFTH );
        WEIGHTS[ 3 ] .setComponent( 1, ONE_FIFTH );
        WEIGHTS[ 3 ] .setComponent( 2, ONE_FIFTH );
        WEIGHTS[ 3 ] .setComponent( 3, ONE_FIFTH );
        WEIGHTS[ 3 ] .setComponent( 4, FOUR_FIFTHS .negate() );

        AlgebraicNumber two = field .createRational( 2 );
        AlgebraicNumber two_neg = field .createRational( -2 );
        
        ROOTS_R4[ 0 ] = field .basisVector( 4, 1 );
        ROOTS_R4[ 0 ] .setComponent( 1, two );
        ROOTS_R4[ 0 ] .setComponent( 2, two_neg );
        
        ROOTS_R4[ 1 ] = field .basisVector( 4, 1 );
        ROOTS_R4[ 1 ] .setComponent( 3, two_neg );
        ROOTS_R4[ 1 ] .setComponent( 1, two_neg );
        
        ROOTS_R4[ 2 ] = field .basisVector( 4, 1 );
        ROOTS_R4[ 2 ] .setComponent( 1, two );
        ROOTS_R4[ 2 ] .setComponent( 2, two );
        
        ROOTS_R4[ 3 ] = field .basisVector( 4, 3 );
        AlgebraicNumber root5 = field .createAlgebraicNumber( -1, 2, 1, 0 );
        ROOTS_R4[ 3 ] .setComponent( 1, neg_one );
        ROOTS_R4[ 3 ] .setComponent( 2, neg_one );
        ROOTS_R4[ 3 ] .setComponent( 0, root5 );
        
//        for ( int i = 0; i < ROOTS_R4.length; i++ ) {
//            System .out .println( RationalNumbers .toString( ROOTS_R4[ i ] ) );
//        }
    }
    

    @Override
    public int getOrder()
    {
        return S5_PERMS.length;
    }

    @Override
    public AlgebraicVector groupAction( AlgebraicVector model, int element )
    {
        AlgebraicVector result = field .origin( 4 );
        AlgebraicNumber sum = field .createRational( 0 );
        for ( int c = 0; c < 4; c++ ) {
            AlgebraicNumber source = model .getComponent( S5_PERMS[ element ][ c ] );
            sum = sum .plus( source );
            AlgebraicVector scaled = ROOTS_R4[ c ] .scale( sum );
            result = result .plus( scaled );
        }
        return result .scale( field .createPower( -1 ) );
    }

    @Override
    public AlgebraicVector getOrigin()
    {
        return field .origin( 5 );
    }

    @Override
    public AlgebraicVector getWeight( int i )
    {
        return WEIGHTS[ i ];
    }

    @Override
    public AlgebraicVector getSimpleRoot( int i )
    {
        return ROOTS[ i ];
    }

    @Override
    public AlgebraicField getField()
    {
        return field;
    }

    @Override
    public AlgebraicVector chiralSubgroupAction( AlgebraicVector model, int element )
    {
        if ( element >= 60 )
            return null;
        AlgebraicVector result = field .origin( 4 );
        AlgebraicNumber sum = field .createRational( 0 );
        for ( int c = 0; c < 4; c++ ) {
            AlgebraicNumber source = model .getComponent( S5_PERMS[ element ][ c ] );
            sum = sum .plus( source );
            AlgebraicVector scaled = ROOTS_R4[ c ] .scale( sum );
            result = result .plus( scaled );
        }
        return result .scale( field .createPower( -1 ) );
    }
}
