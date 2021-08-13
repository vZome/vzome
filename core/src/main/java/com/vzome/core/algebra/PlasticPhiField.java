package com.vzome.core.algebra;

public class PlasticPhiField extends ParameterizedField
{
    public static final String FIELD_NAME = "plasticPhi";
    
    /**
     * @return the coefficients of a PlasticPhiField. 
     * This can be used to determine when two fields have compatible coefficients 
     * without having to generate an instance of the class. 
     */
    public static double[] getFieldCoefficients() {
        final double plasticNumber = 1.32471795724475d; // n + 1 = n^3
        final double phi = (1.0 + Math.sqrt(5)) / 2.0;
        return new double[] {
                1.0d,
                phi,
                plasticNumber,
                plasticNumber * phi,
                plasticNumber * plasticNumber,
                plasticNumber * plasticNumber * phi
        };
    }
    
    @Override
    public int getNumMultipliers()
    {
        return 2; // bivariate polynomial (plasticNumber and phi)
    }
    
    @Override
    public double[] getCoefficients() {
        return getFieldCoefficients();
    }
    
    public PlasticPhiField( AlgebraicNumberFactory factory ) {
        super( FIELD_NAME, 6, factory );
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
    p = plasticNumber
    φ = phi
    
      *  |  1  |  p  |  p^2
    -----+-----+-----+-------
      1  |  1  |  p  |  p^2
      p  |  p  | p^2 |  1+p
     p^2 | p^2 | 1+p | p+p^2
     
      *  |  1  |  φ
    -----+-----+-----
      1  |  1  |  φ  
      φ  |  φ  | φ+1 

      *  |  1   |   φ    |  p   |   pφ   |  p^2  |  p^2φ
    -----+------+--------+------+--------+-------+-------------
      1  |  1   |   φ    |  p   |   pφ   |  p^2  |  p^2φ
      φ  |  φ   |  1+φ   | pφ   |  p+pφ  | p^2φ  | p^2+p^2φ
      p  |  p   |  pφ    | p^2  |  p^2φ  |  1+p  |  φ+pφ 
     pφ  | pφ   |  p+pφ  | p^2φ |p^2+p^2φ| φ+pφ  | 1+p+φ+pφ
     p^2 | p^2  |  p^2φ  | 1+p  |  φ+pφ  | p+p^2 | pφ+p^2φ
    p^2φ | p^2φ |p^2+p^2φ| φ+pφ |1+p+φ+pφ|pφ+p^2φ|p+p^2+pφ+p^2φ

     */
    @Override
    protected void initializeMultiplicationTensor() {
        short[][][] tensor = {
            { // 1
                {1, 0, 0, 0, 0, 0,},
                {0, 1, 0, 0, 0, 0,},
                {0, 0, 0, 0, 1, 0,},
                {0, 0, 0, 0, 0, 1,},
                {0, 0, 1, 0, 0, 0,},
                {0, 0, 0, 1, 0, 0,},
            },
            { // φ
                {0, 1, 0, 0, 0, 0,},
                {1, 1, 0, 0, 0, 0,},
                {0, 0, 0, 0, 0, 1,},
                {0, 0, 0, 0, 1, 1,},
                {0, 0, 0, 1, 0, 0,},
                {0, 0, 1, 1, 0, 0,},
            },
            { // p
                {0, 0, 1, 0, 0, 0,},
                {0, 0, 0, 1, 0, 0,},
                {1, 0, 0, 0, 1, 0,},
                {0, 1, 0, 0, 0, 1,},
                {0, 0, 1, 0, 1, 0,},
                {0, 0, 0, 1, 0, 1,},
            },
            {   // pφ
                {0, 0, 0, 1, 0, 0,},
                {0, 0, 1, 1, 0, 0,},
                {0, 1, 0, 0, 0, 1,},
                {1, 1, 0, 0, 1, 1,},
                {0, 0, 0, 1, 0, 1,},
                {0, 0, 1, 1, 1, 1,},
            },
            { // p^2
                {0, 0, 0, 0, 1, 0,},
                {0, 0, 0, 0, 0, 1,},
                {0, 0, 1, 0, 0, 0,},
                {0, 0, 0, 1, 0, 0,},
                {1, 0, 0, 0, 1, 0,},
                {0, 1, 0, 0, 0, 1,},
            },
            {   // p^2φ
                {0, 0, 0, 0, 0, 1,},
                {0, 0, 0, 0, 1, 1,},
                {0, 0, 0, 1, 0, 0,},
                {0, 0, 1, 1, 0, 0,},
                {0, 1, 0, 0, 0, 1,},
                {1, 1, 0, 0, 1, 1,},
            },
        };

        multiplicationTensor = tensor;
    }

    @Override
    protected void initializeLabels() {
        // according to http://archive.bridgesmathart.org/2000/bridges2000-87.pdf
        // Martin Gardner referred to the 3-fold plastic number proportion as High-Phi. 
        // The capital PHI is too similar to lower case phi so I'm going to use capital RHO. It lools like a P.
        final String upperRho = "\u03A1";
        final String lowerPhi = "\u03C6";
        irrationalLabels[1] = new String[]{lowerPhi, "phi"};                     // phi
        irrationalLabels[2] = new String[]{upperRho, "P"};                       // plasticNumber
        irrationalLabels[3] = new String[]{upperRho+"\u03C6", "Pphi"};           // plasticNumber * phi
        irrationalLabels[4] = new String[]{upperRho+"\u00B2", "P^2"};            // plasticNumber^2
        irrationalLabels[5] = new String[]{upperRho+"\u00B2\u03C6", "P^2phi"};   // plasticNumber^2 * phi
    }

    @Override
    public AlgebraicNumber getGoldenRatio()
    {
        return getUnitTerm(1);
    }
    
    @Override
    protected int[] convertGoldenNumberPairs(int[] terms) {
        if (terms.length == 2) {
            terms = new int[] {
                    terms[0],           // units
                    terms[1],           // phis
                    0,   // zero fill the rest...
                    0,
                    0,
                    0
            };
        }
        return super.convertGoldenNumberPairs(terms);
    }
}
