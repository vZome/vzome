
package com.vzome.core.construction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicNumberImpl;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.BigRational;
import com.vzome.core.algebra.HeptagonField;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.algebra.RootThreeField;
import com.vzome.core.algebra.RootTwoField;
import com.vzome.core.algebra.SnubDodecField;
import com.vzome.core.math.Projection;
import com.vzome.core.math.QuaternionProjection;
import com.vzome.core.math.TetrahedralProjection;

public class VefToModelTest
{
    @Test
    public void testRationalField() {
        final Projection projection = null;
        final NewConstructions effects = new NewConstructions();
        final AlgebraicVector offset = null;
        // Verify that a "rational" field can be imported into all types of AlgebraicField
        final String vefData = "vZome VEF 7 field rational "
                + "actual scale 1 "
                + "3 "
                + "0 0 0 0 "
                + "0 1 2 3 "
                + "4 5 6 7 "
                ;
        
        final AlgebraicField[] fields = { 
        	new PentagonField(), 
            new RootTwoField(), 
            new RootThreeField(), 
            new HeptagonField(), 
            new SnubDodecField(),
        };

        int testsPassed = 0;
        try {
            for(AlgebraicField field : fields ) {
                effects.clear();
                AlgebraicNumber scale = field.createPower(0);
                assertTrue(scale.isOne());
                VefToModel parser = new VefToModel(projection, effects, scale, offset);
                parser.parseVEF(vefData, field);
                assertEquals(effects.size(), 3);
                testsPassed++;
            }
        }
        catch(Exception ex) {
            fail(ex.toString());
        }
        assertEquals(testsPassed, 5);
    }

    @Test
    public void testScaling() {
        final Projection projection = null;
        final NewConstructions effects = new NewConstructions();
        final AlgebraicVector offset = null;
        final AlgebraicField field = new RootTwoField();

        // first we test with a unit scale factor
        VefToModel parser = new VefToModel(projection, effects, field .one(), offset);

        String vefData = "vZome VEF 7 field rootTwo "
                + "scale 1 "
                + "3 "
                + "0 0 0 0 "
                + "0 1 2 3 "
                + "4 5 6 7 "
                ;

        AlgebraicVector expected = field.createVector(new int[][] {
            {1,1, 0,1},  
            {2,1, 0,1},  
            {3,1, 0,1}
        });
        parser.parseVEF(vefData, field);
        Point p0 = (Point) effects.get(1);
        AlgebraicVector v0 = p0.getLocation();
        assertEquals(expected, v0);

        // The scale in vefData is changed to 3, so we multiply our expected value by 3 as well.
        vefData = vefData.replace("scale 1 ", "scale 3 ");
        expected = expected.scale(field.createRational(3));
        effects.clear();
        parser.parseVEF(vefData, field);
        p0 = (Point) effects.get(1);
        v0 = p0.getLocation();
        assertEquals(expected, v0);

        AlgebraicNumber scale = field.createPower(6); // rootTwo^6 = 2^3 = 8
        assertEquals(scale, field.createRational(8));
        parser = new VefToModel(projection, effects, scale, offset);
        
        // Now we'll create a parser with a scale factor,
        // so we must also multiply our expected value by scale, which is 8.
        expected = expected.scale(scale);
        effects.clear();
        parser.parseVEF(vefData, field);
        p0 = (Point) effects.get(1);
        v0 = p0.getLocation();
        assertEquals(expected, v0);

        // just to be sure we ended up where we expected to be...
        assertEquals(expected, field.createVector(new int[][] {
            {3*8*1,1, 0,1},
            {3*8*2,1, 0,1},
            {3*8*3,1, 0,1}
        }));
    }

    @Test
    public void testScalingByVector() {
        final Projection projection = null;
        final NewConstructions effects = new NewConstructions();
        final AlgebraicVector offset = null;
        final AlgebraicField field = new RootThreeField();

        AlgebraicNumber scale = field.one();

        VefToModel parser = new VefToModel(projection, effects, scale, offset);

        String vefData = "vZome VEF 8 field rootThree "
                + "scale 1 "
                + "3 "
                + "0 0 0 0 "
                + "0 1 2 3 "
                + "4 5 6 7 "
                ;

        AlgebraicVector expected = field.createVector(new int[][] {
            {5,1, 0,1},
            {6,1, 0,1},
            {7,1, 0,1}
        });
        parser.parseVEF(vefData, field);
        Point p0 = (Point) effects.get(2);
        AlgebraicVector v0 = p0.getLocation();
        assertEquals(expected, v0);

        // The scale parameter passed to parser is still being ignored
        // because of the keyword "actual" in vefData
        // but the scale in vefData is changed to scaleVector, so we multiply each component of our expected value as necessary.
        vefData = vefData.replace("scale 1 ", "scale vector 0 1/2 (0,1/4) 4/10 ");
        expected = field.createVector(new int[][] {
        	{ 5,2, 0,1},     // 5 * 1/2 = 5/2
        	{ 3,2, 0,1},     // 6 * 1/4 = 3/2
            {14,5, 0,1}      // 7 * 4/10 = 14/5
        });

        effects.clear();
        parser.parseVEF(vefData, field);
        p0 = (Point) effects.get(2);
        v0 = p0.getLocation();
        assertEquals(expected, v0);

    }

    @Test
    public void testParseOffset() {
        final Projection projection = null;
        final NewConstructions effects = new NewConstructions();
        final AlgebraicVector offset = null;
        final AlgebraicField field = new RootThreeField();

        AlgebraicNumber scale = field.one();

        VefToModel parser = new VefToModel(projection, effects, scale, offset);

        String vefData = "vZome VEF 10 field rootThree "
                + "offset 10 -20 30 -40 "
                + "3 "
                + "0 0 0 0 "
                + "0 1 2 3 "
                + "4 5 6 7 "
                ;

        AlgebraicVector expected = field.createVector(new int[][] {
            {5-20,1, 0,1},
            {6+30,1, 0,1},
            {7-40,1, 0,1}
        });
        parser.parseVEF(vefData, field);
        Point p0 = (Point) effects.get(2);
        AlgebraicVector v0 = p0.getLocation();
        assertEquals(expected, v0);
    }

    @Test
    public void testMixedVectorFormat()
    {
        final Projection projection = null;
        final NewConstructions effects = new NewConstructions();
        final AlgebraicVector offset = null;
        final AlgebraicField[] fields = {
        	new PentagonField(),
            new RootTwoField(),
            new RootThreeField(),
            new HeptagonField(),
            new SnubDodecField(),
       };

        int testsPassed = 0;
        // Verify that a mix of integer and rational formatted values can be parsed
        final String vefData = "vZome VEF 7 field rational "
                + "actual scale 1 "
                + "3 "
                + "0 -2 3 4 " // all integers
                + "0 123/456 -7/-8 -9/10 " // all rational
                + "0/1 0/9999 5/5 12/-6 " // some unreduced fractions
                ;

        try {
            for(AlgebraicField field : fields ) {
                effects.clear();

                AlgebraicVector expected[] = new AlgebraicVector[] {
                    new AlgebraicVector( field.createRational(-2),      field.createRational(3),    field.createRational(4)    ),
                    new AlgebraicVector( field.createRational(123,456), field.createRational(7,8),  field.createRational(-9,10) ),
                    new AlgebraicVector( field.createRational(0),       field.createRational(1),    field.createRational(-2) ),
                };
                AlgebraicNumber scale = field.createRational(1);
                VefToModel parser = new VefToModel(projection, effects, scale, offset);
                parser.parseVEF(vefData, field);
                assertEquals(effects.size(), expected.length);
                for( int i = 0; i < effects.size(); i++) {
                    Point p1 = (Point) effects.get(i);
                    AlgebraicVector v1 = p1.getLocation();
                    assertEquals( expected[i], v1 ); // the expected value was parsed
                    // now be sure the irrational elements are all zero and not null
                    for(int dim = 0; dim < v1.dimension(); dim++) {
                        AlgebraicNumberImpl n1 = (AlgebraicNumberImpl) v1.getComponent(dim);
                        BigRational[] factors = n1.getFactors();
                        assertEquals( factors.length, field.getOrder() );
                        for( int f = 1; f < factors.length; f++ ) {
                            assertTrue(factors[f] != null);
                            assertTrue(factors[f].isZero());
                        }
                    }
                    testsPassed++;
                }
            }
        }
        catch(Exception ex) {
            fail(ex.toString());
        }
        assertEquals(testsPassed, 3 * fields.length );
        assertTrue( testsPassed > 0 );
    }

    @Test
    public void testZeroFillHigherOrderIrrationals() {
        // Tests that an order N formatted number can be imported into a field of order > N
        // with the resulting irrational factors automatically zero filled.
        // TODO: Confirm that specifying too many factors for a field will generate an error.

        final Projection projection = null;
        final NewConstructions effects = new NewConstructions();
        final AlgebraicVector offset = null;
        final AlgebraicField[] fields = {
            new PentagonField(),                    // order 2
            new RootTwoField(),                     // order 2
            new RootThreeField(),                   // order 2
            new HeptagonField(),                    // order 3
            new SnubDodecField(),                   // order 6
       };

        int testsPassed = 0;
        // Verify that a mix of integer and order 2 irrational formatted values can be parsed by all fields of order > 2
        final String vefData = "vZome VEF 7 field $$$ "
                + "actual scale 1 "
                + "1 "
                + "0 (-2,3) (4,5) 6/7" // mix of order 2 and rational
                // VEF format has one rational factor on the right and all irrationals on the left
                ;

        try {
            for(AlgebraicField field : fields ) {
                effects.clear();
                AlgebraicVector expected[] = new AlgebraicVector[] {
                    new AlgebraicVector(
                            field.createRational( 3 ).plus( field.createPower(1).times(field.createRational(-2 )) ),
                            field.createRational( 5 ).plus( field.createPower(1).times(field.createRational( 4 )) ),
                            field.createRational( 6, 7 )
                    )
                };
                AlgebraicNumber scale = field.createRational(1);
                VefToModel parser = new VefToModel(projection, effects, scale, offset);
                parser.parseVEF(vefData.replace("$$$", field.getName()), field);
                assertEquals(effects.size(), expected.length);
                for( int i = 0; i < effects.size(); i++) {
                    Point p1 = (Point) effects.get(i);
                    AlgebraicVector v1 = p1.getLocation();
                    assertEquals( expected[i], v1 ); // the expected value was parsed
                    // now be sure the irrational elements are not null
                    for(int dim = 0; dim < v1.dimension(); dim++) {
                        AlgebraicNumberImpl n1 = (AlgebraicNumberImpl) v1.getComponent(dim);
                        BigRational[] factors = n1.getFactors();
                        assertEquals( factors.length, field.getOrder() );
                        for( int f = 0; f < factors.length; f++ ) {
                            assertTrue(factors[f] != null);
                            // rational factor is at f=0, 1st irrational is at f=1, all other factors should be set to 0.
                            if(f >= 2) {
                                // be sure the irrational elements > order 2 are zero
                                assertTrue(factors[f].isZero());
                            }
                        }
                    }
                    testsPassed++;
                }
            }
        }
        catch(Exception ex) {
            fail(ex.toString());
        }
        assertEquals( testsPassed, fields.length );
        assertTrue( testsPassed > 0 );
    }

    @Test
    public void testParse()
    {
        AlgebraicField field = new RootTwoField();
        AlgebraicVector quaternion = field .createVector( new int[][]{ {2,1, 1,1}, {2,1, 1,1}, {2,1, 1,1}, {2,1, 1,1} } );
        Projection projection = new QuaternionProjection(field, null, quaternion);
        NewConstructions effects = new NewConstructions();
        String vefData = "64 (-1,1) (0,1) (0,1) (0,1) (1,-1) (0,1) (0,1) (0,1) " +
               "(0,1) (-1,1) (0,1) (0,1) (0,1) (1,-1) (0,1) (0,1) (0,1) " +
               "(0,1) (-1,1) (0,1) (0,1) (0,1) (1,-1) (0,1) (0,1) (0,1) " +
               "(0,1) (-1,1) (0,1) (0,1) (0,1) (1,-1) (0,-1) (-1,1) (0,1) " +
               "(0,1) (0,-1) (1,-1) (0,1) (0,1) (0,-1) (0,1) (-1,1) (0,1) " +
               "(0,-1) (0,1) (1,-1) (0,1) (0,-1) (0,1) (0,1) (-1,1) (0,-1) " +
               "(0,1) (0,1) (1,-1) (-1,1) (0,-1) (0,1) (0,1) (1,-1) (0,-1) " +
               "(0,1) (0,1) (0,1) (0,-1) (-1,1) (0,1) (0,1) (0,-1) (1,-1) " +
               "(0,1) (0,1) (0,-1) (0,1) (-1,1) (0,1) (0,-1) (0,1) (1,-1) " +
               "(0,-1) (0,-1) (-1,1) (0,1) (0,-1) (0,-1) (1,-1) (0,1) (0,-1) " +
               "(0,-1) (0,1) (-1,1) (0,-1) (0,-1) (0,1) (1,-1) (-1,1) (0,1) " +
               "(0,-1) (0,1) (1,-1) (0,1) (0,-1) (0,1) (0,1) (-1,1) (0,-1) " +
               "(0,1) (0,1) (1,-1) (0,-1) (0,1) (0,1) (0,1) (0,-1) (-1,1) " +
               "(0,1) (0,1) (0,-1) (1,-1) (0,-1) (-1,1) (0,-1) (0,1) (0,-1) " +
               "(1,-1) (0,-1) (0,1) (0,-1) (0,1) (0,-1) (-1,1) (0,-1) (0,1) " +
               "(0,-1) (1,-1) (-1,1) (0,-1) (0,-1) (0,1) (1,-1) (0,-1) (0,-1) " +
               "(0,1) (0,1) (0,-1) (0,-1) (-1,1) (0,1) (0,-1) (0,-1) (1,-1) " +
               "(0,-1) (0,-1) (0,-1) (-1,1) (0,-1) (0,-1) (0,-1) (1,-1) " +
               "(-1,1) (0,1) (0,1) (0,-1) (1,-1) (0,1) (0,1) (0,-1) (0,1) " +
               "(-1,1) (0,1) (0,-1) (0,1) (1,-1) (0,1) (0,-1) (0,1) (0,1) " +
               "(-1,1) (0,-1) (0,1) (0,1) (1,-1) (0,-1) (0,-1) (-1,1) (0,1) " +
               "(0,-1) (0,-1) (1,-1) (0,1) (0,-1) (0,-1) (0,1) (-1,1) (0,-1) " +
               "(0,-1) (0,1) (1,-1) (0,-1) (-1,1) (0,-1) (0,1) (0,-1) (1,-1) " +
               "(0,-1) (0,1) (0,-1) (0,1) (0,-1) (-1,1) (0,-1) (0,1) (0,-1) " +
               "(1,-1) (0,-1) (0,-1) (0,-1) (-1,1) (0,-1) (0,-1) (0,-1) " +
               "(1,-1) (0,-1) (-1,1) (0,1) (0,-1) (0,-1) (1,-1) (0,1) (0,-1) " +
               "(0,-1) (0,1) (-1,1) (0,-1) (0,-1) (0,1) (1,-1) (0,-1) (0,-1) " +
               "(0,-1) (-1,1) (0,-1) (0,-1) (0,-1) (1,-1) (0,-1) (0,-1) " +
               "(-1,1) (0,-1) (0,-1) (0,-1) (1,-1) (0,-1) (0,-1) (0,-1)";

        VefToModel parser = new VefToModel( projection, effects, field .createPower( 5 ), null );
        // Since all of the terms of this particular quaternion are equal, 
        // calling inflateTo4d() will have no effect on it, 
        // so inflateTo4d() will be tested elsewhere rather than modifying this legacy test case 
        parser .parseVEF( vefData, field );
        
        Point p0 = (Point) effects .get( 20 );
        AlgebraicVector v0 = p0 .getLocation();
        AlgebraicVector expected = field .createVector( new int[][]{ {0,1, -8,1}, {-32,1, -24,1}, {0,1, 8,1} } );
        assertEquals( expected, v0 );
        
        Point p1 = (Point) effects .get( 39 );
        AlgebraicVector v1 = p1 .getLocation();
        expected = field .createVector( new int[][]{ {0,1, 8,1}, {-32,1, -24,1}, {0,1, -8,1} } );
        assertEquals( expected, v1 );
    }

    @Test 
    public void testTetrahedralProjection() { 
        final AlgebraicField field = new RootTwoField(); 
        final String header = "vZome VEF 6 field rootTwo "; 
        final String vefData = "2 " 
                + "(0,-1) (0,-1) (-1,1) (0,1) " 
                + "(0,-1) (0,-1) (0,-1) (1,-1) "; 
        // VEF format has one rational factor on the right and all irrationals on the left 
        Projection projection = new TetrahedralProjection(field); 
        NewConstructions effects = new NewConstructions(); 
        VefToModel parser = new VefToModel(projection, effects, field.createPower(5), null); 
        { 
            effects.clear(); 
            parser.parseVEF(vefData, field); 
 
            Point p0 = (Point) effects.get(0); 
            AlgebraicVector v0 = p0.getLocation(); 
            AlgebraicVector expected = field.createVector(new int[][] {{8,1,0,1}, {8,1,0,1}, {-8,1,16,1}}); 
            assertEquals(expected, v0); 
 
            Point p1 = (Point) effects.get(1); 
            AlgebraicVector v1 = p1.getLocation(); 
            expected = field.createVector(new int[][] {{8,1,0,1}, {8,1,0,1}, {8,1,0,1}}); 
            assertEquals(expected, v1); 
        } 
        { 
            // Now we prefix VEFData with a VEF header so that wFirst is true, then reparse 
            effects.clear(); 
            parser.parseVEF(header + vefData, field); 
 
            Point p0 = (Point) effects.get(0); 
            AlgebraicVector v0 = p0.getLocation(); 
            AlgebraicVector expected = field.createVector(new int[][] {{8,1,-16,1}, {-8,1,0,1}, {8,1,0,1}}); 
            assertEquals(expected, v0); 
 
            Point p1 = (Point) effects.get(1); 
            AlgebraicVector v1 = p1.getLocation(); 
            expected = field.createVector(new int[][] {{-8,1,0,1}, {-8,1,0,1}, {8,1,0,1}}); 
            assertEquals(expected, v1); 
        } 
    } 
 
    @Test 
    public void testQuaternionProjection() 
    { 
        final AlgebraicField field = new RootTwoField(); 
        final String header = "vZome VEF 6 field rootTwo "; 
        final String vefData = "2 " + 
                "(0,-1) (0,-1) (-1,1) (0,1) " + 
                "(0,-1) (0,-1) (0,-1) (1,-1) "; 
        // VEF format has one rational factor on the right and all irrationals on the left 
 
        // First of all, parse with no quaternion 
        { 
            // The VEF data is in the old headerless format (wFirst is false), 
            NewConstructions effects = new NewConstructions(); 
            AlgebraicNumber scale = field .createPower( 5 ); 
            VefToModel parser = new VefToModel( null, effects, scale, null ); 
            parser .parseVEF( vefData, field ); 
 
            Point p0 = (Point) effects .get( 0 ); 
            AlgebraicVector v0 = p0 .getLocation(); 
            AlgebraicVector expected = field .createVector( new int[][] {{-1,1,0,1}, {-1,1,0,1}, {1,1,-1,1}} ).scale(scale); 
            assertEquals( expected, v0 ); 
 
            Point p1 = (Point) effects .get( 1 ); 
            AlgebraicVector v1 = p1 .getLocation(); 
            expected = field .createVector( new int[][] {{-1,1,0,1}, {-1,1,0,1}, {-1,1,0,1}} ).scale(scale); 
            assertEquals( expected, v1 ); 
        } 
        { 
            // Now we prefix VEFData with a VEF header so that wFirst is true, then reparse 
            // that means we need a fresh projection and hence a fresh parser and effects 
            NewConstructions effects = new NewConstructions(); 
            AlgebraicNumber scale = field .createPower( 5 ); 
            VefToModel parser = new VefToModel( null, effects, scale, null ); 
            parser .parseVEF( header + vefData, field ); 
 
            Point p0 = (Point) effects .get( 0 ); 
            AlgebraicVector v0 = p0 .getLocation(); 
            AlgebraicVector expected = field .createVector( new int[][] {{-1,1,0,1}, {1,1,-1,1}, {1,1,0,1}} ).scale(scale); 
            assertEquals( expected, v0 ); 
 
            Point p1 = (Point) effects .get( 1 ); 
            AlgebraicVector v1 = p1 .getLocation(); 
            expected = field .createVector( new int[][] {{-1,1,0,1}, {-1,1,0,1}, {-1,1,1,1}} ).scale(scale); 
            assertEquals( expected, v1 ); 
        } 
 
        // The VEF data is in the old headerless format (wFirst is false), 
        // so be sure to use a quaternion that does not have the same value in all 4 places 
        // to ensure that the wFirst logic gets tested here. 
        AlgebraicVector quaternion = field .createVector( new int[][] {{2,1,0,1}, {2,1,1,1}, {2,1,2,1}, {2,1,3,1}} ); 
        Assert.assertNotEquals(quaternion.inflateTo4d(true), quaternion.inflateTo4d(false)); 
 
        { 
            Projection projection = new QuaternionProjection(field, null, quaternion ); 
            NewConstructions effects = new NewConstructions(); 
            VefToModel parser = new VefToModel( projection, effects, field .createPower( 5 ), null ); 
            parser .parseVEF( vefData, field ); 
 
            Point p0 = (Point) effects .get( 0 ); 
            AlgebraicVector v0 = p0 .getLocation(); 
            AlgebraicVector expected = field .createVector( new int[][] {{0,1,8,1}, {-64,1,-16,1}, {-16,1,8,1}} ); 
            assertEquals( expected, v0 ); 
 
            Point p1 = (Point) effects .get( 1 ); 
            AlgebraicVector v1 = p1 .getLocation(); 
            expected = field .createVector( new int[][] {{16,1,16,1}, {-16,1,-40,1}, {-32,1,0,1}} ); 
            assertEquals( expected, v1 ); 
        } 
        { 
            // Now we prefix VEFData with a VEF header so that wFirst is true, then reparse 
            // that means we need a fresh projection and hence a fresh parser and effects 
            Projection projection = new QuaternionProjection(field, null, quaternion ); 
            NewConstructions effects = new NewConstructions(); 
            VefToModel parser = new VefToModel( projection, effects, field .createPower( 5 ), null ); 
            parser .parseVEF( header + vefData, field ); 
 
            Point p0 = (Point) effects .get( 0 ); 
            AlgebraicVector v0 = p0 .getLocation(); 
            AlgebraicVector expected = field .createVector( new int[][] {{-16,1,-40,1}, {0,1,16,1}, {-32,1,-8,1}} ); 
            assertEquals( expected, v0 ); 
 
            Point p1 = (Point) effects .get( 1 ); 
            AlgebraicVector v1 = p1 .getLocation(); 
            expected = field .createVector( new int[][] {{-32,1,-32,1}, {16,1,-8,1}, {-16,1,-16,1}} ); 
            assertEquals( expected, v1 ); 
        } 
    } 
 
    @Test
    public void testVefParseBigInteger()
    {
        AlgebraicField field = new HeptagonField();
        Projection projection = null;
        NewConstructions effects = new NewConstructions();
        String vefData = "vZome VEF 6 field heptagon " +
            "2 " +
            "(0,0,0) (@,#,$) (-@,-#7$,-$) (-173023461584739690778423250124917/3141337203685477234578,2,3) " +
            "(0,0,0) (-524489935216464,1178518186786496,-945098206316384) (320676541831796,-720553648886699,577839161714113) (-196063713670231,440551165646117,-353294604284427) ";

        // be sure we're parsing a number that is bigger than a Long so it requires a BigInteger to hold it.
        // check both positive and negative values as well as fractions with BigInteger numerators and denominators
        Long pos = Long.MAX_VALUE;
        BigInteger a = new BigInteger(pos.toString()); // 9223372036854775807
        BigInteger b = a.add(a); // 18446744073709551614
        BigInteger c = a.multiply(a); // 85070591730234615847396907784232501249

        vefData = vefData.replace("@", a.toString());
        vefData = vefData.replace("#", b.toString());
        vefData = vefData.replace("$", c.toString());

        VefToModel parser = new VefToModel( projection, effects, field.one(), null );
        parser .parseVEF( vefData, field );

        Point p0 = (Point) effects .get( 0 );
        AlgebraicVector v0 = p0 .getLocation();
        assertNotNull( v0 );
//        System.out.println("v0=" + v0.toString());

        Point p1 = (Point) effects .get( 1 );
        AlgebraicVector v1 = p1 .getLocation();
        assertNotNull( v1 );
//        System.out.println("v1=" + v1.toString());
    }

    @Test
    public void testParseSubField() {
        final Projection projection = null;
        final NewConstructions effects = new NewConstructions();
        final AlgebraicVector offset = null;
        final String vefData = "vZome VEF 7 field golden " +
                "actual scale 1 " +
                "1 " +
                "(0,0) (2,1) (4,3) (6,5) " ;
        
        final AlgebraicField[] fields = { 
        	new PentagonField(), 
            new SnubDodecField(),
        };

        int[] terms = new int[] {2, 3};
        double r = new RootTwoField(). createAlgebraicNumber( terms ).evaluate();
        double p = new PentagonField(). createAlgebraicNumber( terms ).evaluate();
        double d = new SnubDodecField().createAlgebraicNumber( terms ).evaluate();
        assertEquals(p, d, 0.0D);
        assertFalse(p == r);

        int testsPassed = 0;
        final int[][] factors = new int[][] { {1,1, 2,1}, {3,1, 4,1}, {5,1, 6,1} };
        for(AlgebraicField field : fields ) {
            effects.clear();
            AlgebraicNumber scale = field.createPower(0);
            assertTrue(scale.isOne());
            VefToModel parser = new VefToModel(projection, effects, scale, offset);
            parser.parseVEF(vefData, field);
            assertEquals(1, effects.size());
            AlgebraicVector v = field.createVector(factors);
            AlgebraicVector v0 = ((FreePoint) effects.get(0)).getLocation();
            assertEquals(v, v0);
            assertEquals(field, v.getField());
            assertEquals(field, v0.getField());
            testsPassed++;
        }
        assertEquals(fields.length, testsPassed);
    }
    
    private static class NewConstructions extends ArrayList<Construction> implements ConstructionChanges
    {
        private static final long serialVersionUID = 1L;

        @Override
        public void constructionAdded( Construction c )
        {
            add( c );
        }

        @Override
        public void constructionAdded( Construction c, Color color )
        {
            add( c );
        }
    }
}
