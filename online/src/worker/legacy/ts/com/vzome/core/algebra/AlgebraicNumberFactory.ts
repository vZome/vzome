/* Generated from Java with JSweet 3.2.0-SNAPSHOT - http://www.jsweet.org */
namespace com.vzome.core.algebra {
    export interface AlgebraicNumberFactory {
        zero(): com.vzome.core.algebra.BigRational;

        one(): com.vzome.core.algebra.BigRational;

        createBigRational(numerator: number, denominator: number): com.vzome.core.algebra.BigRational;

        createAlgebraicNumber(field: com.vzome.core.algebra.AlgebraicField, numerators: number[], divisor: number): com.vzome.core.algebra.AlgebraicNumber;

        createAlgebraicNumberFromTD(field: com.vzome.core.algebra.AlgebraicField, trailingDivisorForm: number[]): com.vzome.core.algebra.AlgebraicNumber;

        createAlgebraicNumberFromPairs(field: com.vzome.core.algebra.AlgebraicField, pairs: number[]): com.vzome.core.algebra.AlgebraicNumber;

        createRational(field: com.vzome.core.algebra.AlgebraicField, numerator: number, denominator: number): com.vzome.core.algebra.AlgebraicNumber;

        isPrime(n: number): boolean;

        nextPrime(n: number): number;
    }
}

