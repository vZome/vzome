package defs.js;

import java.math.BigInteger;

/**
 * Java wrapper for JavaScript BigInt.
 * This class is intended for JSweet transpilation. 
 * Methods map directly to JavaScript BigInt operations
 * as long as this BigInt class is in the defs.js package.
 * Unsupported methods are commented out or deprecated 
 * and throw UnsupportedOperationException when run in Java.
 */
public class BigInt {
    private final BigInteger big;
    
    // Constructors
    public BigInt(String value) {
        this.big = new BigInteger(value);
    }
    
    public BigInt(long value) {
        this.big = BigInteger.valueOf(value);
    }
    
    // Used internally in this Java implementation, not mapped to JavaScript
    private BigInt(BigInteger value) {
        this.big = value;
    }
    
    // Arithmetic operations
    public BigInt add(BigInt other) {
        return new BigInt(big.add(other.big));
    }
    
    public BigInt subtract(BigInt other) {
        return new BigInt(big.subtract(other.big));
    }
    
    public BigInt multiply(BigInt other) {
        return new BigInt(big.multiply(other.big));
    }
    
    public BigInt divide(BigInt other) {
        return new BigInt(big.divide(other.big));
    }
    
    public BigInt remainder(BigInt other) {
        return new BigInt(big.remainder(other.big));
    }
    
//    @Deprecated
//    public BigInt pow(BigInt exponent) {
//        throw new UnsupportedOperationException("Not implemented in Java");
//    }
    
    public BigInt negate() {
        return new BigInt(big.negate());
    }
    
    public BigInt abs() {
        return new BigInt(big.abs());
    }
    
    // Comparison operations
    public boolean equals(BigInt other) {
        return this.big.equals(other.big);
    }
    
    public int compareTo(BigInt other) {
        return this.big.compareTo(other.big);
    }
    
//    @Deprecated
//    public boolean lessThan(BigInt other) {
//        throw new UnsupportedOperationException("Not implemented in Java");
//    }
//    
//    @Deprecated
//    public boolean lessThanOrEqual(BigInt other) {
//        throw new UnsupportedOperationException("Not implemented in Java");
//    }
//    
//    @Deprecated
//    public boolean greaterThan(BigInt other) {
//        throw new UnsupportedOperationException("Not implemented in Java");
//    }
//    
//    @Deprecated
//    public boolean greaterThanOrEqual(BigInt other) {
//        throw new UnsupportedOperationException("Not implemented in Java");
//    }
//    
//    // Bitwise operations
//    @Deprecated
//    public BigInt and(BigInt other) {
//        throw new UnsupportedOperationException("Not implemented in Java");
//    }
//    
//    @Deprecated
//    public BigInt or(BigInt other) {
//        throw new UnsupportedOperationException("Not implemented in Java");
//    }
//    
//    @Deprecated
//    public BigInt xor(BigInt other) {
//        throw new UnsupportedOperationException("Not implemented in Java");
//    }
//    
//    @Deprecated
//    public BigInt not() {
//        throw new UnsupportedOperationException("Not implemented in Java");
//    }
//    
//    @Deprecated
//    public BigInt shiftLeft(long bits) {
//        throw new UnsupportedOperationException("Not implemented in Java");
//    }
//    
//    @Deprecated
//    public BigInt shiftRight(long bits) {
//        throw new UnsupportedOperationException("Not implemented in Java");
//    }
    
    // Conversion methods
    @Override
    public String toString() {
        return this.big.toString();
    }
    
    public String toString(int radix) {
        return this.big.toString(radix);
    }
    
    /**
     * Converts this {@code BigInt} to a {@code long}, checking
     * for lost information.  If the value of this {@code BigInt}
     * is out of the range of the {@code long} type, then an
     * {@code ArithmeticException} is thrown.
     * 
     * We specifically don't want to implement toLong() 
     * without protecting against silent data loss
     *
     * @return this {@code BigInt} converted to a {@code long}.
     * @throws ArithmeticException if the value of {@code this} will
     * not exactly fit in a {@code long}.
     * @see BigInteger#longValueExact
     * 
     * @deprecated Use {@code toString()} instead, to avoid possible data loss.
     */
    @Deprecated
    public long toLong() {
        return this.big.longValueExact();
    }
    
//    @Deprecated
//    public double toDouble() {
//        throw new UnsupportedOperationException("Not implemented in Java");
//    }
//    
//    // Utility methods
//    @Deprecated
//    public BigInt min(BigInt other) {
//        throw new UnsupportedOperationException("Not implemented in Java");
//    }
//    
//    @Deprecated
//    public BigInt max(BigInt other) {
//        throw new UnsupportedOperationException("Not implemented in Java");
//    }
    
    // Static factory methods
    public static BigInt valueOf(String value) {
        return new BigInt(value);
    }
    
    public static BigInt valueOf(long value) {
        return new BigInt(value);
    }
    
//    @Deprecated
//    public static BigInt asIntN(int bits, BigInt value) {
//        throw new UnsupportedOperationException("Not implemented in Java");
//    }
//    
//    @Deprecated
//    public static BigInt asUintN(int bits, BigInt value) {
//        throw new UnsupportedOperationException("Not implemented in Java");
//    }
}