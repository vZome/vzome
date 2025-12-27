/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.algebra {
    /**
     * @author vorth
     * 
     * This interface exists so that the AlgegraicField and AlgebraicNumber interfaces do not
     * need to depend on BigRational.  We need this because we have two kinds of AlgebraicField
     * implementations in Javascript: ones that use BigRational and are transpiled from Java, and
     * ones that are reimplemented in native Javascript using BigInt in trailing-denominator format.
     * 
     * For the transpiled field implementations, we have implementations of AlgebraicNumberFactory,
     * AlgebraicNumber (JavaAlgebraicNumber), and BigRational reimplemented in Javascript.
     * 
     * For the native Javascript field implementations, we have a *different* implementation of
     * AlgebraicNumber (JsAlgebraicNumber) that does not use a factory at all.
     * 
     * We also have the Java implementation of this interface, AlgebraicNumberImpl.FACTORY.
     * @class
     */
    export interface AlgebraicNumberFactory {
        zero(): com.vzome.core.algebra.BigRational;

        one(): com.vzome.core.algebra.BigRational;

        createBigRational(numerator: number, denominator: number): com.vzome.core.algebra.BigRational;

        parseBigRational(str: string): com.vzome.core.algebra.BigRational;

        createAlgebraicNumber(field: com.vzome.core.algebra.AlgebraicField, numerators: number[], divisor: number): com.vzome.core.algebra.AlgebraicNumber;

        createAlgebraicNumberFromTD(field: com.vzome.core.algebra.AlgebraicField, trailingDivisorForm: com.vzome.core.algebra.BigRational[]): com.vzome.core.algebra.AlgebraicNumber;

        createAlgebraicNumberFromPairs(field: com.vzome.core.algebra.AlgebraicField, pairs: number[]): com.vzome.core.algebra.AlgebraicNumber;

        createAlgebraicNumberFromBRs(field: com.vzome.core.algebra.AlgebraicField, pairs: com.vzome.core.algebra.BigRational[]): com.vzome.core.algebra.AlgebraicNumber;

        createRational(field: com.vzome.core.algebra.AlgebraicField, numerator: number, denominator: number): com.vzome.core.algebra.AlgebraicNumber;

        isPrime(n: number): boolean;

        nextPrime(n: number): number;
    }
}

