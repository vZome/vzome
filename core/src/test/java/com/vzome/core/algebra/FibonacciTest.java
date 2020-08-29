package com.vzome.core.algebra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BinaryOperator;

import org.junit.Test;

/**
 * @author David Hall
 */
public class FibonacciTest {
    @SafeVarargs
    @SuppressWarnings("varargs")
    public final <T extends Fields.Element<T>> T varableFibonacci(int reps, T... args) {
        List<T> fibs = varableFibonacci(T::plus, reps, args);
        assertEquals(args.length, fibs.size());
        // return the last element in the list
        return fibs.get(fibs.size()-1);
    }

    // Like Fibonacci, but with a variable number of seed terms
    // and a user specified operator
    // An ordered list of the last numbers in the series is returned
    // The list will contain the same number of elements as the number of seed values provided
    @SafeVarargs
    @SuppressWarnings("varargs")
    public final <T extends Fields.Element<T>> List<T> varableFibonacci(BinaryOperator<T> op, int reps, T... args) {
        int nArgs = args.length;
        List<T> terms = Arrays.asList(args);
        List<T> seriesTail = new ArrayList<>(terms);

        T last = null;
        int next = 0;
        int limit = Math.min(reps, nArgs);
        for (int n = 0; n < limit; n++) {
            last = terms.get(n);
            System.out.println((n + 1) + "=\t" + last);
        }
        for (int rep = nArgs; rep < reps; rep++) {
            T accumulator = terms.get(next);
            int idx = next;
            for (int n = 1; n < nArgs; n++) {
                idx = (idx + 1) % nArgs;
                T term = terms.get(idx);
                accumulator = op.apply(accumulator, term);
            }
            last = accumulator;
            terms.set(next, accumulator);
            next = idx;
            System.out.println((rep + 1) + ":\t" + last);
            seriesTail.remove(0);
            seriesTail.add(last);
        }

        return seriesTail;
    }

    // This works with a variety of seed values including BigRational fractions and AlgebraicNumbers
    public final <T extends Fields.Element<T> & Comparable<T>> void testReverseableFibonacciSeries(Class<T> clazz, int reps, T first, T second)
    {
        assertTrue(first.negate().compareTo(first) <= 0); // not negative
        assertTrue(second.negate().compareTo(second) <= 0); // not negative
        assertTrue(first.compareTo(second) <= 0);

        List<T> fibs = varableFibonacci(T::plus, reps, first, second);
        Collections.reverse(fibs);
        @SuppressWarnings("unchecked")
		T[] checked = (T[]) Array.newInstance(clazz, fibs.size());
		T[] reversed = checked;
        fibs.toArray(reversed);
        System.out.println("reversed...");
        fibs = varableFibonacci(T::minus, reps, reversed);
        assertEquals(first, fibs.get(fibs.size()-1));
    }

    @Test
    public void testAlgebraicNumberReverseableFibonacci() {
        int reps = 1000;
        AlgebraicField field = new HeptagonField();
        AlgebraicNumber n1 = field.parseNumber("0 2 1");
        AlgebraicNumber n2 = field.parseNumber("1 3 5");
        testReverseableFibonacciSeries(AlgebraicNumber.class, reps, n1, n2);
    }

    @Test
    public void testBigRationalReverseableFibonacci() {
        int reps = 1000;
        BigRational n1 = new BigRational(17);
        BigRational n2 = new BigRational(29);
        testReverseableFibonacciSeries(BigRational.class, reps, n1, n2);

        n1 = new BigRational(3, 19);
        n2 = new BigRational(7, 13);
        testReverseableFibonacciSeries(BigRational.class, reps, n1, n2);
    }

    @Test
    public void testVariableFibonacci() {
        {
            // The 92nd Fibonacci number is the largest that will fit in a Long
            BigRational fib = varableFibonacci(92, BigRational.ONE, BigRational.ONE);
            assertEquals("7540113804746346429", fib.toString());
            assertTrue(fib.fitsInLong());
            // The 95th negative Fibonacci number is the largest that will fit in a Long
            fib = varableFibonacci(95, BigRational.ONE, BigRational.ONE.negate());
            assertEquals("-7540113804746346429", fib.toString());
            assertTrue(fib.fitsInLong());
        }
        {
            // TODO: Try various operators
            varableFibonacci(BigRational::dividedBy, 12, new BigRational("23/-31"), new BigRational("-51/-7"), new BigRational("-11/13"), new BigRational("17/19"));
            varableFibonacci(BigRational::times, 12, new BigRational("23/-31"), new BigRational("-51/-7"), new BigRational("-11/13"), new BigRational("17/19"));
            varableFibonacci(BigRational::plus, 12, new BigRational("23/-31"), new BigRational("-51/-7"), new BigRational("-11/13"), new BigRational("17/19"));
            varableFibonacci(BigRational::minus, 12, new BigRational("23/-31"), new BigRational("-51/-7"), new BigRational("-11/13"), new BigRational("17/19"));
        }
        {
            // 100000th Tribonacci number throws OutOfMemoryError
            //  10000th  Fibonacci number is calculated in just a few seconds
            BigRational fib = varableFibonacci(10000, BigRational.ONE, BigRational.ONE);
            assertFalse(fib.fitsInLong());
            assertEquals(
                    "33644764876431783266621612005107543310302148460680063906564769974680081442166662368155595513633734025582065332680836159373734790483865268263040892463056431887354544369559827491606602099884183933864652731300088830269235673613135117579297437854413752130520504347701602264758318906527890855154366159582987279682987510631200575428783453215515103870818298969791613127856265033195487140214287532698187962046936097879900350962302291026368131493195275630227837628441540360584402572114334961180023091208287046088923962328835461505776583271252546093591128203925285393434620904245248929403901706233888991085841065183173360437470737908552631764325733993712871937587746897479926305837065742830161637408969178426378624212835258112820516370298089332099905707920064367426202389783111470054074998459250360633560933883831923386783056136435351892133279732908133732642652633989763922723407882928177953580570993691049175470808931841056146322338217465637321248226383092103297701648054726243842374862411453093812206564914032751086643394517512161526545361333111314042436854805106765843493523836959653428071768775328348234345557366719731392746273629108210679280784718035329131176778924659089938635459327894523777674406192240337638674004021330343297496902028328145933418826817683893072003634795623117103101291953169794607632737589253530772552375943788434504067715555779056450443016640119462580972216729758615026968443146952034614932291105970676243268515992834709891284706740862008587135016260312071903172086094081298321581077282076353186624611278245537208532365305775956430072517744315051539600905168603220349163222640885248852433158051534849622434848299380905070483482449327453732624567755879089187190803662058009594743150052402532709746995318770724376825907419939632265984147498193609285223945039707165443156421328157688908058783183404917434556270520223564846495196112460268313970975069382648706613264507665074611512677522748621598642530711298441182622661057163515069260029861704945425047491378115154139941550671256271197133252763631939606902895650288268608362241082050562430701794976171121233066073310059947366875"
                    , fib.toString());
        }
        {
            // BigRational originally choked on the 93rd Fibonacci number
            // it returned -6246583658587674878 instead of 12200160415121876738
            BigRational fib = varableFibonacci(93, BigRational.ONE, BigRational.ONE);
            assertTrue(fib.isPositive());
            assertEquals("12200160415121876738", fib.toString());
        }
    }

    @Test
    public void testVaribonacciWithUnitTerms() {
        AlgebraicField[] fields = new AlgebraicField[]{
            new PentagonField(),
            new RootTwoField(),
            new RootThreeField(),
            new HeptagonField(),
            new SnubDodecField()
        };

        for (AlgebraicField field : fields) {
        	String fieldType = field.getClass().getSimpleName() + ": ";
            List<AlgebraicNumber> list = new ArrayList<>();
            for (int i = 0; i < field.getOrder(); i++) {
            	BigRational[] factors = ((AlgebraicNumberImpl) field.createRational(0)) .getFactors();
                factors[i] = new BigRational(1);
                list.add(field.createAlgebraicNumber(factors));
            }

            // Using the original BigRational class...
            // PentagonField  failed at 94
            // RootTwoField   failed at 94
            // RootThreeField failed at 94
            // HeptagonField  failed at 76
            // SnubDodecField failed at 71
            // With the new implementation, they all work at 200 reps
            int reps = 200;
            for (int r = 1; r <= reps; r++) {
                AlgebraicNumber[] array = list.toArray(new AlgebraicNumber[list.size()]);
                AlgebraicNumberImpl fib = (AlgebraicNumberImpl) varableFibonacci(r, array);
                for (BigRational factor : fib.getFactors()) {
                	String msg = fieldType + r;
                    assertFalse(msg, factor.isNegative());
                }
            }
        }
    }

}
