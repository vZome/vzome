import { java, javaemul } from "../../../../../candies/j4ts-2.1.0-SNAPSHOT/bundle.js";
import { Fields } from "./Fields.js";

/**
 * Immutable Abstract Data Type for arbitrarily large rational numbers.
 * @class
 */
export interface BigRational extends Fields.RationalElement<java.math.BigInteger, BigRational> {
    isNegative(): boolean;
}
