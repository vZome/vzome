
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.math.symmetry;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.vzome.core.math.RealVector;

public class OrbitSet extends TreeSet implements Set
{    
    public interface Field
    {
        OrbitSet getGroup( String name );

        QuaternionicSymmetry getQuaternionSet( String name );
    }
    
    private final Symmetry symmetry;
    
    public OrbitSet( Symmetry symmetry )
    {
        this.symmetry = symmetry;
    }
    
    public Symmetry getSymmetry()
    {
        return symmetry;
    }

    public Axis getAxis( RealVector vector )
    {
        return symmetry .getAxis( vector, this );
    }
    
    public Direction getDirection( String name )
    {
        for ( Iterator dirs = this.iterator(); dirs .hasNext(); )
        {
            Direction dir = (Direction) dirs .next();
            if ( dir .getName() .equals( name ) )
                return dir;
        }
        return null;
    }
    
    public class OrbitComparator implements Comparator
    {
        private String[] names = getSymmetry() .getDirectionNames();

        public int compare( Object arg1, Object arg2 )
        {
            String name1 = ((Direction) arg1) .getName();
            String name2 = ((Direction) arg2) .getName();
            int i1 = -1, i2 = -1;
            for ( int i = 0; i < names.length; i++ )
                if ( name1 .equals( names[ i ] ) )
                    i1 = i;
                else if ( name2 .equals( names[ i ] ) )
                    i2 = i;
            return i2-i1;
        }
        
    }
}
