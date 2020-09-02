package com.vzome.jsweet;

import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicField;

public class JsAlgebraicNumber implements AlgebraicNumber
{
    private final JsAlgebraicField field;
    private final int[] factors;

    public JsAlgebraicNumber( JsAlgebraicField field, int[] factors )
    {
        this .field = field;
        this .factors = factors;
    }

    @Override
    public AlgebraicField getField()
    {
        return this .field;
    }

    @Override
    public double evaluate()
    {
        return this .field .evaluateNumber( this .factors );
    }

    @Override
    public int compareTo(AlgebraicNumber o) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean greaterThan(AlgebraicNumber other) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean lessThan(AlgebraicNumber other) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean greaterThanOrEqualTo(AlgebraicNumber other) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean lessThanOrEqualTo(AlgebraicNumber other) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public AlgebraicNumber plus(int n) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicNumber plus(int num, int den) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicNumber plus(AlgebraicNumber that) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicNumber times(int n) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicNumber times(int num, int den) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicNumber times(AlgebraicNumber that) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicNumber minus(int n) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicNumber minus(int num, int den) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicNumber minus(AlgebraicNumber that) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicNumber dividedBy(int divisor) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicNumber dividedBy(int num, int den) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicNumber dividedBy(AlgebraicNumber that) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isRational() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isZero() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isOne() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int signum() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public AlgebraicNumber negate() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AlgebraicNumber reciprocal() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void getNumberExpression(StringBuffer buf, int format) {
        // TODO Auto-generated method stub

    }

    @Override
    public String toString(int format) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int[] toTrailingDivisor() {
        // TODO Auto-generated method stub
        return null;
    }

}
