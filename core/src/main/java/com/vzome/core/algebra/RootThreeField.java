
package com.vzome.core.algebra;

public class RootThreeField extends AbstractAlgebraicField
{
    public static final String FIELD_NAME = "rootThree";
    
    /**
     * 
     * @return the coefficients of this AlgebraicField class. 
     * This can be used to determine when two fields have compatible coefficients 
     * without having to generate an instance of the class. 
     */
    public static double[] getFieldCoefficients() {
        return new double[] { 1.0d, ROOT_3 };
    }
    
    @Override
    public double[] getCoefficients() {
        return getFieldCoefficients();
    }

    public static final double ROOT_3 = Math.sqrt( 3d );
    
    private static final BigRational THREE = new BigRationalImpl( 3 );
    
    public RootThreeField()
    {
        super( FIELD_NAME, 2, AlgebraicNumberImpl.FACTORY );
    };

    @Override
    public final BigRational[] multiply( BigRational[] first, BigRational[]  second )
    {
        BigRational sqrt3s = first[ SQRT3_PLACE ].times( second[ ONES_PLACE ]) .plus( first[ ONES_PLACE ].times( second[ SQRT3_PLACE ]) );
        BigRational ones = first[ ONES_PLACE ].times( second[ ONES_PLACE ] ) .plus( first[ SQRT3_PLACE ].times( second[ SQRT3_PLACE ] ) .times( THREE ) );
        
        return new BigRational[]{ ones, sqrt3s };
    }

    private static final int ONES_PLACE = 0, SQRT3_PLACE = 1;

    @Override
    double evaluateNumber( BigRational[] factors )
    {
        return factors[ ONES_PLACE ] .evaluate() + ROOT_3 * factors[ SQRT3_PLACE ] .evaluate();
    }

    @Override
    BigRational[] scaleBy( BigRational[] factors, int whichIrrational )
    {
        switch (whichIrrational) {
            case 0:
                return factors;
            case 1:
                return new BigRational[]{ factors[ 1 ] .times( THREE ), factors[ 0 ] };
            default:
                throw new IllegalArgumentException(whichIrrational + " is not a valid irrational in this field");
        }
    }

    private static final String[][] IRRATIONAL_LABELS = new String[][] {
        {" ", " "},
        {"\u221A3", "sqrt(3)"}
    };
    
    @Override
    public String getIrrational( int i, int format )
    {
        return IRRATIONAL_LABELS[i][format];
    }
}
