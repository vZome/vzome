package com.vzome.jsweet;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicMatrix;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.RealVector;

public class JsAlgebraicField implements AlgebraicField
{
    public JsAlgebraicField() {
        // TODO Take a JS object as a parameter, to make this class wrap JS AFs
    }

    @Override
    public String getName()
    {
        return "golden";
    }

    @Override
    public int getOrder()
    {
        return DIVISOR;
    }

    public static final float PHI_VALUE = (float) (( 1.0 + Math.sqrt( 5.0 ) ) / 2.0);

    private static final int ONES_PLACE = 0, PHIS_PLACE = 1, DIVISOR = 2;

    @Override
    public int getNumIrrationals()
    {
        return 1;
    }

    int[] multiply( int[] v1, int[] v2 )
    {
        return null;
    }

    float evaluateNumber( int[] factors )
    {
        return ( factors[ ONES_PLACE ] + PHI_VALUE * factors[ PHIS_PLACE ] ) / factors[ DIVISOR ] ;
    }

    @Override
    public AlgebraicNumber createAlgebraicNumberFromTD( int[] trailingDivisorForm )
    {
        return new JsAlgebraicNumber( this, trailingDivisorForm );
    }

    int[] scaleBy( int[] factors, int whichIrrational )
    {
        return null;
    }

    private final static int[] ZERO = { 0, 0, 1 };
    
    private final static int[] ONE = { 1, 0, 1 };
    
    @Override
    public AlgebraicNumber zero() {
        return null;
    }

    @Override
    public AlgebraicNumber one() {
        return null;
    }

    @Override
    public AlgebraicVector origin(int dims) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void defineMultiplier(StringBuffer instances, int w) {
        // TODO Auto-generated method stub
    }

    @Override
    public String getIrrational(int i, int format) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getIrrational(int which) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicVector nearestAlgebraicVector(RealVector target) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicNumber createAlgebraicNumber(int[] terms) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicNumber createAlgebraicNumber(int[] numerators, int denominator) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicNumber createAlgebraicNumber(int ones, int irrat, int denominator, int scalePower) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicNumber getGoldenRatio() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicNumber createPower(int power) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicNumber createPower(int power, int irr) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicNumber createRational(long wholeNumber) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicNumber createRational(long numerator, long denominator) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicNumber getAffineScalar() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicNumber getUnitTerm(int n) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicVector projectTo3d(AlgebraicVector source, boolean wFirst) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicVector basisVector(int dims, int axis) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicVector createVector(int[][] nums) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicVector createIntegerVector(int[][] nums) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicVector createIntegerVectorFromTDs(int[][] nums) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicMatrix createMatrix(int[][][] data) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicNumber parseLegacyNumber(String val) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicNumber parseNumber(String nums) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicVector parseVector(String nums) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicMatrix identityMatrix(int dims) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getNumMultipliers() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public AlgebraicNumber parseVefNumber(String string, boolean isRational) {
        // TODO Auto-generated method stub
        return null;
    }

}
