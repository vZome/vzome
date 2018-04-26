package com.vzome.core.algebra;

/**
 * @author David Hall
 */
public class SqrtPhiField  extends ParameterizedField<Integer> {
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
        // Note that these coefficients are not in increasing order
        // but rather, the first two are in the same position as the golden subfield
        // then the remaining coefficients are in increasing order.
        // This is not necessary mathematically, but it means that AlgebraicFields.haveSameInitialCoefficients("golden") will return true. 
        // In addition, since createPower works on the first irrational, we want that to be phi whenever applicable.
        final double PHI_VALUE = PentagonField.PHI_VALUE; // ( 1.0 + Math.sqrt( 5.0 ) ) / 2.0;
        return new double[] {
                1.0d,
                            Math.sqrt(PHI_VALUE),
                PHI_VALUE,
                PHI_VALUE * Math.sqrt(PHI_VALUE)
        };
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
     * A modified version of the base class that maps [ a, b ] to [ a, 0, b, 0 ] for each coordinate.
     */
    @Override
    public AlgebraicVector createVector( int[][] nums )
    {
        int dims = nums.length;
        AlgebraicNumber[] coords = new AlgebraicNumber[ dims ];
        for(int c = 0; c < coords.length; c++) {
            int coordLength = nums[c].length;
            if ( coordLength % 2 != 0 ) {
                throw new IllegalStateException( "Vector dimension " + c + " has " + coordLength + " components. An even number is required." );
            }
            int nFactors = coordLength / 2;
            if ( nFactors > getOrder() ) {
                throw new IllegalStateException( "Vector dimension " + c + " has " + (coordLength /2) + " terms." 
                        + " Each dimension of the " + this.getName() + " field is limited to " + getOrder() + " terms."
                        + " Each term consists of a numerator and a denominator." );
            }
            BigRational[] factors = new BigRational[nFactors*2];
            for (int f = 0; f < nFactors; f++) {
                int numerator   = nums[c][(f*2)  ];
                int denominator = nums[c][(f*2)+1];
                factors[f*2] = new BigRational(numerator, denominator);
                factors[f*2+1] = new BigRational(0,1);
            }
            coords[c] = new AlgebraicNumber(this, factors);
        }
        return new AlgebraicVector( coords );
    }
}
