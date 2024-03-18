package com.vzome.core.editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.AlgebraicNumber;
import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.editor.api.Manifestations;
import com.vzome.core.editor.api.Selection;
import com.vzome.core.model.Connector;
import com.vzome.core.model.ConnectorImpl;
import com.vzome.core.model.Manifestation;
import com.vzome.core.model.Panel;
import com.vzome.core.model.PanelImpl;
import com.vzome.core.model.Strut;
import com.vzome.core.model.StrutImpl;

/**
 * @author David Hall
 */
public class ManifestationIteratorTest {
    
    @Test
    public void IteratorTest () {
        Selection selection = new SelectionImpl();
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

            Connector a = new ConnectorImpl( new AlgebraicVector(w, x, y) );
            Connector b = new ConnectorImpl( new AlgebraicVector(z, w, x) );
            Connector c = new ConnectorImpl( new AlgebraicVector(y, z, w) );
            Connector d = new ConnectorImpl( new AlgebraicVector(x, y, z) );

            Connector e = new ConnectorImpl( new AlgebraicVector(s, t, u) );
            Connector f = new ConnectorImpl( new AlgebraicVector(v, s, t) );
            Connector g = new ConnectorImpl( new AlgebraicVector(u, v, s) );
            Connector h = new ConnectorImpl( new AlgebraicVector(t, u, v) );
            Connector i = new ConnectorImpl( new AlgebraicVector(t, u, u) );
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
            pList.add( new PanelImpl(avList));
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
            pList.add( new PanelImpl(avList));
            avList.clear();

            avList.add(PLUS_Z);
            avList.add(a);
            avList.add(b);
            pList.add( new PanelImpl(avList));
            avList.clear();

            avList.add(c);
            avList.add(d);
            avList.add(e);
            pList.add( new PanelImpl(avList));
            avList.clear();

            avList.add(f);
            avList.add(g);
            avList.add(h);
            avList.add(i);
            pList.add( new PanelImpl(avList));
            avList.clear();
            
            pList.get(0).setHidden(true);

            Strut sa = new StrutImpl(ORIGIN, a);
            Strut sb = new StrutImpl(ORIGIN, b);
            Strut sc = new StrutImpl(ORIGIN, c);
            Strut sd = new StrutImpl(ORIGIN, d);

            Strut se = new StrutImpl(e, ORIGIN);
            Strut sf = new StrutImpl(f, ORIGIN);
            Strut sg = new StrutImpl(g, ORIGIN);
            Strut sh = new StrutImpl(h, ORIGIN);
            Strut si = new StrutImpl(i, ORIGIN);

            Strut X_AXIS = new StrutImpl(ORIGIN, PLUS_X);
            Strut Y_AXIS = new StrutImpl(ORIGIN, PLUS_Y);
            Strut Z_AXIS = new StrutImpl(ORIGIN, PLUS_Z);
            
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
            assertEquals("Connectors: ", connector.getClass(), ConnectorImpl.class);
            nc++;
        }
        assertTrue(expC > 0);
        assertEquals(expC, nc);
        
        int nv = 0;
        for(@SuppressWarnings("unused") Manifestation man : Manifestations.visibleManifestations(selection)) {
            nv++;
        }
        assertTrue(nv > 0);
        assertEquals(selection.size() - 7, nv); // we hid 7 of them
        assertTrue(nv < selection.size());
        
        int ns = 0;
        for(Strut strut : Manifestations.getStruts(selection)) {
            assertEquals("Struts: ", strut.getClass(), StrutImpl.class);
            ns++;
        }
        assertTrue(expS > 0);
        assertEquals(expS, ns);
        
        int np = 0;
        for(Panel panel : Manifestations.getPanels(selection)) {
            assertEquals("Panels: ", panel.getClass(), PanelImpl.class);
            np++;
        }
        assertTrue(expP > 0);
        assertEquals(expP, np);
        
    }

}
