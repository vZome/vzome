
//(c) Copyright 2008, Scott Vorthmann.  All rights reserved.

package com.vzome.core.model;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.vzome.core.algebra.AlgebraicField;
import com.vzome.core.algebra.PentagonField;
import com.vzome.core.model.Panel;

public class TestPanelEquality extends TestCase
{

    public void testEqualsObject()
    {
        AlgebraicField field = new PentagonField();
        int [] a = field .createRational( new int[]{ 0,1 } );
        int [] b = field .createRational( new int[]{ 1,1 } );
        int [] c = field .createRational( new int[]{ 2,1 } );
        int [] d = field .createRational( new int[]{ 3,1 } );
        ArrayList list = new ArrayList();
        list .add( a );
        list .add( b );
        list .add( c );
        list .add( d );
        Panel p0 = new Panel( list );
        list = new ArrayList();
        list .add( d );
        list .add( a );
        list .add( b );
        list .add( c );
        Panel p1 = new Panel( list );
        assertEquals( p0, p1 );
        list = new ArrayList();
        list .add( d );
        list .add( c );
        list .add( b );
        list .add( a );
        Panel p2 = new Panel( list );
        assertTrue( p0 .equals( p2 ) );
    }

}
