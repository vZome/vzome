package com.vzome.core.editor;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.model.Connector;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.Strut;
import java.util.ArrayList;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import org.junit.Test;

/**
 * @author David Hall
 */
public class ManifestationIteratorTest {
    
    @Test
    public void IteratorTest () {
        Selection selection = new Selection();
        AlgebraicField field = new PentagonField();
        int expC = 0, expS = 0, expP = 0;
        {
            ArrayList<Manifestation> cList = new ArrayList<>();
            AlgebraicNumber s = field .createRational( 7 );
            AlgebraicNumber t = field .createRational( 6 );
            AlgebraicNumber u = field .createRational( 5 );
            AlgebraicNumber v = field .createRational( 4 );
            AlgebraicNumber w = field .createRational( 3 );
            AlgebraicNumber x = field .createRational( 2 );
            AlgebraicNumber y = field .createRational( 1 );
            AlgebraicNumber z = field .createRational( 0 );

            Connector a = new Connector( new AlgebraicVector(w, x, y) );
            Connector b = new Connector( new AlgebraicVector(z, w, x) );
            Connector c = new Connector( new AlgebraicVector(y, z, w) );
            Connector d = new Connector( new AlgebraicVector(x, y, z) );

            Connector e = new Connector( new AlgebraicVector(s, t, u) );
            Connector f = new Connector( new AlgebraicVector(v, s, t) );
            Connector g = new Connector( new AlgebraicVector(u, v, s) );
            Connector h = new Connector( new AlgebraicVector(t, u, v) );
            Connector i = new Connector( new AlgebraicVector(t, u, u) );
            cList .add( a );
            cList .add( b );
            cList .add( c );
            cList .add( d );
            
            cList .add( e );
            cList .add( f );
            cList .add( g );
            cList .add( h );
            cList .add( i );
            
            a.setHidden(true);
            e.setHidden(true);
            i.setHidden(true);

            expC = cList.size();
            for(Manifestation m : cList) {
                selection.select(m);
            }
        }
        {
            ArrayList<Manifestation> sList = new ArrayList<>();
            ArrayList<Manifestation> pList = new ArrayList<>();
            ArrayList<AlgebraicVector> avList = new ArrayList<>();
            
            AlgebraicNumber s = field.createRational(7);
            AlgebraicNumber t = field.createRational(6);
            AlgebraicNumber u = field.createRational(5);
            AlgebraicNumber v = field.createRational(4);
            AlgebraicNumber w = field.createRational(3);
            AlgebraicNumber x = field.createRational(2);
            AlgebraicNumber ONE = field.createRational(1);
            AlgebraicNumber ZERO = field.createRational(0);

            AlgebraicVector ORIGIN = new AlgebraicVector(ZERO, ZERO, ZERO);
            AlgebraicVector PLUS_X = new AlgebraicVector(ONE, ZERO, ZERO);
            AlgebraicVector PLUS_Y = new AlgebraicVector(ZERO, ONE, ZERO);
            AlgebraicVector PLUS_Z = new AlgebraicVector(ZERO, ZERO, ONE);
            
            AlgebraicVector a = new AlgebraicVector(w, x, ONE);
            AlgebraicVector b = new AlgebraicVector(ZERO, w, x);
            AlgebraicVector c = new AlgebraicVector(ONE, ZERO, w);
            AlgebraicVector d = new AlgebraicVector(x, ONE, ZERO);

            AlgebraicVector e = new AlgebraicVector(s, t, u);
            AlgebraicVector f = new AlgebraicVector(v, s, t);
            AlgebraicVector g = new AlgebraicVector(u, v, s);
            AlgebraicVector h = new AlgebraicVector(t, u, v);
            AlgebraicVector i = new AlgebraicVector(t, u, u);

            // TODO: Add a test to be sure that clearing the list 
            // that we passed to the panel c'tor doesn't change 
            // the panel's internal list and therefore its hashCode.
            avList.add(ORIGIN);
            avList.add(PLUS_X);
            avList.add(PLUS_Y);
            pList.add( new Panel(avList));
            avList.clear();

            // TODO: Add a test that a panel with points in reverse order is still equal
            // and that starting at a different corner is still equal.
            // and that hashcode is the same when they are equal.
            // add a note in the Panel hashcode method to point out 
            // that the hashcode must be the same for all these conditions.
            // Make the same tests for panel and polyhedron equality.
            
            // TODO: Fix Panel so it calculates the normal correctly if the first three points are collinear.
            // Add a test for that condition.
            // TODO: Add a test that the normal is negated when the points are reversed.
            
            // TODO: Add the new com.vzome.core.model.Manifestation logger level to the sample logging.properties.
            
            // TODO: If normal .isOrigin() in Panel or Face.getNormal(), then log an error or throw an exception. 
            // This would probably only happen in an invalid test case.
            
//            avList.add(ORIGIN);
//            avList.add(PLUS_Y);
//            avList.add(PLUS_X);
//            pList.add( new Panel(avList));
//            avList.clear();

            avList.add(ORIGIN);
            avList.add(PLUS_Y);
            avList.add(PLUS_Z);
            pList.add( new Panel(avList));
            avList.clear();

            avList.add(PLUS_Z);
            avList.add(a);
            avList.add(b);
            pList.add( new Panel(avList));
            avList.clear();

            avList.add(c);
            avList.add(d);
            avList.add(e);
            pList.add( new Panel(avList));
            avList.clear();

            avList.add(f);
            avList.add(g);
            avList.add(h);
            avList.add(i);
            pList.add( new Panel(avList));
            avList.clear();
            
            pList.get(0).setHidden(true);

            Strut sa = new Strut(ORIGIN, a);
            Strut sb = new Strut(ORIGIN, b);
            Strut sc = new Strut(ORIGIN, c);
            Strut sd = new Strut(ORIGIN, d);

            Strut se = new Strut(e, ORIGIN);
            Strut sf = new Strut(f, ORIGIN);
            Strut sg = new Strut(g, ORIGIN);
            Strut sh = new Strut(h, ORIGIN);
            Strut si = new Strut(i, ORIGIN);

            Strut X_AXIS = new Strut(ORIGIN, PLUS_X);
            Strut Y_AXIS = new Strut(ORIGIN, PLUS_Y);
            Strut Z_AXIS = new Strut(ORIGIN, PLUS_Z);
            
            X_AXIS.setHidden(true);
            Y_AXIS.setHidden(true);
            Z_AXIS.setHidden(true);

            sList.add(sa);
            sList.add(sb);
            sList.add(sc);
            sList.add(sd);
            sList.add(se);
            sList.add(sf);
            sList.add(sg);
            sList.add(sh);
            sList.add(si);
            sList.add(X_AXIS);
            sList.add(Y_AXIS);
            sList.add(Z_AXIS);
            
            for (Manifestation m : sList) {
                selection.select(m);
            }
            expS = sList.size();
            
            for (Manifestation m : pList) {
                selection.select(m);
            }
            expP = pList.size();
        }

        assertTrue(expC > 0);
        assertTrue(expS > 0);
        assertTrue(expP > 0);
        assertTrue(selection.size() > 0);
        assertEquals(selection.size(), expC + expS + expP);

        int nc = 0;
        for(Connector connector : Manifestations.getConnectors(selection)) {
            assertEquals("Connectors: ", connector.getClass(), Connector.class);
            nc++;
        }
        assertTrue(expC > 0);
        assertEquals(expC, nc);
        
        int nv = 0;
        for(Manifestation man : Manifestations.visibleManifestations(selection)) {
            nv++;
        }
        assertTrue(nv > 0);
        assertEquals(selection.size() - 7, nv); // we hid 7 of them
        assertTrue(nv < selection.size());
        
        int ns = 0;
        for(Strut strut : Manifestations.getStruts(selection)) {
            assertEquals("Struts: ", strut.getClass(), Strut.class);
            ns++;
        }
        assertTrue(expS > 0);
        assertEquals(expS, ns);
        
        int np = 0;
        for(Panel panel : Manifestations.getPanels(selection)) {
            assertEquals("Panels: ", panel.getClass(), Panel.class);
            np++;
        }
        assertTrue(expP > 0);
        assertEquals(expP, np);
        
    }

}
