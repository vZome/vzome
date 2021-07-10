package com.vzome.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.PentagonField;

/**
 * @author David Hall
 */
public class PanelTest {
    
    @Test
    public void testVerticesAreInvariant() {
        AlgebraicField field = new PentagonField();
        AlgebraicNumber w = field.createRational(0);
        AlgebraicNumber x = field.createRational(1);
        AlgebraicNumber y = field.createRational(2);
        AlgebraicNumber z = field.createRational(3);
        AlgebraicVector a = new AlgebraicVector(w, x, y);
        AlgebraicVector b = new AlgebraicVector(z, w, x);
        AlgebraicVector c = new AlgebraicVector(y, z, w);
        AlgebraicVector d = new AlgebraicVector(x, y, z);

        ArrayList<AlgebraicVector> list = new ArrayList<>();
        list.add(a);
        list.add(b);
        list.add(c);
        list.add(d);
        Panel panel = new PanelImpl(list);

        int expected = list.size();
        assertTrue(expected >= 3);
        assertEquals(expected, panel.getVertexCount());
        // be sure the panel's vertex collection can't be changed after the fact.
        list.clear();
        assertEquals(expected, panel.getVertexCount());
    }
    
    @Test
    public void testNormalsMatchVertexOrder() {
        AlgebraicField field = new PentagonField();
        
        AlgebraicNumber ZERO = field.createRational(0);
        AlgebraicNumber POS1 = field.createRational(181,3982);
        AlgebraicNumber POS2 = field.createRational(27);
        AlgebraicNumber POS3 = field.createRational(3555,78);
        AlgebraicNumber NEG1 = field.createRational(-175);
        AlgebraicNumber NEG2 = field.createRational(-2,42345690);
        AlgebraicNumber NEG3 = field.createRational(-324);
//        AlgebraicVector p = new AlgebraicVector(POS1, ZERO, ZERO);
//        AlgebraicVector q = new AlgebraicVector(ZERO, POS1, ZERO);
//        AlgebraicVector r = new AlgebraicVector(ZERO, ZERO, POS1);
        AlgebraicVector p = new AlgebraicVector(NEG3, ZERO, POS2);
        AlgebraicVector q = new AlgebraicVector(ZERO, POS1, POS3);
        AlgebraicVector r = new AlgebraicVector(NEG1, NEG2, POS1);

        ArrayList<AlgebraicVector> list = new ArrayList<>();
        list.add(p);
        list.add(q);
        list.add(r);
        Panel p0 = new PanelImpl(list);

        int size = list.size();
        assertTrue(size >= 3);
        
        AlgebraicVector n0 = p0.getNormal();
        assertFalse(n0.isOrigin());
        
//        list.add(1, list.remove(0)); // swap first two
        list.add(2, list.remove(1)); // swap last two
        
        Panel p1 = new PanelImpl(list);
        assertEquals(size, list.size());
        
        AlgebraicVector n1 = p1.getNormal();
        assertFalse(n1.isOrigin());
        
        // normals should be equal but opposite directions.
        // so adding them should equal zero
        AlgebraicVector sum = n0.plus(n1);
        assertTrue(sum.isOrigin());
        
        // TODO: Make a specific panel with a known normal and be sure the direction is as expected for the right hand rule 
        //  or at leaast backward compatible with earlier versions...
    }
    
}
