package com.vzome.core.algebra;

import java.util.function.BiConsumer;

/**
 * @author David Hall
 */
public abstract class ParameterizedField<T extends Object> extends AlgebraicField {

    protected final T operand;
    protected final double[] coefficients;
    protected short[][][] multiplierMatrix;
    protected final String[][] irrationalLabels;
    
    public ParameterizedField(String name, int order, T operand) {
        super(name, order);
        this.operand = operand;
        // These arrays are allocated here, but all non-zero values will be initialized in the derived classes.
        coefficients = new double[order];
        multiplierMatrix = new short[order][order][order];
        irrationalLabels = new String[order][2];
        irrationalLabels[0] = new String[] {"", ""}; // unused placeholder for easier indexing
        // overridable methods intentionally called from c'tor. Be sure all member variables are initialized first.
        initialize();
    }

    /**
     * doNothing is the default normalizer method.
     * @param factors
     */
    static final void doNothing(AlgebraicField field, BigRational[] factors) {}
    
    /**
     * Subclasses may need different normalization methods based on their parameters.
     * For example, SqrtField(2) doesn't need normalization, but SqrtField(4) does since 4 is a perfect square 
     * By assigning an appropriate normalizer method once in the c'tor, 
     * the method can avoid the repeated overhead of checking isPerfectSquare() within the normalizer method itself.   
     */
    protected BiConsumer<AlgebraicField, BigRational[]> normalizer = ParameterizedField::doNothing;
    
    @Override
	final void normalize( BigRational[] factors ) 
    {
        normalizer.accept(this, factors);
    }
    
    protected void initialize()
    {
        // In some cases, the coefficients may eventually be determined
        // simply by evaluating the only possible solutions to the multiplierMatrix.
        // The labels are initialized last because they could possibly utilize the other values.
        validate();
        initializeNormalizer();
        initializeMultiplierMatrix();
        initializeCoefficients();
        initializeLabels();
    }

    protected abstract void validate();

    protected void initializeNormalizer() 
    {
        normalizer = ParameterizedField::doNothing;
    }

    protected abstract void initializeMultiplierMatrix();

    protected abstract void initializeCoefficients();
    
    protected abstract void initializeLabels();
    
    @Override
    protected BigRational[] multiply( BigRational[] v1, BigRational[] v2 )
    {
        int order = getOrder();
        BigRational[] result = new BigRational[order];
        for(int i = 0; i < order; i++) {
            result[i] = BigRational.ZERO;
            for (int j = 0; j < order; j++) {
                for (int k = 0; k < order; k++) {
                    int multiplier = multiplierMatrix[i][j][k];
                    // We would get the same result if we do the long math even when multiplier is 0 or 1
                    // but the checks for the two special cases (0 and 1) are quicker than the overhead of BigRational math
                    // so they are included here as performance optimizations.
                    if(multiplier != 0) {
                        BigRational product = v1[j]. times( v2[k] );
                        if(multiplier != 1) {
                            product = product. times( multiplier );
                        }
                        result[i] = result[i].plus(product);
                    }
                }
            }
        }
        return result;
    }

    @Override
    BigRational[] scaleBy(BigRational[] factors, int whichIrrational)
    {
        if (whichIrrational == 0) {
            return factors;
        }
        int order = getOrder();
        BigRational[] result = new BigRational[order];
        for(int i = 0; i < order; i++) {
            result[i] = BigRational.ZERO;
            for (int j = 0; j < order; j++) {
                int multiplier = multiplierMatrix[i][j][whichIrrational];
                // We would get the same result if we do the long math even when multiplier is 0 or 1
                // but the check for the two special case (0 and 1) is lots quicker than the overhead of BigRational math
                // so they are included here as performance optimizations.
                if(multiplier != 0) {
                    if (multiplier == 1) {
                        result[i] = result[i].plus(factors[j]);
                    } else {
                        result[i] = result[i].plus(factors[j].times( multiplier ));
                    }
                }
            }
        }
        normalize( result );
        return result;
    }

    @Override
    double evaluateNumber(BigRational[] factors) {
        double result = 0.0d;
        int order = getOrder();
        for (int i = 0; i < order; i++) {
            result += factors[i].evaluate() * coefficients[i];
        }
        return result;
    }

    @Override
    public final String getIrrational(int i, int format) {
        return irrationalLabels[i][format];
    }

    public double getCoefficient(int i) {
        return coefficients[i];
    }
}
