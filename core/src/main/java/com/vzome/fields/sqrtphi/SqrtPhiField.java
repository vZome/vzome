package com.vzome.fields.sqrtphi;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
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
     * @param radicand
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
        buf .append( "phi = " );
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
    protected void initializeMultiplierMatrix() {
        short[][][] mm = {
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
        multiplierMatrix = mm;
    }

    @Override
    protected void initializeLabels() {
        irrationalLabels[1] = new String[]{"\u221A\u03C6", "sqrt(phi)"};
        irrationalLabels[2] = new String[]{"\u03C6", "phi"};
        irrationalLabels[3] = new String[]{"\u03C6\u221A\u03C6", "phi*sqrt(phi)"};
    }
    
    /**
     * If all elements of {@code nums} are sized exactly right for the golden field,
     * then remap [ units, phis ] to [ units, 0, phis, 0 ] for each coordinate.
     * Otherwise let the base class create the vector normally.
     */
    @Override
    public AlgebraicVector createVector( int[][] nums )
    {
        int dims = nums.length;
        int[][] remapped = new int[dims][];
        for(int dim = 0; dim < dims; dim++) {
            if ( nums[dim].length != 4 ) {  // nums[dim] is not correctly sized for the golden field 
                remapped = nums;            // so just use nums as given, without remapping anything
                break;
            }
            remapped[dim] = new int[8];
            remapped[dim][0] = nums[dim][0];    // units numerator
            remapped[dim][1] = nums[dim][1];    // units denominator
            remapped[dim][2] = 0;
            remapped[dim][3] = 1;
            remapped[dim][4] = nums[dim][2];    // phis  numerator
            remapped[dim][5] = nums[dim][3];    // phis  denominator
            remapped[dim][6] = 0;
            remapped[dim][7] = 1;
        }
        return super.createVector(remapped);
    }
    
    @Override
    public AlgebraicNumber getGoldenRatio()
    {
        return getUnitTerm(2);
    }
    
}
