
package com.vzome.core.math.symmetry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vzome.core.math.RealVector;

public class OrbitSet
{
    private final Map<String, Direction> contents = new HashMap<>();
    
    public interface Field
    {
        OrbitSet getGroup( String name );

        QuaternionicSymmetry getQuaternionSet( String name );
    }
    
    private final Symmetry symmetry;
    private transient Direction lastAdded = null;
    
    public OrbitSet( Symmetry symmetry )
    {
        this.symmetry = symmetry;
    }
	
	@JsonIgnore
    public Symmetry getSymmetry()
    {
        return symmetry;
    }

    public Axis getAxis( RealVector vector )
    {
        return symmetry .getAxis( vector, this .contents .values() );
    }
    
    public Direction getDirection( String name )
    {
        for (Direction dir : this .getDirections()) {
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

    public Iterable<Direction> getDirections()
    {
        return this.contents.values();
    }

    public boolean remove( Direction orbit )
    {
        String key = orbit .toString();
        boolean hadOne = this .contents .containsKey( key );
        this .contents .remove( orbit .toString() );
        return hadOne;
    }

    public boolean add( Direction orbit )
    {
        String key = orbit .toString();
        boolean hadOne = this .contents .containsKey( key );
        this .contents .put( orbit .toString(), orbit );
        if ( ! hadOne )
            this .lastAdded = orbit;
        return ! hadOne;
    }

    public boolean contains( Direction orbit )
    {
        return this .contents .containsKey( orbit .toString() );
    }

    public int size()
    {
        return this .contents .size();
    }

    public void clear()
    {
        this .contents .clear();
    }

    public void addAll( OrbitSet orbits )
    {
        this .contents .putAll( orbits .contents );
    }

    public void retainAll( OrbitSet allOrbits )
    {
        List<String> badKeys = new ArrayList<String>();
        for ( String key : this .contents .keySet() ) {
            if ( ! allOrbits .contents .containsKey( key ) )
                badKeys .add( key );
        }
        for ( String key : badKeys ) {
            this .contents .remove( key );
        }
    }

    public boolean isEmpty()
    {
        return this .contents .isEmpty();
    }

    public Direction last()
    {
        return this .lastAdded ;
    }
}
