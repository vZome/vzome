package com.vzome.fields.sqrtphi;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.BigRational;
import com.vzome.core.algebra.ParameterizedField;
import com.vzome.core.algebra.PentagonField;

/**
 * @author David Hall
 */
public class SqrtPhiField  extends ParameterizedField<Integer>
{
    public static final String FIELD_NAME = "sqrtPhi";
    
    /**
     * 
     * @return the coefficients of a SqrtPhiField. 
     * This can be used to determine when two fields have compatible coefficients 
     * without having to generate an instance of the class. 
     * Note that this method provides no validation of the parameter.
     */
    public static double[] getFieldCoefficients() {
        final double PHI_VALUE = PentagonField.PHI_VALUE; // ( 1.0 + Math.sqrt( 5.0 ) ) / 2.0;
        return new double[] {
                1.0d,
                Math.sqrt(PHI_VALUE),
                PHI_VALUE,
                PHI_VALUE * Math.sqrt(PHI_VALUE)
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
        buf .append( "phi = " ); // note that phi is not the first irrational in this field
        buf .append( PentagonField.PHI_VALUE );
    }

    @Override
    public double[] getCoefficients() {
        return getFieldCoefficients();
    }
    
    public SqrtPhiField() {
        super(FIELD_NAME, 4, 0);
    }

    @Override
    protected void validate() {}

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
    protected BigRational[] prepareAlgebraicNumberTerms(BigRational[] terms) {
        return (terms.length == 2)
            // remap [ units, phis ] to [ units, 0, phis, 0 ]
            ? new BigRational[] { terms[0], BigRational.ZERO, terms[1], BigRational.ZERO }
            : super.prepareAlgebraicNumberTerms(terms);
    }
    
    @Override
    public AlgebraicNumber getGoldenRatio()
    {
        return getUnitTerm(2);
    }
    
}
