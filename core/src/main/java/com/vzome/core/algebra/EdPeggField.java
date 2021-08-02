package com.vzome.core.algebra;

// This field is based on a suggestion by Ed Pegg
public class EdPeggField extends ParameterizedField {
    public static final String FIELD_NAME = "edPegg";
    
    /**
     * 
     * @return the coefficients of an EdPeggField. 
     * This can be used to determine when two fields have compatible coefficients 
     * without having to generate an instance of the class. 
     */
    public static double[] getFieldCoefficients() {
        final double a = 1.76929235423863d; //  +2n +2 = n^3
        return new double[] {
                1.0d,
                a,
                a * a
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
    
    public EdPeggField( AlgebraicNumberFactory factory ) {
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
        short[][][] mt = {
            { // 1
                {1, 0, 0,},
                {0, 0, 2,},
                {0, 2, 0,},
            },
            { // epsilon
                {0, 1, 0,},
                {1, 0, 2,},
                {0, 2, 2,},
            },
            { // epsilon^2
                {0, 0, 1,},
                {0, 1, 0,},
                {1, 0, 2,},
            },
        };

        multiplicationTensor = mt;
    }

    @Override
    protected void initializeLabels() {
        // I'm going to use epsilon for Ed Pegg for lack of any known standard symbol
        irrationalLabels[1] = new String[]{"\u03B5", "epsilon"};
        irrationalLabels[2] = new String[]{"\u03B5\u00B2", "epsilon^2"};
    }

}
