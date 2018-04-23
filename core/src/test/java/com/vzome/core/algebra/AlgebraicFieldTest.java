package com.vzome.core.algebra;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * @author David Hall
 */
public class AlgebraicFieldTest {
    private final static Set<AlgebraicField> fields = new HashSet<>();
    
    static {
        fields.add (new PentagonField());
        fields.add (new RootTwoField());
        fields.add (new RootThreeField());
        fields.add (new HeptagonField());
        fields.add (new SnubDodecField());
    }
    
    @Test
    public void testEquality() {
        AlgebraicField[] f = fields.toArray( new AlgebraicField[fields.size()] );
        for(int j = 0; j < f.length; j++) {
            for(int k = 0; k < f.length; k++) {
                // TODO: This approach won't work when we include parameterized fields in fields
                // Specifically, we need to test the equalities and inequalities described in AlgebraicField.equals()
                boolean same = (j == k);
                assertEquals( same, f[j].equals(f[k]) );
                assertEquals( same, f[j].hashCode() == f[k].hashCode() );
            }
        }
    }
        
    @Test
    public void testOrder() {
        int pass = 0;
        for(AlgebraicField field : fields) {
            assertTrue(field.getOrder() >= 2);
            pass++;
        }
        assertEquals(fields.size(), pass);
	}    

    	@Test
    	public void testReciprocal()
    	{
    		for( AlgebraicField field : fields ) {
    			try {
    				field .zero() .reciprocal() .evaluate();
    				fail( "Zero divide should throw an exception" );
    			} catch ( RuntimeException re ) {
    				assertEquals( "Denominator is zero", re .getMessage() );
    			}
    		}
    	}
}
