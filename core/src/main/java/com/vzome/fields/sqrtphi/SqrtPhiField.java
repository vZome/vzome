package com.vzome.fields.sqrtphi;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicNumberFactory;
import com.vzome.core.algebra.ParameterizedField;

/**
 * @author David Hall
 */
public class SqrtPhiField  extends ParameterizedField
{
    public static final String FIELD_NAME = "sqrtPhi";
    public static final double PHI_VALUE = ( 1.0 + Math.sqrt( 5.0 ) ) / 2.0;
    public static final double SQRT_PHI_VALUE = Math.sqrt( PHI_VALUE );

    /**
     * 
     * @return the coefficients of a SqrtPhiField. 
     * This can be used to determine when two fields have compatible coefficients 
     * without having to generate an instance of the class. 
     * Note that this method provides no validation of the parameter.
     */
    public static double[] getFieldCoefficients() {
        return new double[] {
                1.0d,
                SQRT_PHI_VALUE,
                PHI_VALUE,
                PHI_VALUE * SQRT_PHI_VALUE
        };
    }
    
    @Override
    public int getNumMultipliers()
    {
        return 1; // phi can be achieved by squaring sqrt(phi)
    }

    @Override
    public void defineMultiplier( StringBuffer buf, int w )
    {
        buf .append( "sqrtphi = " ); // note that phi is not the first irrational in this field
        buf .append( SQRT_PHI_VALUE );
    }

    @Override
    public double[] getCoefficients() {
        return getFieldCoefficients();
    }
    
    public SqrtPhiField( AlgebraicNumberFactory factory ) {
        super( FIELD_NAME, 4, factory );
        initialize();
    }

    @Override
    protected void initializeCoefficients() {
        double[] temp = getCoefficients();
        int i = 0;
        for(double coefficient : temp) {
            coefficients[i++] = coefficient;
        }
    }

    /*

    Multiplication table:
    p = phi
    r = sqrt(phi)
    t = phi*sqrt(phi)

      *  |   1  | r   | p   | t
    -----+------+-----+-----+-----
      1  |   1  | r   | p   | t
      r  |   r  | p   | t   | 1+p
      p  |   p  | t   | 1+p | r+t
      t  |   t  | 1+p | r+t | 1+2p

     */
    @Override
    protected void initializeMultiplicationTensor() {
        short[][][] tensor = {
            { // 1
              { 1, 0, 0, 0, },
              { 0, 0, 0, 1, },
              { 0, 0, 1, 0, },
              { 0, 1, 0, 1, },
            },
            { // r = sqrt(phi)
              { 0, 1, 0, 0, },
              { 1, 0, 0, 0, },
              { 0, 0, 0, 1, },
              { 0, 0, 1, 0, },
            },
            { // p = phi
              { 0, 0, 1, 0, },
              { 0, 1, 0, 1, },
              { 1, 0, 1, 0, },
              { 0, 1, 0, 2, },
            },
            { // t = phi*sqrt(phi)
              { 0, 0, 0, 1, },
              { 0, 0, 1, 0, },
              { 0, 1, 0, 1, },
              { 1, 0, 1, 0, },
            },
        };
        multiplicationTensor = tensor;
    }

    @Override
    protected void initializeLabels() {
        irrationalLabels[1] = new String[]{"\u221A\u03C6", "sqrt(phi)"};
        irrationalLabels[2] = new String[]{"\u03C6", "phi"};
        irrationalLabels[3] = new String[]{"\u03C6\u221A\u03C6", "phi*sqrt(phi)"};
    }
    
    @Override
    protected int[] convertGoldenNumberPairs( int[] pairs )
    {
        // remap [ units, phis ] to [ units, 0, phis, 0 ]
        return ( pairs.length == 4 )
            ? new int[] { pairs[0], pairs[1], 0, 1, pairs[2], pairs[3], 0, 1 }
            : super.convertGoldenNumberPairs( pairs );
    }
    
    @Override
    public AlgebraicNumber getGoldenRatio()
    {
        return getUnitTerm(2);
    }
    
}
