package com.vzome.core.algebra;

/**
 * @author David Hall
 */
public class PlasticNumberField  extends ParameterizedField {
    public static final String FIELD_NAME = "plasticNumber";
    
    /**
     * 
     * @return the coefficients of a PlasticNumberField. 
     * This can be used to determine when two fields have compatible coefficients 
     * without having to generate an instance of the class. 
     */
    public static double[] getFieldCoefficients() {
        final double plasticNumber = 1.32471795724475d; // n + 1 = n^3
        return new double[] {
                1.0d,
                plasticNumber,
                plasticNumber * plasticNumber
        };
    }
    
    @Override
    public int getNumMultipliers()
    {
        return 1; // univariate polynomial
    }
    
    @Override
    public double[] getCoefficients() {
        return getFieldCoefficients();
    }
    
    public PlasticNumberField( AlgebraicNumberFactory factory ) {
        super( FIELD_NAME, 3, factory );
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
    -----+-----+-----+------
      1  |  1  |  p  |  p^2
      p  |  p  | p^2 |  1+p
     p^2 | p^2 | 1+p | p+p^2
     
     */
    @Override
    protected void initializeMultiplicationTensor() {
        short[][][] tensor = {
            { // 1
                {1, 0, 0,},
                {0, 0, 1,},
                {0, 1, 0,},
            },
            { // PHI
                {0, 1, 0,},
                {1, 0, 1,},
                {0, 1, 1,},
            },
            { // PHI^2
                {0, 0, 1,},
                {0, 1, 0,},
                {1, 0, 1,},
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
        irrationalLabels[1] = new String[]{upperRho, "P"};
        irrationalLabels[2] = new String[]{upperRho+"\u00B2", "P^2"};
    }
}

/*
coefficientsMultiplied( plasticNumber ) = 
{
  {     1.00000000000000,     1.32471795724475,     1.75487766624670, },
  {     1.32471795724475,     1.75487766624670,     2.32471795724477, },
  {     1.75487766624670,     2.32471795724477,     3.07959562349148, },
}

multiplierMatrix( plasticNumber ) = 
{
  {
    { 1, 0, 0, },
    { 0, 0, 1, },
    { 0, 1, 0, },
  },
  {
    { 0, 1, 0, },
    { 1, 0, 1, },
    { 0, 1, 1, },
  },
  {
    { 0, 0, 1, },
    { 0, 1, 0, },
    { 1, 0, 1, },
  },
}

factorsMultiplied( plasticNumber ) = 
{
  { 1,  Φ,  Φ², },
  { Φ,  Φ², 1+Φ,    },
  { Φ², 1+Φ,    Φ+Φ²,   },
}

factorsDivided( plasticNumber ) = 
{
  { 1,  -1+Φ²,  1+Φ-Φ², },
  { Φ,  1,  -1+Φ²,  },
  { Φ², Φ,  1,  },
}
*/
