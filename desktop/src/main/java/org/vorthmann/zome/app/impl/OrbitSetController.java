//(c) Copyright 2005, Scott Vorthmann.  All rights reserved.

package org.vorthmann.zome.app.impl;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.vorthmann.j3d.MouseTool;
import org.vorthmann.j3d.MouseToolDefault;
import org.vorthmann.ui.Controller;
import org.vorthmann.ui.DefaultController;
import org.vorthmann.ui.LeftMouseDragAdapter;

import com.vzome.core.algebra.AlgebraicVector;
import com.vzome.core.math.RealVector;
import com.vzome.core.math.symmetry.Axis;
import com.vzome.core.math.symmetry.Direction;
import com.vzome.core.math.symmetry.DodecagonalSymmetry;
import com.vzome.core.math.symmetry.OctahedralSymmetry;
import com.vzome.core.math.symmetry.OrbitSet;
import com.vzome.core.math.symmetry.Symmetry;
import com.vzome.core.mesh.Color;
import com.vzome.core.render.RenderedModel.OrbitSource;

public class OrbitSetController extends DefaultController implements PropertyChangeListener
{
    private final OrbitSource colorSource;

    private final OrbitSet orbits, allOrbits;

    private Direction lastOrbit = null;

    private boolean mOneAtATime = true, showLastOrbit = false;

    private final Map<Direction, OrbitState> orbitDots = new HashMap<>();

    private final MouseTool mouseTool = new LeftMouseDragAdapter( new MouseToolDefault()
    {
        @Override
        public void mouseClicked( MouseEvent click )
        {
            Direction pickedDir = pickDirection( click );
            if ( pickedDir != null ) {
                toggleOrbit( pickedDir );
                firePropertyChange( "orbits", true, false );
            }
        }
    }, /* half-second forgiveness */ 500 );

    private static class OrbitState
    {
        double dotX, dotY;
        int dotXint, dotYint;
    }

    public OrbitSetController( OrbitSet orbits, OrbitSet allOrbits, OrbitSource colorSource, boolean showLastOrbit )
    {
        this.orbits = orbits;
        this.allOrbits = allOrbits;
        this.colorSource = colorSource;
        this.showLastOrbit = showLastOrbit;
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

        orbitDots .clear();
        //        lastOrbit = null;  // cannot do this, we might have a valid value, for example after loading from XML
        boolean lastOrbitChanged = false;
        for ( Direction dir : allOrbits )
        {
            if ( lastOrbit == null )
            {
                // just a way to initialize the lastOrbit
                lastOrbit = dir;
                lastOrbitChanged = true;
            }
            OrbitState orbit = new OrbitState();
            orbitDots .put( dir, orbit );

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
            for (Direction dir : allOrbits) {
                if ( dir .isStandard() ) {
                    orbits .add( dir );
                }
            }
        }
        else if ( action .equals( "predefinedOrbits" ) )
        {
            mOneAtATime = false;
            orbits .clear();
            for (Direction dir : allOrbits) {
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

    private static final int RADIUS = 12;
    private static final int INNER_RADIUS = 5;
    private static final int OUTER_RADIUS = 19;
    private static final int DIAM = 2 * RADIUS;
    private static int TOP = 30;
    private static int LEFT = TOP;

    @Override
    public void repaintGraphics( String panelName, Graphics graphics, Dimension size )
    {
        if ( panelName .startsWith( "oneOrbit." ) )
        {
            Direction dir = allOrbits .getDirection( panelName .substring( "oneOrbit." .length() ) );
            Graphics2D g2d = (Graphics2D) graphics;
            g2d .clearRect( 0, 0, (int) size .getWidth(), (int) size .getHeight() );
            Color color = colorSource .getColor( dir );
            g2d .setPaint( color == null? java.awt.Color.WHITE : new java.awt.Color( color .getRGB() ) );
            g2d .fill( g2d .getClipBounds() );
        }
        else if ( "selectedOrbit" .equals( panelName ) )
        {
            Graphics2D g2d = (Graphics2D) graphics;
            g2d .clearRect( 0, 0, (int) size .getWidth(), (int) size .getHeight() );
            Color color = colorSource .getColor( lastOrbit );
            g2d .setPaint( color == null? java.awt.Color.WHITE : new java.awt.Color( color .getRGB() ) );
            g2d .fill( g2d .getClipBounds() );
        }
        else if ( "orbits" .equals( panelName ) )
        {
            int fullwidth = (int) size .getWidth();
            int fullheight = (int) size .getHeight();

            Graphics2D g2d = (Graphics2D) graphics;
            g2d .clearRect( 0, 0, fullwidth, fullheight );
            g2d .setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

            if ( fullheight > 180 )
                fullheight = 180;
            int width = fullwidth - 2*TOP;
            int height = fullheight - 2*LEFT;

            int right = LEFT + width;
            int bottom = TOP + height ;
            int corner = LEFT;
            Symmetry symm = allOrbits .getSymmetry();
            if ( symm .reverseOrbitTriangle() )
                corner = right;

            g2d .setStroke( new BasicStroke( 1.5f , BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
            g2d .setPaint( java.awt.Color.black );

            GeneralPath path = new GeneralPath();
            path .moveTo( corner, TOP );
            path .lineTo( LEFT, bottom );
            path .lineTo( right, bottom );
            path .lineTo( corner, TOP );
            path .closePath();
            g2d .draw( path );

            for (Direction dir : orbitDots .keySet()) {
                OrbitState orbit = orbitDots .get( dir );
                Color color = colorSource .getColor( dir );
                int x = LEFT + (int) Math .round( orbit.dotX * width );
                // flip right/left if necessary
                if ( corner == right )
                    x = corner - (int) Math .round( orbit.dotX * width );
                int y = bottom - (int) Math .round( orbit.dotY * height );
                // now store the int coords for later picking
                orbit.dotXint = x;
                orbit.dotYint = y;
                g2d .setPaint( color == null? java.awt.Color.WHITE : new java.awt.Color( color .getRGB() ) );
                g2d .fillOval( orbit.dotXint-RADIUS, orbit.dotYint-RADIUS, DIAM, DIAM );
                g2d .setPaint( java.awt.Color.black );
                g2d .drawOval( orbit.dotXint-RADIUS, orbit.dotYint-RADIUS, DIAM, DIAM );
                if ( orbits .contains( dir ) ) {
                    g2d .setPaint( java.awt.Color.black );
                    g2d .fillOval( orbit.dotXint-INNER_RADIUS, orbit.dotYint-INNER_RADIUS, INNER_RADIUS*2, INNER_RADIUS*2 );
                }
                if ( showLastOrbit &&  lastOrbit == dir )
                {
                    g2d .setPaint( java.awt.Color.black );
                    g2d .drawOval( orbit.dotXint-OUTER_RADIUS, orbit.dotYint-OUTER_RADIUS, OUTER_RADIUS*2, OUTER_RADIUS*2 );
                }
            }

            g2d.dispose(); //clean up
        }
    }

    Direction pickDirection( MouseEvent click )
    {
        double minDist = 999d;
        Direction pickedDir = null;
        for (Direction dir : orbitDots .keySet()) {
            OrbitState orbit = orbitDots .get( dir );
            double dist = Math.sqrt( Math.pow( click.getX()-orbit.dotXint, 2 ) + Math.pow( click.getY()-orbit.dotYint, 2 ) );
            if ( dist < (double) RADIUS*4 )
            {
                if ( dist < minDist ) {
                    minDist = dist;
                    pickedDir = dir;
                }
            }
        }
        return pickedDir;
    }


    @Override
    public MouseTool getMouseTool()
    {
        return this .mouseTool;
    }

    @Override
    public boolean[] enableContextualCommands( String[] menu, MouseEvent e )
    {
        boolean[] result = new boolean[menu.length];
        for ( int i = 0; i < menu.length; i++ ) {
            String menuItem = menu[i];
            switch ( menuItem ) {

            case "rZomeOrbits":
            case "predefinedOrbits":
            case "setAllDirections":
            case "usedOrbits":
            case "configureDirections":
                result[i] = true;
                break;

            default:
                result[i] = false;
            }
        }
        return result;
    }

    @Override
    public String[] getCommandList( String listName )
    {
        if ( listName .equals( "orbits" ) )
        {
            String[] result = new String[ orbits .size() ];
            int i = 0;
            for ( Direction dir :  orbits ) {
                result[ i ] = dir .getName();
                i++;
            }
            return result;
        }
        if ( listName .equals( "allOrbits" ) )
        {
            String[] result = new String[ allOrbits .size() ];
            int i = 0;
            for ( Direction dir : allOrbits ) {
                result[ i ] = dir .getName();
                i++;
            }
            return result;
        }
        return super .getCommandList( listName );
    }
}
