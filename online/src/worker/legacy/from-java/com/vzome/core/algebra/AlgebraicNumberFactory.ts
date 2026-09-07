import { AlgebraicField } from "./AlgebraicField.js";
import { AlgebraicNumber } from "./AlgebraicNumber.js";
import { BigRational } from "./BigRational.js";

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
    zero(): BigRational;

    one(): BigRational;

    createBigRational(numerator: number, denominator: number): BigRational;

    parseBigRational(str: string): BigRational;

    createAlgebraicNumber(field: AlgebraicField, numerators: number[], divisor: number): AlgebraicNumber;

    createAlgebraicNumberFromTD(field: AlgebraicField, trailingDivisorForm: BigRational[]): AlgebraicNumber;

    createAlgebraicNumberFromPairs(field: AlgebraicField, pairs: number[]): AlgebraicNumber;

    createAlgebraicNumberFromBRs(field: AlgebraicField, pairs: BigRational[]): AlgebraicNumber;

    createRational(field: AlgebraicField, numerator: number, denominator: number): AlgebraicNumber;

    isPrime(n: number): boolean;

    nextPrime(n: number): number;
}
