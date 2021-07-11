package com.vzome.core.algebra;

import static java.lang.Math.PI;
import static java.lang.Math.sin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author David Hall
 */
public class PolygonField extends ParameterizedField<Integer>
{
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

        final int nEquations = primeFactors.stream().mapToInt(prime -> nDiags/prime).sum();

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
    private final AlgebraicNumber goldenDenominator;
    private final AlgebraicNumber goldenNumerator;

    /*
     * Much of the class initialization is done by having member methods call comparable static methods
     * for various steps in the process. This is a bit unususal and isn't necessary at all. 
     * I implemented it that way as I was developing this class 
     * so that I could independently test the math in individual steps of the process.
     * Being able to invoke each step independently from within unit tests 
     * also helps with documenting and explaining the logic that makes this class tick 
     * so I'm going to leave them as public static methods.
     */

    public PolygonField( int polygonSides, AlgebraicNumberFactory factory ) {
        this( FIELD_PREFIX + polygonSides, polygonSides, factory );
    }

    // this protected c'tor is intended to allow PentagonField and HeptagonField classes to be refactored
    // so they are derived from PolygonField and still maintain their original legacy names
    protected PolygonField(String name, int polygonSides, AlgebraicNumberFactory factory ) {
        super( name, getOrder(polygonSides), polygonSides, factory );
        isEven = polygonSides % 2 == 0;
        final boolean isGolden = polygonSides % 5 == 0;
        if (isGolden) {
            int n = polygonSides / 5;
            // Note that more than one term of these AlgebraicNumbers may be non-zero, 
            // especially when polygonSides is not prime (e.g. a multiple of 5 that's greater than 5)
            goldenDenominator = getUnitDiagonal(n - 1); 
            goldenNumerator = getUnitDiagonal((2 * n) - 1);
            goldenRatio = goldenNumerator.dividedBy(goldenDenominator);
        } else {
            goldenDenominator = goldenNumerator = goldenRatio = null;
        }
    }
    
    // SV: This pattern is problematic for decoupling from BigRational.
    //     It also represents a tight coupling between AAF and ANI, which is bad.
    //     The correct pattern is to deal with this problem when parsing VEF, rather than inside the ANI constructor.

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

    @Override
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
    public void defineMultiplier(StringBuffer buf, int i) {
        // This is used by the POVRayExporter. 
        if(i > 0 && i < coefficients.length) {
            String name = getIrrational(i, EXPRESSION_FORMAT);
            // reformat generated irrational names like d[1] to look like d_1
            buf.append( name.replace( "[", "_" ).replace( "]", "" ) )
            .append(" = ")
            .append(coefficients[i]);
        }
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
        return operand;
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
