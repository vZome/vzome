package com.vzome.core.generic;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.HeptagonField;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.algebra.RootThreeField;
import com.vzome.core.algebra.RootTwoField;
import com.vzome.core.algebra.SnubDodecField;
import static java.lang.Integer.signum;
import java.util.HashSet;
import java.util.Set;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import org.junit.Test;

/**
 * @author David Hall Tests the AlgebraicField implementation of Comparable interface.
 * Note that we can't determine greater or less than, only equality or not.
 * The implementation of Comparable<AlgebraicField> intentionally 
 *  throws an exception if different types of fields are compared, that is if they are not equal.
 */
public class ComparableAlgebraicFieldTest {
    
    private final static Set<AlgebraicField> fields = new HashSet<>();
    
    public ComparableAlgebraicFieldTest() {
        AlgebraicField pentagonField = new PentagonField();

        fields.add (pentagonField);
        fields.add (new RootTwoField());
        fields.add (new RootThreeField());
        fields.add (new HeptagonField());
        fields.add (new SnubDodecField(pentagonField));
        
        assertTrue(fields.size() >= 2);
    }
    
    @Test
	public void testMismatchedFieldThrowsException() {
        int pass = 0;
        AlgebraicField last = null;
        for(AlgebraicField field : fields) {
            if(last != null) {
                try {
                    last.compareTo(field);
                    String msg = "Expected IllegalStateException was NOT thrown.";
                    assertTrue(msg, false);
                } catch (IllegalStateException ex) {
                    // this is what the spec requires, so ignore the exception.
                    assertNotNull(ex);
                    pass++;
                }
            }
            last = field;
        }
        assertTrue(pass > 0);
        assertEquals(fields.size() - 1, pass);
	}
    
    @Test
    public void testComparableToEquivalent() { 
        int i = 0;
		for(AlgebraicField field : fields) {
			verifyComparableToEquivalent(field, field); 
            i++;
		}
        assertTrue(i > 0);
        assertEquals(fields.size(), i);
	}
	protected void verifyComparableToEquivalent(AlgebraicField field1, AlgebraicField field2) {
		assertNotNull(field1);
		assertNotNull(field2);
		int expected = 0;
		int result = field1.compareTo(field2);
		assertEquals(expected, signum(result));
	}
    
    
    /**
     * This doesn't have anything to do with Comparable, 
     * but this was a convenient place to throw in this fundamental assertion.
     */
    @Test
	public void testOrder() {
        int pass = 0;
        for(AlgebraicField field : fields) {
            assertTrue(field.getOrder() >= 2);
            pass++;
        }
        assertEquals(fields.size(), pass);
	}    

}
