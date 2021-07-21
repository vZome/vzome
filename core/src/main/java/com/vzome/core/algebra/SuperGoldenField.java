package com.vzome.core.algebra;

// This field is based on a suggestion by Ed Pegg
// See https://community.wolfram.com/groups/-/m/t/1286708
// The Narayana's cows sequence constant is also known as 
// the Super Golden Ratio at https://en.wikipedia.org/wiki/Supergolden_ratio
public class SuperGoldenField extends ParameterizedField {
    public static final String FIELD_NAME = "superGolden";
    
    /**
     * 
     * @return the coefficients of a SuperGoldenField. 
     * This can be used to determine when two fields have compatible coefficients 
     * without having to generate an instance of the class. 
     * Note that this method provides no validation of the parameter.
     */
    public static double[] getFieldCoefficients() {
        // using more digits than will fit in a double
        final double narayanaCowNumber = 1.46557123187676802d; // n^2 + 1 = n^3
        return new double[] {
                1.0d,
                narayanaCowNumber,
                narayanaCowNumber * narayanaCowNumber
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
    
    public SuperGoldenField( AlgebraicNumberFactory factory ) {
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
                {0, 0, 1,},
                {0, 1, 1,},
            },
            { // psi
                {0, 1, 0,},
                {1, 0, 0,},
                {0, 0, 1,},
            },
            { // psi^2
                {0, 0, 1,},
                {0, 1, 1,},
                {1, 1, 1,},
            },
        };

        multiplicationTensor = mt;
    }

    @Override
    protected void initializeLabels() {
        // Psi is the symbol used at https://en.wikipedia.org/wiki/Supergolden_ratio
        irrationalLabels[1] = new String[]{"\u03C8", "psi"};
        irrationalLabels[2] = new String[]{"\u03C8\u00B2", "psi^2"};
    }

}
