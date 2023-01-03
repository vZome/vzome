package com.vzome.core.algebra;

/**
 * @author David Hall
 */
public class SnubDodecahedronField extends ParameterizedField {
    // TODO: Change this name to "SnubDodec" for backward compatibility after we merge the two classes
    public static final String FIELD_NAME = "snubDodecahedron";
    
    /**
     * 
     * @return the coefficients of this AlgebraicField class. 
     * This can be used to determine when two fields have compatible coefficients 
     * without having to generate an instance of the class. 
     */
    public static double[] getFieldCoefficients() {
        double PHI_VALUE = ( 1.0 + Math.sqrt( 5.0 ) ) / 2.0;
        // specified to more precision than a double can retain so that value is as exact as possible: within one ulp().
        final double XI_VALUE = 1.71556149969736783d; // root of x^3 -2x -PHI_VALUE 
        return new double[] { 
            1.0d, 
            PHI_VALUE,
                        XI_VALUE,
            PHI_VALUE * XI_VALUE,
                        XI_VALUE * XI_VALUE,
            PHI_VALUE * XI_VALUE * XI_VALUE
        };
    }

    @Override
    public double[] getCoefficients() {
        return getFieldCoefficients();
    }
    
    public SnubDodecahedronField( AlgebraicNumberFactory factory ) {
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

    @Override
    protected void initializeMultiplicationTensor() {
    	short[][][] mm = {
            { // 1
                {1, 0, 0, 0, 0, 0 },
                {0, 1, 0, 0, 0, 0 },
                {0, 0, 0, 0, 0, 1 },
                {0, 0, 0, 0, 1, 1 },
                {0, 0, 0, 1, 0, 0 },
                {0, 0, 1, 1, 0, 0 },
            },
            { // phi
                {0, 1, 0, 0, 0, 0 },
                {1, 1, 0, 0, 0, 0 },
                {0, 0, 0, 0, 1, 1 },
                {0, 0, 0, 0, 1, 2 },
                {0, 0, 1, 1, 0, 0 },
                {0, 0, 1, 2, 0, 0 },
            },
            { // xi
                {0, 0, 1, 0, 0, 0 },
                {0, 0, 0, 1, 0, 0 },
                {1, 0, 0, 0, 2, 0 },
                {0, 1, 0, 0, 0, 2 },
                {0, 0, 2, 0, 0, 1 },
                {0, 0, 0, 2, 1, 1 },
            },
            { // phi * xi
                {0, 0, 0, 1, 0, 0 },
                {0, 0, 1, 1, 0, 0 },
                {0, 1, 0, 0, 0, 2 },
                {1, 1, 0, 0, 2, 2 },
                {0, 0, 0, 2, 1, 1 },
                {0, 0, 2, 2, 1, 2 },
            },
            { // xi^2
                {0, 0, 0, 0, 1, 0 },
                {0, 0, 0, 0, 0, 1 },
                {0, 0, 1, 0, 0, 0 },
                {0, 0, 0, 1, 0, 0 },
                {1, 0, 0, 0, 2, 0 },
                {0, 1, 0, 0, 0, 2 },
            },
            { // phi * xi^2
                {0, 0, 0, 0, 0, 1 },
                {0, 0, 0, 0, 1, 1 },
                {0, 0, 0, 1, 0, 0 },
                {0, 0, 1, 1, 0, 0 },
                {0, 1, 0, 0, 0, 2 },
                {1, 1, 0, 0, 2, 2 },
            },
        };

    	multiplicationTensor = mm;
    }

    @Override
    protected void initializeLabels() {
        irrationalLabels[1] = new String[]{ "\u03C6",             "phi" };
        irrationalLabels[2] = new String[]{       "\u03BE",           "xi" };
        irrationalLabels[3] = new String[]{ "\u03C6\u03BE",       "phi*xi" };
        irrationalLabels[4] = new String[]{       "\u03BE\u00B2",     "xi^2" };
        irrationalLabels[5] = new String[]{ "\u03C6\u03BE\u00B2", "phi*xi^2" };
    }

    @Override
    public int getNumMultipliers()
    {
        return 2; // only two primitive elements, phi and xi
    }

    /**
     * scalar for an affine pentagon
     * @return 
     */
    @Override
    public AlgebraicNumber getAffineScalar()
    {
        return getGoldenRatio();
    }

    @Override
    public AlgebraicNumber getGoldenRatio()
    {
        return getUnitTerm(1);
    }
    
    // No need to override convertGoldenNumberPairs() as long as phi is the first irrational
    
}
