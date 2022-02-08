package com.vzome.core.algebra;

import static java.lang.Math.sin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author David Hall
 */
public class PolygonField extends ParameterizedField
{
    private static final double PI = 3.14159265358979323846; // JSweet doesn't know Math.PI
    /**
     * 
     * @param nSides
     * @return the coefficients of a PolygonField given the same parameter. 
     * This can be used to determine when two fields have compatible coefficients 
     * without having to generate an instance of the class. 
     */
    public static double[] getFieldCoefficients(int nSides) {
        int order = getOrder(nSides);
        double[] coefficients = new double[order]; 
        double[] diagLengths = getDiagonalLengths(nSides);

        // if nSides is prime or a power of two then all diagLengths are returned
        // otherwise, order is less than diagLengths.length, so not all are returned
        for (int i = 0; i < order; i++) {
            coefficients[i] = diagLengths[i];
        }
        return coefficients;
    }

    /**
     * 
     * @param nSides
     * @return an array with the unique lengths in increasing order 
     * of the diagonals of a regular N-gon having a unit edge length. 
     */
    public static double[] getDiagonalLengths(int nSides) {
        int count = diagonalCount(nSides);
        double[] diagLengths = new double[count]; 
        double unitLength = sin(PI / nSides);

        // The units position should always be exactly 1.0d.
        // We avoid any trig or rounding errors by specifically assigning it that value.
        diagLengths[0] = 1.0d;
        // now initialize the rest, starting from i = 1
        for (int i = 1; i < count; i++) {
            diagLengths[i] = sin((i+1) * PI / nSides) / unitLength;
        }

        // I discovered that a few significant values don't appear to be calculated "correctly" at first glance.
        // I found a great explanation at https://randomascii.wordpress.com/2012/02/25/comparing-floating-point-numbers-2012-edition/
        switch(nSides) {
        case 6:
            // Since PI is irrational and cannot be exactly represented in a double,
            // the trig functions may not produce the exact result we expect.
            // Specifically, for a hexagon, the calculated value of coefficients[2] is 2.0000000000000004
            // I want to have the exact correct value, so I'm going to hard code it.
            // I'm pretty sure that Niven's theorem https://en.wikipedia.org/wiki/Niven%27s_theorem
            // implies that this will be the only case where we'll get a rational result.
            diagLengths[2] = 2.0d;
            // Similarly, the calculated value of coefficients[1] is 1.7320508075688774 but should exactly equal sqrt(3) which is 1.73205080756887729...
            diagLengths[1] = Math.sqrt(3);
            break;

        case 5:
            // Similarly, for pentagons, the trig calculation for coefficients[1] differs from PHI_VALUE by 0.0000000000000002220446049250313
            // PHI_VALUE       = 1.618033988749895
            // coefficients[1] = 1.618033988749897
            // WolframAlpha says 1.618033988749894848204586834365...
            // I want to have the same value in either case, so I'm going to hard code it.
            diagLengths[1] = ( 1.0d + Math.sqrt( 5.0d ) ) / 2.0d;
            break;

            //        default:
            //            // No difference found between sqrt(2) and coefficients[1] of PolygonField(4)
            //            // or any other PolygonFields
            //            break;
        }
        return diagLengths;
    }

    public static final String FIELD_PREFIX = "polygon";

    public static int getOrder(int nSides) {
        return primaryDiagonalCount(nSides);
    }

    public static int diagonalCount(int nSides) {
        return nSides/2;
    }

    // generates the sequence described at https://oeis.org/A055034
    public static int primaryDiagonalCount(int nSides) {
        // as long as nSides is an int, the result can safely be cast down to an int
        // but we need a long to hold 2 * nSides without overflowing
        return (int) (eulerTotient(2L * nSides) / 2L);
    }

    public static int secondaryDiagonalCount(int nSides) {
        return diagonalCount(nSides) - primaryDiagonalCount(nSides);
    }

    // returns the number of positive integers <= n and coprime to n
    // The result will always be less than or equal to n when n is positive. 
    // This function is also known as eulerPhi() or simply phi(),
    // but since we use phi for the golden ratio, I'll call it eulerTotient()
    // It generates the sequence described at https://oeis.org/A000010
    public static long eulerTotient(long n) {
        long result = n; 
        for(long i=2; i*i <= n; i++) { 
            if (n % i == 0) result -= result / i; 
            while (n % i == 0) {
                n /= i;
            }
        } 
        if (n > 1) {
            result -= result / n; 
        }
        return result; 
    }

    public static boolean isPowerOfTwo(int n) {
        return (n != 0) && ( (n & -n) == n );
    }

    public boolean isPrime(int n)
    {
        return this.numberFactory.isPrime( n );
    }

    private List<Integer> distinctPrimeFactors(int n) {
        List<Integer> factors = new ArrayList<>();
        for(int prime = 2; prime <= n; prime = numberFactory.nextPrime( prime ) ) {
            if(n % prime == 0) {
                factors.add(prime);
            }
            while(n % prime == 0) {
                n /= prime;
            }
        }
        return factors;
    }

    public short[][][] getNormalizedMultiplicationTensor(int nSides) {
        short[][][] tensor = getExtendedMultiplicationTensor(nSides);
        if(isPrime(nSides) || isPowerOfTwo(nSides)) {
            return tensor;
        }
        // copy the truncated tensor to result
        int length = primaryDiagonalCount(nSides);
        short[][][] result = new short[length][length][length];
        for(int i = 0; i < length; i++) {
            for(int j = 0; j < length; j++) {
                for(int k = 0; k < length; k++) {
                    result[i][j][k] = tensor[i][j][k];
                }
            }
        }
        // apply normalizer matrix to result
        short[][] normalizerMatrix = getNormalizerMatrix(nSides);
        int n = 0;
        for(int term = length; term < diagonalCount(nSides); term++) {
            for(int r = 0; r < length; r++) {
                for(int c = 0; c < length; c++) {
                    short omit = tensor[term][r][c];
                    if(omit != 0) {
                        for(int t = 0; t < length; t++) {
                            short alt = normalizerMatrix[n][t];
                            if(alt != 0) {
                                int adjust = omit * alt;
                                // This is the same as using 
                                // result[t][r][c] += adjust; 
                                // except that when using the += operator, the cast to short is implicit and thus may be overlooked.
                                result[t][r][c] = (short)(result[t][r][c] + adjust); // cast assumes no overflow or underflow
                            }
                        }
                    }
                }
            }
            n++;
        }
        return result;
    }

    public short[][] getNormalizerMatrix(int nSides)
    {
        if(nSides < MIN_SIDES) {
            throw new IllegalArgumentException("nSides = " + nSides + " but must be greater than or equal to " + MIN_SIDES);
        }
        final int nSecondaryDiags = secondaryDiagonalCount(nSides);
        if(nSecondaryDiags == 0) { // same effect as isPrime(nSides) || isPowerOfTwo(nSides)
            return null;
        }
        final int nPrimaryDiags = primaryDiagonalCount(nSides);
        final int nDiags = nPrimaryDiags + nSecondaryDiags; // equivalent to diagonalCount(nSides);

        List<Integer> primeFactors = distinctPrimeFactors(nSides);

        if(primeFactors.get(0) == 2) {
            primeFactors.remove(0);
        }

        int nEquations = 0;
        for ( Integer prime : primeFactors ) {
            nEquations += nDiags / prime.intValue();
        }

        BigRational[][] primaryDiags = new BigRational[nEquations][nPrimaryDiags];
        BigRational[][] secondaryDiags = new BigRational[nEquations][nSecondaryDiags];
        int equationRow = 0;
        for(int factor : primeFactors) {
            final int period = nSides / factor;
            final int steps  = period / 2;
            final int parity = period % 2; // odd and even periods behave just a little differently
            for(int step = 0; step < steps; step++) {
                int n = (step == 0 && parity == 0) ? 2: 1;
                if(nSides % 2 == parity) {
                    n *= -1;
                }
                int[] terms = new int[nDiags];
                terms[step] = 1;
                for(int mid = period-parity; mid < nDiags; mid += period) {
                    terms[mid + step + parity] = 
                            terms[mid - step] = n;
                    n *= -1;
                }
                // split the array of terms into the two matrices
                primaryDiags[equationRow] = new BigRational[nPrimaryDiags];
                secondaryDiags[equationRow] = new BigRational[nSecondaryDiags];
                for(int t = 0; t < terms.length; t++) {
                    int term = terms[t];
                    if(t < nSecondaryDiags) {
                        secondaryDiags[equationRow][t] = numberFactory .createBigRational(term,1);
                    } else {
                        term *= -1; // negate these since we're moving them across the "equal sign"
                        primaryDiags[equationRow][t-nSecondaryDiags] = numberFactory .createBigRational(term,1);
                    }
                }
                equationRow++;
            }
        }

        int rank = Fields.gaussJordanReduction(secondaryDiags, primaryDiags);

        if(rank != nSecondaryDiags) {
            throw new IllegalStateException("System of equations has unexpected rank: " + rank);            
        }

        for(int r = rank; r < primaryDiags.length; r++) {
            for(int c = 0; c < primaryDiags[0].length; c++) {
                if(!primaryDiags[r][c].isZero()) {
                    throw new IllegalStateException("System of equations is inconsistent. Rank = " + rank);
                }
            }
        }

        short[][] results = new short[rank][nPrimaryDiags];
        for(int r = 0; r < rank; r++) {
            for(int c = nPrimaryDiags-1; c >= 0; c--) {
                BigRational bigTerm = primaryDiags[rank-1-r][nPrimaryDiags-1-c]; // reverse both row and column orders here
                results[r][c] = Double.valueOf(bigTerm.evaluate()).shortValue();
            }
        }
        return results;
    }

    public static short[][][] getExtendedMultiplicationTensor(int nSides) {
        int nDiags = diagonalCount(nSides);
        short[][][] tensor = new short[nDiags][nDiags][nDiags];

        // initialize everything to 0
        for (int i = 0; i < nDiags; i++) {
            for (int j = 0; j < nDiags; j++) {
                for (int k = 0; k < nDiags; k++) {
                    tensor[i][j][k] = 0;
                }
            }
        }

        // initialize all of the \<->\ SouthEasterly diagonal paths
        for (int layer = 0; layer < nDiags; layer++) {
            int midWay = layer/2;
            for (int bx = layer, by = 0; bx > midWay || bx == by; bx--, by++) {
                for (int x = bx, y = by; x < nDiags && y < nDiags; x++, y++) {
                    // Simple assignment would work here 
                    // but incrementing the value identifies unwanted duplicates. Ditto for the mirror.
                    tensor[layer][y][x] += 1;
                    if(x != y) {
                        tensor[layer][x][y] += 1; // mirror around x == y
                    }
                }
            }
        }

        // initialize the remaining /<->/ SouthWesterly diagonal paths
        int box = nSides - 2;
        int parity = (nSides + 1) % 2;
        for (int layer = 0; layer < nDiags-parity; layer++) {
            int base = box - layer;
            for (int xb = base, yb = 0; xb >= 0; xb--, yb++) {
                int x=xb;
                int y=yb;
                while(x<nDiags && y<nDiags) {
                    tensor[layer][y][x] += 1;
                    x++;
                    y++;
                }
            }
        }
        return tensor;
    }

    public static String subscriptString(int i) {
        // https://stackoverflow.com/questions/17908593/how-to-find-the-unicode-of-the-subscript-alphabet
        return Integer.toString(i)
                .replace("0", "\u2080")
                .replace("1", "\u2081")
                .replace("2", "\u2082")
                .replace("3", "\u2083")
                .replace("4", "\u2084")
                .replace("5", "\u2085")
                .replace("6", "\u2086")
                .replace("7", "\u2087")
                .replace("8", "\u2088")
                .replace("9", "\u2089")
                .replace("+", "\u208A")
                .replace("-", "\u208B")
                ;
    }

    public final static int MIN_SIDES = 4;

    private final boolean isEven;
    private final AlgebraicNumber goldenRatio;

    /*
     * Much of the class initialization is done by having member methods call comparable static methods
     * for various steps in the process. This is a bit unususal and isn't necessary at all. 
     * I implemented it that way as I was developing this class 
     * so that I could independently test the math in individual steps of the process.
     * Being able to invoke each step independently from within unit tests 
     * also helps with documenting and explaining the logic that makes this class tick 
     * so I'm going to leave them as public static methods.
     */

    private final int polygonSides;
    
    public PolygonField( int polygonSides, AlgebraicNumberFactory factory ) {
        this( FIELD_PREFIX + polygonSides, polygonSides, factory );
    }

    // this protected c'tor is intended to allow PentagonField and HeptagonField classes to be refactored
    // so they are derived from PolygonField and still maintain their original legacy names
    protected PolygonField(String name, int polygonSides, AlgebraicNumberFactory factory ) {
        super( name, getOrder(polygonSides), factory );
        this.polygonSides = polygonSides;
        validate();
        initialize();
        isEven = polygonSides % 2 == 0;
        goldenRatio = getDiagonalRatio(5);
    }
    
    /**
     * 
     * u = units numerator  
     * U = units denominator  
     * p = phis numerator  
     * P = phis denominator
     * ____ = 0,1
     * COMBO ... see comments inline below
     * Remapping the 4 element pairs array [u,U, p,P] 
     * looks like this based on polygonSides:
     *   5  [  u, U,   p, P] // unchanged
     *  10  [ COMBO,  ____,   p, P    ... // the two units elements combine all of the input pairs 
     *  15  [  u, U,  -p,-P,   ____,   p, P    ...
     *  20  [  u, U,   ____,  -p,-P,   ____,   p, P    ...
     *  25  [  u, U,   ____,   ____,  -p,-P,   ____,   p, P    ...
     *  30  [  u, U,   ____,   ____,   ____,  -p,-P,   ____,   p, P    ...
     *  35  [  u, U,   ____,   ____,   ____,   ____,  -p,-P,   ____,   p, P    ...
     *  40  [  u, U,   ____,   ____,   ____,   ____,   ____,  -p,-P,   ____,   p, P    ...
     *  45  [  u, U,   ____,   ____,   ____,   ____,   ____,   ____,  -p,-P,   ____,   p, P    ...
     * index   0  1    2  3    4  5    6  7    8  9   10 11   12 13   14 15   16 17   18 19
     */
    @Override
    protected long[] convertGoldenNumberPairs( long[] pairs )
    {
        if( polygonSides % 5 == 0 && pairs.length == 4 && getOrder() > 2 ) {
            // remap [ unitNumDen, phiNumDen ] pairs as needed
            final long u = pairs[0]; // units numerator
            final long U = pairs[1]; // units denominator
            final long p = pairs[2]; // phis numerator 
            final long P = pairs[3]; // phis denominator
            long[] remapped = new long[2* getOrder()];
            for(int den = 1; den < remapped.length; den += 2) {
                // Numerators are already set to 0. 
                // Set denominators to 1 for all remapped pairs
                remapped[den] = 1;
            }
            // remap phi's numerator and denominator
            // Each of them maps to 2 terms in the remapped array
            int i = (polygonSides / 5) * 2;
            // negate the first numerator
            remapped[i-4] = -p; // negate phis numerator 
            remapped[i-3] =  P; // phis denominator
            // assign the second pair
            remapped[i+0] =  p; // phis numerator 
            remapped[i+1] =  P; // phis denominator
            
            // remap the unit's numerator and denominator
            if(polygonSides == 10) {
                // This is the only case where we have to do any math.
                // Every other case just remaps the position if the input pairs.
                // Here, we need to treat unit and phi pairs as the actual fractions 
                // they represent so we can combine them arithmetically.
                // We don't need to reduce the resulting fraction to lowest terms here
                // since BigRational will eventually do that.
                //
                // We cast everything to long and store the multiplication results
                // in a long so we can test for integer overflow ourselves
                // without resorting to the overhead of BigInteger.intValueExact().
                // This also avoids any reference to BigInteger for the sake of JSweet.
                //
                // Multiplying an int that's cast to a long by another int that's cast to a long
                // will always fit into a long so we needn't check multiplication for overflows.
                // Just check the actual subtraction and the size of the final results.
                //
                // COMBO = u/U - p/P = u*P/U*P - U*p/U*P = ((u*P)-(U*p))/(U*P)
                // So the numerator is (u*P)-(U*p)
                // and the denominator is U*P.
                remapped[0] = safeSubtract( (u * P), (U * p) );
                remapped[1] = U * P;
            } else {
                // no possible conflict with phis
                remapped[0] = u; // units numerator
                remapped[1] = U; // units denominator
            }
            return remapped;
        }
        return pairs; // unchanged
    }

    /**
     * @param j
     * @param k
     * @return a long that equals j - k
     * @throws ArithmeticException if the subtraction causes an integer overflow
     */
    static long safeSubtract(long j, long k) {
        long result = j - k;
        // check if the subtraction itself causes the long result to overflow
        if( (k > 0 && result >= j) || (k < 0 && result <= j) ) {
            throw new ArithmeticException("Arithmetic Overflow: " + j + " - " + k + " = " + result + ". Result exceeds the size of a long."); 
        }
        return result;
    }

    
    // DJH: I have reimplemented the same effect in convertGoldenNumberPairs() 
    //     but I'll leave this here for now as a reminder of the original logic in case we ever come back to it.
    // Note that this original method works for any golden VEF import into any 5N-gon field
    // whereas the convertGoldenNumberPairs approach inevitably has a potential integer overflow
    // but only in in the case of the 10-gon field importing VEF with very large integers.
    // Given that minute regression, in exchange for supporting N-gon fields online, 
    // we'll live with the limitation for now. 

//    @Override
//    protected BigRational[] prepareAlgebraicNumberTerms(BigRational[] terms) {
//        int nonNullTerms = 0; // can't just use terms.length() since some may be null as when reading VEFShapes
//        for(int i = 0; i < terms.length; i++) {
//            if(terms[i] == null) {
//                break;
//            }
//            nonNullTerms++;
//        }
//        if (goldenRatio != null && nonNullTerms == 2 && getOrder() > 2 ) {
//            AlgebraicNumber scaleUnits = goldenDenominator.times(createRational(terms[0]));
//            AlgebraicNumber scaledPhis = goldenNumerator.times(createRational(terms[1]));
//            return ((AlgebraicNumberImpl)(scaleUnits.plus(scaledPhis)).dividedBy(goldenDenominator)).getFactors();
//        }
//        return super.prepareAlgebraicNumberTerms(terms);
//    }

    public int diagonalCount() {
        return diagonalCount(polygonSides());
    }

    @Override
    public double[] getCoefficients() {
        return getFieldCoefficients(polygonSides());
    }

    @Override
    public AlgebraicNumber getAffineScalar()
    {
        return getUnitDiagonal( 2 ); // be sure to use getUnitDiagonal() instead of getUnitTerm()
    }

    protected void validate() {
        if (polygonSides() < MIN_SIDES) {
            String msg = "polygon sides = " + polygonSides() + ". It must be at least " + MIN_SIDES + ".";
            throw new IllegalArgumentException(msg);
        }
    }

    @Override
    protected void initializeLabels() {
        int nSides = polygonSides();
        if(irrationalLabels.length != diagonalCount(nSides)) {
            String[] unitLabels = irrationalLabels[0];
            irrationalLabels = new String[diagonalCount(nSides)][unitLabels.length];
            irrationalLabels[0] = unitLabels; // retain the default labels for units
        }
        // odd-gons are labeled with the same lower case Greek letters as
        // Peter Steinbach uses in "Sections Beyond Golden"
        // See http://archive.bridgesmathart.org/2000/bridges2000-35.pdf
        switch(polygonSides()) {
        case 4:
            irrationalLabels[1] = new String[]{ "\u221A" + "2", "sqrtTwo" };
            break;

        case 5:
            irrationalLabels[1] = new String[]{ "\u03C6", "phi" };
            break;

        case 6:
            irrationalLabels[1] = new String[]{ "\u221A" + "3", "sqrtThree" };
            irrationalLabels[2] = new String[]{ "\u03B2", "beta" };
            break;

        case 7:
            irrationalLabels[1] = new String[]{ "\u03C1", "rho" };
            irrationalLabels[2] = new String[]{ "\u03C3", "sigma" };
            break;

        case 9:
            irrationalLabels[1] = new String[]{ "\u03B1", "alpha" };
            irrationalLabels[2] = new String[]{ "\u03B2", "beta" };
            irrationalLabels[3] = new String[]{ "\u03B3", "gamma" };
            break;

        case 11:
            irrationalLabels[1] = new String[]{ "\u03B8", "theta"  };
            irrationalLabels[2] = new String[]{ "\u03BA", "kappa"  };
            irrationalLabels[3] = new String[]{ "\u03BB", "lambda" };
            irrationalLabels[4] = new String[]{ "\u03BC", "mu"     };
            break;

        case 13:
            // See https://nbviewer.jupyter.org/github/vorth/ipython/blob/master/triskaidecagons/Triskaidecagons.ipynb
            irrationalLabels[1] = new String[]{ "\u03B1", "alpha" };
            irrationalLabels[2] = new String[]{ "\u03B2", "beta" };
            irrationalLabels[3] = new String[]{ "\u03B3", "gamma" };
            irrationalLabels[4] = new String[]{ "\u03B4", "delta" };
            irrationalLabels[5] = new String[]{ "\u03B5", "epsilon" };
            break;

        default:
            final String alphabet = "abcdefghijklmnopqrstuvwxyz";
            int length = irrationalLabels.length;
            if(length-1 <= alphabet.length()) {
                for(int i = 1; i < length; i++) {
                    String name = alphabet.substring(i-1, i);
                    irrationalLabels[i] = new String[]{ name, "d[" + i + "]" };
                }
            }
            else {
                // The article "Proof by Picture: Products and Reciprocals of Diagonal Length Ratios in the Regular Polygon"
                // at http://forumgeom.fau.edu/FG2006volume6/FG200610.pdf uses one-based indexing for the diagonals,
                // with d[1] representing the polygon edge. 
                // I am going to use zero-based indexing with d[0] representing the unit length polygon edge
                // so it corresponds to our coefficients and multiplicationTensor indices.
                // irrationalLabels[0] remains unchanged from the default (blank).
                for(int i = 1; i < irrationalLabels.length; i++) {
                    irrationalLabels[i] = new String[]{ "d" + subscriptString(i), "d[" + i + "]" };
                }
            }
            break;
        }
    }

    /**
     * getUnitTerm(n) expects n < getOrder().
     * This method handles normalized diagonal lengths 
     * where getOrder() <= n < diagonalCount()
     * In these cases, the resulting AlgebraicNumber will not have just the nth term set to 1,
     * but rather, will have the normalized equivalent.
     * For example, since a normalized PolygonField(6) is of order 2, but diagonalCount() == 3,
     *  PolygonField(6).getUnitTerm(2) would return an AlgebraicNumber with terms of {2,0} rather than {0,0,1}.   
     */
    public AlgebraicNumber getUnitDiagonal(int n) {
        if(n >= getOrder() && n < diagonalCount()) {
            int[] terms = this .zero() .toTrailingDivisor(); // makes a copy
            int row = n - getOrder();
            for(int i = 0; i < getOrder(); i++) {
                int term = normalizerMatrix[row][i];
                if ( term != 0 ) {
                    terms[i] = term;
                }
            }
            return createAlgebraicNumberFromTD( terms );
        }
        return super.getUnitTerm(n);
    }

    @Override
    public AlgebraicNumber getGoldenRatio()
    {
        return goldenRatio;
    }

    @Override
    public AlgebraicNumber getNumberByName(String name) {
        switch(name) {
        case "\u221A2": case "root2": case "sqrt2":
            return getRoot2();

        case "\u221A3": case "root3": case "sqrt3":
            return getRoot3();

        case "\u221A5": case "root5": case "sqrt5":
            return super.getNumberByName("root5"); // base class handles this one

        case "\u221A6": case "root6": case "sqrt6":
            return getRoot6();

        case "\u221A7": case "root7": case "sqrt7":
            return getRoot7();

        case "\u221A8": case "root8": case "sqrt8":
            return super.getNumberByName("root8"); // base class handles this one

        case "\u221A10": case "root10": case "sqrt10":
            return getRoot10();

        case "rho":
            return getDiagonalRatio(7);

        case "sigma":
            AlgebraicNumber rho = getDiagonalRatio(7);
            return rho == null ? null : rho.times(rho).minus(one());
        }
        return super.getNumberByName(name);
    }

    private AlgebraicNumber getRoot2() {
        return getDiagonalRatio(4);
    }

    private AlgebraicNumber getRoot3() {
        return getDiagonalRatio(6);
    }

    private AlgebraicNumber getRoot6() {
        AlgebraicNumber r3 = getNumberByName("root3");
        if(r3 != null) { // root3 is null more often so check it first
            AlgebraicNumber r2 = getNumberByName("root2");
            return r2 == null ? null : r2.times(r3);
        }
        return null;
    }

    private AlgebraicNumber getRoot7() {
        if (polygonSides % 14 == 0) {
            // See the drawing at 
            // https://vzome.com/app/embed.py?url=https://raw.githubusercontent.com/david-hall/vzome-sharing/main/2021/10/31/23-45-24-Sqrt7-from-heptagon-cotangent-sums-in-14gon-field/Sqrt7-from-heptagon-cotangent-sums-in-14gon-field.vZome
            //
            // From https://en.wikipedia.org/wiki/Heptagonal_triangle
            // Given lengths a = 1; b = rho, c = sigma (heptagon names)
            // cosines...
            // cos(A) = b/2a
            // cos(B) = c/2b
            // cos(C) = =a/2c
            //
            // Given angles A = π/7; B = 2π/7; C = 4π/7;
            // cotangents (not cosines)
            // cot(A) + cot(B) + cot(C) = root7
            //
            // For a 14-gon:
            //     |  A  |  B  |  C
            // sin | a/f | c/f | e/f
            // cos | d/f | b/f |-1/f   ... uses all even indices so only cos can be done in polygon7
            // tan | a/d | c/b | e/-1
            // csc | f/a | f/c | f/e
            // sec | f/d | f/b | f/-1
            // cot | d/a | b/c |-1/e

            int n = polygonSides / 14;
            AlgebraicNumber d0 = getUnitDiagonal((1*n)-1).negate();
            AlgebraicNumber d1 = getUnitDiagonal((2*n)-1);
            AlgebraicNumber d2 = getUnitDiagonal((3*n)-1);
            AlgebraicNumber d3 = getUnitDiagonal((4*n)-1);
            AlgebraicNumber d4 = getUnitDiagonal((5*n)-1);
            AlgebraicNumber d5 = getUnitDiagonal((6*n)-1);

            AlgebraicNumber cotA = d4.dividedBy(d1); 
            AlgebraicNumber cotB = d2.dividedBy(d3); 
            AlgebraicNumber cotC = d0.dividedBy(d5); 

            return cotA.plus(cotB).plus(cotC); // root7
        }
        return null;
    }

    // the base class handles root8 but if we were to need it here some day...
//    private AlgebraicNumber getRoot8() {
//        AlgebraicNumber n = getRoot2();
//        return n == null ? null : n.times(createRational(2));
//    }

    private AlgebraicNumber getRoot10() {
        AlgebraicNumber r5 = getNumberByName("root5");
        if(r5 != null) { // root5 is null more often so check it first
            AlgebraicNumber r2 = getNumberByName("root2");
            return r2 == null ? null : r2.times(r5);
        }
        return null;
    }

    private AlgebraicNumber getDiagonalRatio(int divisor) {
        // Note that more than one term of these AlgebraicNumbers may be non-zero. 
        // This typically occurs when polygonSides is not prime.
        // We could apply the logic of convertGoldenNumberPairs() here
        // but this also works and they can be used to cross-check each other.
        if (polygonSides % divisor == 0) {
            int n = polygonSides / divisor;
            AlgebraicNumber denominator = getUnitDiagonal(n - 1); 
            AlgebraicNumber numerator = getUnitDiagonal((2 * n) - 1);
            return numerator.dividedBy(denominator);
        }
        return null;
    }
    
    @Override
    protected void initializeCoefficients() {
        double[] temp = getCoefficients();
        for(int i = 0; i < coefficients.length; i++) {
            coefficients[i] = temp[i];
        }
    }

    @Override
    protected void initializeMultiplicationTensor() {
        multiplicationTensor = getNormalizedMultiplicationTensor(polygonSides());
    }

    protected short[][] normalizerMatrix;

    @Override
    protected void initializeNormalizer() {
        normalizerMatrix = getNormalizerMatrix(polygonSides());
    }

    public Integer polygonSides() {
        return polygonSides;
    }

    public final boolean isEven() {
        return isEven;
    }

    public final boolean isOdd() {
        return !isEven;
    }

    //    unicode for double struck alphabet is at https://www.w3.org/TR/xml-entity-names/double-struck.html
    //    
    //    public static final String[] GREEK_ALPHABET = {
    //            "\u03B1", // alpha
    //            "\u03B2", // beta
    //            "\u03B3", // gamma
    //            "\u03B4", // delta
    //            "\u03B5", // epsilon
    //            "\u03B6", // zeta
    //            "\u03B7", // eta
    //            "\u03B8", // theta
    //            "\u03B9", // iota
    //            "\u03BA", // kappa
    //            "\u03BB", // lambda
    //            "\u03BC", // mu
    //            "\u03BD", // nu
    //            "\u03BE", // xi
    //            "\u03BF", // omicron
    //          //  "\u03C0", // pi            // To avoid confusion, don't use pi (3.1415...) as the name of an irrational factor
    //            "\u03C1", // rho
    //          //  "\u03C2", // 'final_sigma' // Not to be confused with the actual lower case letter 'stigma' (with a 't' in it) @ "\u03DB".
    //            "\u03C3", // sigma
    //            "\u03C4", // tau
    //            "\u03C5", // upsilon
    //            "\u03C6", // phi
    //            "\u03C7", // chi
    //            "\u03C8", // psi
    //            "\u03C9", // omega
    //          };
}
