package com.vzome.core.algebra;

/**
 * @author David Hall
 */
public class SnubCubeField  extends ParameterizedField {
    public static final String FIELD_NAME = "snubCube";
    
    /**
     * 
     * @param radicand
     * @return the coefficients of a SnubCubeField. 
     * This can be used to determine when two fields have compatible coefficients 
     * without having to generate an instance of the class. 
     * Note that this method provides no validation of the parameter.
     */
    public static double[] getFieldCoefficients() {
        // Tribonacci constant is a root of x^3 − x^2 − x − 1 and satisfies x + x^(−3) = 2
        final double tribonacciConstant = (1.0d
                + Math.cbrt(19.0d - (3.0d * Math.sqrt(33))) // this term has a minus in the middle
                + Math.cbrt(19.0d + (3.0d * Math.sqrt(33))) // this term has a plus  in the middle
                ) / 3.0d;
        return new double[] {
                1.0d,
                tribonacciConstant,
                tribonacciConstant * tribonacciConstant
        };
    }
    
    @Override
    public double[] getCoefficients() {
        return getFieldCoefficients();
    }
    
    public SnubCubeField( AlgebraicNumberFactory factory ) {
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

    @Override
    protected void initializeMultiplicationTensor() {
        short[][][] mm = {
            { // 1
                {1, 0, 0,},
                {0, 0, 1,},
                {0, 1, 1,},
            },
            { // psi
                {0, 1, 0,},
                {1, 0, 1,},
                {0, 1, 2,},
            },
            { // psi^2
                {0, 0, 1,},
                {0, 1, 1,},
                {1, 1, 2,},
            },
        };

        multiplicationTensor = mm;
    }

    @Override
    protected void initializeLabels() {
        irrationalLabels[1] = new String[]{"\u03C8", "psi"};
        irrationalLabels[2] = new String[]{"\u03C8\u00B2", "psi^2"};
    }
    
    @Override
    public int getNumMultipliers()
    {
        return 1; // psi^2 can be achieved by squaring psi
    }
}

/*
coefficientsMultiplied( snubCube ) = 
{
  {     1.00000000000000,     1.83928675521416,     3.38297576790624, },
  {     1.83928675521416,     3.38297576790624,     6.22226252312040, },
  {     3.38297576790624,     6.22226252312040,    11.44452504624079, },
}

multiplierMatrix( snubCube ) = 
{
  {
    { 1, 0, 0, },
    { 0, 0, 1, },
    { 0, 1, 1, },
  },
  {
    { 0, 1, 0, },
    { 1, 0, 1, },
    { 0, 1, 2, },
  },
  {
    { 0, 0, 1, },
    { 0, 1, 1, },
    { 1, 1, 2, },
  },
}

factorsMultiplied( snubCube ) = 
{
  { 1,  ψ,  ψ², },
  { ψ,  ψ², 1+ψ+ψ², },
  { ψ², 1+ψ+ψ², 1+2ψ+2ψ²,   },
}

factorsDivided( snubCube ) = 
{
  { 1,  -1-ψ+ψ²,    2ψ-ψ²,  },
  { ψ,  1,  -1-ψ+ψ²,    },
  { ψ², ψ,  1,  },
}
*/
