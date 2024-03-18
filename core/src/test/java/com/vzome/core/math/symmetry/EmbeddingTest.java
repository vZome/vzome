package com.vzome.core.math.symmetry;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import org.junit.Test;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicNumberImpl;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.HeptagonField;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.algebra.PolygonField;
import com.vzome.core.algebra.PolygonFieldTest;
import com.vzome.core.algebra.RootThreeField;
import com.vzome.core.generic.Utilities;
import com.vzome.core.math.RealVector;
import com.vzome.fields.heptagon.HeptagonalAntiprismSymmetry;
import com.vzome.fields.heptagon.TriangularAntiprismSymmetry;
import com.vzome.fields.sqrtphi.PentagonalAntiprismSymmetry;
import com.vzome.fields.sqrtphi.SqrtPhiField;

public class EmbeddingTest {
	public static final int MAX_SIDES = PolygonFieldTest.MAX_SIDES;
	
    @Test
    public void testPolygonFieldAntiprismEmbedding() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        for(int nSides = PolygonField.MIN_SIDES; nSides <= MAX_SIDES; nSides++) {
        	PolygonField field = new PolygonField(nSides, AlgebraicNumberImpl.FACTORY);
        	Symmetry symmetry = new AntiprismSymmetry(field);
        	assertEquals(field.isEven(), symmetry.isTrivial());
        	verfyEmbedding(symmetry);
        }
    }
    
    @Test
    public void testOtherEmbeddings() {
        System.out.println(new Throwable().getStackTrace()[0].getMethodName() + " " + Utilities.thisSourceCodeLine());
        final HeptagonField hf = new HeptagonField();
        Symmetry[] nonTrivialSymmetries = {
        	new HeptagonalAntiprismSymmetry(hf, "blue", false),
       		new HeptagonalAntiprismSymmetry(hf, "blue", true ),       		
       		new TriangularAntiprismSymmetry(hf, "blue" ),       		
        };
        final PentagonField pf = new PentagonField();
        Symmetry[] trivialSymmetries = {
          	new OctahedralSymmetry(pf),
       		new IcosahedralSymmetry(pf),		
       		new DodecagonalSymmetry(new RootThreeField()),		
       		new PentagonalAntiprismSymmetry(new SqrtPhiField(AlgebraicNumberImpl.FACTORY), null),
        };
        // set up for initial test
        int nTests = 0;
        Symmetry[] testSymmetries = nonTrivialSymmetries;
        boolean expectTrivial = false;
        do {
	        for(Symmetry symmetry : testSymmetries) {
	        	assertEquals(expectTrivial, symmetry.isTrivial());
	        	verfyEmbedding(symmetry);
	        	nTests++;
	        }
	        // set up for next test
	        testSymmetries = trivialSymmetries;
	        expectTrivial = ! expectTrivial;
        } while(expectTrivial);
        
        assertTrue("test at least some symmetries", nTests > 0);
        assertEquals("test all symmetries", nonTrivialSymmetries.length + trivialSymmetries.length, nTests);
    }
    
    private void verfyEmbedding(Symmetry symmetry) {
    	final Embedding embedding = symmetry;
    	final AlgebraicField field = symmetry.getField();
    	AlgebraicVector v = getIdentityVector(field);
        final String fieldName = field.getName();
        RealVector embedded = embedding.embedInR3(v);
        double[] embeddedDouble = embedding.embedInR3Double(v);
        if(embedding.isTrivial()) {
            assertEquals(fieldName, 1f, embedded.x);
            assertEquals(fieldName, 1f, embedded.y);
            assertEquals(fieldName, 1f, embedded.z);
            assertEquals(fieldName, 1d, embeddedDouble[0]);
            assertEquals(fieldName, 1d, embeddedDouble[1]);
            assertEquals(fieldName, 1d, embeddedDouble[2]);
        } else {
        	String msg = fieldName + ": At least one dimension should have changed if this is not a trivial embedding.";
        	assertTrue(msg, 
    			(1f != embedded.x) || 
    			(1f != embedded.y) || 
    			(1f != embedded.z) );
        	msg = fieldName + ": embedInR3() and embedInR3Double() should return the same values up to precision.";
        	assertEquals(fieldName, Float.valueOf(embedded.x), Double.valueOf(embeddedDouble[0]).floatValue());
        	assertEquals(fieldName, Float.valueOf(embedded.y), Double.valueOf(embeddedDouble[1]).floatValue());
        	assertEquals(fieldName, Float.valueOf(embedded.z), Double.valueOf(embeddedDouble[2]).floatValue());
        }
    }

    private AlgebraicVector getIdentityVector(AlgebraicField field) {
    	final int dims = 3;
    	final AlgebraicNumber one = field.one();
    	AlgebraicVector v = field.origin(dims);
        for(int axis = AlgebraicVector.X; axis <= AlgebraicVector.Z; axis++) {
            v.setComponent(axis, one);
        }
        return v;
    }
    
}
