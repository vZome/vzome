
//(c) Copyright 2007, Scott Vorthmann.  All rights reserved.

package com.vzome.core.math.symmetry;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import com.vzome.core.math.RealVector;

public class OrbitSet extends TreeSet<Direction> implements Set<Direction>
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
        for (Direction dir : this) {
            if ( dir .getName() .equals( name ) )
                return dir;
        }
        return null;
    }
    
    public class OrbitComparator implements Comparator<Direction>
    {
        private final String[] names = getSymmetry() .getDirectionNames();

        @Override
        public int compare( Direction dir1, Direction dir2 )
        {
            String name1 = dir1 .getName();
            String name2 = dir2 .getName();
            int i1 = -1, i2 = -1;
            for ( int i = 0; i < names.length; i++ ) {
                if ( name1 .equals( names[ i ] ) )
                    i1 = i;
                else if ( name2 .equals( names[ i ] ) )
                    i2 = i;
            }
            return i2-i1;
        }
    }
}
