
package com.vzome.desktop.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.construction.Color;
import com.vzome.core.editor.api.OrbitSource;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.DodecagonalSymmetry;
import com.vzome.core.math.symmetry.OctahedralSymmetry;
import com.vzome.core.math.symmetry.OrbitSet;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.desktop.api.Controller;

public class OrbitSetController extends DefaultController implements PropertyChangeListener
{
    private final OrbitSource colorSource;

    private final OrbitSet orbits, allOrbits;

    private Direction lastOrbit = null;
    
    private boolean mOneAtATime = true;

    private final Map<Direction, OrbitState> orbitDots = new HashMap<>();

    private static class OrbitState
    {
        double dotX, dotY;
        Color color;
    }

    public OrbitSetController( OrbitSet orbits, OrbitSet allOrbits, OrbitSource colorSource )
    {
        this.orbits = orbits;
        this.allOrbits = allOrbits;
        this.colorSource = colorSource;
        this.mOneAtATime = orbits .size() == 1;
        recalculateDots();
    }

    private synchronized void recalculateDots()
    {
        orbits .retainAll( allOrbits );

        Symmetry symmetry = allOrbits .getSymmetry();
        RealVector test = new RealVector( 0.1d, 0.1d, 1d );
        if ( symmetry instanceof OctahedralSymmetry )
            test = new RealVector( 2d, 1d, 4d );
        else if ( symmetry instanceof DodecagonalSymmetry )
            test = new RealVector( 10d, 1d, 1d );

        symmetry .computeOrbitDots();
        
        orbitDots .clear();
        //        lastOrbit = null;  // cannot do this, we might have a valid value, for example after loading from XML
        boolean lastOrbitChanged = false;
        for ( Direction dir : allOrbits .getDirections() )
        {
            if ( lastOrbit == null )
            {
                // just a way to initialize the lastOrbit
                lastOrbit = dir;
                lastOrbitChanged = true;
            }
            OrbitState orbit = new OrbitState();
            orbitDots .put( dir, orbit );

            orbit .color = colorSource .getColor( dir );
            orbit .dotX = dir .getDotX();
            if ( orbit .dotX >= -90d ) {
                // This orbit supports pre-computed dot locations
                orbit .dotY = dir .getDotY();
            }
            else {
                // The old way
                Axis axis = symmetry .getAxis( test, Collections .singleton( dir ) );
                AlgebraicVector v = axis .normal();
                double z =  v .getComponent( 2 ) .evaluate();
                if(z == 0.0d) {
                    z = 1.0d; // hack to partially fix DodecagonalSymmetry layout, but I think blue is still in wrong position.
                }
                orbit.dotX = v .getComponent( 0 ) .evaluate();
                orbit.dotX = orbit.dotX / z; // intersect with z=0 plane
                orbit.dotY = v .getComponent( 1 ) .evaluate();
                orbit.dotY = orbit.dotY / z; // intersect with z=0 plane
            }
        }
        if ( ( lastOrbit == null ) || (! allOrbits .contains( lastOrbit ) ) )
        {
            lastOrbitChanged = true;
            if ( ! orbits .isEmpty() )
                lastOrbit = orbits .last();
            else if ( ! orbitDots .isEmpty() )
                lastOrbit = orbitDots .keySet() .iterator() .next();
            else
                lastOrbit = null;
        }
        if ( lastOrbitChanged )
            firePropertyChange( "selectedOrbit", null, lastOrbit == null? null : lastOrbit .getName() );
    }

    @Override
    public void doAction( String action ) throws Exception
    {
        if ( action .equals( "refreshDots" ) )
        {
            recalculateDots();
            return;
        }
        if ( action .equals( "toggleHalf" ) || action .equals( "reset" )
                || action .equals( "short" ) || action .equals( "medium" ) || action .equals( "long" )
                || action .startsWith( "adjustScale." ) || action .equals( "scaleUp" ) || action .equals( "scaleDown" ) )
        {
            getSubController( "currentLength" ) .actionPerformed( null, action );
            return;
        }
        if ( action .equals( "setNoDirections" ) )
        {
            orbits .clear();
        }
        else if ( action .equals( "setAllDirections" ) )
        {
            mOneAtATime = false;
            orbits .addAll( allOrbits );
        }
        else if ( action .equals( "rZomeOrbits" ) )
        {
            mOneAtATime = false;
            orbits .clear();
            for (Direction dir : allOrbits .getDirections()) {
                if ( dir .isStandard() ) {
                    orbits .add( dir );
                }
            }
        }
        else if ( action .equals( "predefinedOrbits" ) )
        {
            mOneAtATime = false;
            orbits .clear();
            for (Direction dir : allOrbits .getDirections()) {
                if ( ! dir .isAutomatic() ) {
                    orbits .add( dir );
                }
            }
        }
        else if ( action .equals( "oneAtATime" ) )
        {
            mOneAtATime = !mOneAtATime;
            if ( ! mOneAtATime )
                return;  // no action when releasing the constraint
            // else, pick one
            orbits .clear();
            if ( lastOrbit != null )
                orbits .add( lastOrbit );
        }
        else if ( action .startsWith( "setSingleOrbit." ) ) // new for online classic, which cannot use "oneAtATime" toggle
        {
            Boolean lastValue = mOneAtATime;
            String value = action .substring( "setSingleOrbit." .length() );
            mOneAtATime = Boolean.parseBoolean( value );
            if ( mOneAtATime ) {
                // no action when releasing the constraint, else pick one
                orbits .clear();
                if ( lastOrbit != null )
                    orbits .add( lastOrbit );
            }
            firePropertyChange( "oneAtATime", lastValue, mOneAtATime );
        }
        else if ( action .startsWith( "enableDirection." ) )
        {
            String dirName = action .substring( "enableDirection." .length() );
            Direction dir = allOrbits .getDirection( dirName );
            // TODO: figure out why dir can be null here
            if ( dir != null && ! orbits .contains( dir ) )
                toggleOrbit( dir );
        }
        else if ( action .startsWith( "toggleDirection." ) )
        {
            String dirName = action .substring( "toggleDirection." .length() );
            Direction dir = allOrbits .getDirection( dirName );
            toggleOrbit( dir );
        }
        else if ( action .startsWith( "setSingleDirection." ) )
        {
            mOneAtATime = true;
            String dirName = action .substring( "setSingleDirection." .length() );
            Direction dir = allOrbits .getDirection( dirName );
            toggleOrbit( dir );
        }
        firePropertyChange( "orbits", true, false );
    }

    @Override
    public void propertyChange( PropertyChangeEvent evt )
    {
        if ( "length" .equals( evt .getPropertyName() )
                && evt .getSource() == getSubController( "currentLength" ) )
            firePropertyChange( evt ); // forward to the NewLengthPanel

        // allOrbits must have changed, forward to our listeners
        if ( "orbits" .equals( evt .getPropertyName() ) ) {
            recalculateDots();
            firePropertyChange( evt );
        }
    }

    void toggleOrbit( Direction dir )
    {
        if ( mOneAtATime )
            orbits .clear();
        if ( orbits .add( dir ) )
        {
            lastOrbit = dir;
            firePropertyChange( "selectedOrbit", null, dir .getName() );
        }
        else if ( orbits .remove( dir ) )
        {
            // leave lastOrbit alone, it can stay "circled", so we always have a length panel... just like "setNoDirections"
            //            if ( lastOrbit == dir )
            //            {
            //                lastOrbit = null;
            //                if ( ! orbits .isEmpty() )
            //                {
            //                    lastOrbit = (Direction) orbits .last();
            //                }
            //                properties() .firePropertyChange( "selectedOrbit", null, lastOrbit == null ? null : lastOrbit .getName() );
            //            }
        }
        else
            throw new IllegalStateException( "could not toggle direction " + dir .getName() );
    }

    @Override
    public Controller getSubController( String name )
    {
        if ( "currentLength" .equals( name ) )
            return super .getSubController( "length." + getProperty( "selectedOrbit" ) );
        return super .getSubController( name );
    }

    @Override
    public String getProperty( String string )
    {
        if ( "oneAtATime" .equals( string ) )
            return Boolean .toString( mOneAtATime );

        if ( "reverseOrbitTriangle" .equals( string ) )
            return Boolean .toString( this .allOrbits .getSymmetry() .reverseOrbitTriangle() );

        if ( "selectedOrbit" .equals( string ) )
            if ( lastOrbit != null )
                return lastOrbit .getName();
            else
                return null;

        if ( "halfSizes" .equals( string ) )
            if ( lastOrbit != null && lastOrbit .hasHalfSizes() )
                return "true";
            else
                return "false";

        if ( "scaleName.superShort" .equals( string ) )
            return ( lastOrbit == null )? null : lastOrbit .getScaleName( 0 );
        if ( "scaleName.short" .equals( string ) )
            return ( lastOrbit == null )? null : lastOrbit .getScaleName( 1 );
        if ( "scaleName.medium" .equals( string ) )
            return ( lastOrbit == null )? null : lastOrbit .getScaleName( 2 );
        if ( "scaleName.long" .equals( string ) )
            return ( lastOrbit == null )? null : lastOrbit .getScaleName( 3 );

        if ( "color" .equals( string ) )
        {
            Color color = colorSource .getColor( lastOrbit );
            if ( color == null )
                return null;
            int rgb = color .getRGB();
            return "0x" + Integer .toHexString( rgb );
        }

        if ( "half" .equals( string ) | "unitText" .equals( string ) | "multiplierText" .equals( string ) )
            return getSubController( "currentLength" ) .getProperty( string );
        
        if ( string .startsWith( "orbitDot." ) ) {
            String orbitName = string .substring( "orbitDot." .length() );
            Direction orbit = allOrbits .getDirection( orbitName );
            OrbitState dot = this .orbitDots .get( orbit );
            if ( dot == null )
                return "0xffffff/0/0";
            return "0x" + Integer.toHexString( dot.color.getRGB() ) + "/" + dot.dotX + "/" + dot.dotY;
        }
        
        if ( string .startsWith( "orbitEnabled." ) ) {
            String orbitName = string .substring( "orbitEnabled." .length() );
            Direction orbit = orbits .getDirection( orbitName );
            return Boolean.toString( orbit != null );
        }

        return super .getProperty( string );
    }

    @Override
    public void setModelProperty( String cmd, Object value )
    {
        if ( "oneAtATime" .equals( cmd ) )
            mOneAtATime = "true" .equals( value );
        else if ( "multiplier" .equals( cmd ) | "half" .equals( cmd ) )
            getSubController( "currentLength" ) .setProperty( cmd, value );
        else
            super .setModelProperty( cmd, value );
    }


    @Override
    public String[] getCommandList( String listName )
    {
        if ( listName .equals( "orbits" ) )
        {
            String[] result = new String[ orbits .size() ];
            int i = 0;
            for ( Direction dir : orbits .getDirections() ) {
                result[ i ] = dir .getName();
                i++;
            }
            return result;
        }
        if ( listName .equals( "allOrbits" ) )
        {
            String[] result = new String[ allOrbits .size() ];
            int i = 0;
            for ( Direction dir : allOrbits .getDirections() ) {
                result[ i ] = dir .getName();
                i++;
            }
            return result;
        }
        return super .getCommandList( listName );
    }
}
